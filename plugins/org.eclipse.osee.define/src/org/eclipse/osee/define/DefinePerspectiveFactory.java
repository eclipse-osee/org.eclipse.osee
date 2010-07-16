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
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.skynet.skywalker.arttype.ArtifactTypeWalker;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchView;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * Perspective factory for the Define perspective. This sets up the initial layout and place holders for views.
 * 
 * @author Robert A. Fisher
 */
public class DefinePerspectiveFactory implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		layout.addShowViewShortcut(ArtifactExplorer.VIEW_ID);
		layout.addShowViewShortcut(BranchView.VIEW_ID);
		layout.addShowViewShortcut(QuickSearchView.VIEW_ID);
		layout.addShowViewShortcut(DefineNavigateView.VIEW_ID);
		layout.addShowViewShortcut(ArtifactTypeWalker.VIEW_ID);
		layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");

		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);
		topLeft.addPlaceholder(ArtifactExplorer.VIEW_ID + ":*");
		topLeft.addView(ArtifactExplorer.VIEW_ID);
		topLeft.addView(DefineNavigateView.VIEW_ID);

		IFolderLayout lower = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.65f, editorArea);
		lower.addView(BranchView.VIEW_ID);
		lower.addPlaceholder(HistoryView.VIEW_ID);
		lower.addPlaceholder(NewSearchUI.SEARCH_VIEW_ID);
		lower.addPlaceholder("org.eclipse.pde.runtime.LogView");
		lower.addView(QuickSearchView.VIEW_ID);
	}
}