/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.data;

/**
 * @author Roberto E. Escobar
 */
public class OseeImportModelRequest {

   private final String model;
   private final String modelName;
   private final boolean isPersistAllowed;
   private final boolean createTypeChangeReport;
   private final boolean createCompareReport;

   public OseeImportModelRequest(String modelName, String model, boolean createTypeChangeReport, boolean createCompareReport, boolean isPersistAllowed) {
      this.modelName = modelName;
      this.model = model;
      this.isPersistAllowed = isPersistAllowed;
      this.createCompareReport = createCompareReport;
      this.createTypeChangeReport = createTypeChangeReport;
   }

   public String getModel() {
      return model;
   }

   public boolean isCreateTypeChangeReport() {
      return createTypeChangeReport;
   }

   public boolean isCreateCompareReport() {
      return createCompareReport;
   }

   public boolean isPersistAllowed() {
      return isPersistAllowed;
   }

   public String getModelName() {
      return modelName;
   }

}
