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
package org.eclipse.osee.framework.server.admin.management;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.database.core.DatabaseInfoManager;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.server.admin.BaseServerCommand;
import org.eclipse.osee.framework.server.admin.internal.Activator;
import org.eclipse.osgi.framework.console.CommandInterpreter;

/**
 * @author Roberto E. Escobar
 */
class ServerStats extends BaseServerCommand {

	protected ServerStats(CommandInterpreter ci) {
		super("Server Stats", ci);
	}

	@Override
	protected void doCommandWork(IProgressMonitor monitor) throws Exception {
		IApplicationServerManager manager = Activator.getInstance().getApplicationServerManager();
		ISessionManager sessionManager = Activator.getInstance().getSessionManager();

		StringBuffer buffer = new StringBuffer();
		buffer.append("\n----------------------------------------------\n");
		buffer.append("                  Server Stats                \n");
		buffer.append("----------------------------------------------\n");

		buffer.append(String.format("Server:[%s:%s]\n", manager.getServerAddress(), manager.getPort()));
		buffer.append(String.format("Id: [%s]\n", manager.getId()));
		buffer.append(String.format("Running Since: [%s]\n\n",
					SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG).format(manager.getDateStarted())));

		buffer.append(String.format("Code Base Location: [%s]\n", System.getProperty("user.dir")));
		buffer.append(String.format("Datastore: [%s]\n", DatabaseInfoManager.getDefault().toString()));
		buffer.append(String.format("Binary Data Path: [%s]\n\n", OseeServerProperties.getOseeApplicationServerData()));

		buffer.append(String.format("Supported Versions: %s\n", Arrays.deepToString(manager.getSupportedVersions())));
		buffer.append(String.format("Accepting Requests: [%s]\n", manager.isAcceptingRequests()));
		buffer.append(Lib.getMemoryInfo());

		buffer.append("Servlets:");
		List<String> contexts = new ArrayList<String>(manager.getRegisteredServlets());
		Collections.sort(contexts);
		int indexCnt = 0;
		for (String context : contexts) {
			if (indexCnt % 3 == 0) {
				if (indexCnt != 0) {
					buffer.append("\n\t");
				} else {
					buffer.append(" ");
				}
			} else {
				buffer.append("\t\t");
			}
			buffer.append(context);
			indexCnt++;
		}

		buffer.append(String.format("\nSessionsManaged: [%s]\n", sessionManager.getAllSessions(false).size()));
		buffer.append(String.format("\nServer State: [%s]\n", manager.isSystemIdle() ? "IDLE" : "BUSY"));
		buffer.append(String.format("Active Threads: [%s]\n", manager.getNumberOfActiveThreads()));

		IJobManager jobManager = Job.getJobManager();
		buffer.append(String.format("Job Manager: [%s]\n", jobManager.isIdle() ? "IDLE" : "BUSY"));
		buffer.append(String.format("Current Job: [%s]\n", jobManager.currentJob().getName()));

		buffer.append("Current Tasks: ");
		List<String> entries = manager.getCurrentProcesses();
		if (entries.isEmpty()) {
			buffer.append("[NONE]");
		} else {
			buffer.append("\n");
			for (int index = 0; index < entries.size(); index++) {
				buffer.append(String.format("[%s] ", index));
				buffer.append(entries.get(index));
				if (index + 1 < entries.size()) {
					buffer.append("\n");
				}
			}
		}

		buffer.append("\n");
		println(buffer.toString());
	}
}
