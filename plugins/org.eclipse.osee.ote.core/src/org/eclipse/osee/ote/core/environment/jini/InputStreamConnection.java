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
package org.eclipse.osee.ote.core.environment.jini;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

public class InputStreamConnection extends PipedInputStream {

	private PrintWriter writer;
	public InputStreamConnection() {
		PipedOutputStream piped = new PipedOutputStream();
		writer = new PrintWriter(piped);
		try {
			this.connect(piped);
		} catch (IOException e) {
			OseeLog.log(TestEnvironment.class, Level.SEVERE, e);
		}
	}
	
	public InputStreamConnection(OutputStream output) {
		writer = new PrintWriter(output);				
	}
	
	public void write(String string){
		writer.println(string);
		writer.flush();
	}
}
