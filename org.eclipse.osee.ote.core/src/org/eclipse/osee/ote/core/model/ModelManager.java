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
package org.eclipse.osee.ote.core.model;

import java.lang.ref.WeakReference;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.TestException;
import org.eclipse.osee.ote.core.environment.TestEnvironment;
import org.eclipse.osee.ote.core.environment.console.ConsoleCommand;
import org.eclipse.osee.ote.core.environment.console.ConsoleShell;
import org.eclipse.osee.ote.core.framework.DestroyableService;

@SuppressWarnings("unchecked")
/**
 * @author Andrew M. Finkbeiner
 */
public class ModelManager implements IModelManager, DestroyableService {

   private List<IModelListener> modelListeners;
   private Map<ModelKey, List<IModelListener>> modelListenerMap;
   private Map<ModelKey, IModel> models;
   private List<ModelKey> registeredModels;
   private Map<ModelKey, Integer> referenceCountOfModels;
   private WeakReference<TestEnvironment> testEnvironment;

   private boolean isDestroyed = false;
   private final class ModelInfoCmd extends ConsoleCommand {

      protected ModelInfoCmd() {
         super("modinfo", "lists information about all currently running models");
      }

      @Override
      protected void doCmd(ConsoleShell shell, String[] switches, String[] args) {
         for (Map.Entry<ModelKey, IModel> entry : models.entrySet()) {
            try {
               println(String.format("%s: state=%s", entry.getKey().getName(), entry.getValue().getState().name()));
            } catch (Exception e) {
               println("exception while getting model info for " + entry.getKey().getName());
               printStackTrace(e);
            }
         }
      }

   }

   public ModelManager(TestEnvironment testEnvironment) {
      this.testEnvironment = new WeakReference<TestEnvironment>(testEnvironment);
      models = new HashMap<ModelKey, IModel>();
      registeredModels = new ArrayList<ModelKey>();
      referenceCountOfModels = new HashMap<ModelKey, Integer>();
      modelListeners = new ArrayList<IModelListener>();
      modelListenerMap = new HashMap<ModelKey, List<IModelListener>>();
   }

   public <CLASSTYPE extends IModel> CLASSTYPE getModel(ModelKey<CLASSTYPE> key) {
      if (models.containsKey(key)) {
         referenceCountOfModels.put(key, referenceCountOfModels.get(key).intValue() + 1);
         return (CLASSTYPE) models.get(key);
      } else {
         CLASSTYPE model = null;
         try {
            model = createModel(key);
         } catch (Exception e) {
            this.testEnvironment.get().getLogger().severe("COULD NOT CREATE MODEL:\n" + e);
            OseeLog.log(TestEnvironment.class,
                  Level.SEVERE, e.getMessage(), e);
         }

         return model;
      }
   }

   private IModel findModel(ModelKey key) {
      return models.get(findModelKey(models.keySet(), key));
   }

   private ModelKey findModelKey(Collection<ModelKey> list, ModelKey key) {
      for (ModelKey current : list) {
         if (current.equals(key)) return current;
      }
      return null;
   }

   //   private void incrementReferenceCount(ModelKey key)
   //   {
   //      ModelKey realKey = findModelKey(referenceCountOfModels.keySet(), key);
   //      referenceCountOfModels.put(realKey, referenceCountOfModels.get(key).intValue() + 1);
   //   }
   //   
   //   private void decrementReferenceCount(ModelKey key)
   //   {
   //      ModelKey realKey = findModelKey(referenceCountOfModels.keySet(), key);
   //      referenceCountOfModels.put(realKey, referenceCountOfModels.get(key).intValue() -1);
   //   }
   //   
   //   private Integer getReferenceCount(ModelKey key)
   //   {
   //      ModelKey realKey = findModelKey(referenceCountOfModels.keySet(), key);
   //      return referenceCountOfModels.get(realKey);
   //   }

