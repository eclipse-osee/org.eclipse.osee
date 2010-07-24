/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.server.internal;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.jdk.core.reportdata.ReportDataListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.ote.core.IUserSession;
import org.eclipse.osee.ote.core.OSEEPerson1_4;
import org.eclipse.osee.ote.core.ReturnStatus;
import org.eclipse.osee.ote.core.cmd.Command;
import org.eclipse.osee.ote.core.environment.UserTestSessionKey;
import org.eclipse.osee.ote.core.environment.interfaces.IRemoteCommandConsole;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironmentListener;
import org.eclipse.osee.ote.core.environment.status.IServiceStatusListener;
import org.eclipse.osee.ote.core.framework.command.ICommandHandle;
import org.eclipse.osee.ote.core.framework.command.ITestServerCommand;
import org.eclipse.osee.ote.core.model.IModel;
import org.eclipse.osee.ote.core.model.IModelListener;
import org.eclipse.osee.ote.core.model.IModelManagerRemote;
import org.eclipse.osee.ote.core.model.ModelKey;
import org.eclipse.osee.ote.core.model.ModelState;
import org.eclipse.osee.ote.message.IInstrumentationRegistrationListener;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.instrumentation.IOInstrumentation;
import org.eclipse.osee.ote.message.interfaces.IRemoteMessageService;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystem;
import org.eclipse.osee.ote.server.RemoteShell;

public class RemoteTestEnvironment implements ITestEnvironmentMessageSystem {

   private final MessageSystemTestEnvironment env;
   private final IServiceConnector serviceConnector;
   private RemoteModelManager modelManager;
   private final MessageToolServiceTracker messageToolServiceTracker;
   private final HashMap<IRemoteCommandConsole, RemoteShell> exportedConsoles =
      new HashMap<IRemoteCommandConsole, RemoteShell>(32);
   private final boolean keepEnvAliveWithNoUsers;

   public RemoteTestEnvironment(MessageSystemTestEnvironment currentEnvironment, IServiceConnector serviceConnector, boolean keepEnvAliveWithNoUsers) {
      this.env = currentEnvironment;
      this.serviceConnector = serviceConnector;
      this.keepEnvAliveWithNoUsers = keepEnvAliveWithNoUsers;
      messageToolServiceTracker = new MessageToolServiceTracker(new MessageToolExportCustomizer(serviceConnector));
      messageToolServiceTracker.open(true);
   }

   @Override
   public Remote getControlInterface(String controlInterfaceID) throws RemoteException {
      Remote controlInterface = env.getControlInterface(controlInterfaceID);
      if (controlInterface != null) {
         try {
            controlInterface = (Remote) serviceConnector.export(controlInterface);
         } catch (Exception ex) {
            OseeLog.log(RemoteTestEnvironment.class, Level.SEVERE,
               "exception exporting control interface " + controlInterfaceID, ex);
            throw new RemoteException("exception exporting control interface " + controlInterfaceID, ex);
         }
      }
      return controlInterface;
   }

   @Override
   public IOInstrumentation getIOInstrumentation(String name) throws RemoteException {
      IOInstrumentation io = env.getIOInstrumentation(name);
      if (io != null) {
         try {
            Object exported = serviceConnector.findExport(io);
            if (exported == null) {
               exported = serviceConnector.export(io);
            }
            return (IOInstrumentation) exported;
         } catch (Exception ex) {
            OseeLog.log(RemoteTestEnvironment.class, Level.SEVERE, ex.toString(), ex);
            throw new RemoteException("Unable to export the remote IOInstrumentation for " + name, ex);
         }
      }
      throw new RemoteException("No IOInstrumentation registered for type " + name);
   }

