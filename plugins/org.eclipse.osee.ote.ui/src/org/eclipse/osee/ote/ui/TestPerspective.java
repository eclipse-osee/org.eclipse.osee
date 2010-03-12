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

package org.eclipse.osee.ote.ui;

import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.osee.framework.ui.service.control.view.ServiceManagerView;
import org.eclipse.osee.ote.ui.navigate.OteNavigateView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class TestPerspective implements IPerspectiveFactory {

   public TestPerspective() {
      super();
   }

   public void createInitialLayout(IPageLayout layout) {
      defineActions(layout);
      defineLayout(layout);
   }

   public void defineActions(IPageLayout layout) {

      // Add "show views".
      layout.addShowViewShortcut(ServiceManagerView.VIEW_ID);
      layout.addShowViewShortcut(OteNavigateView.VIEW_ID);
      //      layout.addShowViewShortcut(EgiView.VIEW_ID);
      //      layout.addShowViewShortcut(EufdView.VIEW_ID);
      //      layout.addShowViewShortcut(GunView.VIEW_ID);
      //      layout.addShowViewShortcut(IhdssView.VIEW_ID);
      //      layout.addShowViewShortcut(KeyboardView.VIEW_ID);
      //      layout.addShowViewShortcut(LmpView.VIEW_ID);
      //      layout.addShowViewShortcut(MpdView.VIEW_ID);
      //      layout.addShowViewShortcut(MtadsView.VIEW_ID);
      //      layout.addShowViewShortcut(RocketView.VIEW_ID);
      //      layout.addShowViewShortcut(UtaView.VIEW_ID);
      //      layout.addShowViewShortcut(EmulatorsView.VIEW_ID);
      layout.addShowViewShortcut("org.eclipse.pde.runtime.LogView");
      layout.addShowViewShortcut("org.eclipse.ui.console.ConsoleView");
      layout.addShowViewShortcut("org.eclipse.jdt.ui.PackageExplorer");
      layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);

   }

   public void defineLayout(IPageLayout layout) {
      // Editors are placed for free.
      String editorArea = layout.getEditorArea();

      // Place navigator and outline to left of
      // editor area.
      IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, (float) 0.20, editorArea);
      left.addView(OteNavigateView.VIEW_ID);
      left.addView("org.eclipse.jdt.ui.PackageExplorer");

      IFolderLayout lower = layout.createFolder("bottom", IPageLayout.BOTTOM, (float) 0.65, editorArea);
      lower.addView("org.eclipse.pde.runtime.LogView");
      lower.addView("org.eclipse.ui.console.ConsoleView");
      lower.addView(IPageLayout.ID_PROBLEM_VIEW);

      layout.addActionSet(JavaUI.ID_ACTION_SET);
      layout.addActionSet(JavaUI.ID_ELEMENT_CREATION_ACTION_SET);
      layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);

   }
}
