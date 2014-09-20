package com.adp.lumus.tiles;

import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.ButtonSprite.OnClickListener;

import com.adp.lumus.general.ResourcesManager;

public class Dois extends Tile {

	public Dois(int x, int y, boolean completo, ResourcesManager resources, OnClickListener onC){
		super(x, y, resources);
		if(completo)
			image = new ButtonSprite(x, y, resources.dois_region, resources.dois_region, resources.dois_region, resources.engine.getVertexBufferObjectManager(), onC);
		else image = new ButtonSprite(x, y, resources.doisGreen_region, resources.doisGreen_region, resources.doisGreen_region, resources.engine.getVertexBufferObjectManager(), onC);
		image.setHeight(Tile.SIZE);
		image.setWidth(Tile.SIZE);
	}
	
}
