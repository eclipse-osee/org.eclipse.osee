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
import org.eclipse.osee.framework.core.data.ChangeBranchArchiveStateRequest;
import org.eclipse.osee.framework.core.enums.BranchArchivedState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.translation.BranchChangeArchivedStateRequestTranslator;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BranchChangeArchivedStateRequestTranslator}
 * 
 * @author Megumi Telles
 */
@RunWith(Parameterized.class)
public class BranchChangeArchivedStateRequestTranslatorTest extends BaseTranslatorTest<ChangeBranchArchiveStateRequest> {

   public BranchChangeArchivedStateRequestTranslatorTest(ChangeBranchArchiveStateRequest data, ITranslator<ChangeBranchArchiveStateRequest> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(ChangeBranchArchiveStateRequest expected, ChangeBranchArchiveStateRequest actual) throws OseeCoreException {
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      ITranslator<ChangeBranchArchiveStateRequest> translator = new BranchChangeArchivedStateRequestTranslator();
      int state = 0;
      for (int index = 1; index <= 2; index++) {
         data.add(new Object[] {new ChangeBranchArchiveStateRequest(index * 3, BranchArchivedState.valueOf(state++)),
               translator});
      }
      return data;
   }
}
