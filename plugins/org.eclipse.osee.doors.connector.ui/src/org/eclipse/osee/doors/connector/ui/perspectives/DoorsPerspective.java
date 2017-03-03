/*
 * Copyright (c) 2012 Robert Bosch Engineering and Business Solutions Ltd India. All rights reserved. This program and
 * the accompanying materials are made available under the terms of the Eclipse Public License v1.0 which accompanies
 * this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.osee.doors.connector.ui.perspectives;

import java.io.File;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.osee.doors.connector.ui.viewer.RdfExplorer;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;

/**
 * This class is meant to serve as an example for how various contributions are made to a perspective. Note that some of
 * the extension point id's are referred to as API constants while others are hardcoded and may be subject to change.
 *
 * @author Chandan Bandemutt
 */
public class DoorsPerspective implements IPerspectiveFactory, IWorkbenchListener {

   private static final String JAVA_SCRIPT_HTML = "JavaScript.html";

   /**
    * Default Constructor
    */
   public DoorsPerspective() {
      super();
   }

   @Override
   public void createInitialLayout(final IPageLayout layout) {

      PlatformUI.getPreferenceStore().putValue(IWorkbenchPreferenceConstants.SHOW_TRADITIONAL_STYLE_TABS, "false");

      PlatformUI.getPreferenceStore().putValue(IWorkbenchPreferenceConstants.DOCK_PERSPECTIVE_BAR,
         IWorkbenchPreferenceConstants.TOP_RIGHT);

      layout.setEditorAreaVisible(true);

      final String editorArea = layout.getEditorArea();

      final IFolderLayout left = layout.createFolder("left", IPageLayout.LEFT, 0.2f, editorArea);
      left.addView(RdfExplorer.VIEW_ID);

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void postShutdown(final IWorkbench workbench) {
      String fileName =
         ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator + DoorsPerspective.JAVA_SCRIPT_HTML;

      File file = new File(fileName);
      if (file.exists()) {
         file.delete();
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean preShutdown(final IWorkbench workbench, final boolean forced) {
      String fileName =
         ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator + DoorsPerspective.JAVA_SCRIPT_HTML;

      File file = new File(fileName);
      if (file.exists()) {
         file.delete();
      }
      return true;
   }

}
