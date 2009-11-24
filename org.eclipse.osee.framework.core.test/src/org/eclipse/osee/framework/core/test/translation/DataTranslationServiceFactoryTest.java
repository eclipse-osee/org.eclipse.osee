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
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
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
      IDataTranslationService service =
            new DataTranslationServiceFactory().createService(new MockOseeCachingServiceProvider(null),
                  new MockOseeModelFactoryServiceProvider(null));

      checkExists(service, BasicArtifactTranslator.class, IBasicArtifact.class);
      checkExists(service, BranchTranslator.class, Branch.class);
      checkExists(service, TransactionRecordTranslator.class, TransactionRecord.class);
      checkExists(service, BranchCommitRequestTranslator.class, BranchCommitRequest.class);
      checkExists(service, BranchCommitResponseTranslator.class, BranchCommitResponse.class);
      checkExists(service, ChangeVersionTranslator.class, ChangeVersion.class);
      checkExists(service, ChangeItemTranslator.class, ChangeItem.class);
      checkExists(service, ChangeReportResponseTranslator.class, ChangeReportResponse.class);
      checkExists(service, ChangeReportRequestTranslator.class, ChangeReportRequest.class);
      checkExists(service, CacheUpdateRequestTranslator.class, CacheUpdateRequest.class);
      checkExists(service, CacheUpdateResponseTranslator.class, CacheUpdateResponse.class, ArtifactType.class);
      checkExists(service, CacheUpdateResponseTranslator.class, CacheUpdateResponse.class, AttributeType.class);
      checkExists(service, CacheUpdateResponseTranslator.class, CacheUpdateResponse.class, RelationType.class);
      checkExists(service, CacheUpdateResponseTranslator.class, CacheUpdateResponse.class, OseeEnumType.class);
      checkExists(service, CacheUpdateResponseTranslator.class, CacheUpdateResponse.class, Branch.class);
      checkExists(service, CacheUpdateResponseTranslator.class, CacheUpdateResponse.class, TransactionRecord.class);

      checkExists(service, ArtifactTypeTranslator.class, ArtifactType.class);
      checkExists(service, AttributeTypeTranslator.class, AttributeType.class);
      checkExists(service, RelationTypeTranslator.class, RelationType.class);
      checkExists(service, OseeEnumTypeTranslator.class, OseeEnumType.class);
      checkExists(service, OseeEnumEntryTranslator.class, OseeEnumEntry.class);
   }

   @SuppressWarnings("unchecked")
   private void checkExists(IDataTranslationService service, Class<? extends ITranslator> expected, Class<?>... key) throws OseeCoreException {
      ITranslator<?> actual = service.getTranslator(key);
      Assert.assertNotNull(actual);
      Assert.assertEquals(expected, actual.getClass());
   }
}
