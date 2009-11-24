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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.services.IOseeModelFactoryServiceProvider;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.translation.ArtifactTypeTranslator;
import org.eclipse.osee.framework.core.translation.DataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link ArtifactTypeTranslator}
 * 
 * @author Jeff C. Phillips
 */
@RunWith(Parameterized.class)
public class ArtifactTypeTranslatorTest extends BaseTranslatorTest<ArtifactType> {

   public ArtifactTypeTranslatorTest(ArtifactType data, ITranslator<ArtifactType> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(ArtifactType expected, ArtifactType actual) throws OseeCoreException {
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      IOseeModelFactoryServiceProvider factoryProvider = MockDataFactory.createFactoryProvider();
      DataTranslationService dataTranslationService = new DataTranslationService();
      dataTranslationService.addTranslator(new ArtifactTypeTranslator(dataTranslationService, factoryProvider), ArtifactType.class);

      List<Object[]> data = new ArrayList<Object[]>();
      ITranslator<ArtifactType> translator = new ArtifactTypeTranslator(dataTranslationService, factoryProvider);
      data.add(new Object[] {MockDataFactory.createArtifactType(1), translator});
      return data;
   }
}
