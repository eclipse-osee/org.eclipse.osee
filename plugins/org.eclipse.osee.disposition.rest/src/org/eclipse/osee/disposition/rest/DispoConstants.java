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
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Angel Avila
 */
public final class DispoConstants {

   // @formatter:off
   public static final IArtifactType DispoSet = TokenFactory.createArtifactType(807, "Disposition Set");
   public static final IArtifactType DispoItem = TokenFactory.createArtifactType(808, "Dispositionable Item");

   public static final AttributeTypeToken DispoType= AttributeTypeToken.valueOf(1152921504606847893L, "dispo.Dispo Config");
   public static final AttributeTypeToken ImportPath= AttributeTypeToken.valueOf(1152921504606847881L, "dispo.Import Path");
   public static final AttributeTypeToken ImportState= AttributeTypeToken.valueOf(3458764513820541334L, "dispo.Import State");
   public static final AttributeTypeToken OperationSummary= AttributeTypeToken.valueOf(1152921504606847895L, "dispo.Operation Summary");
   public static final AttributeTypeToken DispoAnnotationsJson = AttributeTypeToken.valueOf(1152921504606847878L, "dispo.Annotations JSON");
   public static final AttributeTypeToken DispoDiscrepanciesJson = AttributeTypeToken.valueOf(1152921504606847879L, "dispo.Discrepancies JSON");
   public static final AttributeTypeToken DispoNotesJson = AttributeTypeToken.valueOf(1152921504606847880L, "dispo.Notes JSON");
   public static final AttributeTypeToken DispoDateCreated = AttributeTypeToken.valueOf(1152921504606847889L, "dispo.Date Created");
   public static final AttributeTypeToken DispoLastUpdated = AttributeTypeToken.valueOf(1152921504606847890L, "dispo.Last Updated");
   public static final AttributeTypeToken DispoItemStatus = AttributeTypeToken.valueOf(3458764513820541336L, "dispo.Item Status");
   public static final AttributeTypeToken DispoItemTotalPoints = AttributeTypeToken.valueOf(3458764513820541443L, "dispo.Total Points");
   public static final AttributeTypeToken DispoItemNeedsRerun = AttributeTypeToken.valueOf(3458764513820541444L, "dispo.Needs Rerun");
   public static final AttributeTypeToken DispoItemVersion = AttributeTypeToken.valueOf(3458764513820541440L, "dispo.Item Version");
   public static final AttributeTypeToken DispoItemAssignee = AttributeTypeToken.valueOf(3458764513820541441L, "dispo.Assignee");
   public static final AttributeTypeToken DispoItemCategory = AttributeTypeToken.valueOf(3458764513820541442L, "dispo.Category");
   public static final AttributeTypeToken DispoItemMachine = AttributeTypeToken.valueOf(3458764513820541446L, "dispo.Machine");
   public static final AttributeTypeToken DispoItemElapsedTime = AttributeTypeToken.valueOf(3458764513820541447L, "dispo.Elapsed Time");
   public static final AttributeTypeToken DispoItemAborted = AttributeTypeToken.valueOf(3458764513820541448L, "dispo.Aborted");
   public static final AttributeTypeToken DispoItemItemNotes = AttributeTypeToken.valueOf(3458764513820541456L, "dispo.Item Notes");
   public static final AttributeTypeToken DispoItemNeedsReview = AttributeTypeToken.valueOf(3458764513820541458L, "dispo.Needs Review");
   public static final AttributeTypeToken DispoItemTeam = AttributeTypeToken.valueOf(3160880792426011047L, "dispo.Team");
   public static final AttributeTypeToken DispoItemFileNumber = AttributeTypeToken.valueOf(3458764513820541715L, "dispo.File Number");
   public static final AttributeTypeToken DispoItemMethodNumber = AttributeTypeToken.valueOf(3458764513820541460L, "dispo.Method Number");
   public static final AttributeTypeToken DispoCiSet = AttributeTypeToken.valueOf(5225296359986133054L, "dispo.Ci Set");
   public static final AttributeTypeToken DispoRerunList = AttributeTypeToken.valueOf(3587660131087940587L, "dispo.Rerun List");
   public static final AttributeTypeToken DispoTime = AttributeTypeToken.valueOf(7240092025387115138L, "dispo.Time");


   public static final ArtifactToken DISPO_ARTIFACT = ArtifactToken.valueOf(4757831, "DispositionTypes", COMMON, CoreArtifactTypes.OseeTypeDefinition);

   // For dispo config
   public static final String NAMESPACE = "dispo.api";
   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final String FILE_EXT_REGEX = qualify("file.ext.regex");

   // @formatter:on

   private DispoConstants() {
      // Constants
   }

}
