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
package org.eclipse.osee.framework.ui.skynet.user.perspective;

import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchView;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * @author Donald G. Dunne
 */
public class UserPerspective implements IPerspectiveFactory {
   public static String ID = "org.eclipse.framework.ui.skynet.UserPerspective";

   @Override
   public void createInitialLayout(final IPageLayout layout) {
      defineActions(layout);
      defineLayout(layout);
   }

   public void defineActions(final IPageLayout layout) {
      layout.addShowViewShortcut(UserNavigateView.VIEW_ID);
      layout.addShowViewShortcut(ArtifactExplorer.VIEW_ID);
      layout.addShowViewShortcut(QuickSearchView.VIEW_ID);
   }

   public void defineLayout(final IPageLayout layout) {
      final String editorArea = layout.getEditorArea();

      final IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.2f, editorArea);
      final IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, 0.7f, "left");

      left.addView(UserNavigateView.VIEW_ID);
      bottomLeft.addView(QuickSearchView.VIEW_ID);
   }
}
