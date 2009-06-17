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
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IEnvironmentFactory;
import org.eclipse.osee.ote.core.environment.interfaces.IExecutionUnitManagement;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentListener;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestServerCommand;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Andrew M. Finkbeiner
 */
public interface TestEnvironmentInterface {
   public ServiceTracker getServiceTracker(String clazz);
   public ICommandHandle addCommand(ITestServerCommand cmd) throws ExportException;
   public IRunManager getRunManager();
   public IRuntimeLibraryManager getRuntimeManager();
   public boolean isInBatchMode();
   public void setBatchMode(boolean isInBatchMode) ;
   public void addEnvironmentListener(ITestEnvironmentListener listener) ;
   public boolean addTask(EnvironmentTask task) ;
   public UserTestSessionKey addUser(IUserSession user) throws Exception;
   public long getEnvTime() ;
   public IExecutionUnitManagement getExecutionUnitManagement() ;
   public ITestLogger getLogger() ;
   public List<String> getQueueLabels() ;
   public abstract Object getModel(String modelClassName);
   public IScriptControl getScriptCtrl() ;
   public byte[] getScriptOutfile(String filepath) throws RemoteException ;
   public ITestStation getTestStation() ;
   public ITimerControl getTimerCtrl() ;
   public int getUniqueId() ;
   public ArrayList<Serializable> getUserList() ;
   public void disconnect(final UserTestSessionKey key) throws RemoteException ;
   public Set<Serializable> getSessionKeys() ;
   public IUserSession getUserSession(final UserTestSessionKey key) ;
   public void removeUser(OSEEPerson1_4 user) ;
   public URL setBatchLibJar(byte[] batchJar) throws IOException ;
   public ICancelTimer setTimerFor(ITimeout listener, int time) ;
   public void shutdown() ;
   public IUserSession getActiveUser() ;
   public File getClientResource(String workspacePath) throws Exception, IOException ;
   public void setActiveUser(UserTestSessionKey key) ;
   public File getOutDir() ;
   public List<IUserSession> getUserSessions() ;
   public Remote getControlInterface(String id) ;
   public void registerControlInterface(String id, Remote controlInterface) ;
   public IServiceConnector getConnector() ;
   public void setupClassLoaderAndJar(String[] jarVersions, String classPath) throws Exception ;
   public void setupClassLoaderAndJar(String[] jarVersions, String[] classPaths) throws Exception ;
   public IEnvironmentFactory getEnvironmentFactory();
   public void initializationThreadAdd(Callable callable);
}
