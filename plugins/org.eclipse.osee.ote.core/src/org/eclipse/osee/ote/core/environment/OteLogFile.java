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
package org.eclipse.osee.ote.core.environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.logging.ILoggerListener;

public class OteLogFile implements ILoggerListener {

	public Set<Logger> initializedLoggers;
	private FileOutputStream fos;
	private StringBuilder sb;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	public OteLogFile(File file) throws FileNotFoundException{
		fos = new FileOutputStream(file);
		sb = new StringBuilder();
	}
	
	public synchronized void log(String loggerName, Level level, String message, Throwable th) {
		try{
			sb.append(String.format("<record name=\"%s\" level=\"%s\" >\n", loggerName, level.getName()));
			sb.append("<Time>");
			sb.append(sdf.format(new Date()));
			sb.append("</Time>\n");
			sb.append("<message>\n");
			sb.append(message);
			sb.append("\n");
			sb.append("</message>\n");
			if(th != null){
				sb.append("<stacktrace>");
				writeStackTrace(sb, th);
				sb.append("</stacktrace>\n");
			}
			sb.append("</record>\n");
			fos.write(sb.toString().getBytes());
			fos.flush();
			sb.setLength(0);
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
	}
	
	private void writeStackTrace(StringBuilder sb, Throwable th){
		while(th != null){
			sb.append(th.getMessage());
			sb.append("\n");
			for(StackTraceElement el:th.getStackTrace()){
				sb.append(el.toString());
				sb.append("\n");
			}
			th = th.getCause();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		fos.close();
		super.finalize();
	}
	
}
