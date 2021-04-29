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

   public XNavigateItemOperation(XNavigateItem parent, OseeImage oseeImage, String name, IOperationFactory operationFactory) {
      this(parent, ImageManager.create(oseeImage), name, operationFactory);
   }

   public XNavigateItemOperation(XNavigateItem parent, KeyedImage oseeImage, String name, IOperationFactory operationFactory) {
      super(parent, name, oseeImage);
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
}