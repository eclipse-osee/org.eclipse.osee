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
package org.eclipse.osee.framework.core.message.test.translation;

import java.util.Collection;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.message.internal.DataTranslationService;
import org.eclipse.osee.framework.core.message.internal.DataTranslationServiceFactory;
import org.eclipse.osee.framework.core.message.internal.translation.ArtifactTypeCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.AttributeTypeCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.BranchCacheStoreRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.BranchCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.BranchCommitRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.BranchCommitResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.BranchCreationRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.BranchCreationResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.CacheUpdateRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.ChangeReportRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.ChangeReportResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.DatastoreInitRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.OseeEnumTypeCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.OseeImportModelRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.OseeImportModelResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.RelationTypeCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.SearchRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.SearchResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.TableDataTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.TransactionCacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.TransactionRecordTranslator;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.cache.BranchCache;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.model.type.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.type.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.model.type.RelationTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
import org.eclipse.osee.framework.core.services.TempCachingService;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.ITranslatorId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link DataTranslationServiceFactory}
 * 
 * @author Roberto E. Escobar
 */
public class DataTranslationServiceFactoryTest {

   @Test
   public void testServiceCreation() throws OseeCoreException {
      DataTranslationService srvc = new DataTranslationService();
      srvc.setModelFactory(new MockModelFactoryService());
      srvc.setTempCachingService(new MockOseeCachingService());
      srvc.start();

      checkExists(srvc, TransactionRecordTranslator.class, CoreTranslatorId.TRANSACTION_RECORD);

      checkExists(srvc, BranchCreationRequestTranslator.class, CoreTranslatorId.BRANCH_CREATION_REQUEST);
      checkExists(srvc, BranchCreationResponseTranslator.class, CoreTranslatorId.BRANCH_CREATION_RESPONSE);

      checkExists(srvc, BranchCommitRequestTranslator.class, CoreTranslatorId.BRANCH_COMMIT_REQUEST);
      checkExists(srvc, BranchCommitResponseTranslator.class, CoreTranslatorId.BRANCH_COMMIT_RESPONSE);

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

      checkExists(srvc, DatastoreInitRequestTranslator.class, CoreTranslatorId.OSEE_DATASTORE_INIT_REQUEST);

      checkExists(srvc, SearchRequestTranslator.class, CoreTranslatorId.SEARCH_REQUEST);
      checkExists(srvc, SearchResponseTranslator.class, CoreTranslatorId.SEARCH_RESPONSE);

      srvc.stop();
   }

   private void checkExists(IDataTranslationService service, Class<? extends ITranslator<?>> expected, ITranslatorId key) throws OseeCoreException {
      ITranslator<?> actual = service.getTranslator(key);
      Assert.assertNotNull(actual);
      Assert.assertEquals(expected, actual.getClass());
   }

   private final class MockModelFactoryService implements IOseeModelFactoryService {

      @Override
      public TransactionRecordFactory getTransactionFactory() {
         return null;
      }

      @Override
      public RelationTypeFactory getRelationTypeFactory() {
         return null;
      }

      @Override
      public OseeEnumTypeFactory getOseeEnumTypeFactory() {
         return null;
      }

      @Override
      public BranchFactory getBranchFactory() {
         return null;
      }

      @Override
      public AttributeTypeFactory getAttributeTypeFactory() {
         return null;
      }

      @Override
      public ArtifactTypeFactory getArtifactTypeFactory() {
         return null;
      }
   }

   private final class MockOseeCachingService implements TempCachingService {

      @Override
      public BranchCache getBranchCache() {
         return null;
      }

      @Override
      public TransactionCache getTransactionCache() {
         return null;
      }

      @Override
      public Collection<?> getCaches() {
         return null;
      }

      @Override
      public IOseeCache<?, ?> getCache(OseeCacheEnum cacheId) {
         return null;
      }

      @Override
      public void reloadAll() {
         //
      }

      @Override
      public void clearAll() {
         //
      }

   }
}
