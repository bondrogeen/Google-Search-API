package com.mohammadag.googlesearchapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import static de.robv.android.xposed.XposedHelpers.callStaticMethod;
import static de.robv.android.xposed.XposedHelpers.callMethod;
import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.getObjectField;


public class GoogleSearchAPIModule implements IXposedHookLoadPackage, IXposedHookZygoteInit {

	private Context mContext = null;
	private static ArrayList<Intent> mQueuedIntentList = null;
	private static XSharedPreferences mPreferences;
	 
	String Checker = "";
	
	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
		mPreferences = new XSharedPreferences("com.mohammadag.googlesearchapi");
	}
		
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (lpparam.packageName.equals("com.mohammadag.googlesearchapi")) {
			touchOurself(lpparam.classLoader);
		}

		if (!lpparam.packageName.equals(Constants.GOOGLE_SEARCH_PACKAGE))
			return;

		// Thank you to KeepChat For the Following Code Snippet
		// http://git.io/JJZPaw
		
		Object activityThread = callStaticMethod(findClass("android.app.ActivityThread", null), "currentActivityThread");
        Context context = (Context) callMethod(activityThread, "getSystemContext");

        String versionName = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionName;
        
        int versionCode = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
		
        Checker = versionName;
        
        final int versionCheck = versionCode;
        
		/* IPC, not sure how many processes Google Search runs in, but we need this since
		 * it's surely not one.
		 */
		final BroadcastReceiver internalReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (Constants.INTENT_SETTINGS_UPDATED.equals(intent.getAction())) {
					mPreferences.reload();
				} else if (Constants.INTENT_QUEUE_INTENT.equals(intent.getAction())) {
					Intent intentToQueue = intent.getParcelableExtra(Constants.KEY_INTENT_TO_QUEUE);
					mQueuedIntentList.add(intentToQueue);
				} else if (Constants.INTENT_FLUSH_INTENTS.equals(intent.getAction())) {
					for (Intent intentToFlush : mQueuedIntentList) {
						XposedBridge.log("Sending queued intent");
						sendBroadcast(mContext, intentToFlush);
						mQueuedIntentList.remove(intentToFlush);
					}
				}
			}
		};
		
		
		
		XposedBridge.log("Google Search Version"+Checker+" And Name: "+versionCheck);
		
		//Default Hooks
		String SearchControllerClassHook = "bpn";
		String MyVoiceSearchControllerListenerClassHook = "bpy";
		String SearchResultFetcherClassHook = "cby";
		String SearchOverlayImplClassHook = "cuc";
							
		String mContextHook = "mContext";
					
		String mVoiceSearchServicesHook = "mVoiceSearchServices";
		String ttsManagerHook = "aCT";
		String ttsManagerMethodHook = "a";
				
		String SearchResultFetcherQueryHook = "x";
			    
		String searchQueryTextHook = "bqk";
		String mCacheHook = "mCache";
		String mClockHook = "mClock";
		String mCacheResultHook = "a";
			    
		String MyVoiceSearchControllerListenerMethodHook = "a";
		String CharSequenceClassHook = "hmu";
		String CharSequenceClassHook2 = "cbs";
		
		if(versionCheck == 300306140 || versionCheck == 300306130) {
			SearchControllerClassHook = "bpn";
			MyVoiceSearchControllerListenerClassHook = "bpy";
			SearchResultFetcherClassHook = "cby";
			SearchOverlayImplClassHook = "cuc";
								
			mContextHook = "mContext";
						
			mVoiceSearchServicesHook = "mVoiceSearchServices";
			ttsManagerHook = "aCT";
			ttsManagerMethodHook = "a";
					
			SearchResultFetcherQueryHook = "w";
				    
			searchQueryTextHook = "bqk";
			mCacheHook = "mCache";
			mClockHook = "mClock";
			mCacheResultHook = "a";
				    
			MyVoiceSearchControllerListenerMethodHook = "a";
			CharSequenceClassHook = "hmu";
			CharSequenceClassHook2 = "cbs";
		}
			
		if(versionCheck == 300305160) {
			SearchControllerClassHook = "bir";
			MyVoiceSearchControllerListenerClassHook = "bjb";
			SearchResultFetcherClassHook = "bur";
			SearchOverlayImplClassHook = "cmh";
								
			mContextHook = "mContext";
						
			mVoiceSearchServicesHook = "mVoiceSearchServices";
			ttsManagerHook = "azL";
			ttsManagerMethodHook = "a";
					
			SearchResultFetcherQueryHook = "w";
				    
			searchQueryTextHook = "bkt";
			mCacheHook = "mCache";
			mClockHook = "mClock";
			mCacheResultHook = "a";
				    
			MyVoiceSearchControllerListenerMethodHook = "a";
			CharSequenceClassHook = "heb";
			CharSequenceClassHook2 = "bul";
		}
		
		if (versionCheck == 300305150) {
			SearchControllerClassHook = "bir";
			MyVoiceSearchControllerListenerClassHook = "bjb";
			SearchResultFetcherClassHook = "bur";
			SearchOverlayImplClassHook = "cmh";
								
			mContextHook = "mContext";
						
			mVoiceSearchServicesHook = "mVoiceSearchServices";
			ttsManagerHook = "azL";
			ttsManagerMethodHook = "a";
					
			SearchResultFetcherQueryHook = "w";
				    
			searchQueryTextHook = "mQueryChars";
			mCacheHook = "mCache";
			mClockHook = "mClock";
			mCacheResultHook = "a";
				    
			MyVoiceSearchControllerListenerMethodHook = "a";
			CharSequenceClassHook = "heb";
			CharSequenceClassHook2 = "bul";
		}
		
		if (versionCheck == 300305140) {
			SearchControllerClassHook = "bir";
			MyVoiceSearchControllerListenerClassHook = "bjb";
			SearchResultFetcherClassHook = "bur";
			SearchOverlayImplClassHook = "cmh";
								
			mContextHook = "mContext";
						
			mVoiceSearchServicesHook = "mVoiceSearchServices";
			ttsManagerHook = "azL";
			ttsManagerMethodHook = "a";
					
			SearchResultFetcherQueryHook = "w";
				    
			searchQueryTextHook = "mQueryChars";
			mCacheHook = "mCache";
			mClockHook = "mClock";
			mCacheResultHook = "get";
				    
			MyVoiceSearchControllerListenerMethodHook = "a";
			CharSequenceClassHook = "heb";
			CharSequenceClassHook2 = "bul";
		}
		
		
		//Final Hook Set
		final String SearchControllerClassHookFinal = SearchControllerClassHook;
		final String MyVoiceSearchControllerListenerClassHookFinal = MyVoiceSearchControllerListenerClassHook;
		final String SearchResultFetcherClassHookFinal = SearchResultFetcherClassHook;
		final String SearchOverlayImplClassHookFinal = SearchOverlayImplClassHook;
					
		final String mContextHookFinal = mContextHook;
			
		final String mVoiceSearchServicesHookFinal = mVoiceSearchServicesHook;
		final String ttsManagerHookFinal = ttsManagerHook;
		final String ttsManagerMethodHookFinal = ttsManagerMethodHook;
		
	    	final String SearchResultFetcherQueryHookFinal = SearchResultFetcherQueryHook;
	    
	    	final String searchQueryTextHookFinal = searchQueryTextHook;
		final String mCacheHookFinal = mCacheHook;
		final String mClockHookFinal = mClockHook;
		final String mCacheResultHookFinal = mCacheResultHook;
	    
		final String MyVoiceSearchControllerListenerMethodHookFinal = MyVoiceSearchControllerListenerMethodHook;
		final String CharSequenceClassHookFinal = CharSequenceClassHook;
		final String CharSequenceClassHook2Final = CharSequenceClassHook2;

		// com.google.android.search.shared.api.Query
		Class<?> Query = findClass("com.google.android.shared.search.Query", lpparam.classLoader);

        	// com.google.android.search.core.SearchController
        	// Google Search V3.4: azs
        	// Google Search V3.5: bir
        	Class<?> SearchController = findClass(SearchControllerClassHookFinal, lpparam.classLoader);

		// com.google.android.search.core.SearchController$MyVoiceSearchControllerListener
        	// Google Search V3.4: bae
        	// Google Search V3.5: bjb
		Class<?> MyVoiceSearchControllerListener = findClass(MyVoiceSearchControllerListenerClassHookFinal, lpparam.classLoader);
		
		// com.google.android.search.core.prefetch.SearchResultFetcher
        	// Google Search V3.4: blq
        	// Google Search V3.5: bur
		Class<?> SearchResultFetcher = findClass(SearchResultFetcherClassHookFinal, lpparam.classLoader);
		
		// com.google.android.search.gel.SearchOverlayImpl
        	// Google Search V3.4: ccu
        	// Google Search V3.5: cmh
		Class<?> SearchOverlayImpl = findClass(SearchOverlayImplClassHookFinal, lpparam.classLoader);

		XposedBridge.hookAllConstructors(SearchController, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				
				
				final Object thisObject = param.thisObject;
				mContext = (Context) getObjectField(param.thisObject, mContextHookFinal);
				mQueuedIntentList = new ArrayList<Intent>();
				mContext.registerReceiver(new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
						String string = intent.getStringExtra(GoogleSearchApi.KEY_TEXT_TO_SPEAK);
						if (TextUtils.isEmpty(string))
							return;
						
						Object mVoiceSearchServices = getObjectField(thisObject, mVoiceSearchServicesHookFinal);
			// getLocalTtsManager
                        // Google Search V3.4: asi
                        // Google Search V3.5: azL
						Object ttsManager = XposedHelpers.callMethod(mVoiceSearchServices, ttsManagerHookFinal);
						try {
                        // Google Search V3.4: a
                        // Google Search V3.5: a
							XposedHelpers.callMethod(ttsManager, ttsManagerMethodHookFinal, string, null, 0);
						} catch (NoSuchMethodError e) {
							e.printStackTrace();
							try {
								Field f = XposedHelpers.findFirstFieldByExactType(ttsManager.getClass(),
										TextToSpeech.class);
								f.setAccessible(true);
								TextToSpeech speech = (TextToSpeech) f.get(ttsManager);
								speech.speak(string, TextToSpeech.QUEUE_FLUSH, null);
							} catch (Exception e1) {
								e1.printStackTrace();
							}
						}
					}
				}, new IntentFilter(GoogleSearchApi.INTENT_REQUEST_SPEAK));

				IntentFilter iF = new IntentFilter();
				iF.addAction(Constants.INTENT_SETTINGS_UPDATED);
				iF.addAction(Constants.INTENT_FLUSH_INTENTS);
				iF.addAction(Constants.INTENT_QUEUE_INTENT);

				mContext.registerReceiver(internalReceiver, iF);
			}
		});

		// obtainSearchResult
        // Google Search V3.4: s
        // Google Search V3.5: w
				
		findAndHookMethod(SearchResultFetcher, SearchResultFetcherQueryHookFinal, Query, new XC_MethodHook() {
			@Override
			protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
		
				Object queryResult = param.args[0];
				CharSequence searchQueryText = (CharSequence) getObjectField(queryResult, searchQueryTextHookFinal);
				Object mCache = getObjectField(param.thisObject, mCacheHookFinal);
				Object mClock = getObjectField(param.thisObject, mClockHookFinal);
				Object mCachedResult = XposedHelpers.callMethod(mCache, mCacheResultHookFinal, queryResult,
						XposedHelpers.callMethod(mClock, "elapsedRealtime"),
						true);
				
				mPreferences.reload();

				/* Not doing this causes a usability issue. If the user has a search showing
				 * results, and they tap the mic, then cancel the voice search, then the search
				 * is handled again, thus throwing the user in an infinite loop of pressing back,
				 * until the user figures it out and uses the task switcher to close Google Search.
				 */
				if (mCachedResult != null && mContext != null
						&& mPreferences.getBoolean(Constants.KEY_PREVENT_DUPLICATES, true)) {
					return;
				}

				if (mContext != null) {
					broadcastGoogleSearch(mContext, searchQueryText, false,
							mPreferences.getBoolean(Constants.KEY_DELAY_BROADCASTS, false));
				} else {
					XposedBridge.log(String.format("Google Search API: New Search detected: %s",
							searchQueryText.toString()));
				}
			}
		});

		// onRecognitionResult
        	// Google Search V3.4: s // a(CharSequence paramCharSequence, glq paramglq, blk paramblk)
        	// Google Search V3.5: w // a(CharSequence paramCharSequence, hea paramhea, bul parambul)
		findAndHookMethod(MyVoiceSearchControllerListener, MyVoiceSearchControllerListenerMethodHookFinal, CharSequence.class, findClass(CharSequenceClassHookFinal, lpparam.classLoader), findClass(CharSequenceClassHook2Final, lpparam.classLoader), new XC_MethodHook() {
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

		/* GEL workaround, GEL opens Google Search eventually, so this will overlay whatever
		 * activity a developer has made. This broadcasts intents after the window has gained focus.
		 */				
		findAndHookMethod(SearchOverlayImpl, "onWindowFocusChanged", boolean.class, new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				boolean hasFocus = (Boolean) param.args[0];

				Context context = (Context) getObjectField(param.thisObject, "mContext");
				if (context != null && !hasFocus)
					context.sendBroadcast(new Intent(Constants.INTENT_FLUSH_INTENTS));
			}
		});
	}
	
	private void touchOurself(ClassLoader classLoader) {
		findAndHookMethod("com.mohammadag.googlesearchapi.UiUtils", classLoader,
				"isHookActive", XC_MethodReplacement.returnConstant(true));
	}
	
	
	
	private static void broadcastGoogleSearch(Context context, CharSequence searchText, boolean voice, boolean delayed) {
		Intent intent = new Intent(GoogleSearchApi.INTENT_NEW_SEARCH);
		intent.putExtra(GoogleSearchApi.KEY_VOICE_TYPE, voice);
		intent.putExtra(GoogleSearchApi.KEY_QUERY_TEXT, searchText.toString());
		if (delayed) {
			mQueuedIntentList.add(intent);
		} else {
			sendBroadcast(context, intent);
		}
	}
	
	private static void sendBroadcast(Context context, Intent intent) {
		context.sendBroadcast(intent, Constants.PERMISSION);
	}
}
