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
package org.eclipse.osee.framework.core.message.test.translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.message.DatastoreInitRequest;
import org.eclipse.osee.framework.core.message.TableData;
import org.eclipse.osee.framework.core.message.internal.translation.DatastoreInitRequestTranslator;
import org.eclipse.osee.framework.core.message.test.mocks.DataAsserts;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link TableData}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class DatastoreInitRequestTranslatorTest extends BaseTranslatorTest<DatastoreInitRequest> {

   public DatastoreInitRequestTranslatorTest(DatastoreInitRequest data, ITranslator<DatastoreInitRequest> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(DatastoreInitRequest expected, DatastoreInitRequest actual) {
      Assert.assertNotSame(expected, actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      ITranslator<DatastoreInitRequest> translator = new DatastoreInitRequestTranslator();
      List<Object[]> data = new ArrayList<Object[]>();
      data.add(new Object[] {new DatastoreInitRequest("tableData", "indexData", true), translator});
      data.add(new Object[] {new DatastoreInitRequest("", "", false), translator});
      return data;
   }
}
