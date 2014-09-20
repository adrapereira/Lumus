package com.adp.lumus.menu;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.IOnSceneTouchListener;
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
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.util.Log;
import android.widget.Toast;

import com.adp.lumus.R;
import com.adp.lumus.general.BaseScene;
import com.adp.lumus.general.SceneManager;
import com.adp.lumus.general.SceneManager.SceneType;

public class MenuPacotesScene extends BaseScene implements IOnMenuItemClickListener, IScrollDetectorListener, IOnSceneTouchListener  {

	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	//Scrolling
	private final float FREQ_D = 120.0f;
	private final int STATE_WAIT = 0;
	private final int STATE_SCROLLING = 1;
	private final int STATE_MOMENTUM = 2;
	private final int STATE_DISABLE = 3;
	private final int MAXSCROLL = 600;
	private final int MINSCROLL = 400;
	private final float MAX_ACCEL = 5000;
	private final double FRICTION = 0.96f;

	private SurfaceScrollDetector mScrollDetector;
	private double accel, accel1, accel0;
	private int mState = STATE_DISABLE;
	private float mCurrentY;
	private long t0;

	//Resto
	public static final int PACOTES = 10;
	public static final int FREE_1 = 1;
	public static final int FREE_2 = 2;
	public static final int FREE_3 = 3;
	public static final int FREE_4 = 4;
	public static final int FREE_5 = 5;
	public static final int UNLOCKABLE_1 = 6;
	public static final int UNLOCKABLE_2 = 7;
	public static final int PAID_1 = 8;
	public static final int PAID_2 = 9;
	public static final int PAID_3 = 10;

	private final int DESVIOX = 185;
	private final int DESVIOY = -460; //Y do ultimo pacote

	private MenuScene menuChildScene;
	private TimerHandler thandle;
	private final Scene thisScene = this;


	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------

	@Override
	public void createScene() {
		mScrollDetector = new SurfaceScrollDetector(2, this);
		setOnSceneTouchListener(this);

		thandle = new TimerHandler(1.0f / FREQ_D, true, new ITimerCallback() {
			@Override
			public void onTimePassed(final TimerHandler pTimerHandler) {
				doSetPos();
			}
		});
		
		if(unlock1())	
			rm.preferences.edit().putBoolean("UNLOCKABLE_1", true).commit();
		else rm.preferences.edit().putBoolean("UNLOCKABLE_1", false).commit();
		if(unlock2())	
			rm.preferences.edit().putBoolean("UNLOCKABLE_2", true).commit();
		else rm.preferences.edit().putBoolean("UNLOCKABLE_2", false).commit();

		createBackground();
		createMenuChildScene();
		createTop();

		registerUpdateHandler(thandle);
		mState = STATE_WAIT;
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

		IMenuItem free1 = createPacote(FREE_1);
		IMenuItem free2 = createPacote(FREE_2);
		IMenuItem free3 = createPacote(FREE_3);
		IMenuItem free4 = createPacote(FREE_4);
		IMenuItem free5 = createPacote(FREE_5);
		IMenuItem unl1 = createPacote(UNLOCKABLE_1);
		IMenuItem unl2 = createPacote(UNLOCKABLE_2);
		IMenuItem paid1 = createPacote(PAID_1);
		IMenuItem paid2 = createPacote(PAID_2);
		IMenuItem paid3 = createPacote(PAID_3);


		menuChildScene.addMenuItem(free1);
		menuChildScene.addMenuItem(free2);
		menuChildScene.addMenuItem(free3);
		menuChildScene.addMenuItem(free4);
		menuChildScene.addMenuItem(free5);
		menuChildScene.addMenuItem(unl1);
		menuChildScene.addMenuItem(unl2);
		menuChildScene.addMenuItem(paid1);
		menuChildScene.addMenuItem(paid2);
		menuChildScene.addMenuItem(paid3);

		menuChildScene.setBackgroundEnabled(false);

		free1.setPosition(0, 260);
		free2.setPosition(0, 180);
		free3.setPosition(0, 100);
		free4.setPosition(0, 20);
		free5.setPosition(0, -60);
		unl1.setPosition(0, -140);
		unl2.setPosition(0, -220);
		paid1.setPosition(0, -300);
		paid2.setPosition(0, -380);
		paid3.setPosition(0, -460);


		menuChildScene.setOnMenuItemClickListener(this);
		createHUD();
		setChildScene(menuChildScene);

		mCurrentY = menuChildScene.getY();
	}

