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

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Artifact;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import static org.eclipse.osee.framework.core.enums.RelationSorter.PREEXISTING;
import static org.eclipse.osee.framework.core.enums.RelationSorter.USER_DEFINED;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.osee.framework.core.data.Adaptable;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.EditState;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelationReorder;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.model.type.RelationType;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.FullyNamed;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactReferenceAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.model.AttributeChange;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * {@link ArtifactTest}
 *
 * @author Ryan D. Brooks
 */

public class Artifact extends NamedIdBase implements ArtifactToken, Adaptable, FullyNamed {
   public static final Artifact SENTINEL = new Artifact(Id.SENTINEL, CoreBranches.COMMON, CoreArtifactTypes.Artifact);
   public static final String UNNAMED = "Unnamed";
   public static final String BEFORE_GUID_STRING = "/BeforeGUID/PrePend";
   public static final String AFTER_GUID_STRING = "/AfterGUID";
   private final HashCollection<AttributeTypeId, Attribute<?>> attributes = new HashCollection<>(true);
   private final Set<DefaultBasicUuidRelationReorder> relationOrderRecords = new HashSet<>();
   private final BranchId branch;
   private TransactionToken transaction = TransactionToken.SENTINEL;
   private GammaId gammaId;
   private boolean linksLoaded;
   private boolean historical;
   private ModificationType modType;
   private ModificationType lastValidModType;
   private EditState objectEditState;
   private boolean useBackingData;
   private ArtifactTypeToken artifactType;
   private ApplicabilityId applicabilityId;

   private final String guid;

   public Artifact(String guid, BranchId branch, ArtifactTypeToken artifactTypeId) {
      this(Lib.generateArtifactIdAsInt(), guid, branch, artifactTypeId);
   }

   public Artifact(Long id, BranchId branch, ArtifactTypeToken artifactTypeId) {
      this(id, null, branch, artifactTypeId);
   }

   public Artifact(Long id, String guid, BranchId branch, ArtifactTypeToken artifactType) {
      super(id, null);
      this.guid = GUID.checkOrCreate(guid);
      this.artifactType = artifactType;
      objectEditState = EditState.NO_CHANGE;
      internalSetModType(ModificationType.NEW, false);
      internalSetApplicablityId(ApplicabilityId.BASE);
      this.branch = branch;
   }

   public Artifact(BranchId branch, ArtifactTypeToken artifactType, String name) {
      this((String) null, branch, artifactType);
      setName(name);
   }

   public Artifact(BranchId branch) {
      this(branch, Artifact);
   }

   public Artifact(BranchId branch, String name) {
      this(branch, Artifact, name);
   }

   public Artifact(BranchId branch, ArtifactTypeToken artifactType) {
      this((String) null, branch, artifactType);
   }

   public Artifact(Long id, BranchId branch) {
      this(id, null, branch, Artifact);
   }

   @Override
   public String getGuid() {
      return guid;
   }

   public final boolean isInDb() {
      return transaction.isValid();
   }

   /**
    * A historical artifact always corresponds to a fixed revision of an artifact
    *
    * @return whether this artifact represents a fixed revision
    */
   public final boolean isHistorical() {
      return historical;
   }

   /**
    * All the artifacts related to this artifact by relations of type relationType are returned in a list order based on
    * the stored relation order use getRelatedArtifacts(Artifact artifact, IRelationEnumeration relationEnum) instead
    * (or similar variant)
    */
   @Deprecated
   public final List<Artifact> getRelatedArtifacts(RelationType relationType) {
      return RelationManager.getRelatedArtifacts(this, new RelationTypeSide(relationType, RelationSide.SIDE_B));
   }

   public @NonNull List<Artifact> getRelatedArtifacts(RelationTypeSide relationTypeSide) {
      return RelationManager.getRelatedArtifacts(this, relationTypeSide);
   }

   public final List<Artifact> getRelatedArtifactsUnSorted(RelationTypeSide relationEnum) {
      return RelationManager.getRelatedArtifactsUnSorted(this, relationEnum);
   }

   public final List<Artifact> getRelatedArtifacts(RelationTypeSide relationEnum, DeletionFlag deletionFlag) {
      return RelationManager.getRelatedArtifacts(this, relationEnum, deletionFlag);
   }

   public final String getRelationRationale(Artifact artifact, RelationTypeSide relationTypeSide) {
      if (artifact.isHistorical()) {
         throw new OseeCoreException("Artifact [%s] is historical.  Historical relations are only supported on server",
            artifact);
      }
      Pair<Artifact, Artifact> sides = determineArtifactSides(artifact, relationTypeSide);
      RelationLink link = RelationManager.getRelationLink(sides.getFirst(), sides.getSecond(), relationTypeSide);
      return link.getRationale();
   }

   public final void setRelationRationale(Artifact artifact, RelationTypeSide relationTypeSide, String rationale) {
      Pair<Artifact, Artifact> sides = determineArtifactSides(artifact, relationTypeSide);
      RelationLink link = RelationManager.getRelationLink(sides.getFirst(), sides.getSecond(), relationTypeSide);
      link.setRationale(rationale);
   }

   private Pair<Artifact, Artifact> determineArtifactSides(Artifact artifact, RelationTypeSide relationSide) {
      boolean sideA = relationSide.getSide().isSideA();
      Artifact artifactA = sideA ? artifact : this;
      Artifact artifactB = sideA ? this : artifact;
      return new Pair<>(artifactA, artifactB);
   }

   /**
    * Check if artifacts are related to each other by relation type
    */
   public final boolean isRelated(RelationTypeSide relationEnum, Artifact other) {
      List<Artifact> relatedArtifacts = getRelatedArtifacts(relationEnum);
      return relatedArtifacts.contains(other);
   }

   /**
    * Get the exactly one artifact related to this artifact by a relation of type relationType
    */
   public final Artifact getRelatedArtifact(RelationTypeSide relationEnum) {
      return RelationManager.getRelatedArtifact(this, relationEnum);
   }

   public final int getRelatedArtifactsCount(RelationTypeSide relationEnum) {
      return RelationManager.getRelatedArtifactsCount(this, relationEnum, relationEnum.getSide());
   }

   public final <A extends Artifact> List<A> getRelatedArtifactsUnSorted(RelationTypeSide side, Class<A> clazz) {
      return Collections.castAll(getRelatedArtifactsUnSorted(side));
   }

   public final <A extends Artifact> List<A> getRelatedArtifacts(RelationTypeSide side, Class<A> clazz) {
      return Collections.castAll(getRelatedArtifacts(side));
   }

