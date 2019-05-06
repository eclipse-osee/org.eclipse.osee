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

import static org.eclipse.osee.define.api.DefineTupleTypes.GitLatest;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.AbstractSoftwareRequirement;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.CertificationBaselineEvent;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.CodeUnit;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.BaselinedBy;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.BaselinedTimestamp;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.GitChangeId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ReviewId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.ReviewStoryId;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.UserId;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.SupportingInfo_SupportingInfo;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.ws.rs.WebApplicationException;
import org.eclipse.osee.define.api.CertBaselineData;
import org.eclipse.osee.define.api.CertFileData;
import org.eclipse.osee.define.api.CertFileData.BaselineData;
import org.eclipse.osee.define.api.GitOperations;
import org.eclipse.osee.define.api.TraceData;
import org.eclipse.osee.define.api.TraceabilityOperations;
import org.eclipse.osee.define.rest.internal.TraceReportGenerator;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Ryan D. Brooks
 */
public final class TraceabilityOperationsImpl implements TraceabilityOperations {

   private final OrcsApi orcsApi;
   private final QueryFactory queryFactory;
   private final TupleQuery tupleQuery;
   private final GitOperations gitOps;

   public TraceabilityOperationsImpl(OrcsApi orcsApi, GitOperations gitOperations) {
      this.orcsApi = orcsApi;
      this.queryFactory = orcsApi.getQueryFactory();
      this.tupleQuery = queryFactory.tupleQuery();
      this.gitOps = gitOperations;
   }

   @Override
   public void generateTraceReport(BranchId branchId, String codeRoot, String traceRoot, Writer providedWriter, ArtifactTypeToken artifactType, AttributeTypeToken attributeType) {
      TraceReportGenerator generator = new TraceReportGenerator(artifactType, attributeType);
      try {
         generator.generate(orcsApi, branchId, codeRoot, traceRoot, providedWriter);
      } catch (Exception ex) {
         throw new WebApplicationException(ex);
      }
   }

   @Override
   public TraceData getSrsToImpd(BranchId branch, ArtifactTypeId excludeType) {
      ResultSet<ArtifactReadable> allSwReqs =
         queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.AbstractSoftwareRequirement).getResults();

      List<String> swReqs =
         allSwReqs.getList().stream().filter(req -> excludeType.isInvalid() || !req.isOfType(excludeType)).map(
            req -> req.getName()).collect(Collectors.toList());

      ResultSet<ArtifactReadable> impds =
         queryFactory.fromBranch(branch).andIsOfType(CoreArtifactTypes.ImplementationDetailsMsWord).getResults();

      Map<String, String[]> impdMap = new HashMap<>();
      for (ArtifactReadable impd : impds) {
         List<ArtifactReadable> matchingReq =
            impd.getRelated(CoreRelationTypes.ImplementationInfo_SoftwareRequirement).getList();

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

   @Override
   public ArtifactId baselineFiles(BranchId branch, ArtifactReadable repoArtifact, CertBaselineData baselineData, UserId account, TransactionBuilder tx, String password) {
      ArtifactId baselineEvent = tx.createArtifact(repoArtifact, CertificationBaselineEvent, baselineData.eventName);

      ArtifactId baselinedByUser =
         queryFactory.fromBranch(COMMON).andAttributeIs(UserId, baselineData.baselinedByUserId).asArtifactId();

      ArtifactId baselineCommit = gitOps.getCommitArtifactId(branch, baselineData.changeId);
      if (baselineCommit.isInvalid()) {
         gitOps.updateGitTrackingBranch(branch, repoArtifact, account, null, true, password, false);
         baselineCommit = gitOps.getCommitArtifactId(branch, baselineData.changeId);
         if (baselineCommit.isInvalid()) {
            throw new OseeArgumentException("No commit with change id [%s] can be found", baselineData.changeId);
         }
      }

      tx.setSoleAttributeValue(baselineEvent, GitChangeId, baselineData.changeId);
      tx.setSoleAttributeValue(baselineEvent, BaselinedBy, baselinedByUser);
      Date baselinedTimestamp = baselineData.baselinedTimestamp == null ? new Date() : baselineData.baselinedTimestamp;
      tx.setSoleAttributeValue(baselineEvent, BaselinedTimestamp, baselinedTimestamp);

      if (baselineData.reviewId != null) {
         tx.setSoleAttributeValue(baselineEvent, ReviewId, baselineData.reviewId);
      }
      if (baselineData.reviewStoryId != null) {
         tx.setSoleAttributeValue(baselineEvent, ReviewStoryId, baselineData.reviewStoryId);
      }

      for (String path : baselineData.files) {
         ArtifactId codeUnit = loadCodeUnit(repoArtifact, branch, path);
         if (codeUnit.isInvalid()) {
            throw new OseeArgumentException("No code unit found for path [%s]", path);
         }
         tx.relate(baselineEvent, CoreRelationTypes.SupportingInfo_IsSupportedBy, codeUnit);
         updateLGitLatestTuple(repoArtifact, branch, tx, codeUnit, baselineCommit);
      }

      return baselineEvent;
   }

   @Override
   public ArtifactId baselineFiles(BranchId branch, ArtifactReadable repoArtifact, CertBaselineData baselineData, UserId account, String password) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "rest - baseline  files");
      ArtifactId baselineEvent = baselineFiles(branch, repoArtifact, baselineData, account, tx, password);
      tx.commit();