   /**
    * @param <CLASSTYPE>
    * @param key
    * @return IModel
    * @throws ClassNotFoundException
    * @throws IllegalAccessException
    * @throws InstantiationException
    * @throws RemoteException
    */
   private <CLASSTYPE extends IModel> CLASSTYPE createModel(ModelKey<CLASSTYPE> key) throws ClassNotFoundException, InstantiationException, IllegalAccessException, RemoteException {
      if (key == null) {
         throw new IllegalArgumentException("key cannot be null");
      }
      notifyModelPreCreate(key);
      CLASSTYPE model = (CLASSTYPE) key.getModelClass().newInstance();
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      try {
         Thread.currentThread().setContextClassLoader(key.getModelClass().getClassLoader());
         model.init(testEnvironment.get(), key);
         referenceCountOfModels.put(key, 1);
         models.put(key, model);
         this.registerModel(key);
         notifyModelPostCreate(key);
         return model;
      } finally {
         Thread.currentThread().setContextClassLoader(loader);
      }
   }

   public void addModelActivityListener(IModelListener listener) {
      if(listener  == null){
         OseeLog.log(ModelManager.class, Level.SEVERE, "null listener was being added to model managerl");
      } else {
         if (!collectionContainsListener(modelListeners, listener)) {
            modelListeners.add(listener);
         } else {
         }
      }
   }

