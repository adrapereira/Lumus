package com.adp.lumus.tiles;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;

import com.adp.lumus.general.ResourcesManager;

public abstract class Tile{
	protected static final int SIZE = 66;
	protected ButtonSprite image;
	protected int x;
	protected int y;
	protected ResourcesManager resources;
	
	public Tile(int x, int y, ResourcesManager resources){
		this.x = x;
		this.y = y;
		image = null;
		this.resources = resources;
	}
	
	public Tile(String pasta, String nome, ResourcesManager resources, OnClickListener onC){
		BitmapTextureAtlas texture;
		ITextureRegion textureRegion;
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath(pasta);
		texture = new BitmapTextureAtlas(resources.activity.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, resources.activity, nome, 0, 0);
		texture.load();
		
		image = new ButtonSprite(0, 0, textureRegion, resources.engine.getVertexBufferObjectManager(), onC);
		image.setHeight(57);
		image.setWidth(57);
	}
	
	public Tile(int x, int y, String pasta, String nome, ResourcesManager resources, OnClickListener onC){
		this(pasta, nome, resources, onC);
		image.setX(x);
		image.setY(y);
		this.x = x;
		this.y = y;
		this.resources = resources;
	}
	
	public Sprite getSprite(){
		return this.image;
	}
}
