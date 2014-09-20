package com.adp.lumus.general;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.andengine.audio.music.Music;
import org.andengine.audio.music.MusicFactory;
import org.andengine.audio.sound.Sound;
import org.andengine.audio.sound.SoundFactory;
import org.andengine.engine.Engine;
import org.andengine.engine.camera.Camera;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import com.adp.lumus.activity.GameActivity;
import com.adp.lumus.saveGame.Nivel;
import com.adp.lumus.saveGame.Save;

public class ResourcesManager{
	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------

	private static final ResourcesManager INSTANCE = new ResourcesManager();

	public Engine engine;
	public GameActivity activity;
	public Camera camera;
	public VertexBufferObjectManager vbom;
	public Font levelCompleteFont, nextLevelFont, topBannerFont; //game fonts
	public Font titleFont, descriptionFont, completedFont, levelFont, pacoteFont, menuInicialFont, loadingFont; //menu fonts
	public Font howtoplayFont, statsFont; //menu fonts
	public int nivelSeleccionado;
	public int pacoteSeleccionado;
	public Save save;
	public SharedPreferences preferences;
	public CompraPacote cp;

	public Sound complete;
	public Music music;
	
	//---------------------------------------------
	// TEXTURES & TEXTURE REGIONS
	//---------------------------------------------

	public ITextureRegion splash_region;
	public ITextureRegion background_region;
	public ITextureRegion game_table_region;
	public ITextureRegion pacote_region,topBannerPacotes_region;
	public ITextureRegion lamp_region, lampErrada_region;
	public ITextureRegion livre_region, iluminada_region, marca_region;
	public ITextureRegion zero_region, um_region, dois_region, tres_region,quatro_region;
	public ITextureRegion zeroGreen_region, umGreen_region, doisGreen_region, tresGreen_region,quatroGreen_region;
	public ITextureRegion parede_region;
	public ITextureRegion topBanner_region;
	public ITextureRegion nivelCompleto_region, nivelIncompleto_region;
	public ITextureRegion nextLevel, bkgNextLevel;
	public ITextureRegion bannerMenu_region;
	public ITextureRegion lumusBanner_region;
	public ITextureRegion options_region;
	public ITextureRegion iconReset_region, iconUndo_region, iconSolucao_region;
	public ITextureRegion direcoes_region, marca1_region, marca2_region, numerosCerto_region, numerosErrado_region;

	private BuildableBitmapTextureAtlas menuTextureAtlas;
	private BuildableBitmapTextureAtlas gameTextureAtlas;
	private BuildableBitmapTextureAtlas gameTilesTextureAtlas;
	private BitmapTextureAtlas splashTextureAtlas;
	private BitmapTextureAtlas backgTextureAtlas;

	//---------------------------------------------
	// CLASS LOGIC
	//---------------------------------------------

	private void loadPreferences(){
		preferences = activity.getPreferences(Context.MODE_PRIVATE);

		if(!preferences.contains("existe")){ //se ainda nao existir o objecto, inicializa-lo
			preferences.edit().putString("existe", "existe").commit();
			preferences.edit().putBoolean("FREE_1", true).commit(); 
			preferences.edit().putBoolean("FREE_2", true).commit(); 
			preferences.edit().putBoolean("FREE_3", true).commit(); 
			preferences.edit().putBoolean("FREE_4", true).commit(); 
			preferences.edit().putBoolean("FREE_5", true).commit(); 
			preferences.edit().putBoolean("UNLOCKABLE_1", false).commit(); 
			preferences.edit().putBoolean("UNLOCKABLE_2", false).commit(); 
			preferences.edit().putBoolean("PAID_1", false).commit(); 
			preferences.edit().putBoolean("PAID_2", false).commit(); 
			preferences.edit().putBoolean("PAID_3", false).commit();
		}
		
		if(!preferences.contains("sound"))
			preferences.edit().putBoolean("sound", true).commit(); 
	}


	// MENU

	public void loadMenuResources(){
		loadPreferences();
		loadBackground();
		loadMenuGraphics();
		loadGameResources();
		loadMenuFonts();
		loadSounds();
		loadSavedGame();
		cp = new CompraPacote(this);
		actualizarPaid();
		save.getEstatisticas().addInicializacoes();
	}

