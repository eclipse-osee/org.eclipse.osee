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

package org.eclipse.osee.framework.skynet.core.test.cases;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.RelationOrderBaseTypes;
import org.eclipse.osee.framework.core.exception.BranchDoesNotExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.support.test.util.DemoSawBuilds;

/**
 * @author Theron Virgin
 */
public class ConflictTestManager {
   public static enum Type {
      RELATION,
      ARTIFACT,
      ATTRIBUTE;
   };
   public static enum Modification {
      CREATE,
      DELETE,
      CREATE_AND_MODIFY,
      CREATE_AND_DELETE,
      MODIFY,
      MODIFY_AND_DELETE;
   };

   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core.test/debug/Junit"));

   private static final String FOLDER = "System Requirements";
   private static final String SOURCE_BRANCH = "Conflict_Test_Source_Branch";
   private static final String DEST_BRANCH = "Conflict_Test_Destination_Branch";
   private static Branch sourceBranch;
   private static Branch destBranch;
   private static final int NUMBER_OF_ARTIFACTS = 32;
   private static Artifact[] destArtifacts = new Artifact[NUMBER_OF_ARTIFACTS];
   private static Artifact[] sourceArtifacts = new Artifact[NUMBER_OF_ARTIFACTS];
   private static List<ArtifactModification> modifications = new LinkedList<ArtifactModification>();

   private static ConflictDefinition[] conflictDefs = new ConflictDefinition[NUMBER_OF_ARTIFACTS];
   public static int DELETION_TEST_QUERY = 1;
   public static int DELETION_ATTRIBUTE_TEST_QUERY = 2;
   public static int REVERT_ARTIFACT_QUERY = 3;
   public static int REVERT_ATTRIBUTE_QUERY = 4;
   public static int UPDATE_PARENT_QUERY = 5;
   public static int REVERT_REL_LINK_QUERY = 6;

   protected static class AttributeValue {
      protected String attributeName;
      protected String sourceValue;
      protected String destValue;
      protected String mergeValue;
      protected Class<?> clas;
      protected boolean sourceDeleted;
      protected boolean destinationDeleted;

      protected AttributeValue(String attributeName, String sourceValue, String destValue, String mergeValue, Class<?> clas) {
         this(attributeName, sourceValue, destValue, mergeValue, clas, false, false);
      }

      protected AttributeValue(String attributeName, String sourceValue, String destValue, String mergeValue, Class<?> clas, boolean sourceDeleted, boolean destinationDeleted) {
         this.attributeName = attributeName;
         this.sourceValue = sourceValue;
         this.destValue = destValue;
         this.mergeValue = mergeValue;
         this.clas = clas;
         this.sourceDeleted = sourceDeleted;
         this.destinationDeleted = destinationDeleted;
      }

      protected AttributeValue(String attributeName, String sourceValue, Class<?> clas) {
         this.attributeName = attributeName;
         this.sourceValue = sourceValue;
         this.clas = clas;
         sourceDeleted = false;
         destinationDeleted = false;
      }
   }

   protected static class ArtifactModification {
      Type itemToChange;
      Modification modificationToPerform;
      protected String name;
      protected String value;
      protected Object object;
      protected Object object2;
      protected Class<?> clas;
      protected Branch branch;
      protected int rootArtifact;
      protected String type;

      protected ArtifactModification(Type itemToChange, Modification modificationToPerform, int rootArtifact, Branch branch, String type, String name) throws OseeCoreException {
         if (!itemToChange.equals(Type.ARTIFACT)) {
            throw new OseeCoreException("This is the Artifact Constructor");
         }
         this.itemToChange = itemToChange;
         this.modificationToPerform = modificationToPerform;
         this.rootArtifact = rootArtifact;
         this.branch = branch;
         this.type = type;
         this.name = name;
      }

      protected ArtifactModification(Type itemToChange, Modification modificationToPerform, Object object, String name, Class<?> clas, String value) throws OseeCoreException {
         if (!itemToChange.equals(Type.ATTRIBUTE)) {
            throw new OseeCoreException("This is the Attribute Constructor");
         }
         this.itemToChange = itemToChange;
         this.modificationToPerform = modificationToPerform;
         this.object = object;
         this.clas = clas;
         this.value = value;
         this.name = name;
      }

