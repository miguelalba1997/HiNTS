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


import classes.Result;
import classes.Sample;
import util.Configuration;
import util.Constants;
import util.Utility;






class MobilityProcessor implements Callable<Map<String,Object>> {
	private int id;
	public double T;
	Double[] result;
	
	public MobilityProcessor(int id, double temperature){
		this.id = id;
		this.T=temperature;
	}

	@Override
	public Map<String, Object> call() {
		System.out.println("Starting: "+id);

		
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
		/*
		result[0] = (double) id;
		result[1] = newsample.simulation();
		*/
		Map<String, Object> result = newsample.simulation();
		System.out.println("Completed: "+id);	
		
		return result;
	}
}

class IVProcessor implements Callable<Map<String,Object>> {
	private int id;
	public double vm;
	Double[] result;

	public IVProcessor(int id, double voltageSweepMultiplier){
		this.id = id;
		this.vm=voltageSweepMultiplier;
	}

	@Override
	public Map<String, Object> call() {
		System.out.println("Starting: "+id);


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
		/*
		result[0] = (double) id;
		result[1] = newsample.simulation();
		*/
		Map<String, Object> result = newsample.simulation();

		System.out.println("Completed: "+id);

		return result;
	}
}
class CommensurationnElecProcessor implements Callable<Map<String,Object>> {
	private int id;
	public int nelec;
	public CommensurationnElecProcessor(int id, int numElec){
		this.id = id;
		this.nelec=numElec;
	}

	@Override
	public Map<String,Object> call() {
		System.out.println("Starting: "+id);

		//result = new Map<String,Object>;

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

		Map<String, Object> result = newsample.simulation();

		System.out.println("Completed: "+id);

		return result;
	}
}

class CommensurationBendingProcessor implements Callable<Map<String,Object>> {
	private int id;
	public double delta_bending;
	public CommensurationBendingProcessor(int id, double deltaBending){
		this.id = id;
		this.delta_bending=deltaBending;
	}

	@Override
	public Map<String,Object> call() {
		System.out.println("Starting: "+id);


		Map<String, Object> params = new HashMap<>();

		params.put("nelec", 392);
		params.put("nholes", 0);
		params.put("nnanops", 392);
		params.put("sample_no", id);
		params.put("feature", "layer_occupation");
		params.put("temperature", 1.0);
		params.put("thr", 0.0);
		params.put("voltageRatio", 1);
		params.put("delta_bending", delta_bending);


		Sample newsample = new Sample(params);
		//System.out.println("I've made it past sample creation in " + id);
		// the first element is the id, the second element is the actual result
		//System.out.println("I am in sample number" + id + "In the processor");

		Map<String,Object> result = newsample.simulation();

		System.out.println("Completed: "+id);

		return result;
	}
}


class MattLawProcessor implements Callable<Map<String,Object>> {
	private int id;
	public int numElec;
	public double propLNP;
	public MattLawProcessor(int id, int numElec, double propLNP){
		this.id = id;
		this.numElec=numElec;
		this.propLNP=propLNP;
	}

	@Override
	public Map<String,Object> call() {
		System.out.println("Starting: "+id);


		Map<String, Object> params = new HashMap<>();

		params.put("nelec", numElec);
		params.put("nholes", 0);
		params.put("nnanops", 400);
		params.put("sample_no", id);
		params.put("feature", "ML");
		params.put("temperature", 80.0);
		params.put("thr", 0.0);
		params.put("voltageRatio", 1);
		params.put("delta_bending", 0.0);
		params.put("Proportion_Large_NP", propLNP);


		Sample newsample = new Sample(params);

		Map<String,Object> result = newsample.simulation();

		System.out.println("Completed: "+id);

		return result;
	}
}



public class App {


