/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.mim.types.NodeTraceReportItem;

/**
 * @author Ryan Baldwin
 */
public interface MimReportsApi {

   public List<NodeTraceReportItem> getAllRequirementsToInterface(BranchId branch);

   public List<NodeTraceReportItem> getAllRequirementsToInterface(BranchId branch, long pageNum, long pageSize);

   int getCountRequirementsToInterface(BranchId branch);

   public List<NodeTraceReportItem> getAllRequirementsToInterfaceWithNoMatch(BranchId branch);

   public List<NodeTraceReportItem> getAllRequirementsToInterfaceWithNoMatch(BranchId branch, long pageNum,
      long pageSize);

   public int getCountRequirementsToInterfaceWithNoMatch(BranchId branch);

   public List<NodeTraceReportItem> getAllInterfaceToRequirements(BranchId branch);

   public List<NodeTraceReportItem> getAllInterfaceToRequirements(BranchId branch, long pageNum, long pageSize);

   public int getCountInterfaceToRequirements(BranchId branch);

   public List<NodeTraceReportItem> getAllInterfaceToRequirementsWithNoMatch(BranchId branch);

   public List<NodeTraceReportItem> getAllInterfaceToRequirementsWithNoMatch(BranchId branch, long pageNum,
      long pageSize);

   public int getCountInterfaceToRequirementsWithNoMatch(BranchId branch);

   public NodeTraceReportItem getInterfacesFromRequirement(BranchId branch, ArtifactId artId);

   public NodeTraceReportItem getRequirementsFromInterface(BranchId branch, ArtifactId artId);

   public List<ArtifactToken> getImpactedConnections(BranchId branch);

}
