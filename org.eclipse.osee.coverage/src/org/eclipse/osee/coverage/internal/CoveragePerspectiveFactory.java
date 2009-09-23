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
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
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
      layout.addShowViewShortcut(CoverageNavigateView.VIEW_ID);
      layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");

      // Top left: Artifact Explorer
      IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
      topLeft.addView(CoverageNavigateView.VIEW_ID);
      topLeft.addPlaceholder(ArtifactExplorer.VIEW_ID + ":*");
      topLeft.addView(ArtifactExplorer.VIEW_ID);
      topLeft.addView(BranchView.VIEW_ID);

      IFolderLayout botLeft =
            layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float) 0.60, CoverageNavigateView.VIEW_ID);
      botLeft.addView("org.eclipse.pde.runtime.LogView");
      botLeft.addView(QuickSearchView.VIEW_ID);

   }
}