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
package org.eclipse.osee.coverage.demo.internal;

import org.eclipse.osee.coverage.demo.CoverageBranches;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author Roberto E. Escobar
 */
public class AddCoverageBranch implements IDbInitializationTask {

   @Override
   public void run() throws OseeCoreException {
      Branch coverageBranch = BranchManager.createTopLevelBranch(CoverageBranches.COVERAGE_TEST_BRANCH);
      OseeSystemArtifacts.getDefaultHierarchyRootArtifact(coverageBranch);
   }

}
