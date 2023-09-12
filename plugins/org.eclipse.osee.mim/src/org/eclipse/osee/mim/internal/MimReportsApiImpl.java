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
package org.eclipse.osee.mim.internal;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.mim.MimReportsApi;
import org.eclipse.osee.mim.types.NodeTraceReportItem;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Ryan Baldwin
 */
public class MimReportsApiImpl implements MimReportsApi {

   private final OrcsApi orcsApi;

   public MimReportsApiImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterface(BranchId branch) {
      List<NodeTraceReportItem> results = new LinkedList<>();
      List<ArtifactReadable> requirements =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Requirement).andRelationExists(
            CoreRelationTypes.RequirementsToInterface).follow(
               CoreRelationTypes.RequirementsToInterface_InterfaceArtifact).asArtifacts();
      results = requirements.stream().map(
         r -> new NodeTraceReportItem(r, CoreRelationTypes.RequirementsToInterface_InterfaceArtifact)).collect(
            Collectors.toList());
      return results;
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterfaceWithNoMatch(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.Requirement).andRelationNotExists(
            CoreRelationTypes.RequirementsToInterface).asArtifacts().stream().map(
               r -> new NodeTraceReportItem(r, CoreRelationTypes.RequirementsToInterface_InterfaceArtifact)).collect(
                  Collectors.toList());
   }

   @Override
   public List<NodeTraceReportItem> getAllInterfaceToRequirements(BranchId branch) {
      List<NodeTraceReportItem> results = new LinkedList<>();
      List<ArtifactReadable> arts = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationExists(CoreRelationTypes.RequirementsToInterface).follow(
            CoreRelationTypes.RequirementsToInterface_Artifact).asArtifacts();
      results =
         arts.stream().map(a -> new NodeTraceReportItem(a, CoreRelationTypes.RequirementsToInterface_Artifact)).collect(
            Collectors.toList());
      return results;
   }

   @Override
   public List<NodeTraceReportItem> getAllInterfaceToRequirementsWithNoMatch(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationNotExists(
            CoreRelationTypes.RequirementsToInterface).asArtifacts().stream().map(
               a -> new NodeTraceReportItem(a, CoreRelationTypes.RequirementsToInterface_Artifact)).collect(
                  Collectors.toList());
   }

   @Override
   public NodeTraceReportItem getInterfacesFromRequirement(BranchId branch, ArtifactId artId) {
      ArtifactReadable requirement = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
         CoreRelationTypes.RequirementsToInterface_InterfaceArtifact).asArtifact();
      return new NodeTraceReportItem(requirement, CoreRelationTypes.RequirementsToInterface_InterfaceArtifact);
   }

   @Override
   public NodeTraceReportItem getRequirementsFromInterface(BranchId branch, ArtifactId artId) {
      ArtifactReadable interfaceReadable = orcsApi.getQueryFactory().fromBranch(branch).andId(artId).follow(
         CoreRelationTypes.RequirementsToInterface_Artifact).asArtifact();
      return new NodeTraceReportItem(interfaceReadable, CoreRelationTypes.RequirementsToInterface_Artifact);
   }

}