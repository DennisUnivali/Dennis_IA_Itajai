import java.util.Random;

public class adlineDataSet {
	public static void main(String[] args) {
		Random r = new Random();
		double in[][] = new double[1000][3];
		double out[][] = new double[1000][3];
		
		for(int i = 0; i < 1000;i++) {
			in[i][0] = r.nextDouble()*200-100;
			in[i][1] = r.nextDouble()*200-100;
			in[i][2] = r.nextDouble()*200-100;
		}
		for(int i = 0; i < 1000;i++) {
			out[i][0] = f1(in[i][0],in[i][1],in[i][2])?1.0:0.0;
			out[i][1] =	f2(in[i][0],in[i][1],in[i][2])?1.0:0.0;
			out[i][2] = f3(in[i][0],in[i][1],in[i][2])?1.0:0.0;
		}		
		
		System.out.println("------------------------------------------------------------------------------------");
		for(int i = 0; i < 1000;i++) {
			System.out.println(""+in[i][0]+";"+in[i][1]+";"+in[i][2]+";");
		}
		System.out.println("------------------------------------------------------------------------------------");
		for(int i = 0; i < 1000;i++) {
			System.out.println(""+out[i][0]+";"+out[i][1]+";"+out[i][2]+";");
		}
	}
	public static boolean f1(double x,double y, double z) {
		return (x*2-y*1.5+z*4-3) > 0;
	}
	public static boolean f2(double x,double y, double z) {
		return (x*3-y*2+z-9) > 0;
	}
	public static boolean f3(double x,double y, double z) {
		return (x+y*3-z*1-5) > 0;
	}
}
