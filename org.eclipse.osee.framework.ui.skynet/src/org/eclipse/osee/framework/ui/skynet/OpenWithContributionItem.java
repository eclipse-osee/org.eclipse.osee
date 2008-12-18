package org.eclipse.osee.framework.ui.skynet;

import java.util.Collections;
import java.util.List;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * @author Jeff C. Phillips
 */
public class OpenWithContributionItem extends CompoundContributionItem {
   public OpenWithContributionItem() {
   }

   public OpenWithContributionItem(String id) {
      super(id);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.actions.CompoundContributionItem#getContributionItems()
    */
   @Override
   protected IContributionItem[] getContributionItems() {
      ISelectionProvider selectionProvider =
            AWorkbench.getActivePage().getActivePart().getSite().getSelectionProvider();

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         List<Artifact> artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

         return new IContributionItem[] {new CommandContributionItem(
               PlatformUI.getWorkbench().getActiveWorkbenchWindow(), "org.eclipse.osee.framework.ui.open.with",
               "org.eclipse.osee.framework.ui.skynet.openartifacteditor.command", Collections.emptyMap(), null, null,
               null, null, null, null, SWT.NONE)};
      }
      return null;
   }
}
