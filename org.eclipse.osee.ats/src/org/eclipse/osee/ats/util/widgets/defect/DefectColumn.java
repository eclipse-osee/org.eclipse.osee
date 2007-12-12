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
package org.eclipse.osee.ats.util.widgets.defect;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public enum DefectColumn {

   Empty_Col("", 0, SWT.LEFT, false, SortDataType.String, false),

   Severity_Col("Severity", 35, SWT.LEFT, true, SortDataType.String, false),

   Disposition_Col("Disposition", 35, SWT.CENTER, true, SortDataType.String, false),

   Closed_Col("Closed", 80, SWT.LEFT, true, SortDataType.Boolean, false),

   User_Col("User", 100, SWT.LEFT, true, SortDataType.String, false),

   Created_Date_Col("Created Date", 80, SWT.LEFT, true, SortDataType.Date, false),

   Injection_Activity_Col("Injection Activity", 35, SWT.LEFT, true, SortDataType.String, false),

   Description_Col("Description", 200, SWT.CENTER, true, SortDataType.String, false),

   Location_Col("Location", 200, SWT.LEFT, true, SortDataType.String, false),

   Resolution_Col("Resolution", 200, SWT.LEFT, true, SortDataType.String, false);

   private final String name;
   private final int width;
   private final int align;
   private final boolean show;
   private final SortDataType sortDataType;
   private final String desc;
   private static Map<String, DefectColumn> nameToAtsXColumn = new HashMap<String, DefectColumn>();
   private final boolean multiColumnEditable;

   public static DefectColumn getAtsXColumn(XViewerColumn xCol) {
      if (nameToAtsXColumn.size() == 0) {
         for (DefectColumn atsCol : DefectColumn.values())
            nameToAtsXColumn.put(atsCol.getName(), atsCol);
      }
      return nameToAtsXColumn.get(xCol.getSystemName());
   }

   public XViewerColumn getXViewerColumn(DefectColumn atsXCol) {
      XViewerColumn xCol =
            new XViewerColumn(atsXCol.name, atsXCol.width, atsXCol.width, atsXCol.align, atsXCol.isShow(),
                  atsXCol.sortDataType, 0);
      if (atsXCol.getDesc() != null)
         xCol.setToolTip(atsXCol.getName() + ":\n" + atsXCol.getDesc());
      else
         xCol.setToolTip(atsXCol.getDesc());
      return xCol;
   }

   private DefectColumn(String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      this(name, width, align, show, sortDataType, multiColumnEditable, null);
   }

   private DefectColumn(String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String desc) {
      this.name = name;
      this.width = width;
      this.align = align;
      this.show = show;
      this.sortDataType = sortDataType;
      this.multiColumnEditable = multiColumnEditable;
      this.desc = desc;
   }

   /**
    * @return the name
    */
   public String getName() {
      return name;
   }

   /**
    * @return the align
    */
   public int getAlign() {
      return align;
   }

   /**
    * @return the show
    */
   public boolean isShow() {
      return show;
   }

   /**
    * @return the sortDataType
    */
   public SortDataType getSortDataType() {
      return sortDataType;
   }

   /**
    * @return the width
    */
   public int getWidth() {
      return width;
   }

   /**
    * @return the desc
    */
   public String getDesc() {
      return desc;
   }

   public boolean isMultiColumnEditable() {
      return multiColumnEditable;
   }

}
