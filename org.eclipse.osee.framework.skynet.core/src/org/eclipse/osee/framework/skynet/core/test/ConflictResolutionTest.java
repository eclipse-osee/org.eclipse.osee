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

import java.util.Collection;
import junit.framework.TestCase;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.RelationConflict;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 */
public class ConflictResolutionTest extends TestCase {

   /**
    * @param name
    */
   public ConflictResolutionTest(String name) {
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

   public void testResolveConflicts() {
      try {
         Collection<Conflict> conflicts =
               RevisionManager.getInstance().getConflictsPerBranch(
                     ConflictTestManager.getSourceBranch(),
                     ConflictTestManager.getDestBranch(),
                     TransactionIdManager.getInstance().getStartEndPoint(ConflictTestManager.getSourceBranch()).getKey());
         int whichChange = 1;

         for (Conflict conflict : conflicts) {
            if (conflict instanceof ArtifactConflict) {
               ((ArtifactConflict) conflict).revertSourceArtifact();
            } else if (conflict instanceof AttributeConflict) {
               ConflictTestManager.resolveAttributeConflict((AttributeConflict) conflict);
               conflict.setStatus(Conflict.Status.RESOLVED);
            } else if (conflict instanceof RelationConflict) {
               fail("Relation Conflicts are not supported yet");
            }
            whichChange++;
         }

         conflicts =
               RevisionManager.getInstance().getConflictsPerBranch(
                     ConflictTestManager.getSourceBranch(),
                     ConflictTestManager.getDestBranch(),
                     TransactionIdManager.getInstance().getStartEndPoint(ConflictTestManager.getSourceBranch()).getKey());

         for (Conflict conflict : conflicts) {
            assertTrue(
                  "This conflict was not found to be resolved ArtId = " + conflict.getArtId() + " " + conflict.getSourceDisplayData(),
                  conflict.statusResolved());

         }
      } catch (Exception ex) {
         fail(ex.getMessage());
      }
   }
}
