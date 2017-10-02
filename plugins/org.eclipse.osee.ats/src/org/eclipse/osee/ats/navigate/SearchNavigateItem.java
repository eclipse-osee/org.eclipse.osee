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
package org.eclipse.osee.ats.navigate;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.world.WorldEditor;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItemProvider;
import org.eclipse.osee.ats.world.WorldEditorUISearchItemProvider;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.ats.world.search.WorldUISearchItem;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class SearchNavigateItem extends XNavigateItem {

   private final WorldSearchItem wsi;

   public SearchNavigateItem(XNavigateItem parent, WorldSearchItem wsi) {
      super(parent, wsi.getName(), AtsImage.GLOBE);
      this.wsi = wsi;
   }

   public SearchNavigateItem(XNavigateItem parent, WorldSearchItem wsi, KeyedImage oseeImage) {
      super(parent, wsi.getName(), oseeImage);
      this.wsi = wsi;
   }

   public WorldSearchItem getWorldSearchItem() {
      return wsi;
   }

   @Override
   public Image getImage() {
      Image image = wsi.getImage();
      if (image != null) {
         return image;
      }
      return ImageManager.getImage(AtsImage.GLOBE);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      Collection<TableLoadOption> tableLoadOpts = Arrays.asList(tableLoadOptions);
      boolean dontCopyWsi = tableLoadOpts.contains(TableLoadOption.DontCopySearchItem);
      WorldSearchItem worldSearchItem = getWorldSearchItem();
      if (worldSearchItem.getLoadView() == LoadView.WorldEditor) {
         if (worldSearchItem instanceof WorldUISearchItem) {
            WorldEditor.open(new WorldEditorUISearchItemProvider(
               (WorldUISearchItem) (dontCopyWsi ? worldSearchItem : worldSearchItem.copy()), null, tableLoadOptions));
         } else if (worldSearchItem instanceof WorldEditorParameterSearchItem) {
            WorldEditor.open(new WorldEditorParameterSearchItemProvider(
               (WorldEditorParameterSearchItem) (dontCopyWsi ? worldSearchItem : worldSearchItem.copy()), null,
               tableLoadOptions));
         } else {
            AWorkbench.popup("ERROR", "Unhandled WorldEditor navigate item");
         }
      } else {
         AWorkbench.popup("ERROR", "Unhandled navigate item");
      }
   }

}
