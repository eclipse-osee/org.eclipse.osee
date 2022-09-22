/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.BranchEndpoint;
import org.eclipse.osee.orcs.rest.model.NewBranch;
import org.eclipse.osee.orcs.rest.model.RelationEndpoint;
import org.junit.Assert;

/**
 * Class of static utility methods to aid with client integration testing.
 *
 * @author Donald G. Dunne
 * @author Loren K. Ashley
 */

public final class TestUtil {

   private TestUtil() {
      // Utility Class - class should only have static methods
   }

   /**
    * Compares two attribute values using the {@link #equals} method unless both objects are instances of the
    * {@link Date} class and then the method {@link Date#compareTo} is used.
    *
    * @param expectedValue the expected value, must not be <code>null</code>.
    * @param actualValue the value to be compared, may be <code>null</code>.
    * @return true, when the values are equal; otherwise, <code>false</code>.
    */

   public static boolean compareAttributeValue(Object expectedValue, Object actualValue) {
      Objects.requireNonNull(expectedValue);
      //@formatter:off
      return
         Objects.nonNull( actualValue )
            ? ( expectedValue instanceof Date )
                 ? ( actualValue instanceof Date )
                      ? (((Date) expectedValue).compareTo( (Date) actualValue) == 0)
                      : false
                 : expectedValue.equals( actualValue )
            : false;
      //@formatter:on
   }

   /**
    * {@link Attribute}s on the <code>attributeList</code> with a value that matches a value on the
    * <code>expectedValue</code> are removed. The matching expected value is also removed from the
    * <code>expectedValueList</code>. At the completion of this method the <code>attributeList</code> will contain
    * {@link Attribute}s with values that did not match a value on the <code>expectedValueList</code>. The
    * <code>expectedValueList</code> will only contain the values that did not match an {@link Attribute} value on the
    * <code>attributeList</code>.
    *
    * @param attributeList the list of {@link Attribute}s to be compared.
    * @param expectedValueList the list of expected attribute values.
    */

   public static void compareAttributeValues(List<Attribute<?>> attributeList, List<Object> expectedValueList) {
      for (int i = 0; i < expectedValueList.size(); i++) {
         if (TestUtil.removeFirstAttributeMatch(attributeList, expectedValueList.get(i))) {
            expectedValueList.remove(i--);
         }
      }
   }

   /**
    * Creates a new artifact under the specified parent on the specified branch.
    *
    * @param artifactEndpoint The REST API end point for artifact functions.
    * @param parentBranchId The branch to create the new artifact upon.
    * @param parentArtifactId The hierarchical parent of the artifact to be created.
    * @param childArtifactId the identifier to create the child artifact with or {@link ArtifactId#SENTINEL}.
    * @param childArtifactTypeToken when creating a child artifact, the type of artifact to create.
    * @param childName the name of the child artifact.
    * @return The {@link ArtifactToken} (identifier) for the newly created artifact.
    */

   private static ArtifactToken createChildArtifactToken(ArtifactEndpoint artifactEndpoint, BranchId parentBranchId, ArtifactId parentArtifactId, ArtifactId childArtifactId, ArtifactTypeToken childArtifactTypeToken, String childName) {

      if (ArtifactId.SENTINEL.equals(childArtifactId)) {
         return artifactEndpoint.createArtifact(parentBranchId, childArtifactTypeToken, parentArtifactId, childName);
      }

      var transaction = TransactionManager.createTransaction(parentBranchId, childName);
      var artifact = ArtifactTypeManager.addArtifact(childArtifactTypeToken, BranchToken.valueOf(parentBranchId),
         childName, childArtifactId);

      Artifact parentArtifact;

      try {
         parentArtifact = ArtifactQuery.getArtifactFromId(parentArtifactId, parentBranchId);
      } catch (Exception e) {
         parentArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(parentBranchId);
      }

      artifact.addRelation(CoreRelationTypes.DefaultHierarchical_Parent, parentArtifact);
      transaction.addArtifact(artifact);
      transaction.execute();

      return artifact;
   }

