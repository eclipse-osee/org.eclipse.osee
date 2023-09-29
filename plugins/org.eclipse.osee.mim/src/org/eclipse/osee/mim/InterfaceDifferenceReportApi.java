/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.mim;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.MimChangeSummary;
import org.eclipse.osee.mim.types.MimChangeSummaryItem;
import org.eclipse.osee.mim.types.MimDifferenceReport;

/**
 * @author Ryan T. Baldwin
 */
public interface InterfaceDifferenceReportApi {

   MimDifferenceReport getDifferenceReport(BranchId branch, BranchId compareBranch);

   Map<ArtifactId, MimChangeSummaryItem> getChangeSummaryItems(BranchId branch1, BranchId branch2, ArtifactId view);

   MimChangeSummary getChangeSummary(BranchId branch1, BranchId branch2, ArtifactId view);

   MimChangeSummary getChangeSummary(BranchId branch1, BranchId branch2, ArtifactId view,
      Map<ArtifactId, MimChangeSummaryItem> changes);

   MimChangeSummary getChangeSummary(BranchId branch1, BranchId branch2, ArtifactId view,
      Map<ArtifactId, MimChangeSummaryItem> changes, List<ApplicabilityToken> applicTokenList);
}
