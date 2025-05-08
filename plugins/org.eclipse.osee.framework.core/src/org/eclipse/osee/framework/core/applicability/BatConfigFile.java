/*********************************************************************
 * Copyright (c) 2024 Boeing
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
package org.eclipse.osee.framework.core.applicability;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

public class BatConfigFile extends BatFile {

   private String group = "";

   public BatConfigFile() {
      // for jax-rs doubt it'll be used
   }

   public BatConfigFile(ArtifactReadable configOrGroup, Map<String, List<String>> namedViewApplicabilityMap, List<ArtifactReadable> featureArts) {
      this.setName(configOrGroup.getName().replace(" ", "_"));
      this.addFeatures(featureArts.stream().map(
         f -> f.getName() + "=" + org.eclipse.osee.framework.jdk.core.util.Collections.toString(",",
            namedViewApplicabilityMap.get(f.getName()))).collect(Collectors.toList()));
      ArtifactReadable group = configOrGroup.getRelated(CoreRelationTypes.PlConfigurationGroup_Group).getOneOrDefault(
         ArtifactReadable.SENTINEL);
      if (group.isValid()) {

         this.setGroup(group.getName());
      }
   }

   /**
    * @return the group
    */
   public String getGroup() {
      return group;
   }

   public void setGroup(String group) {
      this.group = group;
   }

}
