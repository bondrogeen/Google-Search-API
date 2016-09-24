package com.mohammadag.googlesearchapi;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.TextView;

import java.lang.reflect.Method;
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
import static de.robv.android.xposed.XposedHelpers.getObjectField;


public class GoogleSearchAPIModule implements IXposedHookLoadPackage, IXposedHookZygoteInit, IXposedHookInitPackageResources {

	private static String checkText = "Nope123";
	private static XSharedPreferences mPreferences;

	//Hook Null Set
	String MyVoiceSearchControllerListenerClassHook = null;
	String MyVoiceSearchControllerListenerMethodHook = null;
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		mPreferences = new XSharedPreferences("com.mohammadag.googlesearchapi", "Hooks");
	}

    @Override
    public void handleInitPackageResources(final XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
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
        final Context context = (Context) callMethod(activityThread, "getSystemContext");
        
        final int versionCheck = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
        //End Snippet

		XposedBridge.log("Version Code: " + versionCheck);

        mPreferences.makeWorldReadable();

        MyVoiceSearchControllerListenerClassHook = mPreferences.getString("First", null);
        MyVoiceSearchControllerListenerMethodHook = mPreferences.getString("Second", null);

        XposedBridge.log("First Hook: " + MyVoiceSearchControllerListenerClassHook);

        try {
            Class<?> Account = findClass(MyVoiceSearchControllerListenerClassHook, lpparam.classLoader);
            Method[] methods = XposedHelpers.findMethodsByExactParameters(Account, void.class, String.class);

            findAndHookMethod(Account, methods[0].getName(), String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    broadcastGoogleSearch(AndroidAppHelper.currentApplication().getApplicationContext(), (String) param.args[0]);
                }
            });
        } catch (Exception e) {
            XposedBridge.log("GS API - " +e);
        }
    }

	private static void broadcastGoogleSearch(Context context, CharSequence searchText) {
        Intent intent = new Intent(GoogleSearchApi.INTENT_NEW_SEARCH);
        intent.putExtra(GoogleSearchApi.KEY_VOICE_TYPE, "voiceResult");
        intent.putExtra(GoogleSearchApi.KEY_QUERY_TEXT, searchText.toString());
		context.sendBroadcast(intent, "com.mohammadag.googlesearchapi.permission.ACCESS_GGOGLE_SEARCH_API");
	}

}
