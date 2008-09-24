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

import static org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
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
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.AttributeAnnotationManager;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.IArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.CharacterBackedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.providers.IAttributeDataProvider;
import org.eclipse.osee.framework.skynet.core.change.ModificationType;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.skynet.core.exception.TransactionDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class Artifact implements IAdaptable, Comparable<Artifact> {
   public static final String UNNAMED = "Unnamed";
   public static final String BEFORE_GUID_STRING = "/BeforeGUID/PrePend";
   public static final String AFTER_GUID_STRING = "/AfterGUID";
   private final HashCollection<String, Attribute<?>> attributes =
         new HashCollection<String, Attribute<?>>(false, LinkedList.class, 12);
   private boolean dirty = false;
   private boolean deleted = false;
   private final Branch branch;
   private final String guid;
   private ArtifactType artifactType;
   private String humanReadableId;
   private final ArtifactFactory parentFactory;
   private AttributeAnnotationManager annotationMgr;
   private TransactionId transactionId;
   private int artId;
   private int gammaId;
   private boolean linksLoaded;
   private boolean historical;

   protected Artifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) {

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
      return gammaId > 0;
   }

   /**
    * A historical artifact always corresponds to a fixed revision of an artifact
    * 
    * @return
    */
   public boolean isHistorical() {
      return historical;
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
      if (AccessControlManager.hasLock(this)) {
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

   /**
    * All the artifacts related to this artifact by relations of type relationTypeName are returned in a list order
    * based on the stored relation order
    * 
    * @param relationTypeName
    * @return
    * @throws ArtifactDoesNotExist
    * @throws OseeDataStoreException
    */
   public List<Artifact> getRelatedArtifacts(String relationTypeName) throws ArtifactDoesNotExist, OseeDataStoreException {
      try {
         return RelationManager.getRelatedArtifacts(this, RelationTypeManager.getType(relationTypeName));
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public List<Artifact> getRelatedArtifacts(RelationType relationType) throws ArtifactDoesNotExist, OseeDataStoreException {
      try {
         return RelationManager.getRelatedArtifacts(this, relationType);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public List<Artifact> getRelatedArtifacts(IRelationEnumeration relationEnum) throws SQLException {
      try {
         return RelationManager.getRelatedArtifacts(this, relationEnum);
      } catch (ArtifactDoesNotExist ex) {
         throw new SQLException(ex);
      }
   }

   /**
    * Get the exactly one artifact related to this artifact by relations of type relationType are returned in a list
    * order based on
    * 
    * @param relationType
    * @return
    * @throws ArtifactDoesNotExist
    * @throws OseeDataStoreException
    * @throws MultipleArtifactsExist
    */
   public Artifact getRelatedArtifact(IRelationEnumeration relationEnum) throws ArtifactDoesNotExist, OseeDataStoreException, MultipleArtifactsExist {
      try {
         return RelationManager.getRelatedArtifact(this, relationEnum);
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   public int getRelatedArtifactsCount(IRelationEnumeration relationEnum) throws OseeDataStoreException {
      try {
         return RelationManager.getRelatedArtifactsCount(this, relationEnum.getRelationType(), relationEnum.getSide());
      } catch (SQLException ex) {
         throw new OseeDataStoreException(ex);
      }
   }

   /**
    * @param <A>
    * @param side
    * @param clazz
    * @throws SQLException
    */
   public <A extends Artifact> List<A> getArtifacts(IRelationEnumeration side, Class<A> clazz) throws SQLException {
      return Collections.castAll(getRelatedArtifacts(side));
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

   @Deprecated
   public void onAttributePersist() throws OseeCoreException {
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

   public String getArtifactTypeName() {
      return artifactType.getName();
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

   @Override
   public String toString() {
      return getDescriptiveName();
   }

   //TODO should not return null but currently application code expects it to
   public Artifact getParent() throws SQLException {
      try {
         return RelationManager.getRelatedArtifact(this, CoreRelationEnumeration.DEFAULT_HIERARCHICAL__PARENT);
      } catch (ArtifactDoesNotExist ex) {
         return null;
      } catch (MultipleArtifactsExist ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
      return null;
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

   public boolean hasChild(String descriptiveName) throws SQLException {
      boolean result = false;
      try {
         if (getChild(descriptiveName) != null) {
            result = true;
         }
      } catch (IllegalArgumentException ex) {
      }
      return result;
   }

   /**
    * @return set of the direct children of this artifact
    * @throws SQLException
    */
   public List<Artifact> getChildren() throws SQLException {
      return getRelatedArtifacts(DEFAULT_HIERARCHICAL__CHILD);
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
      addRelation(DEFAULT_HIERARCHICAL__CHILD, artifact);
   }

   /**
    * creates a new child using descriptor, relates it to its parent, and persists the child
    * 
    * @param artifactType
    * @param name
    * @throws SQLException
    * @throws OseeCoreException
    */
   public Artifact addNewChild(ArtifactType artifactType, String name) throws SQLException, OseeCoreException {
      Artifact child = artifactType.makeNewArtifact(branch);
      child.setDescriptiveName(name);
      addChild(child);
      child.persistAttributesAndRelations();
      return child;
   }

   public void addChildren(List<? extends Artifact> artifacts) throws SQLException {
      for (Artifact artifact : artifacts) {
         addChild(artifact);
      }
   }

   /**
    * Creates an instance of <code>Attribute</code> of the given attribute type. This method should not be called by
    * applications. Use addAttribute() instead
    * 
    * @param <T>
    * @param attributeType
    * @param existingAttribute specifies whether this attribute is new or is being loaded from the database
    * @return
    */
   public <T> Attribute<T> createAttribute(AttributeType attributeType, boolean newAttribute) {
      try {

         attributeType = AttributeTypeManager.getTypeWithWordContentCheck(this, attributeType.getName());

         Object[] params = new Object[] {attributeType, this};
         Class<? extends Attribute<T>> attributeClass =
               (Class<? extends Attribute<T>>) attributeType.getBaseAttributeClass();

         Constructor<? extends Attribute<T>> attributeConstructor =
               attributeClass.getConstructor(new Class[] {AttributeType.class, Artifact.class});
         Attribute<T> attribute = attributeConstructor.newInstance(params);

         Constructor<? extends IAttributeDataProvider> providerConstructor =
               attributeType.getProviderAttributeClass().getConstructor(new Class[] {Attribute.class});
         IAttributeDataProvider provider = providerConstructor.newInstance(new Object[] {attribute});
         attribute.setAttributeDataProvider(provider);

         attributes.put(attributeType.getName(), attribute);
         if (newAttribute) {
            attribute.initializeToDefaultValue();
         }
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
            ConfigurationPersistenceManager.getAttributeTypesFromArtifactType(getArtifactTypeName(), branch);
      for (AttributeType attributeType : attributeTypes) {
         if (attributeType.getName().equals(attributeName)) {
            return true;
         }
      }
      return false;
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    * 
    * @param <T>
    * @param attributeTypeName
    * @return
    * @throws SQLException
    */
   public <T> List<Attribute<T>> getAttributes(String attributeTypeName) throws SQLException {
      ensureAttributesLoaded();
      List<Attribute<?>> notDeltedAttributes = new ArrayList<Attribute<?>>();
      Collection<Attribute<?>> selectedAttributes = attributes.getValues(attributeTypeName);
      if (selectedAttributes == null) {
         return java.util.Collections.emptyList();
      }
      for (Attribute<?> attribute : selectedAttributes) {
         if (!attribute.isDeleted()) {
            notDeltedAttributes.add(attribute);
         }
      }
      return Collections.castAll(notDeltedAttributes);
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    * 
    * @return
    * @throws SQLException
    */
   public List<Attribute<?>> getAttributes(boolean includeDeleted) throws SQLException {
      ensureAttributesLoaded();
      List<Attribute<?>> notDeltedAttributes = new ArrayList<Attribute<?>>();
      for (String attributeTypeName : attributes.keySet()) {
         for (Attribute<?> attribute : attributes.getValues(attributeTypeName)) {
            if (!attribute.isDeleted() || includeDeleted) {
               notDeltedAttributes.add(attribute);
            }
         }
      }

      return notDeltedAttributes;
   }

   /**
    * This returns all attributes including deleted ones
    * 
    * @return
    */
   public List<Attribute<?>> internalGetAttributes() {
      return attributes.getValues();
   }

   void deleteAttributes() {
      for (Attribute<?> attribute : attributes.getValues()) {
         attribute.delete();
      }
   }

   private void ensureAttributesLoaded() throws SQLException {
      if (!isAttributesLoaded() && isInDb()) {
         try {
            ArtifactLoader.loadArtifactData(this, ArtifactLoad.ATTRIBUTE);
         } catch (OseeCoreException ex) {
            throw new SQLException(ex);
         }
      }
   }

   public boolean isAttributesLoaded() {
      return !attributes.isEmpty();
   }

   public Collection<AttributeType> getAttributeTypes() throws SQLException {
      return ConfigurationPersistenceManager.getAttributeTypesFromArtifactType(getArtifactTypeName(), branch);
   }

   public AttributeType getAttributeType(String attributeTypeName) throws SQLException {
      for (AttributeType attributeType : getAttributeTypes()) {
         if (attributeType.getName().equals(attributeTypeName)) {
            return attributeType;
         }
      }
      return null;
   }

   public <T> Attribute<T> getSoleAttribute(String attributeTypeName) throws SQLException, MultipleAttributesExist {
      ensureAttributesLoaded();
      List<Attribute<T>> soleAttributes = getAttributes(attributeTypeName);
      if (soleAttributes.size() == 0) {
         return null;
      } else if (soleAttributes.size() > 1) {
         throw new MultipleAttributesExist(String.format(
               "The attribute \'%s\' can have no more than one instance for sole attribute operations; guid \'%s\'",
               attributeTypeName, getGuid()));
      }
      return (soleAttributes.iterator().next());
   }

   private <T> Attribute<T> getOrCreateSoleAttribute(String attributeTypeName) throws SQLException, MultipleAttributesExist {
      Attribute<T> attribute = getSoleAttribute(attributeTypeName);
      if (attribute == null) {
         attribute = createAttribute(AttributeTypeManager.getType(attributeTypeName), true);
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
    * @throws IllegalStateException
    * @throws SQLException
    * @throws OseeCoreException
    */
   public <T> T getSoleAttributeValue(String attributeTypeName) throws SQLException, OseeCoreException {
      ensureAttributesLoaded();
      List<Attribute<T>> soleAttributes = getAttributes(attributeTypeName);
      if (soleAttributes.size() == 0) {
         throw new AttributeDoesNotExist(
               "Attribute \"" + attributeTypeName + "\" does not exist for artifact " + getHumanReadableId());
      } else if (soleAttributes.size() > 1) {
         throw new MultipleAttributesExist(
               "Attribute \"" + attributeTypeName + "\" must have exactly one instance.  It currently has " + soleAttributes.size() + ".");
      }
      return soleAttributes.iterator().next().getValue();
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
      } catch (OseeCoreException ex) {
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
         T value = getSoleAttributeValue(attributeTypeName);
         if (value == null) {
            OseeLog.log(
                  SkynetActivator.class,
                  Level.SEVERE,
                  "Attribute \"" + attributeTypeName + "\" has null value for Artifact " + getHumanReadableId() + " \"" + getDescriptiveName() + "\"");
            return defaultReturnValue;
         }
         return value;
      } catch (AttributeDoesNotExist ex) {
         return defaultReturnValue;
      } catch (OseeCoreException ex) {
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
      return getSoleAttributeValue(attributeTypeName, defaultReturnValue);
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

   public void deleteAttribute(String attributeTypeName, Object value) throws SQLException, OseeCoreException {
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
    * @throws OseeCoreException
    * @throws SQLException
    */
   public void setAttributeValues(String attributeTypeName, Collection<String> dataStrs) throws OseeCoreException, SQLException {
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
         for (Attribute<?> attribute : getAttributes(attributeTypeName)) {
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
            for (Attribute<?> attribute : getAttributes(attributeTypeName)) {
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
         createAttribute(AttributeTypeManager.getType(attributeTypeName), true).setValue(value);
      } catch (OseeCoreException ex) {
         throw new SQLException(ex);
      }
   }

   /**
    * adds a new attribute of the type named attributeTypeName and assigns it the given value
    * 
    * @param attributeTypeName
    * @param value
    * @throws SQLException
    */
   public <T> void addAttributeFromString(String attributeTypeName, String value) throws OseeCoreException, SQLException {
      try {
         createAttribute(AttributeTypeManager.getType(attributeTypeName), true).setFromString(value);
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
      for (Attribute<?> attribute : getAttributes(attributeTypeName)) {
         items.add(attribute.toString());
      }
      return items;
   }

   public String getInternalDescriptiveName() {
      String name = getInternalAttributeValue("Name");
      if (name.equals("")) return UNNAMED;
      return name;
   }

   /**
    * Return the String value of the first found attributeTypeName attribute whether deleted or not.
    * 
    * @param attributeTypeName
    * @return
    */
   public String getInternalAttributeValue(String attributeTypeName) {
      try {
         if (!isAttributeTypeValid(attributeTypeName)) {
            throw new IllegalStateException(String.format(
                  "Artifact Type [%s] guid [%s] does not have the attribute type 'Name' which is required.",
                  getArtifactTypeName(), getGuid()));
         }
         for (Attribute<?> attribute : internalGetAttributes()) {
            if (attribute.getAttributeType().getName().equals(attributeTypeName)) {
               return (String) attribute.getValue();
            }
         }
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         return ex.getLocalizedMessage();
      }
      return "";
   }

   public String getDescriptiveName() {
      try {
         if (!isAttributeTypeValid("Name")) {
            throw new IllegalStateException(String.format(
                  "Artifact Type [%s] guid [%s] does not have the attribute type 'Name' which is required.",
                  getArtifactTypeName(), getGuid()));
         }
         String name = getSoleAttributeValue("Name");
         return name == null ? UNNAMED : name;
      } catch (Exception ex) {
         SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         return ex.getLocalizedMessage();
      }
   }

   public void setDescriptiveName(String name) throws SQLException {
      try {
         setSoleAttributeValue("Name", name);
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

      for (Attribute<?> attribute : internalGetAttributes()) {
         attribute.setNotDirty();
      }
   }

   /**
    * This is used to mark that the artifact deleted. This should only be called by the RemoteEventManager.
    * 
    * @throws SQLException
    */
   public void setDeleted() {
      this.deleted = true;
   }

   /**
    * This is used to mark that the artifact not deleted. This should only be called by the RemoteEventManager.
    * 
    * @throws SQLException
    */
   public void setNotDeleted() {
      this.deleted = false;
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
         dirtyVal |= RelationManager.hasDirtyLinks(this);
      }
      return dirtyVal;
   }

   public boolean isReadOnly() {
      return deleted || isHistorical() || !AccessControlManager.checkObjectPermission(this, PermissionEnum.WRITE);
   }

   private boolean anAttributeIsDirty() throws SQLException {
      for (Attribute<?> attribute : internalGetAttributes()) {
         if (attribute.isDirty()) {
            return true;
         }
      }
      return false;
   }

   /**
    * Reverts this artifact's attributes and relations back to the last state saved. This will have no effect if the
    * artifact has never been saved.
    * 
    * @throws SQLException
    * @throws MultipleArtifactsExist
    * @throws ArtifactDoesNotExist
    * @throws SQLException
    * @throws IllegalStateException if the artifact is deleted
    */
   public void reloadAttributesAndRelations() throws ArtifactDoesNotExist, MultipleArtifactsExist, SQLException {
      if (!isInDb()) return;

      ArtifactQuery.reloadArtifactFromId(getArtId(), getBranch());
   }

   void prepareForReload() throws OseeCoreException {
      attributes.clear();
      dirty = false;
      linksLoaded = false;

      RelationManager.prepareRelationsForReload(this);
   }

   public final void persistAttributes() throws SQLException {
      if (!AccessControlManager.checkObjectPermission(getBranch(), PermissionEnum.WRITE)) throw new IllegalArgumentException(
            "No write permissions for the branch that this artifact belongs to:" + getBranch());
      if (isHistorical()) {
         throw new IllegalArgumentException(
               "The artifact " + getGuid() + " must be at the head of the branch to be edited.");
      }

      AbstractSkynetTxTemplate artifactPersistTx = new AbstractSkynetTxTemplate(getBranch()) {
         @Override
         protected void handleTxWork() throws OseeCoreException, SQLException {
            if (isDirty()) {
               getTxBuilder().addArtifactToPersist(Artifact.this);
               onAttributePersist();
            }
         }
      };
      try {
         artifactPersistTx.execute();
      } catch (Exception ex) {
         throw new SQLException(ex);
      }
   }

   public final void persistRelations() throws SQLException {
      RelationManager.persistRelationsFor(this, null);
   }

   public final void persistRelations(Collection<RelationType> relationTypes) throws SQLException {
      for (RelationType relationType : relationTypes) {
         RelationManager.persistRelationsFor(this, relationType);
      }
   }

   public final void persistAttributesAndRelations() throws SQLException {
      persistAttributes();
      persistRelations();
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
    * Return relations that exist between artifacts
    * 
    * @throws SQLException
    * @throws ArtifactDoesNotExist
    */
   @Deprecated
   public ArrayList<RelationLink> getRelations(Artifact artifact) throws SQLException {
      ArrayList<RelationLink> relations = new ArrayList<RelationLink>();
      for (RelationLink relation : getRelationsAll(false)) {
         try {
            if (relation.getArtifactOnOtherSide(this).equals(artifact)) {
               relations.add(relation);
            }
         } catch (ArtifactDoesNotExist ex) {
            SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
      }
      return relations;
   }

   /**
    * Return relations that exist between artifacts of type side
    * 
    * @throws SQLException
    */
   @Deprecated
   public ArrayList<RelationLink> getRelations(IRelationEnumeration side, Artifact artifact) throws SQLException {
      ArrayList<RelationLink> relations = new ArrayList<RelationLink>();
      for (RelationLink relation : getRelations(side)) {
         try {
            if (relation.getArtifactOnOtherSide(this).equals(artifact)) {
               relations.add(relation);
            }
         } catch (ArtifactDoesNotExist ex) {
            SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
      }
      return relations;
   }

   /**
    * Removes artifact from a specific branch
    * 
    * @throws SQLException
    */
   public void delete() throws OseeCoreException, SQLException {
      ArtifactPersistenceManager.deleteArtifact(this);
   }

   /**
    * Removes artifact from a specific branch
    * 
    * @param overrideDeleteCheck if <b>true</b> deletes without checking preconditions
    * @throws SQLException
    */
   public void delete(boolean overrideDeleteCheck) throws OseeCoreException, SQLException {
      ArtifactPersistenceManager.deleteArtifact(overrideDeleteCheck, this);
   }

   /**
    * Remove artifact from a specific branch in the database
    * 
    * @throws SQLException
    */
   public void purgeFromBranch() throws OseeCoreException, SQLException {
      ArtifactPersistenceManager.getInstance().purgeArtifactFromBranch(this);
   }

   public boolean isDeleted() {
      return deleted;
   }

   public void setDirty() {
      dirty = true;
   }

   public void setLinksLoaded() {
      linksLoaded = true;
   }

   public void addRelation(IRelationEnumeration relationSide, Artifact artifact, String rationale) throws SQLException {
      boolean sideA = relationSide.isSideA();
      Artifact artifactA = sideA ? artifact : this;
      Artifact artifactB = sideA ? this : artifact;
      RelationManager.addRelation(relationSide.getRelationType(), artifactA, artifactB, rationale);
   }

   public void addRelation(IRelationEnumeration relationSide, Artifact artifact) throws SQLException {
      addRelation(relationSide, artifact, null);
   }

   public void addRelation(Artifact targetArtifact, boolean insertAfterTarget, IRelationEnumeration relationSide, Artifact artifact, String rationale) throws OseeCoreException, SQLException {
      boolean sideA = relationSide.isSideA();
      Artifact artifactA = sideA ? artifact : this;
      Artifact artifactB = sideA ? this : artifact;
      Artifact targetArtifactA = sideA ? targetArtifact : null;
      Artifact targetArtifactB = sideA ? null : targetArtifact;
      boolean insertAfterATarget = sideA ? insertAfterTarget : true;
      boolean insertAfterBTarget = sideA ? true : insertAfterTarget;

      RelationManager.addRelation(targetArtifactA, insertAfterATarget, targetArtifactB, insertAfterBTarget,
            relationSide.getRelationType(), artifactA, artifactB, rationale);
   }

   public void setRelationOrder(IRelationEnumeration relationSide, List<Artifact> artifactsInNewOrder) throws SQLException, OseeCoreException {
      if (artifactsInNewOrder.size() == 0) return;
      List<Artifact> currentOrder = getArtifacts(relationSide, Artifact.class);
      // Insert first artifact before first artifact in list
      Artifact previousArtifact = currentOrder.iterator().next();
      boolean firstArtifact = true;
      for (Artifact artifact : artifactsInNewOrder) {
         if (previousArtifact != artifact) {
            setRelationOrder(previousArtifact, !firstArtifact, relationSide, artifact);
         }
         firstArtifact = false;
         previousArtifact = artifact;
      }
   }

   public void setRelationOrder(Artifact targetArtifact, boolean insertAfterTarget, IRelationEnumeration relationSide, Artifact artifact) throws SQLException, OseeCoreException {
      boolean sideA = relationSide.isSideA();
      Artifact artifactA = sideA ? artifact : this;
      Artifact artifactB = sideA ? this : artifact;
      Artifact targetArtifactA = sideA ? targetArtifact : null;
      Artifact targetArtifactB = sideA ? null : targetArtifact;
      boolean insertAfterATarget = sideA ? insertAfterTarget : true;
      boolean insertAfterBTarget = sideA ? true : insertAfterTarget;
      RelationManager.setRelationOrder(targetArtifactA, insertAfterATarget, targetArtifactB, insertAfterBTarget,
            relationSide.getRelationType(), artifactA, artifactB);
   }

   /**
    * Use addRelation instead
    * 
    * @param relationSide
    * @param artifact
    * @param persist
    * @see #addRelation
    * @throws SQLException
    */
   @Deprecated
   public void relate(IRelationEnumeration relationSide, Artifact artifact, boolean persist) throws SQLException {
      addRelation(relationSide, artifact);
      persistRelations();
   }

   public void deleteRelation(IRelationEnumeration relationSide, Artifact artifact) throws SQLException {
      boolean sideA = relationSide.isSideA();
      Artifact artifactA = sideA ? artifact : this;
      Artifact artifactB = sideA ? this : artifact;
      try {
         RelationManager.deleteRelation(relationSide.getRelationType(), artifactA, artifactB);
      } catch (ArtifactDoesNotExist ex) {
         throw new SQLException(ex);
      }
   }

   /**
    * Overwrites all existing relations to this artifact with a single relation to an artifact
    * 
    * @param relationSide
    * @param artifact
    * @throws SQLException
    * @throws ArtifactDoesNotExist
    * @throws ArtifactDoesNotExist
    */
   public void setSoleRelation(IRelationEnumeration relationSide, Artifact artifact) throws SQLException, ArtifactDoesNotExist {
      // Delete all existing relations
      for (RelationLink relationLink : getRelations(relationSide)) {
         relationLink.delete(true);
      }
      setRelations(relationSide, Arrays.asList(artifact));
   }

   /**
    * Creates new relations that don't already exist and removes relations to artifacts that are not in collection
    * 
    * @param relationSide
    * @param artifacts
    * @throws SQLException
    * @throws ArtifactDoesNotExist
    */
   public void setRelations(IRelationEnumeration relationSide, Collection<? extends Artifact> artifacts) throws SQLException, ArtifactDoesNotExist {
      Collection<Artifact> currentlyRelated = getArtifacts(relationSide, Artifact.class);
      // Add new relations if don't exist
      for (Artifact artifact : artifacts) {
         if (!currentlyRelated.contains(artifact)) {
            addRelation(relationSide, artifact);
         }
      }
      // Remove relations that have been removed
      for (Artifact artifact : currentlyRelated) {
         if (!artifacts.contains(artifact)) {
            deleteRelation(relationSide, artifact);
         }
      }
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
   public ArtifactType getArtifactType() {
      return artifactType;
   }

   public String getVersionedName() {
      String name = getDescriptiveName();

      if (isHistorical()) {
         name += " [Rev:" + transactionId.getTransactionNumber() + "]";
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

            for (Attribute<?> attribute : internalGetAttributes()) {
               if (attribute.isDirty()) {
                  return new Result(true, "===> Dirty Attribute - " + attribute.getAttributeType().getName() + "\n");
               }
            }
            return new Result(true, "Artifact isDirty == true??");
         }
         // Loop through all relations
         for (IRelationEnumeration side : links) {
            for (Artifact art : getRelatedArtifacts(side)) {
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
    * Creates a new artifact and duplicates all of its attribute data.
    * 
    * @throws OseeCoreException
    * @throws Exception
    */
   public Artifact duplicate(Branch branch) throws SQLException, OseeCoreException {
      Artifact newArtifact = artifactType.makeNewArtifact(branch);
      //we do this because attributes were added on creation to meet the minimium attribute requirements      
      newArtifact.attributes.clear();
      copyAttributes(newArtifact);
      return newArtifact;
   }

   private void copyAttributes(Artifact artifact) throws SQLException, OseeCoreException {
      for (Attribute<?> attribute : getAttributes(false)) {
         artifact.addAttribute(attribute.getAttributeType().getName(), attribute.getValue());
      }
   }

   /**
    * @param gammaId
    */
   public void setGammaId(int gammaId) {
      this.gammaId = gammaId;
   }

   /**
    * @return the transaction number for this artifact if it is historical, otherwise 0
    */
   public int getTransactionNumber() {
      return transactionId.getTransactionNumber();
   }

   /**
    * @return Returns the gammaId.
    */
   public int getGammaId() {
      return gammaId;
   }

   /**
    * @return Returns dirty attributes.
    * @throws Exception
    */
   public Collection<SkynetAttributeChange> getDirtySkynetAttributeChanges() throws OseeCoreException {
      List<SkynetAttributeChange> dirtyAttributes = new LinkedList<SkynetAttributeChange>();

      for (Attribute<?> attribute : internalGetAttributes()) {
         if (attribute.isDirty()) {
            dirtyAttributes.add(new SkynetAttributeChange(attribute.getAttributeType().getName(),
                  attribute.getAttributeDataProvider().getData(), attribute.isDeleted(), attribute.getAttrId(),
                  attribute.getGammaId()));
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
   public void changeArtifactType(ArtifactType artifactType) throws SQLException {
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
      return (37 * guid.hashCode()) + branch.hashCode();
   }

   /* (non-Javadoc)
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj) {
      if (obj instanceof Artifact) {
         Artifact otherArtifact = (Artifact) obj;
         return guid == otherArtifact.guid && branch.equals(otherArtifact.branch);
      }
      return false;
   }

   public int getRemainingAttributeCount(AttributeType attributeType) throws SQLException {
      return attributeType.getMaxOccurrences() - getAttributeCount(attributeType.getName());
   }

   public int getAttributeCount(String attributeTypeName) throws SQLException {
      ensureAttributesLoaded();
      return getAttributes(attributeTypeName).size();
   }

   void setArtId(int artifactId) {
      this.artId = artifactId;
   }

   /**
    * @param relationType
    * @return a list of relations from a specific relation type
    */
   public List<RelationLink> getRelations(RelationType relationType) {
      return RelationManager.getRelations(this, relationType, null);
   }

   public List<RelationLink> getRelations(IRelationEnumeration relationEnum) throws SQLException {
      return RelationManager.getRelations(this, relationEnum.getRelationType(), relationEnum.getSide());
   }

   public List<RelationLink> getRelationsAll(boolean includeDeleted) {
      return RelationManager.getRelationsAll(this, includeDeleted);
   }

   /**
    * Returns all artifacts related to this artifact.
    * 
    * @return
    * @throws SQLException
    * @throws ArtifactDoesNotExist
    */
   public List<Artifact> getRelatedArtifactsAll() throws ArtifactDoesNotExist, SQLException {
      return RelationManager.getRelatedArtifactsAll(this);
   }

   /**
    * This method should never be called from outside the OSEE Application Framework
    * 
    * @param gammaId
    * @param transactionId
    * @param modType
    * @param lastModified
    * @param historical
    * @throws TransactionDoesNotExist
    * @throws BranchDoesNotExist
    * @throws SQLException
    */
   public void internalSetPersistenceData(int gammaId, TransactionId transactionId, ModificationType modType, boolean historical) {
      this.deleted = modType == ModificationType.DELETED;
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.historical = historical;
   }

   public Date getLastModified() throws OseeCoreException, SQLException {
      return transactionId.getTime();
   }

   public User getLastModifiedBy() throws OseeCoreException, SQLException {
      return SkynetAuthentication.getUserByArtId(transactionId.getAuthorArtId());
   }
}