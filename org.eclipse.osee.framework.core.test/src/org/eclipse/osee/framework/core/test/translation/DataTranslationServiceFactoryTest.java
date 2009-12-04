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
package org.eclipse.osee.framework.core.test.translation;

import junit.framework.Assert;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.ITranslatorId;
import org.eclipse.osee.framework.core.test.mocks.MockOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.test.mocks.MockOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.translation.ArtifactTypeCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.translation.AttributeTypeCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.translation.BranchCacheStoreRequestTranslator;
import org.eclipse.osee.framework.core.translation.BranchCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.translation.BranchCommitRequestTranslator;
import org.eclipse.osee.framework.core.translation.BranchCommitResponseTranslator;
import org.eclipse.osee.framework.core.translation.BranchCreationRequestTranslator;
import org.eclipse.osee.framework.core.translation.BranchCreationResponseTranslator;
import org.eclipse.osee.framework.core.translation.CacheUpdateRequestTranslator;
import org.eclipse.osee.framework.core.translation.ChangeItemTranslator;
import org.eclipse.osee.framework.core.translation.ChangeReportRequestTranslator;
import org.eclipse.osee.framework.core.translation.ChangeReportResponseTranslator;
import org.eclipse.osee.framework.core.translation.ChangeVersionTranslator;
import org.eclipse.osee.framework.core.translation.DataTranslationServiceFactory;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.OseeEnumTypeCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.translation.OseeImportModelRequestTranslator;
import org.eclipse.osee.framework.core.translation.OseeImportModelResponseTranslator;
import org.eclipse.osee.framework.core.translation.RelationTypeCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.translation.TableDataTranslator;
import org.eclipse.osee.framework.core.translation.TransactionCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.translation.TransactionRecordTranslator;
import org.junit.Test;

/**
 * Test Case for {@link DataTranslationServiceFactory}
 * 
 * @author Roberto E. Escobar
 */
public class DataTranslationServiceFactoryTest {

   @Test
   public void testServiceCreation() throws OseeCoreException {
      IDataTranslationService srvc =
            new DataTranslationServiceFactory().createService(new MockOseeCachingServiceProvider(null),
                  new MockOseeModelFactoryServiceProvider(null));

      checkExists(srvc, TransactionRecordTranslator.class, CoreTranslatorId.TRANSACTION_RECORD);

      checkExists(srvc, BranchCreationRequestTranslator.class, CoreTranslatorId.BRANCH_CREATION_REQUEST);
      checkExists(srvc, BranchCreationResponseTranslator.class, CoreTranslatorId.BRANCH_CREATION_RESPONSE);

      checkExists(srvc, BranchCommitRequestTranslator.class, CoreTranslatorId.BRANCH_COMMIT_REQUEST);
      checkExists(srvc, BranchCommitResponseTranslator.class, CoreTranslatorId.BRANCH_COMMIT_RESPONSE);
      checkExists(srvc, ChangeVersionTranslator.class, CoreTranslatorId.CHANGE_VERSION);
      checkExists(srvc, ChangeItemTranslator.class, CoreTranslatorId.CHANGE_ITEM);
      checkExists(srvc, ChangeReportRequestTranslator.class, CoreTranslatorId.CHANGE_REPORT_REQUEST);
      checkExists(srvc, ChangeReportResponseTranslator.class, CoreTranslatorId.CHANGE_REPORT_RESPONSE);

      checkExists(srvc, CacheUpdateRequestTranslator.class, CoreTranslatorId.OSEE_CACHE_UPDATE_REQUEST);
      checkExists(srvc, ArtifactTypeCacheUpdateResponseTranslator.class,
            CoreTranslatorId.ARTIFACT_TYPE_CACHE_UPDATE_RESPONSE);
      checkExists(srvc, AttributeTypeCacheUpdateResponseTranslator.class,
            CoreTranslatorId.ATTRIBUTE_TYPE_CACHE_UPDATE_RESPONSE);
      checkExists(srvc, RelationTypeCacheUpdateResponseTranslator.class,
            CoreTranslatorId.RELATION_TYPE_CACHE_UPDATE_RESPONSE);
      checkExists(srvc, OseeEnumTypeCacheUpdateResponseTranslator.class,
            CoreTranslatorId.OSEE_ENUM_TYPE_CACHE_UPDATE_RESPONSE);
      checkExists(srvc, BranchCacheUpdateResponseTranslator.class, CoreTranslatorId.BRANCH_CACHE_UPDATE_RESPONSE);
      checkExists(srvc, BranchCacheStoreRequestTranslator.class, CoreTranslatorId.BRANCH_CACHE_STORE_REQUEST);
      checkExists(srvc, TransactionCacheUpdateResponseTranslator.class, CoreTranslatorId.TX_CACHE_UPDATE_RESPONSE);

      checkExists(srvc, OseeImportModelRequestTranslator.class, CoreTranslatorId.OSEE_IMPORT_MODEL_REQUEST);
      checkExists(srvc, OseeImportModelResponseTranslator.class, CoreTranslatorId.OSEE_IMPORT_MODEL_RESPONSE);
      checkExists(srvc, TableDataTranslator.class, CoreTranslatorId.TABLE_DATA);

      //      checkExists(srvc, BasicArtifactTranslator.class, CoreTranslatorId.ARTIFACT_METADATA);
      //      checkExists(srvc, BranchTranslator.class, CoreTranslatorId.BRANCH);
      //      checkExists(srvc, ArtifactTypeTranslator.class, CoreTranslatorId.ARTIFACT_TYPE);
      //      checkExists(srvc, AttributeTypeTranslator.class, CoreTranslatorId.ATTRIBUTE_TYPE);
      //      checkExists(srvc, RelationTypeTranslator.class, CoreTranslatorId.RELATION_TYPE);
      //      checkExists(srvc, OseeEnumTypeTranslator.class, CoreTranslatorId.OSEE_ENUM_TYPE);
      //      checkExists(srvc, OseeEnumEntryTranslator.class, CoreTranslatorId.OSEE_ENUM_ENTRY);
   }

   @SuppressWarnings("unchecked")
   private void checkExists(IDataTranslationService service, Class<? extends ITranslator> expected, ITranslatorId key) throws OseeCoreException {
      ITranslator<?> actual = service.getTranslator(key);
      Assert.assertNotNull(actual);
      Assert.assertEquals(expected, actual.getClass());
   }
}
