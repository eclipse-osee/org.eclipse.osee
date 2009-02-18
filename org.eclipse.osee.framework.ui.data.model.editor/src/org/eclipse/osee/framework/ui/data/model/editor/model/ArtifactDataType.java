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
package org.eclipse.osee.framework.ui.data.model.editor.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDataType extends DataType {

   private Image image;
   private TypeManager<AttributeDataType> attributes;
   private TypeManager<RelationDataType> relations;
   private ArtifactDataType parent;

   public ArtifactDataType() {
      this(EMPTY_STRING, EMPTY_STRING, EMPTY_STRING, null);
   }

   public ArtifactDataType(String namespace, String name, Image imageName) {
      this(EMPTY_STRING, namespace, name, imageName);
   }

   public ArtifactDataType(String uniqueId, String namespace, String name, Image image) {
      super(uniqueId, namespace, name);
      this.image = image;
      this.attributes = new TypeManager<AttributeDataType>();
      this.relations = new TypeManager<RelationDataType>();
   }

   public void add(AttributeDataType attribute) {
      getAttributeManager().add(attribute);
   }

   public void add(RelationDataType relation) {
      getRelationManager().add(relation);
   }

   public void remove(AttributeDataType attribute) {
      getAttributeManager().remove(attribute);
   }

   public void remove(RelationDataType relation) {
      getRelationManager().remove(relation);
   }

   private TypeManager<AttributeDataType> getAttributeManager() {
      return attributes;
   }

   private TypeManager<RelationDataType> getRelationManager() {
      return relations;
   }

   public Image getImage() {
      return image != null ? image : ImageDescriptor.getMissingImageDescriptor().createImage();
   }

   public void setImage(Image image) {
      this.image = image;
   }

   public void setParent(ArtifactDataType parent) {
      this.parent = parent;
   }

   public ArtifactDataType getParent() {
      return parent;
   }

   public List<ArtifactDataType> getSuperTypes() {
      List<ArtifactDataType> toReturn = new ArrayList<ArtifactDataType>();
      if (parent != null) {
         toReturn.add(parent);
         toReturn.addAll(parent.getSuperTypes());
      }
      return toReturn;
   }

   public List<AttributeDataType> getInheritedAttributes() {
      List<AttributeDataType> inherited = new ArrayList<AttributeDataType>();
      if (parent != null) {
         inherited.addAll(parent.getLocalAndInheritedAttributes());
      }
      return inherited;
   }

   public List<RelationDataType> getInheritedRelations() {
      List<RelationDataType> inherited = new ArrayList<RelationDataType>();
      if (parent != null) {
         inherited.addAll(parent.getLocalAndInheritedRelations());
      }
      return inherited;
   }

   public List<AttributeDataType> getLocalAndInheritedAttributes() {
      List<AttributeDataType> allList = new ArrayList<AttributeDataType>();
      allList.addAll(getInheritedAttributes());
      allList.addAll(getLocalAttributes());
      return allList;
   }

   public List<RelationDataType> getLocalAndInheritedRelations() {
      List<RelationDataType> allList = new ArrayList<RelationDataType>();
      allList.addAll(getInheritedRelations());
      allList.addAll(getLocalRelations());
      return allList;
   }

   public List<AttributeDataType> getLocalAttributes() {
      return getAttributeManager().getAll();
   }

   public List<RelationDataType> getLocalRelations() {
      return getRelationManager().getAll();
   }
}
