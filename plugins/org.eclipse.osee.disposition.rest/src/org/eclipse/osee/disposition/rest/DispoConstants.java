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

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import javax.ws.rs.core.MediaType;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeBoolean;
import org.eclipse.osee.framework.core.data.AttributeTypeDate;
import org.eclipse.osee.framework.core.data.AttributeTypeEnum;
import org.eclipse.osee.framework.core.data.AttributeTypeString;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.NamespaceToken;
import org.eclipse.osee.framework.core.data.OrcsTokenService;
import org.eclipse.osee.framework.core.data.OrcsTypeTokenProvider;
import org.eclipse.osee.framework.core.data.OrcsTypeTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Angel Avila
 */
public final class DispoConstants implements OrcsTypeTokenProvider {
   private static final OrcsTypeTokens tokens = new OrcsTypeTokens();

   // @formatter:off
   public static final NamespaceToken DISPO = NamespaceToken.valueOf(8, "dispo", "Namespace for dispo system and content management types");

   public static final ArtifactTypeToken DispositionSet = ArtifactTypeToken.valueOf(807, "Disposition Set");
   public static final ArtifactTypeToken DispositionableItem = ArtifactTypeToken.valueOf(808, "Dispositionable Item");

   public static final AttributeTypeString DispoAnnotationsJson = tokens.add(AttributeTypeToken.createString(1152921504606847878L, DISPO, "dispo.Annotations JSON", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoCiSet = tokens.add(AttributeTypeToken.createString(5225296359986133054L, DISPO, "dispo.CI Set", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoConfig = tokens.add(AttributeTypeToken.createString(1152921504606847893L, DISPO, "dispo.Config", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDate DispoDateCreated = tokens.add(AttributeTypeToken.createDate(1152921504606847889L, DISPO, "dispo.Date Created", AttributeTypeToken.TEXT_CALENDAR, ""));
   public static final AttributeTypeString DispoDiscrepanciesJson = tokens.add(AttributeTypeToken.createString(1152921504606847879L, DISPO, "dispo.Discrepancies JSON", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoImportPath = tokens.add(AttributeTypeToken.createString(1152921504606847881L, DISPO, "dispo.Import Path", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum DispoImportState = tokens.add(AttributeTypeToken.createEnum(3458764513820541334L, DISPO, "dispo.Import State", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean DispoIsMultiEnv = tokens.add(AttributeTypeToken.createBoolean(3587620131443940337L, DISPO, "dispo.Is Multi-Env", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean DispoItemAborted = tokens.add(AttributeTypeToken.createBoolean(3458764513820541448L, DISPO, "dispo.item.Aborted", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoItemAssignee = tokens.add(AttributeTypeToken.createString(3458764513820541441L, DISPO, "dispo.item.Assignee", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoItemCategory = tokens.add(AttributeTypeToken.createString(3458764513820541442L, DISPO, "dispo.item.Category", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoItemElapsedTime = tokens.add(AttributeTypeToken.createString(3458764513820541447L, DISPO, "dispo.item.Elapsed Time", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoItemFileNumber = tokens.add(AttributeTypeToken.createString(3458764513820541715L, DISPO, "dispo.item.File Number", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDate DispoItemLastUpdated = tokens.add(AttributeTypeToken.createDate(1152921504606847890L, DISPO, "dispo.item.Last Updated", AttributeTypeToken.TEXT_CALENDAR, ""));
   public static final AttributeTypeString DispoItemMachine = tokens.add(AttributeTypeToken.createString(3458764513820541446L, DISPO, "dispo.item.Machine", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoItemMethodNumber = tokens.add(AttributeTypeToken.createString(3458764513820541460L, DISPO, "dispo.item.Method Number", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean DispoItemNeedsRerun = tokens.add(AttributeTypeToken.createBoolean(3458764513820541444L, DISPO, "dispo.item.Needs Rerun", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeBoolean DispoItemNeedsReview = tokens.add(AttributeTypeToken.createBoolean(2903020690286924090L, DISPO, "dispo.item.Needs Review", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoItemNotes = tokens.add(AttributeTypeToken.createString(3458764513820541456L, DISPO, "dispo.item.Notes", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoItemPercentComplete = tokens.add(AttributeTypeToken.createString(3458764513820541449L, DISPO, "dispo.item.Percent Complete", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeEnum DispoItemStatus = tokens.add(AttributeTypeToken.createEnum(3458764513820541336L, DISPO, "dispo.item.Status", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoItemTeam = tokens.add(AttributeTypeToken.createString(3160880792426011047L, DISPO, "dispo.Team", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoItemTotalPoints = tokens.add(AttributeTypeToken.createString(3458764513820541443L, DISPO, "dispo.item.Total Points", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoItemVersion = tokens.add(AttributeTypeToken.createString(3458764513820541440L, DISPO, "dispo.item.Version", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoMultiEnvSettings = tokens.add(AttributeTypeToken.createString(3587660131047940387L, DISPO, "dispo.Multi-Env Settings", MediaType.APPLICATION_JSON, ""));
   public static final AttributeTypeString DispoNotesJson = tokens.add(AttributeTypeToken.createString(1152921504606847880L, DISPO, "dispo.Notes JSON", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoOperationSummary = tokens.add(AttributeTypeToken.createString(1152921504606847895L, DISPO, "dispo.Operation Summary", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoRerunList = tokens.add(AttributeTypeToken.createString(3587660131087940587L, DISPO, "dispo.Rerun List", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeString DispoSummaryCount = tokens.add(AttributeTypeToken.createString(1152921504606847491L, DISPO, "dispo.Summary Count", MediaType.TEXT_PLAIN, ""));
   public static final AttributeTypeDate DispoTime = tokens.add(AttributeTypeToken.createDate(7240092025387115138L, DISPO, "dispo.Time", AttributeTypeToken.TEXT_CALENDAR, ""));

   public static final ArtifactToken DISPO_ARTIFACT = ArtifactToken.valueOf(4757831, "DispositionTypes", COMMON, CoreArtifactTypes.OseeTypeDefinition);

   public static final String FILE_EXT_REGEX = "dispo.api.file.ext.regex";
   public static final String RESULTS_FILE_EXT_REGEX = "dispo.api.results.file.ext.regex";

   // @formatter:on

   @Override
   public void registerTypes(OrcsTokenService tokenService) {
      tokens.registerTypes(tokenService);
   }

}
