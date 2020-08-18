/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.jdk.core.result.table;

/**
 * @author Donald G. Dunne
 */
public class XResultTableColumn {

   public String id;
   public String name = "";
   public int width;
   public XResultTableDataType type;

   public XResultTableColumn() {
   }

   public XResultTableColumn(String name, String id, int width, XResultTableDataType type) {
      this.name = name;
      this.id = id;
      this.width = width;
      this.type = type;
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public int getWidth() {
      return width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public XResultTableDataType getType() {
      return type;
   }

   public void setType(XResultTableDataType type) {
      this.type = type;
   }

}