      protected ArtifactModification(Type itemToChange, Modification modificationToPerform, Object object, Object object2) throws OseeCoreException {
         if (!itemToChange.equals(Type.RELATION)) {
            throw new OseeCoreException("This is the Relation Constructor");
         }
         this.itemToChange = itemToChange;
         this.modificationToPerform = modificationToPerform;
         this.object = object;
         this.object2 = object2;
      }
   }

   private static class ConflictDefinition {
      protected Collection<AttributeValue> values = new HashSet<AttributeValue>();
      protected Collection<AttributeValue> newAttributes = new HashSet<AttributeValue>();
      protected String artifactType;
      protected boolean sourceDelete;
      protected boolean destDelete;
      protected int rootArtifact;
      protected int queryNumber;
      protected int numConflicts = 0;
      protected boolean sourceModified = false;
      protected boolean destModified = false;

      protected void setValues(String artifactType, boolean sourceDelete, boolean destDelete, int rootArtifact, int queryNumber) {
         this.artifactType = artifactType;
         this.sourceDelete = sourceDelete;
         this.destDelete = destDelete;
         this.rootArtifact = rootArtifact;
         this.queryNumber = queryNumber;
      }

      protected boolean destinationDeleted(ConflictDefinition[] conflictDefs) {
         if (rootArtifact == 0) {
            return destDelete;
         }
         return destDelete || conflictDefs[rootArtifact].destinationDeleted(conflictDefs);
      }

      protected boolean sourceDeleted(ConflictDefinition[] conflictDefs) {
         if (rootArtifact == 0) {
            return sourceDelete;
         }
         return sourceDelete || conflictDefs[rootArtifact].sourceDeleted(conflictDefs);
      }

      protected int getNumberConflicts(ConflictDefinition[] conflictDefs) {
         if (!destinationDeleted(conflictDefs) && !sourceDeleted(conflictDefs)) {
            return numConflicts;
         } else if (destinationDeleted(conflictDefs) && sourceModified || sourceDeleted(conflictDefs) && destModified) {
            return 1;
         } else {
            return 0;
         }
      }

      protected boolean artifactAdded(ConflictDefinition[] conflictDefs) {
         if (!destinationDeleted(conflictDefs) && !sourceDeleted(conflictDefs)) {
            return numConflicts > 0;
         } else if (destinationDeleted(conflictDefs) && sourceModified || sourceDeleted(conflictDefs) && destModified) {
            return true;
         }
         return false;
      }

   }

