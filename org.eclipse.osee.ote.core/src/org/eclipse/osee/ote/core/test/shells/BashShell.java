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
package org.eclipse.osee.ote.core.test.shells;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.SocketException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.InputBufferThread;

public class BashShell {
	private static final int MAX_RESPONSE_TIME = 20000;

	private static final int ITERATION_TIME = 2000;

	private InputStream in;

	private PrintStream out;

	private InputBufferThread inputBuffer;

//	private String prompt = "$ ";

	/**
	 * Connects telnet to the specified ipAddress and port
	 * 
	 * @throws IOException
	 * @throws SocketException
	 */
	public BashShell() throws SocketException,
			IOException {
		String shell;
		if (Lib.isWindows()) {
			shell = "cmd.exe";
		} else {
			shell = "/bin/bash";
		}
		Process process;
		
		process = Runtime.getRuntime().exec(new String[] { shell });
		
		in = process.getInputStream();
		out = new PrintStream(process.getOutputStream());

		inputBuffer = new InputBufferThread(in);
		inputBuffer.start();

	}

	/**
	 * writes the command given to the output stream ( telnet )
	 * 
	 * @param string
	 *            The command to give
	 */
	public void write(String string) {
		out.println(string);
		out.flush();
	}

	/**
	 * Sits on the line, reading in characters, and waits for the expected
	 * output from telnet
	 * 
	 * @param string
	 *            The String this function will stop on and return
	 * @return The entire string seen up to finding the string provided
	 * @throws InterruptedException
	 */
	public synchronized String waitFor(String string)
			throws InterruptedException {

		int elapsedTime = 0;
		while (elapsedTime <= MAX_RESPONSE_TIME) {
			if (inputBuffer.contains(string, true) >= 0){
				break;
			}
			this.wait(ITERATION_TIME);
			elapsedTime += ITERATION_TIME;
		}
		if (elapsedTime > MAX_RESPONSE_TIME) {
			throw new InterruptedException("Waiting for '" + string
					+ "' took longer then " + MAX_RESPONSE_TIME
					+ " miliseconds.");
		}
		return inputBuffer.getBuffer();
	}

	/**
	 * Writes the command to telnet and waits for the normal command prompt
	 * 
	 * @param string
	 *            The command to issue
	 * @return Returns the whole buffer up to the prompt
	 * @throws InterruptedException
	 */
	public String sendCommand(String string, long wait) throws InterruptedException {
		
		write(string);
		return waitFor(wait);
	}

	private String waitFor(long time) throws InterruptedException {
		while( (System.currentTimeMillis() - inputBuffer.getLastRead()) < time){
			synchronized(this){
			this.wait(100);
			}
		}
		return inputBuffer.getBuffer();
	}

	/**
	 * disconnects from telnet
	 */
	public void disconnect() {
		write("ls");
		inputBuffer.stopOnNextRun(true);
		write("exit");
	}

//	private String getBuffer() {
//		return inputBuffer.getBuffer();
//	}
	
	public static void main(String[] args){
		BashShell shell;
		try {
			shell = new BashShell();
			String env = shell.sendCommand("env", 4000L);
			System.out.println(env);
			shell.disconnect();
		} catch (SocketException e) {
			
		} catch (IOException e) {
			
		} catch (InterruptedException e) {
			
		}
		
	}
}
