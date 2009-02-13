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
package org.eclipse.osee.framework.ui.data.model.editor;

import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.ui.palette.PaletteCustomizer;
import org.eclipse.gef.ui.palette.customize.DefaultEntryPage;
import org.eclipse.gef.ui.palette.customize.DrawerEntryPage;
import org.eclipse.gef.ui.palette.customize.EntryPage;

/**
 * @author Roberto E. Escobar
 */
public final class ODMPaletteCustomizer extends PaletteCustomizer {

   protected static final String ERROR_MESSAGE = "Invalid Characters";

   /**
    * @see org.eclipse.gef.ui.palette.PaletteCustomizer#getPropertiesPage(PaletteEntry)
    */
   public EntryPage getPropertiesPage(PaletteEntry entry) {
      if (entry.getType().equals(PaletteDrawer.PALETTE_TYPE_DRAWER)) {
         return new TypeDrawerEntryPage();
      }
      return new TypeEntryPage();
   }

   /**
    * @see org.eclipse.gef.ui.palette.PaletteCustomizer#revertToSaved()
    */
   public void revertToSaved() {
   }

   /**
    * @see org.eclipse.gef.ui.palette.PaletteCustomizer#save()
    */
   public void save() {
   }

   private class TypeEntryPage extends DefaultEntryPage {
      protected void handleNameChanged(String text) {
         if (text.indexOf('*') >= 0) {
            getPageContainer().showProblem(ERROR_MESSAGE);
         } else {
            super.handleNameChanged(text);
            getPageContainer().clearProblem();
         }
      }
   }

   private class TypeDrawerEntryPage extends DrawerEntryPage {
      protected void handleNameChanged(String text) {
         if (text.indexOf('*') >= 0) {
            getPageContainer().showProblem(ERROR_MESSAGE);
         } else {
            super.handleNameChanged(text);
            getPageContainer().clearProblem();
         }
      }
   }
}