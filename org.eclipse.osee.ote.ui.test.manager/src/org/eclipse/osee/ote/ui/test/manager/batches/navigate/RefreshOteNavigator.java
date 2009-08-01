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
package org.eclipse.osee.ote.ui.test.manager.batches.navigate;

import org.eclipse.osee.ote.ui.navigate.OteNavigateView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class RefreshOteNavigator implements ITestBatchDataListener {

   @Override
   public void dataAddedEvent(TestBatchData data) {
      OteNavigateView navigator = getOteNavigator();
      if (navigator != null) {
         navigator.refresh();
      }
   }

   @Override
   public void dataRemovedEvent(TestBatchData data) {
      OteNavigateView navigator = getOteNavigator();
      if (navigator != null) {
         navigator.refresh();
      }
   }

   private OteNavigateView getOteNavigator() {
      OteNavigateView toReturn = null;
      IWorkbenchPage[] pages = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPages();
      for (IWorkbenchPage page : pages) {
         IViewPart part = page.findView(OteNavigateView.VIEW_ID);
         if (part != null) {
            if (part instanceof OteNavigateView) {
               toReturn = (OteNavigateView) part;
               break;
            }
         }
      }
      return toReturn;
   }
}
