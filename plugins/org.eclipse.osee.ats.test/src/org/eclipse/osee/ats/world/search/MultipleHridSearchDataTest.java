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
package org.eclipse.osee.ats.world.search;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.eclipse.osee.ats.util.AtsEditor;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.junit.Test;

/**
 * Test case for {@link MultipleHridSearchData}
 * 
 * @author Donald G. Dunne
 */
public class MultipleHridSearchDataTest {

   @Test
   public void testGetValidGuidsAndHrids_none() {
      MultipleHridSearchData data = new MultipleHridSearchData("test", AtsEditor.WorldEditor);
      data.setEnteredIds("PCR 1234 23456");
      List<String> validGuidsAndHrids = data.getValidGuidsAndHrids();
      assertTrue(validGuidsAndHrids.isEmpty());
   }

   @Test
   public void testGetValidGuidsAndHrids_valid() {
      MultipleHridSearchData data = new MultipleHridSearchData("test", AtsEditor.WorldEditor);
      String guid1 = GUID.create();
      String guid2 = GUID.create();
      String hrid1 = HumanReadableId.generate();
      data.setEnteredIds(String.format("%s, %s,%s, PCR 1234", guid1, guid2, hrid1));
      List<String> validGuidsAndHrids = data.getValidGuidsAndHrids();
      assertEquals(3, validGuidsAndHrids.size());
      assertTrue(validGuidsAndHrids.contains(guid1));
      assertTrue(validGuidsAndHrids.contains(guid2));
      assertTrue(validGuidsAndHrids.contains(hrid1));
   }

}
