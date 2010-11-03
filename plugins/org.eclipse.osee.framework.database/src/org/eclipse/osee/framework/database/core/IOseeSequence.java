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

import org.eclipse.osee.framework.core.exception.OseeCoreException;

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

   long getNextSequence(String sequenceName) throws OseeCoreException;

   int getNextSessionId() throws OseeCoreException;

   int getNextTransactionId() throws OseeCoreException;

   int getNextArtifactId() throws OseeCoreException;

   int getNextOseeEnumTypeId() throws OseeCoreException;

   int getNextGammaId() throws OseeCoreException;

   int getNextArtifactTypeId() throws OseeCoreException;

   int getNextAttributeBaseTypeId() throws OseeCoreException;

   int getNextAttributeProviderTypeId() throws OseeCoreException;

   int getNextAttributeId() throws OseeCoreException;

   int getNextAttributeTypeId() throws OseeCoreException;

   int getNextFactoryId() throws OseeCoreException;

   int getNextBranchId() throws OseeCoreException;

   int getNextRelationTypeId() throws OseeCoreException;

   int getNextRelationId() throws OseeCoreException;

   int getNextImportId() throws OseeCoreException;

   int getNextImportMappedIndexId() throws OseeCoreException;

   void clear();
}
