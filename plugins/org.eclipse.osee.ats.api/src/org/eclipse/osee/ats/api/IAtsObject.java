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

package org.eclipse.osee.ats.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.HasArtifactType;
import org.eclipse.osee.framework.jdk.core.type.HasDescription;
import org.eclipse.osee.framework.jdk.core.type.NamedId;

/**
 * @author Donald G. Dunne
 */
public interface IAtsObject extends NamedId, HasDescription, HasArtifactType {

   default ArtifactToken getStoreObject() {
      return null;
   }

   default void setStoreObject(ArtifactToken artifact) {
      // do nothing
   }

   @Override
   default String getDescription() {
      return getName();
   }

   default ArtifactId getArtifactId() {
      return ArtifactId.valueOf(getId());
   }

   @JsonIgnore
   default ArtifactToken getArtifactToken() {
      return getStoreObject();
   }

   public AtsApi getAtsApi();

   public Collection<WorkType> getWorkTypes();

   public boolean isWorkType(WorkType workType);

   public Collection<String> getTags();

   public boolean hasTag(String tag);

}