   @SuppressWarnings("unchecked")
   public final <A extends Artifact> List<A> getRelatedArtifactsOfType(RelationTypeSide side, Class<A> clazz) {
      List<A> objs = new ArrayList<>();
      for (Artifact art : getRelatedArtifacts(side)) {
         if (clazz.isInstance(art)) {
            objs.add((A) art);
         }
      }
      return objs;
   }

   public final int getArtId() {
      return getId().intValue();
   }

   @Override
   public final BranchId getBranch() {
      return branch;
   }

   public final IOseeBranch getBranchToken() {
      return BranchManager.getBranchToken(branch);
   }

   public final String getArtifactTypeName() {
      return getArtifactType().getName();
   }

   /**
    * Determines if this artifact's type equals, or is a sub-type of, at least one of the given artifact types. This is
    * a relatively expensive operation, only use this method when you need either the multiple artifact types or to have
    * sub-types included; otherwise us the less expensive isTypeEqual()
    */
   @Override
   public final boolean isOfType(ArtifactTypeId... artifactTypes) {
      for (ArtifactTypeId otherType : artifactTypes) {
         if (getArtifactType().inheritsFrom(otherType)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toString() {
      return getName();
   }

   // TODO should not return null but currently application code expects it to
   /**
    * The method should be used when the caller expects this artifact to have exactly one parent. Otherwise use
    * hasParent() to safely determine whether
    */
   public final Artifact getParent() {
      Artifact toReturn = null;
      List<Artifact> artifacts = getRelatedArtifactsUnSorted(CoreRelationTypes.DefaultHierarchical_Parent);
      int parentCount = artifacts.size();
      if (parentCount == 1) {
         toReturn = artifacts.iterator().next();
      } else if (parentCount > 1) {
         throw new MultipleArtifactsExist("artifact %s branch [%s]-[%s] has %s parents", toStringWithId(),
            getBranchToken().getName(), getBranchIdString(), parentCount);
      }
      return toReturn;
   }

   /**
    * @return a list of parents starting with this Artifact and ending with the same Artifact that is returned from
    * getArtifactRoot().
    */
   public final List<Artifact> getAncestors() {
      List<Artifact> ancestors = new ArrayList<>();

      for (Artifact parent = getParent(); parent != null; parent = parent.getParent()) {
         ancestors.add(parent);
      }
      return ancestors;
   }

   public final Attribute<?> getAttributeById(long attributeId, boolean includeDeleted) {
      return getAttributeById(AttributeId.valueOf(attributeId), includeDeleted);
   }

   public final Attribute<?> getAttributeById(AttributeId attributeId, boolean includeDeleted) {
      for (Attribute<?> attribute : getAttributes(includeDeleted)) {
         if (attributeId.equals(attribute)) {
            return attribute;
         }
      }
      return null;
   }

   public final List<Integer> getAttributeIds(AttributeTypeId attributeType) {
      List<Integer> items = new ArrayList<>();
      List<Attribute<Object>> data = getAttributes(attributeType);
      for (Attribute<Object> attribute : data) {
         items.add(attribute.getId().intValue());
      }
      return items;
   }

   /**
    * @return whether this artifact has exactly one parent artifact related by a relation of type default hierarchical
    * @throws MultipleArtifactsExist if this artiAact has more than one parent
    */
   public final boolean hasParent() {
      int parentCount = getRelatedArtifactsUnSorted(CoreRelationTypes.DefaultHierarchical_Parent).size();
      if (parentCount > 1) {
         throw new MultipleArtifactsExist("artifact [%s] has %d parents", getGuid(), parentCount);
      }
      return parentCount == 1;
   }

   public final boolean isNotRootedInDefaultRoot() {
      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(getBranch());
      if (root.equals(getTopContainer())) {
         return false;
      } else {
         return true;
      }
   }

   /**
    * @return the highest level parent of this artifact which will equal to
    * OseeSystemArtifacts.getDefaultHierarchyRootArtifact(artifact.getBranch()) except when this artifact is an orphan
    * or has a cyclic reference. The getDefaultHierarchyRootArtifact Artifact will return itself from this method.
    */
   private Artifact getTopContainer() {
      Artifact root = null;
      if (this.equals(OseeSystemArtifacts.getDefaultHierarchyRootArtifact(getBranch()))) {
         root = this;
      } else {
         Set<Artifact> set = new HashSet<>();
         set.add(this);
         for (Artifact parent = getParent(); parent != null; parent = parent.getParent()) {
            if (set.add(parent)) {
               root = parent;
            } else {
               OseeLog.log(Activator.class, Level.SEVERE, String.format("Cycle detected with artifact: %s", parent));
               root = null;
               break;
            }
         }
      }
      return root;
   }

   public final Artifact getChild(String descriptiveName) {
      for (Artifact artifact : getChildren()) {
         if (artifact.getName().equals(descriptiveName)) {
            return artifact;
         }
      }
      throw new ArtifactDoesNotExist("artifact [%s] has no child with the name [%s]", this, descriptiveName);
   }

   public final boolean hasChild(String descriptiveName) {
      for (Artifact artifact : getChildren()) {
         if (artifact.getName().equals(descriptiveName)) {
            return true;
         }
      }
      return false;
   }

   /**
    * @return set of the direct children of this artifact
    */
   public final @NonNull List<Artifact> getChildren() {
      return getRelatedArtifacts(DefaultHierarchical_Child);
   }

   /**
    * @return set of the direct children of this artifact
    */
   public final List<Artifact> getChildren(DeletionFlag deletionFlag) {
      return getRelatedArtifacts(DefaultHierarchical_Child, deletionFlag);
   }

   public final List<Artifact> getDescendants(DeletionFlag includeDeleted) {
      List<Artifact> descendants = new LinkedList<>();
      getDescendants(descendants, includeDeleted);
      return descendants;
   }

   /**
    * @return a list of artifacts ordered by a depth first traversal of this artifact's descendants
    */
   public final List<Artifact> getDescendants() {
      List<Artifact> descendants = new LinkedList<>();
      getDescendants(descendants, DeletionFlag.EXCLUDE_DELETED);
      return descendants;
   }

   public final List<Artifact> getDescendants(ArtifactTypeToken[] excludedArtifacts) {
      List<Artifact> descendants = new LinkedList<>();
      getDescendants(descendants, excludedArtifacts, DeletionFlag.EXCLUDE_DELETED);
      return descendants;
   }

   private void getDescendants(Collection<Artifact> descendants, DeletionFlag includeDeleted) {
      for (Artifact child : getChildren(includeDeleted)) {
         descendants.add(child);
         child.getDescendants(descendants, includeDeleted);
      }
   }

   private void getDescendants(Collection<Artifact> descendants, ArtifactTypeToken[] excludedArtifacts, DeletionFlag includeDeleted) {
      for (Artifact child : getChildren(includeDeleted)) {
         if (!child.isOfType(excludedArtifacts)) {
            descendants.add(child);
            child.getDescendants(descendants, excludedArtifacts, includeDeleted);
         }
      }
   }

   public final void addChild(Artifact artifact) {
      addChild(PREEXISTING, artifact);
   }

   public final void addChild(RelationSorter sorterId, Artifact artifact) {
      addRelation(sorterId, DefaultHierarchical_Child, artifact);
   }

   public final Artifact addNewChild(RelationSorter sorterId, ArtifactTypeToken artifactType, String name) {
      Artifact child = ArtifactTypeManager.addArtifact(artifactType, branch);
      child.setName(name);
      addChild(sorterId, child);
      return child;
   }

   /**
    * Creates an instance of <code>Attribute</code> of the given attribute type. This method should not be called by
    * applications. Use addAttribute() instead
    */
   @SuppressWarnings("unchecked")
   private <T> Attribute<T> createAttribute(AttributeTypeId attributeType) {
      Class<? extends Attribute<T>> attributeClass =
         (Class<? extends Attribute<T>>) AttributeTypeManager.getAttributeBaseClass(attributeType);
      Attribute<T> attribute = null;
      try {
         attribute = attributeClass.newInstance();
         attributes.put(attributeType, attribute);
      } catch (InstantiationException | IllegalAccessException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
      return attribute;
   }

   private <T> Attribute<T> initializeAttribute(AttributeTypeId attributeType, ModificationType modificationType, boolean markDirty, boolean setDefaultValue) {
      Attribute<T> attribute = createAttribute(attributeType);
      attribute.internalInitialize(attributeType, this, modificationType, ApplicabilityId.BASE, markDirty,
         setDefaultValue);
      return attribute;
   }

   public final <T> Attribute<T> internalInitializeAttribute(AttributeTypeId attributeType, int attributeId, GammaId gammaId, ModificationType modificationType, ApplicabilityId applicabilityId, boolean markDirty, Object... data) {
      return internalInitializeAttribute(attributeType, AttributeId.valueOf(attributeId), gammaId, modificationType,
         applicabilityId, markDirty, data);
   }

   public final <T> Attribute<T> internalInitializeAttribute(AttributeTypeId attributeType, AttributeId attributeId, GammaId gammaId, ModificationType modificationType, ApplicabilityId applicabilityId, boolean markDirty, Object... data) {
      Attribute<T> attribute = createAttribute(attributeType);
      attribute.internalInitialize(attributeType, this, modificationType, applicabilityId, attributeId, gammaId,
         markDirty, false);
      attribute.getAttributeDataProvider().loadData(data);
      return attribute;
   }

   @Override
   public final boolean isAttributeTypeValid(AttributeTypeId attributeType) {
      if (attributeType.equals(CoreAttributeTypes.Name)) {
         return true;
      }
      return ArtifactTypeManager.isValidAttributeType(attributeType, artifactType, branch);
   }

   public final boolean isRelationTypeValid(IRelationType relationType) {
      return getValidRelationTypes().contains(relationType);
   }

   public final Collection<RelationType> getValidRelationTypes() {
      return RelationTypeManager.getValidTypes(getArtifactType(), branch);
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    */
   public final <T> List<Attribute<T>> getAttributes(AttributeTypeId attributeType, Object value) {
      List<Attribute<?>> filteredList = new ArrayList<>();
      for (Attribute<?> attribute : getAttributes(attributeType)) {
         if (attribute.getValue().equals(value)) {
            filteredList.add(attribute);
         }
      }
      return Collections.castAll(filteredList);
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    *
    * @return attributes All attributes of the specified type name including deleted and artifact deleted
    */
   public final List<Attribute<?>> getAllAttributesIncludingHardDeleted(AttributeTypeId attributeType) {
      ensureAttributesLoaded();
      return getAttributes(attributes.getValues(attributeType), true);
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    */
   public final List<Attribute<?>> getAttributes() {
      return getAttributes(false);
   }

   public final List<Attribute<?>> getAttributes(boolean includeDeleted) {
      ensureAttributesLoaded();
      return getAttributes(attributes.getValues(), includeDeleted);
   }

   private List<Attribute<?>> getAttributes(List<Attribute<?>> attributes, boolean includeDeleted) {
      if (attributes == null) {
         return java.util.Collections.emptyList();
      }
      if (includeDeleted) {
         return attributes;
      }
      return attributes.stream().filter(a -> !a.getModificationType().isHardDeleted()).collect(Collectors.toList());
   }

   /**
    * The use of this method is discouraged since it directly returns Attributes.
    */
   @Deprecated
   public final <T> List<Attribute<T>> getAttributes(AttributeTypeId attributeType) {
      ensureAttributesLoaded();
      return Collections.castAll(getAttributes(attributes.getValues(attributeType), false));
   }

   /**
    * @return all attributes including deleted ones
    */
   public final List<Attribute<?>> internalGetAttributes() {
      return attributes.getValues();
   }

   /**
    * Deletes all attributes of the given type, if any
    */
   public final void deleteAttributes(AttributeTypeId attributeType) {
      for (Attribute<?> attribute : getAttributes(attributeType)) {
         attribute.delete();
      }
   }

   private void ensureAttributesLoaded() {
      if (!isAttributesLoaded() && isInDb()) {
         ArtifactLoader.loadArtifactData(this, LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA,
            BranchManager.isArchived(getBranch()));
      }
   }

   public final boolean isAttributesLoaded() {
      return !attributes.isEmpty();
   }

   public final Collection<AttributeTypeToken> getAttributeTypes() {
      return ArtifactTypeManager.getAttributeTypes(artifactType, branch);
   }

   public final <T> Attribute<T> getSoleAttribute(AttributeTypeId attributeType) {
      ensureAttributesLoaded();
      return Collections.oneOrSentinel(getAttributes(attributeType), null);
   }

   private <T> Attribute<T> getOrCreateSoleAttribute(AttributeTypeId attributeType) {
      if (!isAttributeTypeValid(attributeType)) {
         throw new OseeArgumentException("The attribute type %s is not valid for artifacts of type [%s]", attributeType,
            getArtifactTypeName());
      }
      Attribute<T> attribute = getSoleAttribute(attributeType);
      if (attribute == null) {
         attribute = initializeAttribute(attributeType, ModificationType.NEW, true, true);
      }
      return attribute;
   }

   /**
    * Return he existing attribute value or the default value from a newly initialized attribute if none previously
    * existed
    */
   public final <T> T getOrInitializeSoleAttributeValue(AttributeTypeId attributeType) {
      Attribute<T> attribute = getOrCreateSoleAttribute(attributeType);
      return attribute.getValue();
   }

   /**
    * Return sole attribute value for given attribute type name. Will throw exceptions if "Sole" nature of attribute is
    * invalid.<br>
    * <br>
    * Used for quick access to attribute value that should only have 0 or 1 instances of the attribute.
    */
   public final <T> T getSoleAttributeValue(AttributeTypeId attributeType) {
      List<Attribute<T>> soleAttributes = getAttributes(attributeType);
      if (soleAttributes.isEmpty()) {
         if (!isAttributeTypeValid(attributeType)) {
            throw new OseeArgumentException(
               "The attribute type %s is not valid for artifacts of type [%s] on artifact %s on branch [%s]",
               attributeType, getArtifactTypeName(), toStringWithId(), getBranch());
         }
         throw new AttributeDoesNotExist("Attribute of type [%s] could not be found on artifact [%s] on branch [%s]",
            attributeType, toStringWithId(), getBranch());
      } else if (soleAttributes.size() > 1) {
         String errMsg = String.format(
            "Attribute [%s] must have exactly one instance.  It currently has %d for artifact %s; Attributes [%s]",
            attributeType, soleAttributes.size(), toStringWithId(), getAttributeString(soleAttributes));
         throw new MultipleAttributesExist(errMsg);
      }
      return soleAttributes.iterator().next().getValue();
   }

   private <T> String getAttributeString(List<Attribute<T>> attributes) {
      StringBuilder sb = new StringBuilder();
      for (Attribute<T> attr : attributes) {
         sb.append("attribute id=[");
         sb.append(attr.getId());
         sb.append("] gamma=[");
         sb.append(attr.getGammaId());
         sb.append("] ");
      }
      String result = sb.toString();
      System.err.println(result);
      return result;
   }

   /**
    * Return sole attribute string value for given attribute type name. Handles AttributeDoesNotExist case by returning
    * defaultReturnValue.<br>
    * <br>
    * Used for display purposes where toString() of attribute is to be displayed.
    *
    * @param defaultReturnValue return value if attribute instance does not exist
    * @throws MultipleAttributesExist if multiple attribute instances exist
    */

   public final String getSoleAttributeValueAsString(AttributeTypeId attributeType, String defaultReturnValue) throws MultipleAttributesExist {
      String toReturn = defaultReturnValue;
      if (AttributeTypeManager.isBaseTypeCompatible(ArtifactReferenceAttribute.class, attributeType)) {
         List<Attribute<Object>> soleAttributes = getAttributes(attributeType);
         if (soleAttributes.size() == 1) {
            String value = (String) soleAttributes.iterator().next().getAttributeDataProvider().getData()[0];
            if (value == null) {
               return defaultReturnValue;
            }
            return value;
         } else if (soleAttributes.size() > 1) {
            throw new MultipleAttributesExist(
               "Attribute [%s] must have exactly one instance.  It currently has %d for artifact %s on branch [%s]",
               attributeType, soleAttributes.size(), toStringWithId(), getBranch());
         } else {
            return defaultReturnValue;
         }
      } else {
         Object value = getSoleAttributeValue(attributeType, defaultReturnValue);
         if (value instanceof InputStream) {
            InputStream inputStream = (InputStream) value;
            try {
               toReturn = Lib.inputStreamToString(inputStream);
            } catch (IOException ex) {
               OseeCoreException.wrapAndThrow(ex);
            } finally {
               try {
                  inputStream.close();
               } catch (IOException ex) {
                  OseeCoreException.wrapAndThrow(ex);
               }
            }
         } else {
            if (value != null) {
               toReturn = value.toString();
            }
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
    * @throws MultipleAttributesExist if multiple attribute instances exist
    */
   public final <T> T getSoleAttributeValue(AttributeTypeId attributeType, T defaultReturnValue) {
      List<Attribute<T>> soleAttributes = getAttributes(attributeType);
      if (soleAttributes.size() == 1) {
         T value = soleAttributes.iterator().next().getValue();
         if (value == null) {
            /**
             * ArtifactReferenceAttributes can have an attribute value (art id), but getValue would return null if art
             * id can't be resolved. Do not error on null, but instead just return default value.
             */
            if (!(soleAttributes.iterator().next() instanceof ArtifactReferenceAttribute)) {
               OseeLog.log(Activator.class, Level.SEVERE,
                  "Attribute \"" + attributeType + "\" has null value for Artifact " + getGuid() + " \"" + getName() + "\"");
            }
            return defaultReturnValue;
         }
         return value;
      } else if (soleAttributes.size() > 1) {
         throw new MultipleAttributesExist(
            "Attribute [%s] must have exactly one instance.  It currently has %d for artifact %s on branch [%s]",
            attributeType, soleAttributes.size(), toStringWithId(), getBranch());
      } else {
         return defaultReturnValue;
      }
   }

   /**
    * Delete attribute if exactly one exists. Does nothing if attribute does not exist and throw MultipleAttributesExist
    * is more than one instance of the attribute type exists for this artifact
    */
   public final void deleteSoleAttribute(AttributeTypeId attributeType) {
      Attribute<?> attribute = getSoleAttribute(attributeType);
      if (attribute != null) {
         deleteAttribute(attribute);
      }
   }

   /**
    * Deletes the first attribute found of the given type and value
    */
   public final void deleteAttribute(AttributeTypeId attributeType, Object value) {
      for (Attribute<Object> attribute : getAttributes(attributeType)) {
         if (attribute.getValue().equals(value)) {
            deleteAttribute(attribute);
            break;
         }
      }
   }

   public final void deleteAttribute(AttributeId attributeId) {
      for (Attribute<?> attribute : getAttributes()) {
         if (attributeId.getId().equals(attribute.getId())) {
            deleteAttribute(attribute);
            break;
         }
      }
   }

   public final void deleteAttribute(Attribute<?> attribute) {
      if (attribute.isInDb()) {
         attribute.delete();
      } else {
         attributes.removeValue(attribute.getAttributeType(), attribute);
      }
   }

   /**
    * Used on attribute types with no more than one instance. If the attribute exists, it's value is changed, otherwise
    * a new attribute is added and its value set.
    */
   public final <T> void setSoleAttributeValue(AttributeTypeId attributeType, T value) {
      getOrCreateSoleAttribute(attributeType).setValue(value);
   }

   public final <T> void setSoleAttributeFromString(AttributeTypeId attributeType, String value) {
      getOrCreateSoleAttribute(attributeType).setFromString(value);
   }

   public final void setSoleAttributeFromStream(AttributeTypeId attributeType, InputStream stream) {
      getOrCreateSoleAttribute(attributeType).setValueFromInputStream(stream);
   }

   public final String getAttributesToStringSorted(AttributeTypeId attributeType) {
      return getAttributesToString(attributeType, true);
   }

   /**
    * @return comma delimited representation of all the attributes of the type attributeType in an unspecified order
    */
   public final String getAttributesToString(AttributeTypeId attributeType) {
      return getAttributesToString(attributeType, false);
   }

   /**
    * @return comma delimited representation of all the attributes of the type attributeName
    */
   public final String getAttributesToString(AttributeTypeId attributeType, boolean sorted) {
      List<String> strs = new ArrayList<>();
      List<Attribute<Object>> attributes = getAttributes(attributeType);
      if (sorted) {
         java.util.Collections.sort(attributes);
      }

      for (Attribute<?> attr : attributes) {
         strs.add(String.valueOf(attr));
      }
      return Collections.toString(", ", strs);
   }

   /**
    * @return comma separator representation unique values of the attributes of the type attributeName
    */
   public final String getAttributesToStringUnique(AttributeTypeId attributeType, String separator) {
      Set<String> strs = new HashSet<>();
      for (Attribute<?> attr : getAttributes(attributeType)) {
         strs.add(String.valueOf(attr));
      }
      return Collections.toString(separator, strs);
   }

   /**
    * Will add the single string value if it does not already exist. Will also cleanup if more than one exists with same
    * value. Will not touch any other values.
    */
   public void setSingletonAttributeValue(AttributeTypeId attributeType, String value) {
      List<Attribute<String>> attributes = getAttributes(CoreAttributeTypes.StaticId, value);
      if (attributes.isEmpty()) {
         addAttribute(attributeType, value);
      } else if (attributes.size() > 1) {
         // keep one of the attributes
         for (int x = 1; x < attributes.size(); x++) {
            Attribute<String> attr = attributes.get(x);
            attr.delete();
         }
      }
   }

   /**
    * Will remove one or more of the single string value if artifact has it. Will not touch any other values.
    */
   public void deleteSingletonAttributeValue(AttributeTypeId attributeType, String value) {
      for (Attribute<?> attribute : getAttributes(attributeType, value)) {
         attribute.delete();
      }
   }

   /**
    * All existing attributes matching a new value will be left untouched. Then for any remaining values, other existing
    * attributes will be changed to match or if need be new attributes will be added to stored these values. Finally any
    * excess attributes will be deleted.
    */
   public final void setAttributeValues(AttributeTypeId attributeType, Collection<String> newValues) {
      ensureAttributesLoaded();
      // ensure new values are unique
      HashSet<String> uniqueNewValues = new HashSet<>(newValues);

      List<Attribute<Object>> remainingAttributes = getAttributes(attributeType);
      List<String> remainingNewValues = new ArrayList<>(uniqueNewValues.size());

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
            setOrAddAttribute(attributeType, newValue);
         } else {
            int index = remainingAttributes.size() - 1;
            remainingAttributes.get(index).setFromString(newValue);
            remainingAttributes.remove(index);
         }
      }

      for (Attribute<Object> attribute : remainingAttributes) {
         attribute.delete();
      }
   }

   public final <T> void setAttributeFromValues(AttributeTypeId attributeType, Collection<T> values) {
      ensureAttributesLoaded();

      Set<T> uniqueItems = Collections.toSet(values);

      List<Attribute<T>> remainingAttributes = getAttributes(attributeType);
      List<T> remainingNewValues = new ArrayList<>(uniqueItems.size());

      // all existing attributes matching a new value will be left untouched
      for (T newValue : uniqueItems) {
         boolean found = false;
         for (Attribute<T> attribute : remainingAttributes) {
            if (newValue.equals(attribute.getValue())) {
               remainingAttributes.remove(attribute);
               found = true;
               break;
            }
         }
         if (!found) {
            remainingNewValues.add(newValue);
         }
      }

      for (T newValue : remainingNewValues) {
         if (remainingAttributes.isEmpty()) {
            setOrAddAttribute(attributeType, newValue);
         } else {
            int index = remainingAttributes.size() - 1;
            remainingAttributes.get(index).setValue(newValue);
            remainingAttributes.remove(index);
         }
      }

      for (Attribute<T> attribute : remainingAttributes) {
         attribute.delete();
      }
   }

   public final void setBinaryAttributeFromValues(AttributeTypeId attributeType, Collection<InputStream> values) {
      ensureAttributesLoaded();

      List<Attribute<Object>> remainingAttributes = getAttributes(attributeType);

      for (InputStream newValue : values) {
         if (remainingAttributes.isEmpty()) {
            initializeAttribute(attributeType, ModificationType.NEW, true, false).setValueFromInputStream(newValue);
         } else {
            int index = remainingAttributes.size() - 1;
            remainingAttributes.get(index).setValueFromInputStream(newValue);
            remainingAttributes.remove(index);
         }
      }

      for (Attribute<Object> attribute : remainingAttributes) {
         attribute.delete();
      }
   }

   /**
    * adds a new attribute of the type named attributeTypeName and assigns it the given value
    */
   public final <T> void addAttribute(AttributeTypeId attributeType, T value) {
      Conditions.checkNotNull(value, "Attribute value", "attribute type [%s]", attributeType);
      initializeAttribute(attributeType, ModificationType.NEW, true, false).setValue(value);
   }

   /**
    * adds a new attribute of the type named attributeTypeName. The attribute is set to the default value for its type,
    * if any.
    */
   public final void addAttribute(AttributeTypeId attributeType) {
      initializeAttribute(attributeType, ModificationType.NEW, true, true);
   }

   /**
    * adds a new attribute of the type named attributeTypeName. The attribute is set to the default value for its type,
    * if any.
    */
   public final void addAttribute(AttributeType attributeType) {
      initializeAttribute(attributeType, ModificationType.NEW, true, true);
   }

   /**
    * adds a new attribute of the type named attributeTypeName and assigns it the given value
    */
   public final void addAttributeFromString(AttributeTypeId attributeType, String value) {
      Conditions.checkNotNull(value, "Attribute value", "attribute type [%s]", attributeType);
      initializeAttribute(attributeType, ModificationType.NEW, true, false).setFromString(value);
   }

   /**
    * we do not what duplicated enumerated values so this method silently returns if the specified attribute type is
    * enumerated and value is already present
    */
   private final <T> void setOrAddAttribute(AttributeTypeId attributeType, T value) {
      Conditions.checkNotNull(value, "Attribute value", "attribute type [%s]", attributeType);
      List<Attribute<Object>> attributes = getAttributes(attributeType);
      for (Attribute<?> canidateAttribute : attributes) {
         if (canidateAttribute.getValue().equals(value)) {
            return;
         }
      }
      addAttribute(attributeType, value);
   }

   /**
    * @return string collection containing of all the attribute values of type attributeType
    */
   public final List<String> getAttributesToStringList(AttributeTypeId attributeType) {
      ensureAttributesLoaded();

      List<String> items = new ArrayList<>();
      for (Attribute<?> attribute : getAttributes(attributeType)) {
         items.add(attribute.getDisplayableString());
      }
      return items;
   }

   public final <T> List<T> getAttributeValues(AttributeTypeId attributeType) {
      ensureAttributesLoaded();

      List<T> items = new ArrayList<>();
      List<Attribute<T>> data = getAttributes(attributeType);
      for (Attribute<T> attribute : data) {
         T value = attribute.getValue();
         if (value != null) {
            items.add(value);
         }
      }
      return items;
   }

   @Override
   public String getName() {
      String name = null;
      try {
         ensureAttributesLoaded();
         // use the first name attribute whether deleted or not.
         for (Attribute<?> attribute : internalGetAttributes()) {
            if (attribute.isOfType(CoreAttributeTypes.Name)) {
               name = (String) attribute.getValue();
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      if (!Strings.isValid(name)) {
         return UNNAMED;
      }
      return name;
   }

   @Override
   public final void setName(String name) {
      setSoleAttributeValue(CoreAttributeTypes.Name, name);
   }

   /**
    * artifact.persist(); artifact.reloadAttributesAndRelations(); Will need to be called afterwards to see replaced
    * data in memory
    */
   public void replaceWithVersion(GammaId gammaId) {
      replaceWithVersion(gammaId, ModificationType.REPLACED_WITH_VERSION);
   }

   public void replaceWithVersion(GammaId gammaId, ModificationType modType) {
      internalSetGammaId(gammaId);
      internalSetModType(modType, true);
   }

   private final void internalSetGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   public final void internalSetApplicablityId(ApplicabilityId applicabilityId) {
      this.applicabilityId = applicabilityId;
   }

   protected final void internalSetModType(ModificationType modType, boolean useBackingData) {
      lastValidModType = this.modType;
      this.modType = modType;
      this.useBackingData = useBackingData;
   }

   /**
    * This is used to mark that the artifact deleted.
    */
   public final void internalSetDeleted() {
      internalSetModType(ModificationType.DELETED, true);

      for (Attribute<?> attribute : getAttributes()) {
         attribute.setArtifactDeleted();
      }
   }

   public final void internalSetDeletedFromRemoteEvent() {
      if (!isHistorical()) {
         this.modType = ModificationType.DELETED;
         ArtifactCache.deCache(this);
         RelationManager.deCache(this);

         for (Attribute<?> attribute : getAttributes()) {
            attribute.internalSetModType(ModificationType.DELETED, true, false);
         }
      }
   }

   /**
    * This is used to mark that the artifact not deleted. This should only be called by the RemoteEventManager.
    */
   public final void resetToPreviousModType() {
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
   public final boolean hasDirtyAttributes() {
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
   public final boolean hasDirtyRelations() {
      return RelationManager.hasDirtyLinks(this);
   }

   public final EditState getEditState() {
      return objectEditState;
   }

   public final boolean hasDirtyArtifactType() {
      return objectEditState.isArtifactTypeChange();
   }

   /**
    * @return whether this artifact has unsaved relation changes
    */
   public final boolean isDirty() {
      return hasDirtyAttributes() || hasDirtyRelations() || hasDirtyArtifactType();
   }

   public final boolean isReadOnly() {
      boolean result = true;
      AccessPolicy service = null;
      try {
         service = ServiceUtil.getAccessPolicy();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      if (service != null) {
         result = service.isReadOnly(this);
      }
      return result;
   }

   /**
    * Revert artifact to it's state at base transaction of the branch. <br>
    * This will remove all changes from osee_txs for this artifact on it's branch.<br>
    * <br>
    * NOTE: This should NOT normally be used for baseline branches as the artifact will disappear from existence. <br>
    * <br>
    * Instead use reloadAttributesAndRelations() to restore in memory artifact back to it's non-dirty state.
    */
   /**
    * Reloads this artifact's attributes and relations back to the last state saved. <br>
    * <br>
    * This will have no effect if the artifact has never been saved.
    */
   public final void reloadAttributesAndRelations() {
      if (!isInDb()) {
         return;
      }

      ArtifactQuery.reloadArtifactFromId(this, getBranch());
   }

   void prepareForReload() {
      attributes.clear();
      linksLoaded = false;

      RelationManager.prepareRelationsForReload(this);
   }

   public final TransactionId persist(String comment) {
      SkynetTransaction transaction = TransactionManager.createTransaction(branch, comment);
      persist(transaction);
      return transaction.execute();
   }

   /**
    * <b>THIS ASSUMES YOU ARE MAINTAINING YOUR OWN TRANSACTION</b> vs {@link #SkynetTransaction.persist(String)} where
    * silently you are provided a transaction.
    * <p>
    * Example:
    *
    * <pre>
    * ...
    * Artifact artifact = ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, ARTIFACT_BRANCH);
    * ...
    * <b>SkynetTransaction transaction = TransactionManager.createTransaction(ARTIFACT_BRANCH, name);</b>
    * ...
    * <b>artifact.persist(transaction);</b>
    * ...
    * <b>transaction.execute();</b>
    * ...
    * </pre>
    * </p>
    */
   public final void persist(SkynetTransaction managedTransaction) {
      managedTransaction.addArtifact(this);
   }

   /**
    * Starting from this artifact, walks down the child hierarchy based on the list of child names provided and returns
    * the child of the last name provided. ArtifactDoesNotExist exception is thrown ff any child along the path does not
    * exist.
    *
    * @return child at the leaf (bottom) of the specified hierarchy.
    */
   public final Artifact getDescendant(String... names) {
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
   public final void deleteAndPersist() {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(branch, "Delete artifact from a specific branch");
      deleteAndPersist(transaction);
      transaction.execute();
   }

   public final void deleteAndPersist(SkynetTransaction transaction, boolean overrideChecks) {
      ArtifactPersistenceManager.deleteArtifact(transaction, overrideChecks, this);
   }

   /**
    * Removes artifact from a specific branch
    */
   public final void deleteAndPersist(SkynetTransaction transaction) {
      ArtifactPersistenceManager.deleteArtifact(transaction, false, this);
   }

   public final void delete() {
      ArtifactPersistenceManager.deleteArtifact(null, false, this);
   }

   /**
    * Remove artifact from a specific branch in the database
    */
   public final void purgeFromBranch(boolean purgeChildren) {
      Collection<Artifact> artifacts = new LinkedHashSet<>();
      artifacts.add(this);
      if (purgeChildren) {
         artifacts.addAll(getDescendants());
      }
      Operations.executeWorkAndCheckStatus(new PurgeArtifacts(artifacts));
   }

   public final void purgeFromBranch() {
      purgeFromBranch(false);
   }

   public final boolean isDeleted() {
      return modType == ModificationType.DELETED;
   }

   public final void setLinksLoaded(boolean loaded) {
      linksLoaded = loaded;
   }

   public final void addRelation(RelationSorter sorterId, RelationTypeSide relationTypeSide, Artifact artifact, String rationale) {
      Pair<Artifact, Artifact> sides = determineArtifactSides(artifact, relationTypeSide);
      RelationManager.addRelation(sorterId, relationTypeSide.getRelationType(), sides.getFirst(), sides.getSecond(),
         rationale);
   }

   public final void addRelation(RelationTypeSide relationSide, Artifact artifact) {
      addRelation(PREEXISTING, relationSide, artifact, null);
   }

   public final void addRelation(RelationSorter sorterId, RelationTypeSide relationSide, Artifact artifact) {
      addRelation(sorterId, relationSide, artifact, null);
   }

   public final void addRelation(RelationSorter sorterId, RelationTypeSide relationEnumeration, Artifact targetArtifact, boolean insertAfterTarget, Artifact itemToAdd, String rationale) {
      boolean sideA = relationEnumeration.getSide().isSideA();
      Artifact artifactA = sideA ? itemToAdd : this;
      Artifact artifactB = sideA ? this : itemToAdd;

      RelationManager.addRelation(sorterId, relationEnumeration, artifactA, artifactB, rationale);
      setRelationOrder(relationEnumeration, targetArtifact, insertAfterTarget, itemToAdd);
   }

   public final void setRelationOrder(RelationTypeSide relationSide, List<Artifact> artifactsInNewOrder) {
      RelationManager.setRelationOrder(this, relationSide, relationSide.getSide(), USER_DEFINED, artifactsInNewOrder);
   }

   public final void setRelationOrder(RelationTypeSide relationEnumeration, RelationSorter orderId) {
      if (USER_DEFINED == orderId) {
         setRelationOrder(relationEnumeration, getRelatedArtifacts(relationEnumeration));
      } else {
         List<Artifact> empty = java.util.Collections.emptyList();
         RelationManager.setRelationOrder(this, relationEnumeration, relationEnumeration.getSide(), orderId, empty);
      }
   }

   public final void setRelationOrder(RelationTypeSide relationEnumeration, Artifact targetArtifact, boolean insertAfterTarget, Artifact itemToAdd) {
      List<Artifact> currentOrder = getRelatedArtifacts(relationEnumeration, Artifact.class);
      // target artifact doesn't exist
      if (!currentOrder.contains(targetArtifact)) {
         throw new OseeStateException("Could not set Relation Order: target not in list");
      }
      // add to end of list if not already in list
      if (!currentOrder.contains(itemToAdd)) {
         currentOrder.add(itemToAdd);
      }

      boolean result = Collections.moveItem(currentOrder, itemToAdd, targetArtifact, insertAfterTarget);
      if (!result) {
         throw new OseeStateException("Could not set Relation Order");
      }

      RelationManager.setRelationOrder(this, relationEnumeration, relationEnumeration.getSide(), USER_DEFINED,
         currentOrder);
   }

   public final void deleteRelation(RelationTypeSide relationTypeSide, Artifact artifact) {
      Pair<Artifact, Artifact> sides = determineArtifactSides(artifact, relationTypeSide);
      ArtifactPersistenceManager.performDeleteRelationChecks(artifact, relationTypeSide);
      RelationManager.deleteRelation(relationTypeSide, sides.getFirst(), sides.getSecond());
   }

   public final void deleteRelations(RelationTypeSide relationSide) {
      for (Artifact art : getRelatedArtifacts(relationSide)) {
         ArtifactPersistenceManager.performDeleteRelationChecks(art, relationSide);
         deleteRelation(relationSide, art);
      }
   }

   /**
    * Creates new relations that don't already exist and removes relations to artifacts that are not in collection
    */
   public final void setRelations(RelationSorter sorterId, RelationTypeSide relationSide, Collection<? extends Artifact> artifacts) {
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
            addRelation(sorterId, relationSide, artifact);
         }
      }
   }

   /**
    * Creates new relations that don't already exist and removes relations to artifacts that are not in collection
    */
   public final void setRelations(RelationTypeSide relationSide, Collection<? extends Artifact> artifacts) {
      setRelations(PREEXISTING, relationSide, artifacts);
   }

   public final boolean isLinksLoaded() {
      return linksLoaded;
   }

   @Override
   public final ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   public final String getVersionedName() {
      String name = getName();

      if (isHistorical()) {
         name += " [Rev:" + transaction + "]";
      }

      return name;
   }

   /**
    * Return true if this artifact any of it's links specified or any of the artifacts on the other side of the links
    * are dirty
    */
   public final String isRelationsAndArtifactsDirty(Set<RelationTypeSide> links) {
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
         for (RelationTypeSide side : links) {
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
    */
   public final Artifact duplicate(BranchId branch) {
      return duplicate(branch, new ArrayList<AttributeTypeId>());
   }

   public final Artifact duplicate(BranchId branch, Collection<AttributeTypeId> excludeAttributeTypes) {
      return duplicate(branch, getArtifactType(), excludeAttributeTypes);
   }

   public final Artifact duplicate(BranchId branch, ArtifactTypeToken newType, Collection<AttributeTypeId> excludeAttributeTypes) {
      Artifact newArtifact = ArtifactTypeManager.addArtifact(newType, branch);
      // we do this because attributes were added on creation to meet the
      // minimum attribute requirements
      List<AttributeTypeId> typesToClear =
         Collections.setComplement(newArtifact.attributes.keySet(), excludeAttributeTypes);
      for (AttributeTypeId type : typesToClear) {
         newArtifact.attributes.removeValues(type);
      }
      copyAttributes(newArtifact, excludeAttributeTypes);
      return newArtifact;
   }

   private void copyAttributes(Artifact artifact, Collection<AttributeTypeId> excludeAttributeTypes) {
      for (Attribute<?> attribute : getAttributes()) {
         if (!excludeAttributeTypes.contains(attribute.getAttributeType()) && isCopyAllowed(
            attribute) && artifact.isAttributeTypeValid(attribute.getAttributeType())) {
            artifact.addAttribute(attribute.getAttributeType(), attribute.getValue());
         }
      }
   }

   private boolean isCopyAllowed(Attribute<?> attribute) {
      return attribute != null && !attribute.isOfType(CoreAttributeTypes.RelationOrder);
   }

   /**
    * An artifact reflected about its own branch returns itself. Otherwise a new artifact is introduced on the
    * destinationBranch
    *
    * @return the newly created artifact or this artifact if the destinationBranch is this artifact's branch
    */
   public final Artifact reflect(BranchId destinationBranch) {
      return new IntroduceArtifactOperation(destinationBranch).introduce(this);
   }

   Artifact introduceShallowArtifact(BranchId destinationBranch) {
      Artifact shallowArt = ArtifactTypeManager.getFactory(artifactType).reflectExisitingArtifact(this, getGuid(),
         getArtifactType(), gammaId, destinationBranch, modType, applicabilityId);
      return shallowArt;
   }

   void introduce(Artifact sourceArtifact) {
      replaceWithVersion(sourceArtifact.getGammaId(), sourceArtifact.getModType());
   }

   public boolean isUseBackingdata() {
      return useBackingData;
   }

   /**
    * @return the transaction number that was set when this artifact was loaded
    */
   public final TransactionToken getTransaction() {
      return transaction;
   }

   public final GammaId getGammaId() {
      return gammaId;
   }

   public final ApplicabilityId getApplicablityId() {
      return applicabilityId;
   }

   public final Collection<AttributeChange> getDirtyFrameworkAttributeChanges() {
      List<AttributeChange> dirtyAttributes = new LinkedList<>();

      for (Attribute<?> attribute : internalGetAttributes()) {
         if (attribute.isDirty()) {
            AttributeChange change = attribute.createAttributeChangeFromSelf();
            dirtyAttributes.add(change);
         }
      }
      return dirtyAttributes;
   }

   /**
    * Changes the artifact type.
    */
   public final void setArtifactType(ArtifactTypeId artifactTypeId) {
      if (this.artifactType.notEqual(artifactTypeId)) {
         this.artifactType = ArtifactTypeManager.getType(artifactTypeId);
         objectEditState = EditState.ARTIFACT_TYPE_MODIFIED;
         if (isInDb()) {
            internalSetModType(ModificationType.MODIFIED, false);
         }
      }
   }

   public final void clearEditState() {
      objectEditState = EditState.NO_CHANGE;
      resetToPreviousModType();
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
   public final String getSafeName() {
      String elementName = safeNamePattern.matcher(getName()).replaceAll("_");

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

   /**
    * Note: Artifact class does not implement the hashCode, but instead uses the one implemented by Identity. It can not
    * use the branch uuid due to the need for IArtifactTokens to match Artifact instances. In addition, the event system
    * requires that the DefaultBasicGuidArtifact and Artifact hashcode matches.
    *
    * @param obj the reference object with which to compare.
    * @return <code>true</code> if this artifact has the same GUID and branch <code>false</code> otherwise.
    */
   @Override
   public final boolean equals(Object obj) {
      boolean equals = super.equals(obj);
      if (equals && obj instanceof HasBranch && ((HasBranch) obj).getBranch().isValid()) {
         return isOnSameBranch((HasBranch) obj);
      }
      return equals;
   }

   public final int getRemainingAttributeCount(AttributeTypeId attributeType) {
      return AttributeTypeManager.getMaxOccurrences(attributeType) - getAttributeCount(attributeType);
   }

   public final int getAttributeCount(AttributeTypeId attributeType) {
      ensureAttributesLoaded();
      return getAttributes(attributeType).size();
   }

   /**
    * Return relations that exist between artifacts
    */
   public final ArrayList<RelationLink> internalGetRelations(Artifact artifact) {
      ArrayList<RelationLink> relations = new ArrayList<>();
      for (RelationLink relation : getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
         try {
            if (relation.getOtherSideArtifact(this).equals(artifact)) {
               relations.add(relation);
            }
         } catch (ArtifactDoesNotExist ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return relations;
   }

   public final List<RelationLink> getRelations(RelationTypeSide relationEnum) {
      return RelationManager.getRelations(this, relationEnum, relationEnum.getSide());
   }

   /**
    * Return relations that exist between artifacts of type side
    */
   @Deprecated
   public final ArrayList<RelationLink> getRelations(RelationTypeSide side, Artifact artifact) {
      ArrayList<RelationLink> relations = new ArrayList<>();
      for (RelationLink relation : getRelations(side)) {
         try {
            if (relation.getOtherSideArtifact(this).equals(artifact)) {
               relations.add(relation);
            }
         } catch (ArtifactDoesNotExist ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return relations;
   }

   public final List<RelationLink> getRelationsAll(DeletionFlag deletionFlag) {
      return RelationManager.getRelationsAll(this, deletionFlag);
   }

   /**
    * This method should never be called from outside the OSEE Application Framework
    */
   void internalSetPersistenceData(GammaId gammaId, TransactionToken transactionId, ModificationType modType, ApplicabilityId applicabilityId, boolean historical, boolean useBackingData) {
      this.gammaId = gammaId;
      this.transaction = transactionId;
      this.historical = historical;
      internalSetApplicablityId(applicabilityId);
      internalSetModType(modType, useBackingData);
      this.objectEditState = EditState.NO_CHANGE;
   }

   /**
    * This method should never be called from outside the OSEE Application Framework
    */
   public final void setTransactionId(TransactionToken transaction) {
      this.transaction = transaction;
   }

   public final Date getLastModified() {
      if (transaction.isValid()) {
         return TransactionManager.getTransaction(transaction).getTimeStamp();
      }
      return new Date();
   }

   public final User getLastModifiedBy() {
      if (transaction.isInvalid()) {
         return UserManager.getUser(SystemUser.OseeSystem);
      }
      TransactionRecord transactionRecord = TransactionManager.getTransaction(transaction);
      return UserManager.getUserByArtId(transactionRecord.getAuthor());
   }

   void meetMinimumAttributeCounts(boolean isNewArtifact) {
      if (modType == ModificationType.DELETED) {
         return;
      }
      for (AttributeTypeId attributeType : getAttributeTypes()) {
         int missingCount = AttributeTypeManager.getMinOccurrences(attributeType) - getAttributeCount(attributeType);
         for (int i = 0; i < missingCount; i++) {
            initializeAttribute(attributeType, ModificationType.NEW, isNewArtifact, true);
         }
      }
   }

   public final ModificationType getModType() {
      return modType;
   }

   public final DefaultBasicGuidArtifact getBasicGuidArtifact() {
      return new DefaultBasicGuidArtifact(getBranch(), artifactType, this);
   }

   public final Set<DefaultBasicUuidRelationReorder> getRelationOrderRecords() {
      return relationOrderRecords;
   }

   public Set<AttributeType> getAttributeTypesUsed() {
      Set<AttributeType> types = new HashSet<>();
      for (Attribute<?> attr : getAttributes()) {
         types.add(attr.getAttributeType());
      }
      return types;
   }

   public Artifact getRelatedArtifactOrNull(RelationTypeSide relationSide) {
      Artifact artifact = null;
      try {
         artifact = getRelatedArtifact(relationSide);
      } catch (ArtifactDoesNotExist ex) {
         // do nothing
      }
      return artifact;
   }

   public String getGammaIdString() {
      return String.valueOf(getGammaId());
   }
}
