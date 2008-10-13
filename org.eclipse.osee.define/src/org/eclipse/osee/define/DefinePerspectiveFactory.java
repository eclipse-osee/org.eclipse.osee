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
package org.eclipse.osee.define;

import org.eclipse.osee.define.navigate.DefineNavigateView;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.branch.BranchView;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.queryLog.QueryLogView;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Perspective factory for the Define perspective. This sets up the initial layout and placeholders for views.
 * 
 * @author Robert A. Fisher
 */
public class DefinePerspectiveFactory implements IPerspectiveFactory {

   public void createInitialLayout(IPageLayout layout) {
      // Get the editor area.
      String editorArea = layout.getEditorArea();

      // Add views to Window -> Show View
      layout.addShowViewShortcut(ArtifactExplorer.VIEW_ID);
      layout.addShowViewShortcut(BranchView.VIEW_ID);
      layout.addShowViewShortcut(QueryLogView.VIEW_ID);
      layout.addShowViewShortcut(QuickSearchView.VIEW_ID);
      layout.addShowViewShortcut(DefineNavigateView.VIEW_ID);
      layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");

      // Top left: Artifact Explorer
      IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
      topLeft.addView(ArtifactExplorer.VIEW_ID);
      topLeft.addView(DefineNavigateView.VIEW_ID);

      IFolderLayout lower = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.65f, editorArea);
      lower.addView(BranchView.VIEW_ID);
      lower.addPlaceholder(RevisionHistoryView.VIEW_ID);
      lower.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
      lower.addPlaceholder("org.eclipse.pde.runtime.LogView");

      IFolderLayout underLower = layout.createFolder("underBottom", IPageLayout.BOTTOM, .75f, "bottom");
      underLower.addView(QuickSearchView.VIEW_ID);

      // The following is some sample code that can be used for future reference for other areas and
      // placeholders ...
      // topLeft.addPlaceholder(IPageLayout.ID_BOOKMARKS);
      //
      // // Bottom left: Outline view and Property Sheet view
      // IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.50f,
      // "topLeft");
      // bottomLeft.addView(IPageLayout.ID_OUTLINE);
      // bottomLeft.addView(IPageLayout.ID_PROP_SHEET);
      //
      // // Bottom right: Task List view
      // layout.addView(IPageLayout.ID_TASK_LIST, IPageLayout.BOTTOM, 0.66f, editorArea);
   }
}