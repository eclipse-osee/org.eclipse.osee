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
import org.eclipse.osee.framework.core.IDataTranslationService;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exchange.BasicArtifactDataTranslator;
import org.eclipse.osee.framework.core.exchange.BranchTranslator;
import org.eclipse.osee.framework.core.exchange.DataTranslationService;
import org.eclipse.osee.framework.core.exchange.IDataTranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case For {@link BasicArtifactDataTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class BranchTranslatorTest extends BaseTranslatorTest<Branch> {

   public BranchTranslatorTest(Branch data, IDataTranslator<Branch> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(Branch expected, Branch actual) throws OseeCoreException {
      DataUtility.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() {
      IDataTranslationService service = new DataTranslationService();
      service.addTranslator(Branch.class, new BranchTranslator(service));

      List<Object[]> data = new ArrayList<Object[]>();
      for (int index = 1; index <= 5; index++) {
         data.add(new Object[] {DataUtility.createBranch(index * 10), service});
      }
      data.add(new Object[] {DataUtility.createBranch(-1)});
      return data;
   }
}
