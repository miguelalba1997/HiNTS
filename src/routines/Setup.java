package routines;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
//import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;

import classes.Nanoparticle;
import classes.Sample;
import util.Constants;
import org.apache.commons.math3.*;
import org.apache.commons.math3.fitting.GaussianCurveFitter;
import org.apache.commons.math3.fitting.WeightedObservedPoints;

public class Setup {
	
	// Don't really need this method...
	public static Sample setupSample(Map<String,Object> params) throws Exception{
		
		Sample sample = new Sample(params);
		
		return sample;
	}
	
	// Should be called in the constructor for Sample
	public static Nanoparticle[] setupNanoparticles(Sample sample)  {
		
		List<String> line;
		double xcoord, ycoord, zcoord, diameter;
		
		Nanoparticle[] nanoparticles = new Nanoparticle[sample.nnanops];
		List<String> lines;
		try {
			lines = Files.readLines(new File(sample.getFilename()), Charsets.UTF_8);
			for(int i=0; i<sample.nnanops; i++){
				//System.out.println(lines);
				line = Arrays.asList(lines.get(i+6).split(","));
				xcoord = Double.valueOf(line.get(0));
				ycoord = Double.valueOf(line.get(1));
				zcoord = Double.valueOf(line.get(2));
				diameter = Double.valueOf(line.get(3));
				//System.out.println(line.get(0));
				nanoparticles[i] = new Nanoparticle(xcoord, ycoord, zcoord, diameter, sample);
			}
			return nanoparticles;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return nanoparticles;

	}
	
	
	/**
	@param nanoparticles and sample
	@return return the FWHM in ry unit
	@throws what kind of exception does this method throw
	*/
	public static double getFWHM(Nanoparticle[] nanoparticles, Sample sample){
		//System.out.println("I'm in FWHM");
		double FWHM;
		int bins = 50;
		double[] levels_eV = new double[sample.nnanops];
		double[] levels = new double[sample.nnanops];
		//System.out.println("I've made it past the levels");
		for(int i=0; i<sample.nnanops; i++){
			levels[i] = nanoparticles[i].getCB1(); // levels in ry unit
			levels_eV[i] = nanoparticles[i].getCB1()*Constants.rytoev; // levels in eV unit
		}
		Arrays.sort(levels);
		//System.out.println("I've sorted the levels");
		//System.out.println(Arrays.toString(levels));
		double[] levelCount = util.Utility.calcHistogram(levels, levels[0], levels[levels.length-1], bins);
		//System.out.println("I've calced the histogram");
		double[] levelLinear = util.Utility.linearSpread(levels[0], levels[levels.length-1], bins);
		//System.out.println("I've made the linear spread");
		//System.out.println(levelCount.length);
		//System.out.println(Arrays.toString(levelCount));
		//System.out.println(levelLinear.length);
		//System.out.println(Arrays.toString(levelLinear));
		GaussianCurveFitter fitter = GaussianCurveFitter.create();
		//System.out.println("I've made the Gaussian fitter");
	    WeightedObservedPoints obs = new WeightedObservedPoints();
		//System.out.println("I've initialized the Weighted observed points");
	    for (int index = 0; index < levelLinear.length; index++) {
	            obs.add(levelLinear[index], levelCount[index]);
			//System.out.println("I've added the observed points to a list");
	    }
		//System.out.println(obs.toList());
	    double[] bestFit = fitter.fit(obs.toList());
		//System.out.println("I've best fit");
	    // bestFit = norm, mean, sigma  ---> return sigma ---> FWHM
		
	    FWHM = bestFit[2]*2*Math.sqrt(2*Math.log(2));
	    return FWHM; 
	}
	
	
	public void setupElectrons(){
		
		return;
	}
	

	public static void main(String[] args) {

		// Necessary parameters
		//(INT_t steps, INT_t sample_no, INT_t e_number, INT_t h_number, INT_t nanops_number, 
		//FLOAT_t large_ratio, FLOAT_t temp, FLOAT_t thr, str features, FLOAT_t voltage_ratio, output):
        Map<String, Object> params = new HashMap<>();
        
        params.put("nelec", 200);
    	params.put("nholes", 0);
    	params.put("nnanops", 400);
    	params.put("sample_no", 0);
    	params.put("feature", "mobility");
    	params.put("temperature", 300.0);
    	params.put("thr", 0.0);
        
        Sample newsample = new Sample(params);
        
        
        
        
        
        //System.out.println(newsample.getElectronMass());
        //System.out.println(newsample.getFWHM()*Constants.rytoev);
        //Nanoparticle[] nanoparticles;
        //nanoparticles = setupNanoparticles(newsample);
        
        //System.out.println(nanoparticles[0].x/Constants.nmtobohr);
        //System.out.println(nanoparticles[399].getCB1());
		
		
		
	}

}
