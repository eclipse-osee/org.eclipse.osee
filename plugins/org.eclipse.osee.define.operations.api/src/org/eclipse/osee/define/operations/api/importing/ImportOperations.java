/*********************************************************************
 * Copyright (c) 2019 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.operations.api.importing;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author David W. Miller
 */
public interface ImportOperations {

   public XResultData importWord(BranchId branch, String wordURI, ArtifactId parent, Integer tier);

   public XResultData verifyWordImport(BranchId branch, String wordURI, ArtifactId parent, Integer tier);

   public XResultData rectifyWordImport(BranchId branch, String wordURI, ArtifactId parent, Integer tier, String doorsIds);

   public XResultData importSetup(BranchId branch, String baseDir, Integer startBranch, boolean handleRelations, boolean singleBranch);

}
