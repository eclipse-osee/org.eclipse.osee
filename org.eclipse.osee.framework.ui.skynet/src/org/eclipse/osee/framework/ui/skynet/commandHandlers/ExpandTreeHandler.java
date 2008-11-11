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

import java.util.Iterator;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Theron Virgin
 */
public class ExpandTreeHandler extends AbstractHandler {
   private TreeViewer treeViewer;
   private IStructuredSelection structuredSelection;

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent arg0) throws ExecutionException {
      Iterator<?> iter = structuredSelection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), TreeViewer.ALL_LEVELS);
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
    */
   @Override
   public boolean isEnabled() {
      if (PlatformUI.getWorkbench().isClosing()) {
         return false;
      }
      structuredSelection = null;
      ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();
      treeViewer = selectionProvider instanceof TreeViewer ? (TreeViewer) selectionProvider : null;

      if (treeViewer != null && treeViewer.getSelection() instanceof IStructuredSelection) {
         structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
      }
      return treeViewer != null && structuredSelection != null;
   }
}
