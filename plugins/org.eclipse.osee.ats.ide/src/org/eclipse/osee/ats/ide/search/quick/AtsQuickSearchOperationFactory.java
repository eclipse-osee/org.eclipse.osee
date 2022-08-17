/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.search.quick;

import org.eclipse.jface.window.Window;
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
      if (dialog.open() == Window.OK) {
         return new AtsQuickSearchOperation(
            new AtsQuickSearchData("Search by Strings", dialog.getEntry(), dialog.isChecked()));
      }
      return null;
   }
}