   @Override
   public void addInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) throws RemoteException {
      env.addInstrumentationRegistrationListener(listener);
   }

   @Override
   public void removeInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) throws RemoteException {
      env.removeInstrumentationRegistrationListener(listener);
   }

   @Override
   public IRemoteMessageService getMessageToolServiceProxy() throws RemoteException {
      try {
         return messageToolServiceTracker.waitForService(10000);
      } catch (InterruptedException e) {
         throw new RemoteException("", e);
      }
   }

   public void removeUser(OSEEPerson1_4 user) {
      env.removeUser(user);
   }

   public ReturnStatus isRunningJarVersions(String[] jarVersions) {
      return env.getRuntimeManager().isRunningJarVersions(jarVersions);
   }

   @Override
   public ICommandHandle addCommand(ITestServerCommand cmd) throws RemoteException {
      return env.addCommand(cmd);
   }

   @Override
   @Deprecated
   public boolean isMessageJarAvailable(String version) throws RemoteException {
      return env.isMessageJarAvailable(version);
   }

   @Override
   @Deprecated
   public void sendRuntimeJar(byte[] messageJar) throws RemoteException {
   }

   @Override
   public void addEnvironmentListener(ITestEnvironmentListener listener) throws RemoteException {
      env.addEnvironmentListener(listener);
   }

   @Override
   public void setupClassLoaderAndJar(String[] jarVersions, String classPath) throws RemoteException {
   }

   @Override
   public void setupClassLoaderAndJar(String[] jarVersion, String[] classPaths) throws RemoteException {
   }

   @Override
   public IRemoteCommandConsole getCommandConsole() throws RemoteException {
      OseeLog.log(RemoteTestEnvironment.class, Level.FINE, "Remote command onsole requested");
      RemoteShell shell = new RemoteShell(Activator.getDefault().getCommandManager());

      IRemoteCommandConsole exportedConsole;
      try {
         exportedConsole = (IRemoteCommandConsole) serviceConnector.export(shell);
      } catch (Exception ex) {
         throw new RemoteException("failed to export remote console", ex);
      }
      exportedConsoles.put(exportedConsole, shell);
      return exportedConsole;
   }

   @Override
   public void closeCommandConsole(IRemoteCommandConsole console) throws RemoteException {
      RemoteShell shell = exportedConsoles.remove(console);
      if (shell != null) {
         try {
            serviceConnector.unexport(shell);
         } catch (Exception ex) {
            throw new RemoteException("failed to unexport remote shell", ex);
         }

         OseeLog.log(RemoteTestEnvironment.class, Level.FINE, "closed command console");
      } else {
         OseeLog.log(RemoteTestEnvironment.class, Level.FINE, "trying to remove non existing console");
      }
   }

   @Override
   public void addStatusListener(IServiceStatusListener listener) throws RemoteException {
      if (Activator.getDefault().getOteStatusBoard() != null) {
         Activator.getDefault().getOteStatusBoard().addStatusListener(listener);
      }
   }

   @Override
   public UserTestSessionKey addUser(IUserSession user) throws RemoteException {
      try {
         return env.addUser(user);
      } catch (Exception ex) {
         throw new RemoteException("could not add user sessoion", ex);
      }
   }

   //TODO
   @Override
   public boolean disconnect(UserTestSessionKey user) throws RemoteException {
      env.disconnect(user);
      if (!keepEnvAliveWithNoUsers && env.getSessionKeys().isEmpty()) {
         try {
            messageToolServiceTracker.close();
            for (IRemoteCommandConsole console : exportedConsoles.keySet()) {
               closeCommandConsole(console);
            }
         } catch (Exception ex) {
            throw new RemoteException("failed to unexport test environment", ex);
         }
         env.shutdown();
         return true;
      }
      return false;
   }

   @Override
   public void disconnectAll() throws RemoteException {
      for (Serializable session : env.getSessionKeys()) {
         env.disconnect((UserTestSessionKey) session);
      }
      if (!keepEnvAliveWithNoUsers) {
         messageToolServiceTracker.close();
         for (IRemoteCommandConsole console : exportedConsoles.keySet()) {
            closeCommandConsole(console);
         }
         env.shutdown();
      }
   }

   @Override
   public boolean equals(ITestEnvironment testEnvironment) throws RemoteException {
      return env.getUniqueId() == testEnvironment.getUniqueId();
   }

   @Override
   public List<String> getQueueLabels() throws RemoteException {
      return env.getQueueLabels();
   }

   @Override
   public Remote getRemoteModel(String modelClassName) throws RemoteException {
      return getRemoteModel(modelClassName, new Class[0], new Object[0]);
   }

   @Override
   public IModelManagerRemote getModelManager() throws RemoteException {
      if (modelManager == null) {
         modelManager = new RemoteModelManager();
      }
      try {
         return (IModelManagerRemote) serviceConnector.export(modelManager);
      } catch (Throwable t) {
         throw new RemoteException("Could not load get model manager" + t.getMessage());
      }
   }

   @Override
   public byte[] getScriptOutfile(String outfilePath) throws RemoteException {
      return env.getScriptOutfile(outfilePath);
   }

   @Override
   public int getUniqueId() throws RemoteException {
      return env.getUniqueId();
   }

   @Override
   public Collection<OSEEPerson1_4> getUserList() throws RemoteException {
      Collection<OSEEPerson1_4> users = new ArrayList<OSEEPerson1_4>(env.getSessionKeys().size());
      try {
         Set<Serializable> sessionKeys = env.getSessionKeys();
         for (Serializable serializable : sessionKeys) {
            if (serializable instanceof UserTestSessionKey) {
               try {
                  users.add(((UserTestSessionKey) serializable).getUser());
               } catch (Exception ex) {
                  OseeLog.log(RemoteTestEnvironment.class, Level.SEVERE, "exception while getting user list", ex);
               }
            }
         }
         return users;
      } catch (Exception ex) {
         OseeLog.log(RemoteTestEnvironment.class, Level.SEVERE, "exception while generating user list", ex);
         throw new RemoteException("exception while generating user list", ex);
      }
   }

   //TODO
   @Override
   public void onHostKilled() throws RemoteException {
   }

   @Override
   public void removeQueueListener(ReportDataListener listener) throws RemoteException {
      env.removeQueueListener(listener);
   }

   @Override
   public void removeStatusListener(IServiceStatusListener listener) throws RemoteException {
      if (Activator.getDefault().getOteStatusBoard() != null) {
         Activator.getDefault().getOteStatusBoard().removeStatusListener(listener);
      }
   }

   @Override
   public URL setBatchLibJar(byte[] messageJar) throws RemoteException {
      try {
         return env.setBatchLibJar(messageJar);
      } catch (IOException ex) {
         throw new RemoteException("unable to set batch jar", ex);
      }
   }

   @Override
   public void setClientClasses(URL[] urls) throws RemoteException {
      env.setClientClasses(urls);
   }

   @Override
   public void startup(String outfileDir) throws RemoteException {
      try {
         env.startup(outfileDir);
      } catch (Exception ex) {
         throw new RemoteException("unable to set outfile location " + ex.getMessage());
      }
   }

   public IUserSession getUserSession(UserTestSessionKey key) {
      return env.getUserSession(key);
   }

   private class RemoteModelManager implements IModelManagerRemote {

      @Override
      public void addModelActivityListener(IModelListener listener) throws RemoteException {
         env.getModelManager().addModelActivityListener(listener);
      }

      @Override
      public void removeModelActivityListener(IModelListener listener) throws RemoteException {
         env.getModelManager().removeModelActivityListener(listener);
      }

      @Override
      @SuppressWarnings("unchecked")
      public void addModelActivityListener(IModelListener listener, ModelKey key) throws RemoteException {
         env.getModelManager().addModelActivityListener(listener, key);
      }

      @Override
      @SuppressWarnings("unchecked")
      public void removeModelActivityListener(IModelListener listener, ModelKey key) throws RemoteException {
         env.getModelManager().removeModelActivityListener(listener, key);
      }

      @Override
      @SuppressWarnings("unchecked")
      public List<ModelKey> getRegisteredModels() throws RemoteException {
         return env.getModelManager().getRegisteredModels();
      }

      @Override
      @SuppressWarnings("unchecked")
      public Remote getRemoteModel(ModelKey key) throws RemoteException {
         try {
            Class modelClass;
            try {

               modelClass = env.loadClassFromScriptLoader(key.getClassName());
            } catch (ClassNotFoundException ex) {
               throw new RemoteException("Could not load model class: " + key.getClassName(), ex);
            }
            key.setModelClass(modelClass);
            IModel model = env.getModelManager().getModel(key);
            return (Remote) serviceConnector.export(model);
         } catch (Throwable t) {
            throw new RemoteException("Could not load model " + key.getClassName(), t);
         }
      }

      @Override
      @SuppressWarnings("unchecked")
      public void releaseReference(ModelKey key) throws RemoteException {
         env.getModelManager().releaseReference(key);
      }

      @Override
      @SuppressWarnings("unchecked")
      public void changeModelState(ModelKey key, ModelState state) throws RemoteException {
         Class modelClass;
         try {
            modelClass = env.loadClassFromScriptLoader(key.getClassName());
         } catch (ClassNotFoundException ex) {
            throw new RemoteException("Could not load model class: " + key.getClassName());
         }
         key.setModelClass(modelClass);
         env.getModelManager().changeModelState(key, state);
      }

      @Override
      @SuppressWarnings("unchecked")
      public ModelState getModelState(ModelKey key) throws RemoteException {
         return env.getModelManager().getModelState(key);
      }

      @Override
      @SuppressWarnings("unchecked")
      public void releaseAllReferences(ModelKey key) throws RemoteException {
         env.getModelManager().releaseAllReferences(key);
      }

   }

   @Override
   public void setBatchMode(boolean isInBatchMode) throws RemoteException {
      env.setBatchMode(isInBatchMode);
   }

   @Override
   public void sendCommand(Command command) throws RemoteException {
      env.sendCommand(command);
   }

   @Override
   public void sendMessage(Message message) throws RemoteException {
      env.sendMessageToServer(message);
   }

   @Override
   public Remote getRemoteModel(String modelClassName, Class<?>[] methodParameterTypes, Object[] methodParameters) throws RemoteException {
      return null;
   }
}
