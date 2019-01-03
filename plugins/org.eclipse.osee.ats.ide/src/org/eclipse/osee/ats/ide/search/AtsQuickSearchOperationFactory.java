/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.search;

import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.ui.plugin.xnavigate.IOperationFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryCheckDialog;

/**
 * @author Donald G. Dunne
 */
public class AtsQuickSearchOperationFactory implements IOperationFactory {

   @Override
   public IOperation createOperation() {
      EntryCheckDialog dialog =
         new EntryCheckDialog("Search by Strings", "Enter search strings", "Include Completed/Cancelled Workflows");
      if (dialog.open() == 0) {
         return new AtsQuickSearchOperation(
            new AtsQuickSearchData("Search by Strings", dialog.getEntry(), dialog.isChecked()));
      }
      return null;
   }
}
