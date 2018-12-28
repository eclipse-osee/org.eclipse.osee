/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.util;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class SelectionProvider implements ISelectionProvider {
   private final ListenerList<ISelectionChangedListener> listenerList;
   private ISelection selection;

   public SelectionProvider() {
      this.listenerList = new ListenerList<>();
   }

   @Override
   public ISelection getSelection() {
      return selection;
   }

   @Override
   public void setSelection(ISelection selection) {
      this.selection = selection;
      Object[] listeners = listenerList.getListeners();
      for (int i = 0; i < listeners.length; i++) {
         ((ISelectionChangedListener) listeners[i]).selectionChanged(new SelectionChangedEvent(this, selection));
      }
   }

   @Override
   public void removeSelectionChangedListener(ISelectionChangedListener listener) {
      listenerList.remove(listener);
   }

   @Override
   public void addSelectionChangedListener(ISelectionChangedListener listener) {
      listenerList.add(listener);
   }
}