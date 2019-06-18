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
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.commands.Command;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.enums.CommandGroup;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ISelectedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;

/**
 * Dynamically provides the open/open with CommandContributionItem for menu items
 *
 * @author Jeff C. Phillips
 */
public class OpenContributionItem extends ContributionItem {

   private static final String DEFAULT_OPEN_CMD_ID = "org.eclipse.osee.framework.ui.skynet.open.command";

   private final Collection<IContributionItem> openWithItems = new ArrayList<>();
   private final ICommandService commandService = PlatformUI.getWorkbench().getService(ICommandService.class);

   private IContributionItem defaultOpenItem;
   private final ISelectedArtifacts selectionProvider;

   /**
    * Necessary for extension construction; do not remove
    */
   public OpenContributionItem() {
      this(null, null);
   }

   public OpenContributionItem(String id, ISelectedArtifacts selectionProvider) {
      super(id);
      this.selectionProvider = selectionProvider;
   }

   @Override
   public boolean isDynamic() {
      return true;
   }

   @Override
   public void dispose() {
      clearOpenWithItems();
      clearDefaultOpenItem();
      super.dispose();
   }

   private void clearDefaultOpenItem() {
      if (defaultOpenItem != null) {
         defaultOpenItem.dispose();
      }
      defaultOpenItem = null;
   }

   private void clearOpenWithItems() {
      for (IContributionItem item : openWithItems) {
         item.dispose();
      }
      openWithItems.clear();
   }

   @Override
   public void fill(final ToolBar parent, int index) {
      final ToolItem toolItem = new ToolItem(parent, SWT.DROP_DOWN);
      toolItem.setImage(ImageManager.getImage(FrameworkImage.OPEN));
      toolItem.setToolTipText("Open the Artifact");

      final OpenWithToolItemListener listener = new OpenWithToolItemListener(parent);
      toolItem.addListener(SWT.Selection, listener);
   }

   @Override
   public void fill(final Menu parent, int index) {
      fill(parent, index, true);
   }

   public void fill(final Menu parent, int index, boolean includeOpen) {
      if (index == -1) {
         index = parent.getItemCount();
      }
      if (includeOpen) {
         IContributionItem openItem = createDefaultOpenItem();
         openItem.fill(parent, index);
         MenuItem open = parent.getItem(0);
         if (open != null) {
            open.setEnabled(parent.isEnabled());
         }
      }

      MenuItem openWithMenuItem = null;
      if (includeOpen) {
         openWithMenuItem = new MenuItem(parent, SWT.CASCADE, index + 1);
      } else {
         openWithMenuItem = new MenuItem(parent, SWT.CASCADE, index);
      }
      openWithMenuItem.setText("Open With");

      Menu subMenu = new Menu(openWithMenuItem);
      fillOpenWithSubMenu(subMenu);
      openWithMenuItem.setMenu(subMenu);

      final OpenWithOnShowListener listener = new OpenWithOnShowListener(openWithMenuItem);
      parent.addMenuListener(listener);

      openWithMenuItem.addDisposeListener(new DisposeListener() {

         @Override
         public void widgetDisposed(DisposeEvent e) {
            parent.removeMenuListener(listener);
         }
      });
   }

   @Override
   public void setParent(IContributionManager parent) {
      if (getParent() instanceof IMenuManager) {
         IMenuManager menuMgr = (IMenuManager) getParent();
         menuMgr.removeMenuListener(menuListener);
      }
      if (parent instanceof IMenuManager) {
         IMenuManager menuMgr = (IMenuManager) parent;
         menuMgr.addMenuListener(menuListener);
      }
      super.setParent(parent);
   }

   private final IMenuListener menuListener = new IMenuListener() {
      @Override
      public void menuAboutToShow(IMenuManager manager) {
         manager.markDirty();
      }
   };

   private IContributionItem createDefaultOpenItem() {
      clearDefaultOpenItem();
      defaultOpenItem = createContributionItem(DEFAULT_OPEN_CMD_ID, null);
      return defaultOpenItem;
   }

