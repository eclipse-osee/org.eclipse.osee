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
import org.eclipse.osee.orcs.search.QueryBuilder;

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
      return getAllRequirementsToInterface(branch, 0L, 0L);
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterface(BranchId branch, long pageNum, long pageSize) {
      List<NodeTraceReportItem> results = new LinkedList<>();
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Requirement).andRelationExists(
            CoreRelationTypes.RequirementsToInterface);
      if (pageNum != 0L && pageSize != 0L) {
         query = query.isOnPage(pageNum, pageSize);
      }
      query = query.follow(CoreRelationTypes.RequirementsToInterface_InterfaceArtifact);
      List<ArtifactReadable> requirements = query.asArtifacts();
      results = requirements.stream().map(
         r -> new NodeTraceReportItem(r, CoreRelationTypes.RequirementsToInterface_InterfaceArtifact)).collect(
            Collectors.toList());
      return results;
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterfaceWithNoMatch(BranchId branch) {
      return getAllRequirementsToInterfaceWithNoMatch(branch, 0L, 0L);
   }

   @Override
   public List<NodeTraceReportItem> getAllRequirementsToInterfaceWithNoMatch(BranchId branch, long pageNum,
      long pageSize) {
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Requirement).andRelationNotExists(
            CoreRelationTypes.RequirementsToInterface);
      if (pageNum != 0L && pageSize != 0L) {
         query = query.isOnPage(pageNum, pageSize);
      }
      return query.asArtifacts().stream().map(
         r -> new NodeTraceReportItem(r, CoreRelationTypes.RequirementsToInterface_InterfaceArtifact)).collect(
            Collectors.toList());
   }

   @Override
   public List<NodeTraceReportItem> getAllInterfaceToRequirements(BranchId branch) {
      return getAllInterfaceToRequirements(branch, 0L, 0L);
   }

   @Override
   public List<NodeTraceReportItem> getAllInterfaceToRequirements(BranchId branch, long pageNum, long pageSize) {
      List<NodeTraceReportItem> results = new LinkedList<>();
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationExists(CoreRelationTypes.RequirementsToInterface);
      if (pageNum != 0L && pageSize != 0L) {
         query = query.isOnPage(pageNum, pageSize);
      }
      query = query.follow(CoreRelationTypes.RequirementsToInterface_Artifact);
      List<ArtifactReadable> arts = query.asArtifacts();
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
   public List<NodeTraceReportItem> getAllInterfaceToRequirementsWithNoMatch(BranchId branch, long pageNum,
      long pageSize) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationNotExists(CoreRelationTypes.RequirementsToInterface);
      if (pageNum != 0L && pageSize != 0L) {
         query = query.isOnPage(pageNum, pageSize);
      }
      return query.asArtifacts().stream().map(
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

   @Override
   public int getCountRequirementsToInterface(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Requirement).andRelationExists(
         CoreRelationTypes.RequirementsToInterface).getCount();
   }

   @Override
   public int getCountRequirementsToInterfaceWithNoMatch(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.Requirement).andRelationNotExists(CoreRelationTypes.RequirementsToInterface).getCount();
   }

   @Override
   public int getCountInterfaceToRequirements(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationExists(CoreRelationTypes.RequirementsToInterface).getCount();
   }

   @Override
   public int getCountInterfaceToRequirementsWithNoMatch(BranchId branch) {
      return orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.InterfaceArtifact).andRelationNotExists(
            CoreRelationTypes.RequirementsToInterface).getCount();
   }

}