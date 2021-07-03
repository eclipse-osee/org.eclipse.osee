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

package org.eclipse.osee.ats.api.workdef;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.IAtsObject;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;

/**
 * @author Donald G. Dunne
 */
public interface IAttributeResolver {

   default ArtifactId getSoleArtifactIdReference(IAtsObject atsObject, AttributeTypeToken artifactReferencedAttributeType, ArtifactId defaultValue) {
      return getSoleAttributeValue(atsObject, artifactReferencedAttributeType, ArtifactId.SENTINEL);
   }

   default ArtifactId getSoleArtifactIdReference(ArtifactToken art, AttributeTypeToken artifactReferencedAttributeType, ArtifactId defaultValue) {
      return getSoleAttributeValue(art, artifactReferencedAttributeType, defaultValue);
   }

   default Collection<ArtifactId> getArtifactIdReferences(ArtifactToken artifact, AttributeTypeToken artifactReferencedAttributeType) {
      return getAttributeValues(artifact, artifactReferencedAttributeType);
   }

   <T> T getSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, T defaultReturnValue);

   List<String> getAttributesToStringList(IAtsObject atsObject, AttributeTypeToken attributeType);

   boolean isAttributeTypeValid(IAtsWorkItem workItem, AttributeTypeToken attributeType);

   String getSoleAttributeValueAsString(IAtsObject atsObject, AttributeTypeToken attributeType, String defaultReturnValue);

   int getAttributeCount(IAtsObject atsObject, AttributeTypeToken attributeType);

   int getAttributeCount(ArtifactId artifact, AttributeTypeToken attributeType);

   void addAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType, Object value);

   <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact);

   <T> Collection<IAttribute<T>> getAttributes(IAtsWorkItem workItem);

   <T> Collection<IAttribute<T>> getAttributes(IAtsObject atsObject, AttributeTypeToken attributeType);

   <T> Collection<IAttribute<T>> getAttributes(ArtifactId artifact, AttributeTypeToken attributeType);

   void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeId attributeType);

   <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr);

   <T> void setValue(IAtsWorkItem workItem, IAttribute<String> attr, AttributeTypeId attributeType, T value);

   void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, IAtsChangeSet changes);

   void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes);

   void addAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes);

   void deleteSoleAttribute(IAtsWorkItem workItem, AttributeTypeToken attributeType, Object value, IAtsChangeSet changes);

   <T> void setValue(IAtsWorkItem workItem, IAttribute<T> attr, AttributeTypeId attributeType, T value, IAtsChangeSet changes);

   <T> void deleteAttribute(IAtsWorkItem workItem, IAttribute<T> attr, IAtsChangeSet changes);

   void setSoleAttributeValue(IAtsObject atsObject, AttributeTypeId attributeType, Object value);

   <T> T getSoleAttributeValue(ArtifactId artifact, AttributeTypeToken attributeType, T defaultValue);

   <T> Collection<T> getAttributeValues(ArtifactId artifact, AttributeTypeToken attributeType);

   <T> Collection<T> getAttributeValues(IAtsObject atsObject, AttributeTypeToken attributeType);

   String getSoleAttributeValueAsString(ArtifactId artifact, AttributeTypeToken attributeType, String defaultReturnValue);

   int getAttributeCount(IAtsWorkItem workItem, AttributeTypeToken attributeType);

   default public String getAttributesToStringUniqueList(IAtsObject atsObject, AttributeTypeToken attributeType, String separator) {
      Set<String> strs = new HashSet<>();
      strs.addAll(getAttributesToStringList(atsObject, attributeType));
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString(separator, strs);
   }

   List<String> getAttributesToStringList(ArtifactId artifact, AttributeTypeToken attributeType);

   List<String> getAttributesToStringListFromArt(ArtifactToken artifact, AttributeTypeToken attributeType);

   List<String> getAttributesToStringListFromArt(ArtifactToken artifact, AttributeTypeToken attributeType, DeletionFlag deletionFlag);

   /**
    * @return value in static id field that starts with key=; key= will be stripped off string and remaining returned
    */
   String getStaticIdValue(IAtsWorkItem workItem, String key, String defaultValue);

   /**
    * @return set/update static id in format of key=value
    */
   void setStaticIdValue(IAtsWorkItem workItem, String key, String value, IAtsChangeSet changes);

   default boolean hasTag(ArtifactToken art, String tag) {
      return getAttributesToStringList(art, CoreAttributeTypes.StaticId).contains(tag);
   }

}