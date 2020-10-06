/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.common.interfaces.factory;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;

/**
 * Factory class for different artifacts
 *
 * @author Ajay Chandrahasan
 */
public class TransferableArtifactFactory {

   /**
    * @return the transferable artifact
    */
   public static ITransferableArtifact createArtifact() {

      ITransferableArtifact artifact = new ITransferableArtifact() {

         public String name;
         public String parentGuid;
         public String guid;
         public final Map<String, List<String>> attributeMap = new ConcurrentHashMap<String, List<String>>();
         public Map<String, ? extends Object> metaInfo = new HashMap<String, Object>();
         public final Map<String, List<ITransferableArtifact>> relationMap =
            new ConcurrentHashMap<String, List<ITransferableArtifact>>();
         public String branchGuid;
         public boolean canEdit;
         public String userId;
         public String artifactType;
         public String artifactTypeName;
         public String modifiedDate;

         public String modifiedBy;
         public String revision;
         public Integer[] coloumnsToHide;
         public int localId;
         public boolean isInvalidated = false;
         public boolean includeNonRequirements = true;
         public static final String RELATION_MAP_KEY_SEPARATOR = "##";

         /**
          * {@inheritDoc}
          */
         @Override
         public String getModifiedDate() {
            return this.modifiedDate;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setModifiedDate(final String modifiedDate) {
            this.modifiedDate = modifiedDate;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String getModifiedBy() {
            return this.modifiedBy;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setModifiedBy(final String modifiedBy) {
            this.modifiedBy = modifiedBy;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String getRevision() {
            return this.revision;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setRevision(final String version) {
            this.revision = version;
         }

         /**
          * @param userIdRetreiver function which can help in getting userId
          * @return the userId
          */
         @Override
         public String getUserId(final Function<ITransferableArtifact, String> userIdRetreiver) {
            // return !Strings.isNullOrEmpty(this.userId) ? this.userId
            // : userIdRetreiver.apply(this);
            return "";
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setUserId(final String userId) {
            this.userId = userId;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public boolean isCanEdit() {
            return this.canEdit;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setCanEdit(final boolean canEdit) {
            this.canEdit = canEdit;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String getBranchGuid() {
            return this.branchGuid;
         }

         /**
          * {@inheritDoc}
          */
         public void setBranchGuid(final String branchGuid) {
            this.branchGuid = branchGuid;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String getUuid() {
            return this.guid;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String getArtifactType() {
            return this.artifactType;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setArtifactType(final String artifactType) {
            this.artifactType = artifactType;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public List<String> getAttributes(final String key) {
            if (this.attributeMap.containsKey(key)) {
               return this.attributeMap.get(key);
            }
            return null;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public List<String> getAttributesOrElse(final String key, final List<String> defaultValue) {
            List<String> attributes = getAttributes(key);
            if (attributes != null) {
               return attributes;
            }
            return defaultValue;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public List<String> getAttributesOrElse(final String key) {
            return getAttributesOrElse(key, Arrays.asList(""));
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void putAttributes(final String key, final List<String> value) {
            this.attributeMap.put(key, value);
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void putRelations(final String key, final List<ITransferableArtifact> value) {
            this.relationMap.put(key, value);
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String[] splitKey(final String key) {
            return key.split(RELATION_MAP_KEY_SEPARATOR);
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public Map<String, List<ITransferableArtifact>> getRelationMap() {
            Map<String, List<ITransferableArtifact>> relations = new HashMap<>();
            Set<Entry<String, List<ITransferableArtifact>>> entrySet = this.relationMap.entrySet();
            for (Entry<String, List<ITransferableArtifact>> entry : entrySet) {
               String[] splitKey = splitKey(entry.getKey());
               relations.put(splitKey[0], entry.getValue());
            }
            return relations;
         }

         /**
          * {@inheritDoc}
          */
         public void setGuid(final String guid) {
            this.guid = guid;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public Map<String, List<ITransferableArtifact>> getRelationMapWithSides() {
            return this.relationMap;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public List<ITransferableArtifact> getRelatedArtifacts(final String key) {
            Map<String, List<ITransferableArtifact>> relationMap2 = getRelationMap();
            if (relationMap2.containsKey(key)) {
               return relationMap2.get(key);
            }
            // do not change the return value here
            return null;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String getName() {
            return this.name;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setName(final String name) {
            this.name = name;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String getParentGuid() {
            return this.parentGuid;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setParentGuid(final String parentGuid) {
            this.parentGuid = parentGuid;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public Map<String, List<String>> getAttributes() {
            return this.attributeMap;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String getArtifactTypeName() {
            return this.artifactTypeName;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setArtifactTypeName(final String artifactTypeName) {
            this.artifactTypeName = artifactTypeName;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setcolumnsToHide(final Integer[] colsToHide) {
            this.coloumnsToHide = colsToHide;

         }

         /**
          * {@inheritDoc}
          */
         @Override
         public Integer[] getColoumnsToHide() {
            return this.coloumnsToHide;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public Map<String, ? extends Object> getMetaInfo() {
            return ImmutableMap.copyOf(this.metaInfo);
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setMetaInfo(final Map<String, ? extends Object> metaData) {
            this.metaInfo = metaData;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String toString() {
            return this.name + "-Branch@" + this.branchGuid + "::" + "Guid@" + this.guid;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setLocalId(final int localId) {
            this.localId = localId;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public int getLocalId() {
            return this.localId;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void removeFromRelated(final String key, final ITransferableArtifact element) {
            getRelatedArtifacts(key).remove(element);
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public boolean isInvalidated() {
            return this.isInvalidated;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setInvalidated(final boolean isInvalidated) {
            this.isInvalidated = isInvalidated;
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String getUserId() {
            return "";
         }

         /**
          * {@inheritDoc}
          */
         @Override
         public String getCurrentLoggedInUser() {
            return null;
         }

         /**
          * {@inheritDoc}
          */

         @Override
         public void setBranchGuid(final Long branchGuid) {
            // TODO Auto-generated method stub

         }

         /**
          * {@inheritDoc}
          */
         @Override
         public void setId(final Long id) {
            // TODO Auto-generated method stub

         }
      };

      return artifact;
   }

}
