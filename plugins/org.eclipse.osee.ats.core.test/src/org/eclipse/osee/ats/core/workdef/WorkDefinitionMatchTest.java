/*
 * Created on Mar 21, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.workdef;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test case for {@link WorkDefinitionMatch}
 *
 * @author Donald G. Dunne
 */
public class WorkDefinitionMatchTest {

   @Test
   public void testWorkDefinitionMatch() {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      Assert.assertNull(match.getWorkDefinition());
      Assert.assertTrue(match.getTrace().isEmpty());
   }

   @Test
   public void testWorkDefinitionMatchWorkDefinitionString() {
      WorkDefinitionMatch match = new WorkDefinitionMatch(new WorkDefinition("mine"), "trace");
      Assert.assertNotNull(match.getWorkDefinition());
      Assert.assertFalse(match.getTrace().isEmpty());
   }

   @Test
   public void testGetSetWorkDefinition() {
      WorkDefinitionMatch match = new WorkDefinitionMatch();
      Assert.assertNull(match.getWorkDefinition());
      match.setWorkDefinition(new WorkDefinition("mine"));
      Assert.assertNotNull(match.getWorkDefinition());
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
      WorkDefinitionMatch match = new WorkDefinitionMatch(new WorkDefinition("mine"), "trace");
      Assert.assertTrue(match.isMatched());
      match.setWorkDefinition(null);
      Assert.assertFalse(match.isMatched());
   }

   @Test
   public void testToString() {
      WorkDefinitionMatch match = new WorkDefinitionMatch(new WorkDefinition("mine"), "trace");
      Assert.assertEquals("mine", match.toString());
   }

}
