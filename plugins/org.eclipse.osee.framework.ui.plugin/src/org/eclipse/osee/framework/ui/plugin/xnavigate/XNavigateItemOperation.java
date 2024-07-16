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
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 */
public final class XNavigateItemOperation extends XNavigateItem {

   private final IOperationFactory operationFactory;

   public XNavigateItemOperation(OseeImage oseeImage, String name, IOperationFactory operationFactory, XNavItemCat... xNavItemCat) {
      this(ImageManager.create(oseeImage), name, operationFactory, xNavItemCat);
   }

   public XNavigateItemOperation(KeyedImage oseeImage, String name, IOperationFactory operationFactory, XNavItemCat... xNavItemCat) {
      super(name, oseeImage, xNavItemCat);
      this.operationFactory = operationFactory;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      super.run(tableLoadOptions);
      IOperation operation = operationFactory.createOperation();
      if (operation != null) {
         Operations.executeAsJob(operation, true);
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