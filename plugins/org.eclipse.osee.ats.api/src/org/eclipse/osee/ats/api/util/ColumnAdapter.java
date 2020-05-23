/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.api.util;

/**
 * @author Donald G. Dunne
 */
public class ColumnAdapter implements IColumn {

   private String id;
   private String name;
   private ColumnType dataType;
   private String description;

   public ColumnAdapter(String id, String name, ColumnType dataType, String description) {
      this.id = id;
      this.name = name;
      this.dataType = dataType;
      this.description = description;
   }

   @Override
   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   @Override
   public String getName() {
      return name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public ColumnType getDataType() {
      return dataType;
   }

   public void setDataType(ColumnType dataType) {
      this.dataType = dataType;
   }

   @Override
   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }

}
