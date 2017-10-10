/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class SelectionProviderIntermediate implements IPostSelectionProvider {

   @SuppressWarnings("rawtypes")
   private final ListenerList selectionListeners = new ListenerList();

   @SuppressWarnings("rawtypes")
   private final ListenerList postSelectionListeners = new ListenerList();

   private ISelectionProvider delegate;

   private final ISelectionChangedListener selectionListener = new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
         if (event.getSelectionProvider() == delegate) {
            fireSelectionChanged(event.getSelection());
         }
      }
   };

   private final ISelectionChangedListener postSelectionListener = new ISelectionChangedListener() {
      @Override
      public void selectionChanged(SelectionChangedEvent event) {
         if (event.getSelectionProvider() == delegate) {
            firePostSelectionChanged(event.getSelection());
         }
      }
   };

   /**
    * Sets a new selection provider to delegate to. Selection listeners registered with the previous delegate are
    * removed before.
    *
    * @param newDelegate new selection provider
    */
   public void setSelectionProviderDelegate(ISelectionProvider newDelegate) {
      if (delegate == newDelegate) {
         return;
      }
      if (delegate != null) {
         delegate.removeSelectionChangedListener(selectionListener);
         if (delegate instanceof IPostSelectionProvider) {
            ((IPostSelectionProvider) delegate).removePostSelectionChangedListener(postSelectionListener);
         }
      }
      delegate = newDelegate;
      if (newDelegate != null) {
         newDelegate.addSelectionChangedListener(selectionListener);
         if (newDelegate instanceof IPostSelectionProvider) {
            ((IPostSelectionProvider) newDelegate).addPostSelectionChangedListener(postSelectionListener);
         }
         fireSelectionChanged(newDelegate.getSelection());
         firePostSelectionChanged(newDelegate.getSelection());
      }
   }

   protected void fireSelectionChanged(ISelection selection) {
      fireSelectionChanged(selectionListeners, selection);
   }

   protected void firePostSelectionChanged(ISelection selection) {
      fireSelectionChanged(postSelectionListeners, selection);
   }

   @SuppressWarnings("rawtypes")
   private void fireSelectionChanged(ListenerList list, ISelection selection) {
      SelectionChangedEvent event = new SelectionChangedEvent(delegate, selection);
      Object[] listeners = list.getListeners();
      for (int i = 0; i < listeners.length; i++) {
         ISelectionChangedListener listener = (ISelectionChangedListener) listeners[i];
         listener.selectionChanged(event);
      }
   }

   // IPostSelectionProvider Implementation

   @SuppressWarnings("unchecked")
   @Override
   public void addSelectionChangedListener(ISelectionChangedListener listener) {
      selectionListeners.add(listener);
   }

   @Override
   public void removeSelectionChangedListener(ISelectionChangedListener listener) {
      selectionListeners.remove(listener);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
      postSelectionListeners.add(listener);
   }

   @Override
   public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
      postSelectionListeners.remove(listener);
   }

   @Override
   public ISelection getSelection() {
      return delegate == null ? null : delegate.getSelection();
   }

   @Override
   public void setSelection(ISelection selection) {
      if (delegate != null) {
         delegate.setSelection(selection);
      }
   }

}