   /**
    * Creates the specified number of attributes of the specified type on the provided artifact. All of the artifact's
    * attributes of the specified type are returned. The returned list will also contain previously existing attributes.
    *
    * @param artifact the {@link Artifact} to create attributes on.
    * @param attributeTypeGeneric the type of attributes to be created.
    * @param count the number of attributes to be created.
    * @return a list of all the artifact's attributes of the specified type.
    */

   public static List<Attribute<?>> createAttributes(Artifact artifact, AttributeTypeGeneric<?> attributeTypeGeneric, int count) {
      for (int i = 0; i < count; i++) {
         artifact.addAttribute(attributeTypeGeneric);
      }

      @SuppressWarnings("unchecked")
      var attributes = (List<Attribute<?>>) (Object) artifact.getAttributes(attributeTypeGeneric);

      return attributes;
   }

   public static List<RelationLink> createLinks(int total, BranchId branch) {
      List<RelationLink> links = new ArrayList<>();
      for (Long index = 0L; index < total; index++) {
         RelationLink link = createRelationLink(RelationId.valueOf(index), ArtifactId.valueOf(index + 1),
            ArtifactId.valueOf(index + 2), branch, CoreRelationTypes.Allocation);
         links.add(link);
      }
      return links;
   }

   public static RelationLink createRelationLink(RelationId relationId, ArtifactId artA, ArtifactId artB, BranchId branch, RelationTypeToken relationType) {
      return new RelationLink(ArtifactToken.valueOf(artA, BranchToken.valueOf(branch)),
         ArtifactToken.valueOf(artB, BranchToken.valueOf(branch)), branch, relationType, relationId, GammaId.valueOf(0),
         "relation: " + relationId, ModificationType.MODIFIED, ApplicabilityId.BASE);
   }

   /**
    * Creates a simple artifact and adds it to the root artifact default hierarchical relation
    */

   public static Artifact createSimpleArtifact(ArtifactTypeToken artifactType, String name, BranchToken branch) {
      Artifact softArt = ArtifactTypeManager.addArtifact(artifactType, branch);
      softArt.setName(name);
      if (softArt.isAttributeTypeValid(CoreAttributeTypes.Subsystem)) {
         softArt.setSoleAttributeFromString(CoreAttributeTypes.Subsystem, "Electrical");
      }
      Artifact rootArtifact = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      rootArtifact.addRelation(CoreRelationTypes.DefaultHierarchical_Child, softArt);
      return softArt;
   }

   public static Collection<Artifact> createSimpleArtifacts(ArtifactTypeToken artifactType, int numArts, String name, BranchToken branch) {
      List<Artifact> arts = new ArrayList<>();
      for (int x = 1; x < numArts + 1; x++) {
         arts.add(createSimpleArtifact(artifactType, name + " " + x, branch));
      }
      return arts;
   }

   /**
    * Creates a new working branch in the database.
    *
    * @param branchEndpoint REST API end point for branches.
    * @param branchName The name to assign to the new branch.
    * @param creationComment The creation comment for the new branch.
    * @return The newly created {@link Branch}.
    */

   public static Branch createTestBranch(BranchEndpoint branchEndpoint, String branchName, String creationComment) {
      var newBranch = new NewBranch();
      newBranch.setAssociatedArtifact(ArtifactId.SENTINEL);
      newBranch.setBranchName(branchName);
      newBranch.setBranchType(BranchType.WORKING);
      newBranch.setCreationComment(creationComment);
      newBranch.setMergeAddressingQueryId(0L);
      newBranch.setMergeDestinationBranchId(null);
      newBranch.setParentBranchId(CoreBranches.SYSTEM_ROOT);
      newBranch.setSourceTransactionId(TransactionManager.getHeadTransaction(CoreBranches.SYSTEM_ROOT));
      newBranch.setTxCopyBranchType(false);

      var newBranchId = branchEndpoint.createBranch(newBranch);

      return branchEndpoint.getBranchById(newBranchId);
   }

