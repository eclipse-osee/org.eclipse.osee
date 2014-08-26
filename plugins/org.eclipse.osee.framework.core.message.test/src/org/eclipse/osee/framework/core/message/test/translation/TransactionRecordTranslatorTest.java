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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.message.internal.translation.TransactionRecordTranslator;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link TransactionRecordTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class TransactionRecordTranslatorTest extends BaseTranslatorTest<Integer> {

   public TransactionRecordTranslatorTest(Integer data, ITranslator<Integer> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(Integer expected, Integer actual) {
      Assert.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      ITranslator<Integer> translator = new TransactionRecordTranslator();
      List<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 2; index++) {
         data.add(new Object[] {index * 10, translator});
      }
      return data;
   }
}
