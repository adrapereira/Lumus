package com.adp.lumus.game;

import java.util.EmptyStackException;
import java.util.Stack;

import com.adp.lumus.general.ResourcesManager;
import com.adp.lumus.saveGame.Jogada;
import com.adp.lumus.saveGame.Nivel;

public class GameLogic {
	//CONSTANTES
	public static final int HEIGHT = 7;
	public static final int WIDTH = 9;
	
	public static final int PAREDE = 5;
	public static final int BLOQUEADOR = 6; //numero até ao qual existem tiles que bloqueiam a luz
	public static final int LIVRE = 10;
	public static final int LAMP = 11;
	public static final int MARCA = 12;
	public static final int ILUMINADA = 13;
	public static final int LAMPERRADA = 14;
 
	//VARIAVEIS INSTANCIA
	private int[][] backup;
	private int[][] matriz;
	private boolean[][] modf;
	private Nivel nivel;

	//METODOS
	public GameLogic(ResourcesManager resourcesmanager){ //antigo
		matriz = new int[HEIGHT][WIDTH];
		int i = 0, j = 0;
		for(i = 0; i < HEIGHT; i++)
			for(j = 0; j < WIDTH; j++)
				matriz[i][j] = LIVRE;
	}
	
	public GameLogic(ResourcesManager resourcesmanager, Nivel n){
		this.matriz = n.getMatriz();
		this.nivel = n;
		this.backup = new int[HEIGHT][WIDTH];
		this.modf = new boolean[HEIGHT][WIDTH];
		setModfAs(true); //inicializar a matriz de modificaçoes a true, para que seja imprimida toda a matriz do jogo
	}
	
	public int[][] getMatriz(){return matriz;}
	public int getTile(int i, int j){return matriz[i][j];}
	public void setNivel(Nivel n){
		nivel = n;
		matriz = n.getMatriz();
		setModfAs(true);
	}
	public boolean isResolvido(){return nivel.isResolvido();}
	
	protected void addJogada(Jogada j){
		nivel.addToStack(j);
	}
	
	public void setTile(int i, int j, int tile){
		if(tile == LAMP) 
			if(testeColocarLamp(i, j)){
				matriz[i][j] = tile;
				modf[i][j] = true;
			}
			else{ matriz[i][j] = LAMPERRADA;
				modf[i][j] = true;
			}
		else{
			matriz[i][j] = tile;
			modf[i][j] = true;
		}
		modfNumero(i, j);
	}
	
	protected boolean fazUndo(){
		Jogada j;
		try {
			j = nivel.fazPop();
		} catch (EmptyStackException e) {
			return false;
		}
		matriz[j.getX()][j.getY()] = j.getTileAntigo();
		
		modf[j.getX()][j.getY()] = true;
		modfNumero(j.getX(), j.getY());
		
		return true;
	}
	
	private void modfNumero(int i, int j){
		if(i > 0)
			if(matriz[i-1][j] <= 4) modf[i-1][j] = true;
		if(i < HEIGHT - 1)
			if(matriz[i+1][j] <= 4) modf[i+1][j] = true;
		if(j > 0)
			if(matriz[i][j-1] <= 4) modf[i][j-1] = true;
		if(j < WIDTH - 1)
			if(matriz[i][j+1] <= 4) modf[i][j+1] = true;
	}

	public void updateMatriz(){
		int i = 0, j = 0;
		boolean again = false;

		do{
			again = false;
			for(i = 0; i < HEIGHT; i++){
				for(j = 0; j < WIDTH; j++){
					switch(matriz[i][j]){
					case LAMP:
						acendeTiles(i, j);
						break;
					case ILUMINADA:
						testaLamp(i, j);
						break;
					case LAMPERRADA:
						if(testeColocarLamp(i, j)){
							setTile(i, j, LAMP);
							again = true;
						}
						break;
					}
				}
			}
		}while(again);

		if(resolvido() == 0) nivel.setResolvido(true);
		else nivel.setResolvido(false);
	}

