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

import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.data.BranchCommitResponse;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.data.CacheUpdateResponse;
import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeReportRequest;
import org.eclipse.osee.framework.core.data.ChangeReportResponse;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;

/**
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
public class DataTranslationServiceFactory {

   public DataTranslationServiceFactory() {
   }

   public IDataTranslationService createService(IOseeCachingServiceProvider cachingService) throws OseeCoreException {
      IDataTranslationService service = new DataTranslationService();

      service.addTranslator(new BasicArtifactTranslator(), IBasicArtifact.class);
      service.addTranslator(new BranchTranslator(cachingService), Branch.class);
      service.addTranslator(new TransactionRecordTranslator(service), TransactionRecord.class);

      service.addTranslator(new BranchCommitRequestTranslator(service), BranchCommitRequest.class);
      service.addTranslator(new BranchCommitResponseTranslator(service), BranchCommitResponse.class);

      service.addTranslator(new ChangeVersionTranslator(), ChangeVersion.class);
      service.addTranslator(new ChangeItemTranslator(service), ChangeItem.class);
      service.addTranslator(new ChangeReportResponseTranslator(service), ChangeReportResponse.class);
      service.addTranslator(new ChangeReportRequestTranslator(service), ChangeReportRequest.class);

      service.addTranslator(new CacheUpdateRequestTranslator(), CacheUpdateRequest.class);

      createCacheUpdateTx(service, ArtifactType.class);
      createCacheUpdateTx(service, AttributeType.class);
      createCacheUpdateTx(service, RelationType.class);
      createCacheUpdateTx(service, OseeEnumType.class);

      createCacheUpdateTx(service, Branch.class);
      createCacheUpdateTx(service, TransactionRecord.class);
      return service;
   }

   private <T> void createCacheUpdateTx(IDataTranslationService service, Class<T> clazz) throws OseeCoreException {
      service.addTranslator(new CacheUpdateResponseTranslator<T>(service, clazz), CacheUpdateResponse.class, clazz);
   }
}