   /**
    * @param modelListeners2
    * @param listener
    * @return
    */
   private boolean collectionContainsListener(List<IModelListener> collection, IModelListener listener) {
      try {
         for (IModelListener current : collection) {
            if (current.getHashCode() == listener.getHashCode()) return true;
         }
      } catch (RemoteException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE,
               e.getMessage(), e);
      }
      return false;
   }

   public void addModelActivityListener(IModelListener listener, ModelKey key) {
      if (listener == null) {
         Exception e = new NullPointerException();
         OseeLog.log(TestEnvironment.class, Level.SEVERE,
               e.getMessage(), e);
         return;
      }
      if (modelListenerMap.containsKey(key)) {
         if (!collectionContainsListener(modelListenerMap.get(key), listener)) {
            modelListenerMap.get(key).add(listener);
         }
      } else {
         List<IModelListener> newList = new ArrayList<IModelListener>();
         newList.add(listener);
         modelListenerMap.put(key, newList);
      }
   }

   public void removeModelActivityListener(IModelListener listener) {
      try {
         Iterator<IModelListener> iter = modelListeners.iterator();
         while (iter.hasNext()) {
            if (iter.next().getHashCode() == listener.getHashCode()) iter.remove();
         }
      } catch (RemoteException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE,
               e.getMessage(), e);
      }
   }

   public void removeModelActivityListener(IModelListener listener, ModelKey key) {
      try {
         if (modelListenerMap.containsKey(key)) {
            List<IModelListener> list = modelListenerMap.get(key);
            Iterator<IModelListener> iter = list.iterator();
            while (iter.hasNext()) {
               if (iter.next().getHashCode() == listener.getHashCode()) iter.remove();
            }
         }
      } catch (RemoteException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE,
               e.getMessage(), e);
      }
   }

   public <CLASSTYPE extends IModel> void disposeModel(ModelKey key) {
      IModel localModel = findModel(key);
      //      referenceCountOfModels.remove(findModelKey(referenceCountOfModels.keySet(),key));
      referenceCountOfModels.remove(key);
      models.remove(key);
      try {
         localModel.dispose();

         notifyModelDispose(localModel.getKey());
      } catch (RemoteException ex) {
         throw new TestException("Could Not dispose of the model " + localModel + ":\n" + ex, Level.WARNING);
      }
   }

   public List<ModelKey> getRegisteredModels() {
      return registeredModels;
   }

   public void releaseReference(IModel model) {
      try {
         if (models.containsKey(model.getKey())) {
            referenceCountOfModels.put(model.getKey(), referenceCountOfModels.get(model.getKey()).intValue() - 1);
            //            decrementReferenceCount(model.getKey());
            if (referenceCountOfModels.get(model.getKey()).intValue() <= 0) {
               disposeModel(model.getKey());
            }
         }
      } catch (RemoteException ex) {
         throw new TestException("Could not release reference to " + model + ":\n" + ex, Level.WARNING);
      }
   }

   public void releaseAllReferences(ModelKey key) {
      if (!models.containsKey(key)) return;

      referenceCountOfModels.remove(key);
      disposeModel(key);

   }

   public void releaseReference(ModelKey key) {
      if (models.containsKey(key)) this.releaseReference(models.get(key));
   }

   public <CLASSTYPE extends IModel> void registerModel(ModelKey key) {
      ModelKey cleanKey = new ModelKey(key);
      if (!registeredModels.contains(cleanKey)) {
         registeredModels.add(cleanKey);
      }
      //      else {
      //         throw new TestException(String.format("[%s] is already registered to this model manager.", key.toString()), Level.WARNING);
      //      }
   }

   private <CLASSTYPE extends IModel> void notifyModelPreCreate(ModelKey key) throws RemoteException {
      ModelKey cleanKey = new ModelKey(key);
      for (IModelListener listener : modelListeners) {
            listener.onModelPreCreate(cleanKey);
      }

      if (modelListenerMap.containsKey(cleanKey)) {
         for (IModelListener listener : modelListenerMap.get(cleanKey))
            listener.onModelPreCreate(cleanKey);
      }
   }

   private <CLASSTYPE extends IModel> void notifyModelPostCreate(ModelKey key) throws RemoteException {
      ModelKey cleanKey = new ModelKey(key);
      for (IModelListener listener : modelListeners) {
         listener.onModelPostCreate(cleanKey);
      }

      if (modelListenerMap.containsKey(cleanKey)) {
         for (IModelListener listener : modelListenerMap.get(cleanKey))
            listener.onModelPostCreate(cleanKey);
      }
   }

   public void notifyModeStateListener(ModelKey key, ModelState state) throws RemoteException {
      ModelKey cleanKey = new ModelKey(key);
      for (IModelListener listener : modelListeners) {
         listener.onModelStateChange(cleanKey, state);
      }

      if (modelListenerMap.containsKey(cleanKey)) {
         for (IModelListener listener : modelListenerMap.get(cleanKey))
            listener.onModelStateChange(cleanKey, state);
      }
   }

   private <CLASSTYPE extends IModel> void notifyModelDispose(ModelKey key) throws RemoteException {
      ModelKey cleanKey = new ModelKey(key);
      for (IModelListener listener : modelListeners) {
         listener.onModelDispose(cleanKey);
      }

      if (modelListenerMap.containsKey(cleanKey)) {
         for (IModelListener listener : modelListenerMap.get(cleanKey))
            listener.onModelDispose(cleanKey);
      }
   }

   public void changeModelState(ModelKey key, ModelState state) {
      try {
         if (state == ModelState.PAUSED) {
            if (models.containsKey(key)) getModel(key).turnModelOff();
         } else if (state == ModelState.RUNNING) {
            getModel(key).turnModelOn();
         } else
            this.releaseAllReferences(key);
      } catch (RemoteException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE,
               e.getMessage(), e);
      }
   }

   public ModelState getModelState(ModelKey key) throws RemoteException {
      if (models.containsKey(key))
         return models.get(key).getState();
      else
         return ModelState.DISPOSED;
   }
   
   public synchronized void destroy(){
      if (isDestroyed) {
         return;
      }
      for(ModelKey modelKey :registeredModels){
         releaseAllReferences(modelKey);
      }
      modelListeners.clear();
      modelListenerMap.clear();
      models.clear();
      registeredModels.clear();
      referenceCountOfModels.clear();
      isDestroyed = true;
   }

}
