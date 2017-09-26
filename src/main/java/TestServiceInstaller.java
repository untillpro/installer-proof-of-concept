import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestServiceInstaller {

	public static void install(File srcDir, File dstDir) throws Exception {
		if (new File(dstDir, "lib/installer-proof-of-concept.jar").exists())
			throw new Exception("already installed");
		new File(dstDir, "lib").mkdirs();
		Files.copy(Paths.get(srcDir.getPath(), "installer-proof-of-concept.exe"), dstDir.toPath().resolve("installer-proof-of-concept.exe"));
		Files.copy(Paths.get(srcDir.getPath(), "installer-proof-of-concept-install.bat"), dstDir.toPath().resolve("installer-proof-of-concept-install.bat"));
		Files.copy(Paths.get(srcDir.getPath(), "installer-proof-of-concept-uninstall.bat"), dstDir.toPath().resolve("installer-proof-of-concept-uninstall.bat"));
		Files.copy(Paths.get(srcDir.getPath(), "lib", "installer-proof-of-concept.jar"), dstDir.toPath().resolve("lib").resolve("installer-proof-of-concept.jar"));
		Installer.exec("cmd", "/c", new File(dstDir, "installer-proof-of-concept-install.bat").getPath());
		Installer.exec(new File(dstDir, "installer-proof-of-concept.exe").getPath(), "start");
	}

	public static void uninstall(File dstDir) throws Exception {
		if (!new File(dstDir, "lib/installer-proof-of-concept.jar").exists())
			throw new Exception("not installed");
		Installer.exec("cmd", "/c", new File(dstDir, "installer-proof-of-concept-uninstall.bat").getPath());
		new File(dstDir, "installer-proof-of-concept.exe").delete();
		new File(dstDir, "installer-proof-of-conceptw.exe").delete();
		new File(dstDir, "installer-proof-of-concept-install.bat").delete();
		new File(dstDir, "installer-proof-of-concept-uninstall.bat").delete();
		new File(dstDir, "installer-proof-of-concept-install.bat").delete();
		new File(dstDir, "lib/installer-proof-of-concept.jar").delete();
		new File(dstDir, "lib").delete();
		dstDir.delete();
	}

	public static void disableService(File dstDir) throws Exception {
		Installer.exec(new File(dstDir, "installer-proof-of-concept.exe").getPath(), "update", "--Startup=manual");
	}

}
