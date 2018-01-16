package classes;

import java.util.ArrayList;

public class Charge {
    
	Nanoparticle hostNP;
    int hostOrbital;
    
    double charge;
    double mass;
    double ratesOnCharge;
    Event[] events;
    public ArrayList<HoppingEvent> hoppings;
    
    
    public void setHost(Nanoparticle host, int orbital){
    	hostNP = host;
    	hostOrbital = orbital;
    }
    
    public Charge clone() {
    	return this;
		
	}

}

