package routines;

import java.io.FileNotFoundException;
import java.io.ObjectInputFilter;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.io.PrintWriter;


import classes.Sample;
import util.Configuration;
import util.Constants;
import util.Utility;

class MobilityProcessor implements Callable<Double[]> {
	private int id;
	public double T;
	Double[] result;
	
	public MobilityProcessor(int id, double temperature){
		this.id = id;
		this.T=temperature;
	}

	@Override
	public Double[] call() {
		System.out.println("Starting: "+id);
		
		result = new Double[2];
		
		Map<String, Object> params = new HashMap<>();
		
		params.put("nelec", 100);
		params.put("nholes", 0);
		params.put("nnanops", 400);
		params.put("sample_no", id);
		params.put("feature", "mobility");
		params.put("temperature", T);
		params.put("thr", 0.0);
		params.put("voltageRatio", 1);
		params.put("delta_bending", 0.0);

		
		Sample newsample = new Sample(params);
		//System.out.println("I've made it past sample creation");
		// the first element is the id, the second element is the actual result
		//System.out.println("I am in sample number" + id + "In the processor");

		result[0] = (double) id;
		result[1] = newsample.simulation();
		
		System.out.println("Completed: "+id);	
		
		return result;
	}
}

class IVProcessor implements Callable<Double[]> {
	private int id;
	public double vm;
	Double[] result;

	public IVProcessor(int id, double voltageSweepMultiplier){
		this.id = id;
		this.vm=voltageSweepMultiplier;
	}

	@Override
	public Double[] call() {
		System.out.println("Starting: "+id);

		result = new Double[2];

		Map<String, Object> params = new HashMap<>();

		params.put("nelec", 100);
		params.put("nholes", 0);
		params.put("nnanops", 400);
		params.put("sample_no", id);
		params.put("feature", "iv");
		params.put("temperature", 80.0);
		params.put("thr", 0.0);
		params.put("voltageRatio", vm);
		params.put("delta_bending", 0.0);

		Sample newsample = new Sample(params);

		// the first element is the id, the second element is the actual result
		result[0] = (double) id;
		result[1] = newsample.simulation();

		System.out.println("Completed: "+id);

		return result;
	}
}
class CommensurationnElecProcessor implements Callable<Double[]> {
	private int id;
	public int nelec;
	Double[] result;
	public CommensurationnElecProcessor(int id, int numElec){
		this.id = id;
		this.nelec=numElec;
	}

	@Override
	public Double[] call() {
		System.out.println("Starting: "+id);

		result = new Double[2];

		Map<String, Object> params = new HashMap<>();

		params.put("nelec", nelec);
		params.put("nholes", 0);
		params.put("nnanops", 400);
		params.put("sample_no", id);
		params.put("feature", "mobility");
		params.put("temperature", 80.0);
		params.put("thr", 0.0);
		params.put("voltageRatio", 1);
		params.put("delta_bending", 0.0);


		Sample newsample = new Sample(params);
		//System.out.println("I've made it past sample creation");
		// the first element is the id, the second element is the actual result
		//System.out.println("I am in sample number" + id + "In the processor");

		result[0] = (double) id;
		result[1] = newsample.simulation();

		System.out.println("Completed: "+id);

		return result;
	}
}

class CommensurationBendingProcessor implements Callable<Double[]> {
	private int id;
	public double delta_bending;
	Double[] result;
	public CommensurationBendingProcessor(int id, double deltaBending){
		this.id = id;
		this.delta_bending=deltaBending;
	}

	@Override
	public Double[] call() {
		System.out.println("Starting: "+id);

		result = new Double[2];

		Map<String, Object> params = new HashMap<>();

		params.put("nelec", 400);
		params.put("nholes", 0);
		params.put("nnanops", 400);
		params.put("sample_no", id);
		params.put("feature", "layer_occupation");
		params.put("temperature", 80.0);
		params.put("thr", 0.0);
		params.put("voltageRatio", 1);
		params.put("delta_bending", delta_bending);


		Sample newsample = new Sample(params);
		//System.out.println("I've made it past sample creation");
		// the first element is the id, the second element is the actual result
		//System.out.println("I am in sample number" + id + "In the processor");

		result[0] = (double) id;
		result[1] = newsample.simulation();

		System.out.println("Completed: "+id);

		return result;
	}
}


public class App {



