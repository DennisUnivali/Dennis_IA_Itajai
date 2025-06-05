
public class DNA {
	public static final int ngenes = 979;
	public static final int nivelMutacao = 5;
	int odna[] = new int[ngenes];
	float score = -1;
	
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
}
