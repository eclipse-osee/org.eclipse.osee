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
package org.eclipse.osee.framework.core.translation;

import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.services.ITranslatorId;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class DataTranslationServiceFactory {

   public DataTranslationServiceFactory() {
   }

   public IDataTranslationService createService(IOseeCachingServiceProvider cachingService, IOseeModelFactoryServiceProvider factoryProvider) throws OseeCoreException {
      IDataTranslationService service = new DataTranslationService();

      service.addTranslator(new BasicArtifactTranslator(), CoreTranslatorId.ARTIFACT_METADATA);
      service.addTranslator(new BranchTranslator(service, factoryProvider), CoreTranslatorId.BRANCH);
      service.addTranslator(new TransactionRecordTranslator(factoryProvider), CoreTranslatorId.TRANSACTION_RECORD);
      service.addTranslator(new ArtifactTypeTranslator(service, factoryProvider), CoreTranslatorId.ARTIFACT_TYPE);
      service.addTranslator(new AttributeTypeTranslator(service, factoryProvider), CoreTranslatorId.ATTRIBUTE_TYPE);
      service.addTranslator(new RelationTypeTranslator(service, factoryProvider), CoreTranslatorId.RELATION_TYPE);

      service.addTranslator(new OseeEnumEntryTranslator(factoryProvider), CoreTranslatorId.OSEE_ENUM_ENTRY);
      service.addTranslator(new OseeEnumTypeTranslator(service, factoryProvider), CoreTranslatorId.OSEE_ENUM_TYPE);

      service.addTranslator(new BranchCommitRequestTranslator(service), CoreTranslatorId.BRANCH_COMMIT_REQUEST);
      service.addTranslator(new BranchCommitResponseTranslator(service), CoreTranslatorId.BRANCH_COMMIT_RESPONSE);

      service.addTranslator(new ChangeVersionTranslator(), CoreTranslatorId.CHANGE_VERSION);
      service.addTranslator(new ChangeItemTranslator(service), CoreTranslatorId.CHANGE_ITEM);
      service.addTranslator(new ChangeReportRequestTranslator(service), CoreTranslatorId.CHANGE_REPORT_REQUEST);
      service.addTranslator(new ChangeReportResponseTranslator(service), CoreTranslatorId.CHANGE_REPORT_RESPONSE);

      service.addTranslator(new CacheUpdateRequestTranslator(), CoreTranslatorId.OSEE_CACHE_UPDATE_REQUEST);

      createCacheUpdateTx(service, ArtifactType.class, CoreTranslatorId.ARTIFACT_TYPE,
            CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__ARTIFACT_TYPE);

      createCacheUpdateTx(service, AttributeType.class, CoreTranslatorId.ATTRIBUTE_TYPE,
            CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__ATTRIBUTE_TYPE);

      createCacheUpdateTx(service, RelationType.class, CoreTranslatorId.RELATION_TYPE,
            CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__RELATION_TYPE);

      createCacheUpdateTx(service, OseeEnumType.class, CoreTranslatorId.OSEE_ENUM_TYPE,
            CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__OSEE_ENUM_TYPE);

      createCacheUpdateTx(service, Branch.class, CoreTranslatorId.BRANCH,
            CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__BRANCH);

      createCacheUpdateTx(service, TransactionRecord.class, CoreTranslatorId.TRANSACTION_RECORD,
            CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__TRANSACTION_RECORD);
      return service;
   }

   private <T> void createCacheUpdateTx(IDataTranslationService service, Class<T> clazz, ITranslatorId internalObject, ITranslatorId translatorId) throws OseeCoreException {
      service.addTranslator(new CacheUpdateResponseTranslator<T>(service, internalObject), translatorId);
   }
}
