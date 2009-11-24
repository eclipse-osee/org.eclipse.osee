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
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.cache.ArtifactTypeCache;
import org.eclipse.osee.framework.core.cache.BranchCache;
import org.eclipse.osee.framework.core.enums.CoreTranslationIds;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.OseeCachingService;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.services.IOseeCachingService;
import org.eclipse.osee.framework.core.services.IOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.test.mocks.MockOseeCachingServiceProvider;
import org.eclipse.osee.framework.core.translation.ArtifactTypeTranslator;
import org.eclipse.osee.framework.core.translation.BasicArtifactTranslator;
import org.eclipse.osee.framework.core.translation.DataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.RelationTypeTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link RelationTypeTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class RelationTypeTranslatorTest extends BaseTranslatorTest<RelationType> {

   public RelationTypeTranslatorTest(RelationType data, ITranslator<RelationType> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(RelationType expected, RelationType actual) throws OseeCoreException {
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      IOseeModelFactoryServiceProvider factoryProvider = MockDataFactory.createFactoryProvider();
      IOseeCachingServiceProvider serviceProvider = MockDataFactory.createCachingProvider();
      ArtifactTypeCache cache = serviceProvider.getOseeCachingService().getArtifactTypeCache();

      IDataTranslationService service = new DataTranslationService();
      service.addTranslator(new BasicArtifactTranslator(), CoreTranslationIds.ARTIFACT_METADATA);
      service.addTranslator(new ArtifactTypeTranslator(service, factoryProvider), CoreTranslationIds.ARTIFACT_TYPE);

      ITranslator<RelationType> translator = new RelationTypeTranslator(service, factoryProvider);

      ArtifactType baseType = MockDataFactory.createBaseArtifactType();

      List<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 2; index++) {
         ArtifactType typeA = MockDataFactory.createArtifactType(index * 7);
         ArtifactType typeB = MockDataFactory.createArtifactType(index * 3);
         typeA.setSuperType(Collections.singleton(baseType));
         typeB.setSuperType(Collections.singleton(baseType));
         cache.cache(typeA);
         cache.cache(typeB);
         RelationType relType = MockDataFactory.createRelationType(index, typeA, typeB);
         data.add(new Object[] {relType, translator});
      }

      return data;
   }

   public static IOseeCachingServiceProvider createProvider(BranchCache cache) {
      IOseeCachingService service = new OseeCachingService(cache, null, null, null, null, null);
      return new MockOseeCachingServiceProvider(service);
   }
}
