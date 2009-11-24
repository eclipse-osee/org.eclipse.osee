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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.enums.CoreTranslationIds;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.model.OseeEnumType;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.test.mocks.MockOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.translation.DataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.OseeEnumEntryTranslator;
import org.eclipse.osee.framework.core.translation.OseeEnumTypeTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link OseeEnumTypeTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class OseeEnumTypeTranslatorTest extends BaseTranslatorTest<OseeEnumType> {

   public OseeEnumTypeTranslatorTest(OseeEnumType data, ITranslator<OseeEnumType> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(OseeEnumType expected, OseeEnumType actual) throws OseeCoreException {
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      IOseeModelFactoryServiceProvider provider = MockDataFactory.createFactoryProvider();
      IDataTranslationService service = new DataTranslationService();

      ITranslator<OseeEnumType> translator = new OseeEnumTypeTranslator(service, provider);

      service.addTranslator(new OseeEnumEntryTranslator(provider), CoreTranslationIds.OSEE_ENUM_ENTRY);

      List<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 3; index++) {
         OseeEnumType type = MockDataFactory.createEnumType(index);
         for (int j = 0; j < index * 3; j++) {
            OseeEnumEntry entry = MockDataFactory.createEnumEntry(index * j);
            type.addEntry(entry);
         }
         data.add(new Object[] {type, translator});
      }
      return data;
   }

   public static IOseeCachingServiceProvider createProvider(BranchCache cache) {
      IOseeCachingService service = new OseeCachingService(cache, null, null, null, null, null);
      return new MockOseeCachingServiceProvider(service);
   }
}
