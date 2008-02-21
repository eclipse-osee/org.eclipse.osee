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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.attribute.FullPortableExport;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class RoughArtifact {
   private Artifact realArtifact;
   private RoughArtifact roughParent;
   private ReqNumbering number;
   private List<NameAndVal> attributes;
   private Collection<RoughArtifact> children;
   private String guid;
   private String humandReadableId;
   private ArtifactSubtypeDescriptor headingDescriptor;
   private ArtifactSubtypeDescriptor primaryDescriptor;
   private boolean forcePrimaryType;
   private HashMap<String, File> fileAttributes;
   private final Branch branch;

   public RoughArtifact(Branch branch) {
      attributes = new ArrayList<NameAndVal>();
      children = new ArrayList<RoughArtifact>();
      forcePrimaryType = false;
      this.branch = branch;
   }

   public RoughArtifact(Artifact associatedArtifact) {
      this(associatedArtifact.getBranch());
      realArtifact = associatedArtifact;
   }

   public RoughArtifact(Branch branch, String name) {
      this(branch);
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

   /**
    * @return the roughParent
    */
   public RoughArtifact getRoughParent() {
      return roughParent;
   }

   public Artifact getAssociatedArtifact() {
      return realArtifact;
   }

   public String toString() {
      return getName();
   }

   public void addFileAttribute(String name, File file) {
      if (fileAttributes == null) {
         fileAttributes = new HashMap<String, File>(2, 1);
      }
      fileAttributes.put(name, file);
   }

   public void addAttribute(String name, String value) {
      attributes.add(new NameAndVal(name, value));
   }

   public void addAttribute(String name, String value, AttributeImportType type) {
      attributes.add(new NameAndVal(name, value, type));
   }

   public boolean isChild(RoughArtifact otherArtifact) {
      return number.isChild(otherArtifact.number);
   }

   public void conferAttributesUpon(Artifact artifact) throws FileNotFoundException, SQLException {
      for (NameAndVal attr : attributes) {
         String attributeName = attr.getName();
         String fullValue = attr.getValue();

         if (fullValue != null) {
            try {
               DynamicAttributeManager attributeManager = artifact.getAttributeManager(attributeName);
               DynamicAttributeDescriptor descriptor = attributeManager.getDescriptor();
               if (descriptor.getMinOccurrences() == 1 && descriptor.getMaxOccurrences() == 1) {
                  attributeManager.setValue(fullValue);
               } else {
                  for (String value : fullValue.split(FullPortableExport.ATTRIBUTE_VALUE_DELIMITER_REGEX)) {
                     attributeManager.getNewAttribute().setStringData(value);
                  }
               }
            } catch (IllegalStateException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      }

      setFileAttributes(artifact);
   }

   private void setFileAttributes(Artifact artifact) throws FileNotFoundException, SQLException {
      if (fileAttributes != null) {
         for (Entry<String, File> entry : fileAttributes.entrySet()) {
            DynamicAttributeManager attributeManager = artifact.getAttributeManager(entry.getKey());
            attributeManager.setData(new FileInputStream(entry.getValue()));
         }
      }
   }

   /**
    * @param number The number to set.
    */
   public void setSectionNumber(String number) {
      this.number = new ReqNumbering(number);
   }

   public class NameAndVal {
      private String name;
      private String value;
      private AttributeImportType type;

      /**
       * @param name
       * @param value
       */
      public NameAndVal(String name, String value, AttributeImportType type) {
         super();
         this.name = name;
         this.value = value;
         this.type = type;
      }

      public NameAndVal(String name, String value) {
         this(name, value, AttributeImportType.NONE);
      }

      public String getName() {
         return name;
      }

      public String getValue() {
         return value;
      }

      public AttributeImportType getType() {
         return type;
      }

      public String toString() {
         return name + ": " + value;
      }
   }

   public Collection<NameAndVal> getAttributes() {
      return attributes;
   }

   /**
    * @return Returns the children.
    */
   public Collection<RoughArtifact> getChildren() {
      return children;
   }

   public Artifact getReal(Branch branch, IProgressMonitor monitor, IArtifactImportResolver artifactResolver) throws Exception {
      if (realArtifact != null) {
         return realArtifact;
      }

      realArtifact = artifactResolver.resolve(this);

      if (monitor != null) {
         monitor.subTask(getName());
         monitor.worked(1);
      }

      for (RoughArtifact roughArtifact : children) {
         Artifact tempArtifact = roughArtifact.getReal(branch, monitor, artifactResolver);
         if (realArtifact != null) {
            if (tempArtifact.getParent() == null) {
               realArtifact.addChild(tempArtifact);
            } else if (tempArtifact.getParent() != realArtifact) {
               throw new IllegalStateException(
                     "Artifact already has a parent that is not inline with the import parent");
            }
         }
      }

      realArtifact.persist(true);
      return realArtifact;
   }

   public ArtifactSubtypeDescriptor getDescriptorForGetReal() {
      return children.isEmpty() || forcePrimaryType ? primaryDescriptor : headingDescriptor;
   }

   public void updateValues(Artifact artifact) throws SQLException, FileNotFoundException {
      for (NameAndVal value : attributes) {
         artifact.setSoleAttributeValue(value.getName(), value.getValue());
      }

      setFileAttributes(artifact);
   }

   /**
    * @param guid The guid to set.
    */
   public void setGuid(String guid) {
      this.guid = guid;
   }

   /**
    * @return Returns the guid.
    */
   public String getGuid() {
      return guid;
   }

   /**
    * @param humandReadableId The humandReadableId to set.
    */
   public void setHumandReadableId(String humandReadableId) {
      this.humandReadableId = humandReadableId;
   }

   public String getHumandReadableId() {
      return humandReadableId;
   }

   /**
    * @param headingDescriptor The headingDescriptor to set.
    */
   public void setHeadingDescriptor(ArtifactSubtypeDescriptor headingDescriptor) {
      this.headingDescriptor = headingDescriptor;
   }

   /**
    * @param primaryDescriptor The leafDescriptor to set.
    */
   public void setPrimaryDescriptor(ArtifactSubtypeDescriptor primaryDescriptor) {
      this.primaryDescriptor = primaryDescriptor;
   }

   /**
    * @param forcePrimaryType The forcePrimaryType to set.
    */
   public void setForcePrimaryType(boolean forcePrimaryType) {
      this.forcePrimaryType = forcePrimaryType;
   }

   public String getName() {
      if (realArtifact != null) {
         return realArtifact.getDescriptiveName();
      }
      for (NameAndVal attr : attributes) {
         if (attr.getName().equals("Name")) {
            return attr.getValue();
         }
      }
      return "";
   }

   /**
    * @return the branch
    */
   public Branch getBranch() {
      return branch;
   }
}