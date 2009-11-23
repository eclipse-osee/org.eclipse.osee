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
import org.eclipse.osee.framework.core.model.OseeEnumEntry;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.OseeEnumEntryTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link OseeEnumEntryTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class OseeEnumEntryTranslatorTest extends BaseTranslatorTest<OseeEnumEntry> {

   public OseeEnumEntryTranslatorTest(OseeEnumEntry data, ITranslator<OseeEnumEntry> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(OseeEnumEntry expected, OseeEnumEntry actual) throws OseeCoreException {
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      ITranslator<OseeEnumEntry> translator = new OseeEnumEntryTranslator(MockDataFactory.createFactoryProvider());

      List<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 2; index++) {
         OseeEnumEntry entry = MockDataFactory.createEnumEntry(index);
         data.add(new Object[] {entry, translator});
      }
      return data;
   }
}
