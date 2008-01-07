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
package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.HandlerEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

/**
 * @author Paul K. Waldfogel
 * @author Jeff C. Phillips
 */
public abstract class AbstractSelectionChangedHandler extends AbstractHandler {
   private final HandlerEvent enabledChangedEvent = new HandlerEvent(this, true, false);
   private ISelectionProvider myISelectionProvider;
   private SelectionChangedListener selectionChanhedListener;

   public AbstractSelectionChangedHandler() {
      selectionChanhedListener = new SelectionChangedListener();

      IWorkbenchPage workbenchPage = AWorkbench.getActivePage();
      if (workbenchPage != null) {
         IWorkbenchPart myIWorkbenchPart = workbenchPage.getActivePart();
         IWorkbenchPartSite myIWorkbenchPartSite = myIWorkbenchPart.getSite();
         myISelectionProvider = myIWorkbenchPartSite.getSelectionProvider();
         myISelectionProvider.addSelectionChangedListener(selectionChanhedListener);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.AbstractHandler#dispose()
    */
   @Override
   public void dispose() {
      myISelectionProvider.removeSelectionChangedListener(selectionChanhedListener);
      super.dispose();
   }

   private class SelectionChangedListener implements ISelectionChangedListener {
      /* (non-Javadoc)
       * @see org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.eclipse.jface.viewers.SelectionChangedEvent)
       */
      public void selectionChanged(SelectionChangedEvent event) {
         fireHandlerChanged(enabledChangedEvent);
      }
   }

   protected IStructuredSelection getActiveSiteSelection() {
      IStructuredSelection structuredSelection = null;
      IWorkbenchPage workbenchPage = AWorkbench.getActivePage();
      if (workbenchPage != null) {
         ISelectionProvider provider = workbenchPage.getActivePart().getSite().getSelectionProvider();
         if (provider != null) {
            structuredSelection = (IStructuredSelection) provider.getSelection();
         }
      }

      if (structuredSelection == null) {
         structuredSelection = new IStructuredSelection() {
            List<Object> emptyList = new ArrayList<Object>();

            public Object getFirstElement() {
               return null;
            }

            @SuppressWarnings("unchecked")
            public Iterator iterator() {
               return emptyList.iterator();
            }

            public int size() {
               return 0;
            }

            public Object[] toArray() {
               return new Object[0];
            }

            @SuppressWarnings("unchecked")
            public List toList() {
               return emptyList;
            }

            public boolean isEmpty() {
               return true;
            }
         };
      }
      return structuredSelection;
   }
}
