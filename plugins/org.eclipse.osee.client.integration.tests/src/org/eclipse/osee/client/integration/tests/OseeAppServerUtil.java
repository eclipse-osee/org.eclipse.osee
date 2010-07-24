/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.InputBufferThread;

/**
 * @author Roberto E. Escobar
 */
public class OseeAppServerUtil {
	private static final String OSGI_PROMPT = "osgi> ";
	private static final String EQUINOX_LAUNCHER = "org.eclipse.equinox.launcher_";
	private static final String DERBY_TEST_ID = "DerbyTestId";
	private static final String DERBY_CONNECTION_FILE_NAME = "derbyTestConnection.xml";
	private static final String SERVER_EXEC_FILENAME = "testAppServerExec%s";

	public static interface ServerConfig {
		String getJavaHome();

		String getServerHome();

		int getServerPort();

		String getServerBinaryDataPath();
	}

	private final ServerConfig config;
	private Process process;
	private InputBufferThread inputBuffer;
	private OutputStream outputStream;
	private final String charSet;

	public OseeAppServerUtil(ServerConfig config) {
		this.config = config;
		this.charSet = "utf-8";
	}

	public int getDerbyPort() {
		return config.getServerPort() - 1;
	}

	public String getConnectionId() {
		return DERBY_TEST_ID;
	}

	public File getConnectionFile() {
		return new File(config.getServerHome(), DERBY_CONNECTION_FILE_NAME);
	}

	public String getJavaExec(String javaHome) {
		String javaExec =
					String.format("\"%s%sbin%sjava%s\"", javaHome, File.separator, File.separator,
								Lib.isWindows() ? ".exe" : "");
		return javaExec;
	}

