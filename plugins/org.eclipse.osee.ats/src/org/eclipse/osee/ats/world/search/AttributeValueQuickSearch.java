/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.world.search;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.EXCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;

/**
 * @author Donald G. Dunne
 */
public class AttributeValueQuickSearch {
   private final Collection<String> values;
   private final AttributeTypeId attributeType;

   public AttributeValueQuickSearch(AttributeTypeId attributeType, Collection<String> values) {
      this.attributeType = attributeType;
      this.values = values;
   }

   /**
    * Will match any quick-search token of given value
    */
   public Collection<Artifact> performSearch() throws OseeCoreException {
      return performSearch(false);
   }

   /**
    * Must match full value of given value
    */
   public Collection<Artifact> performSearch(boolean exactMatch) throws OseeCoreException {
      List<Artifact> results = new ArrayList<>();
      if (values != null && values.size() > 0) {
         for (String value : values) {
            List<Artifact> searchResult = ArtifactQuery.getArtifactListFromAttributeKeywords(
               AtsClientService.get().getAtsBranch(), value, false, EXCLUDE_DELETED, false, attributeType);
            // Since quick search is tokenized, re-validate if exactMatch is desired
            if (exactMatch) {
               for (Artifact artifact : searchResult) {
                  for (String value2 : values) {
                     for (Attribute<Object> attribute : artifact.getAttributes(attributeType)) {
                        if (attribute.convertToStorageString(attribute.getValue()).equals(value2)) {
                           results.add(artifact);
                           break;
                        }
                     }
                  }
               }
            } else {
               results.addAll(searchResult);
            }
         }
      }
      return results;
   }

}