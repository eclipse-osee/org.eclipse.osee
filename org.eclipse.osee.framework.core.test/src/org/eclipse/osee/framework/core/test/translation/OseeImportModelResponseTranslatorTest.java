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
import org.eclipse.osee.framework.core.data.OseeImportModelResponse;
import org.eclipse.osee.framework.core.data.TableData;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IDataTranslationService;
import org.eclipse.osee.framework.core.translation.DataTranslationService;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.OseeImportModelResponseTranslator;
import org.eclipse.osee.framework.core.translation.TableDataTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link OseeImportModelResponse}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class OseeImportModelResponseTranslatorTest extends BaseTranslatorTest<OseeImportModelResponse> {

   public OseeImportModelResponseTranslatorTest(OseeImportModelResponse data, ITranslator<OseeImportModelResponse> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(OseeImportModelResponse expected, OseeImportModelResponse actual) throws OseeCoreException {
      Assert.assertNotSame(expected, actual);
      Assert.assertNotNull(actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      IDataTranslationService service = new DataTranslationService();
      service.addTranslator(new TableDataTranslator(), CoreTranslatorId.TABLE_DATA);
      ITranslator<OseeImportModelResponse> translator = new OseeImportModelResponseTranslator(service);

      List<Object[]> data = new ArrayList<Object[]>();
      OseeImportModelResponse response = new OseeImportModelResponse();
      response.setComparisonSnapshotModel("dummy compare");
      response.setComparisonSnapshotModelName("dummy compare name");
      response.setPersisted(true);

      List<TableData> tableDatas = new ArrayList<TableData>();
      tableDatas.add(new TableData("hello", new String[] {"col1", "col2"}, Arrays.asList(new String[] {"one1", "two2"},
            new String[] {"one3", "two4"})));
      response.setReportData(tableDatas);
      data.add(new Object[] {response, translator});
      return data;
   }
}
