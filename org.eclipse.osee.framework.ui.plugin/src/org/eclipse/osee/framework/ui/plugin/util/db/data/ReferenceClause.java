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

package org.eclipse.osee.framework.ui.plugin.util.db.data;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class ReferenceClause implements Xmlizable {
   public static final String REFERENCES_TAG = "References";

   public enum ReferencesFields {
      schema, table, column, onUpdate, onDelete;
   }

   public enum OnDeleteEnum {
      NO_ACTION, RESTRICT, CASCADE, SET_NULL, UNSPECIFIED;

      public String toString() {
         String toReturn = super.toString();
         toReturn = toReturn.replaceAll("_", " ");
         return toReturn;
      }
   }

   public enum OnUpdateEnum {
      NO_ACTION, RESTRICT, UNSPECIFIED;

      public String toString() {
         String toReturn = super.toString();
         toReturn = toReturn.replaceAll("_", " ");
         return toReturn;
      }
   }

   private String schema;
   private String table;
   List<String> columns;
   OnDeleteEnum onDeleteAction;
   OnUpdateEnum onUpdateAction;

   public ReferenceClause(String schema, String table) {
      this.schema = schema.toUpperCase();
      this.schema = this.schema.trim();
      this.table = table.toUpperCase();
      this.table = this.table.trim();
      this.columns = new ArrayList<String>();
      this.onDeleteAction = OnDeleteEnum.UNSPECIFIED;
      this.onUpdateAction = OnUpdateEnum.UNSPECIFIED;
   }

   public void setSchema(String schema) {
      this.schema = schema;
   }

   public OnDeleteEnum getOnDeleteAction() {
      return onDeleteAction;
   }

   public OnUpdateEnum getOnUpdateAction() {
      return onUpdateAction;
   }

   public void setOnDeleteAction(OnDeleteEnum onDeleteAction) {
      this.onDeleteAction = onDeleteAction;
   }

   public void setOnUpdateAction(OnUpdateEnum onUpdateAction) {
      this.onUpdateAction = onUpdateAction;
   }

   public void addColumn(String column) {
      column = column.toUpperCase();
      column = column.trim();
      columns.add(column);
   }

   public String getFullyQualifiedTableName() {
      return schema + "." + table;
   }

   public List<String> getColumns() {
      return columns;
   }

   public String getCommaSeparatedColumnsList() {
      return StringFormat.listToCommaSeparatedString(columns);
   }

   public String toString() {
      String toReturn = REFERENCES_TAG + ": " + getFullyQualifiedTableName();
      toReturn += "\t[";
      for (String column : columns) {
         toReturn += column + " ";
      }
      toReturn += "]";
      return toReturn;
   }

   public Element toXml(Document doc) {
      Element refElement = doc.createElement(REFERENCES_TAG);
      refElement.setAttribute(ReferencesFields.schema.name(), schema);
      refElement.setAttribute(ReferencesFields.table.name(), table);
      refElement.setAttribute(ReferencesFields.column.name(), getCommaSeparatedColumnsList());

      if (!onDeleteAction.equals(OnDeleteEnum.UNSPECIFIED)) {
         refElement.setAttribute(ReferencesFields.onDelete.name(), onDeleteAction.toString());
      }

      if (!onUpdateAction.equals(OnUpdateEnum.UNSPECIFIED)) {
         refElement.setAttribute(ReferencesFields.onUpdate.name(), onUpdateAction.toString());
      }
      return refElement;
   }

   @Override
   public boolean equals(Object otherObject) {
      if (otherObject instanceof ReferenceClause == false) {
         return false;
      }
      if (this == otherObject) {
         return true;
      }
      ReferenceClause that = (ReferenceClause) otherObject;
      return new EqualsBuilder().appendSuper(super.equals(otherObject)).append(this.getFullyQualifiedTableName(),
            that.getFullyQualifiedTableName()).append(this.columns, that.getColumns()).append(this.onDeleteAction,
            that.getOnDeleteAction()).append(this.onUpdateAction, that.getOnUpdateAction()).isEquals();
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder(113, 67).append(schema).append(table).append(columns).append(onDeleteAction).append(
            onUpdateAction).toHashCode();
   }
}
