package com.adp.lumus.menu;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.color.Color;

import com.adp.lumus.general.BaseScene;
import com.adp.lumus.general.SceneManager.SceneType;

public class SplashScene extends BaseScene {

	private Sprite splash;

	@Override
	public void createScene() {
		setBackground(new Background(Color.BLACK));
		splash = new Sprite(0, 0, rm.splash_region, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera) 
			{
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		splash.setPosition(240, 400);
		attachChild(splash);

	}

	@Override
	public void onBackKeyPressed() {
		System.exit(0);

	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_SPLASH;
	}

	@Override
	public void disposeScene() {
		splash.detachSelf();
		splash.dispose();
		this.detachSelf();
		this.dispose();

	}

}
