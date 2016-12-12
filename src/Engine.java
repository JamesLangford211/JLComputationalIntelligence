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



public class Engine{
	
	private static final String TRAIN_URL = "src/cwk_train.csv";
	private static final int STARTING_POP = 100;
	private static double MUTATION_PROBABILITY = 0.3;
	private static final int ITERATIONS = 100;
	private static final int SELECTION_METHOD = 1;
	private static final int TOURNAMENT_METHOD = 1;
	
	private ArrayList<Row> dataSet = popDataTable(TRAIN_URL);
	private DoubleEvaluator evaluator = new DoubleEvaluator();
	
	public Engine(){
		ArrayList<Function> currentFunctions = initialFunctionList(STARTING_POP);
		for(int i = 0; i<ITERATIONS; i++){
			evaluate(currentFunctions);
			ArrayList<Function> sub = selectSubPopulation(SELECTION_METHOD,currentFunctions);
			ArrayList<Function> winners = tournament(TOURNAMENT_METHOD,sub);
			System.out.print("Iteration: "+i+ " Best is: " + winners.get(0).getWeighting());
			ArrayList<Function> mutantOffspring = mutatePop(clonePopulation(uniformCrossOver(winners.get(0),winners.get(1))));
			currentFunctions = mutantOffspring;
		}

	}
	
	public ArrayList<Function> clonePopulation(Function clone){
		ArrayList<Function> newPopulation = new ArrayList<Function>();
		for(int i = 0; i<STARTING_POP; i++){
			newPopulation.add(clone);
		}
		return newPopulation;
	}
	
	public Function uniformCrossOver(Function f1, Function f2){
		
		Random random = new Random();
		Double parentBoolean = 50.0;
		ArrayList<Operator> childArray = new ArrayList<Operator>();
		
		for(int i = 0; i<f1.getFunction().size();i++){
			Double dieRoll = random.nextDouble()*100;
			if(dieRoll < parentBoolean){
				childArray.add(f1.getFunction().get(i));
				parentBoolean -= 100 / (f1.getFunction().size());
			}
			else{
				childArray.add(f2.getFunction().get(i));
				parentBoolean += 100 / (f1.getFunction().size());
			}
		}
		
		Function child = new Function(childArray);
		return child;
	}
	
	public ArrayList<Function> selectSubPopulation(int method, ArrayList<Function> population){
		ArrayList<Function> subPopulation = new ArrayList<Function>();
		
		//Pick a random 20% of the population
		if(method == 1){
			Double d = population.size() * 0.2;
			int size = (int) d.doubleValue();
			for(int i = 0; i<size; i++){
				Random r = new Random();
				subPopulation.add(population.get(r.nextInt(size)));	
			}
		}
		return subPopulation;
	}
	
	public ArrayList<Function> tournament(int method, ArrayList<Function> subPopulation){
		ArrayList<Function> parents = new ArrayList<Function>();
		//Pick the two best (testing purposes mainly)
		if(method == 1){
			ArrayList<Function> ordered = new ArrayList<Function>();
			ordered = bestToWorst(subPopulation);
			System.out.println(ordered.toString().toString());
			parents.add(ordered.get(0));
			parents.add(ordered.get(1));
		}
		return parents;
	}
	
	public ArrayList<Function> bestToWorst(ArrayList<Function> functions){
		
		ArrayList<Function> clone = new ArrayList<Function>(functions);
		Collections.sort(clone, new CustomComparator());
		Collections.reverse(clone);
		return clone;	
	}
	
	public ArrayList<Function> mutatePop(ArrayList<Function> population){
		ArrayList<Function> mutatedPop = new ArrayList<Function>();
		for(int i = 0; i<population.size();i++){
			mutatedPop.add(population.get(i));
			Random r = new Random();
			double randomValue = r.nextDouble();
			if(randomValue < MUTATION_PROBABILITY){
					int random = r.nextInt(population.get(i).getFunction().size());
					population.get(i).getFunction().get(random).changeRandomly();
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
	
	public void evaluate(ArrayList<Function> functions){
		
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
					functions.get(i).setWeighting(fitness);
				}
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
	
	public void test(){
		ArrayList<Operator> o1 = new ArrayList<Operator>();
		ArrayList<Operator> o2 = new ArrayList<Operator>();
		
		o1.add(new Operator('+'));
		o1.add(new Operator('+'));
		o1.add(new Operator('+'));
		o1.add(new Operator('+'));
		
		o2.add(new Operator('-'));
		o2.add(new Operator('-'));
		o2.add(new Operator('-'));
		o2.add(new Operator('-'));
		
		Function f1 = new Function(o1);
		Function f2 = new Function(o2);
		
		//System.out.println(f1.toString());
		//System.out.println(f2.toString());
		
		//System.out.println(uniformCrossOver(f1,f2));
		
	}
	
}


