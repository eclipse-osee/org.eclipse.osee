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

/**
 * @author Roberto E. Escobar
 */
public class MySqlDataType extends SqlDataType {

   public MySqlDataType() {
      super();
   }

   public String getBooleanType() {
      return "boolean";
   }

   public String getBitType() {
      return "smallint";
   }

   public String getIntegerType() {
      return "integer";
   }

   public String getDecimalType() {
      return "decimal";
   }

   public String getFloatType() {
      return "float";
   }

   public String getRealType() {
      return "real";
   }

   public String getDoubleType() {
      return getRealType();
   }

   public String getDateType() {
      return "date";
   }

   public String getCharType() {
      return "char";
   }

   public String getVarCharType() {
      return "varchar";
   }

   @Override
   public String getSmallIntType() {
      return "smallint";
   }

   @Override
   protected String getClobType() {
      return "blob";
   }

   @Override
   protected String getTimestamp() {
      return "timestamp";
   }

   @Override
   protected String getTime() {
      return "time";
   }

   @Override
   protected String getBlobType() {
      return "blob";
   }

   @Override
   protected String getBigInt() {
      return "bigint";
   }

   @Override
   protected String getLongVarCharType() {
      return "long varchar";
   }

   @Override
   protected String getNumericType() {
      return "numeric";
   }

}
