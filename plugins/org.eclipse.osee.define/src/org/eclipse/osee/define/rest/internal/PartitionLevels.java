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

package org.eclipse.osee.define.rest.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author David W. Miller
 */
public class PartitionLevels {
   private List<PartitionLevelsData> data = new ArrayList<>();
   ArtifactReadable partitionArt;

   public void init(OrcsApi orcsApi) {
      Conditions.assertNotNull(orcsApi, "given orcsApi cannot be null");
      ArtifactReadable partitionArt =
         orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON).andId(ArtifactId.valueOf(11508068)).getArtifact();
      Conditions.assertNotNull(partitionArt, "No PartitionLevel Art Found for Art ID 11508068");
      String jsonData = partitionArt.getSoleAttributeAsString(CoreAttributeTypes.Description);
      Conditions.assertNotNullOrEmpty(jsonData, "PartitionArt didn't provide the expected json data");
      ObjectMapper mapper = new ObjectMapper();
      try {
         data = mapper.readValue(jsonData,
            mapper.getTypeFactory().constructCollectionType(List.class, PartitionLevelsData.class));
      } catch (JsonProcessingException ex) {
         throw new OseeCoreException("mapper couldn't map json string in PartitionArt");
      }
   }

   public String getLevelForPartition(List<String> partitions) {
      String max = "C";
      boolean found = false;
      for (String partition : partitions) {
         for (PartitionLevelsData item : data) {
            if (item.getPartition().equals(partition)) {
               max = getMax(item.getLevel(), max);
               found = true;
            }
         }
      }
      if (found) {
         return max;
      }
      return ("BP");
   }

   private String getMax(String level, String current) {
      return current.compareTo(level) > 0 ? level : current;
   }

}