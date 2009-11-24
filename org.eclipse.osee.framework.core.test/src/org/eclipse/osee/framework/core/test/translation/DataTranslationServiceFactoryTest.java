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
import org.eclipse.osee.framework.core.enums.CoreTranslationIds;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.ITranslatorId;
import org.eclipse.osee.framework.core.test.mocks.MockOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.test.mocks.MockOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.translation.ArtifactTypeTranslator;
import org.eclipse.osee.framework.core.translation.AttributeTypeTranslator;
import org.eclipse.osee.framework.core.translation.BasicArtifactTranslator;
import org.eclipse.osee.framework.core.translation.BranchCommitRequestTranslator;
import org.eclipse.osee.framework.core.translation.BranchCommitResponseTranslator;
import org.eclipse.osee.framework.core.translation.BranchTranslator;
import org.eclipse.osee.framework.core.translation.CacheUpdateRequestTranslator;
import org.eclipse.osee.framework.core.translation.CacheUpdateResponseTranslator;
import org.eclipse.osee.framework.core.translation.ChangeItemTranslator;
import org.eclipse.osee.framework.core.translation.ChangeReportRequestTranslator;
import org.eclipse.osee.framework.core.translation.ChangeReportResponseTranslator;
import org.eclipse.osee.framework.core.translation.ChangeVersionTranslator;
import org.eclipse.osee.framework.core.translation.DataTranslationServiceFactory;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.OseeEnumEntryTranslator;
import org.eclipse.osee.framework.core.translation.OseeEnumTypeTranslator;
import org.eclipse.osee.framework.core.translation.RelationTypeTranslator;
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

      checkExists(srvc, BasicArtifactTranslator.class, CoreTranslationIds.ARTIFACT_METADATA);
      checkExists(srvc, BranchTranslator.class, CoreTranslationIds.BRANCH);
      checkExists(srvc, TransactionRecordTranslator.class, CoreTranslationIds.TRANSACTION_RECORD);
      checkExists(srvc, BranchCommitRequestTranslator.class, CoreTranslationIds.BRANCH_COMMIT_REQUEST);
      checkExists(srvc, BranchCommitResponseTranslator.class, CoreTranslationIds.BRANCH_COMMIT_RESPONSE);
      checkExists(srvc, ChangeVersionTranslator.class, CoreTranslationIds.CHANGE_VERSION);
      checkExists(srvc, ChangeItemTranslator.class, CoreTranslationIds.CHANGE_ITEM);
      checkExists(srvc, ChangeReportRequestTranslator.class, CoreTranslationIds.CHANGE_REPORT_REQUEST);
      checkExists(srvc, ChangeReportResponseTranslator.class, CoreTranslationIds.CHANGE_REPORT_RESPONSE);

      checkExists(srvc, CacheUpdateRequestTranslator.class, CoreTranslationIds.OSEE_CACHE_UPDATE_REQUEST);
      checkExists(srvc, CacheUpdateResponseTranslator.class,
            CoreTranslationIds.OSEE_CACHE_UPDATE_RESPONSE__ARTIFACT_TYPE);
      checkExists(srvc, CacheUpdateResponseTranslator.class,
            CoreTranslationIds.OSEE_CACHE_UPDATE_RESPONSE__ATTRIBUTE_TYPE);
      checkExists(srvc, CacheUpdateResponseTranslator.class,
            CoreTranslationIds.OSEE_CACHE_UPDATE_RESPONSE__RELATION_TYPE);
      checkExists(srvc, CacheUpdateResponseTranslator.class,
            CoreTranslationIds.OSEE_CACHE_UPDATE_RESPONSE__OSEE_ENUM_TYPE);
      checkExists(srvc, CacheUpdateResponseTranslator.class, CoreTranslationIds.OSEE_CACHE_UPDATE_RESPONSE__BRANCH);
      checkExists(srvc, CacheUpdateResponseTranslator.class,
            CoreTranslationIds.OSEE_CACHE_UPDATE_RESPONSE__TRANSACTION_RECORD);

      checkExists(srvc, ArtifactTypeTranslator.class, CoreTranslationIds.ARTIFACT_TYPE);
      checkExists(srvc, AttributeTypeTranslator.class, CoreTranslationIds.ATTRIBUTE_TYPE);
      checkExists(srvc, RelationTypeTranslator.class, CoreTranslationIds.RELATION_TYPE);
      checkExists(srvc, OseeEnumTypeTranslator.class, CoreTranslationIds.OSEE_ENUM_TYPE);
      checkExists(srvc, OseeEnumEntryTranslator.class, CoreTranslationIds.OSEE_ENUM_ENTRY);
   }

   @SuppressWarnings("unchecked")
   private void checkExists(IDataTranslationService service, Class<? extends ITranslator> expected, ITranslatorId key) throws OseeCoreException {
      ITranslator<?> actual = service.getTranslator(key);
      Assert.assertNotNull(actual);
      Assert.assertEquals(expected, actual.getClass());
   }
}
