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
package org.eclipse.osee.framework.ui.skynet.renderer;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test Case for {@link PresentationType}
 * 
 * @author Roberto E. Escobar
 */
@RunWith(Parameterized.class)
public class PresentationTypeTest {

   private final PresentationType target;

   public PresentationTypeTest(PresentationType target) {
      this.target = target;
   }

   @Test(expected = OseeArgumentException.class)
   public void testEmptyMatches()  {
      target.matches();
   }

   @Test
   public void testNoMatch()  {
      Collection<PresentationType> toMatch = new ArrayList<>();
      for (PresentationType item : PresentationType.values()) {
         if (item != target) {
            toMatch.add(item);
            boolean actual = target.matches(item);
            Assert.assertFalse(actual);
         } else {
            boolean actual = target.matches(item);
            Assert.assertTrue(actual);
         }
      }

      PresentationType[] items = toMatch.toArray(new PresentationType[toMatch.size()]);
      boolean actual = target.matches(items);
      Assert.assertEquals(false, actual);
   }

   @Test
   public void testMatch()  {
      Collection<PresentationType> toMatch = new ArrayList<>();
      for (PresentationType item : PresentationType.values()) {
         toMatch.add(item);
         if (item != target) {
            boolean actual = target.matches(item);
            Assert.assertFalse(actual);
         } else {
            boolean actual = target.matches(item);
            Assert.assertTrue(actual);
         }
      }

      PresentationType[] items = toMatch.toArray(new PresentationType[toMatch.size()]);
      boolean actual = target.matches(items);
      Assert.assertTrue(actual);
   }

   @Parameters
   public static Collection<Object[]> getData() {
      Collection<Object[]> data = new ArrayList<>();
      for (PresentationType underTest : PresentationType.values()) {
         data.add(new Object[] {underTest});
      }
      return data;
   }
}
