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
package org.eclipse.osee.ats.core.model;

import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageType;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.ats.core.model.impl.AtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.logger.Log;

/**
 * Wrapper class for the Work Package storage artifact
 *
 * @author Donald G. Dunne
 */
public class WorkPackage extends AtsConfigObject implements IAtsWorkPackage {

   private ArtifactToken artifact;
   private final AtsApi atsApi;

   public WorkPackage(Log logger, AtsApi atsApi, ArtifactToken artifact) {
      super(logger, atsApi, artifact, AtsArtifactTypes.WorkPackage);
      this.artifact = artifact;
      this.atsApi = atsApi;
   }

   @Override
   public String getActivityId() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.ActivityId, "");
   }

   @Override
   public String getActivityName() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.ActivityName, "");
   }

   @Override
   public String getName() {
      return artifact.getName();
   }

   @Override
   public String getWorkPackageId() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.WorkPackageId, "");
   }

   @Override
   public String getWorkPackageProgram() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.WorkPackageProgram, "");
   }

   @Override
   public AtsWorkPackageType getWorkPackageType() {
      String value =
         atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.WorkPackageType, "");
      AtsWorkPackageType type = AtsWorkPackageType.None;
      if (Strings.isValid(value)) {
         try {
            type = AtsWorkPackageType.valueOf(value);
            return type;
         } catch (Exception ex) {
            // do nothing
         }
      }
      return type;
   }

   @Override
   public boolean isActive() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.Active, true);
   }

   @Override
   public Date getStartDate() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.StartDate, null);
   }

   @Override
   public Date getEndDate() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.EndDate, null);
   }

   @Override
   public int getWorkPackagePercent() {
      return atsApi.getAttributeResolver().getSoleAttributeValue(artifact, AtsAttributeTypes.PercentComplete, 0);
   }

   @Override
   public String toString() {
      try {
         StringBuilder builder = new StringBuilder(getActivityId());
         addWithHypen(builder, getActivityName());
         addWithHypen(builder, getWorkPackageId());
         addWithHypen(builder, getName());
         return builder.toString();
      } catch (OseeCoreException ex) {
         OseeLog.log(WorkPackage.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return String.format("%s - Exception (see log file)", getName());
   }

   private void addWithHypen(StringBuilder builder, String value) {
      if (Strings.isValid(value)) {
         builder.append(" - ");
         builder.append(value);
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getId().hashCode();
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      IAtsWorkPackage other = (IAtsWorkPackage) obj;
      if (getId() == null) {
         if (other.getId() != null) {
            return false;
         }
      } else if (!getId().equals(other.getId())) {
         return false;
      }
      return true;
   }

   @Override
   public Long getId() {
      return artifact.getId();
   }

   @Override
   public ArtifactToken getStoreObject() {
      return artifact;
   }

   @Override
   public void setStoreObject(ArtifactToken artifact) {
      this.artifact = artifact;
   }

   @Override
   public int compareTo(Named other) {
      return artifact.compareTo(other);
   }
}