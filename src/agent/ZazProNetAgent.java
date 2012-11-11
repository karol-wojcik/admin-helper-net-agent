package agent;


public class ZazProNetAgent {
	
	public static void main(String[] args) {
		ClassExecutingTask executor = new ClassExecutingTask();
		System.out.println("Pingtest START");
		executor.runPingTest();
	}
}
