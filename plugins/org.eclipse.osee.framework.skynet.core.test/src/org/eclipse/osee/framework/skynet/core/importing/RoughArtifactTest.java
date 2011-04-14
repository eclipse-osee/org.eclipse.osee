/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Ryan D. Brooks
 */
public class RoughArtifactTest {

   //TODO: due to issues with the MasterTestSuite_DemoDbTests

   private static RoughArtifact ra;

   @BeforeClass
   public static void setUpBeforeClass() {
      ra = new RoughArtifact(RoughArtifactKind.PRIMARY);
   }

   @Test
   public void testAddAttributeWithNulls() {
      String nullString = null;
      ra.addAttribute(nullString, "");
   }

   @Test
   public void nonExistingEnumeration() {
      ra.addAttribute("Apple", "Orange");
   }
}
