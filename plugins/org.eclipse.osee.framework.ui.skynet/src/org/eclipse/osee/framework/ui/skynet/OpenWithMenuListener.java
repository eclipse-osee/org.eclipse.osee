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
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer.CommandGroup;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;

/**
 * @author Ryan D. Brooks
 * @author Jeff C. Phillips
 */

public class OpenWithMenuListener implements MenuListener {
   private final Menu parentMenu;
   private final ISelectionProvider selectionProvider;
   private final IRebuildMenuListener rebuildMenuListener;
   private static ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(
      ICommandService.class);

   public OpenWithMenuListener(Menu parentMenu, ISelectionProvider selectionProvider, IRebuildMenuListener rebuildMenuListener) {
      super();
      this.parentMenu = parentMenu;
      this.selectionProvider = selectionProvider;
      this.rebuildMenuListener = rebuildMenuListener;
   }

   @Override
   public void menuHidden(MenuEvent e) {
      // do nothing
   }

   @Override
   public void menuShown(MenuEvent e) {
      try {
         IStructuredSelection selection = (IStructuredSelection) selectionProvider.getSelection();

         if (selection.isEmpty()) {
            return;
         }

         rebuildMenuListener.rebuildMenu();

         Iterator<?> iterator = selection.iterator();
         ArrayList<Artifact> artifacts = new ArrayList<Artifact>(selection.size());

         //load artifacts in the list
         Artifact artifact = null;
         while (iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof IAdaptable) {
               artifact = (Artifact) ((IAdaptable) object).getAdapter(Artifact.class);
            } else if (object instanceof Match) {
               artifact = (Artifact) ((Match) object).getElement();
            } else if (object instanceof RelationLink) {
               RelationLink link = (RelationLink) object;
               try {
                  List<Artifact> edittedArtifacts =
                     Handlers.getArtifactsFromStructuredSelection((IStructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection());
                  artifact = link.getArtifactOnOtherSide(edittedArtifacts.iterator().next());
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
            artifacts.add(artifact);
         }

         if (loadMenuItems(parentMenu, IRenderer.CommandGroup.PREVIEW, artifacts)) {
            new MenuItem(parentMenu, SWT.SEPARATOR);
         }
         loadMenuItems(parentMenu, IRenderer.CommandGroup.EDIT, artifacts);

      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public static boolean loadMenuItems(Menu parentMenu, CommandGroup commandGroup, List<Artifact> artifacts) throws OseeCoreException, NotDefinedException {
      PresentationType presentationType =
         CommandGroup.PREVIEW == commandGroup ? PresentationType.PREVIEW : PresentationType.SPECIALIZED_EDIT;

      List<IRenderer> commonRenders = RendererManager.getCommonRenderers(artifacts, presentationType);
      Artifact artifact = artifacts.iterator().next();
      boolean hasMenus = false;
      for (IRenderer renderer : commonRenders) {
         for (String commandId : renderer.getCommandIds(commandGroup)) {
            Command command = commandService.getCommand(commandId);
            if (command != null && command.isEnabled()) {
               hasMenus = true;
               MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
               menuItem.setText(command.getName());
               ImageDescriptor descriptor = renderer.getCommandImageDescriptor(command, artifact);
               menuItem.setImage(descriptor.createImage());
               menuItem.addSelectionListener(new OpenWithSelectionListener(command));
            }
         }
      }
      return hasMenus;
   }
}
