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
package org.eclipse.osee.ats.util.widgets.role;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public enum UserRoleColumn {

   Empty_Col("", 0, SWT.LEFT, false, SortDataType.String, false),

   Role_Col("Role", 100, SWT.LEFT, true, SortDataType.String, false),

   User_Col("User", 150, SWT.LEFT, true, SortDataType.String, false),

   Hours_Spent_Col("Hours Spent", 80, SWT.LEFT, true, SortDataType.Float, false),

   Completed_Col("Completed", 80, SWT.LEFT, true, SortDataType.String, false),

   Num_Major_Col("Major", 60, SWT.CENTER, true, SortDataType.Integer, false),

   Num_Minor_Col("Minor", 60, SWT.CENTER, true, SortDataType.Integer, false),

   Num_Issues_Col("Issues", 60, SWT.CENTER, true, SortDataType.Integer, false);

   private final String name;
   private final int width;
   private final int align;
   private final boolean show;
   private final SortDataType sortDataType;
   private final String desc;
   private static Map<String, UserRoleColumn> nameToAtsXColumn = new HashMap<String, UserRoleColumn>();
   private final boolean multiColumnEditable;

   public static UserRoleColumn getAtsXColumn(XViewerColumn xCol) {
      if (nameToAtsXColumn.size() == 0) {
         for (UserRoleColumn atsCol : UserRoleColumn.values())
            nameToAtsXColumn.put(atsCol.getName(), atsCol);
      }
      return nameToAtsXColumn.get(xCol.getSystemName());
   }

   public XViewerColumn getXViewerColumn(UserRoleColumn atsXCol) {
      XViewerColumn xCol =
            new XViewerColumn(atsXCol.name, atsXCol.width, atsXCol.width, atsXCol.align, atsXCol.isShow(),
                  atsXCol.sortDataType, 0);
      if (atsXCol.getDesc() != null)
         xCol.setToolTip(atsXCol.getName() + ":\n" + atsXCol.getDesc());
      else
         xCol.setToolTip(atsXCol.getDesc());
      return xCol;
   }

   private UserRoleColumn(String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable) {
      this(name, width, align, show, sortDataType, multiColumnEditable, null);
   }

   private UserRoleColumn(String name, int width, int align, boolean show, SortDataType sortDataType, boolean multiColumnEditable, String desc) {
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
