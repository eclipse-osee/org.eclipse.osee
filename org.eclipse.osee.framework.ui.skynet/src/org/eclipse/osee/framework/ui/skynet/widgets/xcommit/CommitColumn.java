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
package org.eclipse.osee.framework.ui.skynet.widgets.xcommit;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public enum CommitColumn {

   Type_Col("Type", 100, SWT.LEFT, true, SortDataType.String, false),

   Status_Col("Status", 100, SWT.LEFT, true, SortDataType.String, false),

   Name_Col("Name", 450, SWT.LEFT, true, SortDataType.String, false),

   Short_Name_Col("Short Name", 200, SWT.LEFT, true, SortDataType.String, false);

   private final String name;
   private final int width;
   private final int align;
   private final boolean show;
   private final SortDataType sortDataType;
   private final String desc;
   private static Map<String, CommitColumn> nameToAtsXColumn = new HashMap<String, CommitColumn>();
   private final boolean multiColumnEditable;

   public static CommitColumn getAtsXColumn(XViewerColumn xCol) {
      if (nameToAtsXColumn.size() == 0) {
         for (CommitColumn atsCol : CommitColumn.values())
            nameToAtsXColumn.put(atsCol.getName(), atsCol);
      }
      return nameToAtsXColumn.get(xCol.getSystemName());
   }

   public XViewerColumn getXViewerColumn(CommitColumn atsXCol) {
      XViewerColumn xCol =
            new XViewerColumn(atsXCol.name, atsXCol.width, atsXCol.width, atsXCol.align, atsXCol.isShow(),
                  atsXCol.sortDataType, 0);
      if (atsXCol.getDesc() != null)
         xCol.setToolTip(atsXCol.getName() + ":\n" + atsXCol.getDesc());
      else
         xCol.setToolTip(atsXCol.getDesc());
      return xCol;
   }

   private CommitColumn(String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      this(name, width, align, show, sortDataType, multiColumnEditable, null);
   }

   private CommitColumn(String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String desc) {
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
