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
import java.io.IOException;
import java.io.InputStream;
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
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.skynet.core.IOseeType;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.IAccessControllable;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.AttributeAnnotationManager;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.IArtifactAnnotation;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.CoreAttributes;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.skynet.core.relation.order.IRelationSorterId;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.types.IArtifact;
import org.osgi.framework.Bundle;

public class Artifact implements IArtifact, IAdaptable, Comparable<Artifact>, IAccessControllable {
   public static final String UNNAMED = "Unnamed";
   public static final String BEFORE_GUID_STRING = "/BeforeGUID/PrePend";
   public static final String AFTER_GUID_STRING = "/AfterGUID";
   private final HashCollection<String, Attribute<?>> attributes =
         new HashCollection<String, Attribute<?>>(false, LinkedList.class, 12);
   private final Branch branch;
   private final String guid;
   private String humanReadableId;
   private ArtifactType artifactType;
   private final ArtifactFactory parentFactory;
   private AttributeAnnotationManager annotationMgr;
   private TransactionId transactionId;
   private int artId;
   private int gammaId;
   private boolean linksLoaded;
   private boolean historical;
   private ModificationType modType;
   private ModificationType lastValidModType;

   public Artifact(ArtifactFactory parentFactory, String guid, String humanReadableId, Branch branch, ArtifactType artifactType) throws OseeDataStoreException {
      modType = ModificationType.NEW;
      if (guid == null) {
         this.guid = GUID.create();
      } else {
         this.guid = guid;
      }

      if (humanReadableId == null) {
         populateHumanReadableID();
      } else {
         this.humanReadableId = humanReadableId;
      }

      this.parentFactory = parentFactory;
      this.branch = branch;
      this.artifactType = artifactType;
   }

   public boolean isInDb() {
      return transactionId != null;
   }

   /**
    * A historical artifact always corresponds to a fixed revision of an artifact
    * 
    * @return whether this artifact represents a fixed revision
    */
   public boolean isHistorical() {
      return historical;
   }

   public boolean isAnnotation(ArtifactAnnotation.Type type) throws OseeCoreException {
      for (ArtifactAnnotation notify : getAnnotations()) {
         if (notify.getType() == type) {
            return true;
         }
      }
      return false;
   }

   public Set<ArtifactAnnotation> getAnnotations() throws OseeCoreException {
      Set<ArtifactAnnotation> annotations = new HashSet<ArtifactAnnotation>();
      for (IArtifactAnnotation annotation : getAnnotationExtensions()) {
         annotation.getAnnotations(this, annotations);
      }
      return annotations;
   }

   public ArtifactAnnotation.Type getMainAnnotationType() throws OseeCoreException {
      if (isAnnotation(ArtifactAnnotation.Type.Error)) {
         return ArtifactAnnotation.Type.Error;
      } else if (isAnnotation(ArtifactAnnotation.Type.Warning)) {
         return ArtifactAnnotation.Type.Warning;
      } else if (isAnnotation(ArtifactAnnotation.Type.Info)) {
         return ArtifactAnnotation.Type.Info;
      }
      return ArtifactAnnotation.Type.None;
   }

   /**
    * All the artifacts related to this artifact by relations of type relationTypeName are returned in a list order
    * based on the stored relation order
    * 
    * @param relationTypeName
    * @return the artifacts related to this artifact by relations of type relationTypeName
    * @throws ArtifactDoesNotExist
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public List<Artifact> getRelatedArtifacts(String relationTypeName) throws OseeCoreException {
      return RelationManager.getRelatedArtifacts(this, RelationTypeManager.getType(relationTypeName));
   }

   public List<? extends IArtifact> getRelatedArtifacts(RelationType relationType) throws OseeCoreException {
      return RelationManager.getRelatedArtifacts(this, relationType);
   }

   public List<Artifact> getRelatedArtifacts(RelationTypeSideSorter relationSorter) throws OseeCoreException {
      return RelationManager.getRelatedArtifacts(this, new RelationTypeSide(relationSorter.getRelationType(),
            relationSorter.getSide()));
   }

   public List<Artifact> getRelatedArtifacts(IRelationEnumeration relationEnum) throws OseeCoreException {
      return RelationManager.getRelatedArtifacts(this, relationEnum);
   }

   public List<Artifact> getRelatedArtifacts(IRelationEnumeration relationEnum, boolean includeDeleted) throws OseeCoreException {
      return RelationManager.getRelatedArtifacts(this, relationEnum, includeDeleted);
   }

   public String getRelationRationale(Artifact artifact, IRelationEnumeration relationTypeSide) throws OseeCoreException {
      Pair<Artifact, Artifact> sides = determineArtifactSides(artifact, relationTypeSide);
      return RelationManager.getRelationRationale(sides.getFirst(), sides.getSecond(),
            relationTypeSide.getRelationType());
   }

   public void setRelationRationale(Artifact artifact, IRelationEnumeration relationTypeSide, String rationale) throws OseeCoreException {
      Pair<Artifact, Artifact> sides = determineArtifactSides(artifact, relationTypeSide);
      RelationManager.setRelationRationale(sides.getFirst(), sides.getSecond(), relationTypeSide.getRelationType(),
            rationale);
   }

   private Pair<Artifact, Artifact> determineArtifactSides(Artifact artifact, IRelationEnumeration relationSide) {
      boolean sideA = relationSide.isSideA();
      Artifact artifactA = sideA ? artifact : this;
      Artifact artifactB = sideA ? this : artifact;
      return new Pair<Artifact, Artifact>(artifactA, artifactB);
   }

   /**
    * Check if artifacts are related to each other by relation type
    * 
    * @param relationEnum
    * @param other artifact to check
    * @return whether they are related
    * @throws OseeCoreException
    */
   public boolean isRelated(IRelationEnumeration relationEnum, Artifact other) throws OseeCoreException {
      List<Artifact> relatedArtifacts = getRelatedArtifacts(relationEnum);
      return relatedArtifacts.contains(other);
   }

   /**
    * Get the exactly one artifact related to this artifact by relations of type relationType are returned in a list
    * order based on
    * 
    * @param relationType
    * @return the related artifact
    * @throws ArtifactDoesNotExist
    * @throws OseeDataStoreException
    * @throws MultipleArtifactsExist
    */
   public Artifact getRelatedArtifact(IRelationEnumeration relationEnum) throws OseeCoreException {
      return RelationManager.getRelatedArtifact(this, relationEnum);
   }

   public int getRelatedArtifactsCount(IRelationEnumeration relationEnum) throws OseeCoreException {
      return RelationManager.getRelatedArtifactsCount(this, relationEnum.getRelationType(), relationEnum.getSide());
   }

