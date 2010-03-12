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

import junit.framework.Assert;

import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.translation.ChangeItemTranslator;
import org.eclipse.osee.framework.core.translation.ChangeVersionTranslator;
import org.eclipse.osee.framework.core.translation.DataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link ChangeItemTranslator}
 * 
 * @author Jeff C. Phillips
 */
@RunWith(Parameterized.class)
public class ChangeItemTranslatorTest extends BaseTranslatorTest<ChangeItem> {

   public ChangeItemTranslatorTest(ChangeItem data, ITranslator<ChangeItem> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(ChangeItem expected, ChangeItem actual) throws OseeCoreException {
      Assert.assertNotSame(expected, actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      DataTranslationService dataTranslationService = new DataTranslationService();
      dataTranslationService.addTranslator(new ChangeVersionTranslator(),
                                           CoreTranslatorId.CHANGE_VERSION);

      List<Object[]> data = new ArrayList<Object[]>();
      ITranslator<ChangeItem> translator = new ChangeItemTranslator(dataTranslationService);
      data.add(new Object[] {MockDataFactory.createArtifactChangeItem(), translator});
      return data;
   }
}
