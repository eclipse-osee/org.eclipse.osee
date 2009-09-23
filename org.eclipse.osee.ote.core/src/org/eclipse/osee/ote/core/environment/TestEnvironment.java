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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.ExportException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.connection.service.LocalConnector;
import org.eclipse.osee.framework.jdk.core.reportdata.ReportDataListener;
import org.eclipse.osee.framework.jdk.core.util.EnhancedProperties;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.framework.messaging.MessagingGateway;
import org.eclipse.osee.ote.core.GCHelper;
import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.OseeTestThread;
import org.eclipse.osee.ote.core.OteProperties;
import org.eclipse.osee.ote.core.TestScript;
import org.eclipse.osee.ote.core.cmd.Command;
import org.eclipse.osee.ote.core.environment.interfaces.IAssociatedObjectListener;
import org.eclipse.osee.ote.core.environment.interfaces.ICancelTimer;
import org.eclipse.osee.ote.core.environment.interfaces.IEnvironmentFactory;
import org.eclipse.osee.ote.core.environment.interfaces.IExecutionUnitManagement;
import org.eclipse.osee.ote.core.environment.interfaces.IRuntimeLibraryManager;
import org.eclipse.osee.ote.core.environment.interfaces.IScriptControl;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentAccessor;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentListener;
import org.eclipse.osee.ote.core.environment.interfaces.ITestLogger;
import org.eclipse.osee.ote.core.environment.interfaces.ITestStation;
import org.eclipse.osee.ote.core.environment.interfaces.ITimeout;
import org.eclipse.osee.ote.core.environment.interfaces.ITimerControl;
import org.eclipse.osee.ote.core.framework.IRunManager;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestContext;
import org.eclipse.osee.ote.core.framework.command.ITestServerCommand;
import org.eclipse.osee.ote.core.internal.Activator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Andrew M. Finkbeiner
 */
public abstract class TestEnvironment implements TestEnvironmentInterface, ITestEnvironmentAccessor, ITestContext {

   private final List<ITestEnvironmentListener> envListeners = new ArrayList<ITestEnvironmentListener>(32);
   private IExecutionUnitManagement executionUnitManagement;

   private File outDir = null;
   private final ITestStation testStation;
   private final HashMap<Serializable, Object> users;
   private volatile IUserSession activeUser = null;
   private boolean batchMode = false;
   private OteLogFile oteLog;
   private final HashMap<String, Remote> controlInterfaces = new HashMap<String, Remote>();
   private final IEnvironmentFactory factory;
   private IServiceConnector connector;
   private final IRuntimeLibraryManager runtimeManager;

   @Deprecated
   private final HashMap<Class<?>, Object> associatedObjects;
   @Deprecated
   private final HashMap<Class<?>, ArrayList<IAssociatedObjectListener>> associatedObjectListeners;
   @Deprecated
   private boolean isEnvSetup = false;
   @Deprecated
   private final List<IScriptCompleteEvent> scriptCompleteListeners = new ArrayList<IScriptCompleteEvent>();
   @Deprecated
   private final List<IScriptSetupEvent> scriptSetupListeners = new ArrayList<IScriptSetupEvent>();

   private OteServerSideEndprointRecieve oteServerSideEndpointRecieve;
   private OteServerSideEndpointSender oteServerSideEndpointSender;
   private final ServiceTracker messagingServiceTracker;
   private ExecutorService execInitializationTasks;
   private final LinkedBlockingQueue<Future> listOfThreadsToWaitOnInInit = new LinkedBlockingQueue<Future>();

   private volatile boolean isShutdown = false;
   
   protected TestEnvironment(IEnvironmentFactory factory) {
      GCHelper.getGCHelper().addRefWatch(this);
      execInitializationTasks = Executors.newCachedThreadPool();
      this.factory = factory;
      this.testStation = factory.getTestStation();
      this.runtimeManager = factory.getRuntimeManager();
      this.runtimeManager.addRuntimeLibraryListener(Activator.getInstance());

      this.associatedObjectListeners = new HashMap<Class<?>, ArrayList<IAssociatedObjectListener>>();
      this.associatedObjects = new HashMap<Class<?>, Object>(100);
      this.batchMode = OteProperties.isOseeOteInBatchModeEnabled();
      this.users = new HashMap<Serializable, Object>(32);

      messagingServiceTracker = setupOteMessagingSenderAndReceiver();

      setupOteServerLogFile();
   }