	public static void main(String[] args) {

		int numberOfSamples = 2;
		String CommensurationLoop=null;

		String feature=null;
		Scanner reader = new Scanner(System.in);
		System.out.println("Please choose the feature you are interested in (1=mobility or 2=iv or 3=commensuration): ");
		int n = reader.nextInt();
		if(n==3) {
			System.out.println("What are you sweeping over? (1=electron number or 2=gate voltage): ");
			int h = reader.nextInt();
			if(h==1){
				CommensurationLoop="electron number";
			}
			else if(h==2){
				CommensurationLoop="gate voltage";
			}
			else{
				System.exit(2);
			}
		}System.out.println("Please choose the nanoparticle distribution you are studying (1=unimodal(must choose unimodal for commensuration) or 2=bimodal): ");
		int l=reader.nextInt();
		if(n==1){
			feature = "mobility";
			Configuration.twoLayer=false;
		}
		else if(n==2) {
			feature = "iv";
			Configuration.twoLayer=false;
		}
		else if(n==3){
			feature="commensuration";
			Configuration.twoLayer=true;
		}

		else{
			System.exit(2);
		}

		if(l==1){
			Configuration.biModal=false;
		}
		else if(l==2) {
			Configuration.biModal=true;
		}
		else{
			System.exit(2);
		}

		//ExecutorService executor = Executors.newFixedThreadPool(4);
		//List<Future<Double[]>> futures = new ArrayList<>();
		if (feature == "mobility") {
			PrintWriter writer = null;

			{if(!Configuration.biModal) {
				try {
					writer = new PrintWriter("mobilitytestfile.txt", "UTF-8");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			if(Configuration.biModal){
				try {
					writer = new PrintWriter("bimodal"+String.valueOf(Configuration.proportionLargeNP)+"AllTempmobilitytestfile.txt", "UTF-8");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			}
			double[] tempList = {30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 80, 120, 150, 180, 210, 240, 270, 300};
			//double[] tempList = {300};
			double[][] resultList = new double[tempList.length][];
			for (int t = 0; t < tempList.length; t++) {
				ExecutorService executor = Executors.newFixedThreadPool(4);
				List<Future<Double[]>> futures = new ArrayList<>();
				for (int i = 0; i < numberOfSamples; i++) {
					futures.add(executor.submit(new MobilityProcessor(i, tempList[t])));
				}

				executor.shutdown();

				// Stop accepting new tasks. Wait for all threads to terminate.


				System.out.println("All tasks submitted.");

				// wait for the processes to finish. Setting a time out limit of 1 day.
				try {
					executor.awaitTermination(1, TimeUnit.DAYS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				System.out.println("All tasks completed.");

				List<Double[]> resultRaw = new ArrayList<>();

				for (Future<Double[]> future : futures) {
					try {
						resultRaw.add(future.get());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}

				//double[] resultOrdered = Utility.orderResult(resultRaw);

				// Average over regular and inverted samples
				double[] resultProcessed = Utility.processResult(resultRaw);
				//System.out.println(Arrays.toString(resultProcessed));
				resultList[t] = Utility.processResult(resultRaw);
			}
			//System.out.println("temp loop complete");
			for (int t = 0; t < resultList.length; t++) {
				System.out.println(Arrays.toString(resultList[t]));
				for (int i = 0; i < resultList[t].length; i++) {
					writer.print(resultList[t][i]);
					writer.print(' ');
					//System.out.println(resultList[t][i]);
				}
				writer.println();
			}
			writer.close();


			//System.out.println(Arrays.toString(resultProcessed));

			//System.out.println(results.get(1)[0]);
		}

		if(feature == "iv"){
			PrintWriter writer = null;

			{if(!Configuration.biModal) {
				try {
					writer = new PrintWriter("ivtestfile.txt", "UTF-8");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
				if(Configuration.biModal){
					try {
						writer = new PrintWriter("bimodal"+String.valueOf(Configuration.proportionLargeNP)+"ivtestfile.txt", "UTF-8");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			double[] multiplierList = {.01,.05,.09,.1,.11,.2,.5,1,2,3,4,5,10,15,20,30,40,50};
			//double[] multiplierList = {1,10};
			double[][] resultList = new double[multiplierList.length][];
			for (int m = 0; m < multiplierList.length; m++) {
				ExecutorService executor = Executors.newFixedThreadPool(4);
				List<Future<Double[]>> futures = new ArrayList<>();
				for (int i = 0; i < numberOfSamples; i++) {
					futures.add(executor.submit(new IVProcessor(i, multiplierList[m])));
				}

				executor.shutdown();

				// Stop accepting new tasks. Wait for all threads to terminate.


				System.out.println("All tasks submitted.");

				// wait for the processes to finish. Setting a time out limit of 1 day.
				try {
					executor.awaitTermination(1, TimeUnit.DAYS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				System.out.println("All tasks completed.");

				List<Double[]> resultRaw = new ArrayList<>();

				for (Future<Double[]> future : futures) {
					try {
						resultRaw.add(future.get());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}

				//double[] resultOrdered = Utility.orderResult(resultRaw);

				// Average over regular and inverted samples
				double[] resultProcessed = Utility.processResult(resultRaw);
				//System.out.println(Arrays.toString(resultProcessed));
				resultList[m] = Utility.processResult(resultRaw);
			}
			System.out.println("Voltage Sweep complete");
			for (int m = 0; m < resultList.length; m++) {
				System.out.println(Arrays.toString(resultList[m]));
				for (int i = 0; i < resultList[m].length; i++) {
					writer.print(resultList[m][i]);
					writer.print(' ');
					//System.out.println(resultList[t][i]);
				}
				writer.println();
			}
			writer.close();


			//System.out.println(Arrays.toString(resultProcessed));

			//System.out.println(results.get(1)[0]);

		}
		if (feature == "commensuration" && CommensurationLoop=="electron number") {
			PrintWriter writer = null;

			{if(!Configuration.biModal) {
				try {
					writer = new PrintWriter("commensurationnElectestfile.txt", "UTF-8");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

				if(Configuration.biModal){
					try {
						writer = new PrintWriter("bimodal"+String.valueOf(Configuration.proportionLargeNP)+"commensurationnElectestfile.txt", "UTF-8");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			int[] numElecList = {10,25,50,75,100,125,150,200,250,300,350,400,450,500,550,600};
			double[][] resultList = new double[numElecList.length][];
			for (int t = 0; t < numElecList.length; t++) {
				ExecutorService executor = Executors.newFixedThreadPool(4);
				List<Future<Double[]>> futures = new ArrayList<>();
				for (int i = 0; i < numberOfSamples; i++) {
					futures.add(executor.submit(new CommensurationnElecProcessor(i, numElecList[t])));
				}

				executor.shutdown();

				// Stop accepting new tasks. Wait for all threads to terminate.


				System.out.println("All tasks submitted.");

				// wait for the processes to finish. Setting a time out limit of 1 day.
				try {
					executor.awaitTermination(1, TimeUnit.DAYS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				System.out.println("All tasks completed.");

				List<Double[]> resultRaw = new ArrayList<>();

				for (Future<Double[]> future : futures) {
					try {
						resultRaw.add(future.get());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}

				//double[] resultOrdered = Utility.orderResult(resultRaw);

				// Average over regular and inverted samples
				double[] resultProcessed = Utility.processResult(resultRaw);
				//System.out.println(Arrays.toString(resultProcessed));
				resultList[t] = Utility.processResult(resultRaw);
			}
			//System.out.println("temp loop complete");
			for (int t = 0; t < resultList.length; t++) {
				System.out.println(Arrays.toString(resultList[t]));
				for (int i = 0; i < resultList[t].length; i++) {
					writer.print(resultList[t][i]);
					writer.print(' ');
					//System.out.println(resultList[t][i]);
				}
				writer.println();
			}
			writer.close();


			//System.out.println(Arrays.toString(resultProcessed));

			//System.out.println(results.get(1)[0]);
		}
		if (feature == "commensuration" && CommensurationLoop=="gate voltage") {
			PrintWriter writer = null;

			{if(!Configuration.biModal) {
				try {
					writer = new PrintWriter("commensurationBendingtestfile"+String.valueOf(400)+"elec.txt", "UTF-8");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

				if(Configuration.biModal){
					try {
						writer = new PrintWriter("bimodal"+String.valueOf(Configuration.proportionLargeNP)+"commensurationBendingtestfile200elec.txt", "UTF-8");
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			double[] meVGateVoltageList = {10,25,50,75,100,125,150,200,250,300,350,400,450,500,550,600};
			double[] gateVoltageList= new double[meVGateVoltageList.length];
			for(int i=0; i<meVGateVoltageList.length; i++){
				gateVoltageList[i]=meVGateVoltageList[i]* Constants.evtory/1000;
			}
			double[][] resultList = new double[gateVoltageList.length][];
			for (int t = 0; t < gateVoltageList.length; t++) {
				ExecutorService executor = Executors.newFixedThreadPool(4);
				List<Future<Double[]>> futures = new ArrayList<>();
				for (int i = 0; i < numberOfSamples; i++) {
					futures.add(executor.submit(new CommensurationBendingProcessor(i, gateVoltageList[t])));
				}

				executor.shutdown();

				// Stop accepting new tasks. Wait for all threads to terminate.


				System.out.println("All tasks submitted.");

				// wait for the processes to finish. Setting a time out limit of 1 day.
				try {
					executor.awaitTermination(1, TimeUnit.DAYS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}


				System.out.println("All tasks completed.");

				List<Double[]> resultRaw = new ArrayList<>();

				for (Future<Double[]> future : futures) {
					try {
						resultRaw.add(future.get());
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}

				//double[] resultOrdered = Utility.orderResult(resultRaw);

				// Average over regular and inverted samples
				double[] resultProcessed = Utility.processResult(resultRaw);
				//System.out.println(Arrays.toString(resultProcessed));
				resultList[t] = Utility.processResult(resultRaw);
			}
			//System.out.println("temp loop complete");
			for (int t = 0; t < resultList.length; t++) {
				System.out.println(Arrays.toString(resultList[t]));
				for (int i = 0; i < resultList[t].length; i++) {
					writer.print(resultList[t][i]);
					writer.print(' ');
					//System.out.println(resultList[t][i]);
				}
				writer.println();
			}
			writer.close();


			//System.out.println(Arrays.toString(resultProcessed));

			//System.out.println(results.get(1)[0]);
		}
	}

}



