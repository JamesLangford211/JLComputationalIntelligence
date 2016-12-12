import java.util.Random;

public abstract class ExpressionPart<T> {
	protected T value; 
	
	public T getPart(){
		return value;
	}
	
	public void changePart(T value){
		this.value = value;
	}
	
	public void changeRandomly(){
		Character[] finalChars = {'+','-','*'};
		Random r = new Random();
		value = (T) finalChars[r.nextInt(3)];
	}
	
	public String toString(){
		if(value == null){
			return "Blank";
		}
		else
			return ""+ String.valueOf(value);
	}
}