	public String getEquinoxLauncher() throws OseeCoreException {
		File pluginFolder = new File(config.getServerHome(), "plugins");
		File[] files = pluginFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return Strings.isValid(name) && name.startsWith(EQUINOX_LAUNCHER);
			}
		});
		if (files.length != 1) {
			throw new OseeStateException(String.format("Unexpected equinox launcher matches - found %s", files.length));
		}
		return files[0].getAbsolutePath();
	}

	private List<String> getLaunchCmds() throws OseeCoreException {
		List<String> cmds = new ArrayList<String>();
		cmds.add(getJavaExec(config.getJavaHome()));
		cmds.add(String.format("-Dorg.osgi.service.http.port=%s", config.getServerPort()));
		cmds.add(String.format("-Dosee.db.connection.id=\"%s\"", "derby")); //getConnectionId()));
		cmds.add(String.format("-Dosee.derby.server=127.0.0.1:%s", getDerbyPort()));
		cmds.add(String.format("-Dosee.connection.info.uri=\"%s\"", getConnectionFile().getAbsolutePath()));
		cmds.add(String.format("-Dosee.application.server.data=\"%s\"", config.getServerBinaryDataPath()));
		cmds.add("-Dosee.check.tag.queue.on.startup=false");
		cmds.add("-jar");
		cmds.add(String.format("\"%s\"", getEquinoxLauncher()));
		cmds.add("-console");
		cmds.add("-consoleLog");
		return cmds;
	}

	public String getAppServerExec() {
		return String.format(SERVER_EXEC_FILENAME, Lib.isWindows() ? ".bat" : ".sh");
	}

	public void start() throws OseeCoreException {
		writeConnectionFile(getConnectionFile(), getConnectionId(), getDerbyPort());

		File workingDirectory = new File(config.getServerHome());

		String fileName = getAppServerExec();
		File executable = new File(workingDirectory, fileName);
		try {
			Lib.writeStringToFile(Collections.toString(" ", getLaunchCmds()), executable);
		} catch (IOException ex) {
			OseeExceptions.wrapAndThrow(ex);
		}
		executable.setExecutable(true);

		ProcessBuilder processBuilder = new ProcessBuilder(executable.getAbsolutePath());
		processBuilder.directory(workingDirectory);
		processBuilder.redirectErrorStream(true);

		System.out.println("Working Directory: " + processBuilder.directory().getAbsolutePath());
		System.out.println("Execute: " + processBuilder.command());
		try {
			process = processBuilder.start();
		} catch (IOException ex) {
			OseeExceptions.wrapAndThrow(ex);
		}

		outputStream = process.getOutputStream();

		inputBuffer = new InputBufferThread(process.getInputStream());
		inputBuffer.start();
		wait(1500);
		waitFor("Registered servlet", 30000);
		waitFor("--------- Derby Network Server Information --------", 30000);
		wait(1000);
		inputBuffer.suspend();
		inputBuffer.clear();
	}

	private void wait(int waitTime) throws OseeCoreException {
		try {
			Thread.sleep(waitTime);
		} catch (InterruptedException ex) {
			OseeExceptions.wrapAndThrow(ex);
		}
	}

	private synchronized void waitFor(String data, int waitTime) throws OseeCoreException {
		int value = -1;
		try {
			value = inputBuffer.waitFor(data, true, waitTime);
		} catch (InterruptedException ex) {
			OseeExceptions.wrapAndThrow(ex);
		}
		if (value < 0) {
			throw new OseeStateException("Waiting for '" + data + "' took longer then " + waitTime + " miliseconds.");
		}
	}

	public synchronized void sendCommand(String data) throws OseeCoreException {
		write(data);
		waitFor(OSGI_PROMPT, 10000);
	}

	private synchronized void write(String data) throws OseeCoreException {
		inputBuffer.clear();
		try {
			for (byte b : data.getBytes(charSet)) {
				outputStream.write(b);
				outputStream.flush();
				Thread.sleep(10);
			}
			outputStream.write(getNewline());
			outputStream.flush();
		} catch (Exception ex) {
			OseeExceptions.wrapAndThrow(ex);
		}
	}

	private byte[] getNewline() throws UnsupportedEncodingException {
		return "\n".getBytes(charSet);
	}

	private void shutdown() throws OseeCoreException {
		inputBuffer.resume();
		inputBuffer.clear();
		write("exit");
		//		sendCommand("osee_shutdown -osee_only");
		//		waitFor("De-registering servlet", 10000);
		try {
			inputBuffer.stopNow();
		} catch (InterruptedException ex) {
			OseeExceptions.wrapAndThrow(ex);
		}
	}

	public void stop() throws OseeCoreException {
		try {
			shutdown();
		} finally {
			if (process != null) {
				process.destroy();
			}
		}
	}

	private void writeConnectionFile(File connectionFile, String connectionId, int port) throws OseeCoreException {
		File parentFile = connectionFile.getParentFile();
		if (parentFile != null) {
			parentFile.mkdirs();
		}
		DerbyDbConnectionFileWriter writer = new DerbyDbConnectionFileWriter();
		writer.write(connectionFile, connectionId, port);
	}

	private static final class DerbyDbConnectionFileWriter {
		private static final String DB_INFO_START = "<DbConnection>\n" + //
		"<DatabaseInfo id=\"DerbyServer\">\n" + //
		"<DatabaseHome key=\"#DBHOME#\" />\n" + //
		"<DatabaseName key=\"#DBNAME#\" value=\"DerbyDatabase\" />\n" + //
		"<DatabaseType key=\"#TYPE#\" value=\"derby\" />\n" + //
		"<Prefix key=\"#PREFIX#\" value=\"jdbc:derby\" />\n" + //
		"<UserName key=\"#USERNAME#\" value=\"osee\" />\n" + //
		"<Password key=\"#PASSWORD#\" value=\"oseeadmin\" />\n" + //
		"<Host key=\"#HOST#\" value=\"@AvailableDbServices.hostAddress\" />\n" + //
		"<Port key=\"#PORT#\" value=\"@AvailableDbServices.port\" />\n" + //
		"</DatabaseInfo>\n\n" + "<ConnectionDescription id=\"NetDerbyClient\">\n" + //
		"<Driver>org.apache.derby.jdbc.ClientDriver</Driver>\n" + //
		"<Url>#PREFIX#://#HOST#:#PORT#/#DBHOME##DBNAME#;</Url>\n" + //
		"<UrlAttributes>\n" + //
		"<Entry>create=true</Entry>\n" + //
		"</UrlAttributes>\n" + //
		"</ConnectionDescription>\n" + //
		"<AvailableDbServices>";

		private static final String SERVER_CONFIG =
					"<Server id=\"%s\" dbInfo=\"DerbyServer\" hostAddress=\"127.0.0.1\" port=\"%s\" connectsWith=\"NetDerbyClient\" />";

		private static final String DB_INFO_END = "</AvailableDbServices>\n</DbConnection>";

		public void write(File file, String connectionId, int derbyServerPort) throws OseeCoreException {
			Writer writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(file));
				writer.write(DB_INFO_START);
				writer.write(String.format(SERVER_CONFIG, connectionId, derbyServerPort));
				writer.write(DB_INFO_END);
			} catch (IOException ex) {
				OseeExceptions.wrapAndThrow(ex);
			} finally {
				Lib.close(writer);
			}
		}
	}
}
