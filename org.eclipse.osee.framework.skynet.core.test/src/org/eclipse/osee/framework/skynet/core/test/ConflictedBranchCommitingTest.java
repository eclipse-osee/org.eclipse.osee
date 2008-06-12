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

package org.eclipse.osee.framework.skynet.core.test;

import junit.framework.TestCase;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.exception.ConflictDetectionException;

/**
 * @author Theron Virgin
 */
public class ConflictedBranchCommitingTest extends TestCase {

   /**
    * @param name
    */
   public ConflictedBranchCommitingTest(String name) {
      super(name);
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#setUp()
    */
   protected void setUp() throws Exception {
      super.setUp();
   }

   /* (non-Javadoc)
    * @see junit.framework.TestCase#tearDown()
    */
   protected void tearDown() throws Exception {
      super.tearDown();
   }

   public void CheckCommitWithResolutionErrors() {
      try {
         BranchPersistenceManager.getInstance().commitBranch(ConflictTestManager.getSourceBranch(),
               ConflictTestManager.getDestBranch(), false, false).join();
      } catch (ConflictDetectionException ex) {
         return;
      } catch (Exception ex) {
         fail("Only the ConflictDetectionException should be thrown not a " + ex.getLocalizedMessage() + "Exception");
      }
   }

   public void CheckCommitWithoutResolutionErrors() {
      try {
         BranchPersistenceManager.getInstance().commitBranch(ConflictTestManager.getSourceBranch(),
               ConflictTestManager.getDestBranch(), false, false).join();
         assertTrue("Commit did not complete as expected", ConflictTestManager.validateCommit());
      } catch (Exception ex) {
         fail("No Exceptions should have been thrown. Not even the " + ex.getLocalizedMessage() + "Exception");
      }
   }
}