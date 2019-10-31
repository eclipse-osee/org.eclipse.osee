/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.change;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.LoadChangeType;

/**
 * @author Megumi Telles
 */
public final class ErrorChange extends Change {
   private static final String ERROR_STRING = "!Error -";

   private final String errorMessage;
   private final String name;

   public ErrorChange(BranchId branch, ArtifactId artId, String exception) {
      super(branch, GammaId.valueOf(0L), artId, null, null, false, Artifact.SENTINEL, null);
      this.errorMessage = String.format("%s %s", ERROR_STRING, exception);
      this.name = String.format("%s ArtID: %s BranchUuid: %s - %s", ERROR_STRING, getArtId(),
         branch == null ? null : branch.getIdString(), exception);
   }

   @Override
   public String getIsValue() {
      return errorMessage;
   }

   @Override
   public ArtifactId getItemId() {
      return ArtifactId.valueOf(0L);
   }

   @Override
   public String getItemKind() {
      return errorMessage;
   }

   @Override
   public Id getItemTypeId() {
      return ArtifactTypeId.SENTINEL;
   }

   @Override
   public String getItemTypeName() {
      return errorMessage;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getWasValue() {
      return errorMessage;
   }

   @Override
   public LoadChangeType getChangeType() {
      return null;
   }
}
