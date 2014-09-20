package com.adp.lumus.menu;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
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
import org.andengine.util.adt.color.Color;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.adp.lumus.R;
import com.adp.lumus.general.BaseScene;
import com.adp.lumus.general.SceneManager;
import com.adp.lumus.general.SceneManager.SceneType;

public class OptionsMenuScrollingScene extends BaseScene implements IOnMenuItemClickListener{
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	//Resto
	private final int NOPTIONS = 4;//numero de opcoes que se pode escolher
	public static final int RESET = 1;
	public static final int RATE = RESET + 1;
	public static final int FEEDBACK = RATE + 1;
	public static final int SOUND = FEEDBACK + 1;

	private MenuScene menuChildScene;
	public int opcaoSeleccionada; //guarda a opcao seleccionada para o metodo OnMenuItem Clicked
	private String toastString;
	private Text soundText;
	private final Scene thisScene = this;


	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------

	@Override
	public void createScene() {
		createBackground();
		createMenuChildScene();
		createTop();
	}

	@Override
	public void onBackKeyPressed() {
		SceneManager.getInstance().loadMenuScene(engine);
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_PACOTES;
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

	//---------------------------------------------
	// METHODS
	//---------------------------------------------

	private void createMenuChildScene(){
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(240, 400);

		IMenuItem reset = new ScaleMenuItemDecorator(new SpriteMenuItem(RESET, rm.options_region, vbom), 1.1f, 1);
		IMenuItem review = new ScaleMenuItemDecorator(new SpriteMenuItem(RATE, rm.options_region, vbom), 1.1f, 1);
		IMenuItem feedback = new ScaleMenuItemDecorator(new SpriteMenuItem(FEEDBACK, rm.options_region, vbom), 1.1f, 1);
		IMenuItem sound = new ScaleMenuItemDecorator(new SpriteMenuItem(SOUND, rm.options_region, vbom), 1.1f, 1);


		menuChildScene.addMenuItem(reset);
		menuChildScene.addMenuItem(review);
		menuChildScene.addMenuItem(feedback);
		menuChildScene.addMenuItem(sound);

		menuChildScene.setBackgroundEnabled(false);	

		reset.setPosition(0, 230);
		review.setPosition(0, 140);
		feedback.setPosition(0, 50);
		sound.setPosition(0, -40);

		menuChildScene.setOnMenuItemClickListener(this);
		createHUD();
		setChildScene(menuChildScene);
	}


	public void createHUD(){
		Text[] textos = new Text[NOPTIONS];

		// CREATE SCORE TEXT
		for(int i = 1; i < NOPTIONS; i++){
			//Texto inferior que diz o tipo do pacote
			textos[i] = new Text(240, 240, rm.levelFont, "ABCDEFGHIJKLMOPQRSTUVWXYZ", new TextOptions(HorizontalAlign.CENTER), vbom);
			textos[i].setAnchorCenter(0, 0);

			switch(i){
			case RESET: 
				textos[i].setText(rm.activity.getString(R.string.resetall)); textos[i].setY(230 - (textos[i].getHeight()/2)); break;
			case RATE: 
				textos[i].setText(rm.activity.getString(R.string.rate)); textos[i].setY(140 - (textos[i].getHeight()/2)); break;
			case FEEDBACK: 
				textos[i].setText(rm.activity.getString(R.string.feedback)); textos[i].setY(50 - (textos[i].getHeight()/2)); break;
			case SOUND: 
				break;
			default: break;
			}
			textos[i].setX(-textos[i].getWidth()/2);

			menuChildScene.attachChild(textos[i]);
		}
		
		soundText = new Text(240, 240, rm.levelFont, "ABCDEFGHIJKLMOPQRSTUVWXYZ", new TextOptions(HorizontalAlign.CENTER), vbom);
		soundText.setAnchorCenter(0, 0);
		textoSound();
		
		Text textoFinal1 = new Text(0, -160, rm.levelFont, rm.activity.getString(R.string.gameBy) + " ADRAPP\n\n Website: www.reddit.com/r/lumus", new TextOptions(HorizontalAlign.CENTER), vbom);
		Text textoFinal2 = new Text(0, -250, rm.pacoteFont, rm.activity.getString(R.string.afterWebsite), new TextOptions(HorizontalAlign.CENTER), vbom);

		menuChildScene.attachChild(soundText);
		menuChildScene.attachChild(textoFinal1);
		menuChildScene.attachChild(textoFinal2);
		
	}

	private void textoSound(){
		String s;
		if(rm.preferences.getBoolean("sound", true))
			s = rm.activity.getString(R.string.soundOff);
		else s = rm.activity.getString(R.string.soundOn);
		
		soundText.setText(s); 
		soundText.setY(-40 - (soundText.getHeight()/2));
		soundText.setX(-(soundText.getWidth()/2));
	}
	
	private void createBackground(){
		setBackground(new Background(Color.WHITE));
		Sprite backg = new Sprite(0, 0, rm.background_region, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		backg.setPosition(240, 400);
		attachChild(backg);
	}

	private void createTop(){
		Sprite banner = new Sprite(0, 0, rm.topBannerPacotes_region, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		Text texto = new Text(0, 345, rm.titleFont, "Options", new TextOptions(HorizontalAlign.CENTER), vbom);

		banner.setPosition(0, 345);
		menuChildScene.attachChild(banner);
		menuChildScene.attachChild(texto);
	}
	
	//ESCOLHER OPCAO
	private void opcaoReset(){
		rm.activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rm.activity);
				// set title
				alertDialogBuilder.setTitle(rm.activity.getString(R.string.resetall)); // **
				// set dialog message
				alertDialogBuilder
				.setMessage(rm.activity.getString(R.string.sureResetAll)) // **
				.setCancelable(false)
				.setPositiveButton(rm.activity.getString(R.string.yes),new DialogInterface.OnClickListener() { // **
					@Override
					public void onClick(DialogInterface dialog,int id) {
						rm.resetSave();
					}
				})
				.setNegativeButton(rm.activity.getString(R.string.no),new DialogInterface.OnClickListener() { // **
					@Override
					public void onClick(DialogInterface dialog,int id) {
						dialog.cancel(); // if this button is clicked, just close the dialog box and do nothing
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create(); // create alert dialog
				alertDialog.show(); // show it
			}
		});
	}

	//CARREGAR NUM ITEM
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		boolean toast = false;
		switch(pMenuItem.getID()){
		case RESET:
			opcaoReset();
			break;
		case RATE:
			Intent intent = new Intent(Intent.ACTION_VIEW);
			//Try Google play
			intent.setData(Uri.parse("market://details?id=com.adp.lumus"));
			if (MyStartActivity(intent) == false) {
				//Market (Google play) app seems not installed, let's try to open a webbrowser
				intent.setData(Uri.parse("https://play.google.com/store/apps/details?com.adp.lumus"));
				if (MyStartActivity(intent) == false) {
					//Well if this also fails, we have run out of options, inform the user.
					Toast.makeText(rm.activity, "Could not open Play Store, please install the store app.", Toast.LENGTH_SHORT).show();
				}
			}
			break;
		case FEEDBACK: //envia um email com o feedback
			final Intent _Intent = new Intent(android.content.Intent.ACTION_SEND);
			_Intent.setType("text/plain");
			_Intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ "adrappcs@gmail.com" });
			_Intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Lumus Feedback");
			_Intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
			rm.activity.startActivity(Intent.createChooser(_Intent, "Send Feedback"));
			break;
		case SOUND:
			if(rm.preferences.getBoolean("sound", true)){
				rm.preferences.edit().putBoolean("sound", false).commit();
				rm.music.pause();
			}else {
				rm.preferences.edit().putBoolean("sound", true).commit();
				rm.music.play();
			}
			textoSound();
			break;
		default:
			onBackKeyPressed();
			Log.d("ERRO!", "Nao existe definiçao criada na opcao seleccionada");
			break;
		}

		if(toast)
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
				}
			});

		return true;
	}

	private boolean MyStartActivity(Intent aIntent) {
		try{
			rm.activity.startActivity(aIntent);
			return true;
		}
		catch (ActivityNotFoundException e){
			return false;
		}
	}
}
