import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;

public class Main {
	public static float matrizdistancias[][];
	public static ArrayList<float[]> listadelinhas = new ArrayList<float[]>();
	public static int citycode = 0;
	public static HashMap<Integer,String> mapCodName = new HashMap<Integer,String>();
	public static HashMap<String,Integer> mapNameCod = new HashMap<String,Integer>();
	public static Random rnd = new Random();
	public static int numerodeindividuos = 10000000;
	
	public static void main(String[] args) {

		carregaMatrizDistancia();
		
		ArrayList<DNA> pollGenetico = new ArrayList<DNA>();
		
		for(int i = 0; i < numerodeindividuos; i++) {
			DNA dna1 = new DNA();
			dna1.criarandomico();
			dna1.avaliaDna();
			pollGenetico.add(dna1);
		}
		
		int numerogeracoes = 100;
		for(int geracao = 0; geracao < numerogeracoes; geracao++) {
			
			Collections.sort(pollGenetico, new Comparator<DNA>() {
				@Override
				public int compare(DNA o1, DNA o2) {
					return o1.score < o2.score? -1: o1.score > o2.score? 1:0;
				}
			});
			
			System.out.println("Geracao "+geracao);
			for(int i = 0; i < 10;i++) {
				DNA dna = pollGenetico.get(i);
				System.out.println(""+i+": "+dna.score+","+dna.distanciaTotal+","+dna.repetidos+" "+dna);
			}
			
			ArrayList<DNA> novosGenes = new ArrayList<DNA>();
			for(int i = 0; i < numerodeindividuos; i++) {
				int pai = sorteio();
				int mae = sorteio();
				
				if(pai==mae) {
					mae++;
				}
				
				DNA dna_pai = pollGenetico.get(pai);
				DNA dna_mae = pollGenetico.get(mae);
				
				int pontocrossover = rnd.nextInt(DNA.ngenes-10)+5;
				
				DNA novodna = new DNA();
				novodna.criaPaiMae(dna_pai, dna_mae, pontocrossover);
				novosGenes.add(novodna);
			}
			
			pollGenetico.clear();
			for(int i = 0; i < numerodeindividuos; i++) {
				DNA dna = novosGenes.get(i);
				dna.avaliaDna();
				pollGenetico.add(dna);
			}
		}
		
	}
	
	public static int sorteio() {
		int sorteio = rnd.nextInt(2048-1);
		int vbase = 1024;
		for(int i = 0; i < 10; i++) {
			sorteio-=vbase;
			if(sorteio<0) {
				return i;
			}
			vbase = vbase/2;
		}
		return 9;
	}

	private static void carregaMatrizDistancia() {
		BufferedReader bfr;
		try {
			bfr = new BufferedReader(new FileReader("distances.csv"), 1024000);
			String header = bfr.readLine();
			System.out.println("header "+header);
			String line = "";
			

			while((line=bfr.readLine())!=null) {
				//System.out.println(""+line);
				String splt[] = line.split(",");
				String cidade_origem = splt[0];
				String cidade_destino = splt[1];
				float dist = Float.parseFloat(splt[3]);
				
				int origemcode = -1;
				if(mapNameCod.containsKey(cidade_origem)) {
					origemcode = mapNameCod.get(cidade_origem);
				}else {
					origemcode = citycode;
					mapNameCod.put(cidade_origem, origemcode);
					mapCodName.put( origemcode, cidade_origem);
					citycode++;
				}
				
				int destinocode = -1;
				if(mapNameCod.containsKey(cidade_destino)) {
					destinocode = mapNameCod.get(cidade_destino);
				}else {
					destinocode = citycode;
					mapNameCod.put(cidade_destino, destinocode);
					mapCodName.put( destinocode, cidade_destino);
					citycode++;
				}
				
				float dado[] = new float[3];
				dado[0] = origemcode;
				dado[1] = destinocode;
				dado[2] = dist;
				
				listadelinhas.add(dado);
			}
			
			System.out.println(" "+citycode+" "+listadelinhas.size());
			
			matrizdistancias = new float[citycode][citycode];
			
			for(int i = 0; i < listadelinhas.size();i++) {
				float dado[] = listadelinhas.get(i);
				matrizdistancias[(int)dado[0]][(int)dado[1]] = dado[2];
			}
			
			//Lenningen,Asselborn,62.3,85.5
//			int cod1 = mapNameCod.get("Lenningen");
//			int cod2 = mapNameCod.get("Asselborn");
//			float dist = matrizdistancias[cod1][cod2];
			
			//System.out.println("Lenningen ->  Asselborn "+cod1+" - "+cod2+" dist "+dist);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
