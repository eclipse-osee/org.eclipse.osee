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

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AtsAttributeValueColumn {
   private String name;
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

}