      return baselineEvent;
   }

   private ArtifactId loadCodeUnit(ArtifactId repository, BranchId branch, String path) {
      List<ArtifactToken> codeUnits =
         queryFactory.fromBranch(branch).andNameEquals(path).andTypeEquals(CodeUnit).asArtifactTokens();
      for (ArtifactToken codeUnit : codeUnits) {
         //TODO: if CoreTupleTypes.GitCommitFile for this repository and path matches then return this codeUnit
         //tupleQuery.doesTuple3E3Exist(tupleType, e3);
         if (codeUnit.getName().equals(path)) {
            return codeUnit;
         }
      }

      return ArtifactId.SENTINEL;
   }

   private void updateLGitLatestTuple(ArtifactId repository, BranchId branch, TransactionBuilder tx, ArtifactId codeUnit, ArtifactId baselineCommit) {
      ArtifactId[] commitWraper = new ArtifactId[1];
      tupleQuery.getTuple4E3E4FromE1E2(GitLatest, branch, repository, codeUnit,
         (changeCommit, ignore) -> commitWraper[0] = changeCommit);

      tx.deleteTuple4ByE1E2(GitLatest, repository, codeUnit);

      tx.addTuple4(GitLatest, repository, codeUnit, commitWraper[0], baselineCommit);
   }

   @Override
   public List<CertBaselineData> getBaselineData(BranchId branch, ArtifactReadable repoArtifact) {
      List<CertBaselineData> certEvents = Collections.transform(repoArtifact.getChildren(), this::getBaselineData);
      java.util.Collections.sort(certEvents);
      return certEvents;
   }

   @Override
   public TransactionToken copyCertBaselineData(UserId account, BranchId destinationBranch, String repositoryName, BranchId sourceBranch) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(destinationBranch, account,
         "rest - copy cert baseline data");
      ArtifactReadable sourceRepo = gitOps.getRepoArtifact(sourceBranch, repositoryName);
      ArtifactReadable destinationRepo = gitOps.getRepoArtifact(destinationBranch, repositoryName);

      for (CertBaselineData certEvent : getBaselineData(sourceBranch, sourceRepo)) {
         baselineFiles(destinationBranch, destinationRepo, certEvent, account, tx, null);
      }
      return tx.commit();
   }

   @Override
   public List<CertFileData> getCertFileData(BranchId branch, ArtifactReadable repoArtifact) {
      List<CertFileData> files = new ArrayList<>();

      HashCollection<String, CertBaselineData> baselineEvents = new HashCollection<>();

      for (ArtifactReadable bl : repoArtifact.getChildren()) {
         CertBaselineData baselineData = getBaselineData(bl);
         for (String filePath : baselineData.files) {
            baselineEvents.put(filePath, baselineData);
         }
      }

      for (String fileName : baselineEvents.keySet()) {

         CertFileData file = new CertFileData();
         file.baselinedInfo = new ArrayList<BaselineData>();

         List<CertBaselineData> baselinedCommits = baselineEvents.getValues(fileName);
         java.util.Collections.sort(baselinedCommits);

         file.path = fileName;

         for (CertBaselineData baselinedCommit : baselinedCommits) {
            CertFileData.BaselineData baselineData = file.new BaselineData();
            baselineData.baselinedChangeId = baselinedCommit.changeId;
            baselineData.baselinedTimestamp = baselinedCommit.baselinedTimestamp;
            file.baselinedInfo.add(baselineData);
         }

         files.add(file);
      }

      return files;
   }

   @Override
   public CertBaselineData getBaselineData(ArtifactReadable baselineArtifact) {
      CertBaselineData baselineData = new CertBaselineData();
      baselineData.eventName = baselineArtifact.getName();
      baselineData.changeId = baselineArtifact.getSoleAttributeValue(GitChangeId);

      ArtifactId baselinedByUser = baselineArtifact.getSoleAttributeValue(BaselinedBy);
      String userId =
         queryFactory.fromBranch(COMMON).andId(baselinedByUser).asArtifactTokens(UserId).iterator().next().getName();

      baselineData.baselinedByUserId = userId;
      baselineData.baselinedTimestamp = baselineArtifact.getSoleAttributeValue(BaselinedTimestamp);
      baselineData.reviewId = baselineArtifact.getSoleAttributeValue(ReviewId, null);
      baselineData.reviewStoryId = baselineArtifact.getSoleAttributeValue(ReviewStoryId, null);

      baselineData.files = Collections.transform(baselineArtifact.getRelated(SupportingInfo_SupportingInfo).getList(),
         ArtifactReadable::getName);
      return baselineData;
   }
}