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
package org.eclipse.osee.framework.core.test.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.CacheUpdateResponse;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
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
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.translation.CacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.translation.DataTranslationServiceFactory;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link CacheUpdateResponseTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class CacheUpdateResponseTranslatorTest extends BaseTranslatorTest<CacheUpdateResponse<?>> {

   public CacheUpdateResponseTranslatorTest(CacheUpdateResponse<?> data, ITranslator<CacheUpdateResponse<?>> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(CacheUpdateResponse<?> expected, CacheUpdateResponse<?> actual) throws OseeCoreException {
      Assert.assertEquals(expected.getCacheId(), actual.getCacheId());
      List<Object> expItems = new ArrayList<Object>(expected.getItems());
      List<Object> actItems = new ArrayList<Object>(actual.getItems());

      Assert.assertEquals(expItems.size(), actItems.size());
      for (int index = 0; index < expItems.size(); index++) {
         Object expObject = expItems.get(index);
         Object actObject = actItems.get(index);

         Assert.assertNotSame(expObject, actObject);
         if (expObject instanceof ArtifactType) {
            DataAsserts.assertEquals((ArtifactType) expObject, (ArtifactType) actObject);
         } else if (expObject instanceof AttributeType) {
            DataAsserts.assertEquals((AttributeType) expObject, (AttributeType) actObject);
         } else if (expObject instanceof RelationType) {
            DataAsserts.assertEquals((RelationType) expObject, (RelationType) actObject);
         } else if (expObject instanceof TransactionRecord) {
            DataAsserts.assertEquals((TransactionRecord) expObject, (TransactionRecord) actObject);
         } else if (expObject instanceof Branch) {
            DataAsserts.assertEquals((Branch) expObject, (Branch) actObject);
         } else if (expObject instanceof OseeEnumType) {
            DataAsserts.assertEquals((OseeEnumType) expObject, (OseeEnumType) actObject);
         } else {
            Assert.fail("Unable to check object" + actObject.getClass());
         }
      }
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      IOseeModelFactoryServiceProvider factoryProvider = MockDataFactory.createFactoryProvider();
      IOseeCachingServiceProvider cachingServiceProvider = MockDataFactory.createCachingProvider();
      IDataTranslationService service =
            new DataTranslationServiceFactory().createService(cachingServiceProvider, factoryProvider);

      List<Object[]> data = new ArrayList<Object[]>();

      data.add(createTest(service, CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__BRANCH, OseeCacheEnum.BRANCH_CACHE,
            createBranches(4)));

      data.add(createTest(service, CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__TRANSACTION_RECORD,
            OseeCacheEnum.TRANSACTION_CACHE, createTransactions(3)));

      data.add(createTest(service, CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__ARTIFACT_TYPE,
            OseeCacheEnum.ARTIFACT_TYPE_CACHE, createArtifactTypes(3)));

      data.add(createTest(service, CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__ATTRIBUTE_TYPE,
            OseeCacheEnum.ATTRIBUTE_TYPE_CACHE, createAttributeTypes(3)));

      data.add(createTest(service, CoreTranslatorId.OSEE_CACHE_UPDATE_RESPONSE__RELATION_TYPE,
            OseeCacheEnum.RELATION_TYPE_CACHE, createRelationTypes(3)));

      return data;
   }

   private static List<TransactionRecord> createTransactions(int count) {
      List<TransactionRecord> txs = new ArrayList<TransactionRecord>();
      for (int index = 0; index < count; index++) {
         txs.add(MockDataFactory.createTransaction(index, index * 7));
      }
      return txs;
   }

   private static List<ArtifactType> createArtifactTypes(int count) throws OseeCoreException {
      ArtifactType base = MockDataFactory.createBaseArtifactType();
      List<ArtifactType> txs = new ArrayList<ArtifactType>();
      for (int index = 0; index < count; index++) {
         ArtifactType type = MockDataFactory.createArtifactType(index);
         type.setSuperType(Collections.singleton(base));
         txs.add(type);
      }
      return txs;
   }

   private static List<AttributeType> createAttributeTypes(int count) throws OseeCoreException {
      List<AttributeType> txs = new ArrayList<AttributeType>();
      for (int index = 0; index < count; index++) {
         OseeEnumType type = MockDataFactory.createEnumType(index * 3);
         txs.add(MockDataFactory.createAttributeType(index, type));
      }
      return txs;
   }

   private static List<Branch> createBranches(int count) throws OseeCoreException {
      List<Branch> txs = new ArrayList<Branch>();
      for (int index = 0; index < count; index++) {
         txs.add(MockDataFactory.createBranch(index));
      }
      return txs;
   }

   private static List<RelationType> createRelationTypes(int count) throws OseeCoreException {
      ArtifactType base = MockDataFactory.createBaseArtifactType();

      List<RelationType> txs = new ArrayList<RelationType>();
      for (int index = 0; index < count; index++) {
         ArtifactType typeA = MockDataFactory.createArtifactType(index * 3);
         typeA.setSuperType(Collections.singleton(base));

         ArtifactType typeB = MockDataFactory.createArtifactType(index * 7);
         typeB.setSuperType(Collections.singleton(base));

         txs.add(MockDataFactory.createRelationType(index, typeA, typeB));
      }
      return txs;
   }

   private static <T> Object[] createTest(IDataTranslationService txService, ITranslatorId toMatch, OseeCacheEnum cacheId, Collection<T> items) throws OseeCoreException {
      return new Object[] {new CacheUpdateResponse<T>(cacheId, items), txService.getTranslator(toMatch)};
   }
}
