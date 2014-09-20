package com.adp.lumus.tiles;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;

import com.adp.lumus.general.ResourcesManager;

public class Tres extends Tile {

	public Tres(int x, int y, boolean completo, ResourcesManager resources, OnClickListener onC){
		super(x, y, resources);
		if(completo)
			image = new ButtonSprite(x, y, resources.tres_region, resources.tres_region, resources.tres_region, resources.engine.getVertexBufferObjectManager(), onC);
		else image = new ButtonSprite(x, y, resources.tresGreen_region, resources.tresGreen_region, resources.tresGreen_region, resources.engine.getVertexBufferObjectManager(), onC);
		image.setHeight(Tile.SIZE);
		image.setWidth(Tile.SIZE);
	}
	
}