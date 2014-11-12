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
import org.eclipse.osee.jdbc.internal.schema.data.AppliesToClause.OrderType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class IndexElement {

   public enum IndexFields {
      id,
      mySqlIgnore,
      type,
      tablespace;
   }

   private String id;
   private String indexType;
   private final List<AppliesToClause> appliesToList;
   private boolean ignoreMySql = false;
   private String tablespace;

   public IndexElement(String id) {
      this.id = id;
      this.appliesToList = new ArrayList<AppliesToClause>();
      this.indexType = "";
      this.tablespace = "";
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getId() {
      return id;
   }

   public void addAppliesTo(String name, OrderType type) {
      appliesToList.add(new AppliesToClause(name, type));
   }

   public List<AppliesToClause> getAppliesToList() {
      return appliesToList;
   }

   @Override
   public String toString() {
      StringBuilder toReturn = new StringBuilder();
      toReturn.append(" Index: " + id);
      toReturn.append(getAppliesToAsString());
      return toReturn.toString();
   }

   public Element toXml(Document doc) {
      Element element = doc.createElement(TableElement.TableSections.Index.name());
      element.setAttribute(IndexFields.id.name(), id);
      for (AppliesToClause clause : appliesToList) {
         element.appendChild(clause.toXml(doc));
      }
      return element;
   }

   public String getAppliesToAsString() {
      StringBuilder toReturn = new StringBuilder();
      for (int index = 0; index < appliesToList.size(); index++) {
         toReturn.append("\n\t\tApplies to " + appliesToList.get(index));
      }
      return toReturn.toString();
   }

   public void setMySqlIgnore(boolean b) {
      this.ignoreMySql = b;
   }

   public void setIndexType(String indexType) {
      if (indexType != null) {
         this.indexType = indexType;
      }
   }

   public String getIndexType() {
      return indexType;
   }

   public boolean ignoreMySql() {
      return this.ignoreMySql;
   }

   public String getTablespace() {
      return Strings.isValid(tablespace) ? tablespace : "";
   }

   public void setTablespace(String tablespace) {
      this.tablespace = tablespace;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((appliesToList == null) ? 0 : appliesToList.hashCode());
      result = prime * result + ((id == null) ? 0 : id.hashCode());
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
      IndexElement other = (IndexElement) obj;
      if (appliesToList == null) {
         if (other.appliesToList != null) {
            return false;
         }
      } else if (!appliesToList.equals(other.appliesToList)) {
         return false;
      }
      if (id == null) {
         if (other.id != null) {
            return false;
         }
      } else if (!id.equals(other.id)) {
         return false;
      }
      return true;
   }

}