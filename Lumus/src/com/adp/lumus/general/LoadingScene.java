package com.adp.lumus.general;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.util.adt.color.Color;

import com.adp.lumus.R;
import com.adp.lumus.general.SceneManager.SceneType;


public class LoadingScene extends BaseScene {

	@Override
	public void createScene() {
		setBackground(new Background(Color.BLACK));
	    attachChild(new Text(240, 400, rm.loadingFont, rm.activity.getString(R.string.loading), vbom));
	}

	@Override
	public void onBackKeyPressed() {
		return;
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_LOADING;
	}

	@Override
	public void disposeScene() {
		// TODO Auto-generated method stub

	}

}
