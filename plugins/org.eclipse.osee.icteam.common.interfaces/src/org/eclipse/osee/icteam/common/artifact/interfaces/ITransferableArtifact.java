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
package org.eclipse.osee.icteam.common.artifact.interfaces;

import com.google.common.base.Function;
import java.util.List;
import java.util.Map;

/**
 * This interface has method signatures related to user assigned for a task
 *
 * @author Ajay Chandrahasan
 */
public interface ITransferableArtifact {

   /**
    * @return the modified Date
    */
   public String getModifiedDate();

   /**
    * @param modifiedDate the modifiedDate to set
    */
   public void setModifiedDate(final String modifiedDate);

   /**
    * @return the modifiedBy
    */
   public String getModifiedBy();

   /**
    * @param modifiedBy the modifiedBy to set
    */
   public void setModifiedBy(final String modifiedBy);

   /**
    * @return the version
    */
   public String getRevision();

   /**
    * @param version the version to set
    */
   public void setRevision(final String version);

   /**
    * @return the userId
    */
   public String getUserId();

   /**
    * @param userId the userId to set
    */
   public void setUserId(final String userId);

   /**
    * @return the canEdit
    */
   public boolean isCanEdit();

   /**
    * @param canEdit the canEdit to set
    */
   public void setCanEdit(final boolean canEdit);

   /**
    * @return the branch GUID
    */
   public String getBranchGuid();

   /**
    * @param branchGuid to set
    */
   public void setBranchGuid(final Long branchGuid);

   /**
    * @return the guid
    */
   public String getUuid();

   /**
    * @return the artifact type
    */
   public String getArtifactType();

   /**
    * @param artifactType to set
    */
   public void setArtifactType(final String artifactType);

   /**
    * @param key to get value
    * @return the values
    */
   public List<String> getAttributes(final String key);

   /**
    * @param key to get value
    * @param defaultValue if value is not there
    * @return the value
    */
   public List<String> getAttributesOrElse(final String key, final List<String> defaultValue);

   /**
    * @param key to get value
    * @return the values
    */
   public List<String> getAttributesOrElse(final String key);

   /**
    * @param key to store
    * @param value to store
    */
   public void putAttributes(final String key, final List<String> value);

   /**
    * @param key relation type
    * @param value artifact to relate
    */
   public void putRelations(final String key, final List<ITransferableArtifact> value);

   /**
    * @param key t split
    * @return the split
    */
   public String[] splitKey(final String key);

   /**
    * @return the relation map
    */
   public Map<String, List<ITransferableArtifact>> getRelationMap();

   /**
    * @param id to set
    */
   public void setId(final Long id);

   /**
    * @return the relation map sides
    */
   public Map<String, List<ITransferableArtifact>> getRelationMapWithSides();

   /**
    * @param key String for which the results are given
    * @return {@link List} obtained as result Or null
    */
   public List<ITransferableArtifact> getRelatedArtifacts(final String key);

   /**
    * @return the name
    */
   public String getName();

   /**
    * @param name to set
    */
   public void setName(final String name);

   /**
    * @return the parent guid
    */
   public String getParentGuid();

   /**
    * @param parentGuid to set
    */
   public void setParentGuid(final String parentGuid);

   /**
    * @return the attribute map
    */
   public Map<String, List<String>> getAttributes();

   /**
    * @return the artifact type name
    */
   public String getArtifactTypeName();

   /**
    * @param artifactTypeName to set
    */
   public void setArtifactTypeName(final String artifactTypeName);

   /**
    * @param colsToHide to set
    */
   public void setcolumnsToHide(final Integer[] colsToHide);

   /**
    * @return the coloumnsToHide
    */
   public Integer[] getColoumnsToHide();

   /**
    * @return the metaInfo. This map value cannot be mutated
    */
   public Map<String, ? extends Object> getMetaInfo();

   /**
    * @param metaData the metaData to set
    */
   public void setMetaInfo(final Map<String, ? extends Object> metaData);

   /**
    * @param localId to set
    */
   public void setLocalId(final int localId);

   /**
    * @return the localId
    */
   public int getLocalId();

   /**
    * @param key to set
    * @param element artifact to remove
    */
   public void removeFromRelated(final String key, final ITransferableArtifact element);

   /**
    * @return the isInvalidated
    */
   public boolean isInvalidated();

   /**
    * @param isInvalidated the isInvalidated to set
    */
   public void setInvalidated(final boolean isInvalidated);

   /**
    * @return : current logged in user
    */
   public String getCurrentLoggedInUser();

   /**
    * @param userIdRetreiverFromTransferableArtifct : userId Retreived from TransferableArtifct
    * @return :User ID
    */
   public String getUserId(Function<ITransferableArtifact, String> userIdRetreiverFromTransferableArtifct);

}
