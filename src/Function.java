import java.util.ArrayList;
import java.util.Arrays;

public class Function {
	
	ArrayList<Operator> function = new ArrayList<Operator>();
	
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

}
