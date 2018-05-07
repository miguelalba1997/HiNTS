
package classes;
import java.util.*;


public class Result {
    double mobility=1.0;
    double top_occupation=1.0;
    int id=1;
    double current=1.0;
    Map<String, Object> resultsMap = new HashMap<>();





    public Result(double mob, int id, double current){
        /* This is constructor for xxx

        Args:
          mob: the mobility calculated in the simulation, in [cm^2/(Vs)]
          id: sample id.
          current: electron current, unit double check.
         */
        this.mobility = mob;
        this.id = id;
        this.current = current;



        resultsMap.put("id", id);
        resultsMap.put("mobility", mobility);
        resultsMap.put("current", current);

    }


    public Result(double mob, int id, double current, double top_occupation){
        /* This is constructor for xxx

        Args:
          mob: the mobility calculated in the simulation, in [cm^2/(Vs)]
          id: sample id.
          current: electron current, unit double check.
          top_occupation: electrons in top layer over total electron number.
         */
        this.mobility = mob;
        this.top_occupation = top_occupation;
        this.id = id;
        this.current = current;



        resultsMap.put("id", id);
        resultsMap.put("mobility", mobility);
        resultsMap.put("current", current);
        resultsMap.put("top occupation", top_occupation);

    }

    public double getCurrent() {
        return current;
    }

    public double getTop_occupation() {
        return top_occupation;
    }

    public double getMobility() {
        return mobility;
    }

    public int getId() {
        return id;
    }

    public Map<String, Object> getResultsMap(){
        return resultsMap;
    }

}



