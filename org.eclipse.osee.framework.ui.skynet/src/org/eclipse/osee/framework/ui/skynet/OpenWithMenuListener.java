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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.skynet.commandHandlers.Handlers;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;
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
   private Menu parentMenu;
   private Viewer viewer;
   private IRebuildMenuListener rebuildMenuListener;
   private static ICommandService commandService =
         (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);

   public OpenWithMenuListener(Menu parentMenu, final Viewer viewer, IRebuildMenuListener rebuildMenuListener) {
      super();
      this.parentMenu = parentMenu;
      this.viewer = viewer;
      this.rebuildMenuListener = rebuildMenuListener;
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.events.MenuListener#menuHidden(org.eclipse.swt.events.MenuEvent)
    */
   @Override
   public void menuHidden(MenuEvent e) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.swt.events.MenuListener#menuShown(org.eclipse.swt.events.MenuEvent)
    */
   @Override
   public void menuShown(MenuEvent e) {
      try {
         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

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

         if (loadMenuItems(parentMenu, PresentationType.PREVIEW, artifacts)) {
            new MenuItem(parentMenu, SWT.SEPARATOR);
         }
         loadMenuItems(parentMenu, PresentationType.SPECIALIZED_EDIT, artifacts);

      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public static boolean loadMenuItems(Menu parentMenu, PresentationType presentationType, List<Artifact> artifacts) throws OseeCoreException, NotDefinedException {
      List<IRenderer> commonRenders = RendererManager.getCommonRenderers(artifacts, presentationType);
      Artifact artifact = artifacts.iterator().next();
      boolean hasMenus = false;
      for (IRenderer renderer : commonRenders) {
         for (String commandId : renderer.getCommandId(presentationType)) {
            Command command = commandService.getCommand(commandId);
            if (command != null && command.isEnabled()) {
               hasMenus = true;
               MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
               menuItem.setText(command.getName());
               menuItem.setImage(renderer.getImage(artifact));
               menuItem.addSelectionListener(new OpenWithSelectionListener(command));
            }
         }
      }
      return hasMenus;
   }
}
