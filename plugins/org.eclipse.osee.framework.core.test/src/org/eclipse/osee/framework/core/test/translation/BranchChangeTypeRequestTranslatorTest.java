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
import org.eclipse.osee.framework.core.data.ChangeBranchTypeRequest;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.translation.BranchChangeStateRequestTranslator;
import org.eclipse.osee.framework.core.translation.BranchChangeTypeRequestTranslator;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BranchChangeStateRequestTranslator}
 * 
 * @author Megumi Telles
 */
@RunWith(Parameterized.class)
public class BranchChangeTypeRequestTranslatorTest extends BaseTranslatorTest<ChangeBranchTypeRequest> {

   public BranchChangeTypeRequestTranslatorTest(ChangeBranchTypeRequest data, ITranslator<ChangeBranchTypeRequest> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(ChangeBranchTypeRequest expected, ChangeBranchTypeRequest actual) throws OseeCoreException {
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      ITranslator<ChangeBranchTypeRequest> translator = new BranchChangeTypeRequestTranslator();

      for (int index = 1; index <= 2; index++) {
         data.add(new Object[] {new ChangeBranchTypeRequest(index * 3, BranchType.valueOf(++index)), translator});
      }
      return data;
   }
}
