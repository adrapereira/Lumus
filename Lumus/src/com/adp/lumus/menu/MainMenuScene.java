package com.adp.lumus.menu;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.align.HorizontalAlign;

import com.adp.lumus.R;
import com.adp.lumus.general.BaseScene;
import com.adp.lumus.general.SceneManager;
import com.adp.lumus.general.SceneManager.SceneType;

public class MainMenuScene extends BaseScene implements IOnMenuItemClickListener {

	private MenuScene menuChildScene;
	private final int PLAY = 0;
	private final int OPTIONS = PLAY + 1;
	private final int HELP = OPTIONS + 1;
	private final int STATS = HELP + 1;
//	private final int LEVELPACKS = STATS + 1;

	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();	
		
		if(rm.preferences.getBoolean("sound", true))
			rm.music.play();
		
		createTopBanner();
	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_MENU;
	}

	@Override
	public void disposeScene() {
	}

	private void createBackground(){
		attachChild(new Sprite(240, 400, rm.background_region, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) 
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
	}

	private void createMenuChildScene(){
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);

		final IMenuItem playMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(PLAY, rm.bannerMenu_region, vbom), 1.1f, 1);
		final IMenuItem optionsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(OPTIONS, rm.bannerMenu_region, vbom), 1.1f, 1);
		final IMenuItem helpMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(HELP, rm.bannerMenu_region, vbom), 1.1f, 1);
		final IMenuItem statsMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(STATS, rm.bannerMenu_region, vbom), 1.1f, 1);
//		final IMenuItem levelpacksMenuItem = new ScaleMenuItemDecorator(new SpriteMenuItem(LEVELPACKS, rm.bannerMenu_region, vbom), 1.1f, 1);

		menuChildScene.addMenuItem(playMenuItem);
		menuChildScene.addMenuItem(optionsMenuItem);
		menuChildScene.addMenuItem(helpMenuItem);
		menuChildScene.addMenuItem(statsMenuItem);
//		menuChildScene.addMenuItem(levelpacksMenuItem);

		menuChildScene.buildAnimations();
		menuChildScene.setBackgroundEnabled(false);

		playMenuItem.setPosition(240, 450);
//		levelpacksMenuItem.setPosition(240, 400);
		helpMenuItem.setPosition(240, 360);
		statsMenuItem.setPosition(240, 270);
		optionsMenuItem.setPosition(240, 180);
		

		Text textoPlay = new Text(240, 450, rm.menuInicialFont, rm.activity.getString(R.string.play), new TextOptions(HorizontalAlign.CENTER), vbom);
		Text textoHelp = new Text(240, 360, rm.menuInicialFont, rm.activity.getString(R.string.howtoplay), new TextOptions(HorizontalAlign.CENTER), vbom);
		Text textoStats = new Text(240, 270, rm.menuInicialFont, rm.activity.getString(R.string.stats), new TextOptions(HorizontalAlign.CENTER), vbom);
		Text textoOptions = new Text(240, 180, rm.menuInicialFont, rm.activity.getString(R.string.options), new TextOptions(HorizontalAlign.CENTER), vbom);
//		Text textoLevelPacks = new Text(240, 400, rm.menuInicialFont, "Level Packs", new TextOptions(HorizontalAlign.CENTER), vbom);		

		menuChildScene.attachChild(textoPlay);
		menuChildScene.attachChild(textoOptions);
		menuChildScene.attachChild(textoHelp);
//		menuChildScene.attachChild(textoLevelPacks);
		menuChildScene.attachChild(textoStats);

		menuChildScene.setOnMenuItemClickListener(this);

		setChildScene(menuChildScene);
	}

	private void createTopBanner(){	
		attachChild(new Sprite(240, 650, rm.lumusBanner_region, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) 
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		});
	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY){
		switch(pMenuItem.getID()){
			case PLAY:
				SceneManager.getInstance().loadPacotesScene(engine);
				break;
			case OPTIONS:
				SceneManager.getInstance().loadOptionsScene(engine);
				break;
			case HELP:
				SceneManager.getInstance().loadHowToPlayScene(engine);
				break;
			case STATS:
				SceneManager.getInstance().loadStatsScene(engine);
				break;
			default:
				break;
		}
		return false;
	}

}
