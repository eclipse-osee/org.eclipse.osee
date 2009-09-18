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
package org.eclipse.osee.framework.ui.skynet.widgets.xchange;

import java.util.ArrayList;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.StaticIdManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.preferences.EditorsPreferencePage;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 * @author Jeff C. Phillips
 */
public class ChangeXViewer extends XViewer {
   private static final boolean CHANGE_DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet/debug/Change"));

   private final XChangeWidget xChangeViewer;

   public ChangeXViewer(Composite parent, int style, XChangeWidget xRoleViewer) {
      super(parent, style, new ChangeXViewerFactory());
      this.xChangeViewer = xRoleViewer;
   }

   @Override
   public void handleDoubleClick() {
      try {
         if (getSelectedChanges().size() == 0) {
            return;
         }

         Change change = getSelectedChanges().iterator().next();
         if (CHANGE_DEBUG) {
            System.out.println(String.format(
                  "Handling a Double Click in the Change Report Table for a %s Change on Artifact %s ",
                  change.getItemKind(), change.getArtId()));
         }
         Artifact artifact = (Artifact) ((IAdaptable) change).getAdapter(Artifact.class);

         if (artifact != null) {
            ArrayList<Artifact> artifacts = new ArrayList<Artifact>(1);
            artifacts.add(artifact);

            if (StaticIdManager.hasValue(UserManager.getUser(),
                  EditorsPreferencePage.PreviewOnDoubleClickForWordArtifacts)) {
               RendererManager.previewInJob(artifacts);
            } else {
               RendererManager.openInJob(artifacts, PresentationType.GENERALIZED_EDIT);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public ArrayList<Change> getSelectedChanges() {
      ArrayList<Change> arts = new ArrayList<Change>();
      TreeItem items[] = getTree().getSelection();

      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((Change) item.getData());
         }
      }
      return arts;
   }

   @Override
   protected void createSupportWidgets(Composite parent) {
      super.createSupportWidgets(parent);
      createMenuActions();
   }

   Action openMergeViewAction;

   public void createMenuActions() {
      MenuManager mm = getMenuManager();
      mm.createContextMenu(getControl());
      mm.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActionsForTable();
         }
      });

      openMergeViewAction = new Action("Open Merge View", Action.AS_PUSH_BUTTON) {
         @Override
         public void run() {
            AWorkbench.popup("ERROR", "Not implemented yet");
         }
      };
   }

   public void updateEditMenuActions() {
      MenuManager mm = getMenuManager();

      // EDIT MENU BLOCK
      mm.insertBefore(MENU_GROUP_PRE, openMergeViewAction);
      openMergeViewAction.setEnabled(getSelectedBranches().size() == 1 && getSelectedBranches().iterator().next().getBranchType().isBaselineBranch());

   }

   @Override
   public void updateMenuActionsForTable() {
      MenuManager mm = getMenuManager();

      updateEditMenuActions();

      mm.insertBefore(MENU_GROUP_PRE, new Separator());
   }

   /**
    * Release resources
    */
   @Override
   public void dispose() {
      getLabelProvider().dispose();
   }

   public ArrayList<Branch> getSelectedBranches() {
      ArrayList<Branch> arts = new ArrayList<Branch>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) {
         for (TreeItem item : items) {
            arts.add((Branch) item.getData());
         }
      }
      return arts;
   }

   /**
    * @return the xChangeViewer
    */
   public XChangeWidget getXChangeViewer() {
      return xChangeViewer;
   }

}