   /**
    * Gets the attributes of the specified type from the artifact.
    *
    * @param artifact the artifact to get attribute from.
    * @param attributeTypeGeneric the type of attributes to get.
    * @return when the artifact has attributes of the specified type, a {@link Optional} with a {@link List} of the
    * {@link Attribute<?>} objects; otherwise, an empty {@link Optional}.
    */

   public static Optional<List<Attribute<?>>> getAttributes(Artifact artifact, AttributeTypeGeneric<?> attributeTypeGeneric) {
      @SuppressWarnings("unchecked")
      var attributes = (List<Attribute<?>>) (Object) artifact.getAttributes(attributeTypeGeneric);

      return (attributes.size() >= 1) ? Optional.of(attributes) : Optional.empty();
   }

   /**
    * Gets a {@link Branch} by name.
    *
    * @param branchEndpoint REST API end point for branches.
    * @param branchName the name of the branch to get.
    * @return If found, an {@link Optional} containing the loaded {@link Branch}; otherwise, an empty {@link Optional}.
    */

   public static Optional<Branch> getBranchByName(BranchEndpoint branchEndpoint, String branchName) {

      return branchEndpoint.getBranches("", "", "", false, false, branchName, "", null, null, null).stream().filter(
         branch -> branch.getName().equals(branchName)).findFirst();
   }

   /**
    * Gets the {@link ArtifactToken} of a hierarchical child artifact by the child artifact's name.
    *
    * @param relationEndpoint REST API end point for relationships.
    * @param parentArtifactId the {@link ArtifactId} of the artifact to find a child of.
    * @param childName the name of the child artifact to find.
    * @return If found, an {@link Optional} containing the {@link ArtifactToken} of the child; otherwise, an empty
    * {@link Optional}.
    */

   public static Optional<ArtifactToken> getChildArtifactTokenByName(RelationEndpoint relationEndpoint, ArtifactId parentArtifactId, String childName) {
      return relationEndpoint.getRelatedHierarchy(parentArtifactId, ArtifactId.SENTINEL).stream().filter(
         artifact -> artifact.getName().equals(childName)).findFirst();
   }

   /**
    * When the artifact has no attributes of the specified type, creates and returns the specified number of attributes;
    * otherwise, returns all of the artifacts attributes of the specified type.
    *
    * @param artifact the artifact to get or create attributes from or for.
    * @param attributeTypeGeneric the type of attribute to get or create.
    * @param count when attributes are created, the number of attributes to create.
    * @return a list of the artifact's existing attributes of the specified type or a list of the newly created
    * attributes.
    */

   public static List<Attribute<?>> getOrCreateAttributes(Artifact artifact, AttributeTypeGeneric<?> attributeTypeGeneric, int count) {
      return TestUtil.getAttributes(artifact, attributeTypeGeneric).orElseGet(
         () -> TestUtil.createAttributes(artifact, attributeTypeGeneric, count));
   }

   /**
    * Gets or creates a hierarchical child of the specified artifact.
    *
    * @param relationEndpoint REST API end point for relationships used to get the hierarchical children of the parent
    * artifact.
    * @param artifactEndpoint The REST API end point used to create the child artifact when necessary.
    * @param branchId the branch to create the artifact on, when necessary.
    * @param parentArtifactId the hierarchical parent artifact identifier.
    * @param childArtifactId the identifier to create the child artifact with or {@link ArtifactId#SENTINEL}.
    * @param childArtifactTypeToken when creating a child artifact, the type of artifact to create.
    * @param childName the name of the child artifact.
    * @return the {@link ArtifactToken} of the existing or newly created hierarchical child with the specified name.
    */

