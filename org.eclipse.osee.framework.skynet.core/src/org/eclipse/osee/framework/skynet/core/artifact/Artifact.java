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
package org.eclipse.osee.framework.skynet.core.artifact;

import static org.eclipse.osee.framework.skynet.core.relation.RelationSide.DEFAULT_HIERARCHICAL__CHILD;
import static org.eclipse.osee.framework.skynet.core.relation.RelationSide.DEFAULT_HIERARCHICAL__PARENT;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.skynet.core.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.AttributeAnnotationManager;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.IArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.CharacterBackedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordTemplateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.WordWholeDocumentAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationType;
import org.eclipse.osee.framework.skynet.core.relation.LinkManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationLinkGroup;
import org.eclipse.osee.framework.skynet.core.util.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.util.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class Artifact implements IAdaptable, Comparable<Artifact> {
   public static final String UNNAMED = "Unnamed";
   public static final String BEFORE_GUID_STRING = "/BeforeGUID/PrePend";
   public static final String AFTER_GUID_STRING = "/AfterGUID";
   private static int count = 0;
   public final int aaaSerialId = count++;
   private final Branch branch;
   private final String guid;
   protected boolean dirty = true;
   protected boolean inTransaction = false;
   private boolean deleted = false;
   private ArtifactSubtypeDescriptor artifactType;
   private String humanReadableId;
   private final LinkManager linkManager = new LinkManager(this);
   private ArtifactFactory parentFactory;
   private int deletionTransactionId = -1;
   private HashCollection<String, Attribute<?>> attributes =
         new HashCollection<String, Attribute<?>>(false, LinkedList.class, 4);
   private AttributeAnnotationManager annotationMgr;
   private int transactionId;
   private int artId;
   private int gammaId;
   private boolean linksLoaded;

   protected Artifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactSubtypeDescriptor artifactType) {

      if (guid == null) {
         this.guid = GUID.generateGuidStr();
      } else {
         this.guid = guid;
      }

      if (humanReadableId == null) {
         rollHumanReadableId();
      } else {
         this.humanReadableId = humanReadableId;
      }

      this.parentFactory = parentFactory;
      this.branch = branch;
      this.artifactType = artifactType;
   }

   public boolean isInDb() {
      return artId > 0;
   }

   /**
    * A live artifact is one that can have its data updated to reflect its most current data in the datastore
    * 
    * @return whether this artifact is live
    */
   public boolean isLive() {
      return transactionId == 0;
   }

   /**
    * A historical artifact always corresponds to a fixed revision of an artifact
    * 
    * @return
    */
   public boolean isHistorical() {
      return !isLive();
   }

   public boolean isAnnotation(ArtifactAnnotation.Type type) {
      for (ArtifactAnnotation notify : getAnnotations()) {
         if (notify.getType() == type) return true;
      }
      return false;
   }

   public Set<ArtifactAnnotation> getAnnotations() {
      Set<ArtifactAnnotation> annotations = new HashSet<ArtifactAnnotation>();
      for (IArtifactAnnotation annotation : getAnnotationExtensions()) {
         annotation.getAnnotations(this, annotations);
      }
      return annotations;
   }

   public ArtifactAnnotation.Type getMainAnnotationType() {
      if (isAnnotation(ArtifactAnnotation.Type.Error))
         return ArtifactAnnotation.Type.Error;
      else if (isAnnotation(ArtifactAnnotation.Type.Warning))
         return ArtifactAnnotation.Type.Warning;
      else if (isAnnotation(ArtifactAnnotation.Type.Info)) return ArtifactAnnotation.Type.Info;
      return ArtifactAnnotation.Type.None;
   }

   public Image getImage() {
      if (AccessControlManager.getInstance().hasLock(this)) {
         return artifactType.getLockedImage(AccessControlManager.getInstance().hasLockAccess(this));
      }

      try {
         if (getArtifactTypeName().equals("Version")) {
            boolean next = getSoleAttributeValue("ats.Next Version", false);
            boolean released = getSoleAttributeValue("ats.Released", false);
            return artifactType.getImage(next, released);
         }
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return artifactType.getAnnotationImage(getMainAnnotationType());
   }

   public boolean isVersionControlled() {
      return true;
      // return controlLevel.isVersionControlled();
   }

   public boolean hasArtifacts(IRelationEnumeration side) {
      try {
         return getLinkManager().hasArtifacts(side);
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return false;

   }

   public Set<Artifact> getArtifacts(IRelationEnumeration side) throws SQLException {
      return getLinkManager().getArtifacts(side);
   }

   public Artifact getFirstArtifact(IRelationEnumeration side) throws SQLException {
      Collection<Artifact> arts = this.getArtifacts(side);
      if (arts.size() > 0) return arts.iterator().next();
      return null;
   }

   /**
    * @param <A>
    * @param side
    * @param clazz
    * @throws SQLException
    */
   public <A extends Artifact> Set<A> getArtifacts(IRelationEnumeration side, Class<A> clazz) throws SQLException {
      RelationLinkGroup group = getLinkManager().getGroup(side);

      if (group == null) return new HashSet<A>();

      return group.getArtifacts(clazz);
   }

   /**
    * Called upon completion of the initialization of an artifact when it is initially created. This allows sub-class
    * artifacts to set default attributes or do default processing.
    */
   public void onBirth() throws OseeCoreException {
   };

   /**
    * Called upon completion of the initialization of an artifact when loaded from the persistence layer, and when
    * initially created. When called upon initial creation, it is called after <code>onBirth()</code>. This allows
    * sub-class artifacts to set default attributes or do default processing.
    */
   public void onInitializationComplete() {
   };

   /**
    * @return Returns the artId.
    */
   public int getArtId() {
      return artId;
   }

   /**
    * @return Returns the artTypeId.
    */
   public int getArtTypeId() {
      return artifactType.getArtTypeId();
   }

   /**
    * @return Returns the branch.
    */
   public Branch getBranch() {
      return branch;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.core.Unique#getGUID()
    */
   public String getGuid() {
      return guid;
   }

   public String getArtifactTypeName() throws SQLException {
      return artifactType.getName();
   }

   public String getArtifactTypeNameSuppressException() {
      try {
         return getArtifactTypeName();
      } catch (SQLException ex) {
         return ex.getLocalizedMessage();
      }
   }

   /**
    * @param artifactType
    * @return whether this artifact's type or any of its super-types are the specified type
    * @throws SQLException
    */
   public boolean isOfType(String artifactType) throws SQLException {
      if (artifactType.equals("Abstract Software Requirement") && (getArtifactTypeName().equals(
            Requirements.SOFTWARE_REQUIREMENT) || getArtifactTypeName().equals(
            Requirements.INDIRECT_SOFTWARE_REQUIREMENT))) {
         return true;
      }
      return getArtifactTypeName().equals(artifactType);
   }

   public String toString() {
      return getDescriptiveName();
   }

   public Artifact getParent() throws SQLException {
      return getLinkManager().getSoleArtifact(DEFAULT_HIERARCHICAL__PARENT);
   }

   public boolean isOrphan() throws SQLException, MultipleArtifactsExist, ArtifactDoesNotExist {
      Artifact root = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(getBranch());
      for (Artifact parent = getParent(); parent != null; parent = parent.getParent()) {
         if (parent.equals(root)) {
            return false;
         }
      }
      return true;
   }

   public Artifact getChild(String descriptiveName) throws SQLException {
      for (Artifact artifact : getChildren()) {
         if (artifact.getDescriptiveName().equals(descriptiveName)) {
            return artifact;
         }
      }
      throw new IllegalArgumentException("No child with the name \"" + descriptiveName + "\" exists");
   }

   /**
    * @return set of the direct children of this artifact
    * @throws SQLException
    */
   public Set<Artifact> getChildren() throws SQLException {
      return getArtifacts(DEFAULT_HIERARCHICAL__CHILD);
   }

   /**
    * @return a list of artifacts ordered by a depth first traversal of this artifact's descendants
    * @throws SQLException
    */
   public List<Artifact> getDescendants() throws SQLException {
      List<Artifact> descendants = new LinkedList<Artifact>();
      getDescendants(descendants);
      return descendants;
   }

   private void getDescendants(Collection<Artifact> descendants) throws SQLException {
      for (Artifact child : getChildren()) {
         descendants.add(child);
         child.getDescendants(descendants);
      }
   }

   /**
    * @param artifact
    * @throws SQLException
    */
   public void addChild(Artifact artifact) throws SQLException {
      relate(DEFAULT_HIERARCHICAL__CHILD, artifact);
   }

   /**
    * creates a new child using descriptor, relates it to its parent, and persists the child
    * 
    * @param artifactType
    * @param name TODO
    * @throws SQLException
    * @throws OseeCoreException
    */
   public Artifact addNewChild(ArtifactSubtypeDescriptor descriptor, String name) throws SQLException, OseeCoreException {
      Artifact child = descriptor.makeNewArtifact(branch);
      child.setDescriptiveName(name);
      addChild(child);
      child.persistAttributes();
      child.getLinkManager().persistLinks();
      return child;
   }

   public void addChildren(List<? extends Artifact> artifacts) throws SQLException {
      for (Artifact artifact : artifacts) {
         addChild(artifact);
      }
   }

   /**
    * Creates a new <code>Attribute</code> of the given attribute type. This method should not be called by
    * applications. Use addAttribute() instead
    * 
    * @param artifact
    * @return the newly created attribute
    */
   public <T> Attribute<T> createAttribute(AttributeType attributeType) {
      try {

         Object[] params = new Object[] {attributeType, this};
         Class<? extends Attribute<T>> attributeClass =
               (Class<? extends Attribute<T>>) attributeType.getBaseAttributeClass();

         //TODO: JPhillips - This should be removed when the blob attribute conversion is complete
         if (this instanceof WordArtifact && attributeType.getName().equals("Word Formatted Content")) {
            WordArtifact wordArtifact = (WordArtifact) this;

            if (wordArtifact.isWholeWordArtifact()) {
               attributeClass = (Class<? extends Attribute<T>>) WordWholeDocumentAttribute.class;
            } else {
               attributeClass = (Class<? extends Attribute<T>>) WordTemplateAttribute.class;
            }
         }

         Constructor<? extends Attribute<T>> attributeConstructor =
               attributeClass.getConstructor(new Class[] {AttributeType.class, Artifact.class});
         Attribute<T> attribute = attributeConstructor.newInstance(params);

         Constructor<? extends IAttributeDataProvider> providerConstructor =
               attributeType.getProviderAttributeClass().getConstructor(new Class[] {Attribute.class});
         IAttributeDataProvider provider = providerConstructor.newInstance(new Object[] {attribute});
         attribute.setAttributeDataProvider(provider);
         attribute.initializeToDefaultValue();
         attributes.put(attributeType.getName(), attribute);
         return attribute;
      } catch (Exception ex) {
         // using reflections causes five different exceptions to be thrown which is too messy and will be very rare
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return null;
   }

   /**
    * @param attributeName
    * @return true if attributeName is valid for the artifact type of this artifact
    * @throws SQLException
    */
   public boolean isAttributeTypeValid(String attributeName) throws SQLException {
      Collection<AttributeType> attributeTypes =
            ConfigurationPersistenceManager.getInstance().getAttributeTypesFromArtifactType(getArtifactTypeName(),
                  branch);
      for (AttributeType attributeType : attributeTypes) {
         if (attributeType.getName().equals(attributeName)) {
            return true;
         }
      }
      return false;
   }

   /**
    * The use of this method is discouraged since it directly returns Attributres.
    * 
    * @param <T>
    * @param attributeTypeName
    * @return
    * @throws SQLException
    */
   public <T> List<Attribute<T>> getAttributes(String attributeTypeName) throws SQLException {
      ensureAttributesLoaded();
      Collection<Attribute<?>> selectedAttributes = attributes.getValues(attributeTypeName);
      if (selectedAttributes == null) {
         return java.util.Collections.emptyList();
      }
      return Collections.castAll(selectedAttributes);
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    * 
    * @return
    * @throws SQLException
    */
   public List<Attribute<?>> getAttributes() throws SQLException {
      ensureAttributesLoaded();
      return attributes.getValues();
   }

   private void ensureAttributesLoaded() throws SQLException {
      if (!isAttributesLoaded() && isInDb()) {
         ArtifactLoader.loadArtifactData(this, ArtifactLoad.ATTRIBUTE);
      }
   }

   public boolean isAttributesLoaded() {
      return !attributes.isEmpty();
   }

   public Collection<AttributeType> getAttributeTypes() throws SQLException {
      return ConfigurationPersistenceManager.getInstance().getAttributeTypesFromArtifactType(getArtifactTypeName(),
            branch);
   }

   private <T> Attribute<T> getSoleAttribute(String attributeTypeName) throws SQLException, MultipleAttributesExist {
      ensureAttributesLoaded();
      Collection<Attribute<?>> soleAttributes = attributes.getValues(attributeTypeName);
      if (soleAttributes == null) {
         return null;
      } else if (soleAttributes.size() > 1) {
         throw new MultipleAttributesExist(String.format(
               "The attribute \'%s\' can have no more than one instance for sole attribute operations; guid \'%s\'",
               attributeTypeName, getGuid()));
      }
      return (Attribute<T>) (soleAttributes.iterator().next());
   }

   private <T> Attribute<T> getOrCreateSoleAttribute(String attributeTypeName) throws SQLException, MultipleAttributesExist {
      Attribute<T> attribute = getSoleAttribute(attributeTypeName);
      if (attribute == null) {
         attribute = createAttribute(AttributeTypeManager.getType(attributeTypeName));
      }
      return attribute;
   }

   /**
    * Return sole attribute value for given attribute type name. Will throw exceptions if "Sole" nature of attribute is
    * invalid.<br>
    * <br>
    * Used for quick access to attribute value that should only have 0 or 1 instances of the attribute.
    * 
    * @param <T>
    * @param attributeTypeName
    * @return sole attribute value
    * @throws AttributeDoesNotExist if no attribute instance exists for this artifact
    * @throws MultipleAttributesExist if 2 or more attribute instances exist for this artifact
    * @throws IllegalStateException
    * @throws SQLException
    */
   public <T> T getSoleAttributeValue(String attributeTypeName) throws AttributeDoesNotExist, MultipleAttributesExist, SQLException {
      ensureAttributesLoaded();
      Collection<Attribute<?>> soleAttributes = attributes.getValues(attributeTypeName);
      if (soleAttributes == null) {
         throw new AttributeDoesNotExist(
               "Attribute \"" + attributeTypeName + "\" does not exist for artifact " + getHumanReadableId());
      } else if (soleAttributes.size() > 1) {
         throw new MultipleAttributesExist(
               "Attribute \"" + attributeTypeName + "\" must have exactly one instance.  It currently has " + soleAttributes.size() + ".");
      }
      return (T) soleAttributes.iterator().next().getValue();
   }

   /**
    * Return sole attribute string value for given attribute type name. Handles AttributeDoesNotExist case by returning
    * defaultReturnValue.<br>
    * <br>
    * Used for display purposes where toString() of attribute is to be displayed.
    * 
    * @param attributeTypeName
    * @param defaultReturnValue return value if attribute instance does not exist
    * @return
    * @throws MultipleAttributesExist if multiple attribute instances exist
    * @throws SQLException
    */
   public String getSoleAttributeValueAsString(String attributeTypeName, String defaultReturnValue) throws MultipleAttributesExist, SQLException {
      try {
         return getSoleAttributeValue(attributeTypeName).toString();
      } catch (AttributeDoesNotExist ex) {
         return defaultReturnValue;
      }
   }

   /**
    * Return sole attribute value for given attribute type name Handles AttributeDoesNotExist case by returning
    * defaultReturnValue.<br>
    * <br>
    * Used for purposes where attribute value of specified type is desired.
    * 
    * @param <T>
    * @param attributeTypeName
    * @param defaultReturnValue
    * @return
    * @throws MultipleAttributesExist if multiple attribute instances exist
    * @throws SQLException
    */
   public <T> T getSoleAttributeValue(String attributeTypeName, T defaultReturnValue) throws MultipleAttributesExist, SQLException {
      try {
         return getSoleAttributeValue(attributeTypeName);
      } catch (AttributeDoesNotExist ex) {
         return defaultReturnValue;
      }
   }

   /**
    * Return sole attribute value for given attribute type name or defaultReturnValue if no attribute instance exists
    * for this artifact.<br>
    * <br>
    * Used for purposes where attribute value of specified type is desired.<br>
    * <br>
    * NOTE: Use only for inline calls. This method returns identical data as
    * getSoleTAttributeValue(attributeTypeName,defaultReturnValue) but provides an extra parameter that allows it to be
    * called within another method call because it specifically defines the return type as clazz
    * 
    * @param <T>
    * @param attributeTypeName
    * @param defaultReturnValue
    * @param clazz class to be returned
    * @return
    * @throws MultipleAttributesExist if multiple attribute instances exist
    * @throws SQLException
    */
   public <T> T getSoleAttributeValue(String attributeTypeName, T defaultReturnValue, Class<T> clazz) throws MultipleAttributesExist, SQLException {
      return (T) getSoleAttributeValue(attributeTypeName, defaultReturnValue);
   }

   /**
    * Delete attribute if exactly one exists. Does nothing if attribute does not exist and throw MultipleAttributesExist
    * is more than one instance of the attribute type exsits for this artifact
    * 
    * @param attributeTypeName
    * @throws SQLException
    * @throws MultipleAttributesExist
    */
   public void deleteSoleAttribute(String attributeTypeName) throws SQLException, MultipleAttributesExist {
      Attribute<?> attribute = getSoleAttribute(attributeTypeName);
      if (attribute != null) {
         attribute.delete();
      }
   }

   public void deleteAttribute(String attributeTypeName, Object value) throws IllegalStateException, SQLException {
      for (Attribute<Object> attribute : getAttributes(attributeTypeName)) {
         if (attribute.getValue().equals(value)) attribute.delete();
      }
   }

   /**
    * Used on attribute types with no more than one instance. If the attribute exists, it's value is changed, otherwise
    * a new attribute is added and its value set.
    * 
    * @param <T>
    * @param attributeTypeName
    * @param value
    * @throws SQLException
    * @throws MultipleAttributesExist
    */
   public <T> void setSoleAttributeValue(String attributeTypeName, T value) throws SQLException, MultipleAttributesExist {
      try {
         getOrCreateSoleAttribute(attributeTypeName).setValue(value);
      } catch (OseeCoreException ex) {
         throw new SQLException(ex);
      }
   }

   @Deprecated
   /**
    * should call setSoleAttributeValue() unless the value needs to be converted from a string in which (less common)
    * case call setSoleAttributeFromString
    */
   public <T> void setSoleXAttributeValue(String attributeTypeName, String value) throws SQLException, MultipleAttributesExist {
      Attribute<T> attribute = getOrCreateSoleAttribute(attributeTypeName);
      if (attribute instanceof CharacterBackedAttribute) {
         try {
            ((CharacterBackedAttribute) attribute).setFromString(value);
         } catch (Exception ex) {
            throw new IllegalStateException(String.format("Unable to set attribute [%s] to [%s]",
                  attribute.getAttributeType(), value));
         }
      } else {
         throw new IllegalStateException(String.format("Attribute [%s] does not support this operation.",
               attribute.getAttributeType()));
      }
   }

   public <T> void setSoleAttributeFromString(String attributeTypeName, String value) throws OseeCoreException, SQLException {
      getOrCreateSoleAttribute(attributeTypeName).setFromString(value);
   }

   public void setSoleAttributeFromStream(String attributeTypeName, InputStream stream) throws OseeCoreException, MultipleAttributesExist, SQLException {
      getOrCreateSoleAttribute(attributeTypeName).setValueFromInputStream(stream);
   }

   /**
    * @param attributeTypeName
    * @return comma delimited representation of all the attributes of the type attributeName
    * @throws SQLException
    */
   public String getAttributesToString(String attributeTypeName) throws SQLException {
      StringBuffer sb = new StringBuffer();
      for (Attribute<?> attr : getAttributes(attributeTypeName)) {
         sb.append(attr);
         sb.append(", ");
      }
      return sb.toString().replaceFirst(", $", "");
   }

   /**
    * Uses the Dynamic Attribute Manager to set a group of attribute data strings into a set of attributes. Checks to
    * see if data value exists before adding and also removes those attribute values that are not in the input dataStrs.
    * 
    * @param attributeTypeName
    * @param dataStrs
    * @throws SQLException
    */
   public void setAttributeValues(String attributeTypeName, Collection<String> dataStrs) throws Exception {
      ensureAttributesLoaded();

      ArrayList<String> storedNames = new ArrayList<String>();

      AttributeType attributeType = AttributeTypeManager.getType(attributeTypeName);
      int minOccur = attributeType.getMinOccurrences();
      int maxOccur = attributeType.getMaxOccurrences();
      for (Attribute<?> attribute : getAttributes(attributeTypeName)) {
         storedNames.add(attribute.toString());
      }

      if (dataStrs.size() > maxOccur) throw new IllegalStateException(
            "Attempting to set " + dataStrs.size() + " when max =" + maxOccur);
      if (dataStrs.size() < minOccur) throw new IllegalStateException(
            "Attempting to set " + dataStrs.size() + " when min =" + minOccur);
      // If size to replace is same as size filled, need to reset existing attributes cause can't
      // add and then remove
      if (dataStrs.size() == maxOccur && !storedNames.equals(dataStrs)) {
         String[] dataStrsArr = dataStrs.toArray(new String[dataStrs.size()]);
         int x = 0;
         for (Attribute<?> attribute : attributes.getValues(attributeTypeName)) {
            if (attribute instanceof CharacterBackedAttribute) {
               ((CharacterBackedAttribute<?>) attribute).setFromString(dataStrsArr[x++]);
            }
         }
         return;
      }

      // Add items that are newly selected
      for (String value : dataStrs) {
         if (!storedNames.contains(value)) {
            addAttribute(attributeTypeName, value);
         }
      }

      // Remove items that aren't selected anymore
      for (String stored : storedNames) {
         if (!dataStrs.contains(stored)) {
            for (Attribute<?> attribute : attributes.getValues(attributeTypeName)) {
               if (attribute.toString().equals(stored)) {
                  attribute.delete();
               }
            }
         }
      }
   }

   /**
    * adds a new attribute of the type named attributeTypeName and assigns it the given value
    * 
    * @param <T>
    * @param attributeTypeName
    * @param value
    * @throws SQLException
    */
   public <T> void addAttribute(String attributeTypeName, T value) throws SQLException {
      try {
         createAttribute(AttributeTypeManager.getType(attributeTypeName)).setValue(value);
      } catch (OseeCoreException ex) {
         throw new SQLException(ex);
      }
   }

   /**
    * @param attributeTypeName
    * @return string collection representation of all the attributes of the type attributeName
    * @throws SQLException
    */
   public List<String> getAttributesToStringList(String attributeTypeName) throws SQLException {
      ensureAttributesLoaded();

      List<String> items = new ArrayList<String>();
      Collection<Attribute<?>> selectedAttributes = attributes.getValues(attributeTypeName);

      if (selectedAttributes != null) {
         for (Attribute<?> attribute : selectedAttributes) {
            items.add(attribute.toString());
         }
      }
      return items;
   }

   public String getDescriptiveName() {
      try {
         if (!isAttributeTypeValid("Name")) {
            throw new IllegalStateException(String.format(
                  "Artifact Type [%s] guid [%s] does not have the attribute type 'Name' which is required.",
                  getArtifactTypeName(), getGuid()));
         }
         return getSoleAttributeValue("Name");
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         return ex.getLocalizedMessage();
      }
   }

   public void setDescriptiveName(String name) throws SQLException {
      try {
         setSoleXAttributeValue("Name", name);
      } catch (MultipleAttributesExist ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public ArtifactFactory getFactory() {
      return parentFactory;
   }

   /**
    * This is used to mark that the artifact has been persisted. This should only be called by the
    * ArtifactPersistenceManager.
    * 
    * @throws SQLException
    */
   public void setNotDirty() throws SQLException {
      dirty = false;

      for (Attribute<?> attribute : getAttributes()) {
         attribute.setNotDirty();
      }
   }

   /**
    * Not supported in the public API for internal use only.
    * 
    * @param inTransaction
    */
   public void setInTransaction(boolean inTransaction) {
      this.inTransaction = inTransaction;
   }

   protected boolean isInTransaction() {
      return inTransaction;
   }

   /**
    * @return Returns the dirty.
    * @throws SQLException
    */
   public boolean isDirty() throws SQLException {
      return isDirty(false);
   }

   /**
    * @return Returns the dirty.
    * @throws SQLException
    */
   public boolean isDirty(boolean includeLinks) throws SQLException {
      boolean dirtyVal = dirty || anAttributeIsDirty();
      if (includeLinks) {
         dirtyVal |= getLinkManager().isDirty();
      }
      return dirtyVal;
   }

   public boolean isReadOnly() {
      return isHistorical() || !AccessControlManager.getInstance().checkObjectPermission(this, PermissionEnum.WRITE);
   }

   private boolean anAttributeIsDirty() throws SQLException {
      for (Attribute<?> attribute : getAttributes()) {
         if (attribute.isDirty()) {
            return true;
         }
      }
      return false;
   }

   /**
    * Reverts this artifact back to the last state saved. This will have no effect if the artifact has never been saved.
    * 
    * @throws SQLException
    * @throws IllegalStateException if the artifact is deleted
    */
   public void revert() throws SQLException {
      if (!isInDb()) return;

      attributes.clear();
      ArtifactLoader.loadArtifactData(this, ArtifactLoad.ATTRIBUTE);

      linkManager.revert();

      dirty = false;
      SkynetEventManager.getInstance().kick(new CacheArtifactModifiedEvent(this, ModType.Reverted, this));
   }

   public void persistAttributes() throws SQLException {
      ArtifactPersistenceManager.makePersistent(this, false);
   }

   public void persistAttributesAndRelations() throws SQLException {
      persistAttributes();
      getLinkManager().persistLinks();
   }

   public void persistRelations() throws SQLException {
      getLinkManager().persistLinks();
   }

   public void persist() throws SQLException {
      ArtifactPersistenceManager.makePersistent(this, true);
   }

   /**
    * make this method private
    * 
    * @param recurse
    * @throws SQLException
    */
   public void persist(boolean recurse) throws SQLException {
      ArtifactPersistenceManager.makePersistent(this, recurse);
   }

   /**
    * Returns all of the descendants through the primary decomposition tree that have a particular human readable id.
    * This will not return the called upon node if the name matches since it can not be a descendant of itself.
    * 
    * @param humanReadableId The human readable id text to match against.
    * @param caseSensitive Whether to use case sensitive matching.
    * @return <code>Collection</code> of <code>Artifact</code>'s that match.
    */
   public Collection<Artifact> getDescendants(String humanReadableId, boolean caseSensitive) {
      Collection<Artifact> descendants = new LinkedList<Artifact>();

      try {
         for (Artifact child : getChildren()) {
            if ((caseSensitive && child.getDescriptiveName().equals(humanReadableId)) || (!caseSensitive && child.getDescriptiveName().equalsIgnoreCase(
                  humanReadableId))) {
               descendants.add(child);
            }
            descendants.addAll(child.getDescendants(humanReadableId, caseSensitive));
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

      return descendants;
   }

   /**
    * Returns all of the descendants through the primary decomposition tree that are already loaded.
    */
   public Collection<Artifact> getLoadedDescendants() {
      Collection<Artifact> descendants = new LinkedList<Artifact>();

      try {
         if (isLinksLoaded()) {
            for (Artifact child : getChildren()) {
               descendants.add(child);
               descendants.addAll(child.getLoadedDescendants());
            }
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

      return descendants;
   }

   /**
    * Return relations that exist between artifacts
    * 
    * @throws SQLException
    */
   public ArrayList<RelationLink> getRelations(Artifact artifact) throws SQLException {
      ArrayList<RelationLink> links = new ArrayList<RelationLink>();
      for (RelationLink link : getLinkManager().getLinks()) {
         if (getLinkManager().getOtherSideAritfact(link).equals(artifact)) links.add(link);
      }
      return links;
   }

   public ArrayList<RelationLink> getRelations(IRelationType relationType) throws SQLException {
      ArrayList<RelationLink> links = new ArrayList<RelationLink>();
      for (RelationLink link : getLinkManager().getLinks()) {
         if (link.getRelationType().equals(relationType)) {
            links.add(link);
         }
      }
      return links;
   }

   /**
    * Return relations that exist between artifacts of type side
    * 
    * @throws SQLException
    */
   public ArrayList<RelationLink> getRelations(IRelationEnumeration side, Artifact artifact) throws SQLException {
      ArrayList<RelationLink> links = new ArrayList<RelationLink>();
      for (RelationLink link : getLinkManager().getLinks()) {
         if (getLinkManager().getOtherSideAritfact(link).equals(artifact)) if (side.isThisType(link)) links.add(link);
      }
      return links;
   }

   /**
    * Removes artifact from a specific branch
    * 
    * @throws SQLException
    */
   public void delete() throws Exception {
      ArtifactPersistenceManager.deleteArtifact(this);
   }

   /**
    * Remove artifact from a specific branch in the database
    * 
    * @throws SQLException
    */
   public void purgeFromBranch() throws Exception {
      ArtifactPersistenceManager.getInstance().purgeArtifactFromBranch(this);
   }

   /**
    * Remove artifact from the database
    * 
    * @throws SQLException
    */
   public void purge() throws SQLException {
      ArtifactPersistenceManager.purgeArtifact(this);
   }

   public boolean isDeleted() {
      return deleted;
   }

   public void setDeleted(int deletionTransactionId) {
      this.deletionTransactionId = deletionTransactionId;
      this.deleted = true;
   }

   /**
    * use setDeleted(int deletionTransactionId) instead
    */
   @Deprecated
   protected void setDeleted() {
      setDeleted(-1);
   }

   public void setDirty() {
      dirty = true;
   }

   /**
    * Ensures the linkManager has been created and populated with links and then returns it
    * 
    * @return Returns the linkManager.
    * @throws SQLException
    */
   public LinkManager getLinkManager() throws SQLException {
      linkManager.ensurePopulated();
      return linkManager;
   }

   public void setLinksLoaded() {
      linksLoaded = true;
   }

   /**
    * @param relationSide
    * @param artifact
    * @throws SQLException
    */
   public void relate(IRelationEnumeration relationSide, Artifact artifact) throws SQLException {
      relate(relationSide, artifact, null, false);
   }

   public void relate(IRelationEnumeration relationSide, Artifact artifact, boolean persist) throws SQLException {
      relate(relationSide, artifact, null, persist);
   }

   public void relate(IRelationEnumeration relationSide, Artifact artifact, String rationale, boolean persist) throws SQLException {
      getLinkManager().ensureRelationGroupExists(relationSide).addArtifact(artifact, rationale, persist);
   }

   public void relate(IRelationEnumeration relationSide, Collection<? extends Artifact> artifacts, boolean persist) throws SQLException {
      relate(relationSide, artifacts, null, persist);
   }

   public void relate(IRelationEnumeration relationSide, Collection<? extends Artifact> artifacts, String rationale, boolean persist) throws SQLException {
      for (Artifact art : artifacts)
         relate(relationSide, art, rationale, persist);
   }

   public void relate(IRelationEnumeration relationSide, Collection<? extends Artifact> artifacts) {
      try {
         relate(relationSide, artifacts, false);
      } catch (SQLException ex) {
         // This should never happen
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public void unrelate(IRelationEnumeration relationSide, Artifact artifact) {
      try {
         unrelate(relationSide, artifact, false);
      } catch (SQLException ex) {
         // Should never happen because not persisting
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public void unrelate(IRelationEnumeration relationSide, Artifact artifact, boolean persist) throws SQLException {
      getLinkManager().ensureRelationGroupExists(relationSide).removeArtifact(artifact);
      if (persist) linkManager.persistLinks();
   }

   public void relateReplace(IRelationEnumeration relationSide, Artifact artifact, boolean persist) throws SQLException {
      relateReplace(relationSide, Arrays.asList(new Artifact[] {artifact}), persist);
   }

   public void relateReplace(IRelationEnumeration relationSide, Collection<? extends Artifact> artifacts, boolean persist) throws SQLException {
      RelationLinkGroup group = getLinkManager().ensureRelationGroupExists(relationSide);
      group.removeAll();
      for (Artifact art : artifacts) {
         group.addArtifact(art);
      }
      if (persist) linkManager.persistLinks();
   }

   public final boolean isLinksLoaded() {
      return linksLoaded;
   }

   /**
    * @return Returns the humanReadableId.
    */
   public String getHumanReadableId() {
      return humanReadableId;
   }

   public void rollHumanReadableId() {
      humanReadableId = generateHumanReadableId();
   }

   private static final char[][] chars =
         new char[][] {
               {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K',
                     'L', 'M', 'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'},
               {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'B', 'C', 'D', 'F', 'G', 'H', 'J', 'K', 'L', 'M',
                     'N', 'P', 'Q', 'R', 'S', 'T', 'V', 'W', 'X', 'Y', 'Z'}};
   private static final int[] charsIndexLookup = new int[] {0, 1, 1, 1, 0};

   /**
    * 5 character human readable identifier where the first and last characters are in the range [A-Z0-9] except 'I' and
    * 'O' and the middle three characters have the same range as above with the additional restrictions of 'A', 'E', 'U'
    * thus the total number of unique values is: 34 * 31 * 31 *31 * 34 = 34,438,396
    */
   private static String generateHumanReadableId() {
      int seed = (int) (Math.random() * 34438396);
      char id[] = new char[charsIndexLookup.length];

      for (int i = 0; i < id.length; i++) {
         int radix = chars[charsIndexLookup[i]].length;
         id[i] = chars[charsIndexLookup[i]][seed % radix];
         seed = seed / radix;
      }
      return new String(id);
   }

   /**
    * @return Returns the descriptor.
    */
   public ArtifactSubtypeDescriptor getArtifactType() {
      return artifactType;
   }

   public String getVersionedName() {
      String name = getDescriptiveName();

      if (isHistorical()) {
         name += " [Rev:" + transactionId + "]";
      }

      return name;
   }

   /**
    * Return true if this artifact any of it's links specified or any of the artifacts on the other side of the links
    * are dirty
    * 
    * @param links
    */
   public Result isRelationsAndArtifactsDirty(Set<IRelationEnumeration> links) {
      try {
         if (isDirty()) {

            for (Attribute<?> attribute : getAttributes()) {
               if (attribute.isDirty()) {
                  return new Result(true, "===> Dirty Attribute - " + attribute.getAttributeType().getName() + "\n");
               }
            }
            return new Result(true, "Artifact isDirty == true??");
         }
         // Loop through all relations
         for (IRelationEnumeration side : links) {
            for (Artifact art : getArtifacts(side)) {
               // Check artifact dirty
               if (art.isDirty()) {
                  return new Result(true, art.getArtifactTypeName() + " \"" + art + "\" => dirty\n");
               }
               // Check the links to this artifact
               for (RelationLink link : getRelations(side, art))
                  if (link.isDirty()) {
                     return new Result(true, "Link \"" + link + "\" => dirty\n");
                  }
            }
         }
      } catch (SQLException ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return Result.FalseResult;
   }

   /**
    * Save artifact, any of it's links specified, and any of the artifacts on the other side of the links that are dirty
    */
   public void saveArtifactsFromRelations(Set<IRelationEnumeration> links) throws SQLException {
      saveRevertArtifactsFromRelations(links, false);
   }

   /**
    * Revert artifact, any of it's links specified, and any of the artifacts on the other side of the links that are
    * dirty
    */
   public void revertArtifactsFromRelations(Set<IRelationEnumeration> links) throws SQLException {
      saveRevertArtifactsFromRelations(links, true);
   }

   @Deprecated
   //  use persistAttributesAndLinks() instead
   private void saveRevertArtifactsFromRelations(Set<IRelationEnumeration> links, boolean revert) throws SQLException {
      Set<Artifact> artifactToManipulate = new HashSet<Artifact>();
      artifactToManipulate.add(this);
      Set<RelationLink> linksToManipulate = new HashSet<RelationLink>();

      // Loop through all relations and collect all artifact to operate on
      for (IRelationEnumeration side : links) {
         for (Artifact artifact : getArtifacts(side)) {
            artifactToManipulate.add(artifact);
            // Check the links to this artifact
            for (RelationLink link : getRelations(artifact)) {
               linksToManipulate.add(link);
            }
         }
      }
      // Loop through all relations and persist/revert as necessary
      for (RelationLink link : linksToManipulate) {
         if (link.isDirty()) {
            if (revert) {
               link.delete();
            } else {
               link.persist();
            }
         }
      }
      // Loop through all artifacts and persist/revert as necessary
      for (Artifact artifact : artifactToManipulate) {
         if (revert) {
            artifact.revert();
         } else {
            artifact.persistAttributes();
         }
      }
      // Persist link manager to ensure deleted links get persisted
      //TODO: this defeats the whole purpose of a selective persist based on link type
      getLinkManager().persistLinks();
   }

   /**
    * Creates a new artifact and duplicates all of its attribute data.
    * 
    * @throws OseeCoreException
    * @throws Exception
    */
   public Artifact duplicate(Branch branch) throws SQLException, OseeCoreException {
      Artifact newArtifact = artifactType.makeNewArtifact(branch);
      copyAttributes(newArtifact);
      return newArtifact;
   }

   private void copyAttributes(Artifact artifact) throws SQLException {
      for (Attribute<?> attribute : getAttributes()) {
         artifact.addAttribute(attribute.getAttributeType().getName(), attribute.getValue());
      }
   }

   public void setIds(int artId, int gammaId) {
      setIds(artId, gammaId, 0);
   }

   public void setIds(int artId, int gammaId, int transactionId) {
      this.gammaId = gammaId;
      this.artId = artId;
      this.transactionId = transactionId;
   }

   /**
    * @return the transaction number for this artifact if it is historical, otherwise 0
    */
   public int getTransactionNumber() {
      return transactionId;
   }

   /**
    * @return Returns the gammaId.
    */
   public int getGammaId() {
      return gammaId;
   }

   /**
    * @return Returns dirty attributes.
    * @throws SQLException
    */
   public Collection<SkynetAttributeChange> getDirtyAttributeSkynetAttributeChanges() throws SQLException {
      List<SkynetAttributeChange> dirtyAttributes = new LinkedList<SkynetAttributeChange>();

      for (Attribute<?> attribute : getAttributes()) {
         if (attribute.isDirty()) {
            dirtyAttributes.add(new SkynetAttributeChange(attribute.getAttributeType().getName(), attribute.getValue(),
                  attribute.getAttrId(), attribute.getGammaId()));
         }
      }
      return dirtyAttributes;
   }

   /**
    * Changes the artifact type in the database.
    * 
    * @param artifactType
    * @throws SQLException
    */
   public void changeArtifactType(ArtifactSubtypeDescriptor artifactType) throws SQLException {
      ArtifactPersistenceManager.changeArtifactSubStype(this, artifactType);
      this.artifactType = artifactType;
   }

   private static final Pattern safeNamePattern = Pattern.compile("[^A-Za-z0-9 ]");
   private static final String[] NUMBER =
         new String[] {"Zero", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine"};

   /**
    * Since artifact names are free text it is important to reformat the name to ensure it is suitable as an element
    * name
    * 
    * @return artifact name in a form that is valid as an XML element
    */
   public String getSafeName() {
      String elementName = safeNamePattern.matcher(getDescriptiveName()).replaceAll("_");

      // Ensure the name did not end up empty
      if (elementName.equals("")) {
         elementName = "nameless";
      }

      // Fix the first character if it is a number by replacing it with its name
      char firstChar = elementName.charAt(0);
      if (firstChar >= '0' && firstChar <= '9') {
         elementName = NUMBER[firstChar - '0'] + elementName.substring(1);
      }

      if (elementName.length() > 75) {
         elementName = elementName.substring(0, 75);
      }

      return elementName;
   }

   @SuppressWarnings("deprecation")
   private Set<IArtifactAnnotation> artifactAnnotationExtensions;

   private Set<IArtifactAnnotation> getAnnotationExtensions() {
      if (artifactAnnotationExtensions != null) return artifactAnnotationExtensions;
      artifactAnnotationExtensions = new HashSet<IArtifactAnnotation>();
      IExtensionPoint point =
            Platform.getExtensionRegistry().getExtensionPoint(
                  "org.eclipse.osee.framework.skynet.core.ArtifactAnnotation");
      if (point == null) {
         System.err.println("Can't access ArtifactAnnotation extension point");
         return artifactAnnotationExtensions;
      }
      IExtension[] extensions = point.getExtensions();
      for (IExtension extension : extensions) {
         IConfigurationElement[] elements = extension.getConfigurationElements();
         String classname = null;
         String bundleName = null;
         for (IConfigurationElement el : elements) {
            if (el.getName().equals("ArtifactAnnotation")) {
               classname = el.getAttribute("classname");
               bundleName = el.getContributor().getName();
               if (classname != null && bundleName != null) {
                  Bundle bundle = Platform.getBundle(bundleName);
                  try {
                     Class<?> taskClass = bundle.loadClass(classname);
                     Object obj = taskClass.newInstance();
                     artifactAnnotationExtensions.add((IArtifactAnnotation) obj);
                  } catch (Exception ex) {
                     ex.printStackTrace();
                  }
               }

            }
         }
      }
      return artifactAnnotationExtensions;
   }

   /**
    * @return the annotationMgr
    */
   public AttributeAnnotationManager getAnnotationMgr() {
      if (annotationMgr == null) {
         annotationMgr = new AttributeAnnotationManager(this);
      }
      return annotationMgr;
   }

   public int getDeletionTransactionId() throws SQLException {
      return deletionTransactionId;
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) throw new IllegalArgumentException("adapter can not be null");

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Artifact otherArtifact) {
      if (otherArtifact == null || otherArtifact.isDeleted()) {
         return -1;
      } else if (this.isDeleted()) {
         return 1;
      }

      int diff;
      if (otherArtifact.equals(this)) {
         diff = 0;
      } else {
         try {
            diff = getDescriptiveName().compareTo(otherArtifact.getDescriptiveName());
         } catch (Exception ex) {
            diff = 0;
         }
      }

      return diff;
   }

   /* (non-Javadoc)
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode() {
      return 31 * guid.hashCode();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Artifact) {
         return guid.hashCode() == ((Artifact) obj).getGuid().hashCode();
      }
      return false;
   }

   public int getRemainingAttributeCount(AttributeType attributeType) throws SQLException {
      return attributeType.getMaxOccurrences() - getAttributeCount(attributeType.getName());
   }

   public int getAttributeCount(String attributeTypeName) throws SQLException {
      ensureAttributesLoaded();

      Collection<Attribute<?>> tempAttributes = attributes.getValues(attributeTypeName);
      if (tempAttributes == null) {
         return 0;
      }
      return tempAttributes.size();
   }

   /**
    * @return
    * @throws SQLException
    */
   public boolean hasChildren() throws SQLException {
      return getLinkManager().getRelationCount(DEFAULT_HIERARCHICAL__CHILD) > 0;
   }
}