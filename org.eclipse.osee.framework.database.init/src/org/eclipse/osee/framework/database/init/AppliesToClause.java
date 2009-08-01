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

package org.eclipse.osee.framework.database.init;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class AppliesToClause implements Xmlizable {
   public static final String APPLIES_TO_TAG = "AppliesTo";

   public enum OrderType {
      Undefined, Ascending, Descending;
   }

   public enum AppliesToEntries {
      id, sort;
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

   public String toString() {
      return String.format("%s:[%s]\t%s:[%s]", AppliesToEntries.id.name(), columnName, AppliesToEntries.sort.name(),
            orderType);
   }

   @Override
   public boolean equals(Object otherObject) {
      if (otherObject instanceof AppliesToClause == false) {
         return false;
      }
      if (this == otherObject) {
         return true;
      }
      AppliesToClause that = (AppliesToClause) otherObject;
      return new EqualsBuilder().appendSuper(super.equals(otherObject)).append(this.columnName, that.getColumnName()).append(
            this.orderType, that.getOrderType()).isEquals();
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder(23, 37).append(columnName).append(orderType).toHashCode();
   }

   public Element toXml(Document doc) {
      Element element = doc.createElement(APPLIES_TO_TAG);
      element.setAttribute(AppliesToEntries.id.name(), columnName);
      if (!orderType.equals(OrderType.Undefined)) {
         element.setAttribute(AppliesToEntries.sort.name(), orderType.name());
      }
      return element;
   }
}
