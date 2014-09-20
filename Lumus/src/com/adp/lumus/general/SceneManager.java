package com.adp.lumus.general;

import org.andengine.engine.Engine;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.ui.IGameInterface.OnCreateSceneCallback;

import android.widget.Toast;

import com.adp.lumus.R;
import com.adp.lumus.game.GameScene;
import com.adp.lumus.menu.HowToPlayScene;
import com.adp.lumus.menu.MainMenuScene;
import com.adp.lumus.menu.MenuNivelScene;
import com.adp.lumus.menu.MenuPacotesScene;
import com.adp.lumus.menu.OptionsMenuScrollingScene;
import com.adp.lumus.menu.SplashScene;
import com.adp.lumus.menu.StatsScene;

public class SceneManager {
	//---------------------------------------------
	// SCENES
	//---------------------------------------------

	private BaseScene splashScene;
	private BaseScene menuScene;
	private BaseScene gameScene;
	private BaseScene loadingScene;
	private BaseScene optionsScene;
	private BaseScene pacotesScene;
	private BaseScene howtoplayScene;
	private BaseScene statsScene;
	private MenuNivelScene niveisScene;

	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------

	private static final SceneManager INSTANCE = new SceneManager();

	private SceneType currentSceneType = SceneType.SCENE_SPLASH;

	private BaseScene currentScene;
	private BaseScene disposableScene;
	
	private ResourcesManager rm = null;

	private final Engine engine = ResourcesManager.getInstance().engine;

	public enum SceneType{
		SCENE_SPLASH,
		SCENE_MENU,
		SCENE_GAME,
		SCENE_LOADING,
		SCENE_PACOTES,
		SCENE_NIVEIS,
		SCENE_OPTIONS,
		SCENE_HOWTOPLAY,
		SCENE_STATS,
	}

	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------

	public void setScene(BaseScene scene){
		engine.setScene(scene);
		currentScene = scene;
		currentSceneType = scene.getSceneType();
	}

	public void setScene(SceneType sceneType){
		switch (sceneType){
		case SCENE_MENU:
			setScene(menuScene);
			break;
		case SCENE_GAME:
			setScene(gameScene);
			break;
		case SCENE_SPLASH:
			setScene(splashScene);
			break;
		case SCENE_LOADING:
			setScene(loadingScene);
			break;
		case SCENE_PACOTES:
			setScene(pacotesScene);
			break;
		case SCENE_NIVEIS:
			setScene(niveisScene);
			break;
		case SCENE_OPTIONS:
			setScene(optionsScene);
			break;
		case SCENE_HOWTOPLAY:
			setScene(howtoplayScene);
			break;
		case SCENE_STATS:
			setScene(statsScene);
			break;
		default:
			break;
		}
	}

	public void createSplashScene(OnCreateSceneCallback pOnCreateSceneCallback){
		ResourcesManager.getInstance().loadSplashScreen();
		splashScene = new SplashScene();
		currentScene = splashScene;
		pOnCreateSceneCallback.onCreateSceneFinished(splashScene);
	}

	private void disposeSplashScene(){
	    ResourcesManager.getInstance().unloadSplashScreen();
		splashScene.disposeScene();
		splashScene = null;
	}
	
	public void createMenuScene(){
		ResourcesManager.getInstance().loadMenuResources();
		menuScene = new MainMenuScene();
		loadingScene = new LoadingScene();
		SceneManager.getInstance().setScene(menuScene);
		disposeSplashScene();
	}

	public void loadMenuScene(final Engine mEngine){
		changeScene();
		menuScene = new MainMenuScene();
		ResourcesManager.getInstance().unloadGameTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) 
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuTextures();
				setScene(menuScene);
			}
		}));
	}
	
	public void loadOptionsScene(final Engine mEngine){
		changeScene();
		optionsScene = new OptionsMenuScrollingScene();
		ResourcesManager.getInstance().unloadGameTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuTextures();
				setScene(optionsScene);
			}
		}));
	}
	
	public void loadPacotesScene(final Engine mEngine){
		changeScene();
		pacotesScene = new MenuPacotesScene();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuTextures();
				setScene(pacotesScene);
			}
		}));
	}
	
	public void loadHowToPlayScene(final Engine mEngine){
		changeScene();
		howtoplayScene = new HowToPlayScene();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuTextures();
				setScene(howtoplayScene);
			}
		}));
	}
	
	public void loadStatsScene(final Engine mEngine){
		changeScene();
		statsScene = new StatsScene();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuTextures();
				setScene(statsScene);
			}
		}));
	}
	
	public void loadNiveisScene(final Engine mEngine, boolean fromGameScene, ResourcesManager rmanager){
		changeSceneSemDispose();
		if(fromGameScene){ //desta forma a gravaçao ocorre durante o ecra que diz loading
			rm = rmanager;
			rm.activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(rm.activity, rm.activity.getString(R.string.saving), Toast.LENGTH_SHORT).show();
				}
			});
			rm.saveGame();
		}
		disposableScene.disposeScene();
		niveisScene = new MenuNivelScene();
		ResourcesManager.getInstance().unloadGameTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadMenuTextures();
				niveisScene.createHUD();
				setScene(niveisScene);
			}
		}));
	}

	public void loadGameScene(final Engine mEngine){
		changeScene();
		ResourcesManager.getInstance().unloadMenuTextures();
		mEngine.registerUpdateHandler(new TimerHandler(0.1f, new ITimerCallback() 
		{
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ResourcesManager.getInstance().loadGameResources();
				gameScene = new GameScene();
				setScene(gameScene);
			}
		}));
	}
	
	private void changeSceneSemDispose(){
		disposableScene = currentScene;
		setScene(loadingScene);
	}
	
	private void changeScene(){
		changeSceneSemDispose();
		disposableScene.disposeScene();
	}

	//---------------------------------------------
	// GETTERS AND SETTERS
	//---------------------------------------------

	public static SceneManager getInstance(){
		return INSTANCE;
	}

	public SceneType getCurrentSceneType(){
		return currentSceneType;
	}

	public BaseScene getCurrentScene(){
		return currentScene;
	}
}
