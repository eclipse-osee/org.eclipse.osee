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
package org.eclipse.osee.framework.skynet.core.test.integration;

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifactKind;

/**
 * @author Ryan D. Brooks
 */
public class RoughArtifactTest {

   private static RoughArtifact ra;

   @org.junit.BeforeClass
   public static void setUpBeforeClass() {
      ra = new RoughArtifact(RoughArtifactKind.PRIMARY);
   }

   @org.junit.Test(expected = OseeTypeDoesNotExist.class)
   public void testNonExistingEnumeration() throws OseeCoreException {
      ra.addAttribute("Apple", "Orange");
   }
}
