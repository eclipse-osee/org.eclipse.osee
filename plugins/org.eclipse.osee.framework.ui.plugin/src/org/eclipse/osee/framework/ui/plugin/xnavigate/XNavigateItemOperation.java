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
package org.eclipse.osee.framework.ui.plugin.xnavigate;

import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.KeyedImage;

/**
 * @author Roberto E. Escobar
 */
public final class XNavigateItemOperation extends XNavigateItem {

   private final IOperationFactory operation;

   public XNavigateItemOperation(XNavigateItem parent, KeyedImage oseeImage, String name, IOperationFactory operation) {
      super(parent, name, oseeImage);
      this.operation = operation;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) throws Exception {
      super.run(tableLoadOptions);
      Operations.executeAsJob(operation.createOperation(), true);
   }
}