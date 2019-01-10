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
package org.eclipse.osee.define.rest.importing.parsers;

import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 * @author David W. Miller
 */
public class DoorsTableRow {
   private final Elements elements = new Elements();
   private RowType type = RowType.SINGLE;
   private DoorsDataType dataType = DoorsDataType.NOT_DEFINED;

   public static enum RowType {
      SINGLE,
      MULTI_START,
      MULTI_MID,
      MULTI_END,
      FIRST_ROW
   }

   public void fill(Node row) {
      Conditions.checkNotNull(row, "Row data");
      for (Node n : row.childNodes()) {
         if (n instanceof Element) {
            elements.add((Element) n);
         }
      }
   }

   public Elements getRows() {
      return elements;
   }

   public Element getElement(int index) {
      return elements.get(index);
   }

   public void setRowType(RowType type) {
      Conditions.checkNotNull(type, "Row Type");
      this.type = type;
   }

   public RowType getType() {
      return type;
   }

   public boolean isMainRow() {
      boolean toReturn = false;
      if (type == RowType.SINGLE || type == RowType.MULTI_START) {
         toReturn = true;
      }
      return toReturn;
   }

   public DoorsDataType getDataType() {
      return dataType;
   }

   public void setDataType(DoorsDataType dataType) {
      Conditions.checkNotNull(dataType, "Data Type");
      this.dataType = dataType;
   }
}
