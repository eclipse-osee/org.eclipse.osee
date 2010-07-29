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
package org.eclipse.osee.framework.core.dsl.integration.test;

import java.util.Arrays;
import java.util.Collection;
import junit.framework.Assert;
import org.eclipse.osee.framework.core.dsl.integration.ArtifactDataProvider;
import org.eclipse.osee.framework.core.dsl.integration.ModelUtil;
import org.eclipse.osee.framework.core.dsl.integration.OseeAccessModelInterpreter;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockAccessContextId;
import org.eclipse.osee.framework.core.dsl.integration.test.mocks.MockModel;
import org.eclipse.osee.framework.core.dsl.oseeDsl.AccessContext;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.junit.Before;
import org.junit.Test;

/**
 * Test Case for {@link ModelUtil}
 * 
 * @author Roberto E. Escobar
 */
public class OseeAccessModelInterpreterTest {
   private static final MockAccessContextId CONTEXT_1 = new MockAccessContextId(GUID.create(), "Context 1");
   private static final MockAccessContextId CONTEXT_2 = new MockAccessContextId(GUID.create(), "Context 2");
   private static final MockAccessContextId FULL_CONTEXT = null;

   private OseeAccessModelInterpreter interpreter;

   @Before
   public void setup() {
      ArtifactDataProvider accessor = null;
      interpreter = new OseeAccessModelInterpreter(accessor);
   }

   @Test
   public void testGetContext() {
      AccessContext expectedContext1 = MockModel.createAccessContext(CONTEXT_1.getGuid(), "c1");
      AccessContext expectedContext2 = MockModel.createAccessContext(CONTEXT_2.getGuid(), "c2");

      Collection<AccessContext> contexts = Arrays.asList(expectedContext1, expectedContext2);

      AccessContext actualContext1 = interpreter.getContext(contexts, CONTEXT_1);
      Assert.assertEquals(expectedContext1, actualContext1);

      AccessContext actualContext2 = interpreter.getContext(contexts, CONTEXT_2);
      Assert.assertEquals(expectedContext2, actualContext2);
   }

   //   @Test
   //   public void testComputeAccessDetails() {
   //      Collection<AccessDetail<?>> details = new ArrayList<AccessDetail<?>>();
   //
   //      //      interpreter.computeAccessDetails(FULL_CONTEXT, objectToCheck, details);
   //   }

}
