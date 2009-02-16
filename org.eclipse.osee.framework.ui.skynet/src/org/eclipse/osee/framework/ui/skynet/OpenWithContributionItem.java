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
package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.NativeArtifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.NativeRenderer;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.render.WordRenderer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

/**
 * Dynamically provides the open with CommandContributionItem for menu items based off of calling applicable renderers
 * getCommandId(presenationType).
 * 
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

         if (artifacts != null && !artifacts.isEmpty()) {
            try {
               contributionItems.addAll(getCommonContributionItems(artifacts, PresentationType.PREVIEW));
               //add separator between preview and edit commands
               if (!contributionItems.isEmpty()) {
                  contributionItems.add(new Separator());
               }
               contributionItems.addAll(getCommonContributionItems(artifacts, PresentationType.SPECIALIZED_EDIT));
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      }

      return contributionItems.toArray(new IContributionItem[0]);
   }

   private ArrayList<IContributionItem> getCommonContributionItems(List<Artifact> artifacts, PresentationType presentationType) throws OseeCoreException {
      ArrayList<IContributionItem> contributionItems = new ArrayList<IContributionItem>(25);
      List<IRenderer> commonRenders = RendererManager.getCommonRenderers(artifacts, presentationType);
      Artifact firstArtifact = artifacts.iterator().next();
      for (IRenderer render : commonRenders) {
         if (render instanceof WordRenderer) {
            contributionItems.addAll(loadCommands(render, presentationType, WordRenderer.getImageDescriptor()));
         } else {
            
            contributionItems.addAll(loadCommands(render, presentationType,
                  render instanceof NativeRenderer && firstArtifact instanceof NativeArtifact ? SkynetActivator.getInstance().getImageDescriptorForProgram(
                        ((NativeArtifact) firstArtifact).getFileExtension()) : null));
         }
      }
      return contributionItems;
   }

   private ArrayList<IContributionItem> loadCommands(IRenderer renderer, PresentationType presentationType, ImageDescriptor imageDescriptor) {
      ArrayList<IContributionItem> contributionItems = new ArrayList<IContributionItem>(25);
      CommandContributionItem contributionItem = null;

      for (String commandId : renderer.getCommandId(presentationType)) {
         contributionItem =
               new CommandContributionItem(new CommandContributionItemParameter(
                     PlatformUI.getWorkbench().getActiveWorkbenchWindow(), commandId, commandId,
                     Collections.emptyMap(), imageDescriptor, null, null, null, null, null, SWT.NONE, null, false));

         Command command = commandService.getCommand(contributionItem.getId());
         if (command != null && command.isEnabled()) {
            contributionItems.add(contributionItem);
         }
      }

      return contributionItems;
   }
}
