package classes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import routines.Setup;
import util.Configuration;
import util.Constants;
import util.MersenneTwisterFast;
import java.lang.Math;

public class Sample {
    String capacitanceModel;
	//boolean lcapzunger, lcapdelerue; 
    String effectiveMedium;
    //boolean lemmg, lemla, lemlll, lempnone;
    String hoppingMechanism;
    //boolean lma, lmarcus; 
    String poissonSolver; 
    //boolean lpoissonnone, lpoissonewald, lpoissonnn;

    boolean perx, pery, perz, bimodal, twolayer, lcapacitance0, pennmodel = false;
    
    double marcusprefac2, jumpfreq, emass, hmass;
    double reorgenergy, bradii, capacitance,ligandlength, sourcewf, drainwf;
    double chemicalPotential = 0, V_prime;  // used for grand canonical ensemble

    public double temperature, voltage ;  
    public double ndist_thr, ediff_thr, dcout, beta, packingfraction;
    public int e_degeneracy, h_degeneracy, nbnd;
    public double cellx, celly, cellz, cellz_nm;
    public double rateOnSample = 0;
    public double systemEnergy = 0;
    long sampleCurrent;
    
    public int nelec   ;   // number of electrons
    public int nhole   ;   // number of holes
    public int nnanops ;   // number of nanoparticles
    int sample_number  ;   // store the nanoparticle file to open
    double thr; // percentage read from program
  
    double npdc, liganddc, metalPrefactor, FWHM;
    String filename, feature;
    
    
    //
    Nanoparticle[] nanoparticles;
    ArrayList<Nanoparticle> sources;
    ArrayList<Nanoparticle> drains;
    HashSet<Nanoparticle> currentUpdate = new HashSet<Nanoparticle>();
    HoppingEvent currentEvent, previousEvent, latestEvent;
    
    MersenneTwisterFast rng;
    public double simTime=0;

 
    public Sample(Map<String,Object> params){
    	
    	nelec = (int) params.get("nelec");
    	nhole = (int) params.get("nholes");
    	nnanops = (int) params.get("nnanops");
    	sample_number = (int) params.get("sample_no");
    	feature = (String) params.get("feature");
    	temperature = (double) params.get("temperature") * Constants.kelvintory;
    	thr = (double) params.get("thr");
    	sampleCurrent = 0;
    	
    	if(feature=="mobility"){
    		voltage = 30*Constants.kelvintory*0.1 * 250 / Constants.sqrt2;
    		System.out.println("Mobility run, voltage is "+voltage);
    	}
    	if(feature=="iv")
    		voltage = 30*Constants.kelvintory*((double)params.get("voltage_ratio"))*25/Constants.sqrt2;
    	
    	

    	loadConfiguration();        
   
		nanoparticles = Setup.setupNanoparticles(this);
    	/*System.out.println(nelec);
        System.out.println(nanoparticles[0].x/Constants.nmtobohr);
        System.out.println(nanoparticles[399].getCB1());
		System.out.println(nanoparticles[0].getCBoccupation()[0]);
		System.out.println(nanoparticles[0].getMAXocc()[0]);
		System.out.println(this.sources.size());
		System.out.println(this.drains.size());
		System.out.println(voltage);*/

		
        FWHM = Setup.getFWHM(nanoparticles, this);
        ediff_thr = FWHM * thr;
        dielectrics();
        //System.out.println(npdc);
        //System.out.println(packingfraction);
        set_selfenergy();
        buildNeighborList(false, true);
        
        
        /*
        int actual_nn = 0;
        
        for(Nanoparticle nanop : nanoparticles){
        	System.out.println(Arrays.toString(nanop.nearestNeighbors));
        	actual_nn += nanop.nearestNeighbors.length;
        }
        System.out.println("actual nn "+ actual_nn);
        */
        
        
        
        // throw electrons on to nanoparticles
        for(int i=0; i<nelec; i++)
        	throw_electron();
 
        
        //simulation();
        
        
        
    }
    
    
    
