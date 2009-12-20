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

import org.eclipse.osee.coverage.navigate.CoverageNavigateView;
import org.eclipse.osee.coverage.util.CoverageUtil;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
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
      final String editorArea = layout.getEditorArea();

      final IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.2f, editorArea);
      final IFolderLayout bottom = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.75f, editorArea);

      // Add views to Window -> Show View
      layout.addShowViewShortcut(ArtifactExplorer.VIEW_ID);
      layout.addShowViewShortcut(QuickSearchView.VIEW_ID);
      layout.addShowViewShortcut(CoverageNavigateView.VIEW_ID);
      layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");

      left.addView(CoverageNavigateView.VIEW_ID);
      left.addView(ArtifactExplorer.VIEW_ID + ":*");

      if (CoverageUtil.isAdmin()) {
         bottom.addView("org.eclipse.pde.runtime.LogView");
      }
      bottom.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);

      final IFolderLayout bottomLeft =
            layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.6f, CoverageNavigateView.VIEW_ID);
      bottomLeft.addView(QuickSearchView.VIEW_ID);
   }
}