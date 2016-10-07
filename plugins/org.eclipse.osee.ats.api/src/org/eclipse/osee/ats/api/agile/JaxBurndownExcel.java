/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.agile;

/**
 * @author Donald G. Dunne
 */
public class JaxBurndownExcel {

   Long excelSheetUuid;
   Long excelQueryUuid;
   String error;

   public Long getExcelSheetUuid() {
      return excelSheetUuid;
   }

   public void setExcelSheetUuid(Long excelSheetUuid) {
      this.excelSheetUuid = excelSheetUuid;
   }

   public Long getExcelQueryUuid() {
      return excelQueryUuid;
   }

   public void setExcelQueryUuid(Long excelQueryUuid) {
      this.excelQueryUuid = excelQueryUuid;
   }

   public String getError() {
      return error;
   }

   public void setError(String error) {
      this.error = error;
   }

}
