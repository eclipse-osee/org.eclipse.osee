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

package org.eclipse.osee.jdbc.internal.schema.data;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.internal.schema.data.TableElement.TableSections;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class ConstraintElement {

   public enum ConstraintFields {
      id,
      schema,
      type,
      appliesTo,
      deferrable
   };

   private final ConstraintTypes constraintType;
   private final boolean deferrable;
   private String schema;
   private String id;
   private final List<String> columns;

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
      if (Strings.isValid(schema)) {
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
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", columns);
   }

   @Override
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
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((columns == null) ? 0 : columns.hashCode());
      result = prime * result + ((constraintType == null) ? 0 : constraintType.hashCode());
      result = prime * result + (deferrable ? 1231 : 1237);
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((schema == null) ? 0 : schema.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      ConstraintElement other = (ConstraintElement) obj;
      if (columns == null) {
         if (other.columns != null) {
            return false;
         }
      } else if (!columns.equals(other.columns)) {
         return false;
      }
      if (constraintType != other.constraintType) {
         return false;
      }
      if (deferrable != other.deferrable) {
         return false;
      }
      if (id == null) {
         if (other.id != null) {
            return false;
         }
      } else if (!id.equals(other.id)) {
         return false;
      }
      if (schema == null) {
         if (other.schema != null) {
            return false;
         }
      } else if (!schema.equals(other.schema)) {
         return false;
      }
      return true;
   }

}
