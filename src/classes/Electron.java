package classes;

import java.util.ArrayList;

import util.Configuration;
import util.Constants;

public class Electron extends Charge {
	public Electron(){
		charge = -1.0*Constants.sqrt2;
		mass = Configuration.emass;
		ratesOnCharge = 0.0;
		hoppings = new ArrayList<HoppingEvent>();
		//events = new RegularHopping[5];
	}
	
	
	
	public double lookForEvents(Sample sample) {
		/* This function looks for all the events that are seen by the electron instance.

		Args:
		  sample: a Sample object.

		Returns:
		  a double, which is the sum of rates on the electron instance in atomic units.
	    */

		//System.out.println(this.hostNP);
		double degeneracy;
		double netDistance;
		double newRate;
		HoppingEvent newEvent;
		Nanoparticle sourceNP = this.hostNP;
		int sourceOrbital = this.hostOrbital;
		
		this.hoppings.clear();
		
		// zero total rates on charge
		ratesOnCharge = 0 ;
		
		// add regular hoppings first
		for(Nanoparticle neighborNP : sourceNP.nearestNeighbors){
			for(int band=0; band<sample.nbnd; band++){
				if(neighborNP.occupationCB[band]<neighborNP.occupationMAX[band]){
					//HoppingEvent(Electron e, Nanoparticle target, Nanoparticle source, int to, int so, boolean intra)
					newEvent = new HoppingEvent(this, neighborNP, sourceNP, band, sourceOrbital, false);
					newRate = calculateRate(neighborNP, sourceNP, band, sourceOrbital, sample);
					newEvent.setRate(newRate);
					this.hoppings.add(newEvent);
					ratesOnCharge += newRate;
				}
			}
		}
		// add cluster hoppings next
		for(Nanoparticle neighborNP : sourceNP.closeNeighbors){
			for(int band=0; band<sample.nbnd; band++){
				// band is targetOrbital
				// allow hopping towards drain only
				netDistance = (neighborNP.z-sourceNP.z-sample.cellz*Math.round((neighborNP.z-sourceNP.z)/sample.cellz));
				if(netDistance>0 && neighborNP.occupationCB[band]<neighborNP.occupationMAX[band] ){
					
					newEvent = new HoppingEvent(this, neighborNP, sourceNP, band, sourceOrbital, true);
					degeneracy = (double) (neighborNP.occupationMAX[band] - neighborNP.occupationCB[band]) ; 
					newRate = -charge*degeneracy*(sample.metalPrefactor)*sample.voltage/sample.cellz*netDistance;
					newEvent.setRate(newRate);
					this.hoppings.add(newEvent);
					ratesOnCharge += newRate ;
				}
			}
		}
		return ratesOnCharge;
	}

	
	private double calculateCharging(Nanoparticle targetNP, Nanoparticle sourceNP, Sample sample){
	    double charging = 0.0;
    	// On-Site charging energy
    	//if(sample.lcapacitance0){
    		/*
    		This was the charging code before 5/3/2018!

    		charging += targetNP.selfenergy0 - sourceNP.selfenergy0 ;
    	
    	charging += targetNP.occupationTotalElectron*targetNP.selfenergy - (sourceNP.occupationTotalElectron-1)*sourceNP.selfenergy;
    	*/
    		double chargingBefore = (targetNP.calculateOnSiteCharging(targetNP.occupationTotalElectron)
					                 + sourceNP.calculateOnSiteCharging((sourceNP.occupationTotalElectron)));
    	    double chargingAfter = (targetNP.calculateOnSiteCharging(targetNP.occupationTotalElectron+1)
				                + sourceNP.calculateOnSiteCharging((sourceNP.occupationTotalElectron-1)));


    		return (chargingAfter-chargingBefore)/Configuration.screeningFactor;
    }
	
	private double calculateExciton(Nanoparticle targetNP, Nanoparticle sourceNP, Sample sample){
	    double exciton = 0.0;
    	
	    double excitonSP = Math.min(sourceNP.occupationTotalHoles, sourceNP.occupationTotalElectron);
	    double excitonTP = Math.min(targetNP.occupationTotalHoles, targetNP.occupationTotalElectron+1);
	    
	    exciton = (excitonTP>0) ? -targetNP.selfenergy0 : 0 ;
	    exciton += (excitonSP>0) ? sourceNP.selfenergy0 : 0 ; 
	    
	    excitonSP = Math.min(sourceNP.occupationTotalHoles, sourceNP.occupationTotalElectron-1);
	    excitonTP = Math.min(targetNP.occupationTotalHoles, targetNP.occupationTotalElectron);

	    exciton += -excitonTP*targetNP.selfenergy + excitonSP*sourceNP.selfenergy ;
	    
    	return exciton;
    }

