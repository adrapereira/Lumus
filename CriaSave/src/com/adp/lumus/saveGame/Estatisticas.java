package com.adp.lumus.saveGame;

import java.io.Serializable;

public class Estatisticas implements Serializable{
	private int pacotesCompletos;
	private int niveisCompletos;
	private int lampadasColocadas;
	private int lampadasRetiradas;
	private int marcasColocadas;
	private int resets;
	private int solucoesVistas;
	private int undos;
	private int inicializacoes;
	public static final long serialVersionUID = 97461165206246L;

	
	public Estatisticas(){
		pacotesCompletos = 0;
		niveisCompletos = 0;
		lampadasColocadas = 0;
		lampadasRetiradas = 0;
		marcasColocadas = 0;
		resets = 0;
		solucoesVistas = 0;
		undos = 0;
		inicializacoes = 0;
	}
	
	public Estatisticas(Estatisticas e){
		pacotesCompletos = e.getPacotesCompletos();
		niveisCompletos = e.getNiveisCompletos();
		lampadasColocadas = e.getLampadasColocadas();
		lampadasRetiradas = e.getLampadasRetiradas();
		marcasColocadas = e.marcasColocadas;
		resets = e.getResets();
		solucoesVistas = e.getSolucoesVistas();
		undos = e.getUndos();
		inicializacoes = e.getInicializacoes();
	}
	
	// ADD
	public void addPacotesCompletos(){
		pacotesCompletos++;
	}
	public void addNiveisCompletos(){
		niveisCompletos++;
	}
	public void addLampadasColocadas(){
		lampadasColocadas++;
	}
	public void addLampadasRetiradas(){
		lampadasRetiradas++;
	}
	public void addmarcasColocadas(){
		marcasColocadas++;
	}
	public void addResets(){
		resets++;
	}
	public void addSolucoesVistas(){
		solucoesVistas++;
	}
	public void addUndos(){
		undos++;
	}
	public void addInicializacoes(){
		inicializacoes++;
	}

	// GETTERS
	public int getPacotesCompletos() {
		return pacotesCompletos;
	}

	public int getNiveisCompletos() {
		return niveisCompletos;
	}

	public int getLampadasColocadas() {
		return lampadasColocadas;
	}

	public int getLampadasRetiradas() {
		return lampadasRetiradas;
	}

	public int getMarcasColocadas() {
		return marcasColocadas;
	}

	public int getResets() {
		return resets;
	}

	public int getSolucoesVistas() {
		return solucoesVistas;
	}

	public int getUndos() {
		return undos;
	}

	public int getInicializacoes() {
		return inicializacoes;
	}
	
	// OUTROS
	
	public Estatisticas clone(){
		return new Estatisticas(this);
	}
}
