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
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * @author Donald G. Dunne
 */
public class ChangeXViewer extends XViewer {
   private static final boolean CHANGE_DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet/debug/Change"));

   private final XChangeViewer xChangeViewer;

   public ChangeXViewer(Composite parent, int style, XChangeViewer xRoleViewer) {
      super(parent, style, new ChangeXViewerFactory());
      this.addDoubleClickListener(new IDoubleClickListener() {
         public void doubleClick(org.eclipse.jface.viewers.DoubleClickEvent event) {
            try {
               handleDoubleClick();
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         };
      });
      this.xChangeViewer = xRoleViewer;
   }

   public void handleDoubleClick() throws Exception {
      if (getSelectedChanges().size() == 0) return;
      Change change = getSelectedChanges().iterator().next();
      if (CHANGE_DEBUG) {
         System.out.println(String.format(
               "Handling a Double Click in the Change Report Table for a %s Change on Artifact %s ",
               change.getItemKind(), change.getArtId()));
      }
      Artifact artifact = (Artifact) ((IAdaptable) change).getAdapter(Artifact.class);

      if (artifact != null) {
         ArtifactEditor.editArtifact(artifact);
      }
   }

   public ArrayList<Change> getSelectedChanges() {
      ArrayList<Change> arts = new ArrayList<Change>();
      TreeItem items[] = getTree().getSelection();
      if (items.length > 0) for (TreeItem item : items)
         arts.add((Change) item.getData());
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
            updateMenuActions();
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
      openMergeViewAction.setEnabled(getSelectedBranches().size() == 1 && getSelectedBranches().iterator().next().isBaselineBranch());

   }

   public void updateMenuActions() {
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
      if (items.length > 0) for (TreeItem item : items)
         arts.add((Branch) item.getData());
      return arts;
   }

   /**
    * @return the xChangeViewer
    */
   public XChangeViewer getXChangeViewer() {
      return xChangeViewer;
   }

}
