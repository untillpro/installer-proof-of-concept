import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class TestService {

	private static Logger log = Logger.getLogger(TestService.class.getName());
	private static File homePath = new File(System.getProperty("user.dir"));

	public static void main(String[] args) {
		try {
			Handler handler = new FileHandler(new File(homePath, TestService.class.getSimpleName()+ ".log").getPath(), true);
			handler.setFormatter(new SimpleFormatter());
			log.addHandler(handler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
			return;
		}
		if (args.length > 0) {
			if ("start".equals(args[0])) {
				start();
			} else if ("stop".equals(args[0])) {
				stop();
			}
		}
	}

	private static boolean stop = false;

	private static void start() {
		log.info("start begin");
		while (!stop) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			File doExit = new File(homePath, "doStop");
			if (doExit.exists() && doExit.delete()) {
				log.info("doStop");
				stop = true;
			}
			log.info("running");
		}
		log.info("start end");
	}

	private static void stop() {
		log.info("stop");
		stop = true;
	}

}
