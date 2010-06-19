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
import junit.framework.Assert;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.ChangeVersion;
import org.eclipse.osee.framework.core.message.internal.translation.ChangeVersionTranslator;
import org.eclipse.osee.framework.core.message.test.mocks.DataAsserts;
import org.eclipse.osee.framework.core.message.test.mocks.MockRequestFactory;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link ChangeVersionTranslator}
 *
 * @author Jeff C. Phillips
 */
@RunWith(Parameterized.class)
public class ChangeVersionTranslatorTest extends BaseTranslatorTest<ChangeVersion> {

   public ChangeVersionTranslatorTest(ChangeVersion data, ITranslator<ChangeVersion> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(ChangeVersion expected, ChangeVersion actual) throws OseeCoreException {
      Assert.assertNotSame(expected, actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      List<Object[]> data = new ArrayList<Object[]>();
      ITranslator<ChangeVersion> translator = new ChangeVersionTranslator();
      data.add(new Object[] {MockRequestFactory.createChangeVersion(22), translator});
      return data;
   }
}