   public static ArtifactToken getOrCreateChildArtifactTokenByName(RelationEndpoint relationEndpoint, ArtifactEndpoint artifactEndpoint, BranchId branchId, ArtifactId parentArtifactId, ArtifactId childArtifactId, ArtifactTypeToken childArtifactTypeToken, String childName) {
      return TestUtil.getChildArtifactTokenByName(relationEndpoint, parentArtifactId, childName).orElseGet(
         () -> TestUtil.createChildArtifactToken(artifactEndpoint, branchId, parentArtifactId, childArtifactId,
            childArtifactTypeToken, childName));
   }

   private static int getTableRowCount(String tableName) {
      return ConnectionHandler.getJdbcClient().fetch(0, "SELECT count(1) FROM " + tableName);
   }

   public static Map<String, Integer> getTableRowCounts(String... tables) {
      Map<String, Integer> data = new HashMap<>();
      for (String tableName : tables) {
         data.put(tableName, getTableRowCount(tableName));
      }
      return data;
   }

   /**
    * Removes the first attribute with a value that compares with the provided object using the method
    * {@link TestUtil#compareAttributeValue}.
    *
    * @param attributeList the list of attributes.
    * @param object the attribute value to search for.
    * @return <code>true</code>, when an attribute value match was found and an attribute was removed from the list;
    * otherwise, <code>false</code>.
    */

   private static boolean removeFirstAttributeMatch(List<Attribute<?>> attributeList, Object object) {
      for (int i = 0, l = attributeList.size(); i < l; i++) {
         if (TestUtil.compareAttributeValue(object, attributeList.get(i).getValue())) {
            attributeList.remove(i);
            return true;
         }
      }

      return false;
   }

   public static void setEveryOtherToDeleted(Collection<RelationLink> sourceLinks) {
      int count = 0;
      for (RelationLink link : sourceLinks) {
         if (count % 2 == 0) {
            link.delete(false);
         }
         count++;
      }

      int deletedCounts = 0;
      for (RelationLink link : sourceLinks) {
         if (link.isDeleted()) {
            deletedCounts++;
         }
      }
      int expected = sourceLinks.isEmpty() ? 0 : sourceLinks.size() / 2;
      Assert.assertEquals("Deleted relation link count did not match", expected, deletedCounts);
   }

   /**
    * Sets an artifact's attribute values of the specified attribute type to the values on the expected value list. Any
    * attributes with values of the specified attribute type that are not on expected value list are removed from the
    * artifact or have their value changed to a value on the expected value list. Attributes will be created and added
    * to the artifact when the artifact has less attribute values of the specified attribute type than values on the
    * expected value list.
    *
    * @param artifact the {@link Artifact} to set attribute values
    * @param attributeTypeGeneric the attribute type to set the values for
    * @param expectedValueList a list of the expected attribute values
    * @param attributeSetter a {@link BiConsumer} used to assign an attribute's value
    */

   public static void setAttributeValues(Artifact artifact, AttributeTypeGeneric<?> attributeTypeGeneric, List<Object> expectedValueList, BiConsumer<Attribute<?>, Object> attributeSetter) {
      var attributeList = TestUtil.getOrCreateAttributes(artifact, attributeTypeGeneric, expectedValueList.size());

      /*
       * Matching attributes and expected values are removed from both lists
       */

      TestUtil.compareAttributeValues(attributeList, expectedValueList);

      if ((attributeList.size() == 0) && (expectedValueList.size() == 0)) {
         /*
          * Noting missing, nothing extra, done
          */

         return;
      }

      /*
       * Remove spare attributes
       */

      for (int i = 0, l = attributeList.size() - expectedValueList.size(); i < l; i++) {
         artifact.deleteAttribute(attributeList.get(0));
         attributeList.remove(0);
      }

      /*
       * Change values in remaining attributes
       */

      for (int i = 0, l = attributeList.size(); i < l; i++) {
         attributeSetter.accept(attributeList.get(i), expectedValueList.get(0));
         expectedValueList.remove(0);
      }

      /*
       * Add missing attributes
       */

      for (int i = 0, l = expectedValueList.size(); i < l; i++) {
         artifact.addAttribute(attributeTypeGeneric, expectedValueList.get(i));
      }

   }

}

/* EOF */