	private double nearestNeighborPoisson(Nanoparticle targetNP, Nanoparticle sourceNP, Sample sample) {
		double nnPoisson=0;
		double ccdistance; 
	    // for the electron before hopping this comes with negative sign as this is subtracted
		for(Nanoparticle sourceNeighbor: sourceNP.nearestNeighbors){
			ccdistance = sourceNP.centerDistanceMap.get(sourceNeighbor);
			nnPoisson += -Constants.e2*(sourceNeighbor.occupationTotalElectron-sourceNeighbor.occupationTotalHoles) / sample.dcout / ccdistance;
		}
		// for the electron after hopping
		for(Nanoparticle targetNeighbor: targetNP.nearestNeighbors){
			ccdistance = targetNP.centerDistanceMap.get(targetNeighbor);
			
			if(targetNeighbor==sourceNP)
				nnPoisson += Constants.e2*(targetNeighbor.occupationTotalElectron - targetNeighbor.occupationTotalHoles -1) / sample.dcout / ccdistance;	
			else
				nnPoisson += Constants.e2*(targetNeighbor.occupationTotalElectron- targetNeighbor.occupationTotalHoles) / sample.dcout / ccdistance;
		}
		return nnPoisson;
	}

    private double calculateRate(Nanoparticle targetNP, Nanoparticle sourceNP, int targetOrbital, int sourceOrbital, Sample sample){
    	double energy_diff = 0.0;
    	double rate = 0, overlap;
    	double npdistance = targetNP.edgeDistanceMap.get(sourceNP);
    	//System.out.println("The np distance is" + npdistance);
    	// Kinetic energy difference
    	energy_diff += targetNP.cbenergy[targetOrbital] - sourceNP.cbenergy[sourceOrbital] ;
    	
    	// On-Site charging energy
    	energy_diff += calculateCharging(targetNP, sourceNP, sample);
    	
    	// Electron-hole exciton interaction
    	energy_diff += calculateExciton(targetNP, sourceNP, sample);
    	
    	// External potential
        energy_diff += -1.0*Constants.sqrt2*(sample.voltage/sample.cellz)*(targetNP.z-sourceNP.z-sample.cellz*Math.round((targetNP.z-sourceNP.z)/sample.cellz));

    	// Nearest neighbor interaction
        if(sample.poissonSolver=="nn")
        	energy_diff += nearestNeighborPoisson(targetNP, sourceNP, sample);
        
        overlap = Math.sqrt(-sample.emass*(sourceNP.cbenergy[sourceOrbital] + targetNP.cbenergy[targetOrbital]) / 2.0) ;

        // Two cases: Miller-Abrahms or Marcus
        switch (sample.hoppingMechanism) {
		case "ma":
			rate = sample.jumpfreq * Math.exp(-2.0*npdistance*overlap);
			if(energy_diff>0.0)
				//rate = rate*Math.exp(-energy_diff/(Constants.k_boltzmann_ry * sample.temperature));
				rate = rate*Math.exp(-energy_diff/(sample.temperature));
			break;

		case "marcus":
			rate = Constants.tpi*sample.marcusprefac2*Math.exp(-2.0 * npdistance * overlap) / Math.sqrt(Constants.fpi*sample.reorgenergy*sample.temperature);
	        rate = rate*Math.exp(-Math.pow((sample.reorgenergy + energy_diff), 2.0) / (4.0*sample.reorgenergy*sample.temperature));
			break;
		}
    	
    	return rate;
    }
    
    
    public void move(Nanoparticle sourceNP, int sourceBand, Nanoparticle destinationNP, int destinationBand) {
		
    	//System.out.println("moving electron "+this);
    	//System.out.println("destinationNP is "+destinationNP+", sourceNP is "+sourceNP);
		// remove from old NP first
    	sourceNP.remove_electron(this, sourceBand);
    	// add to the new NP
    	this.hostNP = destinationNP;
    	this.hostOrbital = destinationBand;
    	destinationNP.add_electron(this, destinationBand);
	}
    
    
    public double calculateEnergy() {
    	return 0;
		
	}


}
