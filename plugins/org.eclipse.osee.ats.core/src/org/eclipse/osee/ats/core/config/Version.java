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
package org.eclipse.osee.ats.core.config;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Donald G. Dunne
 */
public class Version extends AtsConfigObject implements IAtsVersion {

   public Version(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact);
   }

   @Override
   public String getCommitFullDisplayName() {
      List<String> strs = new ArrayList<>();
      strs.add(getName());
      String fullName = atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.FullName, "");
      if (Strings.isValid(fullName)) {
         strs.add(fullName);
      }
      String description =
         atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.Description, "");
      if (Strings.isValid(description)) {
         strs.add(description);
      }
      return Collections.toString(" - ", strs);
   }

   @Override
   public Result isAllowCreateBranchInherited() {
      if (!isAllowCreateBranch()) {
         return new Result(false, "Branch creation disabled for Version [" + this + "]");
      }
      if (!atsApi.getBranchService().isBranchValid(this)) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public boolean isAllowCreateBranch() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AllowCreateBranch, false);
   }

   @Override
   public boolean isAllowCommitBranch() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.AllowCommitBranch, false);
   }

   @Override
   public Result isAllowCommitBranchInherited() {
      if (!isAllowCommitBranch()) {
         return new Result(false, "Version [" + this + "] not configured to allow branch commit.");
      }
      if (!atsApi.getBranchService().isBranchValid(this)) {
         return new Result(false, "Parent Branch not configured for Version [" + this + "]");
      }
      return Result.TrueResult;
   }

   @Override
   public Date getReleaseDate() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.ReleaseDate, null);
   }

   @Override
   public Boolean isReleased() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.Released, false);
   }

   @Override
   public Date getEstimatedReleaseDate() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.EstimatedReleaseDate,
         (Date) null);
   }

   @Override
   public boolean isLocked() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.VersionLocked, false);
   }

   @Override
   public Boolean isVersionLocked() {
      return isLocked();
   }

   @Override
   public Boolean isNextVersion() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.NextVersion, false);
   }

   @Override
   public String getTypeName() {
      return "Version";
   }

   @Override
   public BranchId getBaselineBranchIdInherited() {
      if (getBaselineBranchId().isValid()) {
         return getBaselineBranchId();
      } else {
         try {
            IAtsTeamDefinition teamDef = atsApi.getVersionService().getTeamDefinition(this);
            if (teamDef != null) {
               return teamDef.getTeamBranchId();
            } else {
               return BranchId.SENTINEL;
            }
         } catch (OseeCoreException ex) {
            return BranchId.SENTINEL;
         }
      }
   }

   @Override
   public BranchId getBaselineBranchId() {
      return BranchId.valueOf(
         atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.BaselineBranchUuid, "-1"));
   }

}