	private IMenuItem createPacote(int id){
		SpriteMenuItem fr = new SpriteMenuItem(id, rm.pacote_region, vbom);

		final IMenuItem free = new ScaleMenuItemDecorator(fr, 1.1f, 1){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				mScrollDetector.onTouchEvent(pSceneTouchEvent);
				if(mState == STATE_WAIT || mState == STATE_DISABLE){
					rm.pacoteSeleccionado = this.getID();
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				return true;
			}
		};
		return free;
	}

	public void createHUD(){
		float y;
		int j, soma;
		Text[] textos = new Text[(PACOTES + 1)*3];

		// CREATE SCORE TEXT
		for(int i = 1; i <= PACOTES; i++){
			//Texto superior que diz o numero do pacote
			y = DESVIOY + (PACOTES - i) * 80 + 15;
			textos[i] = new Text(-DESVIOX, y, rm.pacoteFont, "Pack 123456789", new TextOptions(HorizontalAlign.CENTER), vbom);
			textos[i].setText(rm.activity.getString(R.string.pack) + " " + i);
			textos[i].setX(-DESVIOX + (textos[i].getWidth()/2));

			//Texto inferior que diz o tipo do pacote

			y = DESVIOY + (PACOTES - i) * 80 - 15;
			textos[i+PACOTES] = new Text(-DESVIOX, y, rm.descriptionFont, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz", new TextOptions(HorizontalAlign.CENTER), vbom);

			switch(i){
				case FREE_1: textos[i + PACOTES].setText(stringR(R.string.free, R.string.easy)); break;
				case FREE_2: textos[i + PACOTES].setText(stringR(R.string.free, R.string.normal)); break;
				case FREE_3: textos[i + PACOTES].setText(stringR(R.string.free, R.string.normal)); break;
				case FREE_4: textos[i + PACOTES].setText(stringR(R.string.free, R.string.hard)); break;
				case FREE_5: textos[i + PACOTES].setText(stringR(R.string.free, R.string.hard)); break;
				case UNLOCKABLE_1: textos[i + PACOTES].setText(stringR(R.string.unlockable, R.string.normal)); break;
				case UNLOCKABLE_2: textos[i + PACOTES].setText(stringR(R.string.unlockable, R.string.hard)); break;
				case PAID_1: textos[i + PACOTES].setText(stringR(R.string.paid, R.string.normal)); break;
				case PAID_2: textos[i + PACOTES].setText(stringR(R.string.paid, R.string.hard)); break;
				case PAID_3: textos[i + PACOTES].setText(stringR(R.string.paid, R.string.hard)); break;
				default: break;
			}
			textos[i+PACOTES].setX(-DESVIOX + (textos[i+PACOTES].getWidth()/2));


			//numero de niveis completos 
			//Por ex: para o pacote 0 conta quantos niveis entre 1 e 20 estao completos
			soma = 0;
			y =  DESVIOY + (PACOTES - i) * 80;
			for(j = 1; j <= 30; j++){ 
				if(rm.save.getNivel(i, j).isResolvido())
					soma++;
			}
			textos[i+PACOTES*2] = new Text(DESVIOX, y, rm.completedFont, "    / 0123456789", new TextOptions(HorizontalAlign.RIGHT), vbom);
			textos[i+PACOTES*2].setText(soma + " / 30");
			textos[i+PACOTES*2].setX(DESVIOX - (textos[i+PACOTES*2].getWidth()/2));
			menuChildScene.attachChild(textos[i]);
			menuChildScene.attachChild(textos[i+PACOTES]);
			menuChildScene.attachChild(textos[i+PACOTES*2]);
		}
	}

	private String stringR(int a, int b){
		return rm.activity.getString(a) + "  -  " + rm.activity.getString(b);
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

		Text texto = new Text(0, 345, rm.titleFont, rm.activity.getString(R.string.levelpacks), new TextOptions(HorizontalAlign.CENTER), vbom);

		banner.setPosition(0, 345);
		menuChildScene.attachChild(banner);
		menuChildScene.attachChild(texto);
	}

	//SCROLLING
	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if (mState == STATE_DISABLE)
			return true;

		if (mState == STATE_MOMENTUM) {
			accel0 = accel1 = accel = 0;
			mState = STATE_WAIT;
		}

		this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
		return true;
	}

