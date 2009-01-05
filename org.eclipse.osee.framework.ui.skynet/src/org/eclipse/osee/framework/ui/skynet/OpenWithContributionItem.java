package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.NativeRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;
import org.eclipse.osee.framework.ui.skynet.render.WordTemplateRenderer;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
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
      List<IRenderer> commonRenders = null;
      ArrayList<IContributionItem> contributionItems = null;

      if (selectionProvider != null && selectionProvider.getSelection() instanceof IStructuredSelection) {
         IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
         List<Artifact> artifacts = Handlers.getArtifactsFromStructuredSelection(structuredSelection);

         try {
            commonRenders = RendererManager.getCommonSpecializedEditRenders(artifacts);
         } catch (OseeCoreException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }

         contributionItems = new ArrayList<IContributionItem>(commonRenders.size());
         for (IRenderer render : commonRenders) {
            CommandContributionItem contributionItem = null;

            if (render instanceof WordTemplateRenderer) {
               contributionItem =
                     new CommandContributionItem(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                           render.getCommandId(), render.getCommandId(), Collections.emptyMap(),
                           WordRenderer.getImageDescriptor(), null, null, null, null, null, SWT.NONE);
            } else {
               try {
                  contributionItem =
                        new CommandContributionItem(
                              PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
                              render.getCommandId(),
                              render.getCommandId(),
                              Collections.emptyMap(),
                              render instanceof NativeRenderer ? SkynetActivator.getInstance().getImageDescriptorForProgram(
                                    ((NativeArtifact) artifacts.iterator().next()).getFileExtension()) : null, null,
                              null, null, null, null, SWT.NONE);
               } catch (OseeCoreException ex) {
                  ex.printStackTrace();
               }
            }

            Command command = commandService.getCommand(contributionItem.getId());
            if (command != null && command.isEnabled()) {
               contributionItems.add(contributionItem);
            }
         }
      }

      return contributionItems.toArray(new IContributionItem[0]);
   }
}
