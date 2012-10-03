/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.vcast;

/**
 * @author Roberto E. Escobar
 */
public abstract class CoverageImportData {

   private final String vCastDir;
   private final boolean resolveExceptionHandling;

   public CoverageImportData(String vCastDir, boolean resolveExceptionHandling) {
      this.vCastDir = vCastDir;
      this.resolveExceptionHandling = resolveExceptionHandling;
   }

   public String getVCastDirectory() {
      return vCastDir;
   }

   /**
    * true if importer should automatically set known exception handling cases
    */
   public boolean isResolveExceptionHandling() {
      return resolveExceptionHandling;
   }

   public abstract String getFileNamespace(String filename);

}
