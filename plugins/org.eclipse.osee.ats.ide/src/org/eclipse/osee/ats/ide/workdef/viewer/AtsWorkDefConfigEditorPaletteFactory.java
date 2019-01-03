/*******************************************************************************
 * Copyright (c) 2004, 2008 Donald G. Dunne and others.
�* All rights reserved. This program and the accompanying materials
�* are made available under the terms of the Eclipse Public License v1.0
�* which accompanies this distribution, and is available at
�* http://www.eclipse.org/legal/epl-v10.html
�*
�* Contributors:
�*����Donald G. Dunne - initial API and implementation
�*******************************************************************************/
package org.eclipse.osee.ats.ide.workdef.viewer;

import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteToolbar;
import org.eclipse.gef.palette.PanningSelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;

/**
 * Utility class that can create a GEF Palette.
 *
 * @see #createPalette()
 * @author Donald G. Dunne
 */
final class AtsWorkDefConfigEditorPaletteFactory {

   /**
    * Creates the PaletteRoot and adds all palette elements. Use this factory method to create a new palette for your
    * graphical editor.
    *
    * @return a new PaletteRoot
    */
   static PaletteRoot createPalette(AtsWorkDefConfigEditor editor) {
      PaletteRoot palette = new PaletteRoot();
      palette.add(createToolsGroup(palette, editor));
      return palette;
   }

   /** Create the "Tools" group. */
   private static PaletteContainer createToolsGroup(PaletteRoot palette, AtsWorkDefConfigEditor editor) {
      PaletteToolbar toolbar = new PaletteToolbar("Tools");

      // Add a selection tool to the group
      ToolEntry tool = new PanningSelectionToolEntry();
      toolbar.add(tool);
      palette.setDefaultEntry(tool);

      // Add a marquee tool to the group
      toolbar.add(new MarqueeToolEntry());
      return toolbar;
   }

   private AtsWorkDefConfigEditorPaletteFactory() {
      // Utility class
   }

}