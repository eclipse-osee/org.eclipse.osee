/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractSoftwareRequirement;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import org.eclipse.osee.define.api.TraceData;
import org.eclipse.osee.define.api.TraceabilityOperations;
import org.eclipse.osee.define.rest.internal.TraceReportGenerator;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

public class TraceabilityOperationsImpl implements TraceabilityOperations {

   private final OrcsApi orcsApi;

   public TraceabilityOperationsImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   @Override
   public void generateTraceReport(BranchId branchId, String codeRoot, String traceRoot, Writer providedWriter, IArtifactType artifactType, AttributeTypeToken attributeType) {
      TraceReportGenerator generator = new TraceReportGenerator(artifactType, attributeType);
      try {
         generator.generate(orcsApi, branchId, codeRoot, traceRoot, providedWriter);
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

   @Override
   public TraceData getSrsToImpd(BranchId branch, ArtifactTypeId excludeType) {
      ResultSet<ArtifactReadable> allSwReqs = orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(
         CoreArtifactTypes.AbstractSoftwareRequirement).getResults();

      List<String> swReqs =
         allSwReqs.getList().stream().filter(req -> excludeType.isInvalid() || !req.isOfType(excludeType)).map(
            req -> req.getName()).collect(Collectors.toList());

      ResultSet<ArtifactReadable> impds =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.ImplementationDetails).getResults();

      Map<String, String[]> impdMap = new HashMap<>();
      for (ArtifactReadable impd : impds) {
         List<ArtifactReadable> matchingReq =
            impd.getRelated(CoreRelationTypes.Implementation_Info__Requirement).getList();

         String[] pair;
         if (matchingReq.isEmpty()) {
            pair = findMatchingReq(impd);
         } else {
            pair = new String[matchingReq.size() + 1];
            for (int i = 0; i < matchingReq.size(); i++) {
               pair[i] = matchingReq.get(i).getName();
            }
            pair[matchingReq.size()] = "0";
         }
         impdMap.put(impd.getName(), pair);
      }
      TraceData traceData = new TraceData(swReqs, impdMap);
      return traceData;
   }

   private String[] findMatchingReq(ArtifactReadable impd) {
      ArtifactReadable cursor = impd.getParent();
      int level = 1;
      while (cursor != null) {
         if (cursor.isOfType(AbstractSoftwareRequirement)) {
            return new String[] {cursor.getName(), String.valueOf(level)};
         }
         level++;
         cursor = cursor.getParent();
      }
      return null;
   }
}