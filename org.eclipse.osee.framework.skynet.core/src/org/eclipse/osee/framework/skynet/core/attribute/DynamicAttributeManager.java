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

package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.AttributeMemo;

/**
 * Manages attributes to enforce dynamic constraints.
 * 
 * @author Robert A. Fisher
 */
public class DynamicAttributeManager {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(DynamicAttributeManager.class);
   private final DynamicAttributeDescriptor descriptor;
   private int remainingOccurrences;
   private ArrayList<Attribute> attributes;
   private Collection<AttributeMemo> deletedAttributes;
   private boolean initialized;
   private boolean inDbInitialize;
   private Artifact parentArtifact;

   private boolean dirty;

   /**
    * Create a manager that is tied to a particular artifact.
    */
   protected DynamicAttributeManager(Artifact parentArtifact, DynamicAttributeDescriptor descriptor, boolean initialized) {
      this.parentArtifact = parentArtifact;
      this.descriptor = descriptor;
      this.initialized = initialized;

      this.inDbInitialize = false;
      this.dirty = false;

      this.remainingOccurrences = descriptor.getMaxOccurrences();

      this.deletedAttributes = new LinkedList<AttributeMemo>();
   }

   /**
    * Set the contained attribute value. This is only appropriate for <code>UserDefinedAttributeType</code>'s with a
    * max occurrence of 1 so that the assignment is not ambigous. If the attribute has not been created yet, then it
    * will be created and then set to the supplied values.
    * 
    * @param value
    * @throws IllegalStateException if any of the following conditions are true:
    *            <ul>
    *            <li>This object is in progress of being initialized from the database.</li>
    *            <li>This object has not been initialized.</li>
    *            <li>This object allows for more than one occurrence (the max occurrence value is not 1).</li>
    *            </ul>
    */
   public void setValue(String value) {
      Attribute attribute = getAttributeForSet();
      attribute.setStringData(value);
   }

   public void loadValue(String value) {
      Attribute attribute = getAttributeForSet();
      attribute.setStringData(value, false);
   }

   public void swagValue(String value) {
      Attribute attribute = getAttributeForSet();
      attribute.swagValue(value);
   }

   public void setData(InputStream stream) {
      Attribute attribute = getAttributeForSet();
      attribute.setDat(stream);
   }

   private Attribute getAttributeForSet() {
      if (inDbInitialize) throw new IllegalStateException(
            "This object is in progress of being initialized from the datastore");
      if (!initialized) throw new IllegalStateException("This object has not been initialized");
      if (descriptor.getMaxOccurrences() != 1) throw new IllegalStateException(
            "This object can not be set since it allows for more than one Attribute instance");

      Attribute attribute;

      // Acquire a new attribute if necessary (due to the prior exceptions, we know maxOccurrences
      // == 1
      if (hasRemaining()) {
         attribute = getNewAttribute();
      } else {
         attribute = attributes.iterator().next();
      }

      return attribute;
   }

   /**
    * Creates a new instance of an <code>Attribute</code> attached to this <code>UserDefinedAttributeType</code> and
    * returns a reference to it. The new attribute will have the default value defined by this type.
    * 
    * @return The new attribute.
    * @throws IllegalStateException if any of the following conditions are true:
    *            <ul>
    *            <li>This object is in progress of being initialized from the database.</li>
    *            <li>This object has not been initialized.</li>
    *            <li>There are no remaining available occurrences as the max occurrences has been met.</li>
    *            </ul>
    */
   public Attribute getNewAttribute() {
      if (inDbInitialize) throw new IllegalStateException(
            "This object is in progress of being initialized from the datastore");
      if (!initialized) throw new IllegalStateException("This object has not been initialized");
      if (remainingOccurrences <= 0) throw new IllegalStateException(
            "The maxOccurences values has already been met, operation can not be performed");

      Attribute attribute = null;

      try {
         attribute = createAttribute();
      } catch (Exception ex) {
         ex.printStackTrace();
      }

      return attribute;
   }

   private Attribute createAttribute() throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
      Attribute attribute = descriptor.createAttribute();

