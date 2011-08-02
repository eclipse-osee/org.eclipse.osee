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
package org.eclipse.osee.ote.core.model;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;

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
      OseeLog.log(ModelFinderService.class, Level.INFO, "### Starting model finder");
      ModelFinderService.instance = this;
   }

   public void stop() {
      OseeLog.log(ModelFinderService.class, Level.INFO, "### Stopping model finder");
      models.clear();
   }

   public void addModel(IModel newModel) {
      try {
         OseeLog.logf(ModelFinderService.class, Level.INFO, "### Adding model %s\n", newModel.getKey());
         models.put(newModel.getKey(), newModel);
      } catch (RemoteException ex) {
         ex.printStackTrace();
      }
   }

   public void removeModel(IModel newModel) {
      try {
         OseeLog.logf(ModelFinderService.class, Level.INFO, "### Removing model %s\n", newModel.getKey());
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
