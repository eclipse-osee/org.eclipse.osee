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

import junit.framework.Assert;

import org.eclipse.osee.framework.core.data.ChangeItem;
import org.eclipse.osee.framework.core.data.ChangeVersion;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exchange.ChangeItemTranslator;
import org.eclipse.osee.framework.core.exchange.ChangeVersionTranslator;
import org.eclipse.osee.framework.core.exchange.DataTranslationService;
import org.eclipse.osee.framework.core.exchange.IDataTranslator;
import org.eclipse.osee.framework.core.util.ChangeItemBuilder;
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

   public ChangeItemTranslatorTest(ChangeItem data, IDataTranslator<ChangeItem> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(ChangeItem expected, ChangeItem actual) throws OseeCoreException {
       Assert.assertEquals(expected.getArtId(), actual.getArtId());
   }

   @Parameters
   public static Collection<Object[]> data() {
      DataTranslationService dataTranslationService = new DataTranslationService();
      dataTranslationService.addTranslator(ChangeItem.class, new ChangeItemTranslator(dataTranslationService));
      dataTranslationService.addTranslator(ChangeVersion.class, new ChangeVersionTranslator());
      
      List<Object[]> data = new ArrayList<Object[]>();
      IDataTranslator<ChangeItem> translator = new ChangeItemTranslator(dataTranslationService);
      try {
         data.add(new Object[] {ChangeItemBuilder.buildTestChangeItem(), translator});
      }
      catch (OseeArgumentException ex) {
         throw new IllegalArgumentException(ex);
      }
      return data;
   }
}
