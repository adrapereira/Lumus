package com.adp.lumus.menu;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.scene.Scene;
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

public class MenuNivelScene extends BaseScene implements IOnMenuItemClickListener{

	private MenuScene menuChildScene;
	private final int NIVEIS = 30;
	private final int DESVIOX = 60;
	private final int DESVIOY = 175;
	private final int TAMANHOSPRITEX = 90;
	private final int TAMANHOSPRITEY = 95;

	private HUD sceneHUD;
	private final Scene thisScene = this;

	@Override
	public void createScene() {		
		createBackground();
		createMenuChildScene();
		createTop();
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadPacotesScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_NIVEIS;
	}

	@Override
	public void disposeScene() {
		camera.setHUD(null);
		camera.setCenter(240, 400);
		
		rm.activity.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				thisScene.detachChildren();
			}
		});

		this.detachSelf();
		this.clearTouchAreas();
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
		IMenuItem[] items = new IMenuItem[NIVEIS];
		int x, y, aux;
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);

		menuChildScene.buildAnimations();

		for(int i = 0; i < NIVEIS; i++){
			if(rm.save.getNivel(rm.pacoteSeleccionado, i + 1).isResolvido())
				items[i] = new ScaleMenuItemDecorator(new SpriteMenuItem(i, rm.nivelCompleto_region, vbom), 1.2f, 1);
			else items[i] = new ScaleMenuItemDecorator(new SpriteMenuItem(i, rm.nivelIncompleto_region, vbom), 1.2f, 1);
			aux = i;
			while(aux > 4) aux -= 5;
			x = DESVIOX + ( aux * TAMANHOSPRITEX);
			y = DESVIOY + ( (5 - (i/5)) * TAMANHOSPRITEY);
			menuChildScene.addMenuItem(items[i]);
			items[i].setPosition(x, y);
		}

		menuChildScene.setBackgroundEnabled(false);

		menuChildScene.setOnMenuItemClickListener(this);

		setChildScene(menuChildScene);
	}

	public void createHUD(){
		sceneHUD = new HUD();
		float x, y, aux;
		Text[] textos = new Text[NIVEIS];

		// CREATE SCORE TEXT
		for(int i = 0; i < NIVEIS; i++){
			aux = i;
			while(aux > 4) aux -= 5;
			
			textos[i] = new Text(2, 2, rm.levelFont, "0123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
			textos[i].setText("" + (i + 1));
			x = DESVIOX + ( aux * TAMANHOSPRITEX) - (textos[i].getWidth() / 2f);
			y = DESVIOY + ( (5 - (i/5)) * TAMANHOSPRITEY)- (textos[i].getHeight() / 2f);
			textos[i].setPosition(x, y);
			textos[i].setAnchorCenter(0, 0);  
			sceneHUD.attachChild(textos[i]);
		}

		camera.setHUD(sceneHUD);
	}

	private void createTop(){
		Sprite banner = new Sprite(0, 0, rm.topBannerPacotes_region, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		Text texto = new Text(240, 745, rm.titleFont, "Pack 123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
		texto.setText(rm.activity.getString(R.string.pack) + " " + rm.pacoteSeleccionado);
		
		banner.setPosition(240, 745);
		menuChildScene.attachChild(banner);
		menuChildScene.attachChild(texto);
	}
	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY){
		rm.nivelSeleccionado = pMenuItem.getID() + 1;
		SceneManager.getInstance().loadGameScene(engine);
		return true;
	}
}
