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

package org.eclipse.osee.framework.ui.skynet.commandHandlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.ui.plugin.util.CommandHandler;

/**
 * @author Theron Virgin
 */
public class CollapseTreeHandler extends CommandHandler {
   private TreeViewer treeViewer;

   @Override
   protected boolean isEnabledWithException(IStructuredSelection structuredSelection) {
      ISelectionProvider selectionProvider = getSelectionProvider();
      treeViewer = selectionProvider instanceof TreeViewer ? (TreeViewer) selectionProvider : null;

      return treeViewer != null;
   }

   @Override
   protected Object executeWithException(ExecutionEvent event, IStructuredSelection selection) {
      treeViewer.collapseAll();
      return null;
   }
}
