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

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;

public enum PolicyTableColumns {
   dummyColumn("---", 0, XViewerAlign.Left, true, SortDataType.String, false, ""),
   delete("", 25, XViewerAlign.Center, true, SortDataType.String, false, ""),
   userName("User Name", 160, XViewerAlign.Left, true, SortDataType.String, false, ""),
   totalAccess("Access Level", 80, XViewerAlign.Left, true, SortDataType.String, false, ""),
   branchAccess("Branch", 75, XViewerAlign.Left, true, SortDataType.String, false, ""),
   artifactType("Artifact Type", 80, XViewerAlign.Left, true, SortDataType.String, false, ""),
   artifact("Artifact", 75, XViewerAlign.Left, true, SortDataType.String, false, "");

   private final String label;
   private final int width;
   private final XViewerAlign align;
   private final boolean show;
   private final SortDataType sortType;
   private final boolean multiColumnEditable;
   private final String description;

   private PolicyTableColumns(String label, int width, XViewerAlign align, boolean show, SortDataType sortType, boolean multiColumnEditable, String description) {
      this.label = label;
      this.width = width;
      this.align = align;
      this.show = show;
      this.sortType = sortType;
      this.multiColumnEditable = multiColumnEditable;
      this.description = description;
   }

   public final XViewerColumn getXViewerColumn() {
      XViewerColumn toReturn = null;
      if (this.equals(PolicyTableColumns.totalAccess)) {
         toReturn =
            new XViewerColumn(this.toString(), label, width, align, show, sortType, multiColumnEditable, description);
         toReturn.setMultiColumnEditable(true);
      } else {
         toReturn =
            new XViewerColumn(this.toString(), label, width, align, show, sortType, multiColumnEditable, description);
      }
      return toReturn;
   }

   public static String[] getNames() {
      String[] ret = new String[PolicyTableColumns.values().length];
      PolicyTableColumns[] columns = PolicyTableColumns.values();
      for (int i = 0; i < PolicyTableColumns.values().length; i++) {
         ret[i] = columns[i].toString();
      }

      return ret;
   }

   public String getLabel() {
      return label;
   }
}
