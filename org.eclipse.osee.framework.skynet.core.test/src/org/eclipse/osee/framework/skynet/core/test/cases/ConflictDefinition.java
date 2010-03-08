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
package org.eclipse.osee.framework.skynet.core.test.cases;

import java.util.Collection;
import java.util.HashSet;
import org.eclipse.osee.framework.skynet.core.test.cases.ConflictTestManager.AttributeValue;

class ConflictDefinition {
   final Collection<AttributeValue> values = new HashSet<AttributeValue>();
   final Collection<AttributeValue> newAttributes = new HashSet<AttributeValue>();
   String artifactType;
   boolean sourceDelete;
   boolean destDelete;
   int rootArtifact;
   int queryNumber;
   int numConflicts = 0;
   boolean sourceModified = false;
   boolean destModified = false;

   protected void setValues(String artifactType, boolean sourceDelete, boolean destDelete, int rootArtifact, int queryNumber) {
      this.artifactType = artifactType;
      this.sourceDelete = sourceDelete;
      this.destDelete = destDelete;
      this.rootArtifact = rootArtifact;
      this.queryNumber = queryNumber;
   }

   protected boolean destinationDeleted(ConflictDefinition[] conflictDefs) {
      if (rootArtifact == 0) {
         return destDelete;
      }
      return destDelete || conflictDefs[rootArtifact].destinationDeleted(conflictDefs);
   }

   protected boolean sourceDeleted(ConflictDefinition[] conflictDefs) {
      if (rootArtifact == 0) {
         return sourceDelete;
      }
      return sourceDelete || conflictDefs[rootArtifact].sourceDeleted(conflictDefs);
   }

   protected int getNumberConflicts(ConflictDefinition[] conflictDefs) {
      if (!destinationDeleted(conflictDefs) && !sourceDeleted(conflictDefs)) {
         return numConflicts;
      } else if (destinationDeleted(conflictDefs) && sourceModified || sourceDeleted(conflictDefs) && destModified) {
         return 1;
      } else {
         return 0;
      }
   }

   protected boolean artifactAdded(ConflictDefinition[] conflictDefs) {
      if (!destinationDeleted(conflictDefs) && !sourceDeleted(conflictDefs)) {
         return numConflicts > 0;
      } else if (destinationDeleted(conflictDefs) && sourceModified || sourceDeleted(conflictDefs) && destModified) {
         return true;
      }
      return false;
   }

}