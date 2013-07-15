/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.mocks;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.Identity;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.ResultSetList;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author John R. Misinco
 */
public class MockArtifact implements ArtifactReadable {

   private final Map<IRelationTypeSide, List<ArtifactReadable>> relationMap =
      new HashMap<IRelationTypeSide, List<ArtifactReadable>>();
   private final Set<RelationType> validRelationTypes = new LinkedHashSet<RelationType>();

   private final HashCollection<IAttributeType, String> attributes = new HashCollection<IAttributeType, String>();

   private final String name, guid;
   private final IArtifactType type;
   private final IOseeBranch branch;
   private ArtifactReadable parent;

   public MockArtifact(String guid, String name) {
      this(guid, name, CoreArtifactTypes.Artifact, CoreBranches.COMMON);
   }

   public MockArtifact(String guid, String name, IArtifactType type, IOseeBranch branch) {
      this.guid = guid;
      this.name = name;
      this.type = type;
      this.branch = branch;
      addAttribute(CoreAttributeTypes.Name, name);
   }

   public void setParent(ArtifactReadable parent) {
      this.parent = parent;
   }

   public void addAttribute(IAttributeType type, String value) {
      attributes.put(type, value);
   }

   public Collection<RelationType> getValidRelationTypes() {
      return validRelationTypes;
   }

   public void addRelationType(RelationType relationType) {
      validRelationTypes.add(relationType);
   }

   public ResultSet<ArtifactReadable> getRelatedArtifacts(IRelationTypeSide side) {
      List<ArtifactReadable> data = relationMap.get(side);
      if (data == null) {
         data = Collections.emptyList();
      }
      return new ResultSetList<ArtifactReadable>(data);
   }

   public void addRelation(IRelationTypeSide relation, ArtifactReadable artifact) {
      List<ArtifactReadable> artList = relationMap.get(relation);
      if (artList == null) {
         artList = new LinkedList<ArtifactReadable>();
         relationMap.put(relation, artList);
      }
      artList.add(artifact);
      RelationType type =
         new RelationType(relation.getGuid(), relation.getName(), "sideA", "sideB", CoreArtifactTypes.Artifact,
            CoreArtifactTypes.Artifact, RelationTypeMultiplicity.MANY_TO_MANY, "");
      addRelationType(type);
   }

   @Override
   public int getLocalId() {
      return 0;
   }

   @Override
   public IOseeBranch getBranch() {
      return branch;
   }

   @Override
   public String getHumanReadableId() {
      return null;
   }

   @Override
   public int getTransaction() {
      return 0;
   }

   @Override
   public IArtifactType getArtifactType() {
      return type;
   }

   @Override
   public Collection<IAttributeType> getExistingAttributeTypes() {
      return attributes.keySet();
   }

   @SuppressWarnings({"unchecked", "rawtypes"})
   @Override
   public <T> List<AttributeReadable<T>> getAttributes(IAttributeType attributeType) {
      Collection<String> values = attributes.getValues(attributeType);
      List<AttributeReadable<T>> toReturn = null;
      if (values != null && !values.isEmpty()) {
         toReturn = new LinkedList<AttributeReadable<T>>();
         for (String value : values) {
            AttributeReadable<T> attr = new MockAttribute(attributeType, value);
            toReturn.add(attr);
         }
      } else {
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType) {
      return null;
   }

   @Override
   public String getGuid() {
      return guid;
   }

   @Override
   public boolean matches(Identity<?>... identities) {
      return false;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public List<AttributeReadable<Object>> getAttributes() {
      List<AttributeReadable<Object>> toReturn = new ArrayList<AttributeReadable<Object>>();
      for (Entry<IAttributeType, Collection<String>> entry : attributes.entrySet()) {
         for (String value : entry.getValue()) {
            toReturn.add(new MockAttribute<Object>(entry.getKey(), value));
         }
      }
      return toReturn;
   }

   public void clearRelations() {
      validRelationTypes.clear();
      relationMap.clear();
   }

   @Override
   public String getSoleAttributeAsString(IAttributeType attributeType, String defaultValue) {
      return null;
   }

   @Override
   public boolean isOfType(IArtifactType... otherTypes) {
      return false;
   }

   @Override
   public <T> T getSoleAttributeValue(IAttributeType attributeType) {
      return null;
   }

   @Override
   public boolean isAttributeTypeValid(IAttributeType attributeType) {
      return false;
   }

   @Override
   public int getAttributeCount(IAttributeType type) throws OseeCoreException {
      return attributes.getValues(type).size();
   }

   @Override
   public <T> List<T> getAttributeValues(IAttributeType attributeType) throws OseeCoreException {
      return null;
   }

   @Override
   public Collection<? extends IAttributeType> getValidAttributeTypes() throws OseeCoreException {
      return null;
   }

   @Override
   public boolean isDeleted() {
      return false;
   }

   @Override
   public int getAttributeCount(IAttributeType type, DeletionFlag deletionFlag) throws OseeCoreException {
      return 0;
   }

   @Override
   public List<AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) throws OseeCoreException {
      return null;
   }

   @Override
   public <T> List<AttributeReadable<T>> getAttributes(IAttributeType attributeType, DeletionFlag deletionFlag) throws OseeCoreException {
      return null;
   }
}