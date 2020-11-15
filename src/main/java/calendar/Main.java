// need this for some stupid reason with Maven shade
// apparently the main class cant extend application
// this just calls Display's main function

public class Main {
	public static void main(String [] args) {
		Display.main(args);
	}
}
