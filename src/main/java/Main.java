import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {

	public static void main(String[] args) {
		String srcPath = args.length > 1 ? args[1]
				: new File(getJarFile().getParentFile().getParentFile().getParentFile(), "TestService/build/windows-service").getPath();
		if (args.length > 0) {
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
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("\t java -jar " + getJarFile().getName() + " install|uninstall|upgrade|rebootAndUpgrade [srcPath]");
		System.exit(1);
	}

	private static void install(String srcPath) {
		if (!TestServiceInstaller.install(srcPath)) {
			System.err.println("Error: install");
			System.exit(2);
		}
	}

	private static void uninstall() {
		if (!TestServiceInstaller.uninstall()) {
			System.err.println("Error: uninstall");
			System.exit(3);
		}
	}

	private static void upgrade(String srcPath) {
		if (!TestServiceInstaller.uninstall()) {
			System.err.println("Error: upgrade (uninstall)");
			System.exit(4);
		}
		// TODO upgrade
		if (!TestServiceInstaller.install(srcPath)) {
			System.err.println("Error: upgrade (install)");
			System.exit(5);
		}
	}

	private static void rebootAndUpgrade(String srcPath) {
		String jarPath = getJarFile().getPath();
		if (jarPath == null || !jarPath.toLowerCase().endsWith(".jar")) {
			System.err.println("Error: this command is available only from jar");
			System.exit(6);
		}
		// TODO check for elevated
		// disable service
		if (!TestServiceInstaller.disableService()) {
			System.err.println("Error: disableService");
			System.exit(7);
		}
		// create task
		int res = exec("schtasks", "/Create", "/TN", "TestTask",
				"/TR", "java -jar \\\"" + jarPath + "\\\" upgradeAfterReboot",
				"/SC", "ONSTART", "/RU", "SYSTEM");
		if (res != 0) {
			System.err.println(String.format("Error: create task (%d)", res));
			System.exit(8);
		}
		// reboot
		res = exec("shutdown", "-r");
		if (res != 0) {
			System.err.println(String.format("Error: reboot (%d)", res));
			System.exit(9);
		}
	}

	private static void upgradeAfterReboot(String srcPath) {
		// delete task
		int res = exec("schtasks", "/Delete", "/F", "/TN", "TestTask");
		if (res != 0) {
			System.err.println(String.format("Error: delete task (%d)", res));
			System.exit(10);
		}
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

	public static int exec(String... command) {
		ProcessBuilder pb = new ProcessBuilder(command)
				.inheritIO();
		try {
			Process p = pb.start();
			return p.waitFor();
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
