/*********************************************************************
 * Copyright (c) 2022 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.operations.synchronization.forest.denizens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.ComputedCharacteristicToken;
import org.eclipse.osee.framework.core.data.IAttribute;
import org.eclipse.osee.framework.core.data.IRelationLink;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.RelationTypeMultiplicity;
import org.eclipse.osee.framework.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.Named;
import org.eclipse.osee.framework.jdk.core.type.NamedId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;

/**
 * An {@link ArtifactReadable} implementation used to represent the native thing for Spec Relations in the
 * Synchronization Artifact DOM.
 *
 * @author Loren K. Ashley
 */

public class SpecRelationArtifactReadable implements ArtifactReadable {

   /**
    * @implNote for {@link NamedId} and {@link Id}.
    */

   Long id;

   /**
    * @implNote for {@link NamedId} and {@link Named}.
    */

   String name;

   /**
    * Saves the relationship side a name attribute value.
    */

   String relationshipSideAAttributeValue;

   /**
    * Saves the relationship side b name attribute value.
    */

   String relationshipSideBAttributeValue;

   /**
    * Saves the relationship multiplicity attribute value.
    */

   RelationTypeMultiplicity relationshipMultiplicityAttributeValue;

   /**
    * Creates a new {@link ArtifactReadable} implementation with the specified attribute values.
    *
    * @param artifactTypeToken the {@link ArtifactTypeToken} defining the attributes for the artifact.
    * @param sideAName the descriptive name for side A of the relationship.
    * @param sideBName the descriptive name for side B of the relationship.
    * @param relationTypeMultiplicity the relationship multiplicity.
    */

