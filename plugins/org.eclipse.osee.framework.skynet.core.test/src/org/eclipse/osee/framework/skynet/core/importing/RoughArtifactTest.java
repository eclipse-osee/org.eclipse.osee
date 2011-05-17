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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;

/**
 * @author Ryan D. Brooks
 */
public class RoughArtifactTest {

   //TODO: due to issues with the MasterTestSuite_DemoDbTests

   private static RoughArtifact ra;

   @org.junit.BeforeClass
   public static void setUpBeforeClass() {
      ra = new RoughArtifact(RoughArtifactKind.PRIMARY);
   }

   //TODO: Not sure what this is testing
   @org.junit.Test(expected = NullPointerException.class)
   public void testAddAttributeWithNulls() throws OseeCoreException {
      ra.addAttribute((String) null, "");
   }

   @org.junit.Test(expected = OseeTypeDoesNotExist.class)
   public void testNonExistingEnumeration() throws OseeCoreException {
      ra.addAttribute("Apple", "Orange");
   }
}
