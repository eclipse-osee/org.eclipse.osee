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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import junit.framework.TestCase;
import org.eclipse.core.runtime.AssertionFailedException;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;

/**
 * @author Robert A. Fisher
 */
public class ArtifactImportPageTest extends TestCase {

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportPage#ArtifactImportPage(java.io.File, org.eclipse.osee.framework.skynet.core.artifact.Artifact)}.
    */
   public void testArtifactImportPageFileArtifact() {
      try {
         new ArtifactImportPage(new File("somefile.txt"), null);
         fail("Expect AssertionFailedException");
      } catch (AssertionFailedException ex) {
         // Pass
      }
      try {
         new ArtifactImportPage(null, null);
         fail("Expect AssertionFailedException");
      } catch (Exception ex) {
         // Pass
      }
      try {
         new ArtifactImportPage(null, SkynetAuthentication.getInstance().getAuthenticatedUser());
         fail("Expect AssertionFailedException");
      } catch (Exception ex) {
         // Pass
      }

      // This should not generate an exception
      new ArtifactImportPage(new File("somefile.txt"), SkynetAuthentication.getInstance().getAuthenticatedUser());
   }

   /**
    * Test method for
    * {@link org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportPage#ArtifactImportPage(org.eclipse.jface.viewers.IStructuredSelection)}.
    */
   public void testArtifactImportPageIStructuredSelection() {
      // null should not generate any exceptions
      new ArtifactImportPage(null);
   }

}
