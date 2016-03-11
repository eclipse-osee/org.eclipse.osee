/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.config;

import javax.xml.bind.annotation.XmlRootElement;
import org.eclipse.osee.ats.api.util.ColorColumn;
import org.eclipse.osee.ats.api.util.ColumnType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AtsAttributeValueColumn {
   private String name;
   private String id;
   private String namespace;
   private long attrTypeId;
   private String attrTypeName;
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
   private boolean actionRollup;

   public AtsAttributeValueColumn() {
      // For JaxRs Instantitaion
   }

   public AtsAttributeValueColumn(IAttributeType attributeType, String id, String name, int width, String align, boolean show, ColumnType sortDataType, boolean multiColumnEditable, String description, boolean actionRollup) {
      this(attributeType, id, name, width, align, show, sortDataType, multiColumnEditable, description);
      this.actionRollup = actionRollup;
   }

   public AtsAttributeValueColumn(IAttributeType attributeType, String id, String name, int width, String align, boolean show, ColumnType sortDataType, boolean multiColumnEditable, String description) {
      this.id = id;
      this.name = name;
      this.width = width;
      this.align = ColumnAlign.valueOf(align);
      this.visible = show;
      this.sortDataType = sortDataType.name();
      this.columnMultiEdit = multiColumnEditable;
      this.description = description;
      this.actionRollup = false;
   }

   public long getAttrTypeId() {
      return attrTypeId;
   }

   public void setAttrTypeId(long attrTypeId) {
      this.attrTypeId = attrTypeId;
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

   public String getAttrTypeName() {
      return attrTypeName;
   }

   public void setAttrTypeName(String attrTypeName) {
      this.attrTypeName = attrTypeName;
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
      return "AtsAttributeValueColumn [name=" + name + ", namespace=" + namespace + ", attrTypeId=" + attrTypeId + ", attrTypeName=" + attrTypeName + "]";
   }

   public ColorColumn getColor() {
      return color;
   }

   public void setColor(ColorColumn color) {
      this.color = color;
   }

   public boolean isActionRollup() {
      return actionRollup;
   }

   public void setActionRollup(boolean actionRollup) {
      this.actionRollup = actionRollup;
   }

   public String getId() {
      String result = null;
      if (Strings.isValid(id)) {
         result = id;
      } else if (Strings.isValid(attrTypeName)) {
         result = attrTypeName;
      }
      return result;
   }

   public void setId(String id) {
      this.id = id;
   }

}
