package com.adp.lumus.saveGame;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Save implements Serializable{
	private int versao;
	private final Map<Integer, Pacote> pacotes;
	private Estatisticas est;
	public static final long serialVersionUID = 643252346L;
	
	public Save(int versao, boolean over){
		this.versao = versao;
		pacotes = new HashMap<Integer, Pacote>();
		est = new Estatisticas();
	}

	public Nivel getNivel(int p, int n){ 
			return pacotes.get(p).getNivel(n);
	}
	public int getVersao(){ return this.versao;}
	public void setVersao(int v){ this.versao = v;}
	
	public void putPacote(int p, Pacote pac){
		if(!pacotes.containsKey(p)){
			pacotes.put(p, pac.clone());
		}
	}

	public void putPacoteNoCheck(int p, Pacote pac){
		pacotes.put(p, pac.clone());
	}
	
	public Map<Integer, Pacote> getPacotes(){
		HashMap<Integer, Pacote> res = new HashMap<Integer, Pacote>();
		for(int i: pacotes.keySet()){
			res.put(i, pacotes.get(i).clone());
		}
		return res;
	}
	
	public int size(){return pacotes.size();}
	
	public void mergeSave(Save s){
		Map<Integer, Pacote> pacotesAux = s.getPacotes(); // pacotes do novo save
		for(int i: pacotesAux.keySet()){ 
			if(!pacotes.containsKey(i)){ //se nao existir no save antigo algum pacote
				pacotes.put(i, pacotesAux.get(i).clone()); // adicionar
			}
		}
	}
	
	public Estatisticas getEstatisticas(){
		return est;
	}
	public void setEstatisticas(Estatisticas e){
		est = e.clone();
	}
}
