import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

	private static Logger log = Logger.getLogger(Main.class.getName());
	private static File jarFile;
	private static File dstDir;
	
	public static void main(String[] args) {
		jarFile = Main.getJarFile();
		if (jarFile == null || !jarFile.getPath().toLowerCase().endsWith(".jar")) {
			System.err.println("This program is started only from jar");
			System.exit(6);
		}
		dstDir = new File(jarFile.getParentFile(), "TestService");
		try {
			Handler handler = new FileHandler(jarFile.getPath() + ".%u.log", true);
			handler.setFormatter(new SimpleFormatter());
			log.addHandler(handler);
		} catch (SecurityException | IOException e) {
			e.printStackTrace();
			System.exit(3);
		}
		String srcPath = args.length > 1 ? args[1]
				: new File(jarFile.getParentFile().getParentFile().getParentFile(), "TestService/build/windows-service").getPath();
		try {
			if (args.length > 0) {
				log.info(args[0]);
				if ("install".equals(args[0])) {
					install(srcPath);
				} else if ("uninstall".equals(args[0])) {
					uninstall();
				} else if ("upgrade".equals(args[0])) {
					upgrade(srcPath);
				} else if ("rebootAndUpgrade".equals(args[0])) {
					rebootAndUpgrade(srcPath);
				} else if ("upgradeAfterReboot".equals(args[0])) {
					upgradeAfterReboot(srcPath);
				} else
					printUsage();
			} else
				printUsage();
		} catch (Exception e) {
			log.log(Level.SEVERE, args[0], e);
			System.exit(2);
		}
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("\t java -jar " + jarFile.getName() + " install|uninstall|upgrade|rebootAndUpgrade [srcPath]");
		System.exit(1);
	}

	private static void install(String srcPath) throws Exception {
		TestServiceInstaller.install(new File(srcPath), dstDir);
	}

	private static void uninstall() throws Exception {
		TestServiceInstaller.uninstall(dstDir);
	}

	private static void upgrade(String srcPath) throws Exception {
		TestServiceInstaller.uninstall(dstDir);
		TestServiceInstaller.install(new File(srcPath), dstDir);
	}

	private static void rebootAndUpgrade(String srcPath) throws Exception {
		// TODO check for elevated
		TestServiceInstaller.disableService(dstDir);
		exec("schtasks", "/Create", "/TN", "TestTask",
				"/TR", "java -jar \\\"" + jarFile.getPath() + "\\\" upgradeAfterReboot \\\"" + srcPath + "\\\"",
				"/SC", "ONSTART", "/RU", "SYSTEM");
		exec("shutdown", "-r");
	}

	private static void upgradeAfterReboot(String srcPath) throws Exception {
		exec("schtasks", "/Delete", "/F", "/TN", "TestTask");
		upgrade(srcPath);
	}

	public static File getJarFile() {
		try {
			return new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void exec(String... command) throws Exception {
		ProcessBuilder pb = new ProcessBuilder(command)
				.inheritIO();
		Process p = pb.start();
		int res = p.waitFor();
		if (res != 0)
			throw new Exception(String.format("%s: &d", command.toString(), res));
	}
}
