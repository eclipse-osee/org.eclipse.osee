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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.ui.skynet.TableViewerReport;
import org.eclipse.osee.framework.ui.skynet.TreeViewerReport;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerTreeReport;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Jeff C. Phillips
 */
public class ViewerReportHandler extends AbstractHandler {

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.core.commands.AbstractHandler#execute(org.eclipse.core.commands.ExecutionEvent)
    */
   @Override
   public Object execute(ExecutionEvent event) throws ExecutionException {
      if (HandlerUtil.getActivePartChecked(event) instanceof ViewPart) {
         ViewPart view = (ViewPart) HandlerUtil.getActivePartChecked(event);
         IWorkbenchPartSite myIWorkbenchPartSite = view.getSite();
         Object selectionProvider = myIWorkbenchPartSite.getSelectionProvider();

         if (selectionProvider instanceof XViewer) {
            (new XViewerTreeReport((XViewer) selectionProvider)).open();
         } else if (selectionProvider instanceof TableViewer) {
            (new TableViewerReport((TableViewer) selectionProvider)).open();
         } else if (selectionProvider instanceof TreeViewer) {
            (new TreeViewerReport((TreeViewer) selectionProvider)).open();
         }
      }
      return null;
   }
}
