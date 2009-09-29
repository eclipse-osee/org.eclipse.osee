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
package org.eclipse.osee.framework.ui.skynet.access;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;

public enum PolicyTableColumns {
   dummyColumn("---", 0, 1 << 14, true, SortDataType.String, false, ""),
   delete("", 25, 1 << 24, true, SortDataType.String, false, ""),
   userName("User Name", 160, 1 << 14, true, SortDataType.String, false, ""),
   totalAccess("Total", 75, 1 << 14, true, SortDataType.String, false, ""),
   branchAccess("Branch", 75, 1 << 14, true, SortDataType.String, false, ""),
   artifactType("Artifact Type", 75, 1 << 14, true, SortDataType.String, false, ""),
   artifact("Artifact", 75, 1 << 14, true, SortDataType.String, false, "");

   private final String label;
   private final int width;
   private final int SWT;
   private final boolean show;
   private final SortDataType sortType;
   private final boolean multiColumnEditable;
   private final String description;

   private PolicyTableColumns(String label, int width, int SWT, boolean show, SortDataType sortType, boolean multiColumnEditable, String description) {
      this.label = label;
      this.width = width;
      this.SWT = SWT;
      this.show = show;
      this.sortType = sortType;
      this.multiColumnEditable = multiColumnEditable;
      this.description = description;
   }

   public final XViewerColumn getXViewerColumn() {
      return new XViewerColumn(this.toString(), label, width, SWT, show, sortType, multiColumnEditable, description);
   }

   public static String[] getNames() {
      String[] ret = new String[PolicyTableColumns.values().length];
      PolicyTableColumns[] columns = PolicyTableColumns.values();
      for (int i = 0; i < PolicyTableColumns.values().length; i++) {
         ret[i] = columns[i].toString();
      }

      return ret;
   }
}
