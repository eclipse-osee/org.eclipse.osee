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
import org.eclipse.osee.framework.core.message.BranchCommitResponse;
import org.eclipse.osee.framework.core.message.internal.translation.BranchCommitResponseTranslator;
import org.eclipse.osee.framework.core.message.test.mocks.DataAsserts;
import org.eclipse.osee.framework.core.model.cache.TransactionCache;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BranchCommitResponseTranslator}
 * 
 * @author Megumi Telles
 */
@RunWith(Parameterized.class)
public class BranchCommitResponseTranslatorTest extends BaseTranslatorTest<BranchCommitResponse> {

   public BranchCommitResponseTranslatorTest(BranchCommitResponse data, ITranslator<BranchCommitResponse> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(BranchCommitResponse expected, BranchCommitResponse actual) {
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {

      List<Object[]> data = new ArrayList<Object[]>();

      ITranslator<BranchCommitResponse> translator = new BranchCommitResponseTranslator();
      for (int index = 1; index <= 2; index++) {
         BranchCommitResponse response = new BranchCommitResponse();
         response.setTransactionId(index);
         data.add(new Object[] {response, translator});
      }
      return data;
   }
}
