package com.adp.lumus.saveGame;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Pacote implements Serializable{
	private final int numero;
	private final Map<Integer, Nivel> niveis;
	public static final long serialVersionUID = 643226516L;
	
	public Pacote(int num){
		numero = num;
		niveis = new HashMap<Integer, Nivel>();
	}
	
	private Pacote(Pacote p){
		numero = p.getNumero();
		niveis = p.getNiveis();
	}
	
	public int getNumero(){return numero;}
	
	public Nivel getNivel(int i){ return niveis.get(i);}
	
	public void addNivel(int n, Nivel niv){
		if (niveis.size() <= 30){
			niveis.put(n, niv);
		} else System.out.println("Pacote cheio - nivel: " + n);
	}
	
	public boolean isCompleto(){
		boolean res = true;
		
		for (Nivel n: niveis.values()){
			if(!n.isResolvido()) res = false;
		}
		return res;
	}
	
	public void putNivel(int i, Nivel n, boolean b){
		if(niveis.containsKey(i)){
			niveis.put(i, n);
		}
	}

	public void putNivelNoCheck(int i, Nivel n){niveis.put(i, n.clone());}
	
	
	public int sizePacote(){return niveis.size();}
	
	
	
	public Map<Integer, Nivel> getNiveis(){
		HashMap<Integer, Nivel> res = new HashMap<Integer, Nivel>();
		for(int i: niveis.keySet()){
			res.put(i, niveis.get(i).clone());
		}
		return res;
	}
	
	@Override
	public Pacote clone(){
		return new Pacote(this);
	}
	
}
