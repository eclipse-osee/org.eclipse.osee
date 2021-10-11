/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.rest.internal.util.health.check;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.util.health.HealthCheckResults;
import org.eclipse.osee.ats.api.util.health.IAtsHealthCheck;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;

/**
 * @author Donald G. Dunne
 */
public class TestDuplicateAttributesWithPersist implements IAtsHealthCheck {

   @Override
   public void check(ArtifactToken artifact, IAtsWorkItem workItem, HealthCheckResults results, AtsApi atsApi, IAtsChangeSet changes) {
      // Test for null attribute values
      for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(workItem)) {
         if (attr.getValue() == null) {
            error(results, workItem, "Error: Types: " + attr.getAttributeType().getName() + " - Null Attribute");
            if (changes != null) {
               changes.deleteAttribute(workItem, attr);
            }
         }
      }

      CountingMap<AttributeTypeToken> typeCount = new CountingMap<>();
      HashCollection<AttributeTypeToken, IAttribute<?>> attrsByType = new HashCollection<>();
      for (IAttribute<?> attr : atsApi.getAttributeResolver().getAttributes(workItem)) {
         typeCount.put(attr.getAttributeType());
         attrsByType.put(attr.getAttributeType(), attr);
      }

      for (Entry<AttributeTypeToken, List<IAttribute<?>>> entry : attrsByType.entrySet()) {
         AttributeTypeToken attrType = entry.getKey();
         // If attr type missing, log separately cause just bad code/db, handle manually
         if (attrType.getName().contains(AttributeTypeToken.MISSING_TYPE)) {
            results.log(workItem.getStoreObject(), getClass().getSimpleName() + ".MissingAttributeType",
               String.format("Error: %s for " + workItem.getAtsId(), attrType.getName()));
            continue;
         }
         ArtifactReadable art = (ArtifactReadable) artifact;
         int count = entry.getValue().size();
         int max = art.getArtifactType().getMax(attrType);
         if (count > art.getArtifactType().getMax(attrType)) {
            String result = String.format("Artifact: %s Type [%s] AttrType [%s] Max [%d] Actual [%d]",
               artifact.toStringWithId(), art.getArtifactType().getName(), attrType.getName(), max, count);
            Map<String, IAttribute<?>> valuesAttrMap = new HashMap<>();
            GammaId latestGamma = GammaId.valueOf(0);
            StringBuffer fixInfo = new StringBuffer(" - FIX AVAILABLE");
            for (IAttribute<?> attr2 : entry.getValue()) {
               AttributeReadable<?> attr = (AttributeReadable<?>) attr2;
               if (attr.getGammaId().isValid()) {
                  latestGamma = attr.getGammaId();
               }
               String info = String.format("[Gamma [%s] Value [%s]]", attr.getGammaId(), attr.getValue());
               valuesAttrMap.put(info, attr);
               fixInfo.append(info);
            }
            fixInfo.append(" - KEEP Gamma ");
            fixInfo.append(latestGamma);
            if (latestGamma.isValid()) {
               result += fixInfo;
               if (changes != null) {
                  for (IAttribute<?> attr2 : entry.getValue()) {
                     AttributeReadable<?> attr = (AttributeReadable<?>) attr2;
                     if (attr.getGammaId().notEqual(latestGamma)) {
                        changes.deleteAttribute(workItem, attr);
                     }
                  }
                  result += result + " - Fixed";
               }
            }
            error(results, workItem, result);
         }
      }
   }

}
