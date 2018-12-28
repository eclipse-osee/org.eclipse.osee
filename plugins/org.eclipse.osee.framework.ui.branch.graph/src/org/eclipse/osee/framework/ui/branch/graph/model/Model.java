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
package org.eclipse.osee.framework.ui.branch.graph.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Roberto E. Escobar
 */
public class Model {
   private final Set<IModelListener> listeners;

   public Model() {
      this.listeners = Collections.synchronizedSet(new HashSet<IModelListener>());
   }

   public void addListener(IModelListener listener) {
      if (listener != null) {
         listeners.add(listener);
      }
   }

   public void removeListener(IModelListener listener) {
      if (listener != null) {
         listeners.remove(listener);
      }
   }

   protected List<IModelListener> getListeners() {
      return new ArrayList<>(listeners);
   }

   protected void fireModelEvent() {
      fireModelEvent(null);
   }

   protected void fireModelEvent(Object object) {
      for (IModelListener listener : listeners) {
         listener.onModelEvent(object);
      }
   }
}
