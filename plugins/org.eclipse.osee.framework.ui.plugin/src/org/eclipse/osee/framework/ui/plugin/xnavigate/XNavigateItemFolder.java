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

package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Donald G. Dunne
 */
public class XNavigateItemFolder extends XNavigateItem {

   private Collection<IUserGroupArtifactToken> groups = null;

   public XNavigateItemFolder(String name, XNavItemCat... xNavItemCat) {
      super(name, PluginUiImage.FOLDER, xNavItemCat);
   }

   public XNavigateItemFolder(String name, KeyedImage oseeImage, XNavItemCat... xNavItemCat) {
      super(name, oseeImage, xNavItemCat);
   }

   public XNavigateItemFolder(String name, OseeImage oseeImage, XNavItemCat... xNavItemCat) {
      super(name, ImageManager.create(oseeImage), xNavItemCat);
   }

   public XNavigateItemFolder(String name, KeyedImage oseeImage, Collection<IUserGroupArtifactToken> groups, XNavItemCat... xNavItemCat) {
      super(name, oseeImage, xNavItemCat);
      this.groups = groups;
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      if (groups != null && !groups.isEmpty()) {
         return groups;
      }
      if (categories.contains(XNavItemCat.OSEE_ADMIN)) {
         return Arrays.asList(CoreUserGroups.OseeAdmin);
      }
      return Arrays.asList(CoreUserGroups.Everyone);
   }

}