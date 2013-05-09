/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
