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
import java.util.Collection;
import java.util.List;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.data.OseeImportModelRequest;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.core.translation.OseeImportModelRequestTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link OseeImportModelRequest}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class OseeImportModelRequestTranslatorTest extends BaseTranslatorTest<OseeImportModelRequest> {

   public OseeImportModelRequestTranslatorTest(OseeImportModelRequest data, ITranslator<OseeImportModelRequest> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(OseeImportModelRequest expected, OseeImportModelRequest actual) throws OseeCoreException {
      Assert.assertNotSame(expected, actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      ITranslator<OseeImportModelRequest> translator = new OseeImportModelRequestTranslator();

      List<Object[]> data = new ArrayList<Object[]>();
      data.add(new Object[] {new OseeImportModelRequest("dummy:/model.osee", "dummy model", false, true, false),
            translator});
      return data;
   }
}
