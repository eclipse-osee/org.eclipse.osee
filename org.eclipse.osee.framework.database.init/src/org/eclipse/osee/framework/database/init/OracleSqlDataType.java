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

import org.eclipse.osee.framework.database.core.SQL3DataType;

/**
 * @author Roberto E. Escobar
 */
public class OracleSqlDataType extends SqlDataType {

   public OracleSqlDataType() {
      super();
   }

   @Override
   public String getLimit(SQL3DataType dataType, String limit) {
      if (dataType == SQL3DataType.VARCHAR && limit != null && !limit.equals("")) {
         return limit + " CHAR";
      } else {
         return super.getLimit(dataType, limit);
      }
   }

   @Override
   public String getBooleanType() {
      return "number";
   }

   @Override
   public String getBitType() {
      return "number";
   }

   @Override
   public String getIntegerType() {
      return "integer";
   }

   @Override
   public String getDecimalType() {
      return "number";
   }

   @Override
   public String getFloatType() {
      return "float";
   }

   @Override
   public String getRealType() {
      return "real";
   }

   @Override
   public String getDoubleType() {
      return "number";
   }

   @Override
   public String getDateType() {
      return "date";
   }

   @Override
   public String getCharType() {
      return "char";
   }

   @Override
   public String getVarCharType() {
      return "varchar2";
   }

   @Override
   public String getSmallIntType() {
      return "smallint";
   }

   @Override
   protected String getClobType() {
      return "clob";
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
      return "number";
   }

   @Override
   protected String getLongVarCharType() {
      return "varchar2";
   }

   @Override
   protected String getNumericType() {
      return "number";
   }

}
