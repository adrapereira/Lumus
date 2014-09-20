package com.adp.lumus.tiles;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;

import com.adp.lumus.general.ResourcesManager;

public class Iluminada extends Tile {

	public Iluminada(int x, int y, ResourcesManager resources, OnClickListener onC){
		//super(x, y, "gfx/tiles/", "iluminada.png", resources, onC);
		super(x, y, resources);
		image = new ButtonSprite(x, y, resources.iluminada_region, resources.iluminada_region, resources.iluminada_region, resources.engine.getVertexBufferObjectManager(), onC);
		image.setHeight(Tile.SIZE);
		image.setWidth(Tile.SIZE);
	}
	
}
