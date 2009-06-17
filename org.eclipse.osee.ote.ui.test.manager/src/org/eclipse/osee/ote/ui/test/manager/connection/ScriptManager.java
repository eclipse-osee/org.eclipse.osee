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
package org.eclipse.osee.ote.ui.test.manager.connection;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.core.environment.status.IServiceStatusListener;
import org.eclipse.osee.ote.core.environment.status.TestComplete;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.ui.test.manager.TestManagerPlugin;
import org.eclipse.osee.ote.ui.test.manager.core.TestManagerEditor;
import org.eclipse.osee.ote.ui.test.manager.jobs.StoreOutfileJob;
import org.eclipse.osee.ote.ui.test.manager.models.OutputModelJob;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask;
import org.eclipse.osee.ote.ui.test.manager.pages.scriptTable.ScriptTask.ScriptStatusEnum;
import org.eclipse.swt.widgets.Display;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class ScriptManager implements Runnable {
	private final Map<String, ScriptTask> guidToScriptTask = new HashMap<String, ScriptTask>();
	private TestManagerStatusListener statusListenerImpl;
	private final TestManagerEditor testManager;

	private volatile boolean updateScriptTable;
	private StructuredViewer stv;
	private ScheduledExecutorService updater;
	private Set<ScriptTask> tasksToUpdate;
	private ITestEnvironment connectedEnv;
	private UserTestSessionKey sessionKey;

	public ScriptManager(TestManagerEditor testManager, StructuredViewer stv) {
		this.testManager = testManager;
		this.stv = stv;

		tasksToUpdate = new HashSet<ScriptTask>();
		updater = Executors.newScheduledThreadPool(1, new ThreadFactory() {

			public Thread newThread(Runnable r) {
				Thread th = new Thread(r, "TM Table updater");
				th.setDaemon(true);
				return th;
			}

		});
		updater.scheduleAtFixedRate(this, 0, 2000, TimeUnit.MILLISECONDS);
		OutputModelJob.createSingleton(this);
	}

	public abstract void abortScript(boolean isBatchAbort) throws RemoteException;

	public void notifyScriptDequeued(String className) {
		ScriptTask task = guidToScriptTask.get(className);
		if (task != null) {
			guidToScriptTask.remove(task);
		}
	}

	/**
	 * This should be called after the environment is received in order to
	 * configure necessary items.
	 * 
	 * @return null if successful, otherwise a string describing the error
	 * @throws RemoteException
	 */
	public boolean connect(ConnectionEvent event) {

		connectedEnv = event.getEnvironment();
		sessionKey = event.getSessionKey();
		try {
			/*
			 * Setup the status listener for commands
			 */
			statusListenerImpl = new TestManagerStatusListener(testManager, this);

			connectedEnv.addStatusListener((IServiceStatusListener) event.getConnector().export(statusListenerImpl));
			return false;
		} catch (Exception e) {
			TestManagerPlugin.log(Level.SEVERE, "failed to connect script manager", e);
			return true;
		}
	}

	/**
	 * This should NOT be called directly, users should call the HostDataStore's
	 * disconnect.
	 */
	public boolean disconnect(ConnectionEvent event) {
		connectedEnv = null;
		sessionKey = null;
		guidToScriptTask.clear();
		try {

			event.getEnvironment().removeStatusListener((IServiceStatusListener) event.getConnector().findExport(statusListenerImpl));
			return false;
		} catch (RemoteException e) {
			TestManagerPlugin.log(Level.SEVERE, "problems removing listener", e);
			return true;
		}
	}

	public boolean onConnectionLost(IHostTestEnvironment testHost) {
		connectedEnv = null;
		sessionKey = null;
		guidToScriptTask.clear();
		return false;
	}

	public ScriptTask getScriptTask(String name) {
		return guidToScriptTask.get(name);
	}

	public void notifyScriptQueued(GUID theGUID, final ScriptTask script) {
		guidToScriptTask.put(script.getScriptModel().getTestClass(), script);
		script.setStatus(ScriptStatusEnum.IN_QUEUE);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (stv.getControl().isDisposed()) {
					return;
				}
				stv.refresh(script);
			}
		});
	}

	public void updateScriptTableViewer(final ScriptTask task) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (stv.getControl().isDisposed()) {
					return;
				}
				stv.refresh(task);
			}
		});
	}

	public void updateScriptTableViewerTimed(ScriptTask task) {
		updateScriptTable = true;
		synchronized (tasksToUpdate) {
			tasksToUpdate.add(task);
		}
	}

	public void run() {
		if (updateScriptTable) {
			updateScriptTable = false;
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					synchronized (tasksToUpdate) {
						if (stv.getControl().isDisposed()) {
							return;
						}
						for (ScriptTask task : tasksToUpdate) {
							stv.refresh(task);
						}
						tasksToUpdate.clear();
					}
				}
			});
		}
	}

	protected TestManagerEditor getTestManagerEditor() {
		return testManager;
	}

	public abstract void addTestsToQueue(List<ScriptTask> scripts);

	/**
	 * @param task
	 */
	public void notifyScriptStart(final ScriptTask task) {
		task.setStatus(ScriptStatusEnum.RUNNING);
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				stv.refresh(task);
			}
		});
	}

	 public void storeOutFile(ScriptTask task, TestComplete testComplete, boolean isValidRun) {
		if (task.getScriptModel() != null) {
			Job job = new StoreOutfileJob(connectedEnv, testManager, this, task, testComplete.getClientOutfilePath(), testComplete.getServerOutfilePath(),
					isValidRun);
			StoreOutfileJob.scheduleJob(job);
		}
	}

	 protected UserTestSessionKey getSessionKey() {
		return sessionKey;
	}
}