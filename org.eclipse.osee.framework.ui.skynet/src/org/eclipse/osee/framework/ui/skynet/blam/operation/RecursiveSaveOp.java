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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class RecursiveSaveOp implements BlamOperation {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();

   public void runOperation(BlamVariableMap variableMap, Branch branch, IProgressMonitor monitor) throws Exception {
      Artifact root = artifactManager.getDefaultHierarchyRootArtifact(branch);
      root.getChild("System Requirements").persist(true);
   }
}