    private void loadConfiguration()  {
    	
    	// setting up the random number generator
        rng = new MersenneTwisterFast(Configuration.rngSeed);

    	
    	emass = Configuration.emass;
    	hmass = Configuration.hmass;
    	ligandlength = Configuration.ligandLength*Constants.nmtobohr;
    	capacitance = Configuration.capacitance*2.0/Constants.evtory; // used for zunger
    	ndist_thr = Configuration.distThr*Constants.nmtobohr;
    	liganddc = Configuration.ligandDC;
    	npdc = Configuration.npDC;
    	nbnd = Configuration.nBands;
    	e_degeneracy = Configuration.e_degeneracy;
    	h_degeneracy = Configuration.h_degeneracy;
    	
    	metalPrefactor = Configuration.metallicPrefactor;
    	
    	perx = Configuration.PERX;
    	pery = Configuration.PERY;
    	perz = Configuration.PERZ;
    	
    	sources = new ArrayList<Nanoparticle>();
    	drains = new ArrayList<Nanoparticle>();
    	
    	hoppingMechanism = Configuration.hoppingMechanism;
    	effectiveMedium = Configuration.effectiveMedium;
    	capacitanceModel = Configuration.capacitanceModel;
    	poissonSolver = Configuration.poissonSolver;
    	
    	lcapacitance0 = Configuration.lcapacitance0;
    	marcusprefac2 = Configuration.marcusprefac2*Constants.evtory*Constants.evtory;
    	jumpfreq = Configuration.jumpFreq*Constants.ry_ps;
    	
    	reorgenergy = Configuration.reorgenergy*Constants.evtory;
    	bradii = Configuration.bradii*Constants.nmtobohr;
    	
    	bimodal = Configuration.biModal;
    	twolayer = Configuration.twoLayer;
    	
    	if(!bimodal){
    		String prefix= "./data/nanoparticles/";
    		String middle = String.valueOf(nnanops)+"_"+Configuration.diameter;
    		String end = "nanoparticles"+String.valueOf(sample_number)+".inp";
    		filename = prefix + middle + end;
    	}
    	
		List<String> lines;
		try {
			lines = Files.readLines(new File(filename), Charsets.UTF_8);
			//System.out.println(lines);
			cellx = Double.valueOf(Arrays.asList(lines.get(3).split(",")).get(1))*Constants.nmtobohr;
			celly = Double.valueOf(Arrays.asList(lines.get(4).split(",")).get(1))*Constants.nmtobohr;
			cellz = Double.valueOf(Arrays.asList(lines.get(5).split(",")).get(1))*Constants.nmtobohr;
			cellz_nm = Double.valueOf(Arrays.asList(lines.get(5).split(",")).get(1));
			
			previousEvent = new HoppingEvent();
			currentEvent = new HoppingEvent();
			latestEvent = new HoppingEvent();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// calculate V_prime for Grand Canonical
        V_prime = (cellx*celly*cellz) / (Math.pow(2*Math.PI, 1.5) / Math.pow(Configuration.emass*temperature*Constants.k_boltzmann_ry, -1.5));
		System.out.println("V_prime is "+ V_prime+" "+cellx);

	}
    
    public double getElectronMass(){
    	return emass;
    }
    
    public String getFilename(){
    	return filename;
    }
    
    public double getFWHM() {
    	return FWHM;
		
	}

    private void dielectrics() {
		
    	double npVolume = 0;
    	// set dcin for each nanoparticle
    	for(int i=0; i<nanoparticles.length; i++){
    		npVolume += 4.0/3.0*Constants.pi*Math.pow(nanoparticles[i].radius, 3);
    		if(pennmodel == true)
    			nanoparticles[i].dcin = 1+(npdc-1)/(1+Math.pow((bradii/nanoparticles[i].diameter),2));
    		else
    			nanoparticles[i].dcin = npdc;
    	}
    	packingfraction = npVolume / (cellx*celly*cellz);
    	// set dcout for the sample
    	switch (effectiveMedium){
			case "mg":
				dcout = liganddc*(npdc*(1+2*packingfraction)-liganddc*(2*packingfraction-2))/(liganddc*(2+packingfraction)+npdc*(1-packingfraction));
				//dcout = sample.liganddc*(sample.npdc*(1+2*packingfraction)-sample.liganddc*(2*packingfraction-2))/(sample.liganddc*(2+packingfraction)+sample.npdc*(1-packingfraction))
				//System.out.println("running mg");
			break;

			default:
				dcout = liganddc;
			break;
		}	
	}

    private void set_selfenergy(){
		for(int i=0; i<nanoparticles.length; i++){
			
			switch (capacitanceModel) {
				case "delerue":
					//System.out.println(i);
					nanoparticles[i].selfenergy0 = (Constants.e2/nanoparticles[i].radius)*((0.5/dcout - 0.5/nanoparticles[i].dcin) + 0.47*(nanoparticles[i].dcin-dcout)/((nanoparticles[i].dcin+dcout)*nanoparticles[i].dcin)); 
					nanoparticles[i].selfenergy = Constants.e2/nanoparticles[i].radius*(1.0 / dcout + 0.79 /nanoparticles[i].dcin);
					nanoparticles[i].hselfenergy0 = Constants.e2/nanoparticles[i].radius*((0.5 /dcout-0.5 /nanoparticles[i].dcin)+0.47 /nanoparticles[i].dcin*(nanoparticles[i].dcin - dcout)/(nanoparticles[i].dcin + dcout));
					nanoparticles[i].hselfenergy = Constants.e2/nanoparticles[i].radius*(1.0 / dcout + 0.79 /nanoparticles[i].dcin);
					//System.out.println(nanoparticles[i].selfenergy0);
				break;
				

			case "zunger":
					nanoparticles[i].selfenergy0 = Constants.e2/(2.0 *capacitance*nanoparticles[i].radius);
					nanoparticles[i].selfenergy = Constants.e2/(2.0 * capacitance*nanoparticles[i].radius);
					nanoparticles[i].hselfenergy0 = Constants.e2/(2.0 *capacitance*nanoparticles[i].radius);
					nanoparticles[i].hselfenergy=Constants.e2/(2.0 *capacitance*nanoparticles[i].radius);
				break;
			}
		}
	}
    
    
    
    /**
	@param direction_selection, periodic in z
	@return nothing, modifies the sample neighborlist
	@throws what kind of exception does this method throw
	*/
    private void buildNeighborList(boolean directionSelection, boolean connected_z) {
    	int totalnn, totalcn, totalhcn;
    	double edgeDist, centerDist;
    	ArrayList<Nanoparticle> nearestNeighbors, closeNeighbors, closeHoleNeighbors;
    	
    	totalcn=0;
    	totalnn=0;
    	totalhcn=0;
    	
		for(int i=0; i<nanoparticles.length; i++){
			
			nearestNeighbors = new ArrayList<Nanoparticle>();
			closeNeighbors = new ArrayList<Nanoparticle>();
			closeHoleNeighbors = new ArrayList<Nanoparticle>();
			nanoparticles[i].edgeDistanceMap = new HashMap<Nanoparticle, Double>();
			nanoparticles[i].centerDistanceMap = new HashMap<Nanoparticle, Double>();
			
			for(int j=0; j<nanoparticles.length; j++){
				if(i != j){
					edgeDist = nanoparticles[i].npnpdistance(nanoparticles[j], this, connected_z, false);
					centerDist = nanoparticles[i].npnpdistance(nanoparticles[j], this, connected_z, true);
					
					// Add nearest neighbors
					if(edgeDist <= ndist_thr){
						nanoparticles[i].edgeDistanceMap.put(nanoparticles[j], edgeDist);
						nanoparticles[i].centerDistanceMap.put(nanoparticles[j], centerDist);
						nearestNeighbors.add(nanoparticles[j]);
						totalnn += 1;
						//add electron close neighbors
						if(Math.abs(nanoparticles[i].cbenergy[0]-nanoparticles[j].cbenergy[0]) < ediff_thr){
							closeNeighbors.add(nanoparticles[j]);
							totalcn += 1;
						}
						//add hole close neighbors
						if(Math.abs(nanoparticles[i].vbenergy[0]-nanoparticles[j].vbenergy[0]) < ediff_thr){
							closeHoleNeighbors.add(nanoparticles[j]);
							totalhcn += 1;
						}
					}
				}
			}
			nanoparticles[i].nearestNeighbors = new Nanoparticle[nearestNeighbors.size()];
			nanoparticles[i].nearestNeighbors = nearestNeighbors.toArray(nanoparticles[i].nearestNeighbors);
			
			nanoparticles[i].closeNeighbors = new Nanoparticle[closeNeighbors.size()];
			nanoparticles[i].closeNeighbors = closeNeighbors.toArray(nanoparticles[i].closeNeighbors);
			
			nanoparticles[i].holeCloseNeighbors = new Nanoparticle[closeHoleNeighbors.size()];
			nanoparticles[i].holeCloseNeighbors = closeHoleNeighbors.toArray(nanoparticles[i].holeCloseNeighbors);

		}
		System.out.println("total number of close neighbor is "+totalcn);
		System.out.println("total number of nearest neighbor is "+totalnn);
		//System.out.println(totalhcn);
	}
    
    private void throw_electron() {
		int NP, trials=0, maxTrials=100;
		Nanoparticle targetNP;
    	
    	// initialize an electron
    	Electron e = new Electron();
		
    	boolean success = false;
    	
    	while(success != true && trials < maxTrials){
        	// randomly select a NP
    		NP = rng.nextInt(nanoparticles.length);
    		targetNP = nanoparticles[NP];
    		//System.out.println("target NP "+targetNP+ " "+NP);
    		//System.out.println(targetNP.electronsOnNP[0]);
    		for(int i=0; i<nbnd; i++){
    			if(targetNP.occupationCB[i] < targetNP.occupationMAX[i]){
    				// put electron onto ith orbital on targetNP
    				e.setHost(targetNP, i);
    				//targetNP.electronsOnNP[i].add(e);
    				targetNP.add_electron(e, i);
    				success = true;
    			}
    		}
			trials++;
			
	    	//System.out.println(targetNP.electronsOnNP[0]);
			
    	}
    	
    	if(!success)
    		System.out.println("Not able to throw electron within max trials!");
	}
    
    private void throw_hole() {
		int NP, trials=0, maxTrials=100;
		Nanoparticle targetNP;
    	
    	// initialize an electron
    	Hole h = new Hole();
		
    	boolean success = false;
    	
    	while(success != true & trials < maxTrials){
        	// randomly select a NP
    		NP = rng.nextInt(nanoparticles.length);
    		targetNP = nanoparticles[NP];
    		for(int i=0; i<nbnd; i++){
    			if(targetNP.occupationVB[i] < targetNP.occupationMAX[i]){
    				// put electron onto ith orbital on targetNP
    				h.setHost(targetNP, i);
    				//targetNP.electronsOnNP[i].add(e);
    				targetNP.add_hole(h, i);
    				success = true;
    			}
    		}
			trials++;
	    	//System.out.println(targetNP.electronsOnNP[0]);
    	}
    	if(!success)
    		System.out.println("Not able to throw hole within max trials!");
	}
    
    // for electron hopping only at moment
    private void initializeEvents() {
		for(Nanoparticle nanops : nanoparticles){
			for(int band=0; band<nbnd; band++){
				for(Electron electron : nanops.electronsOnNP[band]){
					// look for events and calculate the system total rate
					//System.out.println(electron+" events before "+ electron.hoppings);
					rateOnSample += electron.lookForEvents(this);
					//System.out.println(electron+" events after "+ electron.hoppings+ " total rate "+rateOnSample);

				}
			}	
		}
	}

    
    // update events, double NP version.
    public void updateEvents(Nanoparticle NP1, Nanoparticle NP2) {
    	// zero current update cycle
    	////currentUpdate.clear();
		// update targetNP and its neighbors
    	NP1.updateEvents(true, this);
    	
    	NP2.updateEvents(true, this);
	}
    
    
    // only works with electron at the moment!!!
    public HoppingEvent searchHoppingEvent()  {
    	double targetRate = rateOnSample*rng.nextDouble();
    	double currentRate = 0.0 ;
    	
    	
    	//System.out.println(targetRate+" "+ rateOnSample);
    	
    	//latestEvent = new HoppingEvent();
    	
		// loop over all nanoparticles
		for(Nanoparticle nanop : nanoparticles){
			// check each band
			for(int band=0; band<nbnd; band++){
				// loop over electrons sitting on the band
				for(Electron e: nanop.electronsOnNP[band]){
					// finally loop over events associated with the electron
					for(HoppingEvent event : e.hoppings){
						// update latest event to return
						// add current event rate
						currentRate += event.rate;
						//System.out.println(e+" current rate "+currentRate+" target rate "+targetRate);

						if(currentRate>=targetRate){
    						latestEvent = event;
    						//System.out.println("found event");
    						//System.out.println("in search event "+event.targetNP.source +" " +event.sourceNP.drain);
							return latestEvent;
							}
						}
					}
				}
    		}
        //TODO note the print statement below was not originally commented out
    	//System.out.println(targetRate<currentRate);
    	if(latestEvent.type=="empty")
    		System.out.println("empty events!"); 
    	
    	//System.out.println(latestEvent.sourceNP+" "+latestEvent.targetNP);
    	return latestEvent;
	}

    
    public double executeEvent(HoppingEvent event, int i) {
    	
    	
    	double current=0;
    	
    	if(event.targetNP != previousEvent.sourceNP)
    		event.targetNP.hotness++;
    	
    	//System.out.println("executing event "+event+" hosting e is "+event.hostElectron);
    	Electron eMoving = event.hostElectron;
    	
    	//before move
    	
    	//System.out.println("before moving "+eMoving+" "+event.targetNP.electronsOnNP[event.targetOrbital]);

    	eMoving.move(event.sourceNP, event.sourceOrbital, event.targetNP, event.targetOrbital);
    	
    	//System.out.println("after moving "+event.targetNP.electronsOnNP[event.targetOrbital]);
    	
    	
    	// after move
    	
    	if(event.targetNP.drain && event.sourceNP.source){
    		//System.out.println("exe");
    		current = -1;
    		this.sampleCurrent -= 1;
    	}
    	
    	if(event.targetNP.source && event.sourceNP.drain){
    		current = 1;
    		this.sampleCurrent +=1 ;
        	//System.out.println(i);

    		//System.out.println("in exe exeeee "+ event.targetNP.source +" " +event.sourceNP.drain);
    	}
    	
    	
    	previousEvent = event;
    	return current;
	}
 
    
    public double simulation(){
    	
    	
    	
    	long l;
    	//int numberEvents;
    	Nanoparticle source, target;
    	
    	l = System.nanoTime();
    	
        initializeEvents();
    	
    	for(int i=0; i<Configuration.STEPS; i++){
    		
    		//System.out.println();
    		//System.out.println("step "+i);
            currentEvent =new HoppingEvent();
    		currentEvent = searchHoppingEvent();

    		source = currentEvent.sourceNP;
    		target = currentEvent.targetNP;
    		
    		executeEvent(currentEvent, i);
    		simTime+=-Math.log(rng.nextDouble())/rateOnSample*Constants.ry_ps; //This time is in picoseconds
    		updateEvents(source, target);
    		
    		
    		
    		if(i%200000==0){
    			
    			System.out.println(i);
    			source.add_electron_try(this);
    
    		}
    		
    		
    		
    		/*/debug section
    		System.out.println(currentEvent+" "+currentEvent.sourceNP+" "+currentEvent.targetNP);
    		numberEvents = 0;
    		for(Nanoparticle nanop : nanoparticles){
    			for(Electron e:nanop.electronsOnNP[0]){
    				numberEvents += e.hoppings.size();
    			}
    		}
    		System.out.println("number of events "+ numberEvents);
    		
    		if(i%20000==0){
    			
    			System.out.println(i);
    			//System.out.println(currentEvent.sourceNP);
    			System.out.println(sampleCurrent);
    		}
    		*/
    		
    		
    		
    		// end of debug section
    		

    	}
		System.out.println(sampleCurrent);

    	l = System.nanoTime() - l;
        System.out.println("iteration took " + l/1000000000 + "s");

        //(eleccurrent*cellz*bohrtonm*cellz*bohrtonm)*0.01*volt_ry/(time_ps*voltage*nelec) A snippet from the Cython Version.
        //return sampleCurrent;
        double mobility = sampleCurrent*cellz*cellz*Constants.bohrtonm*Constants.bohrtonm*.01*Constants.volt_ry/(simTime*voltage*nelec);
        return mobility;
    }
 
    
    

	public static void main(String[] args) {

		// Necessary parameters
		//(INT_t steps, INT_t sample_no, INT_t e_number, INT_t h_number, INT_t nanops_number, 
		//FLOAT_t large_ratio, FLOAT_t temp, FLOAT_t thr, str features, FLOAT_t voltage_ratio, output):
        Map<String, Object> params = new HashMap<>();
        
        params.put("nelec", 100);
    	params.put("nholes", 0);
    	params.put("nnanops", 400);
    	params.put("sample_no", 0);
    	params.put("feature", "mobility");
    	params.put("temperature", 80.0);
    	params.put("thr", 0.0);
        
        Sample newsample = new Sample(params);
        
        newsample.simulation();
        
        
        
        
        //System.out.println(newsample.getElectronMass());
        //System.out.println(newsample.getFWHM()*Constants.rytoev);
        //Nanoparticle[] nanoparticles;
        //nanoparticles = setupNanoparticles(newsample);
        
        //System.out.println(nanoparticles[0].x/Constants.nmtobohr);
        //System.out.println(nanoparticles[399].getCB1());
	}
}
    