	private void loadMenuGraphics(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		menuTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1512, 1512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		nivelCompleto_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "nivelCompleto.png");
		nivelIncompleto_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "nivelIncompleto.png");
		pacote_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "pacote.png");
		topBannerPacotes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "topPacotes.png");
		bannerMenu_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "bannerMenu.png");
		lumusBanner_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "lumusTop.png");
		options_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "bkg_options.png");
		direcoes_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "4direcoes.png");
		marca1_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "marca1.png");
		marca2_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "marca2.png");
		numerosCerto_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "numerosCerto.png");
		numerosErrado_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(menuTextureAtlas, activity, "numerosErrado1.png");


		try 
		{
			this.menuTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.menuTextureAtlas.load();
		} 
		catch (final TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
	}

	private void loadMenuFonts(){
		FontFactory.setAssetBasePath("font/");
		final ITexture titleFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture descriptionFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture pacoteFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture completedFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture levelFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture menuFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture loadingFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture howtoplayFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		final ITexture statsFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);

		descriptionFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), descriptionFontTexture, activity.getAssets(), "Roboto-Light.ttf", 18, true, android.graphics.Color.GRAY, 1, android.graphics.Color.GRAY);
		descriptionFont.load();
		titleFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), titleFontTexture, activity.getAssets(), "Roboto-Regular.ttf", 36, true, android.graphics.Color.DKGRAY, 0f, android.graphics.Color.DKGRAY);
		titleFont.load();
		pacoteFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), pacoteFontTexture, activity.getAssets(), "Roboto-Light.ttf", 26, true, android.graphics.Color.BLACK, (float)0.7, android.graphics.Color.BLACK);
		pacoteFont.load();
		levelFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), levelFontTexture, activity.getAssets(), "Roboto-Light.ttf", 28, true, android.graphics.Color.DKGRAY, (float)0.4, android.graphics.Color.BLACK);
		levelFont.load();
		completedFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), completedFontTexture, activity.getAssets(), "Roboto-Light.ttf", 32, true, android.graphics.Color.DKGRAY, (float)0.4, android.graphics.Color.BLACK);
		completedFont.load();
		menuInicialFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), menuFontTexture, activity.getAssets(), "Roboto-Light.ttf", 30, true, android.graphics.Color.BLACK, (float)0.4, android.graphics.Color.BLACK);
		menuInicialFont.load();
		loadingFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), loadingFontTexture, activity.getAssets(), "Roboto-Light.ttf", 30, true, android.graphics.Color.GRAY, 1, android.graphics.Color.WHITE);
		loadingFont.load();
		howtoplayFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), howtoplayFontTexture, activity.getAssets(), "Roboto-Regular.ttf", 26, true, android.graphics.Color.BLACK, 0.5f, android.graphics.Color.BLACK);
		howtoplayFont.load();
		statsFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), statsFontTexture, activity.getAssets(), "Roboto-Regular.ttf", 30, true, android.graphics.Color.BLACK, 0.2f, android.graphics.Color.BLACK);
		statsFont.load();
	}

	public void unloadMenuTextures(){ menuTextureAtlas.unload();}
	public void loadMenuTextures(){ menuTextureAtlas.load();}

	// SOUNDS
	
	private void loadSounds(){
		loadJingle();
		loadMusic();
	}

	private void loadJingle(){
		SoundFactory.setAssetBasePath("mfx/");

		try {
			this.complete = SoundFactory.createSoundFromAsset(this.engine.getSoundManager(), activity, "completo.mp3");
			this.complete.setVolume(0.6f);
		} catch (final IOException e) {
			Debug.e(e);
		}
	}
	
	private void loadMusic(){
		MusicFactory.setAssetBasePath("mfx/");
		try {
			this.music = MusicFactory.createMusicFromAsset(this.engine.getMusicManager(), activity, "music.mp3");
			this.music.setVolume(1);
			this.music.setLooping(true);
		} catch (final IOException e) {
			Debug.e(e);
		}
	}
	
	// GAME

	public void loadGameResources(){
		loadGameGraphics();
		loadGameFonts();
	}

	private void loadGameFonts(){
		FontFactory.setAssetBasePath("font/");
		final ITexture levelCompleteFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		final ITexture nextLevelFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		final ITexture topBannerFontTexture = new BitmapTextureAtlas(activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);

		levelCompleteFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), levelCompleteFontTexture, activity.getAssets(), "Roboto-Regular.ttf", 34, true, android.graphics.Color.DKGRAY, 0.6f, android.graphics.Color.BLACK);
		levelCompleteFont.load();
		nextLevelFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), nextLevelFontTexture, activity.getAssets(), "Roboto-Regular.ttf", 25, true, android.graphics.Color.DKGRAY, 0f, android.graphics.Color.DKGRAY);
		nextLevelFont.load();
		topBannerFont = FontFactory.createStrokeFromAsset(activity.getFontManager(), topBannerFontTexture, activity.getAssets(), "Roboto-Light.ttf", 38, true, android.graphics.Color.DKGRAY, 1, android.graphics.Color.DKGRAY);
		topBannerFont.load();
	}

	private void loadGameGraphics(){
		//load Background etc
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/game/");
		gameTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		game_table_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "tableHUD.png");
		topBanner_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "topBanner.png");
		nextLevel = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "bt_nextlevel.png");
		bkgNextLevel = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "bkg_nextLevel.png");
		iconReset_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "iconReset.png");
		iconUndo_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "iconUndo.png");
		iconSolucao_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTextureAtlas, activity, "iconSolucao.png");

		try 
		{
			this.gameTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTextureAtlas.load();
		} 
		catch (final TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
		loadGameTiles();
	}

	private void loadGameTiles(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/tiles/");
		gameTilesTextureAtlas = new BuildableBitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		lamp_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "lamp.png");
		livre_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "livre.png");
		iluminada_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "iluminada.png");
		zero_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "zero.png");
		um_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "um.png");
		dois_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "dois.png");
		tres_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "tres.png");
		quatro_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "quatro.png");
		lampErrada_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "lampErrada.png");
		parede_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "parede.png");
		marca_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "marca.png");
		zeroGreen_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "0green.png");
		umGreen_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "1green.png");
		doisGreen_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "2green.png");
		tresGreen_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "3green.png");
		quatroGreen_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(gameTilesTextureAtlas, activity, "4green.png");

		try 
		{
			this.gameTilesTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 1, 0));
			this.gameTilesTextureAtlas.load();
		} 
		catch (final TextureAtlasBuilderException e)
		{
			Debug.e(e);
		}
	}

	public void unloadGameTextures(){
		gameTextureAtlas.unload();
		gameTilesTextureAtlas.unload();
	}


	// SAVE

	private void loadSavedGame(){
		loadSavedGameMemInterna();

		ObjectInputStream stream = null;
		AssetManager am = activity.getAssets();
		try {
			InputStream is = am.open("save.lms");
			stream = new ObjectInputStream(is);
			Save save2 = (Save) stream.readObject();
			if(save != null){
				Log.d("Abrir", "aberto ficheiro da mem interna");
				if(save2.getVersao() > save.getVersao()){
					Log.d("Abrir", "Juntar niveis completos a Save novo");
					for(int i = 1; i <= 10; i++){
						for(int j = 1; j <= 30; j++){
							Nivel n = save.getNivel(i, j);
							if(n.isResolvido()){
								Nivel n2 = save2.getNivel(i, j);
								n2.setResolvido(true);
								n2.setMatriz(n.getMatriz());
								if(save.getVersao() > 20130820) //so a partir desta versao é que começou a existir stack
									n2.setStack(n.getStack());
							}
						}
					}
					if(save.getVersao() > 20130820) //so a partir desta versao é que começou a existir estatisticas
						save2.setEstatisticas(save.getEstatisticas());
					save = save2;
				}
			}else{ 
				Log.d("Estado Save", "Save Novo");
				save = save2;
			}
		} catch (FileNotFoundException e) {
			Debug.e(e);
		} catch (IOException e) {
			Debug.e(e);
		} catch (ClassNotFoundException e) {
			Debug.e(e);
		}	
	}

	private void loadSavedGameMemInterna(){
		ObjectInputStream stream = null;
		try {
			FileInputStream fos = activity.openFileInput("save.lms");
			stream = new ObjectInputStream(fos);
			save = (Save) stream.readObject();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void saveGame(){		
		ObjectOutputStream stream = null;
		try {
			FileOutputStream fos = activity.openFileOutput("save.lms", Context.MODE_PRIVATE);
			stream = new ObjectOutputStream(fos);
			stream.writeObject(save);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.flush();
					stream.close();
					Log.d("Gravacao", "Save guardado em memoria interna!");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public void resetSave(){
		ObjectInputStream stream = null;
		AssetManager am = activity.getAssets();
		try {
			InputStream is = am.open("save.lms");
			stream = new ObjectInputStream(is);
			save = (Save) stream.readObject();
		} catch (FileNotFoundException e) {
			Debug.e(e);
		} catch (IOException e) {
			Debug.e(e);
		} catch (ClassNotFoundException e) {
			Debug.e(e);
		}	

		saveGame();
	}

	// SPLASH SCREEN

	public void loadSplashScreen(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		splashTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		splash_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(splashTextureAtlas, activity, "splash.png", 0, 0);
		splashTextureAtlas.load();
	}

	public void unloadSplashScreen() {
		splashTextureAtlas.unload();
		splash_region = null;
	}

	// OUTROS

	public static void prepareManager(Engine engine, GameActivity activity, Camera camera, VertexBufferObjectManager vbom){
		getInstance().engine = engine;
		getInstance().activity = activity;
		getInstance().camera = camera;
		getInstance().vbom = vbom;
	}

	public void actualizarPaid(){
		preferences.edit().putBoolean("PAID_1", cp.isHasPack1()).commit(); 
		preferences.edit().putBoolean("PAID_2", cp.isHasPack2()).commit(); 
		preferences.edit().putBoolean("PAID_3", cp.isHasPack3()).commit();
	}

	public static ResourcesManager getInstance(){
		return INSTANCE;
	}
	
	private void loadBackground(){
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		backgTextureAtlas = new BitmapTextureAtlas(activity.getTextureManager(), 1024, 1024, TextureOptions.BILINEAR);
		background_region = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgTextureAtlas, activity, "background.png", 0, 0);
		backgTextureAtlas.load();
	}
}
