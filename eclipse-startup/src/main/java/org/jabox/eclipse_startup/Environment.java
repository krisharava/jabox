package org.jabox.eclipse_startup;

import java.io.File;

public class Environment {

	public static String getBaseDir() {
		return getHomeDir() + File.separatorChar + ".jabox" + File.separatorChar;
	}

	private static String getHomeDir() {
		return System.getProperty("user.home");
	}
}