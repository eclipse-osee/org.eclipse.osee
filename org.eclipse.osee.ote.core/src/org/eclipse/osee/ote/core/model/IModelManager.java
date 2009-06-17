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

import java.rmi.RemoteException;
import java.util.List;
@SuppressWarnings("unchecked")
/**
 * @author Andrew M. Finkbeiner
 */
public interface IModelManager {
   <CLASSTYPE extends IModel> CLASSTYPE getModel(ModelKey<CLASSTYPE> key);
   <CLASSTYPE extends IModel> void disposeModel(ModelKey key);
   <CLASSTYPE extends IModel> void registerModel(ModelKey key);
   void changeModelState(ModelKey key, ModelState state);
   void notifyModeStateListener(ModelKey key, ModelState state) throws RemoteException;
   List<ModelKey> getRegisteredModels();
   void releaseReference(IModel model);
   void releaseAllReferences(ModelKey key);
   void addModelActivityListener(IModelListener listener);
   void removeModelActivityListener(IModelListener listener);
   void addModelActivityListener(IModelListener listener, ModelKey key);
   void removeModelActivityListener(IModelListener listener, ModelKey key);
   ModelState getModelState(ModelKey key) throws RemoteException;
   /**
    * Releases a single reference of the model given by the class.
    * @param key
    */
   void releaseReference(ModelKey key);
   void destroy();
}
