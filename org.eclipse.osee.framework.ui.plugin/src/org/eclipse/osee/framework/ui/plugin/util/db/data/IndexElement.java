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
import org.eclipse.osee.framework.ui.plugin.util.db.data.AppliesToClause.OrderType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class IndexElement implements Xmlizable {

   public enum IndexFields {
      id, mySqlIgnore, type
   }

   private String id;
   private String indexType;
   private List<AppliesToClause> appliesToList;
   private boolean ignoreMySql = false;

   public IndexElement(String id) {
      this.id = id;
      this.appliesToList = new ArrayList<AppliesToClause>();
      this.indexType = "";
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

   public String toString() {
      StringBuilder toReturn = new StringBuilder();
      toReturn.append(" Index: " + id);
      toReturn.append(getAppliesToAsString());
      return toReturn.toString();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jdk.core.persistence.Xmlizable#toXml()
    */
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

   @Override
   public boolean equals(Object otherObject) {
      if (otherObject instanceof IndexElement == false) {
         return false;
      }
      if (this == otherObject) {
         return true;
      }
      IndexElement that = (IndexElement) otherObject;
      return new EqualsBuilder().appendSuper(super.equals(otherObject)).append(this.appliesToList,
            that.getAppliesToList()).append(this.id, that.getId()).isEquals();
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder(113, 67).append(id).append(appliesToList).toHashCode();
   }

   /**
    * @param b
    */
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
}