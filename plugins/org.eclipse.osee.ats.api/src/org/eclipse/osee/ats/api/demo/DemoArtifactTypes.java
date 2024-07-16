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

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.ats;
import static org.eclipse.osee.ats.api.data.AtsTypeTokenProvider.atsDemo;
import static org.eclipse.osee.ats.api.util.AtsImage.CHANGE_REQUEST;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Partition;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.demo.enums.token.CodeCategoryAttributeType;
import org.eclipse.osee.ats.api.demo.enums.token.CodeDefectCodeAttributeType;
import org.eclipse.osee.ats.api.demo.enums.token.CodeDetectionAttributeType;
import org.eclipse.osee.ats.api.demo.enums.token.CodeReqDocAttributeType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.computed.ComputedCharacteristicDelta;

/**
 * @author Ryan D. Brooks
 */
public interface DemoArtifactTypes {

   // @formatter:off
   AttributeTypeString ChangeType = atsDemo.createString(1152921504606847253L, "demo.code.Change Type", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Action = atsDemo.createString(1152921504606847254L, "demo.code.Action", MediaType.TEXT_PLAIN, "");
   CodeCategoryAttributeType Category = atsDemo.createEnumNoTag(new CodeCategoryAttributeType());
   AttributeTypeString CloseDetection = atsDemo.createString(1152921504606847239L, "demo.code.Close Detection", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CodeChangeReq = atsDemo.createString(1152921504606847240L, "demo.code.Code Change Req", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CSCI = atsDemo.createString(1152921504606847241L, "demo.code.CSCI", MediaType.TEXT_PLAIN, "");
   CodeDefectCodeAttributeType DefectCode = atsDemo.createEnumNoTag(new CodeDefectCodeAttributeType());
   CodeDetectionAttributeType Detection = atsDemo.createEnumNoTag(new CodeDetectionAttributeType());
   AttributeTypeString IncludeBuild = atsDemo.createString(6624602983846643901L, "demo.code.Include Build", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger LocAffected = atsDemo.createIntegerNoTag(2266722106367646882L, "demo.code.LOC Affected", MediaType.TEXT_PLAIN, "Total Lines of Code Affected");
   AttributeTypeInteger LocReviewed = ats.createInteger(2266722106367646342L, "demo.code.LOC Reviewed", MediaType.TEXT_PLAIN, "Total Lines of Code Reviewed");
   ComputedCharacteristicDelta LocRemaining = ats.createComp(ComputedCharacteristicDelta::new, 89273067834049579L, "demo.code.LOC Remaining", "Total Lines of Code Remaining", LocAffected, LocReviewed);
   AttributeTypeString OriginatingBuild = atsDemo.createString(6539429238794418072L, "demo.code.Originating Build", MediaType.TEXT_PLAIN, "");
   CodeReqDocAttributeType ReqDoc = atsDemo.createEnumNoTag(new CodeReqDocAttributeType());
   AttributeTypeString Subsystem = atsDemo.createString(1152921504606847248L, "demo.code.Subsystem", MediaType.TEXT_PLAIN, "");
   AttributeTypeString TestBuild = atsDemo.createString(1152921504606847249L, "demo.code.Test Build", MediaType.TEXT_PLAIN, "");
   AttributeTypeString TestMode = atsDemo.createString(1152921504606847250L, "demo.code.Test Mode", MediaType.TEXT_PLAIN, "");
   AttributeTypeString TestNotes = atsDemo.createString(1152921504606847251L, "demo.code.Test Notes", MediaType.TEXT_PLAIN, "");
   AttributeTypeString TestScript = atsDemo.createString(1152921504606847252L, "demo.code.Test Script", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Volume = atsDemo.createString(1152921504606847255L, "demo.code.Volume", MediaType.TEXT_PLAIN, "");
   AttributeTypeString VerifyDate = atsDemo.createStringNoTag(1152921504606847256L, "demo.code.Verify Date", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Verifyer = atsDemo.createString(1152921504606847257L, "demo.code.Verifyer", MediaType.TEXT_PLAIN, "");
   AttributeTypeString VerifyNote = atsDemo.createString(1152921504606847258L, "demo.code.Verify Note", MediaType.TEXT_PLAIN, "");
   AttributeTypeString VerifyDetection = atsDemo.createString(1152921504606847259L, "demo.code.Verify Detection", MediaType.TEXT_PLAIN, "");
   AttributeTypeString HoldStart = atsDemo.createStringNoTag(1152921504606847260L, "demo.code.Hold Start", MediaType.TEXT_PLAIN, "");
   AttributeTypeString HoldEnd = atsDemo.createStringNoTag(1152921504606847261L, "demo.code.Hold End", MediaType.TEXT_PLAIN, "");
   AttributeTypeString VerifyMode = atsDemo.createString(1152921504606847262L, "demo.code.Verify Mode", MediaType.TEXT_PLAIN, "");
   AttributeTypeString VerifiedBuild = atsDemo.createString(1152921504606847263L, "demo.code.Verified Build", MediaType.TEXT_PLAIN, "");
   AttributeTypeString PeerReviewReqd = atsDemo.createStringNoTag(1152921504606847264L, "demo.code.Peer Review Reqd", MediaType.TEXT_PLAIN, "");
   AttributeTypeString PeerReviewId = atsDemo.createStringNoTag(1152921504606847265L, "demo.code.Peer Review Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString RefPcr = atsDemo.createStringNoTag(1152921504606847266L, "demo.code.Ref PCR", MediaType.TEXT_PLAIN, "");
   AttributeTypeString References = atsDemo.createString(1152921504606847267L, "demo.code.References", MediaType.TEXT_PLAIN, "");
   AttributeTypeString RequirementId = atsDemo.createStringNoTag(1152921504606847268L, "demo.code.Requirement Id", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Librarian = atsDemo.createString(1152921504606847273L, "demo.code.Librarian", MediaType.TEXT_PLAIN, "");
   AttributeTypeString PromoteDate = atsDemo.createStringNoTag(1152921504606847274L, "demo.code.Promote Date", MediaType.TEXT_PLAIN, "");
   AttributeTypeString AuthorizationNotes = atsDemo.createString(1152921504606847275L, "demo.code.Authorization Notes", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ReworkLetter = atsDemo.createStringNoTag(1152921504606847276L, "demo.code.Rework Letter", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ProblemNo = atsDemo.createStringNoTag(1152921504606847277L, "demo.code.Problem No", MediaType.TEXT_PLAIN, "");

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
      .computed(LocRemaining)
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