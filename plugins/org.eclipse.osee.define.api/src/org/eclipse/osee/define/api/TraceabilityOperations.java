/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.define.api;

import java.io.Writer;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Morgan E. Cook
 */
public interface TraceabilityOperations {

   void generateTraceReport(BranchId branchId, String codeRoot, String traceRoot, Writer providedWriter, ArtifactTypeToken artifactType, AttributeTypeToken attributeType);

   TraceData getSrsToImpd(BranchId branch, ArtifactTypeId excludeType);

   ArtifactId baselineFiles(BranchId branch, ArtifactReadable repoArtifact, CertBaselineData baselineData, String password);

   ArtifactId baselineFiles(BranchId branch, ArtifactReadable repoArtifact, CertBaselineData baselineData, TransactionBuilder tx, String password);

   CertBaselineData getBaselineData(ArtifactReadable baselineArtifact);

   List<CertBaselineData> getBaselineData(BranchId branch, ArtifactReadable repoArtifact);

   TransactionToken copyCertBaselineData(BranchId destinationBranch, String repositoryName, BranchId sourceBranch);

   /**
    * @return list of current files (excluding deleted) in given Git repository with their latest change and baselined
    * data
    */
   List<CertFileData> getCertFileData(BranchId branch, ArtifactReadable repoArtifact);
}