   private Collection<IContributionItem> createOpenWithItems() {
      clearOpenWithItems();
      Collection<Artifact> artifacts = getSelectedArtifacts();
      boolean readOnly = false;
      if (!artifacts.isEmpty()) {
         for (Artifact art : artifacts) {
            if (art.isReadOnly()) {
               readOnly = true;
               break;
            }
         }
         Artifact testArtifact = artifacts.iterator().next();
         try {
            CommandGroup[] groups = CommandGroup.values();
            if (readOnly) {
               groups = CommandGroup.getReadOnly();
            }
            CommandGroup lastGroup = groups[groups.length - 1];
            for (CommandGroup commandGroup : groups) {

               List<IRenderer> commonRenders =
                  RendererManager.getCommonRenderers(artifacts, commandGroup.getPresentationType());
               openWithItems.addAll(getCommonContributionItems(commandGroup, testArtifact, commonRenders));

               if (lastGroup != commandGroup && !openWithItems.isEmpty()) {
                  //add separator between presentation type commands
                  openWithItems.add(new Separator());
               }
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return openWithItems;
   }

   private Collection<Artifact> getSelectedArtifacts() {
      if (selectionProvider == null) {
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
      } else {
         return selectionProvider.getSelectedArtifacts();
      }
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

   private Collection<IContributionItem> getCommonContributionItems(CommandGroup commandGroup, Artifact testArtifact, Collection<IRenderer> commonRenders) {
      ArrayList<IContributionItem> items = new ArrayList<>();
      ArrayList<MenuCmdDef> commands = new ArrayList<>();
      for (IRenderer renderer : commonRenders) {
         renderer.addMenuCommandDefinitions(commands, testArtifact);
      }

      for (MenuCmdDef commandDefinition : commands) {
         if (commandDefinition.getcommandGroup().equals(commandGroup)) {
            items.add(createContributionItem(commandDefinition));
         }
      }
      return items;
   }

   private IContributionItem createContributionItem(MenuCmdDef def) {
      Map<String, String> commandParamMap = def.getCommandParamMap();
      CommandContributionItemParameter param = new CommandContributionItemParameter(
         PlatformUI.getWorkbench().getActiveWorkbenchWindow(), def.getCommandId(), def.getCommandId(), commandParamMap,
         def.getIcon(), null, null, def.getLabel(), null, null, SWT.NONE, null, false);

      return new CommandContributionItem(param);
   }

   private IContributionItem createContributionItem(String commandId, ImageDescriptor imageDescriptor) {
      CommandContributionItemParameter param =
         new CommandContributionItemParameter(PlatformUI.getWorkbench().getActiveWorkbenchWindow(), commandId,
            commandId, null, imageDescriptor, null, null, null, null, null, SWT.NONE, null, false);

      CommandContributionItem contributionItem = new CommandContributionItem(param);
      return contributionItem;
   }

   private void fillOpenWithSubMenu(Menu menu) {
      for (IContributionItem item : createOpenWithItems()) {
         item.fill(menu, -1);
      }
   }

   private final class OpenWithOnShowListener implements MenuListener {
      private final MenuItem parentItem;

      public OpenWithOnShowListener(MenuItem parentItem) {
         this.parentItem = parentItem;
      }

      @Override
      public void menuShown(MenuEvent e) {
         Menu oldMenu = parentItem.getMenu();
         if (oldMenu != null) {
            oldMenu.dispose();
         }

         Menu subMenu = new Menu(parentItem);
         fillOpenWithSubMenu(subMenu);
         parentItem.setMenu(subMenu);
      }

      @Override
      public void menuHidden(MenuEvent e) {
         // Do Nothing
      }
   }

   private final class OpenWithToolItemListener implements Listener {

      private final ToolBar parent;

      public OpenWithToolItemListener(ToolBar parent) {
         this.parent = parent;
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

               Menu subMenu = new Menu(parent.getShell(), SWT.POP_UP);
               fillOpenWithSubMenu(subMenu);
               subMenu.setLocation(pt.x, pt.y);
               subMenu.setVisible(true);
            }

            if (event.detail == 0) {
               try {
                  IHandlerService handlerService = PlatformUI.getWorkbench().getService(IHandlerService.class);

                  Command command = commandService.getCommand(DEFAULT_OPEN_CMD_ID);
                  if (command.isEnabled()) {
                     handlerService.executeCommand(command.getId(), null);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }
      }
   }

}
