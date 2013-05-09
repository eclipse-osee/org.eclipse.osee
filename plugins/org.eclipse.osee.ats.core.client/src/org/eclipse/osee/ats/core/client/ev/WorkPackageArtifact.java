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
package org.eclipse.osee.ats.core.client.ev;

import java.util.Date;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.ev.AtsWorkPackageType;
import org.eclipse.osee.ats.api.ev.IAtsWorkPackage;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Wrapper class for the Work Package storage artifact
 * 
 * @author Donald G. Dunne
 */
public class WorkPackageArtifact implements IAtsWorkPackage {

   private final Artifact artifact;

   public WorkPackageArtifact(Artifact artifact) {
      this.artifact = artifact;
   }

   @Override
   public String getActivityId() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.ActivityId, "");
   }

   @Override
   public String getActivityName() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.ActivityName, "");
   }

   @Override
   public String getName() {
      return artifact.getName();
   }

   @Override
   public String getGuid() {
      return artifact.getGuid();
   }

   @Override
   public String getWorkPackageId() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.WorkPackageId, "");
   }

   @Override
   public String getWorkPackageProgram() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.WorkPackageProgram, "");
   }

   @Override
   public AtsWorkPackageType getWorkPackageType() throws OseeCoreException {
      String value = artifact.getSoleAttributeValue(AtsAttributeTypes.WorkPackageType, "");
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
   public boolean isActive() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.Active, true);
   }

   @Override
   public Date getStartDate() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.StartDate, null);
   }

   @Override
   public Date getEndDate() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.EndDate, null);
   }

   @Override
   public int getWorkPackagePercent() throws OseeCoreException {
      return artifact.getSoleAttributeValue(AtsAttributeTypes.PercentComplete, 0);
   }

   @Override
   public String toString() {
      try {
         return String.format("%s - %s", getWorkPackageId(), getName());
      } catch (OseeCoreException ex) {
         OseeLog.log(org.eclipse.osee.ats.core.client.internal.Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return String.format("%s - Exception (see log file)", getName());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((getGuid() == null) ? 0 : getGuid().hashCode());
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
      if (getGuid() == null) {
         if (other.getGuid() != null) {
            return false;
         }
      } else if (!getGuid().equals(other.getGuid())) {
         return false;
      }
      return true;
   }

}
