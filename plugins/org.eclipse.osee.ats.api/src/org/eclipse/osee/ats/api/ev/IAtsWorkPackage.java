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
package org.eclipse.osee.ats.api.ev;

import java.util.Date;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.jdk.core.type.Id;
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

         public IAtsWorkPackageSentinel() {
            super(Id.SENTINEL, "SENTINEL");

         }

         @Override
         public boolean isActive() {
            return false;
         }

         @Override
         public ArtifactTypeId getArtifactType() {
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

      }
      return new IAtsWorkPackageSentinel();
   }

}