	public void acendeTiles(int x, int y){
		int i = x + 1, j = y;

		for(; i < HEIGHT && matriz[i][j] > BLOQUEADOR; i++)
			if(matriz[i][j] == LIVRE || matriz[i][j] == MARCA)
				setTile(i, j, ILUMINADA);

		i = x-1; j = y;
		for(; i >= 0 && matriz[i][j] > BLOQUEADOR; i--)
			if(matriz[i][j] == LIVRE || matriz[i][j] == MARCA)
				setTile(i, j, ILUMINADA);

		i=x; j=y+1;
		for(; j < WIDTH && matriz[i][j] > BLOQUEADOR; j++)
			if(matriz[i][j] == LIVRE || matriz[i][j] == MARCA)
				setTile(i, j, ILUMINADA);

		i=x; j=y-1;
		for(; j >= 0 && matriz[i][j] > BLOQUEADOR; j--)
			if(matriz[i][j] == LIVRE || matriz[i][j] == MARCA)
				setTile(i, j, ILUMINADA);
	}
	//testar se uma casa iluminada, têm uma lampada que a possa iluminar
	public void testaLamp(int x, int y){
		if(validaLamp(x, y)){
			setTile(x, y, LIVRE);
		}
		//se nao existir lampada em nenhuma ortogonal da casa iluminada, esta passa a Livre
	}

	private boolean testeColocarLamp(int i, int j){
		if(i > 0)
			if(matriz[i-1][j] < 5) 
				if(!testaNumCompleto(i-1, j))
					return false;
		if(i < HEIGHT - 1)
			if(matriz[i+1][j] < 5) 
				if(!testaNumCompleto(i+1, j))
					return false;
		if(j > 0)
			if(matriz[i][j-1] < 5) 
				if(!testaNumCompleto(i, j-1))
					return false;
		if(j < WIDTH - 1)
			if(matriz[i][j+1] < 5) 
				if(!testaNumCompleto(i, j+1))
					return false;
		return true;
	}

	//testa se um tile com um numero ja tem esse numero de lampadas ou mais nas suas ortogonais
	public boolean testaNumCompleto(int i, int j){
		int nLamp = 0;
		if(i > 0)
			if(matriz[i-1][j] == LAMP) nLamp++;
		if(i < HEIGHT - 1)
			if(matriz[i+1][j] == LAMP) nLamp++;
		if(j > 0)
			if(matriz[i][j-1] == LAMP) nLamp++;
		if(j < WIDTH - 1)
			if(matriz[i][j+1] == LAMP) nLamp++;

		if(nLamp >= matriz[i][j]) return false;
		return true;
	}

	public int resolvido(){
		int x, y;
		
		if(validaBoard() == false) return 2;

		for(x=0; x < HEIGHT; x++)
			for(y = 0; y < WIDTH; y++)
				if(matriz[x][y] == LIVRE) //se estiver incompleto
					return 1;
				else if (matriz[x][y] == MARCA)
					return 3;

		return 0;
	}

	private boolean validaBoard(){
		int i, j; 
		boolean valido = true;

		for(i = 0; i < HEIGHT; i++)
			for(j = 0; j < WIDTH; j++){
				switch(matriz[i][j]){
				case LAMP: 
					if(!validaLamp(i, j))
						valido = false;
					break;
				case 1: ;
				case 2: ;
				case 3: ;
				case 4: 
					if(!validaBlock(i, j))
						valido = false;
					break;
				case MARCA: 
					if(!validaMarca(i, i))
						valido = false;
					break;
				case LAMPERRADA: 
					valido = false;
					break;
				default: break;
				}
				if(!valido) return false;
			}
		return true;
	}

