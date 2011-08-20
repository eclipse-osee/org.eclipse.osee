package org.eclipse.osee.ote.model.manager;
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


import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.console.ConsoleCommand;
import org.eclipse.osee.ote.core.environment.console.ConsoleShell;
import org.eclipse.osee.ote.core.framework.DestroyableService;
import org.eclipse.osee.ote.core.model.IModel;
import org.eclipse.osee.ote.core.model.IModelListener;
import org.eclipse.osee.ote.core.model.IModelManager;
import org.eclipse.osee.ote.core.model.ModelKey;
import org.eclipse.osee.ote.core.model.ModelState;

@SuppressWarnings("unchecked")
/**
 * @author Andrew M. Finkbeiner
 */
public class ModelManager implements IModelManager, DestroyableService {

   private final List<IModelListener> modelListeners;
   private final Map<ModelKey<?>, ModelReference> modelReferenceMap;
   private final List<ModelKey> registeredModels;
   private final WeakReference<TestEnvironment> testEnvironment;

   private boolean isDestroyed = false;
   private final class ModelInfoCmd extends ConsoleCommand {

      protected ModelInfoCmd() {
         super("modinfo", "lists information about all currently running models");
      }

      @Override
      protected void doCmd(ConsoleShell shell, String[] switches, String[] args) {
         for (Entry<ModelKey<?>, ModelReference> entry : modelReferenceMap.entrySet()) {
            try {
               println(String.format("%s: state=%s", entry.getKey().getName(), entry.getValue().getModel().getState().name()));
            } catch (Exception e) {
               println("exception while getting model info for " + entry.getKey().getName());
               printStackTrace(e);
            }
         }
      }

   }

   public ModelManager(TestEnvironment testEnvironment) {
      this.testEnvironment = new WeakReference<TestEnvironment>(testEnvironment);
      registeredModels = new ArrayList<ModelKey>();
      modelListeners = new ArrayList<IModelListener>();
      modelReferenceMap = new HashMap<ModelKey<?>, ModelReference>();
   }

   @Override
   public <CLASSTYPE extends IModel> CLASSTYPE getModel(ModelKey<CLASSTYPE> key) {
      if (modelReferenceMap.containsKey(key)) {
         ModelReference modelReference = modelReferenceMap.get(key);
         modelReference.incrementReferenceCount();
         return (CLASSTYPE) modelReference.getModel();
      } else {
         CLASSTYPE model = null;
         try {
            model = createModel(key);
         } catch (Exception e) {
            this.testEnvironment.get().getLogger().severe("COULD NOT CREATE MODEL:\n" + e);
            OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
         }

         return model;
      }
   }

   private IModel findModel(ModelKey<?> key) {
      return modelReferenceMap.get(findModelKey(modelReferenceMap.keySet(), key)).getModel();
   }

   private ModelKey<?> findModelKey(Collection<ModelKey<?>> list, ModelKey<?> key) {
      for (ModelKey<?> current : list) {
         if (current.equals(key)) {
            return current;
         }
      }
      return null;
   }

   private <CLASSTYPE extends IModel> CLASSTYPE createModel(ModelKey<CLASSTYPE> key) throws ClassNotFoundException, InstantiationException, IllegalAccessException, RemoteException {
      if (key == null) {
         throw new IllegalArgumentException("key cannot be null");
      }
      notifyModelPreCreate(key);
      CLASSTYPE model = findOrCreateModel(key);
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      try {
         Class<CLASSTYPE> modelClass = key.getModelClass();
         System.out.printf("############### model class = %s\n", modelClass);
         Thread.currentThread().setContextClassLoader(modelClass.getClassLoader());
         model.init(testEnvironment.get(), key);
         modelReferenceMap.put(key, new ModelReference(model));
         this.registerModel(key);
         notifyModelPostCreate(key);
         return model;
      } finally {
         Thread.currentThread().setContextClassLoader(loader);
      }
   }

   /**
    * @param <CLASSTYPE>
    * @param key
    * @return
    * @throws InstantiationException
    * @throws IllegalAccessException
    */
   private <CLASSTYPE extends IModel> CLASSTYPE findOrCreateModel(ModelKey<CLASSTYPE> key) throws InstantiationException, IllegalAccessException {
      System.out.printf("################################# Trying to find model %s...\n", key);
      System.out.printf("################################# Trying to find model %s...\n", key);
      CLASSTYPE model = ModelFinderService.getInstance().getModel(key);
      System.out.printf("################################ model found = %s\n", model );
      if( model == null )
      {
         System.out.println("############################# Getting the old fashioned way.");
         model = key.getModelClass().newInstance();
      }
      return model;
   }

   @Override
   public void addModelActivityListener(IModelListener listener) {
      if (listener == null) {
         OseeLog.log(ModelManager.class, Level.SEVERE, "null listener was being added to model managerl");
      } else {
         if (!collectionContainsListener(modelListeners, listener)) {
            modelListeners.add(listener);
         } else {
         }
      }
   }

   private boolean collectionContainsListener(List<IModelListener> collection, IModelListener listener) {
      try {
         for (IModelListener current : collection) {
            if (current.getHashCode() == listener.getHashCode()) {
               return true;
            }
         }
      } catch (RemoteException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
      }
      return false;
   }

   @Override
   public void addModelActivityListener(IModelListener listener, ModelKey<?> key) {
      if (listener == null) {
         Exception e = new NullPointerException();
         OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
         return;
      }
      if (modelReferenceMap.containsKey(key)) {
         modelReferenceMap.get(key).addModelActivityListener(listener);
      }
   }

