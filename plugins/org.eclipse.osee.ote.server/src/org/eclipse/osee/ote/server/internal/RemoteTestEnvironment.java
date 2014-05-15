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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.Message;
import org.eclipse.osee.ote.core.ServiceUtility;
import org.eclipse.osee.ote.core.cmd.Command;
import org.eclipse.osee.ote.core.environment.console.ICommandManager;
import org.eclipse.osee.ote.core.environment.interfaces.IRemoteCommandConsole;
import org.eclipse.osee.ote.core.model.IModel;
import org.eclipse.osee.ote.core.model.IModelListener;
import org.eclipse.osee.ote.core.model.IModelManagerRemote;
import org.eclipse.osee.ote.core.model.ModelKey;
import org.eclipse.osee.ote.core.model.ModelState;
import org.eclipse.osee.ote.message.IInstrumentationRegistrationListener;
import org.eclipse.osee.ote.message.MessageSystemTestEnvironment;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystem;
import org.eclipse.osee.ote.server.RemoteShell;

public class RemoteTestEnvironment implements ITestEnvironmentMessageSystem {

   private final MessageSystemTestEnvironment env;
   private final IServiceConnector serviceConnector;
   private RemoteModelManager modelManager;
   private final HashMap<IRemoteCommandConsole, RemoteShell> exportedConsoles =
      new HashMap<IRemoteCommandConsole, RemoteShell>(32);

   private final ReentrantLock lock = new ReentrantLock();
//   private IRemoteMessageService exportedRemoteMessageService;

   public RemoteTestEnvironment(MessageSystemTestEnvironment currentEnvironment, IServiceConnector serviceConnector, boolean keepEnvAliveWithNoUsers) {
      if (serviceConnector == null) {
         throw new NullPointerException("Servce connector cannot be null");
      }
      this.env = currentEnvironment;
      this.serviceConnector = serviceConnector;
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
   public void addInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) throws RemoteException {
      env.addInstrumentationRegistrationListener(listener);
   }

   @Override
   public void removeInstrumentationRegistrationListener(IInstrumentationRegistrationListener listener) throws RemoteException {
      env.removeInstrumentationRegistrationListener(listener);
   }

//   @Override
//   public IRemoteMessageService getMessageToolServiceProxy() throws RemoteException {
//      if(exportedRemoteMessageService == null){
//         IRemoteMessageService service = ServiceUtility.getService(IRemoteMessageService.class, 30000);
//         exportedRemoteMessageService = (IRemoteMessageService)this.serviceConnector.export(service);
//      }
//      return exportedRemoteMessageService;
//   }

   @Override
   public IRemoteCommandConsole getCommandConsole() throws RemoteException {
      OseeLog.log(RemoteTestEnvironment.class, Level.FINE, "Remote command onsole requested");
      ICommandManager commandManager = ServiceUtility.getService(ICommandManager.class);
      RemoteShell shell = new RemoteShell(commandManager);

      IRemoteCommandConsole exportedConsole;
      try {
         exportedConsole = (IRemoteCommandConsole) serviceConnector.export(shell);
      } catch (Exception ex) {
         throw new RemoteException("failed to export remote console", ex);
      }
      lock.lock();
      try {
         exportedConsoles.put(exportedConsole, shell);
      } finally {
         lock.unlock();
      }
      return exportedConsole;
   }

   @Override
   public void closeCommandConsole(IRemoteCommandConsole console) throws RemoteException {
      RemoteShell shell;
      lock.lock();
      try {
         shell = exportedConsoles.remove(console);
      } finally {
         lock.unlock();
      }
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
      public void addModelActivityListener(IModelListener listener, ModelKey<?> key) throws RemoteException {
         env.getModelManager().addModelActivityListener(listener, key);
      }

      @Override
      public void removeModelActivityListener(IModelListener listener, ModelKey<?> key) throws RemoteException {
         env.getModelManager().removeModelActivityListener(listener, key);
      }

      @SuppressWarnings("rawtypes")
      @Override
      public List<ModelKey> getRegisteredModels() throws RemoteException {
         return env.getModelManager().getRegisteredModels();
      }

      @Override
      @SuppressWarnings({ "unchecked", "rawtypes" })
      public Remote getRemoteModel(ModelKey<?> key) throws RemoteException {
         try {
            Class modelClass;
            try {
               modelClass = env.loadClassFromMessageLoader(key.getClassName());
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
      public void releaseReference(ModelKey<?> key) throws RemoteException {
         env.getModelManager().releaseReference(key);
      }

      @Override
      @SuppressWarnings({ "unchecked", "rawtypes" })
      public void changeModelState(ModelKey<?> key, ModelState state) throws RemoteException {
         Class modelClass;
         try {
            modelClass = env.loadClassFromMessageLoader(key.getClassName());
         } catch (ClassNotFoundException ex) {
            throw new RemoteException("Could not load model class: " + key.getClassName());
         }
         key.setModelClass(modelClass);
         env.getModelManager().changeModelState(key, state);
      }

      @Override
      public ModelState getModelState(ModelKey<?> key) throws RemoteException {
         return env.getModelManager().getModelState(key);
      }

      @SuppressWarnings("rawtypes")
      @Override
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

}