   public void init(IServiceConnector connector) {
      this.connector = connector;
      initializationThreadAdd(new Callable() {
         public Object call() throws Exception {
            Activator.getInstance().registerTestEnvironment(TestEnvironment.this);
            return null;
         }
      });
      waitForWorkerThreadsToComplete();
   }

   public void waitForWorkerThreadsToComplete() {
      while (!listOfThreadsToWaitOnInInit.isEmpty()) {
         Future future = listOfThreadsToWaitOnInInit.poll();
         try {
            future.get();
         } catch (InterruptedException ex) {
            OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
         } catch (ExecutionException ex) {
            OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
         }
      }
   }

   public void initializationThreadAdd(Callable callable) {
      listOfThreadsToWaitOnInInit.add(execInitializationTasks.submit(callable));
   }

   /**
    * 
    */
   private void setupDefaultConnector() {
      EnhancedProperties props = new EnhancedProperties();
      try {
         props.setProperty("station", InetAddress.getLocalHost().getHostName());
      } catch (UnknownHostException ex) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, ex);
      }
      props.setProperty("date", new Date());
      props.setProperty("group", "OSEE Test Environment");
      props.setProperty("owner", System.getProperty("user.name"));
      connector = new LocalConnector(this, props);
   }

   private void setupOteServerLogFile() {
      try {
         String saveFile = OteProperties.getOseeOteLogFilePath();
         if (!Strings.isValid(saveFile)) {
            saveFile = System.getProperty("user.home" + File.pathSeparator + "osee_log");
         }
         if (saveFile == null) {
            saveFile = System.getProperty("user.home");
         }

         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH_mm_ss");
         saveFile =
               String.format("%s%s%s_%s_%s.xml", saveFile, System.getProperty("file.separator"),
                     InetAddress.getLocalHost().getHostName(), System.getProperty("user.name"), sdf.format(new Date()));
         oteLog = new OteLogFile(new File(saveFile));
         OseeLog.registerLoggerListener(oteLog);
      } catch (Exception e1) {
         e1.printStackTrace();
      }
   }

   private ServiceTracker setupOteMessagingSenderAndReceiver() {
      oteServerSideEndpointRecieve = new OteServerSideEndprointRecieve();
      oteServerSideEndpointSender = new OteServerSideEndpointSender(this);
      BundleContext context = Platform.getBundle("org.eclipse.osee.ote.core").getBundleContext();
      return getServiceTracker(MessagingGateway.class.getName(), new OteEnvironmentTrackerCustomizer(context,
            oteServerSideEndpointRecieve, oteServerSideEndpointSender,
            OteServerSideEndpointSender.OTE_SERVER_SIDE_SEND_PROTOCOL));
   }

   public void sendCommand(Command command) {
      Activator.getInstance().getCommandDistributer().distribute(command);
   }

   public void sendMessageToServer(Message message) {
      oteServerSideEndpointRecieve.recievedMessage(message);
   }

   public ServiceTracker getServiceTracker(String clazz, ServiceTrackerCustomizer customizer) {
      return Activator.getInstance().getServiceTracker(clazz, customizer);
   }

   public ServiceTracker getServiceTracker(String clazz) {
      return getServiceTracker(clazz, null);
   }

   public ICommandHandle addCommand(ITestServerCommand cmd) throws ExportException {
      return factory.getCommandManager().addCommand(cmd, this);
   }

   public IRunManager getRunManager() {
      return factory.getRunManager();
   }

   public IRuntimeLibraryManager getRuntimeManager() {
      return this.runtimeManager;
   }

   public IEnvironmentFactory getEnvironmentFactory() {
      return factory;
   }

   public boolean isInBatchMode() {
      return batchMode;
   }

   public void setBatchMode(boolean isInBatchMode) {
      if (!OteProperties.isOseeOteInBatchModeEnabled()) {
         this.batchMode = isInBatchMode;
      }
   }

   public void addEnvironmentListener(ITestEnvironmentListener listener) {
      envListeners.add(listener);
   }

   public boolean addTask(EnvironmentTask task) {
      factory.getTimerControl().addTask(task, this);
      return true;
   }

   public UserTestSessionKey addUser(IUserSession user) throws Exception {
      UserTestSessionKey key = new UserTestSessionKey(user.getUser());
      users.put(key, user);
      return key;
   }

   public boolean equals(ITestEnvironment testEnvironment) throws RemoteException {
      if (testEnvironment.getUniqueId() == getUniqueId()) {
         return true;
      } else {
         return false;
      }
   }

   public long getEnvTime() {
      return getTimerCtrl().getEnvTime();
   }

   public IExecutionUnitManagement getExecutionUnitManagement() {
      return this.executionUnitManagement;
   }

   public ITestLogger getLogger() {
      return factory.getTestLogger();
   }

   public List<String> getQueueLabels() {
      List<String> list = new ArrayList<String>();
      list.add("Description");
      return list;
   }

   public abstract Object getModel(String modelClassName);

   /*
    * public Remote getRemoteModel(String modelClassName) throws RemoteException { return
    * getRemoteModel(modelClassName, new Class[] {}, new Object[] {}); }
    */

   public IScriptControl getScriptCtrl() {
      return factory.getScriptControl();
   }

   public byte[] getScriptOutfile(String filepath) throws RemoteException {
      try {
         File file = new File(filepath);
         InputStream is = new FileInputStream(file);
         long length = file.length();
         byte[] bytes = new byte[(int) length];

         int numRead = is.read(bytes);
         if (numRead < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
         }
         is.close();
         OseeLog.log(TestEnvironment.class, Level.FINE, "going to send " + bytes.length + " bytes to the client");

         return bytes;
      } catch (Exception ex) {
         throw new RemoteException("Error retrieving the script output", ex);
      }
   }

   public ITestStation getTestStation() {
      return testStation;
   }

   public ITimerControl getTimerCtrl() {
      return factory.getTimerControl();
   }

   public int getUniqueId() {
      return this.hashCode();
   }

   public ArrayList<Serializable> getUserList() {
      return new ArrayList<Serializable>(getSessionKeys());
   }

   public void disconnect(final UserTestSessionKey key) throws RemoteException {
      try {
         OseeLog.log(TestEnvironment.class, Level.INFO, "Disconnecting user " + getUserSession(key).getUser().getName());
      } catch (Exception ex) {
         OseeLog.log(TestEnvironment.class, Level.INFO, "problem with accessing user name from the useer session");
      }
      users.remove(key);
   }

   public Set<Serializable> getSessionKeys() {
      return users.keySet();
   }

   public IUserSession getUserSession(final UserTestSessionKey key) {
      return (IUserSession) users.get(key);
   }

   private final void removeAllTasks() {
      factory.getTimerControl().cancelAllTasks();
   }

   public void removeUser(OSEEPerson1_4 user) {
      if (users.containsKey(user)) {
         users.put(user, new Integer(((Integer) users.get(user)).intValue() - 1));
         if (((Integer) users.get(user)).intValue() == 0) {
            users.remove(user);
         }
      }
   }

   public URL setBatchLibJar(byte[] batchJar) throws IOException {
      String path = System.getProperty("user.home") + File.separator + TestEnvironment.class.getName();

      File dir = new File(path, "batchLibCache");
      if (!dir.isDirectory()) {
         dir.mkdir();
      }
      File jar = File.createTempFile("Batch", ".jar", dir);
      Lib.writeBytesToFile(batchJar, jar);
      return jar.toURI().toURL();
   }

   public ICancelTimer setTimerFor(ITimeout listener, int time) {
      return getTimerCtrl().setTimerFor(listener, time);
   }

   protected void setupOutfileDir(String outfileDir) throws IOException {
      if (outfileDir != null && !outfileDir.equals("")) {
         outDir = new File(outfileDir);
         if (!outDir.isDirectory()) {
            if (!outDir.mkdirs()) {
               throw new IOException("Failed to create the output directory");
            }
            OseeLog.log(TestEnvironment.class, Level.INFO, String.format("Outfile Dir [%s] created.",
                  outDir.getAbsolutePath()));
         } else {
            OseeLog.log(TestEnvironment.class, Level.FINE, String.format("Outfile Dir [%s] exists.",
                  outDir.getAbsolutePath()));
         }
      } else {
         throw new IOException("A valid outfile directory must be specified.");
      }
   }

   public void shutdown() {
      if (isShutdown) {
         return;
      }
      isShutdown = true;
      runtimeManager.cleanup();
      Activator.getInstance().unregisterTestEnvironment();
      // here we remove all environment tasks (emulators)
      removeAllTasks();
      if (associatedObjects != null) {
         this.associatedObjects.clear();// get rid of all models and support
      }

      messagingServiceTracker.close();

      OseeLog.log(TestEnvironment.class, Level.FINE, "shutting down environment");
      factory.getTimerControl().cancelTimers();
      stop();
      cleanupClassReferences();
      OseeTestThread.clearThreadReferences();
      for (ITestEnvironmentListener listener : envListeners) {
         listener.onEnvironmentKilled(this);
      }
      envListeners.clear();
      if (getRunManager() != null) {
         getRunManager().clearAllListeners();
      }
      users.clear();
      
   }

   protected abstract void loadExternalDrivers();

   public void startup(String outfileDir) throws Exception {
      try {
         setupOutfileDir(outfileDir);
      } catch (IOException ex) {
         throw new Exception("Error in directory setup. " + outfileDir, ex);
      }
   }

   protected void stop() {

   }

   public IUserSession getActiveUser() {
      return activeUser;
   }

   public File getClientResource(String workspacePath) throws Exception, IOException {
      if (activeUser == null) {
         throw new IllegalStateException("No active user");
      }
      String version = activeUser.getFileVersion(workspacePath);
      File resource = getResourceFile(workspacePath, version);
      if (!resource.exists()) {
         byte[] bytes = activeUser.getFile(workspacePath);
         if (bytes != null) {
            Lib.writeBytesToFile(bytes, resource);
         }
      }
      return resource;
   }

   private File getResourceDir() {
      File file =
            new File(
                  System.getProperty("java.io.tmpdir") + File.separator + System.getProperty("user.name") + File.separator + "oseeresources");
      file.mkdirs();
      return file;
   }

   private File getResourceFile(String workspacePath, String version) {
      String filename = workspacePath.replace("/", "_");
      int extension = filename.lastIndexOf('.');
      filename = filename.substring(0, extension - 1) + version.toUpperCase() + filename.substring(extension);
      return new File(getResourceDir(), filename);
   }

   public void setActiveUser(UserTestSessionKey key) {
      activeUser = (IUserSession) users.get(key);
   }

   protected void cleanupClassReferences() {
      OseeLog.log(TestEnvironment.class, Level.FINE, "cleanupreferences");

      System.out.println("Associated objects that are getting cleaned up.");
      for (Class<?> clazz : associatedObjects.keySet()) {
         System.out.println(clazz.toString());
      }

      if (associatedObjects != null) {
         associatedObjects.clear();
      }
      OseeLog.log(TestEnvironment.class, Level.FINE, "got the other PM REF");
      if (associatedObjectListeners != null) {
         associatedObjectListeners.clear();
      }
      GCHelper.getGCHelper().printLiveReferences();
   }

   public void setExecutionUnitManagement(IExecutionUnitManagement executionUnitManagement) {
      this.executionUnitManagement = executionUnitManagement;
   }

   public File getOutDir() {
      return outDir;
   }

   public List<IUserSession> getUserSessions() {
      List<IUserSession> people = new ArrayList<IUserSession>();
      for (Object user : users.values()) {
         if (user instanceof IUserSession) {
            people.add((IUserSession) user);
         }
      }
      return people;
   }

   public Remote getControlInterface(String id) {
      return controlInterfaces.get(id);
   }

   public void registerControlInterface(String id, Remote controlInterface) {
      controlInterfaces.put(id, controlInterface);
   }

   public IServiceConnector getConnector() {
      return connector;
   }

   //   public void setConnector(IServiceConnector connector) {
   //      this.connector = connector;
   //   }

   public void setupClassLoaderAndJar(String[] jarVersions, String classPath) throws Exception {
      setupClassLoaderAndJar(jarVersions, new String[] {classPath});
   }

   public void setupClassLoaderAndJar(String[] jarVersions, String[] classPaths) throws Exception {
      getRuntimeManager().setupClassLoaderAndJar(jarVersions, classPaths);
   }

   @Deprecated
   public void setEnvSetup(boolean isEnvSetup) {
      this.isEnvSetup = isEnvSetup;
   }

   @Deprecated
   public void addScriptCompleteListener(IScriptCompleteEvent scriptComplete) {
      this.scriptCompleteListeners.add(scriptComplete);
   }

   @Deprecated
   public void removeScriptCompleteListener(IScriptCompleteEvent scriptComplete) {
      this.scriptCompleteListeners.remove(scriptComplete);
   }

   @Deprecated
   public void addScriptSetupListener(IScriptSetupEvent scriptSetup) {
      this.scriptSetupListeners.add(scriptSetup);
   }

   @Deprecated
   public void removeScriptSetupListener(IScriptSetupEvent scriptSetup) {
      this.scriptSetupListeners.remove(scriptSetup);
   }

   @Deprecated
   protected boolean isEnvSetup() {
      return isEnvSetup;
   }

   @Deprecated
   /**
    * alerts the environment of an exception. The environment will take any necessary actions and alert any interested
    * entities of the problem. Any runing test script will be terminated
    * 
    * @param t
    * @param logLevel
    */
   public void handleException(Throwable t, Level logLevel) {
      handleException(t, "An exception has occurred in the environment", logLevel, true);
   }

   @Deprecated
   /**
    * @param t
    * @param logLevel
    * @param abortScript true will cause the currently running script to abort
    */
   public void handleException(Throwable t, Level logLevel, boolean abortScript) {
      handleException(t, "An exception has occurred in the environment", logLevel, abortScript);
   }

   @Deprecated
   /**
    * alerts the environment of an exception. The environment will take any necessary actions and alert any interested
    * entities of the problem
    * 
    * @param t the exception
    * @param logLevel the severity of the exception. Specifing a Level.OFF will
    * @param abortScript cause the exception to not be logged
    */
   public void handleException(Throwable t, String message, Level logLevel, boolean abortScript) {
      if (logLevel != Level.OFF) {
         OseeLog.log(TestEnvironment.class, logLevel, message, t);
      }
      if (getTestScript() != null && abortScript) {
         getTestScript().abortDueToThrowable(t);
      }
      Iterator<ITestEnvironmentListener> iter = envListeners.iterator();
      while (iter.hasNext()) {
         final ITestEnvironmentListener listener = iter.next();
         listener.onException(message, t);
      }
   }

   @Deprecated
   public void testEnvironmentCommandComplete(ICommandHandle handle) {
      for (ITestEnvironmentListener listener : envListeners) {
         try {
            listener.onTestServerCommandFinished(this, handle);
         } catch (Throwable th) {
            System.out.println(listener.getClass().getName());
            th.printStackTrace();
         }
      }
   }

   @Deprecated
   /**
    * marks the script as ready as well as clears any objects that are associated with the environment.
    */
   public synchronized void onScriptSetup() {
      factory.getScriptControl().setScriptReady(true);

      for (IScriptSetupEvent listeners : scriptSetupListeners) {
         listeners.scriptSetup();
      }

      this.associatedObjects.clear();
   }

   @Deprecated
   public void removeQueueListener(ReportDataListener listener) throws RemoteException {
      factory.getReportDataControl().removeQueueListener(listener);
   }

   @Deprecated
   public void onScriptComplete() throws InterruptedException {
      factory.getScriptControl().setScriptReady(false);

      for (int i = 0; i < scriptCompleteListeners.size(); i++) {
         try {
            scriptCompleteListeners.get(i).scriptComplete();
         } catch (Exception e) {
            OseeLog.log(TestEnvironment.class, Level.SEVERE, "problem with script complete listener", e);
         }
      }

      // here we remove all environment tasks (emulators)
      if (associatedObjects != null) {
         this.associatedObjects.clear();// get rid of all models and support
      }
   }

   @Deprecated
   public void associateObject(Class<?> c, Object obj) {
      associatedObjects.put(c, obj);
      ArrayList<IAssociatedObjectListener> listeners = this.associatedObjectListeners.get(c);
      if (listeners != null) {
         for (int i = 0; i < listeners.size(); i++) {
            try {
               (listeners.get(i)).updateAssociatedListener();
            } catch (RemoteException e) {
               OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
            }

         }
      }
   }

   @Deprecated
   public Object getAssociatedObject(Class<?> c) {
      return associatedObjects.get(c);
   }

   @Deprecated
   public Set<Class<?>> getAssociatedObjects() {
      return associatedObjects.keySet();
   }

   @Deprecated
   /**
    * Use getRunManager().getCurrentScript() instead of this method.
    */
   public TestScript getTestScript() {
      return getRunManager().getCurrentScript();
   }

   @Deprecated
   public void abortTestScript() {
      getRunManager().abort();
   }

   @Deprecated
   public void abortTestScript(Throwable t) {
      getRunManager().abort(t, false);
   }
}
