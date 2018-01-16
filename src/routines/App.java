package routines;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


import classes.Sample;
import util.Utility;

class Processor implements Callable<Double[]> {
	private int id;
	Double[] result;
	
	public Processor(int id){
		this.id = id;
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
		params.put("temperature", 80.0);
		params.put("thr", 0.0);
		
		Sample newsample = new Sample(params);
		
		// the first element is the id, the second element is the actual result
		result[0] = (double) id;
		result[1] = newsample.simulation();
		
		System.out.println("Completed: "+id);	
		
		return result;
	}
}

public class App {

	public static void main(String[] args) {
		
		int numberOfSamples = 2;

		ExecutorService executor = Executors.newFixedThreadPool(4);
		List<Future<Double[]>> futures = new ArrayList<>();
		
		
		
		for(int i=0; i<numberOfSamples; i++){
			futures.add(executor.submit(new Processor(i)));
		}
		
		// Stop accepting new tasks. Wait for all threads to terminate.
		executor.shutdown();
		
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
		
		for(Future<Double[]> future: futures){
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

		System.out.println(Arrays.toString(resultProcessed));
		
		//System.out.println(results.get(1)[0]);
	}

}



