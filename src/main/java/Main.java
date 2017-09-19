public class Main {

	public static void main(String[] args) {
		if (args.length > 0) {
			if ("install".equals(args[0])) {
				install();
			} else if ("uninstall".equals(args[0])) {
				uninstall();
			} else if ("upgrade".equals(args[0])) {
				upgrade();
			} else if ("reboot&upgrade".equals(args[0])) {
				rebootAndUpgrade();
			} else if ("upgradeAfterReboot".equals(args[0])) {
				upgradeAfterReboot();
			} else
				printUsage();
		} else
			printUsage();
	}

	private static void printUsage() {
		System.out.println("Usage:");
		System.out.println("\t" + Main.class.getSimpleName() + " install|uninstall|upgrade|reboot&upgrade");
		System.exit(1);
	}

	private static void install() {
		if (!TestServiceInstaller.install())
			System.exit(2);
	}

	private static void uninstall() {
		if (!TestServiceInstaller.uninstall())
			System.exit(2);
	}

	private static void upgrade() {
		if (!TestServiceInstaller.uninstall())
			System.exit(2);
		if (!TestServiceInstaller.install())
			System.exit(2);
	}

	private static void rebootAndUpgrade() {
		TestServiceInstaller.disableService();
		// TODO create task: schtasks /Create /TN TestTask /TR "java -jar \"<jarPath>\" upgradeAfterReboot" /SC ONSTART /RU SYSTEM
		// TODO reboot
	}

	private static void upgradeAfterReboot() {
		// TODO remove task: schtasks /Delete /F /TN TestTask
		if (!TestServiceInstaller.uninstall())
			System.exit(2);
		if (!TestServiceInstaller.install())
			System.exit(2);
	}

}
