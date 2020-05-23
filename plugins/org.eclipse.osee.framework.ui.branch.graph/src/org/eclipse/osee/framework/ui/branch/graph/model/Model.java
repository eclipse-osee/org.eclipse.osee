/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

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
