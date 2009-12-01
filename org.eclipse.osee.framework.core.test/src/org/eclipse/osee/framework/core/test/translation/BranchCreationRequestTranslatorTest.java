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
import org.eclipse.osee.framework.core.data.BranchCreationRequest;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.test.mocks.MockDataFactory;
import org.eclipse.osee.framework.core.translation.BranchCreationRequestTranslator;
import org.eclipse.osee.framework.core.translation.ITranslator;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link BranchCreationRequestTranslator}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class BranchCreationRequestTranslatorTest extends BaseTranslatorTest<BranchCreationRequest> {

   public BranchCreationRequestTranslatorTest(BranchCreationRequest data, ITranslator<BranchCreationRequest> translator) {
      super(data, translator);
   }

   @Override
   protected void checkEquals(BranchCreationRequest expected, BranchCreationRequest actual) throws OseeCoreException {
      Assert.assertNotSame(expected, actual);
      DataAsserts.assertEquals(expected, actual);
   }

   @Parameters
   public static Collection<Object[]> data() throws OseeCoreException {
      List<Object[]> data = new ArrayList<Object[]>();
      ITranslator<BranchCreationRequest> translator = new BranchCreationRequestTranslator();

      data.add(new Object[] {MockDataFactory.createBranchCreateRequest(45), translator});
      return data;
   }
}
