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

package org.eclipse.osee.framework.ui.skynet.blam;

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.enums.OseeImage;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * BLAMs should not be manually added to NavigateItems. They are auto created and added through BlamNavigateViewItems.
 *
 * @author Donald G. Dunne
 */
public class XNavigateBlamItem extends XNavigateItem {

   private final AbstractBlam blamOperation;

   protected XNavigateBlamItem(final AbstractBlam blamOperation, OseeImage oseeImage, XNavItemCat... xNavItemCat) {
      super(blamOperation.getName(), ImageManager.create(oseeImage), xNavItemCat);
      this.blamOperation = blamOperation;
   }

   protected XNavigateBlamItem(AbstractBlam blamOperation) {
      this(blamOperation, blamOperation.getImage(),
         blamOperation.getCategories().toArray(new XNavItemCat[blamOperation.getCategories().size()]));
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      // Need a new copy of the BLAM operation so widgets and fields don't collide
      String blamClassName = blamOperation.getClass().getName();
      try {
         Class<?> blamClass = blamOperation.getClass().getClassLoader().loadClass(blamClassName);
         AbstractBlam newBlamOp = (AbstractBlam) blamClass.getDeclaredConstructor().newInstance();
         BlamEditor.edit(newBlamOp);
      } catch (ClassNotFoundException ex) {
         // No test class for this blam
      } catch (Exception ex) {
         OseeLog.logf(BlamService.class, Level.WARNING, "Error instantiating test %s: %s", blamClassName,
            ex.getMessage());
      }
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return blamOperation.getUserGroups();
   }

}