   @Override
   public void removeModelActivityListener(IModelListener listener) {
      try {
         Iterator<IModelListener> iter = modelListeners.iterator();
         while (iter.hasNext()) {
            if (iter.next().getHashCode() == listener.getHashCode()) {
               iter.remove();
            }
         }
      } catch (RemoteException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
      }
   }

   @Override
   public void removeModelActivityListener(IModelListener listener, ModelKey<?> key) {
      try {
         if (modelReferenceMap.containsKey(key)) {
            ModelReference modelReference = modelReferenceMap.get(key);
            modelReference.removeModelActivityListener(listener);
         }
      } catch (RemoteException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
      }
   }

   @Override
   public <CLASSTYPE extends IModel> void disposeModel(ModelKey<?> key) {
      IModel localModel = findModel(key);
      modelReferenceMap.remove(key);
      try {
         localModel.dispose();
         notifyModelDispose(localModel.getKey());
      } catch (RemoteException ex) {
         throw new TestException("Could Not dispose of the model " + localModel + ":\n" + ex, Level.WARNING);
      }
   }

   @Override
   public List<ModelKey> getRegisteredModels() {
      return registeredModels;
   }

   @Override
   public void releaseReference(IModel model) {
      try {
         if (modelReferenceMap.containsKey(model.getKey())) {
            ModelReference modelReference = modelReferenceMap.get(model.getKey());
            modelReference.decrementReferenceCount();
            if (modelReference.getReferenceCount() <= 0) {
               disposeModel(model.getKey());
            }
         }
      } catch (RemoteException ex) {
         throw new TestException("Could not release reference to " + model + ":\n" + ex, Level.WARNING);
      }
   }

   @Override
   public void releaseAllReferences(ModelKey<?> key) {
      if (!modelReferenceMap.containsKey(key)) {
         return;
      }
      disposeModel(key);
      modelReferenceMap.remove(key);

   }

   @Override
   public void releaseReference(ModelKey<?> key) {
      if (modelReferenceMap.containsKey(key)) {
         this.releaseReference(modelReferenceMap.get(key).getModel());
      }
   }

   @Override
   public <CLASSTYPE extends IModel> void registerModel(ModelKey<?> key) {
      ModelKey<?> cleanKey = new ModelKey(key);
      if (!registeredModels.contains(cleanKey)) {
         registeredModels.add(cleanKey);
      }
   }

   private <CLASSTYPE extends IModel> void notifyModelPreCreate(ModelKey<?> key) throws RemoteException {
      ModelKey<?> cleanKey = new ModelKey(key);
      for (IModelListener listener : modelListeners) {
         listener.onModelPreCreate(cleanKey);
      }

      if (modelReferenceMap.containsKey(cleanKey)) {
         modelReferenceMap.get(cleanKey).notifyModelPreCreate();
      }
   }

   private <CLASSTYPE extends IModel> void notifyModelPostCreate(ModelKey<?> key) throws RemoteException {
      ModelKey<?> cleanKey = new ModelKey(key);
      for (IModelListener listener : modelListeners) {
         listener.onModelPostCreate(cleanKey);
      }

      if (modelReferenceMap.containsKey(cleanKey)) {
         modelReferenceMap.get(cleanKey).notifyModelPostCreate();
      }
   }

   @Override
   public void notifyModelStateListener(ModelKey<?> key, ModelState state) throws RemoteException {
      ModelKey<?> cleanKey = new ModelKey(key);
      for (IModelListener listener : modelListeners) {
         listener.onModelStateChange(cleanKey, state);
      }

      if (modelReferenceMap.containsKey(cleanKey)) {
         modelReferenceMap.get(cleanKey).notifyModelStateListener();
      }
   }

   @Override
   public void notifyModeStateListener(ModelKey key, ModelState state) throws RemoteException {
	   notifyModelStateListener(key, state);
   }

   private <CLASSTYPE extends IModel> void notifyModelDispose(ModelKey<?> key) throws RemoteException {
      @SuppressWarnings("rawtypes")
      ModelKey<?> cleanKey = new ModelKey(key);
      for (IModelListener listener : modelListeners) {
         listener.onModelDispose(cleanKey);
      }

      if (modelReferenceMap.containsKey(cleanKey)) {
         modelReferenceMap.get(cleanKey).notifyModelDispose();
      }
   }

   @Override
   public void changeModelState(ModelKey<?> key, ModelState state) {
      try {
         if (state == ModelState.PAUSED) {
            if (modelReferenceMap.containsKey(key)) {
               getModel(key).turnModelOff();
            }
         } else if (state == ModelState.RUNNING) {
            getModel(key).turnModelOn();
         } else {
            this.releaseAllReferences(key);
         }
      } catch (RemoteException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
      }
   }

   @Override
   public ModelState getModelState(ModelKey<?> key) throws RemoteException {
      if (modelReferenceMap.containsKey(key)) {
         return modelReferenceMap.get(key).getModel().getState();
      } else {
         return ModelState.DISPOSED;
      }
   }

   @Override
   public synchronized void destroy() {
      if (isDestroyed) {
         return;
      }
      for (ModelKey<?> modelKey : registeredModels) {
         releaseAllReferences(modelKey);
      }
      modelListeners.clear();
      registeredModels.clear();
      isDestroyed = true;
   }



}
