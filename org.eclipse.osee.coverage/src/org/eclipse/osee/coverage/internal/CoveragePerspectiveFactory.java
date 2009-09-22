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
package org.eclipse.osee.coverage.internal;

import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Perspective factory for the Coverage perspective.
 * 
 * @author Donald G. Dunne
 */
public class CoveragePerspectiveFactory implements IPerspectiveFactory {

   public void createInitialLayout(IPageLayout layout) {
      // Get the editor area.
      String editorArea = layout.getEditorArea();

      // Add views to Window -> Show View
      layout.addShowViewShortcut(ArtifactExplorer.VIEW_ID);
      layout.addShowViewShortcut(QuickSearchView.VIEW_ID);
      layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");

      // Top left: Artifact Explorer
      IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
      topLeft.addPlaceholder(ArtifactExplorer.VIEW_ID + ":*");
      topLeft.addView(ArtifactExplorer.VIEW_ID);

      IFolderLayout lower = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.65f, editorArea);
      lower.addView(BranchView.VIEW_ID);
      lower.addPlaceholder(HistoryView.VIEW_ID);
      lower.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
      lower.addPlaceholder("org.eclipse.pde.runtime.LogView");

      IFolderLayout underLower = layout.createFolder("underBottom", IPageLayout.BOTTOM, .75f, "bottom");
      underLower.addView(QuickSearchView.VIEW_ID);

   }
}