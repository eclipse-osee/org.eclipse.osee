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
import org.eclipse.osee.framework.core.data.BranchCommitRequest;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.translation.BranchCommitRequestTranslator;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BranchCommitRequestTranslator}
 * 
 * @author Megumi Telles
 */
@RunWith(Parameterized.class)
public class BranchCommitRequestTranslatorTest extends BaseTranslatorTest<BranchCommitRequest> {

   public BranchCommitRequestTranslatorTest(BranchCommitRequest data, ITranslator<BranchCommitRequest> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(BranchCommitRequest expected, BranchCommitRequest actual) throws OseeCoreException {
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      ITranslator<BranchCommitRequest> translator = new BranchCommitRequestTranslator();

      boolean archive = false;
      for (int index = 1; index <= 2; index++) {
         archive ^= archive;
         data.add(new Object[] {new BranchCommitRequest(index * 3, index * 2, index * 4, archive), translator});
      }
      return data;
   }
}
