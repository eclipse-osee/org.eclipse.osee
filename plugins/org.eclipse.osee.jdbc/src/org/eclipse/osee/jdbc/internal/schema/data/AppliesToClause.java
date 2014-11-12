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

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class AppliesToClause {
   public static final String APPLIES_TO_TAG = "AppliesTo";

   public enum OrderType {
      Undefined,
      Ascending,
      Descending;
   }

   public enum AppliesToEntries {
      id,
      sort;
   }

   private String columnName;
   private OrderType orderType;

   public AppliesToClause(String columnName, OrderType orderType) {
      this.columnName = columnName;
      this.orderType = orderType;
   }

   public String getColumnName() {
      return columnName;
   }

   public void setColumnName(String columnName) {
      this.columnName = columnName;
   }

   public OrderType getOrderType() {
      return orderType;
   }

   public void setOrderType(OrderType orderType) {
      this.orderType = orderType;
   }

   @Override
   public String toString() {
      return String.format("%s:[%s]\t%s:[%s]", AppliesToEntries.id.name(), columnName, AppliesToEntries.sort.name(),
         orderType);
   }

   public Element toXml(Document doc) {
      Element element = doc.createElement(APPLIES_TO_TAG);
      element.setAttribute(AppliesToEntries.id.name(), columnName);
      if (!orderType.equals(OrderType.Undefined)) {
         element.setAttribute(AppliesToEntries.sort.name(), orderType.name());
      }
      return element;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((columnName == null) ? 0 : columnName.hashCode());
      result = prime * result + ((orderType == null) ? 0 : orderType.hashCode());
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
      AppliesToClause other = (AppliesToClause) obj;
      if (columnName == null) {
         if (other.columnName != null) {
            return false;
         }
      } else if (!columnName.equals(other.columnName)) {
         return false;
      }
      if (orderType != other.orderType) {
         return false;
      }
      return true;
   }

}
