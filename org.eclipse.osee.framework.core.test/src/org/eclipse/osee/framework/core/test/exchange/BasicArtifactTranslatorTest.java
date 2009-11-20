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
package org.eclipse.osee.framework.core.test.exchange;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.DefaultBasicArtifact;
import org.eclipse.osee.framework.core.data.IBasicArtifact;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exchange.BasicArtifactTranslator;
import org.eclipse.osee.framework.core.exchange.ITranslator;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link BasicArtifactTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class BasicArtifactTranslatorTest extends BaseTranslatorTest<IBasicArtifact<?>> {

   public BasicArtifactTranslatorTest(IBasicArtifact<?> data, ITranslator<IBasicArtifact<?>> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(IBasicArtifact<?> expected, IBasicArtifact<?> actual) throws OseeCoreException {
      Assert.assertNotSame(expected, actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<Object[]>();
      ITranslator<IBasicArtifact<?>> translator = new BasicArtifactTranslator();
      for (int index = 1; index <= 5; index++) {
         data.add(new Object[] {new DefaultBasicArtifact(index * 10, GUID.create(), "art: " + index), translator});
      }
      data.add(new Object[] {new DefaultBasicArtifact(-1, null, null), translator});
      return data;
   }
}
