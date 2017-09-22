import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestServiceInstaller {

	public static boolean install(String srcPath) {
		File dstDir = new File(Main.getJarFile().getParentFile(), "TestService");
		if (new File(dstDir, "lib/TestService.jar").exists()) {
			System.err.println("install: warning: already installed");
			return false;
		}
		try {
			new File(dstDir, "lib").mkdirs();
			Files.copy(Paths.get(srcPath, "TestService.exe"), dstDir.toPath().resolve("TestService.exe"));
			Files.copy(Paths.get(srcPath, "TestService-install.bat"), dstDir.toPath().resolve("TestService-install.bat"));
			Files.copy(Paths.get(srcPath, "TestService-uninstall.bat"), dstDir.toPath().resolve("TestService-uninstall.bat"));
			Files.copy(Paths.get(srcPath, "lib", "TestService.jar"), dstDir.toPath().resolve("lib").resolve("TestService.jar"));
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("install: error: copy");
			return false;
		}
		if (Main.exec("cmd", "/c", new File(dstDir, "TestService-install.bat").getPath()) != 0) {
			System.err.println("install: error: TestService-install.bat");
			return false;
		}
		if (Main.exec(new File(dstDir, "TestService.exe").getPath(), "start") != 0) {
			System.err.println("install: error: TestService start");
			return false;
		}
		return true;
	}

	public static boolean uninstall() {
		File dstDir = new File(Main.getJarFile().getParentFile(), "TestService");
		if (!new File(dstDir, "lib/TestService.jar").exists()) {
			System.err.println("uninstall: info: not installed");
			return false;
		}
		if (Main.exec("cmd", "/c", new File(dstDir, "TestService-uninstall.bat").getPath()) != 0) {
			System.err.println("uninstall: error: TestService-uninstall.bat");
			return false;
		}
		new File(dstDir, "TestService.exe").delete();
		new File(dstDir, "TestServicew.exe").delete();
		new File(dstDir, "TestService-install.bat").delete();
		new File(dstDir, "TestService-uninstall.bat").delete();
		new File(dstDir, "TestService-install.bat").delete();
		new File(dstDir, "lib/TestService.jar").delete();
		new File(dstDir, "lib").delete();
		dstDir.delete();
		return true;
	}

	public static boolean disableService() {
		File dstDir = new File(Main.getJarFile().getParentFile(), "TestService");
		if (Main.exec(new File(dstDir, "TestService.exe").getPath(), "update", "--Startup=manual") != 0) {
			System.err.println("disableService: error: TestService update --Startup=manual");
			return false;
		}
		return true;
	}

}
