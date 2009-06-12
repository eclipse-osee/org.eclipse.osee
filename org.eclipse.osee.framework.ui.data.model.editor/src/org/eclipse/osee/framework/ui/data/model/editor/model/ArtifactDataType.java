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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.exception.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.data.model.editor.ODMEditorActivator;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.swt.graphics.Image;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactDataType extends DataType {

   private Image image;
   private TypeManager<AttributeDataType> attributes;
   private TypeManager<RelationDataType> relations;
   private ArtifactDataType superType;
   private Set<ArtifactDataType> subTypes;

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
      this.subTypes = new HashSet<ArtifactDataType>();
   }

   public void add(AttributeDataType attribute) {
      getAttributeManager().add(attribute);
      fireModelEvent();
   }

   public void add(RelationDataType relation) {
      getRelationManager().add(relation);
      fireModelEvent();
   }

   public void remove(AttributeDataType attribute) {
      getAttributeManager().remove(attribute);
      fireModelEvent();
   }

   public void remove(RelationDataType relation) {
      getRelationManager().remove(relation);
      fireModelEvent();
   }

   private TypeManager<AttributeDataType> getAttributeManager() {
      return attributes;
   }

   private TypeManager<RelationDataType> getRelationManager() {
      return relations;
   }

   public Image getImage() {
      return image == null ? ImageManager.getImage(FrameworkImage.MISSING) : image;
   }

   public void setImage(Image image) {
      if (this.image != image) {
         this.image = image;
         fireModelEvent();
      }
   }

   @Override
   public void addConnection(ConnectionModel connection) {
      if (connection != null) {
         try {
            if (connection.getTarget() == this) {
               setSuperType((ArtifactDataType) connection.getSource());
            } else if (connection.getSource() == this) {
               addSubType((ArtifactDataType) connection.getTarget());
            }
            super.addConnection(connection);
         } catch (OseeStateException ex) {
            OseeLog.log(ODMEditorActivator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public void removeConnection(ConnectionModel connection) {
      if (connection != null) {
         try {
            if (connection.getTarget() == this) {
               setSuperType(null);
            } else if (connection.getSource() == this) {
               removeSubType((ArtifactDataType) connection.getTarget());
            }
            super.removeConnection(connection);
         } catch (OseeStateException ex) {
            OseeLog.log(ODMEditorActivator.class, Level.SEVERE, ex);
         }
      }
   }

   public void setSuperType(ArtifactDataType superType) throws OseeStateException {
      getSourceConnections(); // I am the source;

      if (this.superType != superType && !this.equals(superType)) {
         if (superType == null) {
            if (this.superType != null) {
               this.superType.removeSubType(this);
            }
            this.superType = superType;
            fireModelEvent();
         } else {
            checkInheritance(superType);
            if (this.superType != null) {
               this.superType.removeSubType(this);
            }
            this.superType = superType;
            this.superType.addSubType(this);
            fireModelEvent();
         }
      }
   }

   private void checkInheritance(ArtifactDataType item) throws OseeStateException {
      boolean thisIsAItem = instanceOf(this, item);
      boolean itemIsAThis = instanceOf(item, this);
      String message = "Inheritance constraint violation - [%s] is a [%s]";
      if (thisIsAItem) {
         throw new OseeStateException(String.format(message, this, item));
      } else if (itemIsAThis) {
         throw new OseeStateException(String.format(message, item, this));
      }
   }

   public static boolean instanceOf(ArtifactDataType art1, ArtifactDataType art2) {
      boolean toReturn = false;
      if (art1 == art2) {
         toReturn = true;
      } else if (art1 != null && art2 != null) {
         toReturn = art1.equals(art2) || art2.getSubTypes().contains(art1);
      }
      return toReturn;
   }

   private void addSubType(ArtifactDataType childType) throws OseeStateException {
      if (childType != this) {
         checkInheritance(childType);
         subTypes.add(childType);
      }
   }

   private void removeSubType(ArtifactDataType childType) {
      subTypes.remove(childType);
   }

   public List<ArtifactDataType> getSubTypes() {
      List<ArtifactDataType> descendants = new ArrayList<ArtifactDataType>();
      for (ArtifactDataType descendant : subTypes) {
         if (descendant != this) {
            descendants.add(descendant);
            descendants.addAll(descendant.getSubTypes());
         }
      }
      return descendants;
   }

   public ArtifactDataType getSuperType() {
      return this.superType;
   }

   public List<ArtifactDataType> getSuperTypes() {
      List<ArtifactDataType> toReturn = new ArrayList<ArtifactDataType>();
      if (this.superType != null) {
         toReturn.add(this.superType);
         toReturn.addAll(this.superType.getSuperTypes());
      }
      return toReturn;
   }

   public List<AttributeDataType> getInheritedAttributes() {
      List<AttributeDataType> inherited = new ArrayList<AttributeDataType>();
      if (this.superType != null) {
         inherited.addAll(this.superType.getLocalAndInheritedAttributes());
      }
      return inherited;
   }

   public List<RelationDataType> getInheritedRelations() {
      List<RelationDataType> inherited = new ArrayList<RelationDataType>();
      if (this.superType != null) {
         inherited.addAll(this.superType.getLocalAndInheritedRelations());
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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.data.model.editor.model.Model#fireModelEvent()
    */
   @Override
   protected void fireModelEvent() {
      super.fireModelEvent();
      for (ArtifactDataType descendant : getSubTypes()) {
         descendant.fireModelEvent();
      }
   }
}
