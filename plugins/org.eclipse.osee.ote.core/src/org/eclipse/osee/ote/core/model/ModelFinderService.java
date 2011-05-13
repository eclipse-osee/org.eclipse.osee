/*
 * Created on Mar 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

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
         OseeLog.log(ModelFinderService.class, Level.INFO, null, "### Adding model %s\n", newModel.getKey());
         models.put(newModel.getKey(), newModel);
      } catch (RemoteException ex) {
         ex.printStackTrace();
      }
   }

   public void removeModel(IModel newModel) {
      try {
         OseeLog.log(ModelFinderService.class, Level.INFO, null, "### Removing model %s\n", newModel.getKey());
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
