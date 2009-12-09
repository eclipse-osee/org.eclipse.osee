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
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.search.ui.NewSearchUI;
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
      layout.addShowViewShortcut(NavigateView.VIEW_ID);
      layout.addShowViewShortcut(ActionHyperView.VIEW_ID);

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
      final String editorArea = layout.getEditorArea();

      final IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.2f, editorArea);
      final IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, .75f, editorArea);
      final IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorArea);

      left.addView(NavigateView.VIEW_ID);

      right.addView(ArtifactExplorer.VIEW_ID);

      if (AtsUtil.isAtsAdmin()) {
         bottom.addView("org.eclipse.pde.runtime.LogView");
      }
      bottom.addView(BranchView.VIEW_ID);
      bottom.addView(GroupExplorer.VIEW_ID);
      bottom.addView(NewSearchUI.SEARCH_VIEW_ID);
      bottom.addPlaceholder(MergeView.VIEW_ID);

      final IFolderLayout bottomRight =
            layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.6f, ArtifactExplorer.VIEW_ID);
      final IFolderLayout bottomLeft =
            layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.6f, NavigateView.VIEW_ID);
      bottomLeft.addView(QuickSearchView.VIEW_ID);
      bottomRight.addView(ActionHyperView.VIEW_ID);
   }
}