   public SpecRelationArtifactReadable(ArtifactTypeToken artifactTypeToken, String sideAName, String sideBName, RelationTypeMultiplicity relationTypeMultiplicity) {

      //@formatter:off
      assert
            Objects.nonNull( artifactTypeToken )
         && Objects.nonNull( sideAName )
         && Objects.nonNull( sideBName )
         && Objects.nonNull( relationTypeMultiplicity );
      //@formatter:on

      this.id = Objects.requireNonNull(artifactTypeToken).getId();
      this.name = "Synchronization Artifact SpecRelationArtifactReadble - " + artifactTypeToken.getName();
      this.relationshipSideAAttributeValue = sideAName;
      this.relationshipSideBAttributeValue = sideBName;
      this.relationshipMultiplicityAttributeValue = relationTypeMultiplicity;
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public boolean areRelated(RelationTypeSide typeAndSide, ArtifactReadable artifact) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public List<String> fetchAttributesAsStringList(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public ApplicabilityId getApplicability() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public ApplicabilityToken getApplicabilityToken() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public ArtifactTypeToken getArtifactType() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public AttributeReadable<Object> getAttributeById(AttributeId attributeId) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public int getAttributeCount(AttributeTypeToken type) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public int getAttributeCount(AttributeTypeToken type, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public Iterable<Collection<? extends AttributeReadable<Object>>> getAttributeIterable() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public <T> List<IAttribute<T>> getAttributeList(AttributeTypeGeneric<T> attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public <T> ResultSet<? extends AttributeReadable<T>> getAttributes(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public ResultSet<? extends AttributeReadable<Object>> getAttributes(DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnknownAttributeTypeTokenException when the value of an attribute is requested for an attribute type that
    * is not known to the implementation.
    * @implNote All the implemented attribute values are of {@link String} type. Requesting a list that is not of type
    * {@link String} or a super type of {@link String} will result in a class cast exception.
    */

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType) {

      if (AttributeTypeTokens.relationshipSideAAttributeTypeToken.equals(attributeType)) {

         var stringList = new ArrayList<String>();

         if (Objects.nonNull(this.relationshipSideAAttributeValue)) {
            stringList.add(this.relationshipSideAAttributeValue);
         }

         @SuppressWarnings("unchecked")
         var rv = (List<T>) stringList;
         return rv;
      }

      if (AttributeTypeTokens.relationshipSideBAttributeTypeToken.equals(attributeType)) {

         var stringList = new ArrayList<String>();

         if (Objects.nonNull(this.relationshipSideBAttributeValue)) {
            stringList.add(this.relationshipSideBAttributeValue);
         }

         @SuppressWarnings("unchecked")
         var rv = (List<T>) stringList;
         return rv;
      }

      if (AttributeTypeTokens.relationshipMultiplicityAttributeTypeToken.equals(attributeType)) {

         var stringList = new ArrayList<String>();

         if (Objects.nonNull(this.relationshipMultiplicityAttributeValue)) {
            stringList.add(this.relationshipMultiplicityAttributeValue.name());
         }

         @SuppressWarnings("unchecked")
         var rv = (List<T>) stringList;
         return rv;
      }

      throw new UnknownAttributeTypeTokenException(this, attributeType);
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public <T> List<T> getAttributeValues(AttributeTypeToken attributeType, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public BranchToken getBranch() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public List<ArtifactReadable> getChildren() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public Collection<ArtifactId> getChildrenIds() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public <T> T getComputedCharacteristicValue(ComputedCharacteristicToken<T> computedCharacteristic) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public List<ArtifactReadable> getDescendants() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public void getDescendants(List<ArtifactReadable> descendants) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public Collection<AttributeTypeToken> getExistingAttributeTypes() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public Collection<RelationTypeToken> getExistingRelationTypes() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Long getId() {

      return this.id;
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public TransactionId getLastModifiedTransaction() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public int getMaximumRelationAllowed(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public ModificationType getModificationType() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String getName() {
      return this.name;
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public String getRationale(RelationTypeSide typeAndSide, ArtifactReadable readable) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public ResultSet<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public List<ArtifactReadable> getRelated(RelationTypeSide relationTypeSide, DeletionFlag deletionFlag) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public int getRelatedCount(RelationTypeSide typeAndSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public List<ArtifactId> getRelatedIds(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public List<ArtifactReadable> getRelatedList(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public ResultSet<IRelationLink> getRelations(RelationTypeSide relationTypeSide) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public String getSafeName() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public String getSoleAttributeAsString(AttributeTypeToken attributeType, String defaultValue) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public AttributeId getSoleAttributeId(AttributeTypeToken attributeType) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public Long getSoleAttributeId(AttributeTypeToken attributeType, Long defaultValue) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws AttributeDoesNotExist {@inheritDoc}
    * @throws UnknownAttributeTypeTokenException when the value of an attribute is requested for an attribute type that
    * is not known to the implementation.
    */

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType) {

      if (AttributeTypeTokens.relationshipSideAAttributeTypeToken.equals(attributeType)) {

         if (Objects.isNull(this.relationshipSideAAttributeValue)) {
            //@formatter:off
            throw new AttributeDoesNotExist
                         (
                            "Value is not present for a known attribute type.\n   ArtifactReadable Identifier and Name: %s ( \"%s\" )\n   AttributeTypeToken Identifier and Name: %s ( \"%s\" )\nArtifactReadable follows:\n%s\nAttributeTypeToken follows:\n%s\n",
                            this.getIdString(),          this.getName(),
                            attributeType.getIdString(), attributeType.getName(),
                            this.toString(),
                            attributeType.toString()
                         );
            //@formatter:on
         }

         @SuppressWarnings("unchecked")
         var rv = (T) this.relationshipSideAAttributeValue;
         return rv;
      }

      if (AttributeTypeTokens.relationshipSideBAttributeTypeToken.equals(attributeType)) {

         if (Objects.isNull(this.relationshipSideBAttributeValue)) {
            //@formatter:off
            throw new AttributeDoesNotExist
                         (
                            "Value is not present for a known attribute type.\n   ArtifactReadable Identifier and Name: %s ( \"%s\" )\n   AttributeTypeToken Identifier and Name: %s ( \"%s\" )\nArtifactReadable follows:\n%s\nAttributeTypeToken follows:\n%s\n",
                            this.getIdString(),          this.getName(),
                            attributeType.getIdString(), attributeType.getName(),
                            this.toString(),
                            attributeType.toString()
                         );
            //@formatter:on
         }

         @SuppressWarnings("unchecked")
         var rv = (T) this.relationshipSideBAttributeValue;
         return rv;
      }

      if (AttributeTypeTokens.relationshipMultiplicityAttributeTypeToken.equals(attributeType)) {

         if (Objects.isNull(this.relationshipMultiplicityAttributeValue)) {
            //@formatter:off
            throw new AttributeDoesNotExist
                         (
                            "Value is not present for a known attribute type.\n   ArtifactReadable Identifier and Name: %s ( \"%s\" )\n   AttributeTypeToken Identifier and Name: %s ( \"%s\" )\nArtifactReadable follows:\n%s\nAttributeTypeToken follows:\n%s\n",
                            this.getIdString(),          this.getName(),
                            attributeType.getIdString(), attributeType.getName(),
                            this.toString(),
                            attributeType.toString()
                         );
            //@formatter:on
         }

         @SuppressWarnings("unchecked")
         var rv = (T) this.relationshipMultiplicityAttributeValue.name();
         return rv;
      }

      throw new UnknownAttributeTypeTokenException(this, attributeType);

   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, DeletionFlag flag, T defaultValue) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public <T> T getSoleAttributeValue(AttributeTypeToken attributeType, T defaultValue) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public TransactionId getTransaction() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public Collection<AttributeTypeToken> getValidAttributeTypes() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public Collection<RelationTypeToken> getValidRelationTypes() {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public boolean isDescendantOf(ArtifactToken parent) {
      throw new UnsupportedOperationException();
   }

   /**
    * {@inheritDoc}
    *
    * @throws UnsupportedOperationException method is not implemented
    */

   @Override
   public boolean isHistorical() {
      throw new UnsupportedOperationException();
   }

}

/* EOF */
