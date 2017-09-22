import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestServiceInstaller {

	public static void install(File srcDir, File dstDir) throws Exception {
		if (new File(dstDir, "lib/TestService.jar").exists())
			throw new Exception("already installed");
		new File(dstDir, "lib").mkdirs();
		Files.copy(Paths.get(srcDir.getPath(), "TestService.exe"), dstDir.toPath().resolve("TestService.exe"));
		Files.copy(Paths.get(srcDir.getPath(), "TestService-install.bat"), dstDir.toPath().resolve("TestService-install.bat"));
		Files.copy(Paths.get(srcDir.getPath(), "TestService-uninstall.bat"), dstDir.toPath().resolve("TestService-uninstall.bat"));
		Files.copy(Paths.get(srcDir.getPath(), "lib", "TestService.jar"), dstDir.toPath().resolve("lib").resolve("TestService.jar"));
		Main.exec("cmd", "/c", new File(dstDir, "TestService-install.bat").getPath());
		Main.exec(new File(dstDir, "TestService.exe").getPath(), "start");
	}

	public static void uninstall(File dstDir) throws Exception {
		if (!new File(dstDir, "lib/TestService.jar").exists())
			throw new Exception("not installed");
		Main.exec("cmd", "/c", new File(dstDir, "TestService-uninstall.bat").getPath());
		new File(dstDir, "TestService.exe").delete();
		new File(dstDir, "TestServicew.exe").delete();
		new File(dstDir, "TestService-install.bat").delete();
		new File(dstDir, "TestService-uninstall.bat").delete();
		new File(dstDir, "TestService-install.bat").delete();
		new File(dstDir, "lib/TestService.jar").delete();
		new File(dstDir, "lib").delete();
		dstDir.delete();
	}

	public static void disableService(File dstDir) throws Exception {
		Main.exec(new File(dstDir, "TestService.exe").getPath(), "update", "--Startup=manual");
	}

}
