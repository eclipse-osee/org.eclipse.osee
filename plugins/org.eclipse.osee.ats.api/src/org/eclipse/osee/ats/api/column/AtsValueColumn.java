/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.column;

import org.eclipse.osee.ats.api.config.ColumnAlign;
import org.eclipse.osee.ats.api.util.ColorColumn;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
public class AtsValueColumn {
   private String name;
   private String id;
   private String namespace;
   private int width;
   private ColumnAlign align;
   private boolean visible;
   private String sortDataType;
   private String booleanOnTrueShow;
   private String booleanOnFalseShow;
   private String booleanNotSetShow;
   private boolean columnMultiEdit;
   private String description;
   private ColorColumn color;
   private Boolean actionRollup;
   private Boolean inheritParent;

   public AtsValueColumn() {
      // For JaxRs Instantiation
   }

   public AtsValueColumn(String id, String name, int width, String align, boolean show, ColumnType sortDataType, boolean multiColumnEditable, String description, Boolean actionRollup, Boolean inheritParent) {
      this.id = id;
      this.name = name;
      this.width = width;
      this.align = ColumnAlign.valueOf(align);
      this.visible = show;
      this.sortDataType = sortDataType.name();
      this.columnMultiEdit = multiColumnEditable;
      if (Strings.isValid(description)) {
         this.description = description;
      }
      this.actionRollup = actionRollup;
      this.inheritParent = inheritParent;
   }

   public int getWidth() {
      return width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public boolean isVisible() {
      return visible;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public String getSortDataType() {
      return sortDataType;
   }

   public void setSortDataType(String sortDataType) {
      this.sortDataType = sortDataType;
   }

   public boolean isColumnMultiEdit() {
      return columnMultiEdit;
   }

   public void setColumnMultiEdit(boolean columnMultiEdit) {
      this.columnMultiEdit = columnMultiEdit;
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

   public ColumnAlign getAlign() {
      return align;
   }

   public void setAlign(ColumnAlign align) {
      this.align = align;
   }

   public String getNamespace() {
      return namespace;
   }

   public void setNamespace(String namespace) {
      this.namespace = namespace;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getBooleanOnTrueShow() {
      return booleanOnTrueShow;
   }

   public void setBooleanOnTrueShow(String booleanOnTrueShow) {
      this.booleanOnTrueShow = booleanOnTrueShow;
   }

   public String getBooleanOnFalseShow() {
      return booleanOnFalseShow;
   }

   public void setBooleanOnFalseShow(String booleanOnFalseShow) {
      this.booleanOnFalseShow = booleanOnFalseShow;
   }

   public String getBooleanNotSetShow() {
      return booleanNotSetShow;
   }

   public void setBooleanNotSetShow(String booleanNotSetShow) {
      this.booleanNotSetShow = booleanNotSetShow;
   }

   @Override
   public String toString() {
      return "AtsAttributeValueColumn [name=" + name + ", namespace=" + namespace + "]";
   }

   public ColorColumn getColor() {
      return color;
   }

   public void setColor(ColorColumn color) {
      this.color = color;
   }

   public Boolean isActionRollup() {
      return actionRollup;
   }

   public void setActionRollup(Boolean actionRollup) {
      this.actionRollup = actionRollup;
   }

   public String getId() {
      String result = null;
      if (Strings.isValid(id)) {
         result = id;
      }
      return result;
   }

   public void setId(String id) {
      this.id = id;
   }

   public Boolean isInheritParent() {
      return inheritParent;
   }

   public void setInheritParent(Boolean inheritParent) {
      this.inheritParent = inheritParent;
   }

}
