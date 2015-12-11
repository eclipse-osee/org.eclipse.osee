/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.workdef;

import org.eclipse.osee.ats.api.workdef.IAtsWorkDefinition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Test case for {@link WorkDefinitionMatch}
 *
 * @author Donald G. Dunne
 */
public class WorkDefinitionMatchTest {

   // @formatter:off
   @Mock private IAtsWorkDefinition workDef;
   // @formatter:on

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      Mockito.when(workDef.getName()).thenReturn("mine");
   }

   @Test
   public void testWorkDefinitionMatch() {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      Assert.assertNull(match.getWorkDefinition());
      Assert.assertTrue(match.getTrace().isEmpty());
   }

   @Test
   public void testWorkDefinitionMatchWorkDefinitionString() {
      WorkDefinitionMatch match = new WorkDefinitionMatch(workDef.getName(), "trace");
      match.setWorkDefinition(workDef);
      Assert.assertNotNull(match.getWorkDefinition());
      Assert.assertFalse(match.getTrace().isEmpty());
   }

   @Test
   public void testAddTrace() {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      Assert.assertTrue(match.getTrace().isEmpty());
      match.addTrace("trace 1");
      Assert.assertEquals("trace 1", match.getTrace().iterator().next());
      match.addTrace("trace 1");
      Assert.assertEquals(1, match.getTrace().size());
      Assert.assertEquals("trace 1", match.getTrace().iterator().next());
      match.addTrace("trace 2");
      Assert.assertEquals("trace 1", match.getTrace().iterator().next());
      Assert.assertEquals(2, match.getTrace().size());
   }

   @Test
   public void testIsMatched() {
      WorkDefinitionMatch match = new WorkDefinitionMatch(workDef.getName(), "trace");
      match.setWorkDefinition(workDef);
      Assert.assertTrue(match.isMatched());
      match.setWorkDefinition(null);
      Assert.assertFalse(match.isMatched());
   }

   @Test
   public void testToString() {
      WorkDefinitionMatch match = new WorkDefinitionMatch(workDef.getName(), "trace");
      match.setWorkDefinition(workDef);
      Assert.assertEquals("mine", match.toString());
   }

}
