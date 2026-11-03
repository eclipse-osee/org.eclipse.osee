package org.eclipse.osee.framework.ui.skynet.widgets.xx.util;

/*********************************************************************
 * Copyright (c) 2026 Boeing This program and the accompanying materials are made available under the terms of the
 * Eclipse Public License 2.0 which is available at https://www.eclipse.org/legal/epl-2.0/ SPDX-License-Identifier:
 * EPL-2.0 Contributors: Boeing - initial API and implementation
 **********************************************************************/

public class XXListItem {
   private String name = "";
   private String xmlValue = null;
   private Object data = null;
   private boolean selected = false;

   public XXListItem(String name) {
      this.name = name;
   }

   @Override
   public String toString() {
      return name;
   }

   public Object getData() {
      return data;
   }

   public void setData(Object data) {
      this.data = data;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public boolean isSelected() {
      return selected;
   }

   public void setSelected(boolean selected) {
      this.selected = selected;
   }

   public String getXmlValue() {
      return xmlValue;
   }

   public void setXmlValue(String xmlValue) {
      this.xmlValue = xmlValue;
   }
}
