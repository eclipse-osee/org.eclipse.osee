/*********************************************************************
 * Copyright (c) 2009 Boeing
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

package org.eclipse.osee.ats.api.demo;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static org.eclipse.osee.ats.api.config.AtsDisplayHint.Edit;
import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.ats;
import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.atsDemo;
import static org.eclipse.osee.ats.api.util.AtsImage.CHANGE_REQUEST;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Partition;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.enums.token.CodeCategoryAttributeType;
import org.eclipse.osee.ats.api.demo.enums.token.CodeDefectCodeAttributeType;
import org.eclipse.osee.ats.api.demo.enums.token.CodeDetectionAttributeType;
import org.eclipse.osee.ats.api.demo.enums.token.CodeReqDocAttributeType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeString;

/**
 * @author Ryan D. Brooks
 */
public interface DemoArtifactTypes {

   // @formatter:off
   AttributeTypeString ChangeType = atsDemo.createString(1152921504606847253L, "demo.code.Change Type", TEXT_PLAIN, "", Edit);
   AttributeTypeString Action = atsDemo.createString(1152921504606847254L, "demo.code.Action", TEXT_PLAIN, "", Edit);
   CodeCategoryAttributeType Category = atsDemo.createEnumNoTag(new CodeCategoryAttributeType(), Edit);
   AttributeTypeString CloseDetection = atsDemo.createString(1152921504606847239L, "demo.code.Close Detection", TEXT_PLAIN, "", Edit);
   AttributeTypeString CodeChangeReq = atsDemo.createString(1152921504606847240L, "demo.code.Code Change Req", TEXT_PLAIN, "", Edit);
   AttributeTypeString CSCI = atsDemo.createString(1152921504606847241L, "demo.code.CSCI", TEXT_PLAIN, "", Edit);
   CodeDefectCodeAttributeType DefectCode = atsDemo.createEnumNoTag(new CodeDefectCodeAttributeType(), Edit);
   CodeDetectionAttributeType Detection = atsDemo.createEnumNoTag(new CodeDetectionAttributeType(), Edit);
   AttributeTypeString IncludeBuild = atsDemo.createString(6624602983846643901L, "demo.code.Include Build", TEXT_PLAIN, "", Edit);
   AttributeTypeInteger LocAffected = atsDemo.createIntegerNoTag(2266722106367646882L, "demo.code.LOC Affected", TEXT_PLAIN, "Total Lines of Code Affected", Edit);
   AttributeTypeInteger LocReviewed = ats.createInteger(2266722106367646342L, "demo.code.LOC Reviewed", TEXT_PLAIN, "Total Lines of Code Reviewed", Edit);
   //ComputedCharacteristicDelta LocRemaining = ats.createComp(ComputedCharacteristicDelta::new, 89273067834049579L, "demo.code.LOC Remaining", "Total Lines of Code Remaining", LocAffected, LocReviewed);
   AttributeTypeString OriginatingBuild = atsDemo.createString(6539429238794418072L, "demo.code.Originating Build", TEXT_PLAIN, "", Edit);
   CodeReqDocAttributeType ReqDoc = atsDemo.createEnumNoTag(new CodeReqDocAttributeType());
   AttributeTypeString Subsystem = atsDemo.createString(1152921504606847248L, "demo.code.Subsystem", TEXT_PLAIN, "", Edit);
   AttributeTypeString TestBuild = atsDemo.createString(1152921504606847249L, "demo.code.Test Build", TEXT_PLAIN, "", Edit);
   AttributeTypeString TestMode = atsDemo.createString(1152921504606847250L, "demo.code.Test Mode", TEXT_PLAIN, "", Edit);
   AttributeTypeString TestNotes = atsDemo.createString(1152921504606847251L, "demo.code.Test Notes", TEXT_PLAIN, "", Edit);
   AttributeTypeString TestScript = atsDemo.createString(1152921504606847252L, "demo.code.Test Script", TEXT_PLAIN, "", Edit);
   AttributeTypeString Volume = atsDemo.createString(1152921504606847255L, "demo.code.Volume", TEXT_PLAIN, "", Edit);
   AttributeTypeString VerifyDate = atsDemo.createStringNoTag(1152921504606847256L, "demo.code.Verify Date", TEXT_PLAIN, "", Edit);
   AttributeTypeString Verifyer = atsDemo.createString(1152921504606847257L, "demo.code.Verifyer", TEXT_PLAIN, "", Edit);
   AttributeTypeString VerifyNote = atsDemo.createString(1152921504606847258L, "demo.code.Verify Note", TEXT_PLAIN, "", Edit);
   AttributeTypeString VerifyDetection = atsDemo.createString(1152921504606847259L, "demo.code.Verify Detection", TEXT_PLAIN, "", Edit);
   AttributeTypeString HoldStart = atsDemo.createStringNoTag(1152921504606847260L, "demo.code.Hold Start", TEXT_PLAIN, "", Edit);
   AttributeTypeString HoldEnd = atsDemo.createStringNoTag(1152921504606847261L, "demo.code.Hold End", TEXT_PLAIN, "", Edit);
   AttributeTypeString VerifyMode = atsDemo.createString(1152921504606847262L, "demo.code.Verify Mode", TEXT_PLAIN, "", Edit);
   AttributeTypeString VerifiedBuild = atsDemo.createString(1152921504606847263L, "demo.code.Verified Build", TEXT_PLAIN, "", Edit);
   AttributeTypeString PeerReviewReqd = atsDemo.createStringNoTag(1152921504606847264L, "demo.code.Peer Review Reqd", TEXT_PLAIN, "", Edit);
   AttributeTypeString PeerReviewId = atsDemo.createStringNoTag(1152921504606847265L, "demo.code.Peer Review Id", TEXT_PLAIN, "", Edit);
   AttributeTypeString RefPcr = atsDemo.createStringNoTag(1152921504606847266L, "demo.code.Ref PCR", TEXT_PLAIN, "", Edit);
   AttributeTypeString References = atsDemo.createString(1152921504606847267L, "demo.code.References", TEXT_PLAIN, "", Edit);
   AttributeTypeString RequirementId = atsDemo.createStringNoTag(1152921504606847268L, "demo.code.Requirement Id", TEXT_PLAIN, "", Edit);
   AttributeTypeString Librarian = atsDemo.createString(1152921504606847273L, "demo.code.Librarian", TEXT_PLAIN, "", Edit);
   AttributeTypeString PromoteDate = atsDemo.createStringNoTag(1152921504606847274L, "demo.code.Promote Date", TEXT_PLAIN, "", Edit);
   AttributeTypeString AuthorizationNotes = atsDemo.createString(1152921504606847275L, "demo.code.Authorization Notes", TEXT_PLAIN, "", Edit);
   AttributeTypeString ReworkLetter = atsDemo.createStringNoTag(1152921504606847276L, "demo.code.Rework Letter", TEXT_PLAIN, "", Edit);
   AttributeTypeString ProblemNo = atsDemo.createStringNoTag(1152921504606847277L, "demo.code.Problem No", TEXT_PLAIN, "", Edit);

