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
package org.eclipse.osee.framework.ui.branch.graph.core;

import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 */
public class BranchGraphPaletteProvider {

   private static final String[] drawerNames = new String[] {"Filters"};
   private PaletteRoot paletteRoot;
   private final BranchGraphEditor editor;

   public BranchGraphPaletteProvider(BranchGraphEditor editor) {
      this.paletteRoot = null;
      this.editor = editor;
   }

   public PaletteRoot getPaletteRoot() {
      if (paletteRoot == null) {
         paletteRoot = new PaletteRoot();
         paletteRoot.add(createToolsGroup(paletteRoot));
         //         addDrawers(paletteRoot);
      }
      return paletteRoot;
   }

   private void addDrawers(PaletteRoot paletteRoot) {
      for (String drawerName : drawerNames) {
         PaletteContainer container = new PaletteDrawer(drawerName);

         String name = "one";
         String description = "example";
         Class<?> clazz = Object.class;

         container.add(
            createComponent(name, description, clazz, FrameworkImage.RECTANGLE_16, FrameworkImage.RECTANGLE_24));
         paletteRoot.add(container);
      }
   }

   private ToolEntry createComponent(String label, String description, Class<?> clazz, KeyedImage smallImage, KeyedImage largeImage) {
      ToolEntry toolEntry = new ToolEntry(label, description, ImageManager.getImageDescriptor(smallImage),
         ImageManager.getImageDescriptor(largeImage)) {

      };
      return toolEntry;
   }

   private PaletteContainer createToolsGroup(PaletteRoot palette) {
      PaletteToolbar toolbar = new PaletteToolbar("Tools");

      ToolEntry tool = new PanningSelectionToolEntry();
      toolbar.add(tool);
      palette.setDefaultEntry(tool);

      toolbar.add(new MarqueeToolEntry());
      return toolbar;
   }
}
