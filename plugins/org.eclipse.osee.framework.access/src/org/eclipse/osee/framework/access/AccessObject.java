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
package org.eclipse.osee.framework.access;

import org.eclipse.osee.framework.access.internal.data.ArtifactAccessObject;
import org.eclipse.osee.framework.access.internal.data.BranchAccessObject;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Jeff C. Phillips
 */
public abstract class AccessObject implements HasBranch {
   public abstract void removeFromCache();

   public abstract void removeFromDatabase(int subjectId) ;

   public static AccessObject getAccessObject(Object object)  {
      if (object instanceof Artifact) {
         return ArtifactAccessObject.getArtifactAccessObject((Artifact) object);
      } else if (object instanceof BranchId) {
         return BranchAccessObject.getBranchAccessObject((BranchId) object);
      } else {
         return null;
      }
   }

   public static AccessObject getAccessObjectFromCache(Object object)  {
      if (object instanceof Artifact) {
         return ArtifactAccessObject.getArtifactAccessObjectFromCache((Artifact) object);
      } else if (object instanceof BranchId) {
         return BranchAccessObject.getBranchAccessObjectFromCache((BranchId) object);
      } else {
         return null;
      }
   }
}
