package com.adp.lumus.saveGame;
import java.io.Serializable;

public class Jogada implements Serializable{
	private int x, y;
	private int tileAntigo;
	public static final long serialVersionUID = 9711651235346L;

	
	public Jogada(int x, int y, int tAnt){
		this.x = x;
		this.y = y;
		this.tileAntigo = tAnt;
	}

	public int getX() { return x;}
	public void setX(int x) { this.x = x;}
	public int getY() { return y;}
	public void setY(int y) { this.y = y;}
	public int getTileAntigo() { return tileAntigo;}
	public void setTileAntigo(int tileAntigo) { this.tileAntigo = tileAntigo;}
}