   public static void initializeConflictTest() throws Exception {
      cleanUpConflictTest();
      createConflictDefinitions();
      destBranch = BranchManager.createWorkingBranch(DemoSawBuilds.SAW_Bld_1, DEST_BRANCH, null);

      Artifact rootArtifact = ArtifactQuery.getArtifactFromAttribute("Name", FOLDER, destBranch);

      // Add artifacts onto the destination Branch
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         ArtifactType artType = ArtifactTypeManager.getType(conflictDefs[i].artifactType);
         if (conflictDefs[i].rootArtifact > 0 && conflictDefs[i].rootArtifact < i) {
            destArtifacts[i] =
                  destArtifacts[conflictDefs[i].rootArtifact].addNewChild(RelationOrderBaseTypes.USER_DEFINED, artType,
                        "Test Artifact Number " + i);
         } else {
            destArtifacts[i] =
                  rootArtifact.addNewChild(RelationOrderBaseTypes.USER_DEFINED, artType, "Test Artifact Number " + i);
         }
         for (AttributeValue value : conflictDefs[i].newAttributes) {
            destArtifacts[i].addAttribute(value.attributeName, stringToObject(value.clas, value.sourceValue));
         }
         destArtifacts[i].persist();
      }
      // Create the source branch
      sourceBranch = BranchManager.createWorkingBranch(destBranch, SOURCE_BRANCH, null);

      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         sourceArtifacts[i] = ArtifactQuery.getArtifactFromId(destArtifacts[i].getArtId(), sourceBranch);
      }
      // create attribute conflicts

      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         //handle source objects
         for (AttributeValue value : conflictDefs[i].values) {
            // source objects
            if (value.sourceDeleted) {
               sourceArtifacts[i].getSoleAttribute(value.attributeName).delete();
            } else {
               if (value.sourceValue != null) {
                  conflictDefs[i].sourceModified = true;
                  sourceArtifacts[i].setSoleAttributeValue(value.attributeName, stringToObject(value.clas,
                        value.sourceValue));
               }
               if (value.sourceValue != null && value.destValue != null) {
                  conflictDefs[i].numConflicts++;
               }
            }
            // destination objects
            if (value.destinationDeleted) {
               destArtifacts[i].getSoleAttribute(value.attributeName).delete();
            } else if (value.destValue != null) {
               conflictDefs[i].destModified = true;
               destArtifacts[i].setSoleAttributeValue(value.attributeName, stringToObject(value.clas, value.destValue));
            }
         }
         sourceArtifacts[i].persist();
         destArtifacts[i].persist();

      }
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         if (conflictDefs[i].destDelete) {
            System.out.println("Deleting Artifact with ID " + destArtifacts[i].getArtId() + " index " + i);
            destArtifacts[i].deleteAndPersist();
         }
         if (conflictDefs[i].sourceDelete) {
            System.out.println("Deleting Artifact with ID " + sourceArtifacts[i].getArtId() + " index " + i);
            sourceArtifacts[i].deleteAndPersist();
         }
         if (DEBUG) {
            DeletionTest.dumpArtifact(sourceArtifacts[i]);
            for (RelationLink link : sourceArtifacts[i].getRelationsAll(false)) {
               DeletionTest.dumpRelation(link, sourceArtifacts[i]);
            }
            DeletionTest.dumpArtifact(destArtifacts[i]);
            for (RelationLink link : destArtifacts[i].getRelationsAll(false)) {
               DeletionTest.dumpRelation(link, destArtifacts[i]);
            }
            System.out.println(" ");
         }
      }
      performModifications();
   }

   private static void performModifications() throws OseeCoreException {
      createModifications();
      for (ArtifactModification modification : modifications) {
         switch (modification.modificationToPerform) {
            case CREATE:
               switch (modification.itemToChange) {
                  case ARTIFACT:
                     createArtifact(modification.rootArtifact, modification.branch, modification.type,
                           modification.name);
                     break;
                  case ATTRIBUTE:
                     createAttribute((Artifact) modification.object, modification.name, modification.clas,
                           modification.value);
                     break;
                  case RELATION:
                     createRelation((Artifact) modification.object, (Artifact) modification.object2);
                     break;
               }
               break;
            case DELETE:
               break;
            case MODIFY:
               break;
            case CREATE_AND_DELETE:
               switch (modification.itemToChange) {
                  case ARTIFACT:
                     createArtifact(modification.rootArtifact, modification.branch, modification.type,
                           modification.name).deleteAndPersist();
                     break;
                  case ATTRIBUTE:
                     createAttribute((Artifact) modification.object, modification.name, modification.clas,
                           modification.value);
                     ((Artifact) modification.object).deleteSoleAttribute(modification.name);
                     ((Artifact) modification.object).persist();
                     break;
                  case RELATION:
                     createRelation((Artifact) modification.object, (Artifact) modification.object2);
                     ((Artifact) modification.object).deleteRelation(CoreRelationTypes.Dependency__Dependency,
                           (Artifact) modification.object2);
                     ((Artifact) modification.object).persist();
                     break;
               }

               break;
            case CREATE_AND_MODIFY:
               break;
            case MODIFY_AND_DELETE:
               break;
         }
      }
   }

   protected static Artifact createArtifact(int rootArtifactId, Branch branch, String type, String name) throws OseeCoreException {
      Artifact rootArtifact = ArtifactQuery.getArtifactFromAttribute("Name", FOLDER, branch);
      //               ConfigurationPersistenceManager.getArtifactSubtypeDescriptor(conflictDefs[i].artifactType);
      if (rootArtifactId > 0 && rootArtifactId < NUMBER_OF_ARTIFACTS) {
         if (branch.equals(destArtifacts[0].getBranch())) {
            rootArtifact = destArtifacts[rootArtifactId];
         }
         if (branch.equals(sourceArtifacts[0].getBranch())) {
            rootArtifact = sourceArtifacts[rootArtifactId];
         }
      }
      Artifact child =
            rootArtifact.addNewChild(RelationOrderBaseTypes.USER_DEFINED, ArtifactTypeManager.getType(type), name);
      child.persist();
      return child;
   }

   protected static Attribute<?> createAttribute(Artifact artifact, String name, Class<?> clas, String value) throws OseeCoreException {
      artifact.addAttribute(name, stringToObject(clas, value));
      artifact.persist();
      return artifact.getSoleAttribute(name);
   }

   protected static RelationLink createRelation(Artifact artifact, Artifact artifactB) throws OseeCoreException {
      artifact.addRelation(CoreRelationTypes.Dependency__Dependency, artifactB);
      artifact.persist();
      return artifact.getRelations(CoreRelationTypes.Dependency__Dependency).get(0);
   }

   public static void cleanUpConflictTest() throws OseeCoreException, InterruptedException {
      BranchManager.refreshBranches();
      Branch sBranch = getArchivedBranch(SOURCE_BRANCH);
      Branch dBranch = getArchivedBranch(DEST_BRANCH);
      Branch mBranch = null;
      if (sBranch != null && dBranch != null) {
         mBranch = BranchManager.getMergeBranch(sBranch, dBranch);
      }
      purgeBranch(mBranch);
      Thread.sleep(10000);
      purgeBranch(sBranch);
      Thread.sleep(10000);
      purgeBranch(dBranch);
      Thread.sleep(10000);
   }

   private static Branch getArchivedBranch(String branchName) throws OseeCoreException {
      try {
         return BranchManager.getBranch(branchName);
      } catch (BranchDoesNotExist e) {
         Collection<Branch> archivedBranches = BranchManager.getArchivedBranches();
         for (Branch archivedBranch : archivedBranches) {
            if (archivedBranch.getName().equals(branchName)) {
               return archivedBranch;
            }
         }
         return null;
      }
   }

   private static void purgeBranch(Branch branch) throws OseeCoreException {
      if (branch != null) {
         BranchManager.purgeBranch(branch);
      }
   }

   public static Branch getSourceBranch() {
      return sourceBranch;
   }

   public static Branch getDestBranch() {
      return destBranch;
   }

   public static Artifact getSourceArtifact(int position) {
      if (position >= 0 && position < NUMBER_OF_ARTIFACTS) {
         return sourceArtifacts[position];
      }
      return null;
   }

   public static Artifact getDestArtifact(int position) {
      if (position >= 0 && position < NUMBER_OF_ARTIFACTS) {
         return destArtifacts[position];
      }
      return null;
   }

   public static int numberOfConflicts() {
      int total = 0;
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         total += conflictDefs[i].getNumberConflicts(conflictDefs);
      }
      return total;
   }

   public static int numberOfArtifactsOnMergeBranch() {
      int total = 0;
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         total += conflictDefs[i].artifactAdded(conflictDefs) ? 1 : 0;
      }
      return total;
   }

   public static boolean hasConflicts() {
      return numberOfConflicts() > 0;
   }

   public static void resolveAttributeConflict(AttributeConflict conflict) throws Exception {
      int sourceArtifactId = conflict.getSourceArtifact().getArtId();
      String attributeName = conflict.getSourceAttribute(true).getAttributeType().getName();
      AttributeValue aValue = null;
      int artNumber = -1;
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         if (sourceArtifactId == sourceArtifacts[i].getArtId()) {
            artNumber = i;
            break;
         }
      }
      if (artNumber == -1) {
         throw new Exception("Source Artifact " + sourceArtifactId + " could not be found in the list of artifatcs");
      }
      for (AttributeValue value : conflictDefs[artNumber].values) {
         if (value.attributeName.equals(attributeName)) {
            aValue = value;
            break;
         }
      }
      if (aValue == null) {
         throw new Exception(
               "Source Artifact " + sourceArtifactId + " does not have a conflict for the" + attributeName + " attribute");
      }
      if (aValue.mergeValue == null) {
         throw new Exception("Merge Value has a null value so no resolution possible");
      }
      if (aValue.mergeValue.equalsIgnoreCase("Source")) {
         conflict.setToSource();
      } else if (aValue.mergeValue.equalsIgnoreCase("Destination")) {
         conflict.setToDest();
      } else {
         conflict.setAttributeValue(stringToObject(aValue.clas, aValue.mergeValue));
      }
      conflict.getAttribute();
   }

   public static boolean validateCommit() throws Exception {
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         if (!conflictDefs[i].destDelete && !conflictDefs[i].sourceDelete) {
            for (AttributeValue value : conflictDefs[i].values) {
               String expected = value.mergeValue;
               if (expected.equalsIgnoreCase("Source")) {
                  expected = value.sourceValue;
               } else if (expected.equalsIgnoreCase("Destination")) {
                  expected = value.destValue;
               }
               if (value.sourceValue == null) {
                  expected = value.destValue;
               }
               if (value.destValue == null) {
                  expected = value.sourceValue;
               }
               if (value.sourceDeleted) {
                  if (destArtifacts[i].getSoleAttributeValueAsString(value.attributeName, "Deleted").equals("Deleted")) {
                     System.err.println("The attribute should have been deleted but wasn't");
                     return false;
                  }
               } else if (!stringToObject(value.clas, expected).toString().equals(
                     destArtifacts[i].getSoleAttributeValueAsString(value.attributeName, " ")) && !destArtifacts[i].isDeleted()) {
                  System.err.println("Expected the " + value.attributeName + " attribute to have a value of " + stringToObject(
                        value.clas, expected) + " but got " + destArtifacts[i].getSoleAttributeValueAsString(
                        value.attributeName, " ") + " for Artifact " + destArtifacts[i].getArtId() + " conflict index: " + i);
                  return false;
               }
            }
         } else {
            if (conflictDefs[i].destDelete && !destArtifacts[i].isDeleted()) {
               System.err.println("Artifact " + destArtifacts[i] + " " + i + " should be deleted but isn't");
               return false;
            }
         }
      }
      return true;
   }

   public static void createModifications() throws OseeCoreException {
      modifications.clear();
      modifications.add(new ArtifactModification(Type.ARTIFACT, Modification.CREATE_AND_DELETE, 0,
            sourceArtifacts[0].getBranch(), "Software Requirement", "Test create an Delete"));
      modifications.add(new ArtifactModification(Type.ATTRIBUTE, Modification.CREATE_AND_DELETE, sourceArtifacts[2],
            "Page Type", StringAttribute.class, "Portrait"));
      modifications.add(new ArtifactModification(Type.RELATION, Modification.CREATE_AND_DELETE, sourceArtifacts[6],
            sourceArtifacts[7]));
   }

   public static void createConflictDefinitions() {
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         conflictDefs[i] = new ConflictDefinition();
      }

      conflictDefs[0].setValues("Software Requirement", false, false, 0, 0);
      conflictDefs[1].setValues("Software Requirement", false, false, 0, 0);

      conflictDefs[2].setValues("Software Requirement", false, false, 0, 0);
      conflictDefs[2].values.add(new AttributeValue("CSCI", "Sights", "Navigation", "Source", StringAttribute.class));
      conflictDefs[2].values.add(new AttributeValue("Subsystem", "Electrical", "Sights", "Navigation",
            StringAttribute.class));
      conflictDefs[2].values.add(new AttributeValue("Name", "Test Artifact Number 2 - Source",
            "Test Artifact Number 2 - Destination", "Test Artifact Number 2 - Merge", StringAttribute.class));

      conflictDefs[3].setValues("Software Requirement", true, false, 0, 0);
      conflictDefs[3].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[3].values.add(new AttributeValue("Name", "Test Artifact Number 3 - Source", null, "Destination",
            StringAttribute.class));

      conflictDefs[4].setValues("Software Requirement", false, false, 0, 0);

      conflictDefs[5].setValues("Software Requirement", false, true, 0, 0);
      conflictDefs[5].values.add(new AttributeValue("Page Type", "Landscape", "Portrait", "Destination",
            StringAttribute.class));
      conflictDefs[5].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[5].values.add(new AttributeValue("Name", "Test Artifact Number 5 - Source", null, "Source",
            StringAttribute.class));

      conflictDefs[6].setValues("Software Requirement", false, false, 0, 0);

      conflictDefs[7].setValues("Version", false, false, 0, 0);

      conflictDefs[8].setValues("Version", false, false, 0, 0);
      conflictDefs[8].values.add(new AttributeValue("ats.Release Date", "2000", "50000000", "Source",
            DateAttribute.class));

      conflictDefs[9].setValues("Version", false, false, 0, 0);

      conflictDefs[10].setValues("Software Requirement", false, false, 0, DELETION_TEST_QUERY);
      conflictDefs[10].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[10].values.add(new AttributeValue("Name", "Test Artifact Number 10 - Parent", null, "Source",
            StringAttribute.class));
      conflictDefs[11].setValues("Software Requirement", false, false, 10, 0);
      conflictDefs[11].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[11].values.add(new AttributeValue("Name", "Test Artifact Number 11 - Child", null, "Source",
            StringAttribute.class));
      conflictDefs[12].setValues("Software Requirement", false, false, 10, 0);
      conflictDefs[12].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[12].values.add(new AttributeValue("Name", "Test Artifact Number 12 - Child/Parent", null, "Source",
            StringAttribute.class));
      conflictDefs[13].setValues("Software Requirement", false, false, 12, 0);
      conflictDefs[13].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[13].values.add(new AttributeValue("Name", "Test Artifact Number 13 - Child", null, "Source",
            StringAttribute.class));

      conflictDefs[14].setValues("Software Requirement", false, false, 0, 0);
      conflictDefs[14].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[14].values.add(new AttributeValue("Name", "Test Artifact Number 14 - Parent", null, "Source",
            StringAttribute.class));

      conflictDefs[15].setValues("Software Requirement", false, false, 14, DELETION_TEST_QUERY);
      conflictDefs[15].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[15].values.add(new AttributeValue("Name", "Test Artifact Number 15 - Child", null, "Source",
            StringAttribute.class));
      conflictDefs[16].setValues("Software Requirement", false, false, 14, 0);
      conflictDefs[16].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[16].values.add(new AttributeValue("Name", "Test Artifact Number 16 - Child/Parent", null, "Source",
            StringAttribute.class));
      conflictDefs[17].setValues("Software Requirement", false, false, 15, 0);
      conflictDefs[17].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[17].values.add(new AttributeValue("Name", "Test Artifact Number 17 - Child", null, "Source",
            StringAttribute.class));

      conflictDefs[18].setValues("Software Requirement", false, false, 0, 0);
      conflictDefs[18].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[18].values.add(new AttributeValue("Name", "Test Artifact Number 18 - Parent", null, "Source",
            StringAttribute.class));
      conflictDefs[19].setValues("Software Requirement", false, false, 18, DELETION_ATTRIBUTE_TEST_QUERY);
      conflictDefs[19].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[19].values.add(new AttributeValue("Name", "Test Artifact Number 19 - Child", null, "Source",
            StringAttribute.class));

      conflictDefs[20].setValues("Software Requirement", true, false, 0, REVERT_ARTIFACT_QUERY);
      conflictDefs[20].values.add(new AttributeValue("Subsystem", "Electrical", "Sights", "Source",
            StringAttribute.class));
      conflictDefs[20].values.add(new AttributeValue("Name", "Test Artifact Number 20 - Parent", null, "Source",
            StringAttribute.class));
      conflictDefs[21].setValues("Software Requirement", false, false, 20, 0);
      conflictDefs[21].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[21].values.add(new AttributeValue("Name", "Test Artifact Number 21 - Child", null, "Source",
            StringAttribute.class));
      conflictDefs[22].setValues("Software Requirement", true, false, 20, 0);
      conflictDefs[22].values.add(new AttributeValue("Subsystem", "Electrical", "dest", "Source", StringAttribute.class));
      conflictDefs[22].values.add(new AttributeValue("Name", "Test Artifact Number 22 - Child/Parent", null, "Source",
            StringAttribute.class));
      conflictDefs[23].setValues("Software Requirement", true, false, 22, 0);
      conflictDefs[23].values.add(new AttributeValue("Subsystem", "Electrical", "Sights", "Source",
            StringAttribute.class));
      conflictDefs[23].values.add(new AttributeValue("Name", "Test Artifact Number 23 - Child", "The Other Name",
            "Source", StringAttribute.class));

      conflictDefs[24].setValues("Software Requirement", false, false, 0, 0);
      conflictDefs[24].values.add(new AttributeValue("Subsystem", "Electrical", "Sights", "Source",
            StringAttribute.class));
      conflictDefs[24].values.add(new AttributeValue("Name", "Test Artifact Number 24 - Parent", null, "Source",
            StringAttribute.class));
      conflictDefs[25].setValues("Software Requirement", false, false, 24, 0);
      conflictDefs[25].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[25].values.add(new AttributeValue("Name", "Test Artifact Number 25 - Child", null, "Source",
            StringAttribute.class));
      conflictDefs[26].setValues("Software Requirement", false, false, 24, REVERT_REL_LINK_QUERY);
      conflictDefs[26].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[26].values.add(new AttributeValue("Name", "Test Artifact Number 26 - Child/Parent", null, "Source",
            StringAttribute.class));
      conflictDefs[27].setValues("Software Requirement", false, false, 26, REVERT_ATTRIBUTE_QUERY);
      conflictDefs[27].values.add(new AttributeValue("Subsystem", "Electrical", "Sights", "Source",
            StringAttribute.class));
      conflictDefs[27].values.add(new AttributeValue("Name", "Test Artifact Number 27 - Child", "The Other Name",
            "Source", StringAttribute.class));

      conflictDefs[28].setValues("Software Requirement", false, false, 0, UPDATE_PARENT_QUERY);
      conflictDefs[28].values.add(new AttributeValue("Subsystem", "Electrical", "Sights", "Source",
            StringAttribute.class));
      conflictDefs[28].values.add(new AttributeValue("Name", "Test Artifact Number 28 Source",
            "Test Artifact Number 28 Destination", "Source", StringAttribute.class));
      conflictDefs[29].setValues("Software Requirement", false, false, 28, 0);
      conflictDefs[29].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[29].values.add(new AttributeValue("Name", "Test Artifact Number 29 - Child", null, "Source",
            StringAttribute.class));
      conflictDefs[30].setValues("Software Requirement", false, false, 28, 0);
      conflictDefs[30].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[30].values.add(new AttributeValue("Name", "Test Artifact Number 30 - Child/Parent", null, "Source",
            StringAttribute.class));
      conflictDefs[31].setValues("Software Requirement", false, false, 30, 0);
      conflictDefs[31].values.add(new AttributeValue("Subsystem", "Electrical", "Sights", "Source",
            StringAttribute.class));
      conflictDefs[31].values.add(new AttributeValue("Name", "Test Artifact Number 31 - Child", "The Other Name",
            "Source", StringAttribute.class));
   }

   public static List<Artifact> getArtifacts(boolean sourceBranch, int queryId) {
      List<Artifact> queriedArtifacts = new LinkedList<Artifact>();
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         if (conflictDefs[i].queryNumber == queryId) {
            if (sourceBranch) {
               queriedArtifacts.add(sourceArtifacts[i]);
            } else {
               queriedArtifacts.add(destArtifacts[i]);
            }
         }
      }
      return queriedArtifacts;
   }

   @SuppressWarnings( {"unchecked"})
   public static Object stringToObject(Class clas, String value) {

      if (clas.equals(BooleanAttribute.class)) {
         return Boolean.valueOf(value.equals(BooleanAttribute.booleanChoices[0]));
      }
      if (clas.equals(IntegerAttribute.class)) {
         if (value.equals("")) {
            return Integer.valueOf(0);
         }
         return new Integer(value);
      }
      if (clas.equals(DateAttribute.class)) {
         if (value.equals("")) {
            return new Date(1);
         }
         return new Date(Long.parseLong(value));
      }
      if (clas.equals(FloatingPointAttribute.class)) {
         if (value.equals("")) {
            return new Double(0);
         }
         return new Double(value);
      }
      return value;
   }
}