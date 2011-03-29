/*
 * Created on Mar 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */

package org.eclipse.osee.ote.core.model;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.osee.framework.jdk.core.type.MutableInteger;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.ote.core.environment.TestEnvironment;

/**
 * @author Michael P. Masterson
 */
public class ModelReference {
   private final IModel model;
   private final MutableInteger referenceCount;
   private final List<IModelListener> listeners;

   public ModelReference(IModel model) {
      this.model = model;
      this.referenceCount = new MutableInteger(1);
      this.listeners = new ArrayList<IModelListener>();
   }

   public IModel getModel() {
      return model;
   }

   public Integer getReferenceCount() {
      return referenceCount.getValue();
   }

   public void incrementReferenceCount() {
      referenceCount.setValue(referenceCount.getValue() + 1);
   }

   public void decrementReferenceCount() {
      referenceCount.setValue(referenceCount.getValue() - 1);
   }

   public void addModelActivityListener(IModelListener listener) {
      if (listener == null) {
         Exception e = new NullPointerException();
         OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
         return;
      }
      if (!collectionContainsListener(listener)) {
         listeners.add(listener);
      }
   }

   public void removeModelActivityListener(IModelListener listener) throws RemoteException {
      Iterator<IModelListener> iter = listeners.iterator();
      while (iter.hasNext()) {
         if (iter.next().getHashCode() == listener.getHashCode()) {
            iter.remove();
         }
      }
   }

   /**
    * Can not just use equals because of remote references.
    *
    * @param listener
    * @return
    */
   private boolean collectionContainsListener(IModelListener listener) {
      try {
         for (IModelListener current : listeners) {
            if (current.getHashCode() == listener.getHashCode()) {
               return true;
            }
         }
      }
      catch (RemoteException e) {
         OseeLog.log(TestEnvironment.class, Level.SEVERE, e.getMessage(), e);
      }
      return false;
   }

   public <CLASSTYPE extends IModel> void notifyModelPreCreate() throws RemoteException {
      for (IModelListener listener : listeners) {
         listener.onModelPreCreate(model.getKey());
      }
   }

   public <CLASSTYPE extends IModel> void notifyModelPostCreate() throws RemoteException {
      for (IModelListener listener : listeners) {
         listener.onModelPostCreate(model.getKey());
      }
   }

   public void notifyModelStateListener() throws RemoteException {
      for (IModelListener listener : listeners) {
         listener.onModelStateChange(model.getKey(), model.getState());
      }
   }

   public <CLASSTYPE extends IModel> void notifyModelDispose() throws RemoteException {
      for (IModelListener listener : listeners) {
         listener.onModelDispose(model.getKey());
      }
   }

}
