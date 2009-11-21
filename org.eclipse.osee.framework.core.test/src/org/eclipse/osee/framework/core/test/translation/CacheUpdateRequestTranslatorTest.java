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
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.CacheUpdateRequest;
import org.eclipse.osee.framework.core.enums.OseeCacheEnum;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.translation.CacheUpdateRequestTranslator;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link CacheUpdateRequestTranslator}
 * 
 * @author Roberto E. Escobar
 * @author Jeff C. Phillips
 */
@RunWith(Parameterized.class)
public class CacheUpdateRequestTranslatorTest extends BaseTranslatorTest<CacheUpdateRequest> {

   public CacheUpdateRequestTranslatorTest(CacheUpdateRequest data, ITranslator<CacheUpdateRequest> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(CacheUpdateRequest expected, CacheUpdateRequest actual) throws OseeCoreException {
      Assert.assertNotSame(expected, actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      CacheUpdateRequestTranslator translator = new CacheUpdateRequestTranslator();

      List<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 5; index++) {
         data.add(new Object[] {MockDataFactory.createRequest(index), translator});
      }
      //      
      data.add(new Object[] {new CacheUpdateRequest(OseeCacheEnum.ARTIFACT_TYPE_CACHE), translator});
      return data;
   }
}
