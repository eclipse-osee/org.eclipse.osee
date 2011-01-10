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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer.CommandGroup;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
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

   @Override
   protected IContributionItem[] getContributionItems() {
      ArrayList<IContributionItem> contributionItems = new ArrayList<IContributionItem>(40);
      List<Artifact> artifacts = getSelectedArtifacts();
      if (!artifacts.isEmpty()) {

         Artifact testArtifact = artifacts.iterator().next();

         try {
            CommandGroup[] groups = IRenderer.CommandGroup.values();
            CommandGroup lastGroup = groups[groups.length - 1];
            for (CommandGroup commandGroup : groups) {

               List<IRenderer> commonRenders =
                  RendererManager.getCommonRenderers(artifacts, commandGroup.getPresentationType());
               contributionItems.addAll(getCommonContributionItems(commandGroup, testArtifact, commonRenders));

               if (lastGroup != commandGroup && !contributionItems.isEmpty()) {
                  //add separator between presentation type commands
                  contributionItems.add(new Separator());
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
      return contributionItems.toArray(new IContributionItem[contributionItems.size()]);
   }

   private Collection<IContributionItem> getCommonContributionItems(CommandGroup commandGroup, Artifact testArtifact, Collection<IRenderer> commonRenders) throws OseeCoreException {
      Map<String, IContributionItem> contributedItems = new LinkedHashMap<String, IContributionItem>(25);
      for (IRenderer renderer : commonRenders) {
         for (String commandId : renderer.getCommandIds(commandGroup)) {
            Command command = commandService.getCommand(commandId);
            if (command != null && command.isEnabled()) {
               ImageDescriptor imageDescriptor = renderer.getCommandImageDescriptor(command, testArtifact);
               IContributionItem item = createContributionItem(commandId, imageDescriptor);
               contributedItems.put(commandId, item);
            }
         }
      }
      return contributedItems.values();
   }

   private IContributionItem createContributionItem(String commandId, ImageDescriptor imageDescriptor) {
      IContributionItem contributionItem =
         new CommandContributionItem(new CommandContributionItemParameter(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow(), commandId, commandId, Collections.emptyMap(),
            imageDescriptor, null, null, null, null, null, SWT.NONE, null, false));
      return contributionItem;
   }

   @Override
   public void fill(final ToolBar parent, int index) {
      final ToolItem toolItem = new ToolItem(parent, SWT.DROP_DOWN);
      toolItem.setImage(ImageManager.getImage(FrameworkImage.OPEN));
      toolItem.setToolTipText("Open the Artifact");

      OpenWithToolItemListener listener = new OpenWithToolItemListener(parent.getShell());
      toolItem.addListener(SWT.Selection, listener);
      toolItem.setEnabled(listener.isPreviewMenuEnabled());
   }

   @Override
   public void fill(Menu parent, int index) {
      /**
       * hard coded to show as the 2nd menu option; necessary for Xviewer menu options to work with Change View
       * command/handlers and this dynamic menu option
       */
      final MenuItem item = new MenuItem(parent, SWT.CASCADE, 1);
      item.setText("Open With");

      Menu subMenu = new Menu(parent);
      item.setMenu(subMenu);

      createDropDownMenu(parent.getShell(), subMenu);
      item.setEnabled(isMenuEnabled(subMenu));
   }

   private ISelectionProvider getSelectionProvider() {
      ISelectionProvider toReturn = null;
      IWorkbenchPage page = AWorkbench.getActivePage();
      if (page != null) {
         IWorkbenchPart part = page.getActivePart();
         if (part != null) {
            IWorkbenchPartSite site = part.getSite();
            if (site != null) {
               toReturn = site.getSelectionProvider();
            }
         }
      }
      return toReturn;
   }

   private List<Artifact> getSelectedArtifacts() {
      List<Artifact> toReturn = Collections.emptyList();
      ISelectionProvider selectionProvider = getSelectionProvider();
      if (selectionProvider != null) {
         ISelection selection = selectionProvider.getSelection();
         if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection) selectionProvider.getSelection();
            toReturn = Handlers.getArtifactsFromStructuredSelection(structuredSelection);
         }
      }
      return toReturn;
   }

   private void createDropDownMenu(Shell shell, Menu previewMenu) {
      for (IContributionItem item : getContributionItems()) {
         item.fill(previewMenu, -1);
      }
   }

   public boolean isMenuEnabled(Menu menu) {
      boolean itemzEnabled = false;
      for (MenuItem menuItems : menu.getItems()) {
         if (menuItems.isEnabled()) {
            itemzEnabled = true;
         }
      }
      return itemzEnabled;
   }

   private final class OpenWithToolItemListener implements Listener {
      private final Menu previewMenu;
      private final boolean isPreviewEnabled;

      public OpenWithToolItemListener(Shell shell) {
         previewMenu = new Menu(shell, SWT.POP_UP);
         createDropDownMenu(shell, previewMenu);
         isPreviewEnabled = isPreviewMenuEnabled();
      }

      public boolean isPreviewMenuEnabled() {
         return isMenuEnabled(previewMenu);
      }

      @Override
      public void handleEvent(Event event) {
         Widget widget = event.widget;
         if (widget instanceof ToolItem) {
            ToolItem toolItem = (ToolItem) widget;
            ToolBar toolBar = toolItem.getParent();

            if (event.detail == SWT.ARROW) {
               Rectangle rect = toolItem.getBounds();
               Point pt = new Point(rect.x, rect.y + rect.height);
               pt = toolBar.toDisplay(pt);
               previewMenu.setLocation(pt.x, pt.y);
               previewMenu.setVisible(true);
            }

            if (event.detail == 0) {
               List<Artifact> artifacts = getSelectedArtifacts();
               if (artifacts != null && !artifacts.isEmpty()) {
                  Artifact artifact = artifacts.iterator().next();
                  if (isPreviewEnabled && artifact != null) {
                     RendererManager.openInJob(artifacts, PresentationType.DEFAULT_OPEN);
                  }
               }
            }
         }
      }
   }
}
