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

package org.eclipse.osee.ats.api.ev;

import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorkPackage extends IAtsConfigObject {

   public String getActivityId();

   public String getActivityName();

   public String getWorkPackageId();

   public String getWorkPackageProgram();

   public AtsWorkPackageType getWorkPackageType();

   public int getWorkPackagePercent();

   public Date getStartDate();

   public Date getEndDate();

   IAtsWorkPackage SENTINEL = createSentinel();

   public static IAtsWorkPackage createSentinel() {
      final class IAtsWorkPackageSentinel extends NamedIdBase implements IAtsWorkPackage {

         @Override
         public boolean isActive() {
            return false;
         }

         @Override
         public ArtifactTypeToken getArtifactType() {
            return null;
         }

         @Override
         public String getActivityId() {
            return null;
         }

         @Override
         public String getActivityName() {
            return null;
         }

         @Override
         public String getWorkPackageId() {
            return null;
         }

         @Override
         public String getWorkPackageProgram() {
            return null;
         }

         @Override
         public AtsWorkPackageType getWorkPackageType() {
            return null;
         }

         @Override
         public int getWorkPackagePercent() {
            return 0;
         }

         @Override
         public Date getStartDate() {
            return null;
         }

         @Override
         public Date getEndDate() {
            return null;
         }

         @Override
         public AtsApi getAtsApi() {
            return null;
         }

         @Override
         public Collection<WorkType> getWorkTypes() {
            return null;
         }

         @Override
         public boolean isWorkType(WorkType workType) {
            return false;
         }

         @Override
         public Collection<String> getTags() {
            return null;
         }

         @Override
         public boolean hasTag(String tag) {
            return false;
         }

      }
      return new IAtsWorkPackageSentinel();
   }

}
