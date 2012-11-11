package agent;

import static java.util.concurrent.TimeUnit.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class ClassExecutingTask {
	private final ScheduledExecutorService scheduler =
       Executors.newScheduledThreadPool(1);
	
	public void runPingTest() {
		Properties prop = new Properties();
		InputStream in = this.getClass().getResourceAsStream("config.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long pingPeriod;
		
		final Runnable pinger = new Runnable() {
			public void run() {
				
				
				System.out.println();
				System.out.println("====================== New test below: " + new Date() + " ======================");
				Ping ping = new Ping();
				ping.testPing();
			}
		};
		pingPeriod = Long.parseLong(prop.getProperty("PING_TEST_PERIOD_SECONDS").trim());
		final ScheduledFuture<?> pingerHandle = scheduler.scheduleAtFixedRate(pinger, 0,  pingPeriod, SECONDS);
//		scheduler.schedule(new Runnable() {public void run () {pingerHandle.cancel(true);}}, 10, SECONDS);
	}
	
    public void beepForAnHour() {
        final Runnable beeper = new Runnable() {
                public void run() { System.out.println("beep"); }
            };
        final ScheduledFuture<?> beeperHandle =
            scheduler.scheduleAtFixedRate(beeper, 10, 10, SECONDS);
        scheduler.schedule(new Runnable() {
                public void run() { beeperHandle.cancel(true); }
            }, 60 * 60, SECONDS);
    }
}
