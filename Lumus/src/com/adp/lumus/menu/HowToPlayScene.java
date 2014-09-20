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
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import com.adp.lumus.R;
import com.adp.lumus.general.BaseScene;
import com.adp.lumus.general.SceneManager;
import com.adp.lumus.general.SceneManager.SceneType;

public class HowToPlayScene extends BaseScene implements IOnMenuItemClickListener, IScrollDetectorListener, IOnSceneTouchListener{
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	//Scrolling
	private final float FREQ_D = 120.0f;
	private final int STATE_WAIT = 0;
	private final int STATE_SCROLLING = 1;
	private final int STATE_MOMENTUM = 2;
	private final int STATE_DISABLE = 3;
	private final int MAXSCROLL = 1300;
	private final int MINSCROLL = 400;
	private final float MAX_ACCEL = 5000;
	private final double FRICTION = 0.96f;

	private SurfaceScrollDetector mScrollDetector;
	private double accel, accel1, accel0;
	private int mState = STATE_DISABLE;
	private float mCurrentY;
	private long t0;
	private TimerHandler thandle;

	//Resto
	private MenuScene menuChildScene;
	public int opcaoSeleccionada; //guarda a opcao seleccionada para o metodo OnMenuItem Clicked
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
		return SceneType.SCENE_HOWTOPLAY;
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

		SpriteMenuItem img1 = new SpriteMenuItem(1, rm.direcoes_region, vbom);
		final IMenuItem img11 = new ScaleMenuItemDecorator(img1, 1f, 1){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				mScrollDetector.onTouchEvent(pSceneTouchEvent);
				if(mState == STATE_WAIT || mState == STATE_DISABLE){
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				return true;
			}
		};
		menuChildScene.addMenuItem(img11);

		SpriteMenuItem img2 = new SpriteMenuItem(1, rm.numerosCerto_region, vbom);
		final IMenuItem img21 = new ScaleMenuItemDecorator(img2, 1f, 1){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				mScrollDetector.onTouchEvent(pSceneTouchEvent);
				if(mState == STATE_WAIT || mState == STATE_DISABLE){
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				return true;
			}
		};
		menuChildScene.addMenuItem(img21);

		SpriteMenuItem img3 = new SpriteMenuItem(1, rm.numerosErrado_region, vbom);
		final IMenuItem img31 = new ScaleMenuItemDecorator(img3, 1f, 1){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				mScrollDetector.onTouchEvent(pSceneTouchEvent);
				if(mState == STATE_WAIT || mState == STATE_DISABLE){
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				return true;
			}
		};
		menuChildScene.addMenuItem(img31);
		
		SpriteMenuItem img4 = new SpriteMenuItem(1, rm.marca2_region, vbom);
		final IMenuItem img41 = new ScaleMenuItemDecorator(img4, 1f, 1){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				mScrollDetector.onTouchEvent(pSceneTouchEvent);
				if(mState == STATE_WAIT || mState == STATE_DISABLE){
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				return true;
			}
		};
		menuChildScene.addMenuItem(img41);
		
		SpriteMenuItem img5 = new SpriteMenuItem(1, rm.marca1_region, vbom);
		final IMenuItem img51 = new ScaleMenuItemDecorator(img5, 1f, 1){
			@Override
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,float pTouchAreaLocalX, float pTouchAreaLocalY) {
				mScrollDetector.onTouchEvent(pSceneTouchEvent);
				if(mState == STATE_WAIT || mState == STATE_DISABLE){
					return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
				}
				return true;
			}
		};
		menuChildScene.addMenuItem(img51);


		menuChildScene.setBackgroundEnabled(false);

		img11.setPosition(0, -15);
		img21.setPosition(-110, -400);
		img31.setPosition(110, -400);
		img41.setPosition(0, -890);
		img51.setPosition(0, -1110);

		menuChildScene.setOnMenuItemClickListener(this);
		createHUD();
		setChildScene(menuChildScene);

		mCurrentY = menuChildScene.getY();
	}

	public void createHUD(){
		Text primeiro = new Text(240, 230, rm.howtoplayFont, rm.activity.getString(R.string.howtoplay1), new TextOptions(HorizontalAlign.LEFT), vbom);
		primeiro.setAutoWrap(AutoWrap.WORDS);
		primeiro.setAutoWrapWidth(430);
		primeiro.setAnchorCenter(0, 0);
		primeiro.setPosition(-primeiro.getWidth()/2, 230);
		menuChildScene.attachChild(primeiro);

		Text segundo = new Text(240, 150, rm.howtoplayFont, rm.activity.getString(R.string.howtoplay2), new TextOptions(HorizontalAlign.LEFT), vbom);
		segundo.setAutoWrap(AutoWrap.WORDS);
		segundo.setAutoWrapWidth(430);
		segundo.setAnchorCenter(0, 0);
		segundo.setPosition(-segundo.getWidth()/2, 150);
		menuChildScene.attachChild(segundo);

		Text segundo2 = new Text(240, 120, rm.howtoplayFont, rm.activity.getString(R.string.howtoplay3), new TextOptions(HorizontalAlign.LEFT), vbom);
		segundo2.setAnchorCenter(0, 0);
		segundo2.setPosition(-segundo2.getWidth()/2, 120);
		menuChildScene.attachChild(segundo2);

		Text terceiro = new Text(240, -270, rm.howtoplayFont, rm.activity.getString(R.string.howtoplay4), new TextOptions(HorizontalAlign.LEFT), vbom);
		terceiro.setAutoWrap(AutoWrap.WORDS);
		terceiro.setAutoWrapWidth(430);
		terceiro.setAnchorCenter(0, 0);
		terceiro.setPosition(-terceiro .getWidth()/2, -270);
		menuChildScene.attachChild(terceiro);

		Text quarto = new Text(240, -610, rm.howtoplayFont, rm.activity.getString(R.string.howtoplay5), new TextOptions(HorizontalAlign.LEFT), vbom);
		quarto.setAutoWrap(AutoWrap.WORDS);
		quarto.setAutoWrapWidth(430);
		quarto.setAnchorCenter(0, 0);
		quarto.setPosition(-quarto .getWidth()/2, -610);
		menuChildScene.attachChild(quarto);

		Text quinto = new Text(240, -770, rm.howtoplayFont, rm.activity.getString(R.string.howtoplay6), new TextOptions(HorizontalAlign.LEFT), vbom);
		quinto.setAutoWrap(AutoWrap.WORDS);
		quinto.setAutoWrapWidth(430);
		quinto.setAnchorCenter(0, 0);
		quinto.setPosition(-quinto .getWidth()/2, -770);
		menuChildScene.attachChild(quinto);
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

		Text texto = new Text(0, 345, rm.titleFont, rm.activity.getString(R.string.howtoplay), new TextOptions(HorizontalAlign.CENTER), vbom);

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
		return true;
	}
}
