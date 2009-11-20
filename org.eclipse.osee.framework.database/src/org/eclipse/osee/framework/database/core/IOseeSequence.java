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

/**
 * @author Roberto E. Escobar
 */
public interface IOseeSequence {

   static final String ART_ID_SEQ = "SKYNET_ART_ID_SEQ";
   static final String ENUM_TYPE_ID_SEQ = "SKYNET_ENUM_TYPE_ID_SEQ";
   static final String ART_TYPE_ID_SEQ = "SKYNET_ART_TYPE_ID_SEQ";
   static final String ATTR_BASE_TYPE_ID_SEQ = "SKYNET_ATTR_BASE_TYPE_ID_SEQ";
   static final String ATTR_PROVIDER_TYPE_ID_SEQ = "SKYNET_ATTR_PROVIDER_TYPE_ID_SEQ";
   static final String ATTR_ID_SEQ = "SKYNET_ATTR_ID_SEQ";
   static final String ATTR_TYPE_ID_SEQ = "SKYNET_ATTR_TYPE_ID_SEQ";
   static final String FACTORY_ID_SEQ = "SKYNET_FACTORY_ID_SEQ";
   static final String BRANCH_ID_SEQ = "SKYNET_BRANCH_ID_SEQ";
   static final String REL_LINK_TYPE_ID_SEQ = "SKYNET_REL_LINK_TYPE_ID_SEQ";
   static final String REL_LINK_ID_SEQ = "SKYNET_REL_LINK_ID_SEQ";
   static final String GAMMA_ID_SEQ = "SKYNET_GAMMA_ID_SEQ";
   static final String TRANSACTION_ID_SEQ = "SKYNET_TRANSACTION_ID_SEQ";
   static final String IMPORT_ID_SEQ = "SKYNET_IMPORT_ID_SEQ";
   static final String IMPORT_MAPPED_INDEX_SEQ = "SKYNET_IMPORT_MAPPED_INDEX_SEQ";
   static final String TTE_SESSION_SEQ = "TTE_SESSION_SEQ";

   long getNextSequence(String sequenceName) throws OseeDataStoreException;

   int getNextSessionId() throws OseeDataStoreException;

   int getNextTransactionId() throws OseeDataStoreException;

   int getNextArtifactId() throws OseeDataStoreException;

   int getNextOseeEnumTypeId() throws OseeDataStoreException;

   int getNextGammaId() throws OseeDataStoreException;

   int getNextArtifactTypeId() throws OseeDataStoreException;

   int getNextAttributeBaseTypeId() throws OseeDataStoreException;

   int getNextAttributeProviderTypeId() throws OseeDataStoreException;

   int getNextAttributeId() throws OseeDataStoreException;

   int getNextAttributeTypeId() throws OseeDataStoreException;

   int getNextFactoryId() throws OseeDataStoreException;

   int getNextBranchId() throws OseeDataStoreException;

   int getNextRelationTypeId() throws OseeDataStoreException;

   int getNextRelationId() throws OseeDataStoreException;

   int getNextImportId() throws OseeDataStoreException;

   int getNextImportMappedIndexId() throws OseeDataStoreException;
}
