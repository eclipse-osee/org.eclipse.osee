/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.internal;

import org.eclipse.osee.ats.ide.navigate.NavigateView;
import org.eclipse.osee.ats.ide.walker.ActionWalkerView;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.skynet.skywalker.arttype.ArtifactTypeWalker;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchSearchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xmerge.MergeView;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Single main OSEE Perspective
 *
 * @author Donald G. Dunne
 */
public class OseePerspective implements IPerspectiveFactory {

   public static String ID = "org.eclipse.osee.OseePerspective";

   @Override
   public void createInitialLayout(final IPageLayout layout) {
      defineActions(layout);
      defineLayout(layout);
   }

   public void defineActions(final IPageLayout layout) {
      layout.addShowViewShortcut(NavigateView.VIEW_ID);
      layout.addShowViewShortcut(ActionWalkerView.VIEW_ID);

      layout.addShowViewShortcut(ArtifactExplorer.VIEW_ID);
      layout.addShowViewShortcut(BranchView.VIEW_ID);
      layout.addShowViewShortcut(QuickSearchView.VIEW_ID);
      layout.addShowViewShortcut(ArtifactTypeWalker.VIEW_ID);
      layout.addShowViewShortcut("osee.admin.AdminView");
      layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");
      layout.addShowViewShortcut("org.eclipse.ui.views.PropertySheet");
      layout.addShowViewShortcut("org.eclipse.ui.views.ContentOutline");
   }

   public void defineLayout(final IPageLayout layout) {
      final String editorArea = layout.getEditorArea();

      final IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.2f, editorArea);
      final IFolderLayout leftBottom = layout.createFolder("leftBottom", IPageLayout.BOTTOM, 0.7f, "left");
      final IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT, .75f, editorArea);

      layout.createFolder("bottom", IPageLayout.BOTTOM, 0.7f, editorArea);
      final IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.LEFT, 0.7f, "bottom");
      final IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.RIGHT, 0.3f, "bottom");

      left.addView(NavigateView.VIEW_ID);
      leftBottom.addView(QuickSearchView.VIEW_ID);
      right.addView(ArtifactExplorer.VIEW_ID);

      if (AtsApiService.get().getUserService().isAtsAdmin()) {
         bottomLeft.addView(BranchSearchView.VIEW_ID);
      } else {
         bottomLeft.addView(BranchView.VIEW_ID);
      }
      bottomLeft.addView(NewSearchUI.SEARCH_VIEW_ID);
      bottomLeft.addPlaceholder(MergeView.VIEW_ID);
      if (AtsApiService.get().getUserService().isAtsAdmin()) {
         bottomRight.addView("org.eclipse.pde.runtime.LogView");
      }
   }
}
