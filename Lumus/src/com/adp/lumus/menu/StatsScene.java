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

import com.adp.lumus.R;
import com.adp.lumus.general.BaseScene;
import com.adp.lumus.general.SceneManager;
import com.adp.lumus.general.SceneManager.SceneType;

public class StatsScene extends BaseScene implements IOnMenuItemClickListener, IScrollDetectorListener, IOnSceneTouchListener{
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------
	//Scrolling
	private final float FREQ_D = 120.0f;
	private final int STATE_WAIT = 0;
	private final int STATE_SCROLLING = 1;
	private final int STATE_MOMENTUM = 2;
	private final int STATE_DISABLE = 3;
	private final int MAXSCROLL = 400;
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
	private final int DESVIOX = 200;
	private final Scene thisScene = this;


	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------

	@Override
	public void createScene() {
		mScrollDetector = new SurfaceScrollDetector(2, this);
		setOnSceneTouchListener(this);
		
		contarEstatisticas();

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
		return SceneType.SCENE_STATS;
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
		
		menuChildScene.setBackgroundEnabled(false);

		menuChildScene.setOnMenuItemClickListener(this);
		createTextos();
		setChildScene(menuChildScene);

		mCurrentY = menuChildScene.getY();
	}

	public void createTextos(){
		colocaTexto(rm.activity.getString(R.string.niveisCompletos), rm.save.getEstatisticas().getNiveisCompletos() + " / 300" , 240);
		colocaTexto(rm.activity.getString(R.string.pacotesCompletos), rm.save.getEstatisticas().getPacotesCompletos() + " / 10", 180);
		colocaTexto(rm.activity.getString(R.string.lampadasColocadas), "" + rm.save.getEstatisticas().getLampadasColocadas(),120);
		colocaTexto(rm.activity.getString(R.string.lampadasRemovidas), "" + rm.save.getEstatisticas().getLampadasRetiradas(), 60);
		colocaTexto(rm.activity.getString(R.string.marcasColocadas), "" + rm.save.getEstatisticas().getMarcasColocadas(), 0);
		colocaTexto(rm.activity.getString(R.string.resets), "" + rm.save.getEstatisticas().getResets(), -60);
		colocaTexto(rm.activity.getString(R.string.solucoes), "" + rm.save.getEstatisticas().getSolucoesVistas(), -120);
		colocaTexto(rm.activity.getString(R.string.undos), "" + rm.save.getEstatisticas().getUndos(), -180);
		colocaTexto(rm.activity.getString(R.string.inicializacoes), "" + rm.save.getEstatisticas().getInicializacoes(), -240);
	}
	
	private void colocaTexto(String s, String valor, int y){
		Text texto = new Text(240, y, rm.statsFont, s, new TextOptions(HorizontalAlign.LEFT), vbom);
		texto.setPosition(-DESVIOX + (texto.getWidth()/2), y);
		menuChildScene.attachChild(texto);
		
		Text texto2 = new Text(240, y, rm.statsFont, valor, new TextOptions(HorizontalAlign.LEFT), vbom);
		texto2.setPosition(DESVIOX - (texto2.getWidth()/2), y);
		menuChildScene.attachChild(texto2);
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

		Text texto = new Text(0, 345, rm.titleFont, rm.activity.getString(R.string.stats), new TextOptions(HorizontalAlign.CENTER), vbom);

		banner.setPosition(0, 345);
		menuChildScene.attachChild(banner);
		menuChildScene.attachChild(texto);
	}
	
	private void contarEstatisticas(){
		int niveis = 0, niveisTotais = 0, pacotes = 0;
		for(int i = 1; i <= 10; i++){
			niveis = 0;
			for(int j = 1; j <= 30; j++){
				if(rm.save.getNivel(i, j).isResolvido()){
					niveis++;
					niveisTotais++;
				}
			}
			if(niveis == 30) pacotes++;
			
		}
		rm.save.getEstatisticas().setNiveisCompletos(niveisTotais);	
		rm.save.getEstatisticas().setPacotesCompletos(pacotes);	
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
