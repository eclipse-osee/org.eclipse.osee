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
package org.eclipse.osee.framework.database.core;

import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.internal.InternalActivator;

/**
 * @author Ryan D. Brooks
 */
public class SequenceManager {

   private SequenceManager() {
   }

   private static IOseeDatabaseService getDatabase() throws OseeDataStoreException {
      return InternalActivator.getInstance().getOseeDatabaseService();
   }

   private static IOseeSequence getSequence() throws OseeDataStoreException {
      return getDatabase().getSequence();
   }

   public static long getNextSequence(String sequenceName) throws OseeDataStoreException {
      return getSequence().getNextSequence(sequenceName);
   }

   public static int getNextSessionId() throws OseeDataStoreException {
      return getSequence().getNextSessionId();
   }

   public static int getNextTransactionId() throws OseeDataStoreException {
      return getSequence().getNextTransactionId();
   }

   public static int getNextArtifactId() throws OseeDataStoreException {
      return getSequence().getNextArtifactId();
   }

   public static int getNextOseeEnumTypeId() throws OseeDataStoreException {
      return getSequence().getNextOseeEnumTypeId();
   }

   public static int getNextGammaId() throws OseeDataStoreException {
      return getSequence().getNextGammaId();
   }

   public static int getNextArtifactTypeId() throws OseeDataStoreException {
      return getSequence().getNextArtifactTypeId();
   }

   public static int getNextAttributeBaseTypeId() throws OseeDataStoreException {
      return getSequence().getNextAttributeBaseTypeId();
   }

   public static int getNextAttributeProviderTypeId() throws OseeDataStoreException {
      return getSequence().getNextAttributeProviderTypeId();
   }

   public static int getNextAttributeId() throws OseeDataStoreException {
      return getSequence().getNextAttributeId();
   }

   public static int getNextAttributeTypeId() throws OseeDataStoreException {
      return getSequence().getNextAttributeTypeId();
   }

   public static int getNextFactoryId() throws OseeDataStoreException {
      return getSequence().getNextFactoryId();
   }

   public static int getNextBranchId() throws OseeDataStoreException {
      return getSequence().getNextBranchId();
   }

   public static int getNextRelationTypeId() throws OseeDataStoreException {
      return getSequence().getNextRelationTypeId();
   }

   public static int getNextRelationId() throws OseeDataStoreException {
      return getSequence().getNextRelationId();
   }

   public static int getNextImportId() throws OseeDataStoreException {
      return getSequence().getNextImportId();
   }

   public static int getNextImportMappedIndexId() throws OseeDataStoreException {
      return getSequence().getNextImportMappedIndexId();
   }
}