/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.osee.display.api.data.StyledText;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for {@link Utility}
 * 
 * @author Roberto E. Escobar
 */
public class UtilityTest {

   @Test
   public void testGetMatchedTextInner() {
      List<MatchLocation> matches = new ArrayList<MatchLocation>();
      matches.add(new MatchLocation(5, 8));
      matches.add(new MatchLocation(12, 14));

      List<StyledText> actual = Utility.getMatchedText("Software Requirements", matches);
      Assert.assertEquals(5, actual.size());
      Iterator<StyledText> iterator = actual.iterator();
      checkText(iterator.next(), "Soft", false);
      checkText(iterator.next(), "ware", true);
      checkText(iterator.next(), " Re", false);
      checkText(iterator.next(), "qui", true);
      checkText(iterator.next(), "rements", false);
   }

   @Test
   public void testGetMatchedTextOuter() {
      List<MatchLocation> matches = new ArrayList<MatchLocation>();
      matches.add(new MatchLocation(0, 4));
      matches.add(new MatchLocation(17, 21));

      List<StyledText> actual = Utility.getMatchedText("Software Requirements", matches);
      Assert.assertEquals(3, actual.size());
      Iterator<StyledText> iterator = actual.iterator();
      checkText(iterator.next(), "Soft", true);
      checkText(iterator.next(), "ware Require", false);
      checkText(iterator.next(), "ments", true);
   }

   private static void checkText(StyledText text, String expected, boolean isHighlighted) {
      Assert.assertEquals(expected, text.getData());
      Assert.assertEquals(isHighlighted, text.isHighLighted());
   }
}
