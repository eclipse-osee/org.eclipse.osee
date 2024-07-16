/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.framework.core.enums;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Active;
import static org.eclipse.osee.framework.core.enums.DispoTypeTokenProvider.dispo;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.token.DispoImportStateAttributeType;
import org.eclipse.osee.framework.core.enums.token.DispoItemStatusAttributeType;

/**
 * @author Angel Avila
 */
public interface DispoOseeTypes {

   // @formatter:off
   AttributeTypeString DispoAnnotationsJson = dispo.createString(1152921504606847878L, "dispo.Annotations JSON", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoCiSet = dispo.createString(5225296359986133054L, "dispo.CI Set", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoConfig = dispo.createString(1152921504606847893L, "dispo.Config", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate DispoDateCreated = dispo.createDate(1152921504606847889L, "dispo.Date Created", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeString DispoDiscrepanciesJson = dispo.createString(1152921504606847879L, "dispo.Discrepancies JSON", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoImportPath = dispo.createString(1152921504606847881L, "dispo.Import Path", MediaType.TEXT_PLAIN, "");
   AttributeTypeString ServerImportPath = dispo.createString(1152921504606847894L, "dispo.Import Url", MediaType.TEXT_PLAIN, "");
   DispoImportStateAttributeType DispoImportState = dispo.createEnum(new DispoImportStateAttributeType());
   AttributeTypeBoolean DispoIsMultiEnv = dispo.createBoolean(3587620131443940337L, "dispo.Is Multi-Env", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean DispoItemAborted = dispo.createBoolean(3458764513820541448L, "dispo.item.Aborted", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemAssignee = dispo.createString(3458764513820541441L, "dispo.item.Assignee", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemCategory = dispo.createString(3458764513820541442L, "dispo.item.Category", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemElapsedTime = dispo.createString(3458764513820541447L, "dispo.item.Elapsed Time", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemFileNumber = dispo.createString(3458764513820541715L, "dispo.item.File Number", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate DispoItemLastUpdated = dispo.createDate(1152921504606847890L, "dispo.item.Last Updated", AttributeTypeToken.TEXT_CALENDAR, "");
   AttributeTypeString DispoItemMachine = dispo.createString(3458764513820541446L, "dispo.item.Machine", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemMethodNumber = dispo.createString(3458764513820541460L, "dispo.item.Method Number", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean DispoItemNeedsRerun = dispo.createBoolean(3458764513820541444L, "dispo.item.Needs Rerun", MediaType.TEXT_PLAIN, "");
   AttributeTypeBoolean DispoItemNeedsReview = dispo.createBoolean(2903020690286924090L, "dispo.item.Needs Review", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemNotes = dispo.createString(3458764513820541456L, "dispo.item.Notes", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemPercentComplete = dispo.createString(3458764513820541449L, "dispo.item.Percent Complete", MediaType.TEXT_PLAIN, "");
   DispoItemStatusAttributeType DispoItemStatus = dispo.createEnum(new DispoItemStatusAttributeType());
   AttributeTypeString DispoItemTeam = dispo.createString(3160880792426011047L, "dispo.Team", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemTotalPoints = dispo.createString(3458764513820541443L, "dispo.item.Total Points", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoItemVersion = dispo.createString(3458764513820541440L, "dispo.item.Version", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoMultiEnvSettings = dispo.createString(3587660131047940387L, "dispo.Multi-Env Settings", MediaType.APPLICATION_JSON, "");
   AttributeTypeString DispoNotesJson = dispo.createString(1152921504606847880L, "dispo.Notes JSON", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoOperationSummary = dispo.createString(1152921504606847895L, "dispo.Operation Summary", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoRerunList = dispo.createString(3587660131087940587L, "dispo.Rerun List", MediaType.TEXT_PLAIN, "");
   AttributeTypeString DispoSummaryCount = dispo.createString(1152921504606847491L, "dispo.Summary Count", MediaType.TEXT_PLAIN, "");
   AttributeTypeDate DispoTime = dispo.createDate(7240092025387115138L, "dispo.Time", AttributeTypeToken.TEXT_CALENDAR, "");

   ArtifactTypeToken DispositionableItem = dispo.add(dispo.artifactType(808L, "dispo.Dispositionable Item", false, Artifact)
      .exactlyOne(Active, Boolean.TRUE)
      .exactlyOne(DispoAnnotationsJson, "{}")
      .zeroOrOne(DispoDateCreated)
      .zeroOrOne(DispoDiscrepanciesJson, "[]")
      .exactlyOne(DispoItemAborted)
      .zeroOrOne(DispoItemAssignee, "UnAssigned")
      .zeroOrOne(DispoItemCategory)
      .zeroOrOne(DispoItemElapsedTime)
      .zeroOrOne(DispoItemFileNumber)
      .zeroOrOne(DispoItemLastUpdated)
      .zeroOrOne(DispoItemMachine, "n/a")
      .zeroOrOne(DispoItemMethodNumber)
      .exactlyOne(DispoItemNeedsRerun)
      .exactlyOne(DispoItemNeedsReview)
      .zeroOrOne(DispoItemNotes)
      .zeroOrOne(DispoItemPercentComplete, "0%")
      .zeroOrOne(DispoItemStatus, DispoItemStatus.Unspecified)
      .zeroOrOne(DispoItemTotalPoints)
      .zeroOrOne(DispoItemVersion)
      .exactlyOne(DispoItemTeam, "Unassigned"));
   ArtifactTypeToken DispositionSet = dispo.add(dispo.artifactType(807L, "Disposition Set", false, Artifact)
      .exactlyOne(Active, Boolean.TRUE)
      .zeroOrOne(DispoCiSet)
      .zeroOrOne(DispoConfig)
      .zeroOrOne(DispoDateCreated)
      .zeroOrOne(DispoImportPath)
      .zeroOrOne(ServerImportPath)
      .zeroOrOne(DispoImportState, DispoImportState.Unspecified)
      .zeroOrOne(DispoIsMultiEnv)
      .zeroOrOne(DispoMultiEnvSettings)
      .zeroOrOne(DispoNotesJson, "[]")
      .zeroOrOne(DispoOperationSummary)
      .zeroOrOne(DispoRerunList)
      .zeroOrOne(DispoSummaryCount, "0/0")
      .zeroOrOne(DispoTime));

   String FILE_EXT_REGEX = "dispo.api.file.ext.regex";
   String RESULTS_FILE_EXT_REGEX = "dispo.api.results.file.ext.regex";
}