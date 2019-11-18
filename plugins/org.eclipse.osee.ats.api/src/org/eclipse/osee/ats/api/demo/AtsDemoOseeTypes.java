/*******************************************************************************
 * Copyright (c) 2009 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.api.demo;

import static org.eclipse.osee.ats.api.data.AtsArtifactTypes.TeamWorkflow;
import static org.eclipse.osee.ats.api.demo.AtsDemoTypeTokenProvider.atsDemo;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Partition;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeInteger;
import org.eclipse.osee.framework.core.data.AttributeTypeString;

/**
 * @author Ryan D. Brooks
 */
public interface AtsDemoOseeTypes {

   // @formatter:off
   AttributeTypeString ChangeType = atsDemo.createString(1152921504606847253L, "demo.code.Change Type", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Action = atsDemo.createString(1152921504606847254L, "demo.code.Action", MediaType.TEXT_PLAIN, "");
   AttributeTypeEnum Category = atsDemo.createEnumNoTag(1152921504606847238L, "demo.code.Category", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CloseDetection = atsDemo.createString(1152921504606847239L, "demo.code.Close Detection", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CodeChangeReq = atsDemo.createString(1152921504606847240L, "demo.code.Code Change Req", MediaType.TEXT_PLAIN, "");
   AttributeTypeString CSCI = atsDemo.createString(1152921504606847241L, "demo.code.CSCI", MediaType.TEXT_PLAIN, "");
   AttributeTypeEnum DefectCode = atsDemo.createEnumNoTag(1152921504606847242L, "demo.code.Defect Code", MediaType.TEXT_PLAIN, "");
   AttributeTypeEnum Detection = atsDemo.createEnumNoTag(1152921504606847243L, "demo.code.Detection", MediaType.TEXT_PLAIN, "");
   AttributeTypeString IncludeBuild = atsDemo.createString(6624602983846643901L, "demo.code.Include Build", MediaType.TEXT_PLAIN, "");
   AttributeTypeInteger LocAffected = atsDemo.createIntegerNoTag(2266722106367646882L, "demo.code.LOC Affected", MediaType.TEXT_PLAIN, "");
   AttributeTypeString OriginatingBuild = atsDemo.createString(6539429238794418072L, "demo.code.Originating Build", MediaType.TEXT_PLAIN, "");
   AttributeTypeEnum ReqDoc = atsDemo.createEnumNoTag(1740569308658341L, "demo.code.Req Doc", MediaType.TEXT_PLAIN, "");
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
   AttributeTypeString DupCpcrNo = atsDemo.createStringNoTag(1152921504606847269L, "demo.code.Dup CPCR No", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DupCsci = atsDemo.createStringNoTag(1152921504606847270L, "demo.code.Dup CSCI", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DupCpcrType = atsDemo.createStringNoTag(1152921504606847271L, "demo.code.Dup CPCR Type", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DupOfCpcrNoSeq = atsDemo.createStringNoTag(1152921504606847272L, "demo.code.Dup Of CPCR No Seq", MediaType.TEXT_PLAIN, "");
   AttributeTypeString Librarian = atsDemo.createString(1152921504606847273L, "demo.code.Librarian", MediaType.TEXT_PLAIN, "");
   AttributeTypeString PromoteDate = atsDemo.createStringNoTag(1152921504606847274L, "demo.code.Promote Date", MediaType.TEXT_PLAIN, "");
   AttributeTypeString AuthorizationNotes = atsDemo.createString(1152921504606847275L, "demo.code.Authorization Notes", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ReworkLetter = atsDemo.createStringNoTag(1152921504606847276L, "demo.code.Rework Letter", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ProblemNo = atsDemo.createStringNoTag(1152921504606847277L, "demo.code.Problem No", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DpEffectivity = atsDemo.createStringNoTag(1152921504606847278L, "demo.code.DP Effectivity", MediaType.TEXT_PLAIN, "");
   AttributeTypeString SpEffectivity = atsDemo.createStringNoTag(1152921504606847279L, "demo.code.SP Effectivity", MediaType.TEXT_PLAIN, "");
   AttributeTypeString WpEffectivity = atsDemo.createStringNoTag(1152921504606847280L, "demo.code.WP Effectivity", MediaType.TEXT_PLAIN, "");

   ArtifactTypeToken DemoCodeTeamWorkflow = atsDemo.add(atsDemo.artifactType(79L, "Demo Code Team Workflow", false, TeamWorkflow)
      .zeroOrOne(ChangeType, "")
      .zeroOrOne(Action, "")
      .zeroOrOne(Category, "", 3458764513820541328L)
      .zeroOrOne(CloseDetection, "")
      .zeroOrOne(CodeChangeReq, "")
      .zeroOrOne(CSCI, "")
      .zeroOrOne(DefectCode, "", 3458764513820541329L)
      .zeroOrOne(Detection, "", 3458764513820541330L)
      .zeroOrOne(IncludeBuild, "")
      .zeroOrOne(LocAffected, "")
      .zeroOrOne(OriginatingBuild, "")
      .zeroOrOne(ReqDoc, "", 3458764513820541331L)
      .zeroOrOne(Subsystem, "")
      .zeroOrOne(TestBuild, "")
      .zeroOrOne(TestMode, "")
      .zeroOrOne(TestNotes, "")
      .zeroOrOne(TestScript, "")
      .zeroOrOne(Volume, "")
      .zeroOrOne(VerifyDate, "")
      .zeroOrOne(Verifyer, "")
      .zeroOrOne(VerifyNote, "")
      .zeroOrOne(VerifyDetection, "")
      .zeroOrOne(HoldStart, "")
      .zeroOrOne(HoldEnd, "")
      .zeroOrOne(VerifyMode, "")
      .zeroOrOne(VerifiedBuild, "")
      .zeroOrOne(PeerReviewReqd, "")
      .zeroOrOne(PeerReviewId, "")
      .zeroOrOne(RefPcr, "")
      .zeroOrOne(References, "")
      .zeroOrOne(RequirementId, "")
      .zeroOrOne(DupCpcrNo, "")
      .zeroOrOne(DupCsci, "")
      .zeroOrOne(DupCpcrType, "")
      .zeroOrOne(DupOfCpcrNoSeq, "")
      .zeroOrOne(Librarian, "")
      .zeroOrOne(PromoteDate, "")
      .zeroOrOne(AuthorizationNotes, "")
      .zeroOrOne(ReworkLetter, "")
      .zeroOrOne(ProblemNo, "")
      .zeroOrOne(DpEffectivity, "")
      .zeroOrOne(SpEffectivity, "")
      .zeroOrOne(WpEffectivity, "")
      .atLeastOne(Partition, " ", 3458764513820541309L));
   ArtifactTypeToken DemoReqTeamWorkflow = atsDemo.add(atsDemo.artifactType(80L, "Demo Req Team Workflow", false, TeamWorkflow));
   ArtifactTypeToken DemoTestTeamWorkflow = atsDemo.add(atsDemo.artifactType(81L, "Demo Test Team Workflow", false, TeamWorkflow));
   // @formatter:on

}