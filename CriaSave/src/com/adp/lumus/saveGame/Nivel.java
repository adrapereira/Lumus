package com.adp.lumus.saveGame;

import java.io.Serializable;
import java.util.Stack;

public class Nivel implements Serializable{
	private final String nome;
	private final int[][] matriz;
	private final int[][] solucao;
	private boolean resolvido;
	private Stack<Jogada> stack;
	public static final long serialVersionUID = 64367434546L;
	
	public Nivel(String nome, int[][] m, int[][] s){
		this.nome = nome;
		matriz = m;
		solucao = s;
		resolvido = false;
		stack = new Stack<Jogada>();
	}
	
	private Nivel(Nivel n){
		nome = n.getNome();
		matriz = n.getMatriz();
		solucao = n.getSolucao();
		resolvido  = n.isResolvido();
		stack = n.getStack();
	}
	
	public String getNome() {return nome;}
	public int getCoord(int x, int y){ return matriz[x][y];}
	public int[][] getMatriz(){ return matriz;}	
	public int[][] getSolucao(){ return solucao;}
	public boolean isResolvido(){return resolvido;}
	public void setResolvido(boolean valor){resolvido = valor;}
	public void setMatriz(int[][] m){
		for(int i = 0; i < 7; i++)
			for(int j = 0; j < 9; j++)
				matriz[i][j] = m[i][j];
	}
	public void setStack(Stack<Jogada> s){
		stack = (Stack<Jogada>)s.clone();
	}
	public Stack<Jogada> getStack(){ return (Stack<Jogada>)stack.clone();}
	
	public Jogada fazPop(){
		return stack.pop();
	}
	
	public void addToStack(Jogada j){
		stack.push(j);
	}
	
	@Override
	public Nivel clone(){
		return new Nivel(this);
	}
	
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < 7; i++){
			for(int j = 0; j < 9; j++)
				sb.append(matriz[i][j] + " ");
			sb.append("\n");
		}
		sb.append("------Solucao-------\n");
		for(int i = 0; i < 7; i++){
			for(int j = 0; j < 9; j++)
				sb.append(solucao[i][j] + " ");
			sb.append("\n");
		}
		return sb.toString();
	}
}