   public int getRelatedArtifactsCount(RelationTypeSideSorter relationSorter) throws OseeCoreException {
      return RelationManager.getRelatedArtifactsCount(relationSorter.getArtifact(), relationSorter.getRelationType(),
            relationSorter.getSide());
   }

   /**
    * @param <A>
    * @param side
    * @param clazz
    * @throws OseeCoreException
    */
   public <A extends Artifact> List<A> getRelatedArtifacts(IRelationEnumeration side, Class<A> clazz) throws OseeCoreException {
      return Collections.castAll(getRelatedArtifacts(side));
   }

   @SuppressWarnings("unchecked")
   public <A extends Artifact> List<A> getRelatedArtifactsOfType(IRelationEnumeration side, Class<A> clazz) throws OseeCoreException {
      List<A> objs = new ArrayList<A>();
      for (Artifact art : getRelatedArtifacts(side)) {
         if (clazz.isInstance(art)) {
            objs.add((A) art);
         }
      }
      return objs;
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
   public void onAttributePersist(SkynetTransaction transaction) throws OseeCoreException {
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
      return artifactType.getId();
   }

   /**
    * @return Returns the branch.
    */
   public Branch getBranch() {
      return branch;
   }

   public String getGuid() {
      return guid;
   }

   public String getArtifactTypeName() {
      return artifactType.getName();
   }

   /**
    * Currently this method provides support for quasi artifact type inheritance
    * 
    * @param artifactType
    * @return whether this artifact's type or any of its super-types (any ancestor type) are the specified type
    * @throws OseeDataStoreException
    * @throws OseeTypeDoesNotExist
    */
   public boolean isOfType(String artifactTypeName) throws OseeCoreException {
      return artifactType.inheritsFrom(artifactTypeName);
   }

   public boolean isOfType(IOseeType oseeType) throws OseeCoreException {
      return artifactType.inheritsFrom(oseeType);
   }

   @Override
   public String toString() {
      return getName();
   }

   //TODO should not return null but currently application code expects it to
   /**
    * The method should be used when the caller expects this artifact to have exactly one parent. Otherwise use
    * hasParent() to safely determine whether
    */
   public Artifact getParent() throws OseeCoreException {
      if (hasParent()) {
         return RelationManager.getRelatedArtifact(this, CoreRelationEnumeration.DEFAULT_HIERARCHICAL__PARENT);
      }
      return null;
   }

   public Attribute<?> getAttributeById(int attrId, boolean includeDeleted) throws OseeCoreException {
      for (Attribute<?> attribute : getAttributes(includeDeleted)) {
         if (attribute.getAttrId() == attrId) {
            return attribute;
         }
      }
      return null;
   }

   /**
    * @return whether this artifact has exactly one parent artifact related by a relation of type default hierarchical
    * @throws OseeCoreException
    * @throws MultipleArtifactsExist if this artifact has more than one parent
    */
   public boolean hasParent() throws OseeCoreException {
      int parentCount = getRelatedArtifactsCount(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__PARENT);
      if (parentCount > 1) {
         throw new MultipleArtifactsExist(humanReadableId + " has " + parentCount + " parents");
      }

      return parentCount == 1;
   }

   public boolean isOrphan() throws OseeCoreException {
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(getBranch());
      for (Artifact parent = getParent(); parent != null; parent = parent.getParent()) {
         if (parent.equals(root)) {
            return false;
         }
      }
      return true;
   }

   public Artifact getChild(String descriptiveName) throws OseeCoreException {
      for (Artifact artifact : getChildren()) {
         if (artifact.getName().equals(descriptiveName)) {
            return artifact;
         }
      }
      throw new ArtifactDoesNotExist("\"" + getName() + "\" has no child with the name \"" + descriptiveName + "\"");
   }

   public boolean hasChild(String descriptiveName) throws OseeCoreException {
      for (Artifact artifact : getChildren()) {
         if (artifact.getName().equals(descriptiveName)) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return set of the direct children of this artifact
    * @throws OseeCoreException
    */
   public List<Artifact> getChildren() throws OseeCoreException {
      return getRelatedArtifacts(DEFAULT_HIERARCHICAL__CHILD);
   }

   /**
    * @return set of the direct children of this artifact
    * @throws OseeCoreException
    */
   public List<Artifact> getChildren(boolean includeDeleted) throws OseeCoreException {
      return getRelatedArtifacts(DEFAULT_HIERARCHICAL__CHILD, includeDeleted);
   }

   /**
    * @return a list of artifacts ordered by a depth first traversal of this artifact's descendants
    * @throws OseeCoreException
    */
   public List<Artifact> getDescendants() throws OseeCoreException {
      List<Artifact> descendants = new LinkedList<Artifact>();
      getDescendants(descendants);
      return descendants;
   }

   private void getDescendants(Collection<Artifact> descendants) throws OseeCoreException {
      for (Artifact child : getChildren()) {
         descendants.add(child);
         child.getDescendants(descendants);
      }
   }

   /**
    * @param artifact
    * @throws OseeCoreException
    */
   public void addChild(Artifact artifact) throws OseeCoreException {
      addRelation(DEFAULT_HIERARCHICAL__CHILD, artifact);
   }

   /**
    * creates a new child using artifactType with the given name and relates it to its parent
    * 
    * @param artifactType
    * @param name
    * @throws OseeCoreException
    */
   public Artifact addNewChild(ArtifactType artifactType, String name) throws OseeCoreException {
      Artifact child = artifactType.makeNewArtifact(branch);
      child.setName(name);
      addChild(child);
      return child;
   }

   /**
    * creates a new child using artifactType with the given name and relates it to its parent
    * 
    * @param artifactTypeName
    * @param name
    * @return the newly created artifact
    * @throws OseeCoreException
    */
   public Artifact addNewChild(String artifactTypeName, String name) throws OseeCoreException {
      return addNewChild(ArtifactTypeManager.getType(artifactTypeName), name);
   }

   public void addChildren(List<? extends Artifact> artifacts) throws OseeCoreException {
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
    * @return new Attribute
    * @throws OseeCoreException
    */
   @SuppressWarnings("unchecked")
   private <T> Attribute<T> createAttribute(AttributeType attributeType) throws OseeCoreException {
      Class<? extends Attribute<T>> attributeClass =
            (Class<? extends Attribute<T>>) attributeType.getBaseAttributeClass();

      try {
         Attribute<T> attribute = attributeClass.newInstance();
         attributes.put(attributeType.getName(), attribute);
         return attribute;
      } catch (InstantiationException ex) {
         throw new OseeWrappedException(ex);
      } catch (IllegalAccessException ex) {
         throw new OseeWrappedException(ex);
      }
   }

   private <T> Attribute<T> initializeAttribute(AttributeType attributeType, ModificationType modificationType, boolean markDirty, boolean setDefaultValue) throws OseeCoreException {
      Attribute<T> attribute = createAttribute(attributeType);
      attribute.internalInitialize(attributeType, this, modificationType, markDirty, setDefaultValue);
      return attribute;
   }

   public <T> Attribute<T> internalInitializeAttribute(AttributeType attributeType, int attributeId, int gammaId, ModificationType modificationType, boolean markDirty, Object... data) throws OseeCoreException {
      Attribute<T> attribute = createAttribute(attributeType);
      attribute.internalInitialize(attributeType, this, modificationType, attributeId, gammaId, markDirty, false);
      attribute.getAttributeDataProvider().loadData(data);
      return attribute;
   }

   public void onAttributeModify() throws OseeStateException {
      if (modType == ModificationType.DELETED) {
         throw new OseeStateException(
               "Attempted to change an attribute on the artifact " + this + " after the artifact had been deleted.");
      }
      if (isInDb()) {
         modType = ModificationType.MODIFIED;
      }
   }

   /**
    * @param attributeName
    * @return true if attributeName is valid for the artifact type of this artifact
    * @throws OseeTypeDoesNotExist
    * @throws OseeDataStoreException
    * @throws BranchDoesNotExist
    */
   public boolean isAttributeTypeValid(String attributeName) throws OseeCoreException {
      AttributeType attributeType = AttributeTypeManager.getType(attributeName);
      return getArtifactType().isValidAttributeType(attributeType, branch);
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    * 
    * @param <T>
    * @param attributeTypeName
    * @param value
    * @throws OseeCoreException
    */
   public <T> List<Attribute<T>> getAttributes(String attributeTypeName, Object value) throws OseeCoreException {
      List<Attribute<?>> filteredList = new ArrayList<Attribute<?>>();
      for (Attribute<?> attribute : getAttributes(attributeTypeName)) {
         if (attribute.getValue().equals(value)) {
            filteredList.add(attribute);
         }
      }
      return Collections.castAll(filteredList);
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    * 
    * @return attributes All attributes including deleted and artifact deleted
    * @throws OseeCoreException
    */
   public List<Attribute<?>> getAllAttributesIncludingHardDeleted() throws OseeCoreException {
      return getAttributesByModificationType(ModificationType.getAllStates());
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    * 
    * @return attributes All attributes of the specified type name including deleted and artifact deleted
    * @throws OseeCoreException
    */
   public List<Attribute<?>> getAllAttributesIncludingHardDeleted(String artifactTypeName) throws OseeCoreException {
      return getAttributesByModificationType(artifactTypeName, ModificationType.getAllStates());
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    * 
    * @return attributes
    * @throws OseeCoreException
    */
   public List<Attribute<?>> getAttributes() throws OseeCoreException {
      return getAttributes(false);
   }

   public List<Attribute<?>> getAttributes(boolean includeDeleted) throws OseeCoreException {
      List<Attribute<?>> attributes;
      if (includeDeleted) {
         attributes = getAttributesByModificationType(ModificationType.getAllCurrentModTypes());
      } else {
         attributes = getAttributesByModificationType(ModificationType.getCurrentModTypes());
      }
      return attributes;
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    * 
    * @param <T>
    * @param attributeTypeName
    * @throws OseeCoreException
    */
   public <T> List<Attribute<T>> getAttributes(String attributeTypeName) throws OseeCoreException {
      return Collections.castAll(getAttributesByModificationType(attributeTypeName,
            ModificationType.getCurrentModTypes()));
   }

   private List<Attribute<?>> getAttributesByModificationType(Set<ModificationType> allowedModTypes) throws OseeCoreException {
      ensureAttributesLoaded();
      return filterByModificationType(attributes.getValues(), allowedModTypes);
   }

   private List<Attribute<?>> getAttributesByModificationType(String attributeTypeName, Set<ModificationType> allowedModTypes) throws OseeCoreException {
      ensureAttributesLoaded();
      return filterByModificationType(attributes.getValues(attributeTypeName), allowedModTypes);
   }

   private List<Attribute<?>> filterByModificationType(Collection<Attribute<?>> attributes, Set<ModificationType> allowedModTypes) throws OseeCoreException {
      List<Attribute<?>> filteredList = new ArrayList<Attribute<?>>();
      if (allowedModTypes != null && !allowedModTypes.isEmpty() && attributes != null && !attributes.isEmpty()) {
         for (Attribute<?> attribute : attributes) {
            if (allowedModTypes.contains(attribute.getModificationType())) {
               filteredList.add(attribute);
            }
         }
      }
      return filteredList;
   }

   /**
    * @return all attributes including deleted ones
    */
   public List<Attribute<?>> internalGetAttributes() {
      return attributes.getValues();
   }

   /**
    * Deletes all attributes of the given type, if any
    * 
    * @param attributeTypeName
    * @throws OseeCoreException
    */
   public void deleteAttributes(String attributeTypeName) throws OseeCoreException {
      for (Attribute<?> attribute : getAttributes(attributeTypeName)) {
         attribute.delete();
      }
   }

   private void ensureAttributesLoaded() throws OseeCoreException {
      if (!isAttributesLoaded() && isInDb()) {
         ArtifactLoader.loadArtifactData(this, ArtifactLoad.ATTRIBUTE);
      }
   }

   public boolean isAttributesLoaded() {
      return !attributes.isEmpty();
   }

   public Collection<AttributeType> getAttributeTypes() throws OseeCoreException {
      return getArtifactType().getAttributeTypes(branch);
   }

   public <T> Attribute<T> getSoleAttribute(String attributeTypeName) throws OseeCoreException {
      ensureAttributesLoaded();
      List<Attribute<T>> soleAttributes = getAttributes(attributeTypeName);
      if (soleAttributes.size() == 0) {
         return null;
      } else if (soleAttributes.size() > 1) {
         throw new MultipleAttributesExist(String.format(
               "The attribute \'%s\' can have no more than one instance for sole attribute operations; guid \'%s\'",
               attributeTypeName, getGuid()));
      }
      return soleAttributes.iterator().next();
   }

   private <T> Attribute<T> getOrCreateSoleAttribute(String attributeTypeName) throws OseeCoreException {
      Attribute<T> attribute = getSoleAttribute(attributeTypeName);
      if (attribute == null) {
         attribute =
               initializeAttribute(AttributeTypeManager.getType(attributeTypeName), ModificationType.NEW, true, true);
      }
      return attribute;
   }

   /**
    * @param <T>
    * @param attributeTypeName
    * @return the existing attribute value or the default value from a newly initialized attribute if none previously
    *         existed
    * @throws OseeCoreException
    */
   public <T> T getOrInitializeSoleAttributeValue(String attributeTypeName) throws OseeCoreException {
      Attribute<T> attribute = getOrCreateSoleAttribute(attributeTypeName);
      return attribute.getValue();
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
    * @throws OseeCoreException
    */
   public <T> T getSoleAttributeValue(String attributeTypeName) throws OseeCoreException {
      List<Attribute<T>> soleAttributes = getAttributes(attributeTypeName);
      if (soleAttributes.size() == 0) {
         if (!isAttributeTypeValid(attributeTypeName)) {
            throw new OseeArgumentException(String.format(
                  "The attribute type %s is not valid for artifacts of type [%s]", attributeTypeName,
                  getArtifactTypeName()));
         }
         throw new AttributeDoesNotExist(
               "Attribute \"" + attributeTypeName + "\" does not exist for artifact " + getHumanReadableId());
      } else if (soleAttributes.size() > 1) {
         throw new MultipleAttributesExist(
               "Attribute \"" + attributeTypeName + "\" must have exactly one instance.  It currently has " + soleAttributes.size() + " for artifact " + getHumanReadableId());
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
    * @return attribute value
    * @throws MultipleAttributesExist if multiple attribute instances exist
    */
   public String getSoleAttributeValueAsString(String attributeTypeName, String defaultReturnValue) throws OseeCoreException, MultipleAttributesExist {
      String toReturn = null;
      Object value = getSoleAttributeValue(attributeTypeName, defaultReturnValue);
      if (value instanceof InputStream) {
         InputStream inputStream = (InputStream) value;
         try {
            toReturn = Lib.inputStreamToString(inputStream);
         } catch (IOException ex) {
            throw new OseeWrappedException(ex);
         } finally {
            try {
               inputStream.close();
            } catch (IOException ex) {
               throw new OseeWrappedException(ex);
            }
         }
      } else {
         if (value != null) {
            toReturn = value.toString();
         }
      }
      return toReturn;
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
    * @return attribute value
    * @throws MultipleAttributesExist if multiple attribute instances exist
    * @throws OseeCoreException
    */
   public <T> T getSoleAttributeValue(String attributeTypeName, T defaultReturnValue) throws OseeCoreException {
      List<Attribute<T>> soleAttributes = getAttributes(attributeTypeName);
      if (soleAttributes.size() == 1) {
         T value = soleAttributes.iterator().next().getValue();
         if (value == null) {
            OseeLog.log(
                  Activator.class,
                  Level.SEVERE,
                  "Attribute \"" + attributeTypeName + "\" has null value for Artifact " + getHumanReadableId() + " \"" + getName() + "\"");
            return defaultReturnValue;
         }
         return value;
      } else {
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
    * @return attribute value
    * @throws MultipleAttributesExist if multiple attribute instances exist
    * @throws OseeCoreException
    */
   public <T> T getSoleAttributeValue(String attributeTypeName, T defaultReturnValue, Class<T> clazz) throws OseeCoreException {
      return getSoleAttributeValue(attributeTypeName, defaultReturnValue);
   }

   /**
    * Delete attribute if exactly one exists. Does nothing if attribute does not exist and throw MultipleAttributesExist
    * is more than one instance of the attribute type exsits for this artifact
    * 
    * @param attributeTypeName
    * @throws OseeCoreException
    */
   public void deleteSoleAttribute(String attributeTypeName) throws OseeCoreException {
      Attribute<?> attribute = getSoleAttribute(attributeTypeName);
      if (attribute != null) {
         attribute.delete();
      }
   }

   public void deleteAttribute(String attributeTypeName, Object value) throws OseeCoreException {
      for (Attribute<Object> attribute : getAttributes(attributeTypeName)) {
         if (attribute.getValue().equals(value)) {
            attribute.delete();
         }
      }
   }

   /**
    * Used on attribute types with no more than one instance. If the attribute exists, it's value is changed, otherwise
    * a new attribute is added and its value set.
    * 
    * @param <T>
    * @param attributeTypeName
    * @param value
    * @throws OseeCoreException
    * @throws MultipleAttributesExist
    */
   public <T> void setSoleAttributeValue(String attributeTypeName, T value) throws OseeCoreException {
      getOrCreateSoleAttribute(attributeTypeName).setValue(value);
   }

   public <T> void setSoleAttributeFromString(String attributeTypeName, String value) throws OseeCoreException {
      getOrCreateSoleAttribute(attributeTypeName).setFromString(value);
   }

   public void setSoleAttributeFromStream(String attributeTypeName, InputStream stream) throws OseeCoreException {
      getOrCreateSoleAttribute(attributeTypeName).setValueFromInputStream(stream);
   }

   /**
    * @param attributeTypeName
    * @return comma delimited representation of all the attributes of the type attributeName
    * @throws OseeCoreException
    */
   public String getAttributesToString(String attributeTypeName) throws OseeCoreException {
      StringBuffer sb = new StringBuffer();
      for (Attribute<?> attr : getAttributes(attributeTypeName)) {
         sb.append(attr);
         sb.append(", ");
      }
      return sb.toString().replaceFirst(", $", "");
   }

   /**
    * All existing attributes matching a new value will be left untouched. Then for any remaining values, other existing
    * attributes will be changed to match or if need be new attributes will be added to stored these values. Finally any
    * excess attributes will be deleted.
    * 
    * @param attributeType
    * @param newValues
    * @throws OseeCoreException
    */
   public void setAttributeValues(AttributeType attributeType, Collection<String> newValues) throws OseeCoreException {
      setAttributeValues(attributeType.getName(), newValues);
   }

   /**
    * All existing attributes matching a new value will be left untouched. Then for any remaining values, other existing
    * attributes will be changed to match or if need be new attributes will be added to stored these values. Finally any
    * excess attributes will be deleted.
    * 
    * @param attributeTypeName
    * @param newValues
    * @throws OseeCoreException
    */
   public void setAttributeValues(String attributeTypeName, Collection<String> newValues) throws OseeCoreException {
      ensureAttributesLoaded();
      HashSet<String> uniqueNewValues = new HashSet<String>(newValues); // ensure new values are unique

      List<Attribute<Object>> remainingAttributes = getAttributes(attributeTypeName);
      List<String> remainingNewValues = new ArrayList<String>(uniqueNewValues.size());

      // all existing attributes matching a new value will be left untouched
      for (String newValue : uniqueNewValues) {
         boolean found = false;
         for (Attribute<Object> attribute : remainingAttributes) {
            if (attribute.getValue().toString().equals(newValue)) {
               remainingAttributes.remove(attribute);
               found = true;
               break;
            }
         }
         if (!found) {
            remainingNewValues.add(newValue);
         }
      }

      for (String newValue : remainingNewValues) {
         if (remainingAttributes.isEmpty()) {
            setOrAddAttribute(attributeTypeName, newValue);
         } else {
            int index = remainingAttributes.size() - 1;
            remainingAttributes.get(index).setValue(newValue);
            remainingAttributes.remove(index);
         }
      }

      for (Attribute<Object> attribute : remainingAttributes) {
         attribute.delete();
      }
   }

   /**
    * adds a new attribute of the type named attributeTypeName and assigns it the given value
    * 
    * @param <T>
    * @param attributeTypeName
    * @param value
    * @throws OseeCoreException
    */
   public <T> void addAttribute(String attributeTypeName, T value) throws OseeCoreException {
      addAttribute(AttributeTypeManager.getType(attributeTypeName), value);
   }

   /**
    * adds a new attribute of the type named attributeTypeName and assigns it the given value
    * 
    * @param <T>
    * @param attributeType
    * @param value
    * @throws OseeCoreException
    */
   public <T> void addAttribute(AttributeType attributeType, T value) throws OseeCoreException {
      initializeAttribute(attributeType, ModificationType.NEW, true, false).setValue(value);
   }

   /**
    * adds a new attribute of the type named attributeTypeName. The attribute is set to the default value for its type,
    * if any.
    * 
    * @param attributeType
    * @throws OseeCoreException
    */
   public void addAttribute(AttributeType attributeType) throws OseeCoreException {
      initializeAttribute(attributeType, ModificationType.NEW, true, true);
   }

   /**
    * adds a new attribute of the type named attributeTypeName and assigns it the given value
    * 
    * @param attributeTypeName
    * @param value
    * @throws OseeCoreException
    */
   public void addAttributeFromString(String attributeTypeName, String value) throws OseeCoreException {
      initializeAttribute(AttributeTypeManager.getType(attributeTypeName), ModificationType.NEW, true, false).setFromString(
            value);
   }

   /**
    * we do not what duplicated enumerated values so this method silently returns if the specified attribute type is
    * enumerated and value is already present
    * 
    * @param <T>
    * @param attributeTypeName
    * @param value
    * @throws OseeCoreException
    */
   public <T> void setOrAddAttribute(String attributeTypeName, T value) throws OseeCoreException {
      List<Attribute<String>> attributes = getAttributes(attributeTypeName);
      for (Attribute<String> canidateAttribute : attributes) {
         if (canidateAttribute.getValue().equals(value)) {
            return;
         }
      }
      addAttribute(attributeTypeName, value);
   }

   /**
    * @param attributeTypeName
    * @return string collection containing of all the attribute values of the type attributeName
    * @throws OseeCoreException
    */
   public List<String> getAttributesToStringList(String attributeTypeName) throws OseeCoreException {
      ensureAttributesLoaded();

      List<String> items = new ArrayList<String>();
      for (Attribute<?> attribute : getAttributes(attributeTypeName)) {
         items.add(attribute.toString());
      }
      return items;
   }

   /**
    * @param attributeType
    * @return string collection containing of all the attribute values of the type attributeName
    * @throws OseeCoreException
    */
   public List<String> getAttributesToStringList(AttributeType attributeType) throws OseeCoreException {
      return getAttributesToStringList(attributeType.getName());
   }

   /**
    * Return the String value of the first found attributeTypeName attribute whether deleted or not.
    * 
    * @param attributeTypeName
    * @return attribute value
    * @throws OseeCoreException
    */
   private String getInternalAttributeValue(String attributeTypeName) throws OseeCoreException {
      ensureAttributesLoaded();
      if (!isAttributeTypeValid(attributeTypeName)) {
         throw new OseeStateException(String.format(
               "ArtifactType [%s] definition error - Attribute Type [%s] is not a part of Artifact Type [%s:%s]",
               getArtifactTypeName(), attributeTypeName, getArtifactTypeName(), getGuid()));
      }
      for (Attribute<?> attribute : internalGetAttributes()) {
         if (attribute.isOfType(attributeTypeName)) {
            return (String) attribute.getValue();
         }
      }
      return "";
   }

   public String getName() {
      String name = null;
      try {
         name = getInternalAttributeValue("Name");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      if (!Strings.isValid(name)) {
         return UNNAMED;
      }
      return name;
   }

   public void setName(String name) throws OseeCoreException {
      setSoleAttributeValue("Name", name);
   }

   public ArtifactFactory getFactory() {
      return parentFactory;
   }

   /**
    * This is used to mark that the artifact deleted.
    * 
    * @throws OseeCoreException
    */
   public void internalSetDeleted() throws OseeCoreException {
      this.modType = ModificationType.DELETED;

      for (Attribute<?> attribute : getAttributes()) {
         attribute.setArtifactDeleted();
      }
   }

   /**
    * This is used to mark that the artifact not deleted. This should only be called by the RemoteEventManager.
    */
   public void resetToPreviousModType() {
      this.modType = lastValidModType;

      for (Attribute<?> attribute : attributes.getValues()) {
         if (attribute.getModificationType() == ModificationType.ARTIFACT_DELETED) {
            attribute.resetModType();
         }
      }
   }

   /**
    * @return whether this artifact has unsaved attribute changes
    */
   public boolean hasDirtyAttributes() {
      for (Attribute<?> attribute : internalGetAttributes()) {
         if (attribute.isDirty()) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return whether this artifact has unsaved relation changes
    */
   public boolean hasDirtyRelations() {
      return RelationManager.hasDirtyLinks(this);
   }

   /**
    * @return whether this artifact has unsaved relation changes
    */
   public boolean isDirty() {
      return hasDirtyAttributes() || hasDirtyRelations();
   }

   public boolean isReadOnly() {
      try {
         return isDeleted() || isHistorical() || !getBranch().isEditable() || !AccessControlManager.hasPermission(this,
               PermissionEnum.WRITE);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         return true;
      }
   }

   public void revert() throws OseeCoreException {
      DbTransaction dbTransaction = new DbTransaction() {
         @Override
         protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
            ArtifactPersistenceManager.revertArtifact(connection, Artifact.this);
         }
      };
      dbTransaction.execute();
   }

   /**
    * Reloads this artifact's attributes and relations back to the last state saved. This will have no effect if the
    * artifact has never been saved.
    * 
    * @throws MultipleArtifactsExist
    * @throws ArtifactDoesNotExist
    * @throws IllegalStateException if the artifact is deleted
    */
   public void reloadAttributesAndRelations() throws OseeCoreException {
      if (!isInDb()) {
         return;
      }

      ArtifactQuery.reloadArtifactFromId(getArtId(), getBranch());
   }

   void prepareForReload() throws OseeCoreException {
      attributes.clear();
      linksLoaded = false;

      RelationManager.prepareRelationsForReload(this);
   }

   private final void persistAttributes(SkynetTransaction transaction) throws OseeCoreException {
      if (!UserManager.duringMainUserCreation() && !AccessControlManager.hasPermission(getBranch(),
            PermissionEnum.WRITE)) {
         throw new OseeArgumentException(
               "No write permissions for the branch that this artifact belongs to:" + getBranch());
      }
      if (isHistorical()) {
         throw new OseeArgumentException(
               "The artifact " + getGuid() + " must be at the head of the branch to be edited.");
      }

      if (hasDirtyAttributes()) {
         transaction.addArtifact(this);
         onAttributePersist(transaction);
      }
   }

   private final void persistRelations(SkynetTransaction transaction) throws OseeCoreException {
      RelationManager.persistRelationsFor(transaction, this, null);
   }

   public final void persist() throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(branch);
      persist(transaction);
      transaction.execute();
   }

   public final void persist(SkynetTransaction transaction) throws OseeCoreException {
      if (transaction == null) {
         persist();
      } else {
         persistAttributes(transaction);
         persistRelations(transaction);
      }
   }

   /**
    * Returns all of the descendants through the primary decomposition tree that have a particular human readable id.
    * This will not return the called upon node if the name matches since it can not be a descendant of itself.
    * 
    * @param humanReadableId The human readable id text to match against.
    * @param caseSensitive Whether to use case sensitive matching.
    * @return <code>Collection</code> of <code>Artifact</code>'s that match.
    * @throws OseeCoreException
    */
   public Collection<Artifact> getDescendants(String humanReadableId, boolean caseSensitive) throws OseeCoreException {
      Collection<Artifact> descendants = new LinkedList<Artifact>();

      for (Artifact child : getChildren()) {
         if (caseSensitive && child.getName().equals(humanReadableId) || !caseSensitive && child.getName().equalsIgnoreCase(
               humanReadableId)) {
            descendants.add(child);
         }
         descendants.addAll(child.getDescendants(humanReadableId, caseSensitive));
      }

      return descendants;
   }

   /**
    * Starting from this artifact, walks down the child hierarchy based on the list of child names provided and returns
    * the child of the last name provided. ArtifactDoesNotExist exception is thrown ff any child along the path does not
    * exist.
    * 
    * @param names
    * @return child at the leaf (bottom) of the specified hierarchy.
    * @throws OseeCoreException
    */
   public Artifact getDescendant(String... names) throws OseeCoreException {
      if (names.length == 0) {
         throw new OseeArgumentException("Must suply at least one name to getDescendant()");
      }
      Artifact descendant = this;
      for (String name : names) {
         descendant = descendant.getChild(name);
      }
      return descendant;
   }

   /**
    * Removes artifact from a specific branch
    */
   public void deleteAndPersist() throws OseeCoreException {
      SkynetTransaction transaction = new SkynetTransaction(branch);
      deleteAndPersist(transaction);
      transaction.execute();
   }

   /**
    * Removes artifact from a specific branch
    */
   public void deleteAndPersist(SkynetTransaction transaction) throws OseeCoreException {
      ArtifactPersistenceManager.deleteArtifact(transaction, false, this);
   }

   public void delete() throws OseeCoreException {
      ArtifactPersistenceManager.deleteArtifact(null, false, this);
   }

   /**
    * Remove artifact from a specific branch in the database
    * 
    * @throws OseeCoreException
    */
   public void purgeFromBranch() throws OseeCoreException {
      new PurgeDbTransaction(Arrays.asList(this)).execute();
   }

   public boolean isDeleted() {
      return modType == ModificationType.DELETED;
   }

   public void setLinksLoaded(boolean loaded) {
      linksLoaded = loaded;
   }

   public void addRelation(IRelationSorterId sorterId, IRelationEnumeration relationSide, Artifact artifact, String rationale) throws OseeCoreException {
      Pair<Artifact, Artifact> sides = determineArtifactSides(artifact, relationSide);
      RelationManager.addRelation(sorterId, relationSide.getRelationType(), sides.getFirst(), sides.getSecond(),
            rationale);
   }

   public void addRelation(IRelationEnumeration relationSide, Artifact artifact) throws OseeCoreException {
      addRelation(null, relationSide, artifact, null);
   }

   public void addRelation(IRelationSorterId sorterId, IRelationEnumeration relationSide, Artifact artifact) throws OseeCoreException {
      addRelation(sorterId, relationSide, artifact, null);
   }

   public void addRelation(IRelationSorterId sorterId, IRelationEnumeration relationEnumeration, Artifact targetArtifact, boolean insertAfterTarget, Artifact itemToAdd, String rationale) throws OseeCoreException {
      boolean sideA = relationEnumeration.isSideA();
      Artifact artifactA = sideA ? itemToAdd : this;
      Artifact artifactB = sideA ? this : itemToAdd;

      RelationManager.addRelation(sorterId, relationEnumeration.getRelationType(), artifactA, artifactB, rationale);
      setRelationOrder(relationEnumeration, targetArtifact, insertAfterTarget, itemToAdd);
   }

   public void setRelationOrder(IRelationEnumeration relationSide, List<Artifact> artifactsInNewOrder) throws OseeCoreException {
      RelationManager.setRelationOrder(this, relationSide.getRelationType(), relationSide.getSide(),
            RelationOrderBaseTypes.USER_DEFINED, artifactsInNewOrder);
   }

   public void setRelationOrder(IRelationEnumeration relationEnumeration, IRelationSorterId orderId) throws OseeCoreException {
      if (RelationOrderBaseTypes.USER_DEFINED == orderId) {
         throw new OseeArgumentException(
               "Wrong method called - use setRelationOrder(IRelationEnumeration relationSide, List<Artifact> artifactsInNewOrder) instead");
      }
      List<Artifact> empty = java.util.Collections.emptyList();
      RelationManager.setRelationOrder(this, relationEnumeration.getRelationType(), relationEnumeration.getSide(),
            orderId, empty);
   }

   public void setRelationOrder(IRelationEnumeration relationEnumeration, Artifact targetArtifact, boolean insertAfterTarget, Artifact itemToAdd) throws OseeCoreException {
      List<Artifact> currentOrder = getRelatedArtifacts(relationEnumeration, Artifact.class);
      boolean found = false;
      int index = 0;
      for (int i = 0; i < currentOrder.size(); i++) {
         if (currentOrder.get(i).equals(targetArtifact)) {
            index = i;
            found = true;
            break;
         }
      }
      currentOrder.remove(itemToAdd);
      if (found) {
         if (insertAfterTarget) {
            index++;
         }
         if (index > currentOrder.size()) {
            currentOrder.add(itemToAdd);
         } else {
            currentOrder.add(index, itemToAdd);
         }
      } else {
         currentOrder.add(itemToAdd);
      }
      RelationManager.setRelationOrder(this, relationEnumeration.getRelationType(), relationEnumeration.getSide(),
            RelationOrderBaseTypes.USER_DEFINED, currentOrder);
   }

   public void deleteRelation(IRelationEnumeration relationSide, Artifact artifact) throws OseeCoreException {
      Pair<Artifact, Artifact> sides = determineArtifactSides(artifact, relationSide);
      RelationManager.deleteRelation(relationSide.getRelationType(), sides.getFirst(), sides.getSecond());
   }

   public void deleteRelations(IRelationEnumeration relationSide) throws OseeCoreException {
      for (Artifact art : getRelatedArtifacts(relationSide)) {
         deleteRelation(relationSide, art);
      }
   }

   /**
    * Creates new relations that don't already exist and removes relations to artifacts that are not in collection
    * 
    * @param relationSide
    * @param artifacts
    * @throws OseeCoreException
    */
   public void setRelations(IRelationEnumeration relationSide, Collection<? extends Artifact> artifacts) throws OseeCoreException {
      Collection<Artifact> currentlyRelated = getRelatedArtifacts(relationSide, Artifact.class);
      // Remove relations that have been removed
      for (Artifact artifact : currentlyRelated) {
         if (!artifacts.contains(artifact)) {
            deleteRelation(relationSide, artifact);
         }
      }
      // Add new relations if don't exist
      for (Artifact artifact : artifacts) {
         if (!currentlyRelated.contains(artifact)) {
            addRelation(relationSide, artifact);
         }
      }
   }

   /**
    * Creates new relations that don't already exist and removes relations to artifacts that are not in collection
    * 
    * @param relationSide
    * @param artifacts
    * @throws OseeCoreException
    */
   public void setRelationsOfTypeUseCurrentOrder(IRelationEnumeration relationSide, Collection<? extends Artifact> artifacts, Class<?> clazz) throws OseeCoreException {
      RelationTypeSideSorter sorter =
            RelationManager.createTypeSideSorter(this, relationSide.getRelationType(), relationSide.getSide());
      Collection<Artifact> currentlyRelated = getRelatedArtifacts(relationSide, Artifact.class);
      // Add new relations if don't exist
      for (Artifact artifact : artifacts) {
         if (clazz.isInstance(artifact) && !currentlyRelated.contains(artifact)) {
            addRelation(sorter.getSorterId(), relationSide, artifact);
         }
      }
      // Remove relations that have been removed
      for (Artifact artifact : currentlyRelated) {
         if (clazz.isInstance(artifact) && !artifacts.contains(artifact)) {
            deleteRelation(relationSide, artifact);
         }
      }
   }

   public final boolean isLinksLoaded() {
      return linksLoaded;
   }

   public String getHumanReadableId() {
      return humanReadableId;
   }

   private void populateHumanReadableID() throws OseeDataStoreException {
      String hrid = HumanReadableId.generate();
      humanReadableId = isUniqueHRID(hrid) ? hrid : HumanReadableId.generate();
   }

   public static boolean isUniqueHRID(String id) throws OseeDataStoreException {
      String DUPLICATE_HRID_SEARCH = "SELECT COUNT(1) FROM osee_artifact t1 WHERE t1.human_readable_id = ?";

      return ConnectionHandler.runPreparedQueryFetchInt(-1, DUPLICATE_HRID_SEARCH, id) == 0;
   }

   /**
    * @return Returns the descriptor.
    */
   public ArtifactType getArtifactType() {
      return artifactType;
   }

   public String getVersionedName() {
      String name = getName();

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
   public String isRelationsAndArtifactsDirty(Set<IRelationEnumeration> links) {
      try {
         if (hasDirtyAttributes()) {

            for (Attribute<?> attribute : internalGetAttributes()) {
               if (attribute.isDirty()) {
                  return "===> Dirty Attribute - " + attribute.getAttributeType().getName() + "\n";
               }
            }
            return "Artifact isDirty == true??";
         }
         // Loop through all relations
         for (IRelationEnumeration side : links) {
            for (Artifact art : getRelatedArtifacts(side)) {
               // Check artifact dirty
               if (art.hasDirtyAttributes()) {
                  return art.getArtifactTypeName() + " \"" + art + "\" => dirty\n";
               }
               // Check the links to this artifact
               for (RelationLink link : getRelations(side, art)) {
                  if (link.isDirty()) {
                     return "Link \"" + link + "\" => dirty\n";
                  }
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return null;
   }

   /**
    * Creates a new artifact and duplicates all of its attribute data.
    * 
    * @throws OseeCoreException
    */
   public Artifact duplicate(Branch branch) throws OseeCoreException {
      Artifact newArtifact = artifactType.makeNewArtifact(branch);
      //we do this because attributes were added on creation to meet the minimum attribute requirements      
      newArtifact.attributes.clear();
      copyAttributes(newArtifact);
      return newArtifact;
   }

   private void copyAttributes(Artifact artifact) throws OseeCoreException {
      for (Attribute<?> attribute : getAttributes()) {
         if (isCopyAllowed(attribute)) {
            artifact.addAttribute(attribute.getAttributeType(), attribute.getValue());
         }
      }
   }

   private boolean isCopyAllowed(Attribute<?> attribute) {
      return attribute != null && !attribute.isOfType(CoreAttributes.RELATION_ORDER.getName());
   }

   /**
    * An artifact reflected about its own branch returns itself. Otherwise a new artifact is introduced on the
    * destinationBranch
    * 
    * @param destinationBranch
    * @return the newly created artifact or this artifact if the destinationBranch is this artifact's branch
    * @throws OseeCoreException
    */
   public Artifact reflect(Branch destinationBranch) throws OseeCoreException {
      if (branch.equals(destinationBranch)) {
         return this;
      }
      Artifact reflectedArtifact = reflectHelper(destinationBranch);
      reflectedArtifact.transactionId = TransactionIdManager.getlatestTransactionForBranch(destinationBranch);
      return reflectedArtifact;
   }

   private Artifact reflectHelper(Branch branch) throws OseeCoreException {
      Artifact reflectedArtifact =
            artifactType.getFactory().reflectExisitingArtifact(artId, guid, humanReadableId, artifactType, gammaId,
                  branch, ModificationType.INTRODUCED);

      for (Attribute<?> sourceAttribute : attributes.getValues()) {
         //In order to reflect attributes they must exist in the data store and be valid for the destination branch as well
         if (sourceAttribute.isInDb() && reflectedArtifact.isAttributeTypeValid(sourceAttribute.getAttributeType().getName())) {
            reflectedArtifact.internalInitializeAttribute(sourceAttribute.getAttributeType(),
                  sourceAttribute.getAttrId(), sourceAttribute.getGammaId(), ModificationType.INTRODUCED, true,
                  sourceAttribute.getAttributeDataProvider().getData());
         }
      }
      return reflectedArtifact;
   }

   /**
    * @return the transaction number for this artifact if it is historical, otherwise 0
    */
   public int getTransactionNumber() {
      return transactionId != null ? transactionId.getTransactionNumber() : -1;
   }

   public TransactionId getTransactionId() {
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
    * @throws Exception
    */
   public Collection<SkynetAttributeChange> getDirtySkynetAttributeChanges() throws OseeDataStoreException {
      List<SkynetAttributeChange> dirtyAttributes = new LinkedList<SkynetAttributeChange>();

      for (Attribute<?> attribute : internalGetAttributes()) {
         if (attribute.isDirty()) {
            dirtyAttributes.add(new SkynetAttributeChange(attribute.getAttributeType().getId(),
                  attribute.getAttributeDataProvider().getData(), attribute.getModificationType(),
                  attribute.getAttrId(), attribute.getGammaId()));
         }
      }
      return dirtyAttributes;
   }

   /**
    * Changes the artifact type in the database.
    * 
    * @param artifactType
    * @throws OseeDataStoreException
    */
   public void changeArtifactType(ArtifactType artifactType) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate("UPDATE osee_artifact SET art_type_id = ? WHERE art_id = ?",
            artifactType.getId(), artId);
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
      String elementName = safeNamePattern.matcher(getName()).replaceAll("_");

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

   private Set<IArtifactAnnotation> artifactAnnotationExtensions;

   private Set<IArtifactAnnotation> getAnnotationExtensions() {
      if (artifactAnnotationExtensions != null) {
         return artifactAnnotationExtensions;
      }
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

   @SuppressWarnings("unchecked")
   public Object getAdapter(Class adapter) {
      if (adapter == null) {
         throw new IllegalArgumentException("adapter can not be null");
      }

      if (adapter.isInstance(this)) {
         return this;
      }
      return null;
   }

   public final int compareTo(Artifact otherArtifact) {
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
            diff = getName().compareTo(otherArtifact.getName());
         } catch (Exception ex) {
            diff = 0;
         }
      }

      return diff;
   }

   @Override
   public final int hashCode() {
      int hashCode = 11;
      hashCode = hashCode * 37 + getArtId();
      hashCode = hashCode * 37 + getBranch().hashCode();
      return hashCode;
   }

   /**
    * @param obj the reference object with which to compare.
    * @return <code>true</code> if this artifact has the same GUID and branch <code>false</code> otherwise.
    */
   @Override
   public final boolean equals(Object obj) {
      if (obj instanceof IArtifact) {
         IArtifact other = (IArtifact) obj;
         boolean result = getArtId() == other.getArtId();
         if (result) {
            if (getBranch() != null && other.getBranch() != null) {
               result = getBranch().equals(other.getBranch());
            } else {
               result = getBranch() == null && other.getBranch() == null;
            }
         }
         return result;
      }
      return false;
   }

   public int getRemainingAttributeCount(AttributeType attributeType) throws OseeCoreException {
      return attributeType.getMaxOccurrences() - getAttributeCount(attributeType.getName());
   }

   public int getAttributeCount(String attributeTypeName) throws OseeCoreException {
      ensureAttributesLoaded();
      return getAttributes(attributeTypeName).size();
   }

   void setArtId(int artifactId) {
      this.artId = artifactId;
   }

   /**
    * Return relations that exist between artifacts
    * 
    * @throws ArtifactDoesNotExist
    */
   @Deprecated
   public ArrayList<RelationLink> getRelations(Artifact artifact) throws OseeCoreException {
      ArrayList<RelationLink> relations = new ArrayList<RelationLink>();
      for (RelationLink relation : getRelationsAll(false)) {
         try {
            if (relation.getArtifactOnOtherSide(this).equals(artifact)) {
               relations.add(relation);
            }
         } catch (ArtifactDoesNotExist ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return relations;
   }

   @Deprecated
   public List<RelationLink> getRelations(IRelationEnumeration relationEnum) throws OseeCoreException {
      return RelationManager.getRelations(this, relationEnum.getRelationType(), relationEnum.getSide());
   }

   /**
    * Return relations that exist between artifacts of type side
    * 
    * @throws OseeCoreException
    */
   @Deprecated
   public ArrayList<RelationLink> getRelations(IRelationEnumeration side, Artifact artifact) throws OseeCoreException {
      ArrayList<RelationLink> relations = new ArrayList<RelationLink>();
      for (RelationLink relation : getRelations(side)) {
         try {
            if (relation.getArtifactOnOtherSide(this).equals(artifact)) {
               relations.add(relation);
            }
         } catch (ArtifactDoesNotExist ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return relations;
   }

   /**
    * @param relationType
    * @return a list of relations from a specific relation type
    */
   @Deprecated
   public List<RelationLink> getRelations(RelationType relationType) {
      return RelationManager.getRelations(this, relationType, null);
   }

   public List<RelationLink> getRelationsAll(boolean includeDeleted) {
      return RelationManager.getRelationsAll(getArtId(), getBranch().getBranchId(), includeDeleted);
   }

   /**
    * @return all artifacts related to this artifact.
    * @throws ArtifactDoesNotExist
    */
   public List<Artifact> getRelatedArtifactsAll() throws OseeCoreException {
      return RelationManager.getRelatedArtifactsAll(this);
   }

   /**
    * This method should never be called from outside the OSEE Application Framework
    */
   void internalSetPersistenceData(int gammaId, TransactionId transactionId, ModificationType modType, boolean historical) {
      this.gammaId = gammaId;
      this.transactionId = transactionId;
      this.historical = historical;
      this.modType = modType;
      this.lastValidModType = modType;
   }

   public Date getLastModified() {
      if (transactionId == null) {
         return new Date();
      }
      return transactionId.getDate();
   }

   public User getLastModifiedBy() throws OseeCoreException {
      if (transactionId == null) {
         return UserManager.getUser(SystemUser.OseeSystem);
      }
      return UserManager.getUserByArtId(transactionId.getAuthorArtId());
   }

   void meetMinimumAttributeCounts(boolean isNewArtifact) throws OseeCoreException {
      if (modType == ModificationType.DELETED) {
         return;
      }
      for (AttributeType attributeType : getAttributeTypes()) {
         int missingCount = attributeType.getMinOccurrences() - getAttributeCount(attributeType.getName());
         for (int i = 0; i < missingCount; i++) {
            initializeAttribute(attributeType, ModificationType.NEW, isNewArtifact, true);
         }
      }
   }

   public ModificationType getModType() {
      return modType;
   }

   @Override
   public Branch getAccessControlBranch() {
      return branch;
   }

   @Override
   public Artifact getFullArtifact() throws OseeCoreException {
      return this;
   }
}
