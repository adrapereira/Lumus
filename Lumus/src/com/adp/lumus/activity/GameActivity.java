package com.adp.lumus.activity;

import java.io.IOException;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.adp.lumus.general.ResourcesManager;
import com.adp.lumus.general.SceneManager;
import com.example.android.util.IabHelper;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class GameActivity extends BaseGameActivity{
	private Camera camera;
	private ResourcesManager rm;
	IabHelper mHelper;


	@Override
	protected void onSetContentView() {
		final FrameLayout frameLayout = new FrameLayout(this);
		final FrameLayout.LayoutParams frameLayoutLayoutParams =
				new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);

		final AdView adView = new AdView(this, AdSize.SMART_BANNER, fazString());
		adView.refreshDrawableState();
		adView.setVisibility(AdView.VISIBLE);
		final FrameLayout.LayoutParams adViewLayoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
				FrameLayout.LayoutParams.WRAP_CONTENT,
				Gravity.BOTTOM);

		AdRequest adRequest = new AdRequest();
		adRequest.addTestDevice( AdRequest.TEST_EMULATOR);
		adRequest.addTestDevice("C84C92123BF1F99B66ADFE36202BCBF3");
		adRequest.addTestDevice("AAD3E0D55E4489B5F2C2C181BB98F3F3");
		adRequest.addTestDevice("FCC21A0D88996B0DEFD693DD6D5813FB");
		adRequest.addTestDevice("1C092E5035CE26CB17232E6F7FF6E001");
		adView.loadAd(adRequest);

		this.mRenderSurfaceView = new RenderSurfaceView(this);

		mRenderSurfaceView.setRenderer(mEngine, this);

		final android.widget.FrameLayout.LayoutParams surfaceViewLayoutParams =
				new FrameLayout.LayoutParams(super.createSurfaceViewLayoutParams());

		frameLayout.addView(this.mRenderSurfaceView, surfaceViewLayoutParams);
		frameLayout.addView(adView, adViewLayoutParams);

		this.setContentView(frameLayout, frameLayoutLayoutParams);
	}

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions) {
		return new LimitedFPSEngine(pEngineOptions, 60);
	}

	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, 480, 800);
		EngineOptions engineOptions = new EngineOptions(false, ScreenOrientation.PORTRAIT_FIXED, new FillResolutionPolicy(), this.camera);
		engineOptions.getAudioOptions().setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_DIM);
		return engineOptions;
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException{
		ResourcesManager.prepareManager(mEngine, this, camera, getVertexBufferObjectManager());
		rm = ResourcesManager.getInstance();
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException{
		SceneManager.getInstance().createSplashScene(pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException{
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback() 
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) 
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				SceneManager.getInstance().createMenuScene();
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
		if (keyCode == KeyEvent.KEYCODE_BACK){
			SceneManager.getInstance().getCurrentScene().onBackKeyPressed();
		}
		return false; 
	}


	@Override
	protected void onDestroy(){
		rm.saveGame();
		rm.cp.destroy();

		super.onDestroy();

		if (this.isGameLoaded()){
			System.exit(0);    
		}
	}

	@Override
	protected void onPause(){
		rm.music.pause();
		rm.saveGame();
		super.onPause();
	}

	@Override
	protected void onResume(){
		super.onResume();
		if(rm != null)
			if(rm.music != null)
				rm.music.resume();
	}


	public void setIabHelper(IabHelper mH){
		mHelper = mH;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d("activityResult", "onActivityResult(" + requestCode + "," + resultCode + "," + data);

		// Pass on the activity result to the helper for handling
		if(mHelper != null){
			if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
				// not handled, so handle it ourselves (here's where you'd
				// perform any handling of activity results not related to in-app
				// billing...
				super.onActivityResult(requestCode, resultCode, data);
			}
			else {
				Log.d("activityResult", "onActivityResult handled by IABUtil.");
			}
		}else Log.d("activityResult", "No IabHelper found!!");
	}

}
