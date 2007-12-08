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
import org.eclipse.osee.framework.ui.plugin.util.db.data.TableElement.TableSections;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class ConstraintElement implements Xmlizable {

   public enum ConstraintFields {
      id, schema, type, appliesTo, deferrable
   };

   private ConstraintTypes constraintType;
   private boolean deferrable;
   private String schema;
   private String id;
   private List<String> columns;

   public ConstraintElement(ConstraintTypes constraintType, String schema, String id, boolean deferrable) {
      this.schema = schema.toUpperCase();
      this.schema = this.schema.trim();
      this.id = id.toUpperCase();
      this.id = this.id.trim();
      this.deferrable = deferrable;
      this.constraintType = constraintType;
      this.columns = new ArrayList<String>();
   }

   public List<String> getColumns() {
      return columns;
   }

   public String getId() {
      return id;
   }

   public String getSchema() {
      return schema;
   }

   public void addColumn(String columnName) {
      columnName = columnName.toUpperCase();
      columnName = columnName.trim();
      this.columns.add(columnName);
   }

   public void setId(String id) {
      id = id.toUpperCase();
      id = id.trim();
      this.id = id;
   }

   public void setSchema(String schema) {
      schema = schema.toUpperCase();
      schema = schema.trim();
      this.schema = schema;
   }

   public String getFullyQualifiedId() {
      if (schema != null && !schema.equals("")) {
         return schema + "." + id;
      } else {
         return id;
      }
   }

   /**
    * @return Returns the deferrable.
    */
   public boolean isDeferrable() {
      return deferrable;
   }

   public ConstraintTypes getConstraintType() {
      return constraintType;
   }

   public String getCommaSeparatedColumnsList() {
      return StringFormat.listToCommaSeparatedString(columns);
   }

   public String toString() {
      String toReturn = TableSections.Constraint + ": ";
      toReturn += "\t" + ConstraintFields.schema + "[" + schema + "]";
      toReturn += "\t" + ConstraintFields.id + "[" + id + "]";
      toReturn += "\t" + ConstraintFields.type + "[" + constraintType.toString() + "]";
      toReturn += "\t" + ConstraintFields.appliesTo + "[" + getCommaSeparatedColumnsList() + "]";
      toReturn += "\t" + ConstraintFields.deferrable + "[" + Boolean.toString(deferrable) + "]";
      return toReturn;
   }

   public Element toXml(Document doc) {
      Element constraintElement = doc.createElement(TableSections.Constraint.toString());
      constraintElement.setAttribute(ConstraintFields.schema.name(), schema);
      constraintElement.setAttribute(ConstraintFields.id.name(), id);
      constraintElement.setAttribute(ConstraintFields.type.name(), constraintType.toString());
      constraintElement.setAttribute(ConstraintFields.appliesTo.name(), getCommaSeparatedColumnsList());
      constraintElement.setAttribute(ConstraintFields.deferrable.name(), Boolean.toString(deferrable));
      return constraintElement;
   }

   @Override
   public boolean equals(Object otherObject) {
      if (otherObject instanceof ConstraintElement == false) {
         return false;
      }
      if (this == otherObject) {
         return true;
      }
      ConstraintElement that = (ConstraintElement) otherObject;
      return new EqualsBuilder().appendSuper(super.equals(otherObject)).append(this.constraintType,
            that.getConstraintType()).append(this.schema, that.getSchema()).append(this.id, that.getId()).append(
            this.columns, that.getColumns()).append(this.deferrable, that.deferrable).isEquals();
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder(37, 11).append(constraintType).append(schema).append(id).append(columns).append(
            deferrable).toHashCode();
   }
}
