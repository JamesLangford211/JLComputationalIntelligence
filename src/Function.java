import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class Function implements Comparator<Function>{
	
	ArrayList<Operator> function = new ArrayList<Operator>();
	Double weighting = 100.0;
	Double score = 0.0;
	
	public Function(ArrayList<Operator> exp){ 
		for(int i = 0; i<exp.size();i++){
			function.add(new Operator(exp.get(i).toString().charAt(0)));
		}
	}
	
	public ArrayList<Operator> getFunction(){
		return function;
	}
	
	public String toString(){
		String str = "";
			for(int i = 0; i<function.size(); i++){
				str += function.get(i).toString() + "";
			}
		return str;
	}
	
	public Double getWeighting(){
		return weighting;
	}
	
	public void setWeighting(Double w){
		weighting = w;
	}
	
	public Double getScore(){
		return score;
	}
	
	public void setScore(Double s){
		score = s;
	}

	@Override
	public int compare(Function o1, Function o2) {
		return o1.getWeighting().compareTo(o2.getWeighting());
	}

	

}
