/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.mbse.cameo.browser;

import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 * @author David W. Miller
 */
public class BranchTableModel extends AbstractTableModel {

   private static final long serialVersionUID = 1790682018158846189L;
   private List<BranchData> branchData = new ArrayList<BranchData>();
   private final String[] columnNames = {"Branch Name"};

   public BranchTableModel() {
   }

   public BranchTableModel(List<BranchData> branchData) {
      this.branchData = branchData;
   }

   @Override
   public String getColumnName(int column) {
      return columnNames[column];
   }

   @Override
   public int getColumnCount() {
      return columnNames.length;
   }

   @Override
   public int getRowCount() {
      return branchData.size();
   }

   @Override
   public Object getValueAt(int row, int column) {
      Object branchAttribute = null;
      BranchData branchObject = branchData.get(row);
      switch (column) {
         case 0:
            branchAttribute = branchObject.getName();
            break;
         default:
            break;
      }
      return branchAttribute;
   }

   public void addBranch(BranchData branch) {
      branchData.add(branch);
      fireTableDataChanged();
   }

   public BranchData getBranchData(int row) {
      if (row < branchData.size()) {
         return branchData.get(row);
      }
      return null;
   }
}
