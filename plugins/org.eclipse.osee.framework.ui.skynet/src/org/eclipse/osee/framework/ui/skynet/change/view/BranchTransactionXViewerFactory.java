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

package org.eclipse.osee.framework.ui.skynet.change.view;

import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xBranch.BranchXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;

/**
 * @author Donald G. Dunne
 */
public class BranchTransactionXViewerFactory extends BranchXViewerFactory {

   public final static String NAMESPACE = "BranchTransactionXViewer";

   public BranchTransactionXViewerFactory(IOseeTreeReportProvider reportProvider) {
      super(reportProvider);
      setNamespace(NAMESPACE);
      this.clearColumnRegistration();
      registerColumns(transaction, createdDate, author, comment);
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData customizeData = super.getDefaultTableCustomizeData();
      for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
         if (xCol.getId().equals(transaction.getId())) {
            xCol.setSortForward(false);
         }
      }
      customizeData.getSortingData().setSortingNames(transaction.getId());
      return customizeData;
   }

   @Override
   public boolean isBranchManager() {
      return false;
   }
}
