
public class DNA {
	public static final int ngenes = 30;//979;
	public static final int nivelMutacao = 10;
	int odna[] = new int[ngenes];
	float score = -1;
	float distanciaTotal = -1;
	int repetidos = -1;
	
	public void criarandomico() {
		for(int i = 0; i < ngenes; i++) {
			odna[i] = Main.rnd.nextInt(ngenes);
		}
	}
	
	public void criaPaiMae(DNA pai,DNA mae,int cossover) {
		for(int i = 0; i < ngenes; i++) {
			if(i < cossover) {
				odna[i] = pai.odna[i];
			}else {
				odna[i] = mae.odna[i];
			}
		}
		
		for(int i = 0; i < 5; i++) {
			int index = Main.rnd.nextInt(ngenes);
			odna[index] = Main.rnd.nextInt(ngenes);
		}
		
	}
	
	@Override
	public String toString() {
		StringBuilder sdna = new StringBuilder();
		sdna.append("[");
		for(int i = 0; i < ngenes; i++) {
			if(i>0) {
				sdna.append(",");
			}
			sdna.append(odna[i]);
		}
		sdna.append("]");
		return sdna.toString();
	}
	
	public void avaliaDna() {
		Float somaValor = 0.0f;
		int repetidos = 0;
		int visitados[] = new int[ngenes];
		visitados[odna[0]] = 1;
		for(int i = 1; i < ngenes; i++) {
			int cod1 = odna[i-1];
			int cod2 = odna[i];
			if(visitados[cod2]==1) {
				repetidos++;
			}else {
				visitados[cod2]=1;
			}
			
			float dist = Main.matrizdistancias[cod1][cod2];
			somaValor+=dist;
		}
		
		distanciaTotal = somaValor;
		this.repetidos = repetidos;
		score = somaValor + repetidos*100;
	}	
}
