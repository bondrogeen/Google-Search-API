package com.mohammadag.googlesearchapi;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.TextView;

import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;


public class GoogleSearchAPIModule implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {

	private Context mContext = null;
	private static ArrayList<Intent> mQueuedIntentList = null;
	private static XSharedPreferences mPreferences;
	
	//Hook Null Set
	String SearchControllerClassHook = null;
	String MyVoiceSearchControllerListenerClassHook = null;
	String MyVoiceSearchControllerListenerMethodHook = null;
	String CharSequenceClassHook = null;
	String CharSequenceClassHook2 = null;
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		mPreferences = new XSharedPreferences("com.mohammadag.googlesearchapi");
	}

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        if (!resparam.packageName.equals("com.mohammadag.googlesearchapi")) {
            return;
        }

        resparam.res.hookLayout("com.mohammadag.googlesearchapi", "layout", "fragment_intro", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(XC_LayoutInflated.LayoutInflatedParam liparam) throws Throwable {
                TextView status = (TextView) liparam.view.findViewById(
                        liparam.res.getIdentifier("status_text", "id", "com.mohammadag.googlesearchapi"));
                status.setText(Html.fromHtml("<b>Status:</b> Up and running<br/>"));
            }
        });
    }

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals(Constants.GOOGLE_SEARCH_PACKAGE))
			return;

		// Thank you to KeepChat For the Following Code Snippet
		// http://git.io/JJZPaw
		Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        Context context = (Context) callMethod(activityThread, "getSystemContext");
        
        final int versionCheck = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
        //End Snippet

		XposedBridge.log("Version Code: "+versionCheck);

        if (versionCheck >= 300401290) {
            SearchControllerClassHook = "cws";
            MyVoiceSearchControllerListenerClassHook = "cwx";
            MyVoiceSearchControllerListenerMethodHook = "a";
            CharSequenceClassHook = "isj";
            CharSequenceClassHook2 = "daj";
        }

        if(versionCheck == 300401210) {
            SearchControllerClassHook = "cws";
            MyVoiceSearchControllerListenerClassHook = "cwx";
            MyVoiceSearchControllerListenerMethodHook = "a";
            CharSequenceClassHook = "isi";
            CharSequenceClassHook2 = "daj";
        }

		//3.6.16
        if(versionCheck >= 300306260 && versionCheck < 300308000) {
            SearchControllerClassHook = "cjq";
            MyVoiceSearchControllerListenerClassHook = "cjw";
            MyVoiceSearchControllerListenerMethodHook = "a";
            CharSequenceClassHook = "ijg";
            CharSequenceClassHook2 = "cmn";
        }

        //Google Search 3.6.15
        if (versionCheck >= 300306150 && versionCheck < 300306260) {
            SearchControllerClassHook = "bpn";
            MyVoiceSearchControllerListenerClassHook = "bpy";
            MyVoiceSearchControllerListenerMethodHook = "a";
            CharSequenceClassHook = "hmu";
            CharSequenceClassHook2 = "cbs";
        }

        //Google Search 4.0+
        if(versionCheck == 300308000) {
            SearchControllerClassHook = "cjq";
            MyVoiceSearchControllerListenerClassHook = "cjw";
            MyVoiceSearchControllerListenerMethodHook = "a";
            CharSequenceClassHook = "ijg";
            CharSequenceClassHook2 = "cmn";
        }

        //Older Versions
		if(versionCheck == 300306140 || versionCheck == 300306130) {
			SearchControllerClassHook = "bpn";
            MyVoiceSearchControllerListenerClassHook = "bpy";
			MyVoiceSearchControllerListenerMethodHook = "a";
			CharSequenceClassHook = "hmu";
			CharSequenceClassHook2 = "cbs";
		}

		if(versionCheck == 300305160 || versionCheck == 300305150 || versionCheck == 300305140) {
			SearchControllerClassHook = "bir";
            MyVoiceSearchControllerListenerClassHook = "bjb";
			MyVoiceSearchControllerListenerMethodHook = "a";
			CharSequenceClassHook = "heb";
			CharSequenceClassHook2 = "bul";
		}

        XposedBridge.log("First Hook: " +SearchControllerClassHook);

		// com.google.android.search.core.SearchController$MyVoiceSearchControllerListener
		Class<?> MyVoiceSearchControllerListener = findClass(MyVoiceSearchControllerListenerClassHook, lpparam.classLoader);

        Class<?> SearchController = findClass(SearchControllerClassHook, lpparam.classLoader);

        XposedBridge.hookAllConstructors(SearchController, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                mContext = (Context) XposedHelpers.getObjectField(param.thisObject, "mContext");
            }
        });

		// onRecognitionResult
		findAndHookMethod(MyVoiceSearchControllerListener, MyVoiceSearchControllerListenerMethodHook, CharSequence.class, findClass(CharSequenceClassHook, lpparam.classLoader), findClass(CharSequenceClassHook2, lpparam.classLoader), new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
				CharSequence voiceResult = (CharSequence) param.args[0];
				mPreferences.reload();
				if (mContext != null) {
					broadcastGoogleSearch(mContext, voiceResult, true,
							mPreferences.getBoolean(Constants.KEY_DELAY_BROADCASTS, false));
				} else {
					XposedBridge.log(voiceResult.toString());
				}
			}
		});

	}

	private static void broadcastGoogleSearch(Context context, CharSequence searchText, boolean voice, boolean delayed) {
        Intent intent = new Intent(GoogleSearchApi.INTENT_NEW_SEARCH);
        intent.putExtra(GoogleSearchApi.KEY_VOICE_TYPE, "voiceResult");
        intent.putExtra(GoogleSearchApi.KEY_QUERY_TEXT, searchText.toString());
		if (delayed) {
			mQueuedIntentList.add(intent);
		} else {
            context.sendBroadcast(intent, "com.mohammadag.googlesearchapi.permission.ACCESS_GGOGLE_SEARCH_API");
		}
	}

}