      attribute.setParent(this);
      attributes.add(attribute);

      remainingOccurrences--;
      return attribute;
   }

   /**
    * Removes an <code>Attribute</code> attached to this <code>UserDefinedAttributeType</code>.
    * 
    * @throws IllegalStateException if any of the following conditions are true:
    *            <ul>
    *            <li>This object is in progress of being initialized from the database.</li>
    *            <li>This object has not been initialized.</li>
    *            <li>The minimum attribute bound has been met.</li>
    *            </ul>
    * @throws IllegalArgumentException if the supplied attribute does not belong to this object.
    */
   public void removeAttribute(Attribute attribute) {
      if (inDbInitialize) throw new IllegalStateException(
            "This object is in progress of being initialized from the datastore");
      if (!initialized) throw new IllegalStateException("This object has not been initialized");
      if (!canDelete()) throw new IllegalStateException(
            "The minOccurences values has already been met, operation can not be performed");

      if (attributes.remove(attribute)) {
         remainingOccurrences++;
         addAttributeToDeleteList(attribute);
         attribute.setDirty();
         dirty = true;

      } else {
         throw new IllegalArgumentException("The supplied attribute was not created by this object");
      }
   }

   /**
    * @return Returns the descriptor.
    */
   public DynamicAttributeDescriptor getDescriptor() {
      return descriptor;
   }

   // /**
   // * @return Returns the baseAttributeClass.
   // */
   // public Class<? extends Attribute> getBaseAttributeClass() {
   // return baseAttributeClass;
   // }

   public Collection<Attribute> getAttributes() {
      // Return a copy of our attributes list so they can't modify our list
      return new ArrayList<Attribute>(attributes);
   }

   /**
    * This method is only to be called on attributes that are allowed min = 0 or min = 1 If an attribute instance
    * exists, it's value set Else the attribute is created and it's value set
    * 
    * @param value
    */
   public void setSoleAttributeValue(String value) {
      if (getAttributes().size() == 1)
         getSoleAttribute().setStringData(value);
      else
         getNewAttribute().setStringData(value);
   }

   /**
    * This method is only to be called on attributes that are allowed min = 0 or min = 1 If an attribute instance
    * exists, it's string value is returned Else an empty string is returned
    */
   public String getSoleAttributeValue() {
      if (getAttributes().size() == 1)
         return getSoleAttribute().getStringData();
      else
         return "";
   }

   public Attribute getSoleAttribute() {
      // Return the single attribute
      if (attributes.size() > 1) throw new IllegalArgumentException(
            "Attribute \"" + getDescriptor().getName() + "\" Must have exactly one instance.  It currently has " + attributes.size() + ".");

      if (attributes.isEmpty()) {
         getNewAttribute();
      }
      return attributes.get(0);
   }

   // /**
   // * @return Returns the defaultValue.
   // */
   // public String getDefaultValue() {
   // return defaultValue;
   // }
   //
   // /**
   // * @return Returns the defaultValue.
   // */
   // public String getTipText() {
   // return tipText;
   // }
   //
   // /**
   // * @return Returns the userViewable.
   // */
   // public boolean isUserViewable() {
   // return userViewable;
   // }
   //
   // /**
   // * @return Returns the maxOccurrences.
   // */
   // public int getMaxOccurrences() {
   // return maxOccurrences;
   // }
   //
   // /**
   // * @return Returns the minOccurrences.
   // */
   // public int getMinOccurrences() {
   // return minOccurrences;
   // }
   //
   // /**
   // * @return Returns the name.
   // */
   // public String getName() {
   // return name;
   // }

   /**
    * @return Returns the remainingOccurrences.
    * @throws IllegalStateException if any of the following conditions are true:
    *            <ul>
    *            <li>This object is in progress of being initialized from the database.</li>
    *            <li>This object has not been initialized.</li>
    *            </ul>
    */
   public int getRemainingOccurrences() {
      if (inDbInitialize) throw new IllegalStateException(
            "This object is in progress of being initialized from the datastore");
      if (!initialized) throw new IllegalStateException("This object has not been initialized");

      return remainingOccurrences;
   }

   // /**
   // * @return Returns the typeId.
   // */
   // public int getAttrTypeId() {
   // return attrTypeId;
   // }
   //
   // /**
   // * @return Returns the validityXml.
   // */
   // public String getValidityXml() {
   // return validityXml;
   // }

   /**
    * Reports if there are remaining occurences. This call is equivalent to <code>(getRemainingOccurences() > 0)</code>,
    * and provided simply for convenience.
    * 
    * @return <code>true</code> if there are remaining occurences.
    * @throws IllegalStateException if any of the following conditions are true:
    *            <ul>
    *            <li>This object is in progress of being initialized from the database.</li>
    *            <li>This object has not been initialized.</li>
    *            <li>There are no remaining available occurrences as the max occurrences has been met.</li>
    *            </ul>
    */
   public boolean hasRemaining() {
      if (inDbInitialize) throw new IllegalStateException(
            "This object is in progress of being initialized from the datastore");
      if (!initialized) throw new IllegalStateException("This object has not been initialized");

      return remainingOccurrences > 0;
   }

   /**
    * Reports whether the Attribute can be deleted
    * 
    * @return <code>true</code> if the Attribute can be deleted
    * @throws IllegalStateException if any of the following conditions are true:
    *            <ul>
    *            <li>This object is in progress of being initialized from the database.</li>
    *            <li>This object has not been initialized.</li>
    *            </ul>
    */
   public boolean canDelete() {
      if (inDbInitialize) throw new IllegalStateException(
            "This object is in the progress of being initialized from the datastore");
      if (!initialized) throw new IllegalStateException("This object has not been initialized");

      return attributes.size() > descriptor.getMinOccurrences();
   }

   // /**
   // * This should never be called from the application software.
   // */
   // public void setAttrTypeId(int attrTypeId) {
   // this.attrTypeId = attrTypeId;
   // }

   /**
    * This should never be called from the application software.
    */
   public Attribute injectFromDb(InputStream stream, String varchar) {
      Attribute attribute;
      try {
         attribute = createAttribute();
      } catch (Exception ex) {
         throw new IllegalStateException(
               "Failed to create an attribute on the collection for injection of database data", ex);
      }

      if (varchar != null) attribute.setVarchar(varchar);
      if (stream != null) attribute.setBlobData(stream);

      // It is fresh from the database, so mark it as not dirty
      attribute.getManager().setDirty(false);
      attribute.setNotDirty();

      return attribute;
   }

   /**
    * This should never be called from the application software.
    */
   public void setupForInitialization(boolean localInitialization) {
      inDbInitialize = !localInitialization;
      attributes = new ArrayList<Attribute>(descriptor.getMinOccurrences());
   }

   /**
    * This should never be called from the application software.
    */
   public void enforceMinMaxConstraints() {
      inDbInitialize = false;
      int occurences = attributes.size();

      int neededAmount = descriptor.getMinOccurrences() - occurences;
      while (neededAmount-- > 0)
         try {
            createAttribute();
         } catch (Exception ex) {
            ex.printStackTrace();
         }

      occurences = attributes.size(); // this may have changed since createAttribute() may have
      // been called
      remainingOccurrences = descriptor.getMaxOccurrences() - occurences;
      if (remainingOccurrences < 0) {

         StringBuilder errorBuilder = new StringBuilder();
         errorBuilder.append(getDescriptor().getName() + " setup with too many attributes from the database, acquired " + occurences + " expected no more than " + descriptor.getMaxOccurrences() + "; hrid " + parentArtifact.getHumanReadableId() + " guid " + parentArtifact.getGuid() + ".");

         Attribute removedAttribute;
         while (remainingOccurrences < 0) {
            removedAttribute = attributes.remove(attributes.size() - 1);
            remainingOccurrences++;

            errorBuilder.append("\nAttribute with attr_id " + removedAttribute.getPersistenceMemo().getAttrId() + " not loaded");
         }

         logger.log(Level.WARNING, errorBuilder.toString());
      }
      // Trim the attributes storage array since at this point since many times attributes are not
      // being modified
      attributes.trimToSize();

      initialized = true;
   }

   /**
    * @return Returns the dirty.
    */
   public boolean isDirty() {
      boolean isDirty = dirty;

      if (!isDirty) {
         for (Attribute attr : getAttributes()) {
            isDirty |= attr.isDirty();
            if (isDirty) break;
         }
      }
      return isDirty;
   }

   /**
    * @param dirty The dirty to set.
    */
   public void setDirty(boolean dirty) {
      // When the dirty flag has been cleared, we can dump our list of deleted attributes
      if (dirty) {
         parentArtifact.setInTransaction(false);
      } else {
         deletedAttributes.clear();
      }
      this.dirty = dirty;
   }

   /**
    * Acquire a list of memos of all the deleted attributes. The provided collection is a duplication of the deletion
    * list, so modification to this list will not change the attribute's deletion status.
    * 
    * @return Return a reference to deleted attributes
    */
   public Collection<AttributeMemo> getDeletedAttributes() {
      // Give out a new collection so that our internal data can not be modified
      return new ArrayList<AttributeMemo>(deletedAttributes);
   }

   private void addAttributeToDeleteList(Attribute attribute) {
      deletedAttributes.add(attribute.getPersistenceMemo());
   }

   /*
    * (non-Javadoc)
    * 
    * @see java.lang.Object#toString()
    */
   @Override
   public String toString() {
      return descriptor.getName();
   }

   public Artifact getParentArtifact() {
      return parentArtifact;
   }

   /**
    * @return String of comma delimited attribute values
    */
   public String getAttributesStr() {
      return getAttributesStr(", ");
   }

   /**
    * @param delimiter delimiter to use between attribute values
    * @return String of attribute values delimited by delimiter
    */
   public String getAttributesStr(String delimiter) {
      StringBuffer sb = new StringBuffer();
      boolean firstTime = true;
      for (Attribute attr : getAttributes()) {
         if (firstTime)
            firstTime = false;
         else
            sb.append(delimiter);
         sb.append(attr.getStringData());
      }
      return sb.toString();
   }

   public Object clone(Artifact artifact) throws CloneNotSupportedException {
      DynamicAttributeManager attributeManager = getDescriptor().createAttributeManager(artifact, false);
      AttributeMemo memo;
      Attribute newAttribute;

      attributeManager.setupForInitialization(false);

      for (Attribute attribute : getAttributes()) {
         boolean containsBlobData = attribute.getBlobData() == null;
         newAttribute =
               attributeManager.injectFromDb(
                     containsBlobData ? null : new ByteArrayInputStream(attribute.getBlobData()),
                     attribute.getStringData());

         memo = attribute.getPersistenceMemo();

         if (memo != null) {
            newAttribute.setPersistenceMemo(memo);
         }
      }
      attributeManager.enforceMinMaxConstraints();

      return attributeManager;
   }

   public Attribute getAttribute(SkynetAttributeChange attrChange) {
      Attribute foundAttribute = null;

      for (Attribute attribute : attributes) {

         if (attribute.getPersistenceMemo() == null) {
            attribute.setPersistenceMemo(new AttributeMemo(attrChange.getAttributeId(),
                  getDescriptor().getAttrTypeId(), attrChange.getGammaId()));
            foundAttribute = attribute;
            break;
         }

         if (attribute.getPersistenceMemo().getAttrId() == attrChange.getAttributeId()) {
            foundAttribute = attribute;
            break;
         }
      }
      if (foundAttribute == null) {
         foundAttribute = getNewAttribute();
         foundAttribute.setPersistenceMemo(new AttributeMemo(attrChange.getAttributeId(),
               getDescriptor().getAttrTypeId(), attrChange.getGammaId()));
      }

      return foundAttribute;
   }

   /**
    * Purge attribute from parent artifact.
    * 
    * @param attribute
    */
   public void purge(Attribute attribute) throws SQLException {
      parentArtifact.purgeAttribute(attribute);
   }
}
