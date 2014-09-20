package com.adp.lumus.game;

import java.util.ArrayList;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.camera.hud.HUD;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.util.GLState;
import org.andengine.util.adt.align.HorizontalAlign;
import org.andengine.util.adt.color.Color;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.Toast;

import com.adp.lumus.R;
import com.adp.lumus.general.BaseScene;
import com.adp.lumus.general.SceneManager;
import com.adp.lumus.general.SceneManager.SceneType;
import com.adp.lumus.saveGame.Jogada;
import com.adp.lumus.saveGame.Nivel;
import com.adp.lumus.tiles.Dois;
import com.adp.lumus.tiles.Iluminada;
import com.adp.lumus.tiles.Lamp;
import com.adp.lumus.tiles.LampErrada;
import com.adp.lumus.tiles.Livre;
import com.adp.lumus.tiles.Marca;
import com.adp.lumus.tiles.Parede;
import com.adp.lumus.tiles.Quatro;
import com.adp.lumus.tiles.Tile;
import com.adp.lumus.tiles.Tres;
import com.adp.lumus.tiles.Um;
import com.adp.lumus.tiles.Zero;

public class GameScene extends BaseScene implements IOnMenuItemClickListener{

	private static final int XMULTIPLIER = 66;
	private static final int YMULTIPLIER = 66;
	private static final int BEGINTABLEX = 42;
	private final int BEGINTABLEY = 75 + YMULTIPLIER;
	private final int NEXTLEVEL = 1;
	private final int RESET = 2;
	private final int MENU = 3;

	private final int TEMPOMARCA = 300000000; // 300 ms

	private BaseScene thisScene;
	private HUD gameHUD;
	private GameLogic game;
	private Text nivelText;
	private Text nextLevelText, resetText, menuText, levelCompleteText;
	private ButtonSprite[][] sprites;
	private Rectangle backg;
	private Sprite contentor, table;
	private IMenuItem btNext, btReset, btMenu;

	private MenuScene menuChildScene;

	private long timeofclick;
	private int ultClickX, ultClickY;
	private boolean clickable, solution;
	private ArrayList<Sprite> spritesAEliminar;
	private int completos;


	@Override
	public void createScene() {	
		createBackground();

		sprites = new ButtonSprite[GameLogic.HEIGHT][GameLogic.WIDTH];
		spritesAEliminar = new ArrayList<Sprite>();

		Nivel nivel = rm.save.getNivel(rm.pacoteSeleccionado, rm.nivelSeleccionado);
		game = new GameLogic(rm, nivel);

		createTopBanner();
		createHUD();

		updateTable();
		
		createButtons();

		thisScene = this;
		clickable = true;
		solution = false;

		if(game.isResolvido())
			createChangeLevelScene();
	}

	@Override
	public void onBackKeyPressed(){
		if(solution && clickable) {
			solution = false;
			game.restaurarEstado();
			updateTable();
		}
		else{ 
			SceneManager.getInstance().loadNiveisScene(engine, true, rm);
		}
	}

	@Override
	public SceneType getSceneType() {
		return SceneType.SCENE_GAME;
	}

