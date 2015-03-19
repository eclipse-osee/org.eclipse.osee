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

import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.internal.DataTranslationService;
import org.eclipse.osee.framework.core.message.internal.DataTranslationServiceFactory;
import org.eclipse.osee.framework.core.message.internal.translation.DatastoreInitRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.OseeImportModelRequestTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.OseeImportModelResponseTranslator;
import org.eclipse.osee.framework.core.message.internal.translation.TableDataTranslator;
import org.eclipse.osee.framework.core.model.BranchFactory;
import org.eclipse.osee.framework.core.model.TransactionRecordFactory;
import org.eclipse.osee.framework.core.model.type.ArtifactTypeFactory;
import org.eclipse.osee.framework.core.model.type.AttributeTypeFactory;
import org.eclipse.osee.framework.core.model.type.OseeEnumTypeFactory;
import org.eclipse.osee.framework.core.model.type.RelationTypeFactory;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryService;
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
      srvc.start();

      checkExists(srvc, OseeImportModelRequestTranslator.class, CoreTranslatorId.OSEE_IMPORT_MODEL_REQUEST);
      checkExists(srvc, OseeImportModelResponseTranslator.class, CoreTranslatorId.OSEE_IMPORT_MODEL_RESPONSE);
      checkExists(srvc, TableDataTranslator.class, CoreTranslatorId.TABLE_DATA);

      checkExists(srvc, DatastoreInitRequestTranslator.class, CoreTranslatorId.OSEE_DATASTORE_INIT_REQUEST);

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

}
