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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;

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
   private final Map<String, String> attributes;
   private final Map<String, URI> uriAttributes;
   private final Collection<RoughArtifact> children;
   private ArtifactType primaryArtifactType;

   public RoughArtifact(RoughArtifactKind roughArtifactKind) {
      this.uriAttributes = new HashMap<String, URI>(2, 1);
      this.attributes = new LinkedHashMap<String, String>();
      this.children = new ArrayList<RoughArtifact>();
      this.roughArtifactKind = roughArtifactKind;
   }

   public void clear() {
      this.attributes.clear();
      this.children.clear();
      this.uriAttributes.clear();
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

   public void addURIAttribute(String name, URI file) {
      uriAttributes.put(name, file);
   }

   public void addAttribute(String name, String value) {
      attributes.put(name, value);
   }

   public Map<String, URI> getURIAttributes() {
      return uriAttributes;
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

   public Map<String, String> getAttributes() {
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

   public String getHumandReadableId() {
      return humandReadableId;
   }

   public RoughArtifactKind getRoughArtifactKind() {
      return roughArtifactKind;
   }

   public void setRoughArtifactKind(RoughArtifactKind roughArtifactKind) {
      this.roughArtifactKind = roughArtifactKind;
   }

   public String getName() {
      for (Entry<String, String> roughtAttribute : attributes.entrySet()) {
         if ("Name".equals(roughtAttribute.getKey())) {
            return roughtAttribute.getValue();
         }
      }
      return "";
   }

   public String getRoughAttribute(String attributeName) {
      for (Entry<String, String> roughtAttribute : attributes.entrySet()) {
         if (roughtAttribute.getKey().equalsIgnoreCase(attributeName)) {
            return roughtAttribute.getValue();
         }
      }
      return null;
   }

   public ArtifactType getPrimaryArtifactType() {
      return primaryArtifactType;
   }

   public void setPrimaryArtifactType(ArtifactType primaryArtifactType) {
      this.primaryArtifactType = primaryArtifactType;
   }

}