	public boolean validaLamp(int x, int y){
		//Metodo que testa se existem duas lampadas na mesma linha/coluna sem nenhum bloqueio entre elas
		// Se existirem, retorna false, ou seja, o tabuleiro está invalido.
		int i = x+1, j = y;
		for(; i < HEIGHT && matriz[i][j] > BLOQUEADOR; i++)
			if(matriz[i][j] == LAMP) return false;

		i = x-1; j = y;
		for(; i >= 0 && matriz[i][j] > BLOQUEADOR; i--)
			if(matriz[i][j] == LAMP) return false;

		i=x; j=y+1;
		for(; j < WIDTH && matriz[i][j] > BLOQUEADOR; j++)
			if(matriz[i][j] == LAMP) return false;

		i=x; j=y-1;
		for(; j >= 0 && matriz[i][j] > BLOQUEADOR; j--)
			if(matriz[i][j] == LAMP) return false;
		
		return true;
	}
	
	public boolean validaBlock(int x, int y){
		int nLamps = 0;
		
		if(x+1 < HEIGHT)
			if(matriz[x+1][y] == LAMP) nLamps++; 	
		if(x-1 >= 0)
			if(matriz[x-1][y] == LAMP) nLamps++; 
		if(y+1 < WIDTH)
			if(matriz[x][y+1] == LAMP) nLamps++; 
		if(y-1 >= 0)
			if(matriz[x][y-1] == LAMP) nLamps++; 
		
		if(nLamps < matriz[x][y]) return false;
		
		return true;
	}
	
	public boolean validaMarca(int x, int y){
		int i = x+1, j = y;
		for(; i < HEIGHT && matriz[i][j] > BLOQUEADOR; i++)
			if(matriz[i][j] == LIVRE) return true;

		i = x-1; j = y;
		for(; i >= 0 && matriz[i][j] > BLOQUEADOR; i--)
			if(matriz[i][j] == LIVRE) return true;

		i=x; j=y+1;
		for(; j < WIDTH && matriz[i][j] > BLOQUEADOR; j++)
			if(matriz[i][j] == LIVRE) return true;

		i=x; j=y-1;
		for(; j >= 0 && matriz[i][j] > BLOQUEADOR; j--)
			if(matriz[i][j] == LIVRE) return true;
		
		return false;
	}
	
	private void guardaEstadoActual(){
		int i, j;
		for(i = 0; i < HEIGHT; i++)
			for(j = 0; j < WIDTH; j++){
				backup[i][j] = matriz[i][j];
			}
	}
	
	public void restaurarEstado(){
		int i, j;
		for(i = 0; i < HEIGHT; i++)
			for(j = 0; j < WIDTH; j++){
				matriz[i][j] = backup[i][j];
			}
		setModfAs(true);
	}
	
	public void copySolucao(){
		int i, j;
		
		guardaEstadoActual();
		
		int[][] sol = nivel.getSolucao();
		for(i = 0; i < HEIGHT; i++)
			for(j = 0; j < WIDTH; j++){
				matriz[i][j] = sol[i][j];
			}
		setModfAs(true);
	}
	
	public void reset(){
		int i, j; 
		
		for(i = 0; i < HEIGHT; i++)
			for(j = 0; j < WIDTH; j++){
				switch(matriz[i][j]){
				case LAMP: 
					matriz[i][j] = LIVRE;
					break;
				case MARCA: 
					matriz[i][j] = LIVRE;
					break;
				case LAMPERRADA: 
					matriz[i][j] = LIVRE;
					break;
				case ILUMINADA: 
					matriz[i][j] = LIVRE;
					break;
				default: break;
				}
			}
		setModfAs(true);
		nivel.setStack(new Stack<Jogada>());
	}
	
	private void setModfAs(boolean v){
		for(int i = 0; i < HEIGHT; i++)
			for(int j = 0; j < WIDTH; j++){
				modf[i][j] = v;
			}
	}
	public void resetModf(){
		setModfAs(false);
	}
	
	public boolean isUpdate(int i, int j){
		return modf[i][j];
	}
}
