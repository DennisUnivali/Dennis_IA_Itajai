import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.ImageIO;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.NeuralNetworkEvent;
import org.neuroph.core.events.NeuralNetworkEventListener;
import org.neuroph.util.TransferFunctionType;


public class TitanicPerceptronSample {
	
	public static void main(String[] args) {

		DataSet trainingSet = new DataSet(10, 1);
		DataSet testSet = new DataSet(10, 1);
		try {
			BufferedReader bfr = new BufferedReader(new FileReader("titanic.csv"));
			String line = bfr.readLine();
			System.out.println("Header: "+line);
			int count = 0;
			while((line=bfr.readLine())!=null) {
				String split[] = line.split(";");
				int survive = Integer.parseInt(split[1]);
				int pclass = Integer.parseInt(split[2]); 
				int sex = split[4].equals("male")?0:1;
				float age = 0;
				int semidade = 0;
				try {
					age = Float.parseFloat(split[5].replace(",", "."));
					semidade = 0;
				}catch(Exception e1) {
					age = -1;
					semidade = 1;
				}
				int sibSp = Integer.parseInt(split[6]);
				int parch = Integer.parseInt(split[7]);
				float fare = Float.parseFloat(split[9].replace(",", "."));
				
				int embark0 = 0;
				int embark1 = 0;
				int embark2 = 0;
				
				if(split.length>=12) {
					if(split[11].equals("S")) {
						embark0 = 1;
					}
					if(split[11].equals("C")) {
						embark1 = 1;
					}
					if(split[11].equals("Q")) {
						embark2 = 1;
					}
				}
				
				if(count<500) {
					trainingSet.addRow(new DataSetRow(new double[] { pclass,sex,age,semidade,sibSp,parch,fare,embark0,embark1,embark2 }, new double[] {survive}));
				}else {
					testSet.addRow(new DataSetRow(new double[] { pclass,sex,age,semidade,sibSp,parch,fare,embark0,embark1,embark2 }, new double[] {survive}));	
				}
				count++;
				//System.out.println("Linhas: "+line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		
		
		System.out.println("Vai Treinar");

		// create multi layer perceptron
		MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.TANH, 10, 16,32, 1);
		BackPropagation learningrules = new BackPropagation();
		learningrules.setMaxIterations(1000); 
		learningrules.setLearningRate(0.001);
		learningrules.setMaxError(0.000000001);
		
		myMlPerceptron.addListener(new NeuralNetworkEventListener() {
			int epoca = -1;
			@Override
			public void handleNeuralNetworkEvent(NeuralNetworkEvent arg0) {
				//System.out.println(arg0.toString());
				if(epoca!=learningrules.getCurrentIteration()) {
					System.out.println(""+learningrules.getCurrentIteration()+" - "+learningrules.getPreviousEpochError());
					epoca = learningrules.getCurrentIteration();
				}
			}
		});
		myMlPerceptron.learn(trainingSet, learningrules);

		
		//testNeuralNetwork(myMlPerceptron,trainingSet);
		
		int matrizDeConfusao[][] = new int[2][2];
		
		for (DataSetRow dataRow : trainingSet.getRows()) {
			myMlPerceptron.setInput(dataRow.getInput());
			myMlPerceptron.calculate();
			double[] networkOutput = myMlPerceptron.getOutput();
			double[] desiredOutput = dataRow.getDesiredOutput();
			
			if(desiredOutput[0]==1) {
				if(networkOutput[0]>0.5) {
					matrizDeConfusao[0][0]++;
				}else {
					matrizDeConfusao[0][1]++;
				}
			}else {
				if(networkOutput[0]>0.5) {
					matrizDeConfusao[1][0]++;
				}else {
					matrizDeConfusao[1][1]++;
				}
			}
		}
		
		printaEstatisticas(matrizDeConfusao);
		
		
		int matrizDeConfusaoTest[][] = new int[2][2];
		
		for (DataSetRow dataRow : testSet.getRows()) {
			myMlPerceptron.setInput(dataRow.getInput());
			myMlPerceptron.calculate();
			double[] networkOutput = myMlPerceptron.getOutput();
			double[] desiredOutput = dataRow.getDesiredOutput();
			
			if(desiredOutput[0]==1) {
				if(networkOutput[0]>0.5) {
					matrizDeConfusaoTest[0][0]++;
				}else {
					matrizDeConfusaoTest[0][1]++;
				}
			}else {
				if(networkOutput[0]>0.5) {
					matrizDeConfusaoTest[1][0]++;
				}else {
					matrizDeConfusaoTest[1][1]++;
				}
			}
		}
		
		printaEstatisticas(matrizDeConfusaoTest);		
		
		
		// save trained neural network
		System.out.println("Save Perceptron");
		myMlPerceptron.save("myMlPerceptron.nnet");
		// load saved neural network
		
		System.out.println("Load Perceptron");
		NeuralNetwork loadedMlPerceptron = NeuralNetwork.createFromFile("myMlPerceptron.nnet");
		// test loaded neural network
		

	}

	private static void printaEstatisticas(int[][] matrizDeConfusao) {
		System.out.println("------------------");
		System.out.println("Matriz de Confusão");
		System.out.println(""+matrizDeConfusao[0][0]+"\t\t"+matrizDeConfusao[0][1]);
		System.out.println(""+matrizDeConfusao[1][0]+"\t\t"+matrizDeConfusao[1][1]);
		System.out.println("");

		float total = matrizDeConfusao[0][0]+matrizDeConfusao[0][1]+matrizDeConfusao[1][0]+matrizDeConfusao[1][1];
		System.out.println("Total: "+total);
		System.out.println("");
		float acc = (matrizDeConfusao[0][0]+matrizDeConfusao[1][1])/total;
		System.out.println("ACC: "+acc);
		float precisao = (matrizDeConfusao[0][0])/(float)(matrizDeConfusao[0][0]+matrizDeConfusao[1][0]);
		System.out.println("Precisão: "+precisao);
		float revocacao = (matrizDeConfusao[0][0])/(float)(matrizDeConfusao[0][0]+matrizDeConfusao[0][1]);
		System.out.println("Revocacao: "+revocacao);
		float f1 = 2*((precisao*revocacao)/(precisao+revocacao));
		System.out.println("F1: "+f1);
		
		System.out.println("");
	}

	public static void testNeuralNetwork(NeuralNetwork nnet, DataSet testSet) {
		for (DataSetRow dataRow : testSet.getRows()) {
			nnet.setInput(dataRow.getInput());
			nnet.calculate();
			double[] networkOutput = nnet.getOutput();
			System.out.print("Input: " + Arrays.toString(dataRow.getInput()));
			System.out.println(" Output: " + Arrays.toString(networkOutput)+" DESIRED OUTPUT: "+Arrays.toString(dataRow.getDesiredOutput()));
		}
	}
	


}