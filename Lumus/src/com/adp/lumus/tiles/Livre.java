package com.adp.lumus.tiles;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;

import com.adp.lumus.general.ResourcesManager;

public class Livre extends Tile {	
	
	public Livre(int x, int y, ResourcesManager resources, OnClickListener onC){
		//super(x, y, "gfx/tiles/", "livre.png", resources, onC);
		super(x, y, resources);
		image = new ButtonSprite(x, y, resources.livre_region, resources.livre_region, resources.livre_region, resources.engine.getVertexBufferObjectManager(), onC);
		image.setHeight(Tile.SIZE);
		image.setWidth(Tile.SIZE);
	}
	
}
