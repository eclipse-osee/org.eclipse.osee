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
package org.eclipse.osee.framework.skynet.core.artifact;

/**
 * @author Roberto E. Escobar
 */
class RootBranchInitializer {

   /**
    * Add common artifacts that should be available to all branches
    * 
    * @param branch
    */
   protected void initialize(Branch branch) throws Exception {
      // Create necessary default hierarchy root artifact
      ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(branch, true);

      // Create necessary top universal group artifact
      UniversalGroup.createTopUniversalGroupArtifact(branch);
   }
}
