package com.adp.lumus.saveGame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.HashMap;



public class Loader {
	//CONSTANTES
	public static final int VERSAO = 20130820;

	public static final int HEIGHT = 9;
	public static final int WIDTH = 7;

	public static final int PAREDE = 5;
	public static final int BLOQUEADOR = 6; //numero até ao qual existem tiles que bloqueiam a luz
	public static final int LIVRE = 10;
	public static final int LAMP = 11;
	public static final int MARCA = 12;
	public static final int ILUMINADA = 13;
	public static final int LAMPERRADA = 14;

	private static Save save;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File pastaNiveis = new File("Niveis/");
		save = new Save(VERSAO, true); //true faz overwrite na save guardada em memoria, false nao faz

		for (String pasta: pastaNiveis.list()) {
			int numPac = Integer.parseInt(pasta);
			File pastaPacote = new File("Niveis/" + pasta);
			Pacote pac = new Pacote(numPac);
			HashMap<String, int[][]> matrizes = new HashMap<String, int[][]>();
			HashMap<String, int[][]> solucoes = new HashMap<String, int[][]>();

			for(File file: pastaPacote.listFiles())
				if (file.isFile() && file.getName().endsWith(".ill")) {
					try {
						BufferedReader br = new BufferedReader(new FileReader(file));
						String nome = file.getName();
						int matriz[][] = lerMatriz(br);
						if(nome.endsWith("sol.ill")){
							solucoes.put(nome.substring(0, nome.length() - 7), matriz);
						} else matrizes.put(nome.substring(0, nome.length() - 4), matriz);
						br.close();
					} catch (FileNotFoundException e) {
					} catch (IOException e){
					}			
				} 
			int i = 1;
			for(String s: matrizes.keySet()){
				Nivel n = new Nivel(s, matrizes.get(s), solucoes.get(s));
				pac.putNivelNoCheck(i, n);
				i++;
			}
			save.putPacote(numPac, pac);
			System.out.println("NumPac: " + pac.getNumero() + "  SizePacote: " + pac.sizePacote() + "  SizeSave: " + save.size());
		}
		
		System.out.println("Done");
		guardaSave(save);

		/*Scanner sc = new Scanner(System.in);
		while(!sc.nextLine().equals("stop")){
			System.out.println("O que fazer?");
			String s = sc.nextLine();
			System.out.println(save.getNivel(Integer.parseInt(s), 1).toString());
		}
		sc.close();*/
	}

	
	private static int[][] lerMatriz(BufferedReader br){
		String linha, res[];
		char c;
		int i = 0, j = HEIGHT - 1; //o sistema de coordenadas e diferente entre os dois programas 
		int matriz[][] = new int[WIDTH][HEIGHT];
		try {
			linha = br.readLine(); //esquecer a linha com o 8 8
			while ((linha = br.readLine()) != null){
				res = linha.split(" ");
				i = 0;
				for(String s: res){
					c = s.charAt(0);
					switch(c){
						case 'x': 
							matriz[i][j] = PAREDE;
							break;
						case '-': 
							matriz[i][j] = LIVRE;
							break;
						case '@':
							matriz[i][j] = LAMP;
							break;
						case '.':
							matriz[i][j] = ILUMINADA;
							break;
						default:
							matriz[i][j] = Integer.parseInt(s);
							break;
					}
					i++;
				}
				j--;
			}
			br.close();
			return matriz;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return matriz;
	}

	private static void guardaSave(Save s){
		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(new FileOutputStream("save.lms"));
			stream.writeObject(s);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (stream != null) {
					stream.flush();
					stream.close();
				}
				System.out.println("Guardado");
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	//	private static void loadSavedGame(){
	//		ObjectInputStream stream = null;
	//		try {
	//			save = new Save(2);
	//			stream = new ObjectInputStream(new FileInputStream("save.lms"));
	//			save = (Save) stream.readObject();
	//		} catch (FileNotFoundException e) {
	//			e.printStackTrace();
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		} catch (ClassNotFoundException e) {
	//			e.printStackTrace();
	//		}
	//	}
}
