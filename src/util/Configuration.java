package util;

//import com.sun.org.apache.bcel.internal.generic.NEW;

public class Configuration {
	
	// System
	public static final int STEPS = 500000;
	public static final double ligandDC = 2.0; // ligand dc
	public static final double npDC = 22.0; //bulk dielectric constant
	public static final double ligandLength = 0.5 ; // in nm
	public static final double emass = 0.05/2.0; //effective mass for electrons 
	public static final double hmass = 0.05/2.0; //effective mass for holes 
	public static final String hoppingMechanism = "ma"; // 'ma' : miller-abrahams, 'marcus' : marcus
	public static final String capacitanceModel = "delerue"; //"delerue" or "zunger"
	public static final String effectiveMedium = "mg";
	public static final String poissonSolver = "none";
	public static final int[] rngSeed = new int[]{1,2,3,4};
	
	// Lattice
	public static final boolean PERX = true; //whether the structure is periodic in hopping in x
	public static final boolean PERY = true; //whether the structure is periodic in hopping in y
	public static final boolean PERZ = true; //whether the structure is periodic in hopping in z
		
	// NP
	public static final double distThr = 1.1; //TODO WAS 1.27
	public static final double bradii = 47.0;
	public static final double reorgenergy = 0.05;
	public static final double jumpFreq = 1.0;
	public static final double marcusprefac2 = 1.0;
	public static final double capacitance = 0.05; //capacitance is used if capacitance_model = 'zunger'
	public static final int nBands = 1;
	public static final double metallicPrefactor = 1.0;
	public static final String diameter = "6.6nm/";
	public static final int e_degeneracy = 8;
	public static final int h_degeneracy = 8;
	
	// Boolean control
	public static final boolean twoLayer = false;
	public static final boolean biModal = false;
	public static final boolean pennModel = false;
	public static final boolean lcapacitance0 = true; //whether to add polarization term
	
	
}