	@Override
	public void disposeScene(){
		camera.setHUD(null);
		camera.setCenter(240, 400);

		rm.activity.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				thisScene.detachChildren();
			}
		});
		
		this.clearTouchAreas();
	}	

	private void createBackground(){
		setBackground(new Background(Color.WHITE));
		Sprite background = new Sprite(0, 0, rm.background_region, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		background.setPosition(240, 400);
		attachChild(background);
	}

	private void createHUD(){
		gameHUD = new HUD();

		//create Table
		int y = BEGINTABLEY + 4 * YMULTIPLIER;
		table = new Sprite(240, y, rm.game_table_region, rm.engine.getVertexBufferObjectManager());
		this.attachChild(table);

		// CREATE SCORE TEXT
		y = BEGINTABLEY + 9 * YMULTIPLIER - 6;
		nivelText = new Text(BEGINTABLEX - 10, y, rm.topBannerFont, "Level / . 0123456789", new TextOptions(HorizontalAlign.LEFT), vbom);
		nivelText.setAnchorCenter(0, 0);    
		nivelText.setText(stringNivelTopBanner());
		this.attachChild(nivelText);

		//Criar sprites para parte de Next Level
		contentor = new Sprite(0, 0, rm.bkgNextLevel, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};
		contentor.setAlpha((float)0.90);
		nextLevelText = new Text(240, 435, rm.nextLevelFont, rm.activity.getString(R.string.nextLevel), new TextOptions(HorizontalAlign.LEFT), vbom);
		resetText = new Text(240, 370, rm.nextLevelFont, rm.activity.getString(R.string.playAgain), new TextOptions(HorizontalAlign.LEFT), vbom);
		menuText = new Text(240, 305, rm.nextLevelFont, rm.activity.getString(R.string.menu), new TextOptions(HorizontalAlign.LEFT), vbom);
		levelCompleteText = new Text(240, 505, rm.levelCompleteFont, rm.activity.getString(R.string.levelComplete), new TextOptions(HorizontalAlign.LEFT), vbom);

		camera.setHUD(gameHUD);
	}

	private String stringNivelTopBanner(){
		StringBuilder sb = new StringBuilder();

		sb.append(rm.activity.getString(R.string.level) + " ");
		sb.append(rm.pacoteSeleccionado);
		sb.append(".");
		sb.append(rm.nivelSeleccionado);

		return sb.toString();
	}

	private void createTopBanner(){
		Sprite banner = new Sprite(0, 0, rm.topBanner_region, vbom){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		int y = BEGINTABLEY + (9 * YMULTIPLIER) + 17;
		banner.setPosition(240, y);
		this.registerTouchArea(banner);
		attachChild(banner);
	}
	
	private void createButtons(){
		ButtonSprite undo = new ButtonSprite(0, 0, rm.iconUndo_region, vbom, clUndo()){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		int y = BEGINTABLEY + (9 * YMULTIPLIER) + 17;
		undo.setPosition(420, y);
		this.registerTouchArea(undo);
		attachChild(undo);
		
		ButtonSprite reset = new ButtonSprite(0, 0, rm.iconReset_region, vbom, clReset()){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		reset.setPosition(350, y);
		this.registerTouchArea(reset);
		attachChild(reset);
		
		ButtonSprite soluc = new ButtonSprite(0, 0, rm.iconSolucao_region, vbom, clSoluc()){
			@Override
			protected void preDraw(GLState pGLState, Camera pCamera){
				super.preDraw(pGLState, pCamera);
				pGLState.enableDither();
			}
		};

		soluc.setPosition(280, y + 4);
		this.registerTouchArea(soluc);
		attachChild(soluc);
	}

	public void updateTable(){
		int i, j;

		game.updateMatriz();
		this.detachChild(table);

		for(i = 0; i < GameLogic.HEIGHT; i++)
			for(j = 0; j < GameLogic.WIDTH; j++)
				if(game.isUpdate(i, j))
					updateTile(i, j);

		this.attachChild(table);
		rm.activity.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				for(Sprite s: spritesAEliminar)
					detachChild(s);
			}
		});

		game.resetModf();
	}

	public void updateTile(int i, int j){
		int [][] matriz = game.getMatriz();

		//eliminar sprite anterior
		spritesAEliminar.add(sprites[i][j]);
		unregisterTouchArea(sprites[i][j]);

		//colocar nova sprite
		int x = (i * XMULTIPLIER) + BEGINTABLEX;
		int y = (j * YMULTIPLIER) + BEGINTABLEY;
		Tile t = null;

		switch(matriz[i][j]){
		case 0:
			t = new Zero(x, y, rm, clNada()); break;
		case 1:
			t = new Um(x, y, game.testaNumCompleto(i, j), rm, clNada()); break;
		case 2:
			t = new Dois(x, y, game.testaNumCompleto(i, j), rm, clNada()); break;
		case 3:
			t = new Tres(x, y, game.testaNumCompleto(i, j), rm, clNada()); break;
		case 4:
			t = new Quatro(x, y, game.testaNumCompleto(i, j), rm, clNada()); break;
		case GameLogic.LIVRE:
			t = new Livre(x, y, rm, clLivre()); break;
		case GameLogic.LAMP:
			t = new Lamp(x, y, rm, clLamp()); break;
		case GameLogic.MARCA:
			t = new Marca(x, y, rm, clLamp()); break;
		case GameLogic.PAREDE:
			t = new Parede(x, y, rm, clNada()); break;
		case GameLogic.ILUMINADA:
			t = new Iluminada(x, y, rm, clNada()); break;
		case GameLogic.LAMPERRADA:
			t = new LampErrada(x, y, rm, clLamp()); break;
		default: return;
		}

		if( t != null){
			this.registerTouchArea(t.getSprite());
			attachChild(t.getSprite());
			sprites[i][j] = (ButtonSprite)t.getSprite();
		}
	}

	private void createChangeLevelScene(){
		clickable = false;

		backg = new Rectangle(240, 400, 480, 800, this.vbom);
		backg.setColor(Color.BLACK);
		backg.setAlpha((float)0.35);
		backg.setPosition(240, 400);
		attachChild(backg);

		contentor.setPosition(240, 400);
		contentor.setAlpha((float)0.90);
		gameHUD.attachChild(contentor);
		gameHUD.attachChild(levelCompleteText);

		createMenuChildScene();
	}

	private void createMenuChildScene(){
		//play sound
		if(rm.preferences.getBoolean("sound", true))
			rm.complete.play();
		
		//create menu		
		menuChildScene = new MenuScene(camera);
		menuChildScene.setPosition(0, 0);

		btNext = new ScaleMenuItemDecorator(new SpriteMenuItem(NEXTLEVEL, rm.nextLevel, vbom), 1.1f, 1);
		menuChildScene.addMenuItem(btNext);
		btNext.setPosition(240, 435);
		menuChildScene.attachChild(nextLevelText);

		btReset = new ScaleMenuItemDecorator(new SpriteMenuItem(RESET, rm.nextLevel, vbom), 1.1f, 1);
		menuChildScene.addMenuItem(btReset);
		btReset.setPosition(240, 370);
		menuChildScene.attachChild(resetText);

		btMenu = new ScaleMenuItemDecorator(new SpriteMenuItem(MENU, rm.nextLevel, vbom), 1.1f, 1);
		menuChildScene.addMenuItem(btMenu);
		btMenu.setPosition(240, 305);
		menuChildScene.attachChild(menuText);

		menuChildScene.setBackgroundEnabled(false);

		menuChildScene.setOnMenuItemClickListener(this);

		gameHUD.setChildScene(menuChildScene);
	}

	private void changeLevel(){
		if(rm.nivelSeleccionado < 30)
			rm.nivelSeleccionado++;
		else {onBackKeyPressed(); return;} //voltar para o menu se acabar os niveis do pacote

		Nivel nivel = rm.save.getNivel(rm.pacoteSeleccionado, rm.nivelSeleccionado);
		game = new GameLogic(rm, nivel);

		updateTable();
		clickable = true;

		if(!game.isResolvido()){ //caso o nivel que esta a ser carregado ja estiver resolvido, nao se tira nada e volta-se a mudar de nivel
			nivelText.setText(stringNivelTopBanner());
			limparHUDChangeLevel();
		}else{
			changeLevel();
		}
	}



	private void limparHUDChangeLevel(){
		if(completos > 9){
			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(activity, rm.activity.getString(R.string.saving), Toast.LENGTH_SHORT).show();
				}
			});
			rm.saveGame(); //Guardar o jogo quando se abrir um novo nivel
			completos = 0;
		}
		rm.activity.runOnUpdateThread(new Runnable() {
			@Override
			public void run() {
				gameHUD.detachChild(contentor);
				gameHUD.detachChild(levelCompleteText);
				menuChildScene.detachChildren();
				menuChildScene.setOnMenuItemClickListener(null);
				gameHUD.clearChildScene();
				detachChild(backg);
			}
		});
	}
	
	private void fazReset(){
		game.reset();
		updateTable();
		clickable = true;
		solution = false;
		rm.save.getEstatisticas().addResets();
	}

	//CLICK LISTENERS
	private OnClickListener clLivre(){
		return new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(clickable){
					int i = ((int)pButtonSprite.getX() - BEGINTABLEX) / XMULTIPLIER;
					int j = ((int)pButtonSprite.getY() - BEGINTABLEY) / YMULTIPLIER;

					if(game.getMatriz()[i][j] == GameLogic.LIVRE){
						Jogada jog = new Jogada(i, j, GameLogic.LIVRE);
						game.addJogada(jog);
						
						game.setTile(i, j, GameLogic.LAMP);
						timeofclick = System.nanoTime();
						ultClickX = i;
						ultClickY = j;
						updateTable();
						
						rm.save.getEstatisticas().addLampadasColocadas();
					}

					solution = false;
					
					int a = game.resolvido();
					if(a == 0) createChangeLevelScene();
				}
			}
		};
	}

	private OnClickListener clLamp(){
		return new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(clickable){
					int i = ((int)pButtonSprite.getX() - BEGINTABLEX) / XMULTIPLIER;
					int j = ((int)pButtonSprite.getY() - BEGINTABLEY) / YMULTIPLIER;

					Jogada jog = new Jogada(i, j, game.getTile(i, j));
					game.addJogada(jog);
					
					if((System.nanoTime() - timeofclick) < TEMPOMARCA && i == ultClickX && j == ultClickY){
						game.setTile(i, j, GameLogic.MARCA);
						rm.save.getEstatisticas().addmarcasColocadas(); //adiciono uma marca e retiro a lampada que la estava
						rm.save.getEstatisticas().subLampadasColocadas();
					}else{ 
						game.setTile(i, j, GameLogic.LIVRE);
						rm.save.getEstatisticas().addLampadasRetiradas();
					}
					
					solution = false;
					
					updateTable();
				}
			}
		};
	}

	private OnClickListener clNada(){
		return new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
			}
		};
	}

	private OnClickListener clSoluc(){
		return new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(clickable)
					rm.activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rm.activity);
							// set title
							alertDialogBuilder.setTitle(rm.activity.getString(R.string.showSolution)); // **
							// set dialog message
							alertDialogBuilder
							.setMessage(rm.activity.getString(R.string.seeSolution)) // **
							.setCancelable(false)
							.setPositiveButton(rm.activity.getString(R.string.yes),new DialogInterface.OnClickListener() { // **
								@Override
								public void onClick(DialogInterface dialog,int id) {
									solution = true;
									game.copySolucao();
									updateTable();
									rm.save.getEstatisticas().addSolucoesVistas();
								}
							})
							.setNegativeButton(rm.activity.getString(R.string.no),new DialogInterface.OnClickListener() { // **
								@Override
								public void onClick(DialogInterface dialog,int id) {
									// if this button is clicked, just close the dialog box and do nothing
									dialog.cancel();
								}
							});
							// create alert dialog
							AlertDialog alertDialog = alertDialogBuilder.create();
							// show it
							alertDialog.show();
						}
					});
			}
		};
	}
	
	private OnClickListener clReset(){
		return new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(clickable)
					rm.activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(rm.activity);
							// set title
							alertDialogBuilder.setTitle(rm.activity.getString(R.string.reset) + "?"); // **
							// set dialog message
							alertDialogBuilder
							.setMessage(rm.activity.getString(R.string.sureReset)) // **
							.setCancelable(false)
							.setPositiveButton(rm.activity.getString(R.string.yes),new DialogInterface.OnClickListener() { // **
								@Override
								public void onClick(DialogInterface dialog,int id) {
									fazReset();
								}
							})
							.setNegativeButton(rm.activity.getString(R.string.no),new DialogInterface.OnClickListener() { // **
								@Override
								public void onClick(DialogInterface dialog,int id) {
									// if this button is clicked, just close the dialog box and do nothing
									dialog.cancel();
								}
							});
							// create alert dialog
							AlertDialog alertDialog = alertDialogBuilder.create();
							// show it
							alertDialog.show();
						}
					});
				
			}
		};
	}

	private OnClickListener clUndo(){
		return new OnClickListener() {
			@Override
			public void onClick(ButtonSprite pButtonSprite, float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if(clickable){
					if(game.fazUndo()){
						updateTable();
						rm.save.getEstatisticas().addUndos();
					}else {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(activity, rm.activity.getString(R.string.outofmoves), Toast.LENGTH_SHORT).show();
							}
						});
					}
				}
			}
		};
	}

	
	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY){
		switch(pMenuItem.getID()){
		case NEXTLEVEL:
			changeLevel();
			completos++;
			solution = false;
			break;
		case RESET:
			fazReset();
			limparHUDChangeLevel();
			break;
		case MENU:
			SceneManager.getInstance().loadNiveisScene(engine, true, rm);
			break;
		}
		return true;
	}
}