	@Override
	public void onScrollStarted(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
		t0 = System.currentTimeMillis();
		mState = STATE_SCROLLING;
	}


	@Override
	public void onScroll(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
		long dt = System.currentTimeMillis() - t0;
		if (dt == 0)
			return;
		double s =  (pDistanceY - (pDistanceY/15)) / (double)dt * 1000.0;  // pixel/second
		accel = (accel0 + accel1 + s) / 3;
		accel0 = accel1;
		accel1 = accel;

		t0 = System.currentTimeMillis();
		mState = STATE_SCROLLING;
	}

	@Override
	public void onScrollFinished(ScrollDetector pScollDetector, int pPointerID, float pDistanceX, float pDistanceY) {
		mState = STATE_MOMENTUM;
	}

	protected synchronized void doSetPos() {
		if(mState == STATE_MOMENTUM && (mCurrentY == MAXSCROLL || mCurrentY == MINSCROLL))
			mState = STATE_WAIT;

		if (accel == 0) {
			return;
		}

		if (mCurrentY > MAXSCROLL) {
			mCurrentY = MAXSCROLL;
			mState = STATE_WAIT;
			accel0 = accel1 = accel = 0;
		}
		if (mCurrentY < MINSCROLL) {
			mCurrentY = MINSCROLL;
			mState = STATE_WAIT;
			accel0 = accel1 = accel = 0;
		}

		menuChildScene.setPosition(menuChildScene.getX(), mCurrentY);

		if (accel < 0 && accel < -MAX_ACCEL)
			accel0 = accel1 = accel = - MAX_ACCEL;
		if (accel > 0 && accel > MAX_ACCEL)
			accel0 = accel1 = accel = MAX_ACCEL;

		double ny = accel / FREQ_D;
		if (ny >= -1 && ny <= 1) {
			mState = STATE_WAIT;
			accel0 = accel1 = accel = 0;
			return;
		}
		if (! (Double.isNaN(ny) || Double.isInfinite(ny)))
			mCurrentY -= ny;
		accel = (accel * FRICTION);
	}

	//CARREGAR NUM ITEM
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY) {
		carregarPacote();
		return true;
	}

	private void carregarPacote(){
		boolean carregar = true, comprar = false;
		int pacote = 0;
		switch(rm.pacoteSeleccionado){
			case FREE_1:
			case FREE_2:
			case FREE_3:
			case FREE_4:
			case FREE_5:
				break;
			case UNLOCKABLE_1:
				if(!rm.preferences.getBoolean("UNLOCKABLE_1", false)){
					carregar = false;
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, rm.activity.getString(R.string.complete1), Toast.LENGTH_SHORT).show();
						}
					});
				}
				break;
			case UNLOCKABLE_2:
				if(!rm.preferences.getBoolean("UNLOCKABLE_2", false)){
					carregar = false;
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(activity, rm.activity.getString(R.string.complete2), Toast.LENGTH_SHORT).show();
						}
					});
				}
				break;
			case PAID_1:
				if(!rm.preferences.getBoolean("PAID_1", false)){
					carregar = false;
					comprar = true;
					pacote = 1;
				}
				break;
			case PAID_2:
				if(!rm.preferences.getBoolean("PAID_2", false)){
					carregar = false;
					comprar = true;
					pacote = 2;
				}
				break;
			case PAID_3:
				if(!rm.preferences.getBoolean("PAID_3", false)){
					carregar = false;
					comprar = true;
					pacote = 3;
				}
				break;
			default:
				onBackKeyPressed();
				Log.d("ERRO!", "Nao existe definiçao criada num pacote seleccionado");
				break;
		}
		if(carregar)
			SceneManager.getInstance().loadNiveisScene(engine, false, rm);
		
		if(comprar){
			rm.cp.onBuyPacoteButtonClicked(pacote);
		}
	}
	
	private boolean unlock1(){
		boolean res = true;
		
		for(int i = 1; i <= 3 && res; i++){
			for(int j = 1; j <= 30 && res; j++){
				if(!rm.save.getNivel(i, j).isResolvido())
					res = false;
			}
		}
		return res;
	}
	
	private boolean unlock2(){
		boolean res = true;
		
		for(int i = 4; i <= 5 && res; i++){
			for(int j = 1; j <= 30 && res; j++){
				if(!rm.save.getNivel(i, j).isResolvido())
					res = false;
			}
		}
		return res;
	}
	
}