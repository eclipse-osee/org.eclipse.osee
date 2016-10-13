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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;

/**
 * @author Angel Avila
 */
public final class DispoConstants {

   // @formatter:off
   public static final IArtifactType DispoSet = TokenFactory.createArtifactType(807, "Disposition Set");
   public static final IArtifactType DispoItem = TokenFactory.createArtifactType(808, "Dispositionable Item");

   public static final IAttributeType DispoType= TokenFactory.createAttributeType(1152921504606847893L, "dispo.Dispo Config");
   public static final IAttributeType ImportPath= TokenFactory.createAttributeType(1152921504606847881L, "dispo.Import Path");
   public static final IAttributeType ImportState= TokenFactory.createAttributeType(3458764513820541334L, "dispo.Import State");
   public static final IAttributeType OperationSummary= TokenFactory.createAttributeType(1152921504606847895L, "dispo.Operation Summary");
   public static final IAttributeType DispoAnnotationsJson = TokenFactory.createAttributeType(1152921504606847878L, "dispo.Annotations JSON");
   public static final IAttributeType DispoDiscrepanciesJson = TokenFactory.createAttributeType(1152921504606847879L, "dispo.Discrepancies JSON");
   public static final IAttributeType DispoNotesJson = TokenFactory.createAttributeType(1152921504606847880L, "dispo.Notes JSON");
   public static final IAttributeType DispoDateCreated = TokenFactory.createAttributeType(1152921504606847889L, "dispo.Date Created");
   public static final IAttributeType DispoLastUpdated = TokenFactory.createAttributeType(1152921504606847890L, "dispo.Last Updated");
   public static final IAttributeType DispoItemStatus = TokenFactory.createAttributeType(3458764513820541336L, "dispo.Item Status");
   public static final IAttributeType DispoItemTotalPoints = TokenFactory.createAttributeType(3458764513820541443L, "dispo.Total Points");
   public static final IAttributeType DispoItemNeedsRerun = TokenFactory.createAttributeType(3458764513820541444L, "dispo.Needs Rerun");
   public static final IAttributeType DispoItemVersion = TokenFactory.createAttributeType(3458764513820541440L, "dispo.Item Version");
   public static final IAttributeType DispoItemAssignee = TokenFactory.createAttributeType(3458764513820541441L, "dispo.Assignee");
   public static final IAttributeType DispoItemCategory = TokenFactory.createAttributeType(3458764513820541442L, "dispo.Category");
   public static final IAttributeType DispoItemMachine = TokenFactory.createAttributeType(3458764513820541446L, "dispo.Machine");
   public static final IAttributeType DispoItemElapsedTime = TokenFactory.createAttributeType(3458764513820541447L, "dispo.Elapsed Time");
   public static final IAttributeType DispoItemAborted = TokenFactory.createAttributeType(3458764513820541448L, "dispo.Aborted");
   public static final IAttributeType DispoItemItemNotes = TokenFactory.createAttributeType(3458764513820541456L, "dispo.Item Notes");
   public static final IAttributeType DispoItemNeedsReview = TokenFactory.createAttributeType(3458764513820541458L, "dispo.Needs Review");
   public static final IAttributeType DispoItemFileNumber = TokenFactory.createAttributeType(3458764513820541715L, "dispo.File Number");
   public static final IAttributeType DispoItemMethodNumber = TokenFactory.createAttributeType(3458764513820541460L, "dispo.Method Number");


   public static final ArtifactToken DISPO_ARTIFACT = ArtifactToken.valueOf(4757831, "DispositionTypes", COMMON, CoreArtifactTypes.OseeTypeDefinition);

   // @formatter:on

   private DispoConstants() {
      // Constants
   }

}
