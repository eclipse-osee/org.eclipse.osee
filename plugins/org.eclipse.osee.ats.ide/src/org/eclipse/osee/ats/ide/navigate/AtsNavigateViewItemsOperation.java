/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.navigate;

import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;

/**
 * @author Donald G. Dunne
 */
public class AtsNavigateViewItemsOperation extends AbstractOperation {

   private final NavigateView navigateView;

   public AtsNavigateViewItemsOperation(NavigateView navigateView) {
      super("Loading ATS Navigate View Items", Activator.PLUGIN_ID);
      this.navigateView = navigateView;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      List<XNavigateItem> navigateItems = NavigateViewItems.getInstance().getSearchNavigateItems();
      setRefresher(navigateItems);
   }

   private void setRefresher(List<XNavigateItem> navigateItems) {
      for (XNavigateItem item : navigateItems) {
         item.setRefresher(navigateView);
         setRefresher(item.getChildren());
      }
   }

}
