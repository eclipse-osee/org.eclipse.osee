/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/


import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;

import org.eclipse.osee.ote.core.model.IModel;
import org.eclipse.osee.ote.core.model.ModelKey;

/**
 * @author Michael P. Masterson
 */
public class ModelFinderService {
   private final ConcurrentHashMap<ModelKey<?>, IModel> models = new ConcurrentHashMap<ModelKey<?>, IModel>();

   private static ModelFinderService instance;

   public static ModelFinderService getInstance() {

      return instance;
   }

   public void start() {
      ModelFinderService.instance = this;
   }

   public void stop() {
      models.clear();
   }

   public void addModel(IModel newModel) {
      try {
         models.put(newModel.getKey(), newModel);
      } catch (RemoteException ex) {
         ex.printStackTrace();
      }
   }

   public void removeModel(IModel newModel) {
      try {
         models.remove(newModel.getKey());
      } catch (RemoteException ex) {
         ex.printStackTrace();
      }
   }

   public <T extends IModel> T getModel(ModelKey<T> key) {
      IModel retVal = models.get(key);
      return (T) retVal;
   }

}
