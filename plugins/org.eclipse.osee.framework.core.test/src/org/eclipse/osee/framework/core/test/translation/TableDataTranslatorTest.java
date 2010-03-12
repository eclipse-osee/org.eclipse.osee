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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.TableData;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.TableDataTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link TableData}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class TableDataTranslatorTest extends BaseTranslatorTest<TableData> {

   public TableDataTranslatorTest(TableData data, ITranslator<TableData> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(TableData expected, TableData actual) throws OseeCoreException {
      Assert.assertNotSame(expected, actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      ITranslator<TableData> translator = new TableDataTranslator();

      List<Object[]> data = new ArrayList<Object[]>();
      String[] columns = new String[] {"col1", "col2"};
      List<String[]> rows = Arrays.asList(new String[] {"one1", "two2"}, new String[] {"one3", "two4"});
      data.add(new Object[] {new TableData("title", columns, rows), translator});
      return data;
   }
}
