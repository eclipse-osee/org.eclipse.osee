/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.api.program;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsConfigObject;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;

/**
 * @author Donald G. Dunne
 */
public interface IAtsProgram extends IAtsConfigObject {
   // do nothing

   IAtsProgram SENTINEL = createSentinel();

   public String getClosureState();

   public static IAtsProgram createSentinel() {
      final class IAtsProgramSentinel extends NamedIdBase implements IAtsProgram {

         @Override
         public boolean isActive() {
            return false;
         }

         @Override
         public ArtifactTypeToken getArtifactType() {
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

         @Override
         public String getClosureState() {
            return "";
         }

      }
      return new IAtsProgramSentinel();
   }

}
