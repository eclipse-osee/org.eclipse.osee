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
import org.eclipse.osee.framework.ui.skynet.changeReport.ChangeReportView;
import org.eclipse.osee.framework.ui.skynet.group.GroupExplorer;
import org.eclipse.osee.framework.ui.skynet.queryLog.QueryLogView;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ATSTestPerspective implements IPerspectiveFactory {

   public ATSTestPerspective() {
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
      layout.addShowViewShortcut("osee.admin.AdminView");
      layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");

      layout.addShowViewShortcut(ArtifactExplorer.VIEW_ID);
      layout.addShowViewShortcut(BranchView.VIEW_ID);
      layout.addShowViewShortcut(ChangeReportView.VIEW_ID);
      layout.addShowViewShortcut(QueryLogView.VIEW_ID);
      layout.addShowViewShortcut(QuickSearchView.VIEW_ID);
      layout.addShowViewShortcut("org.eclipse.osee.define.DefineNavigateView");
      layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");

   }

   public void defineLayout(IPageLayout layout) {
      // Editors are placed for free.
      String editorArea = layout.getEditorArea();

      IFolderLayout left1 = layout.createFolder("left1", IPageLayout.LEFT, (float) 0.20, editorArea);
      left1.addView(NavigateView.VIEW_ID);
      IFolderLayout left1Bot1 =
            layout.createFolder("left1Bot1", IPageLayout.BOTTOM, (float) 0.50, NavigateView.VIEW_ID);
      left1Bot1.addView(GroupExplorer.VIEW_ID);
      IFolderLayout left1Bot2 =
            layout.createFolder("left1Bot2", IPageLayout.BOTTOM, (float) 0.50, GroupExplorer.VIEW_ID);
      left1Bot2.addView(ArtifactExplorer.VIEW_ID);

      IFolderLayout left2 = layout.createFolder("left2", IPageLayout.LEFT, (float) 0.25, editorArea);
      left2.addView(ActionHyperView.VIEW_ID);
      IFolderLayout left2Bot1 =
            layout.createFolder("left2Bot1", IPageLayout.BOTTOM, (float) 0.50, ActionHyperView.VIEW_ID);
      left2Bot1.addView(ArtifactHyperView.VIEW_ID);
      IFolderLayout left2Bot2 =
            layout.createFolder("left2Bot2", IPageLayout.BOTTOM, (float) 0.50, ArtifactHyperView.VIEW_ID);
      left2Bot2.addView(SkyWalkerView.VIEW_ID);

      IFolderLayout right1 = layout.createFolder("right1", IPageLayout.RIGHT, (float) 0.65, editorArea);
      right1.addView(BranchView.VIEW_ID);
      IFolderLayout right1Bot1 =
            layout.createFolder("right1Bot1", IPageLayout.BOTTOM, (float) 0.35, BranchView.VIEW_ID);
      right1Bot1.addView("org.eclipse.pde.runtime.LogView");

      IFolderLayout bottom1 = layout.createFolder("bottom1", IPageLayout.BOTTOM, (float) 0.55, editorArea);
      bottom1.addView(WorldView.VIEW_ID);
   }
}
