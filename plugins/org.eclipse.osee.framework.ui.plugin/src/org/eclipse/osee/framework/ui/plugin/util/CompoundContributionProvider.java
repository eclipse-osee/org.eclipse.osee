/*
 * Created on Oct 4, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.util;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;

public abstract class CompoundContributionProvider extends CompoundContributionItem {

   public CompoundContributionProvider(String id) {
      super(id);
   }

   public CompoundContributionProvider() {
      //do nothing
   }

   protected ISelectionProvider getSelectionProvider() {
      ISelectionProvider selectionProvider = null;
      IWorkbench workbench = PlatformUI.getWorkbench();
      if (!workbench.isStarting() && !workbench.isClosing()) {
         IWorkbenchPage page = AWorkbench.getActivePage();
         if (page != null) {
            IWorkbenchPart part = page.getActivePart();
            if (part != null) {
               IWorkbenchSite site = part.getSite();
               if (site != null) {
                  selectionProvider = site.getSelectionProvider();
               }
            }
         }
      }
      return selectionProvider;
   }
}
