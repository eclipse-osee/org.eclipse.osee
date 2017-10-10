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
package org.eclipse.osee.orcs.db.internal.exchange.transformer;

import java.util.Collection;
import java.util.Iterator;
import org.eclipse.osee.orcs.db.internal.exchange.transform.ExchangeTransformProvider;
import org.eclipse.osee.orcs.db.internal.exchange.transform.IExchangeTransformProvider;
import org.eclipse.osee.orcs.db.internal.exchange.transform.IOseeExchangeVersionTransformer;
import org.eclipse.osee.orcs.db.internal.exchange.transform.V0_9_2Transformer;
import org.eclipse.osee.orcs.db.internal.exchange.transform.V0_9_4Transformer;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.osgi.framework.Version;

/**
 * Test Case for {@link ExchangeTransformProvider}
 *
 * @author Roberto E. Escobar
 */
public class ExchangeTransformProviderTest {
   private static IExchangeTransformProvider transformProvider = new ExchangeTransformProvider();

   @BeforeClass
   public static void setup() {
      transformProvider = new ExchangeTransformProvider();
   }

   @Test
   @SuppressWarnings("unchecked")
   public void testGetApplicableTransforms() {
      assertApplicable("0.0.0", V0_9_2Transformer.class, V0_9_4Transformer.class);
      assertApplicable("0.0.0.v201009081001", V0_9_2Transformer.class, V0_9_4Transformer.class);
      assertApplicable("0.8.2.v201009081001", V0_9_2Transformer.class, V0_9_4Transformer.class);
      assertApplicable("0.8.3", V0_9_2Transformer.class, V0_9_4Transformer.class);
      assertApplicable("0.8.3.v201009081001", V0_9_2Transformer.class, V0_9_4Transformer.class);
      assertApplicable("0.9", V0_9_2Transformer.class, V0_9_4Transformer.class);
      assertApplicable("0.9.1.v201009081001", V0_9_2Transformer.class, V0_9_4Transformer.class);
      assertApplicable("0.9.2", V0_9_4Transformer.class);
      assertApplicable("0.9.3", V0_9_4Transformer.class);
      assertApplicable("0.9.4");
      assertApplicable("1");
   }

   @SuppressWarnings("unchecked")
   private static void assertApplicable(String versionToCheck, Class<? extends IOseeExchangeVersionTransformer>... expectedTransforms) {
      Version version = new Version(versionToCheck);

      String message = String.format("Version[%s]", version);

      Collection<IOseeExchangeVersionTransformer> actualTransforms =
         transformProvider.getApplicableTransformers(version);
      Assert.assertEquals(message, expectedTransforms.length, actualTransforms.size());

      Iterator<IOseeExchangeVersionTransformer> iterator = actualTransforms.iterator();
      for (int index = 0; index < expectedTransforms.length; index++) {
         Object expected = expectedTransforms[index];
         Object actual = iterator.next();

         Assert.assertEquals(message, expected, actual.getClass());
      }

   }
}
