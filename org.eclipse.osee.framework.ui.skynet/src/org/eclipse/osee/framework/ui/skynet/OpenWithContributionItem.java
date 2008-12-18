package org.eclipse.osee.framework.ui.skynet;

import java.util.Collections;
import java.util.List;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
      List<IRenderer> commonRenders = null;

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         List<Artifact> artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

         try {
            commonRenders = RendererManager.getCommonSpecializedEditRenders(artifacts);
         } catch (OseeCoreException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }

         for (IRenderer render : commonRenders) {
            if (render instanceof WordTemplateRenderer) {
               return new IContributionItem[] {
                     new CommandContributionItem(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                           "org.eclipse.osee.framework.ui.skynet.artifacteditor.command",
                           "org.eclipse.osee.framework.ui.skynet.artifacteditor.command", Collections.emptyMap(), null,
                           null, null, null, null, null, SWT.NONE),
                     new CommandContributionItem(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                           render.getCommandId(), render.getCommandId(), Collections.emptyMap(),
                           WordRenderer.getImageDescriptor(), null, null, null, null, null, SWT.NONE)};
            }
         }

         return new IContributionItem[] {new CommandContributionItem(
               PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
               "org.eclipse.osee.framework.ui.skynet.artifacteditor.command",
               "org.eclipse.osee.framework.ui.skynet.artifacteditor.command", Collections.emptyMap(), null, null, null,
               null, null, null, SWT.NONE)};
      }
      return null;
   }
}
