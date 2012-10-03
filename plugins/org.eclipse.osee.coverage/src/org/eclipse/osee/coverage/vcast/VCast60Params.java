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
public class VCast60Params {

   private final String vCastDir;
   private final String namespace;
   private final boolean resolveExceptionHandling;
   private final String dbPath;

   public VCast60Params(String vCastDir, String namespace, boolean resolveExceptionHandling, String dbPath) {
      this.vCastDir = vCastDir;
      this.namespace = namespace;
      this.resolveExceptionHandling = resolveExceptionHandling;
      this.dbPath = dbPath;
   }

   public String getVCastDirectory() {
      return vCastDir;
   }

   public String getNamespace() {
      return namespace;
   }

   public String getVCastDbPath() {
      return dbPath;
   }

   /**
    * true if importer should automatically set known exception handling cases
    */
   public boolean isResolveExceptionHandling() {
      return resolveExceptionHandling;
   }

}
