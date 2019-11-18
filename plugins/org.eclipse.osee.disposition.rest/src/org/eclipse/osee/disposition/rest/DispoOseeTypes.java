/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest;

import static org.eclipse.osee.disposition.rest.DispoTypeTokenProvider.dispo;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Active;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

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
   AttributeTypeEnum DispoImportState = dispo.createEnum(3458764513820541334L, "dispo.Import State", MediaType.TEXT_PLAIN, "");
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
   AttributeTypeEnum DispoItemStatus = dispo.createEnum(3458764513820541336L, "dispo.item.Status", MediaType.TEXT_PLAIN, "");
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
      .exactlyOne(Active, "true")
      .exactlyOne(DispoAnnotationsJson, "{}")
      .zeroOrOne(DispoDateCreated, "")
      .zeroOrOne(DispoDiscrepanciesJson, "[]")
      .exactlyOne(DispoItemAborted, "false")
      .zeroOrOne(DispoItemAssignee, "UnAssinged")
      .zeroOrOne(DispoItemCategory, "")
      .zeroOrOne(DispoItemElapsedTime, "0.0")
      .zeroOrOne(DispoItemFileNumber, "0")
      .zeroOrOne(DispoItemLastUpdated, "")
      .zeroOrOne(DispoItemMachine, "n/a")
      .zeroOrOne(DispoItemMethodNumber, "0")
      .exactlyOne(DispoItemNeedsRerun, "false")
      .exactlyOne(DispoItemNeedsReview, "false")
      .zeroOrOne(DispoItemNotes, "")
      .zeroOrOne(DispoItemPercentComplete, "0%")
      .zeroOrOne(DispoItemStatus, "Unspecified", 3458764513820541337L)
      .zeroOrOne(DispoItemTotalPoints, "0.0")
      .zeroOrOne(DispoItemVersion, "0.0")
      .exactlyOne(DispoItemTeam, "Unassigned"));
   ArtifactTypeToken DispositionSet = dispo.add(dispo.artifactType(807L, "Disposition Set", false, Artifact)
      .exactlyOne(Active, "true")
      .zeroOrOne(DispoCiSet, "")
      .zeroOrOne(DispoConfig, "")
      .zeroOrOne(DispoDateCreated, "")
      .zeroOrOne(DispoImportPath, "")
      .zeroOrOne(DispoImportState, "No Import", 3458764513820541335L)
      .zeroOrOne(DispoIsMultiEnv, "")
      .zeroOrOne(DispoMultiEnvSettings, "")
      .zeroOrOne(DispoNotesJson, "[]")
      .zeroOrOne(DispoOperationSummary, "")
      .zeroOrOne(DispoRerunList, "")
      .zeroOrOne(DispoSummaryCount, "0/0")
      .zeroOrOne(DispoTime, ""));


   ArtifactToken DISPO_ARTIFACT = ArtifactToken.valueOf(4757831, "DispositionTypes", COMMON, CoreArtifactTypes.OseeTypeDefinition);

   String FILE_EXT_REGEX = "dispo.api.file.ext.regex";
   String RESULTS_FILE_EXT_REGEX = "dispo.api.results.file.ext.regex";
   // @formatter:on

}
