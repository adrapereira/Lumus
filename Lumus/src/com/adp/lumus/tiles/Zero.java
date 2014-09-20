package com.adp.lumus.tiles;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;

import com.adp.lumus.general.ResourcesManager;

public class Zero extends Tile {

	public Zero(int x, int y, ResourcesManager resources, OnClickListener onC){
		super(x, y, resources);
		image = new ButtonSprite(x, y, resources.zeroGreen_region, resources.zeroGreen_region, resources.zeroGreen_region, resources.engine.getVertexBufferObjectManager(), onC);
		image.setHeight(Tile.SIZE);
		image.setWidth(Tile.SIZE);
	}
}
