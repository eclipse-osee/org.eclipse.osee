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
package org.eclipse.osee.ats;

import org.eclipse.osee.ats.hyper.ActionHyperView;
import org.eclipse.osee.ats.hyper.ArtifactHyperView;
import org.eclipse.osee.ats.navigate.NavigateView;
import org.eclipse.osee.ats.world.WorldView;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.branch.BranchView;
import org.eclipse.osee.framework.ui.skynet.group.GroupExplorer;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ATSPerspective implements IPerspectiveFactory {

   public ATSPerspective() {
      super();
   }

   public void createInitialLayout(IPageLayout layout) {
      defineActions(layout);
      defineLayout(layout);
   }

   public void defineActions(IPageLayout layout) {

      // Add "show views".
      layout.addShowViewShortcut(WorldView.VIEW_ID);
      layout.addShowViewShortcut(NavigateView.VIEW_ID);
      layout.addShowViewShortcut(ActionHyperView.VIEW_ID);
      //      layout.addShowViewShortcut(ActionSkyWalker.VIEW_ID);

      layout.addShowViewShortcut(ArtifactHyperView.VIEW_ID);
      layout.addShowViewShortcut(ArtifactExplorer.VIEW_ID);
      layout.addShowViewShortcut(XResultView.VIEW_ID);
      layout.addShowViewShortcut(BranchView.VIEW_ID);
      layout.addShowViewShortcut(GroupExplorer.VIEW_ID);
      layout.addShowViewShortcut(QuickSearchView.VIEW_ID);
      layout.addShowViewShortcut("org.eclipse.osee.framework.ui.admin.AdminView");
      layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");

   }

   public void defineLayout(IPageLayout layout) {
      // Editors are placed for free.
      String editorArea = layout.getEditorArea();

      // Place navigator and outline to left of
      // editor area.
      IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.20, editorArea);
      left.addView(NavigateView.VIEW_ID);
      left.addView(GroupExplorer.VIEW_ID);
      left.addView(ArtifactExplorer.VIEW_ID);
      left.addView(BranchView.VIEW_ID);

      IFolderLayout botLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float) 0.60, NavigateView.VIEW_ID);
      //      botLeft.addView(ActionSkyWalker.VIEW_ID);
      botLeft.addView(ActionHyperView.VIEW_ID);

      IFolderLayout lower = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.65, editorArea);
      lower.addView(WorldView.VIEW_ID);
      lower.addView(QuickSearchView.VIEW_ID);
      if (AtsPlugin.isAtsAdmin()) {
         lower.addView(ArtifactHyperView.VIEW_ID);
      }

      if (AtsPlugin.isAtsAdmin()) {
         IFolderLayout bottomRight =
               layout.createFolder("bottomRight", IPageLayout.RIGHT, (float) 0.75, WorldView.VIEW_ID);
         bottomRight.addView("org.eclipse.pde.runtime.LogView");
      }
   }
}
