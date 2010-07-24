/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.importing;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class RoughArtifact {
   private RoughArtifact roughParent;
   private ReqNumbering number;
   private String guid;
   private String humandReadableId;
   private RoughArtifactKind roughArtifactKind;
   private final RoughAttributeSet attributes;
   private final Collection<RoughArtifact> children;
   private ArtifactType primaryArtifactType;

   public RoughArtifact(RoughArtifactKind roughArtifactKind) {

      this.attributes = new RoughAttributeSet();
      this.children = new ArrayList<RoughArtifact>();
      this.roughArtifactKind = roughArtifactKind;
   }

   public void clear() {
      this.attributes.clear();
      this.children.clear();
      humandReadableId = null;
      guid = null;
      number = null;
      roughParent = null;
      primaryArtifactType = null;
   }

   public RoughArtifact(RoughArtifactKind roughArtifactKind, String name) {
      this(roughArtifactKind);
      addAttribute("Name", name);
   }

   public Set<String> getAttributeTypeNames() {
      return attributes.getAttributeTypeNames();
   }

   public boolean hasHierarchicalRelation() {
      return number != null;
   }

   public void addChild(RoughArtifact child) {
      child.roughParent = this;
      children.add(child);
   }

   public boolean hasParent() {
      return roughParent != null;
   }

   public RoughArtifact getRoughParent() {
      return roughParent;
   }

   public void addURIAttribute(String name, URI url) {
      attributes.addURIAttribute(name, url);
   }

   public void addURIAttribute(IAttributeType attributeType, URI file) {
      addURIAttribute(attributeType.getName(), file);
   }

   public void addAttribute(String name, String value) {
      if (isMultipleEnum(name, value)) {
         attributes.addMultiple(name, StringUtils.split(value, ','));
      } else {
         attributes.add(name, value);
      }
   }

   private boolean isMultipleEnum(String typeName, String value) {
      try {
         AttributeType type = AttributeTypeManager.getType(typeName);
         if (type.isEnumerated() && type.getMaxOccurrences() > 1 && value.contains(",")) {
            return true;
         }
      } catch (OseeCoreException e) {
      }
      return false;
   }

   public void addAttribute(IAttributeType attrType, String value) {
      attributes.add(attrType.getName(), value);
   }

   public Map<String, URI> getURIAttributes() {
      return getURIAttributes();
   }

   public boolean isChild(RoughArtifact otherArtifact) {
      return number.isChild(otherArtifact.number);
   }

   @Override
   public String toString() {
      return getName();
   }

   public void setSectionNumber(String number) {
      this.number = new ReqNumbering(number);
   }

   public RoughAttributeSet getAttributes() {
      return attributes;
   }

   public Collection<RoughArtifact> getChildren() {
      return children;
   }

   public void setGuid(String guid) {
      this.guid = guid;
   }

   public String getGuid() {
      return guid;
   }

   public void setHumandReadableId(String humandReadableId) {
      this.humandReadableId = humandReadableId;
   }

   public String getHumanReadableId() {
      return humandReadableId;
   }

   public RoughArtifactKind getRoughArtifactKind() {
      return roughArtifactKind;
   }

   public void setRoughArtifactKind(RoughArtifactKind roughArtifactKind) {
      this.roughArtifactKind = roughArtifactKind;
   }

   public String getName() {
      return attributes.getSoleAttributeValue(CoreAttributeTypes.NAME.getName());
   }

   public String getRoughAttribute(String attributeName) {
      return attributes.getSoleAttributeValue(attributeName);
   }

   public ArtifactType getPrimaryArtifactType() {
      return primaryArtifactType;
   }

   public void setPrimaryArtifactType(ArtifactType primaryArtifactType) {
      this.primaryArtifactType = primaryArtifactType;
   }

   public void translateAttributes(Artifact artifact) throws OseeCoreException {
      attributes.translateAttributes(artifact);
   }
}