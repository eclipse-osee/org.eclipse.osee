package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.skynet.core.artifact.WordArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.NativeRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PreviewRendererData;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * @author Jeff C. Phillips
 */
public class OpenWithContributionItem extends CompoundContributionItem {
   private ICommandService commandService;

   public OpenWithContributionItem() {
      this.commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
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
      ArrayList<IContributionItem> contributionItems = new ArrayList<IContributionItem>(40);

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         List<Artifact> artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

         try {
            contributionItems.addAll(getPreviewContributionItems(artifacts));
            contributionItems.addAll(getCommonSpecializedEditContributionItems(artifacts));
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }

      return contributionItems.toArray(new IContributionItem[0]);
   }

   @SuppressWarnings("deprecation")
   private ArrayList<IContributionItem> getCommonSpecializedEditContributionItems(List<Artifact> artifacts) throws OseeCoreException {
      ArrayList<IContributionItem> contributionItems = new ArrayList<IContributionItem>(25);
      List<IRenderer> commonRenders = RendererManager.getCommonSpecializedEditRenders(artifacts);

      for (IRenderer render : commonRenders) {
         CommandContributionItem contributionItem = null;

         if (render instanceof WordRenderer) {
            contributionItem =
                  new CommandContributionItem(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                        render.getCommandId(), render.getCommandId(), Collections.emptyMap(),
                        WordRenderer.getImageDescriptor(), null, null, null, null, null, SWT.NONE);
         } else {
            contributionItem =
                  new CommandContributionItem(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                        render.getCommandId(), render.getCommandId(), Collections.emptyMap(),
                        render instanceof NativeRenderer ? SkynetActivator.getInstance().getImageDescriptorForProgram(
                              ((NativeArtifact) artifacts.iterator().next()).getFileExtension()) : null, null, null,
                        null, null, null, SWT.NONE);
         }

         Command command = commandService.getCommand(contributionItem.getId());
         if (command != null && command.isEnabled()) {
            contributionItems.add(contributionItem);
         }
      }
      return contributionItems;
   }

   /**
    * @param artifacts
    * @return Returns the preview renderer contribution items.
    * @throws OseeCoreException
    */
   private ArrayList<IContributionItem> getPreviewContributionItems(List<Artifact> artifacts) throws OseeCoreException {
      ArrayList<IContributionItem> contributionItems = new ArrayList<IContributionItem>(10);
      boolean validForPreview = true;

      for (Artifact artifact : artifacts) {
         validForPreview &= !artifact.isOfType(WordArtifact.WHOLE_WORD) && !artifact.isOfType("Native");
      }

      if (validForPreview) {
         ContributionItem contributionItem;
         for (IRenderer previewRenderer : RendererManager.getPreviewPresentableRenders(artifacts.iterator().next())) {
            for (PreviewRendererData data : previewRenderer.getPreviewData()) {
               contributionItem =
                     new CommandContributionItem(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                           data.getCommandId(), data.getCommandId(), Collections.emptyMap(), data.getImageDescriptor(),
                           null, null, null, null, null, SWT.NONE);

               Command command = commandService.getCommand(contributionItem.getId());
               if (command != null && command.isEnabled()) {
                  contributionItems.add(contributionItem);
               }
            }
         }

         if (!contributionItems.isEmpty()) {
            contributionItems.add(new Separator());
         }
      }
      return contributionItems;
   }
}
