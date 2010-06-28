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

import org.eclipse.osee.framework.core.exception.OseeDataStoreException;

/**
 * @author Jeff C. Phillips
 */
public abstract class AccessObject {
   public abstract void removeFromCache();

   public abstract void removeFromDatabase(int subjectId) throws OseeDataStoreException;

   public abstract int getId();

   public static AccessObject getAccessObject(Object object) {
      //      if (object instanceof IBasicArtifact<?>) {
      //         return ArtifactAccessObject.getArtifactAccessObject((IBasicArtifact<?>) object);
      //      } else if (object instanceof IOseeBranch) {
      //         return BranchAccessObject.getBranchAccessObject((Branch) object);
      //      } else {
      //         return null;
      //      }
      return null;
   }

   public static AccessObject getAccessObjectFromCache(Object object) {
      //      if (object instanceof IBasicArtifact<?>) {
      //         return ArtifactAccessObject.getArtifactAccessObjectFromCache((Artifact) object);
      //      } else if (object instanceof IOseeBranch) {
      //         return BranchAccessObject.getBranchAccessObjectFromCache((Branch) object);
      //      } else {
      //         return null;
      //      }
      return null;
   }
}
