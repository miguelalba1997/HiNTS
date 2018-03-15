
package classes;
import java.util.*;

public class Result {

    //int electronindex;
    //int holeindex  ;
    //int chargeindex ;
    //int sourceparticle;
    //int sourceorbital  ;
    //int targetparticle;
    //int targetorbital  ;
    double mobility=1.0;
    double top_occupation=1.0;
    int id=1;
    double current=1.0;

    Map <String,Object> resultMap = new HashMap<>();

    public Result(){
        resultMap.put("mobility", mobility);
        resultMap.put("top occupation", top_occupation);
        resultMap.put("id", id);
        resultMap.put("current", current);

    }

    public Result(double mob, double occu, int id, double curr){
        resultMap.put("mobility", mob);
        resultMap.put("top occupation", occu);
        resultMap.put("id", id);
        resultMap.put("current", curr);
    }

}



