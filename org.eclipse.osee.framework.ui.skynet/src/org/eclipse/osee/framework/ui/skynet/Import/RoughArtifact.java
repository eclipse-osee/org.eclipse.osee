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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeWrappedException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class RoughArtifact {
   private Artifact realArtifact;
   private RoughArtifact roughParent;
   private ReqNumbering number;
   private String guid;
   private String humandReadableId;
   private HashMap<String, File> fileAttributes;
   private final Branch branch;
   private RoughArtifactKind roughArtifactKind;
   private final List<NameAndVal> attributes = new ArrayList<NameAndVal>();
   private final Collection<RoughArtifact> children = new ArrayList<RoughArtifact>();

   public RoughArtifact(RoughArtifactKind roughArtifactKind, Branch branch) {
      this.branch = branch;
      this.roughArtifactKind = roughArtifactKind;
   }

   public RoughArtifact(Artifact associatedArtifact) {
      this(RoughArtifactKind.PRIMARY, associatedArtifact.getBranch());
      realArtifact = associatedArtifact;
   }

   public RoughArtifact(RoughArtifactKind roughArtifactKind, Branch branch, String name) {
      this(roughArtifactKind, branch);
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

   public void conferAttributesUpon(Artifact artifact) throws OseeCoreException {
      for (NameAndVal roughtAttribute : attributes) {
         if (roughtAttribute.getValue() != null) {
            artifact.addAttributeFromString(roughtAttribute.getName(), roughtAttribute.getValue());
         }
      }
      setFileAttributes(artifact);
   }

   private void setFileAttributes(Artifact artifact) throws OseeCoreException {
      if (fileAttributes != null) {
         for (Entry<String, File> entry : fileAttributes.entrySet()) {
            try {
               artifact.setSoleAttributeFromStream(entry.getKey(), new FileInputStream(entry.getValue()));
            } catch (FileNotFoundException ex) {
               throw new OseeWrappedException(ex);
            }
         }
      }
   }

   public String toString() {
      return getName();
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

   public Artifact getReal(SkynetTransaction transaction, IProgressMonitor monitor, IArtifactImportResolver artifactResolver) throws OseeCoreException {
      if (realArtifact != null) {
         return realArtifact;
      }

      realArtifact = artifactResolver.resolve(this);

      if (monitor != null) {
         monitor.subTask(getName());
         monitor.worked(1);
      }

      for (RoughArtifact roughArtifact : children) {
         Artifact tempArtifact = roughArtifact.getReal(transaction, monitor, artifactResolver);
         if (realArtifact != null && tempArtifact != null) {
            if (tempArtifact.getParent() == null) {
               realArtifact.addChild(tempArtifact);
            } else if (tempArtifact.getParent() != realArtifact) {
               throw new IllegalStateException(
                     "Artifact already has a parent that is not inline with the import parent");
            }
         }
      }

      if (realArtifact != null) {
         realArtifact.persistAttributesAndRelations(transaction);
      }
      return realArtifact;
   }

   public void updateValues(Artifact artifact) throws OseeCoreException, FileNotFoundException {
      for (NameAndVal value : attributes) {
         artifact.setSoleAttributeFromString(value.getName(), value.getValue());
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
    * @return the roughArtifactKind
    */
   public RoughArtifactKind getRoughArtifactKind() {
      return roughArtifactKind;
   }

   /**
    * @param roughArtifactKind the roughArtifactKind to set
    */
   public void setRoughArtifactKind(RoughArtifactKind roughArtifactKind) {
      this.roughArtifactKind = roughArtifactKind;
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