	public static void main(String[] args) {

		Map<String, Object> results = new HashMap<>();

		results.put("id", 1);
		results.put("mobility", 1.0);
		results.put("current", 1.0);
		results.put("top occupation", 1.0);

		// TODO check that the number of samples is correct.

		int numberOfSamples = 40;
		String CommensurationLoop = null;

		String feature = null;
		Scanner reader = new Scanner(System.in);
		System.out.println("Please choose the feature you are interested in (1=mobility or 2=iv or 3=commensuration or 4=LawBimodalMobility): ");
		int n = reader.nextInt();
		if (n == 3) {
			System.out.println("What are you sweeping over? (1=electron number or 2=gate voltage): ");
			int h = reader.nextInt();
			if (h == 1) {
				CommensurationLoop = "electron number";
			} else if (h == 2) {
				CommensurationLoop = "gate voltage";
			} else {
				System.exit(2);
			}
		}
		System.out.println("Please choose the nanoparticle distribution you are studying (1=unimodal(must choose unimodal for commensuration) or 2=bimodal): ");
		int l = reader.nextInt();
		if (n == 1) {
			feature = "mobility";
			Configuration.twoLayer = false;
		} else if (n == 2) {
			feature = "iv";
			Configuration.twoLayer = false;
		} else if (n == 3) {
			feature = "commensuration";
			Configuration.twoLayer = true;
		}
			else if (n==4){
			Configuration.mattLawSamples = true;
			Configuration.twoLayer = false;
		}
		else {
			System.exit(2);
		}

		if (l == 1) {
			Configuration.biModal = false;
		} else if (l == 2) {
			Configuration.biModal = true;
		} else {
			System.exit(2);
		}

		//ExecutorService executor = Executors.newFixedThreadPool(4);
		//List<Future<Double[]>> futures = new ArrayList<>();
		if (feature == "mobility") {
			PrintWriter writer = null;

			double[] tempList = {30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 80, 120, 150, 180, 210, 240, 270, 300};
			//double[] tempList = {80};
			double[][] mobilityresultList = new double[tempList.length][];
			double[][] currentresultList = new double[tempList.length][];
			double[][] occupationresultList = new double[tempList.length][];

			for (int t = 0; t < tempList.length; t++) {
				ExecutorService executor = Executors.newFixedThreadPool(4);
				List<Future<Map<String, Object>>> futures = new ArrayList<>();
				for (int i = 0; i < numberOfSamples; i++) {
					futures.add(executor.submit(new CommensurationBendingProcessor(i, tempList[t])));
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

				//List<Double[]> resultRaw = new ArrayList<>();
				List<Map<String, Object>> resultRaw = new ArrayList<>();

				//for (Future<Double[]> future : futures) {
				for (Future<Map<String, Object>> future : futures) {
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
				//double[] resultProcessed = new double[resultRaw.size()/2];
				for (String key : Utility.processResult(resultRaw).get(1).keySet()) {
					double[] resultProcessed = new double[resultRaw.size() / 2];
					for (int i = 0; i < Utility.processResult(resultRaw).size(); i++) {
						resultProcessed[i] = (double) Utility.processResult(resultRaw).get(i).get(key);
						//System.out.println("result processed");
						//System.out.println(Arrays.toString(resultProcessed));
					}

					if (key == "id") {

					}
					if (key == "mobility") {
						mobilityresultList[t] = resultProcessed;
					}
					if (key == "current") {
						currentresultList[t] = resultProcessed;
					}
					if (key == "top occupation") {
						occupationresultList[t] = resultProcessed;
					}

				}


			}


			for (String key : results.keySet()) {
				{
					if (!Configuration.biModal && key != "id") {
						try {
							writer = new PrintWriter(key + "Standard Processor.txt", "UTF-8");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						double[][] resultList = new double[tempList.length][];
						if (key == "mobility") {
							resultList = mobilityresultList;
						} else if (key == "top occupation") {
							resultList = occupationresultList;
						} else if (key == "current") {
							resultList = currentresultList;
						}


						for (int r = 0; r < resultList.length; r++) {
							System.out.println(Arrays.toString(resultList[r]));
							for (int i = 0; i < resultList[r].length; i++) {
								writer.print(resultList[r][i]);
								writer.print(' ');
								//System.out.println(resultList[t][i]);
							}
							writer.println();
						}
						writer.close();

					}


					//System.out.println(Arrays.toString(resultProcessed));

					//System.out.println(results.get(1)[0]);
				}
			}
		}

		if (feature == "iv") {
			PrintWriter writer = null;

			double[] multiplierList = {.01, .05, .09, .1, .11, .2, .5, 1, 2, 3, 4, 5, 10, 15, 20, 30, 40, 50};
			double[][] mobilityresultList = new double[multiplierList.length][];
			double[][] currentresultList = new double[multiplierList.length][];
			double[][] occupationresultList = new double[multiplierList.length][];

			for (int t = 0; t < multiplierList.length; t++) {
				ExecutorService executor = Executors.newFixedThreadPool(4);
				List<Future<Map<String, Object>>> futures = new ArrayList<>();
				for (int i = 0; i < numberOfSamples; i++) {
					futures.add(executor.submit(new IVProcessor(i, multiplierList[t])));
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

				//List<Double[]> resultRaw = new ArrayList<>();
				List<Map<String, Object>> resultRaw = new ArrayList<>();

				//for (Future<Double[]> future : futures) {
				for (Future<Map<String, Object>> future : futures) {
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
				//double[] resultProcessed = new double[resultRaw.size()/2];
				for (String key : Utility.processResult(resultRaw).get(1).keySet()) {
					double[] resultProcessed = new double[resultRaw.size() / 2];
					for (int i = 0; i < Utility.processResult(resultRaw).size(); i++) {
						resultProcessed[i] = (double) Utility.processResult(resultRaw).get(i).get(key);
						System.out.println("result processed");
						//System.out.println(Arrays.toString(resultProcessed));
					}

					if (key == "id") {

					}
					if (key == "mobility") {
						mobilityresultList[t] = resultProcessed;
					}
					if (key == "current") {
						currentresultList[t] = resultProcessed;
					}
					if (key == "top occupation") {
						occupationresultList[t] = resultProcessed;
					}

				}


			}


			for (String key : results.keySet()) {
				{
					if (!Configuration.biModal && key != "id") {
						try {
							writer = new PrintWriter(key + "IVProcessor.txt", "UTF-8");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						double[][] resultList = new double[multiplierList.length][];
						if (key == "mobility") {
							resultList = mobilityresultList;
						} else if (key == "top occupation") {
							resultList = occupationresultList;
						} else if (key == "current") {
							resultList = currentresultList;
						}


						for (int r = 0; r < resultList.length; r++) {
							System.out.println(Arrays.toString(resultList[r]));
							for (int i = 0; i < resultList[r].length; i++) {
								writer.print(resultList[r][i]);
								writer.print(' ');
								//System.out.println(resultList[t][i]);
							}
							writer.println();
						}
						writer.close();

					}


					//System.out.println(Arrays.toString(resultProcessed));

					//System.out.println(results.get(1)[0]);
				}
			}
		}
		if (feature == "commensuration" && CommensurationLoop == "electron number") {
			PrintWriter writer = null;


			int[] numElecList = {10, 25, 50, 75, 100, 125, 150, 200, 250, 300, 350, 400, 450, 500, 550, 600};
			double[][] mobilityresultList = new double[numElecList.length][];
			double[][] currentresultList = new double[numElecList.length][];
			double[][] occupationresultList = new double[numElecList.length][];

			for (int t = 0; t < numElecList.length; t++) {
				ExecutorService executor = Executors.newFixedThreadPool(4);
				List<Future<Map<String, Object>>> futures = new ArrayList<>();
				for (int i = 0; i < numberOfSamples; i++) {
					futures.add(executor.submit(new CommensurationBendingProcessor(i, numElecList[t])));
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

				//List<Double[]> resultRaw = new ArrayList<>();
				List<Map<String, Object>> resultRaw = new ArrayList<>();

				//for (Future<Double[]> future : futures) {
				for (Future<Map<String, Object>> future : futures) {
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
				//double[] resultProcessed = new double[resultRaw.size()/2];
				for (String key : Utility.processResult(resultRaw).get(1).keySet()) {
					double[] resultProcessed = new double[resultRaw.size() / 2];
					for (int i = 0; i < Utility.processResult(resultRaw).size(); i++) {
						resultProcessed[i] = (double) Utility.processResult(resultRaw).get(i).get(key);
						System.out.println("result processed");
						//System.out.println(Arrays.toString(resultProcessed));
					}

					if (key == "id") {

					}
					if (key == "mobility") {
						mobilityresultList[t] = resultProcessed;
					}
					if (key == "current") {
						currentresultList[t] = resultProcessed;
					}
					if (key == "top occupation") {
						occupationresultList[t] = resultProcessed;
					}

				}


			}


			for (String key : results.keySet()) {
				{
					if (!Configuration.biModal && key != "id") {
						try {
							writer = new PrintWriter(key + "CommensurationElecLoop.txt", "UTF-8");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						double[][] resultList = new double[numElecList.length][];
						if (key == "mobility") {
							resultList = mobilityresultList;
						} else if (key == "top occupation") {
							resultList = occupationresultList;
						} else if (key == "current") {
							resultList = currentresultList;
						}


						for (int r = 0; r < resultList.length; r++) {
							System.out.println(Arrays.toString(resultList[r]));
							for (int i = 0; i < resultList[r].length; i++) {
								writer.print(resultList[r][i]);
								writer.print(' ');
								//System.out.println(resultList[t][i]);
							}
							writer.println();
						}
						writer.close();

					}


					//System.out.println(Arrays.toString(resultProcessed));

					//System.out.println(results.get(1)[0]);
				}
			}
		}
		if (feature == "commensuration" && CommensurationLoop == "gate voltage") {
			PrintWriter writer = null;

			double[] meVGateVoltageList = {0, 10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 125, 150, 175, 200, 250};
			//double[] meVGateVoltageList = {250};
			double[] gateVoltageList = new double[meVGateVoltageList.length];
			for (int i = 0; i < meVGateVoltageList.length; i++) {
				gateVoltageList[i] = meVGateVoltageList[i] * Constants.evtory / 1000;
			}
			double[][] mobilityresultList = new double[gateVoltageList.length][];
			double[][] currentresultList = new double[gateVoltageList.length][];
			double[][] occupationresultList = new double[gateVoltageList.length][];

			for (int t = 0; t < gateVoltageList.length; t++) {
				ExecutorService executor = Executors.newFixedThreadPool(4);
				List<Future<Map<String, Object>>> futures = new ArrayList<>();
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

				//List<Double[]> resultRaw = new ArrayList<>();
				List<Map<String, Object>> resultRaw = new ArrayList<>();

				//for (Future<Double[]> future : futures) {
				for (Future<Map<String, Object>> future : futures) {
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
				//double[] resultProcessed = new double[resultRaw.size()/2];
				for (String key : results.keySet()) {
					double[] resultProcessed = new double[resultRaw.size() / 2];
					for (int i = 0; i < Utility.processResult(resultRaw).size(); i += 1) {
						//System.out.println(Utility.processResult(resultRaw));
						if (key != "id") {
							resultProcessed[i] = (double) Utility.processResult(resultRaw).get(i).get(key);
						}
						//System.out.println(i);
						//System.out.println("result processed");
						//System.out.println(Arrays.toString(resultProcessed));
					}

					if (key == "id") {

					}
					if (key == "mobility") {
						mobilityresultList[t] = resultProcessed;
					}
					if (key == "current") {
						currentresultList[t] = resultProcessed;
					}
					if (key == "top occupation") {
						occupationresultList[t] = resultProcessed;
					}

				}


			}

			//TODO KEEP THE NUMBER OF ELECTRONS CORRECT
			for (String key : results.keySet()) {
				{
					if (!Configuration.biModal && key != "id") {
						try {
							writer = new PrintWriter(key + String.valueOf(392) + "elec_" + String.valueOf(Configuration.sizeDisorder) + "Ordered392.txt", "UTF-8");
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}

						double[][] resultList = new double[gateVoltageList.length][];
						if (key == "mobility") {
							resultList = mobilityresultList;
						} else if (key == "top occupation") {
							resultList = occupationresultList;
						} else if (key == "current") {
							resultList = currentresultList;
						}


						for (int r = 0; r < resultList.length; r++) {
							System.out.println(Arrays.toString(resultList[r]));
							for (int i = 0; i < resultList[r].length; i++) {
								writer.print(resultList[r][i]);
								writer.print(' ');
								//System.out.println(resultList[t][i]);
							}
							writer.println();
						}
						writer.close();

					}


					System.out.println("Voltage loop complete");


					//System.out.println(Arrays.toString(resultProcessed));

					//System.out.println(results.get(1)[0]);
				}
			}

		}


		if (Configuration.mattLawSamples) {
			PrintWriter writer = null;


			int[] numElecList = {20, 50, 100, 150, 200};
			double[] propLNPList = {0.0, 0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7, 0.75, 0.8, 0.85, 0.9, 0.95, 1.0};
			double[][][] mobilityresultList = new double[numElecList.length][propLNPList.length][];

			for (int t = 0; t < numElecList.length; t++) {
				for (int p = 0; p < propLNPList.length; p++) {
					Configuration.proportionLargeNP=propLNPList[p];
					ExecutorService executor = Executors.newFixedThreadPool(4);
					List<Future<Map<String, Object>>> futures = new ArrayList<>();
					for (int i = 0; i < numberOfSamples; i++) {
						futures.add(executor.submit(new MattLawProcessor(i, numElecList[t], propLNPList[p])));
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

					//List<Double[]> resultRaw = new ArrayList<>();
					List<Map<String, Object>> resultRaw = new ArrayList<>();

					//for (Future<Double[]> future : futures) {
					for (Future<Map<String, Object>> future : futures) {
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
					//double[] resultProcessed = new double[resultRaw.size()/2];
					for (String key : Utility.processResult(resultRaw).get(1).keySet()) {
						double[] resultProcessed = new double[resultRaw.size() / 2];
						for (int i = 0; i < Utility.processResult(resultRaw).size(); i++) {
							resultProcessed[i] = (double) Utility.processResult(resultRaw).get(i).get(key);
							//System.out.println("result processed");
							//System.out.println(Arrays.toString(resultProcessed));
						}

						if (key == "id") {

						}
						if (key == "mobility") {
							mobilityresultList[t][p] = resultProcessed;

						}
						//System.out.print("The result list is: " + mobilityresultList[t][p]);
						System.out.println("We are now " + ((float) t)/numElecList.length + " Through the elec loop in the " + p +
								"th proportion loop");


					}
				}
			}


					for (String key : results.keySet()) {

							if (key == "mobility") {
								try {
									writer = new PrintWriter(key + "MLSample_elec_prop_loop.txt", "UTF-8");
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (UnsupportedEncodingException e) {
									e.printStackTrace();
								}

								double[][][] resultList = new double[numElecList.length][propLNPList.length][];
								if (key == "mobility") {
									resultList = mobilityresultList;
								}


								for (int r1 = 0; r1 < numElecList.length; r1++) {
									for (int r2 = 0; r2 < propLNPList.length; r2++) {
										System.out.println(Arrays.toString(resultList[r1][r2]));
										for (int i = 0; i < resultList[r1][r2].length; i++) {
											writer.print(resultList[r1][r2][i]);
											writer.print(' ');
											//System.out.println(resultList[t][i]);
										}
										writer.println();
									}
								}
								writer.close();

							}


							//System.out.println(Arrays.toString(resultProcessed));

							//System.out.println(results.get(1)[0]);
						}
					}
				}



			}



