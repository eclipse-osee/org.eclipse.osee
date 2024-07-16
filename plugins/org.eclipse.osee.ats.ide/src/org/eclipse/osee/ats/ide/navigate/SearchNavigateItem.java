/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.navigate;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItemProvider;
import org.eclipse.osee.ats.ide.world.WorldEditorUISearchItemProvider;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.LoadView;
import org.eclipse.osee.ats.ide.world.search.WorldUISearchItem;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class SearchNavigateItem extends XNavigateItem {

   private final WorldSearchItem wsi;

   public SearchNavigateItem(WorldSearchItem wsi, XNavItemCat... xNavItemCat) {
      super(wsi.getName(), AtsImage.GLOBE, xNavItemCat);
      this.wsi = wsi;
   }

   public SearchNavigateItem(WorldSearchItem wsi, AtsImage atsImage, XNavItemCat... xNavItemCat) {
      super(wsi.getName(), atsImage, xNavItemCat);
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

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      if (categories.contains(XNavItemCat.OSEE_ADMIN)) {
         return Arrays.asList(CoreUserGroups.OseeAdmin);
      }
      return Arrays.asList(CoreUserGroups.Everyone);
   }

}
