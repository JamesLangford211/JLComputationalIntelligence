import java.util.ArrayList;
import java.util.Arrays;

public class Row {
	
	ArrayList<Operand> data = new ArrayList<Operand>();
	private Double expected = 0.0;
	
	public Row(String[] row){
		ArrayList<String> rowList = new ArrayList<String>(Arrays.asList(row)); 
		expected = Double.parseDouble(rowList.get(0));
    	rowList.remove(0);
		for(int i = 0; i<rowList.size();i++){
			data.add(new Operand(Double.parseDouble(rowList.get(0))));
		}
	}
	
	public ArrayList<Operand> getRow(){
		return data;
	}
	
	public Double getExpected(){
		return expected;
	}
	
	public String toString(){
		String str = "Expected Value: "+expected+" \n Data: [";
			for(int i = 0; i<data.size(); i++){
				str += data.get(i).toString()+", ";
			}		
		return str;
	}
	

}
