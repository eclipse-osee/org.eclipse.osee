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

package org.eclipse.osee.framework.ui.skynet.widgets.xnavigate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;
import org.eclipse.swt.graphics.Image;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemBlam extends XNavigateItem {
   private final IBlamProvider blamProvider;

   public XNavigateItemBlam(AbstractBlam blamOperation, XNavItemCat xNavItemCat) {
      this(blamOperation, FrameworkImage.BLAM, xNavItemCat);
   }

   public XNavigateItemBlam(final AbstractBlam blamOperation, KeyedImage keyedImage, XNavItemCat... xNavItemCat) {
      super(blamOperation.getName(), keyedImage, xNavItemCat);
      Image image = blamOperation.getImage();
      if (image != null) {
         setImage(image);
      }
      blamProvider = new IBlamProvider() {

         @Override
         public AbstractBlam getBlam() {
            return blamOperation;
         }
      };
   }

   public XNavigateItemBlam(final AbstractBlam blamOperation, OseeImage oseeImage, XNavItemCat... xNavItemCat) {
      super(blamOperation.getName(), ImageManager.create(oseeImage), xNavItemCat);
      blamProvider = new IBlamProvider() {

         @Override
         public AbstractBlam getBlam() {
            return blamOperation;
         }
      };
   }

   public XNavigateItemBlam(IBlamProvider blamProvider, String name, OseeImage oseeImage, XNavItemCat xNavItemCat) {
      super(name, ImageManager.create(oseeImage), xNavItemCat);
      this.blamProvider = blamProvider;
   }

   public XNavigateItemBlam(IBlamProvider blamProvider, String name, KeyedImage keyedImage, XNavItemCat xNavItemCat) {
      super(name, keyedImage, xNavItemCat);
      this.blamProvider = blamProvider;
   }

   public XNavigateItemBlam(AbstractBlam blamOperation) {
      this(blamOperation, FrameworkImage.BLAM,
         blamOperation.getCategories().toArray(new XNavItemCat[blamOperation.getCategories().size()]));
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      // Need a new copy of the BLAM operation so widgets don't collide
      BlamEditor.edit(blamProvider.getBlam());
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      Set<IUserGroupArtifactToken> userGroups = new HashSet<IUserGroupArtifactToken>();
      if (categories.contains(XNavItemCat.OSEE_ADMIN)) {
         userGroups.add(CoreUserGroups.OseeAdmin);
      }
      for (IUserGroupArtifactToken group : blamProvider.getBlam().getUserGroups()) {
         userGroups.add(group);
      }
      return userGroups;
   }

}