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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

public class BatGroupFile extends BatFile {

   private List<String> configs = new ArrayList<String>();

   public BatGroupFile() {
      // for jax-rs doubt it'll be used
   }

   public BatGroupFile(ArtifactReadable configOrGroup, Map<String, List<String>> namedViewApplicabilityMap, List<ArtifactReadable> featureArts) {
      this.setName(configOrGroup.getName().replace(" ", "_"));
      this.addFeatures(featureArts.stream().map(
         f -> f.getName() + "=" + org.eclipse.osee.framework.jdk.core.util.Collections.toString(",",
            namedViewApplicabilityMap.get(f.getName()))).collect(Collectors.toList()));
      List<ArtifactReadable> configs =
         configOrGroup.getRelated(CoreRelationTypes.PlConfigurationGroup_BranchView).getList();
      this.setConfigs(configs.stream().map(x -> x.getName().replace(" ", "_")).collect(Collectors.toList()));
   }

   /**
    * @return the group
    */
   public List<String> getConfigs() {
      return configs;
   }

   public void setConfigs(List<String> configs) {
      this.configs = configs;
   }

}