   ArtifactTypeToken DemoChangeRequestTeamWorkflow = atsDemo.add(atsDemo.artifactType(3456L, "Demo Change Request", false,
      CHANGE_REQUEST, AtsArtifactTypes.ChangeRequestTeamWorkflow));

   ArtifactTypeToken DemoCodeTeamWorkflow = atsDemo.add(atsDemo.artifactType(79L, "Demo Code Team Workflow", false, TeamWorkflow)
      .zeroOrOne(ChangeType)
      .zeroOrOne(Action)
      .zeroOrOne(Category, Category.CodeProblem)
      .zeroOrOne(CloseDetection)
      .zeroOrOne(CodeChangeReq)
      .zeroOrOne(CSCI)
      .zeroOrOne(DefectCode, DefectCode.Te99OtherToolError)
      .zeroOrOne(Detection, Detection.Other)
      .zeroOrOne(IncludeBuild)
      .zeroOrOne(LocAffected)
      .zeroOrOne(LocReviewed)
     // .computed(LocRemaining)
      .zeroOrOne(OriginatingBuild)
      .zeroOrOne(ReqDoc, ReqDoc.Unknown)
      .zeroOrOne(Subsystem)
      .zeroOrOne(TestBuild)
      .zeroOrOne(TestMode)
      .zeroOrOne(TestNotes)
      .zeroOrOne(TestScript)
      .zeroOrOne(Volume)
      .zeroOrOne(VerifyDate)
      .zeroOrOne(Verifyer)
      .zeroOrOne(VerifyNote)
      .zeroOrOne(VerifyDetection)
      .zeroOrOne(HoldStart)
      .zeroOrOne(HoldEnd)
      .zeroOrOne(VerifyMode)
      .zeroOrOne(VerifiedBuild)
      .zeroOrOne(PeerReviewReqd)
      .zeroOrOne(PeerReviewId)
      .zeroOrOne(RefPcr)
      .zeroOrOne(References)
      .zeroOrOne(RequirementId)
      .zeroOrOne(Librarian)
      .zeroOrOne(PromoteDate)
      .zeroOrOne(AuthorizationNotes)
      .zeroOrOne(ReworkLetter)
      .zeroOrOne(ProblemNo)
      .atLeastOne(Partition, Partition.Unspecified));
   ArtifactTypeToken DemoReqTeamWorkflow = atsDemo.add(atsDemo.artifactType(80L, "Demo Req Team Workflow", false, TeamWorkflow));
   ArtifactTypeToken DemoTestTeamWorkflow = atsDemo.add(atsDemo.artifactType(81L, "Demo Test Team Workflow", false, TeamWorkflow));
   // @formatter:on

}