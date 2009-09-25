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
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.group.GroupExplorer;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ATSPerspective implements IPerspectiveFactory {

   @Override
   public void createInitialLayout(final IPageLayout layout) {
      defineActions(layout);
      defineLayout(layout);
   }

   public void defineActions(final IPageLayout layout) {

      // Add "show views".
      layout.addShowViewShortcut(NavigateView.VIEW_ID);
      layout.addShowViewShortcut(ActionHyperView.VIEW_ID);
      //      layout.addShowViewShortcut(ActionSkyWalker.VIEW_ID);

      layout.addShowViewShortcut(ArtifactHyperView.VIEW_ID);
      layout.addShowViewShortcut(ArtifactExplorer.VIEW_ID);
      layout.addShowViewShortcut(BranchView.VIEW_ID);
      layout.addShowViewShortcut(GroupExplorer.VIEW_ID);
      layout.addShowViewShortcut(QuickSearchView.VIEW_ID);
      layout.addShowViewShortcut("osee.admin.AdminView");
      layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");
      layout.addShowViewShortcut("org.eclipse.ui.views.PropertySheet");

   }

   public void defineLayout(final IPageLayout layout) {
      // Editors are placed for free.
      final String editorArea = layout.getEditorArea();

      // Place navigator and outline to left of
      // editor area.
      final IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.19, editorArea);
      left.addView(NavigateView.VIEW_ID);
      left.addView(GroupExplorer.VIEW_ID);
      left.addView(ArtifactExplorer.VIEW_ID);
      left.addView(BranchView.VIEW_ID);

      if (AtsUtil.isAtsAdmin()) {
         final IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, (float) 0.80, editorArea);
         right.addView("org.eclipse.pde.runtime.LogView");
      }

      IFolderLayout botLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float) 0.60, NavigateView.VIEW_ID);
      //      botLeft.addView(ActionSkyWalker.VIEW_ID);
      botLeft.addView(ActionHyperView.VIEW_ID);
      botLeft.addView(QuickSearchView.VIEW_ID);

   }
}
