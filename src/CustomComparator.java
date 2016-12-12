import java.util.Comparator;

public class CustomComparator implements Comparator<Function> {
	@Override
	public int compare(Function o1, Function o2) {
		return o1.getWeighting().compareTo(o2.getWeighting());
	}
}
