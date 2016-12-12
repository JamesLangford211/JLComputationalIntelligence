import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;
import com.fathzer.soft.javaluator.*;

public class Engine {
	
	private static final String TRAIN_URL = "src/cwk_train.csv";
	private static final int STARTING_POP = 5;
	private static double MUTATION_PROBABILITY = 0.3;
	private static final int ITERATIONS = 1;
	
	private ArrayList<Row> dataSet = popDataTable(TRAIN_URL);
	private DoubleEvaluator evaluator = new DoubleEvaluator();
	
	public Engine(){
		ArrayList<Function> currentFunctions = initialFunctionList(STARTING_POP);
		System.out.println(evaluate(currentFunctions).toString());
		
		currentFunctions = mutatePop(currentFunctions);
		System.out.println(evaluate(currentFunctions).toString());
	}
	
	public ArrayList<Function> mutatePop(ArrayList<Function> population){
		ArrayList<Function> mutatedPop = new ArrayList<Function>();
		for(int i = 0; i<population.size();i++){
			mutatedPop.add(population.get(i));
			for(int j = 0; j<population.get(i).getFunction().size(); j++){
				Random r = new Random();
				double randomValue = r.nextDouble();
				if(randomValue<=MUTATION_PROBABILITY){
					mutatedPop.get(i).getFunction().get(j).changeRandomly();
				}
				
			}
		}
		return mutatedPop;
	}
	
	public ArrayList<Row> popDataTable(String url){
		ArrayList<Row> dataTable = new ArrayList<Row>();
		Scanner scanner;
		try {
			scanner = new Scanner(new File(url));
			scanner.useDelimiter(",");
			
			while(scanner.hasNextLine()){
	        	String line = scanner.nextLine();
	        	String[] lineSplit = line.split(",");
	        	dataTable.add(new Row(lineSplit));  	
		}
		} catch(FileNotFoundException e){
		}		
		
		return dataTable;
	}
	
	public ArrayList<Function> initialFunctionList(int populationSize){
		ArrayList<Function> expressionList = new ArrayList<Function>();
		for(int index = 0; index<populationSize; index++){
			ArrayList<Operator> operators = new ArrayList<Operator>();
			
			for(int i = 0; i<12; i++){
				Operator op = new Operator();
				op.changeRandomly();
				operators.add(op);
			}
			Function expression = new Function(operators);
			expressionList.add(expression);
		}
		return expressionList;
	}
	
	public void printDataTable(){
		for(int i = 0; i<dataSet.size(); i++){
			System.out.println(dataSet.get(i).toString());
		}
	}
	
	public void printFunctions(ArrayList<Function> expressions){
		for(int i = 0; i<expressions.size(); i++){
			System.out.println(expressions.get(i).toString());
		}
	}
	
	public ArrayList<ArrayList<String>> evaluate(ArrayList<Function> functions){
		
		ArrayList<ArrayList<String>> iterationFitnesses = new ArrayList<ArrayList<String>>();
				for(int i = 0; i<functions.size(); i++){
					Double fitness = 0.0;
					// for every function
					for(int j = 0; j<dataSet.size(); j++){
						//for every data row
						//combine row of data and function
						ArrayList<ExpressionPart> expression = formExpression(functions.get(i), dataSet.get(j));
						Double result = evaluator.evaluate(listToString(formExpression(functions.get(i), dataSet.get(j))));
						fitness += weighting(result, dataSet.get(j).getExpected());	
					}
					fitness = fitness/dataSet.size();
					ArrayList<String> fitnessRow = new ArrayList<String>();
					fitnessRow.add(""+fitness);
					fitnessRow.add(functions.get(i).toString());
					iterationFitnesses.add(fitnessRow);
				}
					
		return iterationFitnesses;
	}
	
	public ArrayList<ExpressionPart> formExpression(Function ops, Row data){
		ArrayList<ExpressionPart> expression = new ArrayList<ExpressionPart>();
		//for every data but the last, add the operator to it
		for(int i = 0; i<data.getRow().size(); i++){
			expression.add(data.getRow().get(i));
			if(i<data.getRow().size()-1){
				expression.add(ops.getFunction().get(i));
			}
		}		
		return expression;
	}
	
	public String listToString(ArrayList<ExpressionPart> function){
		String returnStr = "";
		for(int i = 0; i<function.size(); i++){
			returnStr += function.get(i).toString() + " ";
		}
		
		return returnStr;
	}
	
	public Double score(Double number, Double expected){
		if(number>expected){
			return number - expected;
		}
		else if(expected<number){
			return 0.0 - (expected - number);
		}
		else{
			return 0.0;
		}
	}
	
	public Double weighting(Double number, Double expected){
		Double weighting = 100.0;
		if(number == expected){
			return weighting;
		}
		else{
			for(int i = 1; i<201; i++){
				Double range = (number*0.005) * i;
				if(!isInRange(number, expected, range)){
					weighting -= 1;
				}
			}
		}
		return weighting;
	}
	
	public Boolean isInRange(Double number, Double expected, Double range){
		Double lowerBound = expected-(range);
		Double upperBound = expected + range;
		if (lowerBound <= number && number <= upperBound){
			return true;
		}
		return false;
	}
	
}


