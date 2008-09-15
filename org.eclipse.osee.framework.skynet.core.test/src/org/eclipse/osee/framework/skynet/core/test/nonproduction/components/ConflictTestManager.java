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

package org.eclipse.osee.framework.skynet.core.test.nonproduction.components;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.BlobWordAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.BooleanAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.CompressedContentAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.DateAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.EnumeratedAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.FloatingPointAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.IntegerAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.JavaObjectAttribute;
import org.eclipse.osee.framework.skynet.core.attribute.StringAttribute;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 */
public class ConflictTestManager {
   private static final String FOLDER = "System Requirements";
   private static final String SOURCE_BRANCH = "Conflict_Test_Source_Branch";
   private static final String DEST_BRANCH = "Conflict_Test_Destination_Branch";
   private static Branch sourceBranch;
   private static Branch destBranch;
   private static final int NUMBER_OF_ARTIFACTS = 20;
   private static Artifact[] destArtifacts = new Artifact[NUMBER_OF_ARTIFACTS];
   private static Artifact[] sourceArtifacts = new Artifact[NUMBER_OF_ARTIFACTS];
   private static ConflictDefinition[] conflictDefs = new ConflictDefinition[NUMBER_OF_ARTIFACTS];
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private static int NUMBER_OF_CONFLICTS = 0;
   private static int NUMBER_OF_ARTIFACTS_ON_BRANCH = 0;
   public static int DELETION_TEST_QUERY = 1;
   public static int DELETION_ATTRIBUTE_TEST_QUERY = 2;

   protected static class AttributeValue {
      protected String attributeName;
      protected String sourceValue;
      protected String destValue;
      protected String mergeValue;
      protected Class<?> clas;

      protected AttributeValue(String attributeName, String sourceValue, String destValue, String mergeValue, Class<?> clas) {
         this.attributeName = attributeName;
         this.sourceValue = sourceValue;
         this.destValue = destValue;
         this.mergeValue = mergeValue;
         this.clas = clas;
      }

      protected AttributeValue(String attributeName, String sourceValue, Class<?> clas) {
         this.attributeName = attributeName;
         this.sourceValue = sourceValue;
         this.clas = clas;
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

      protected void setValues(String artifactType, boolean sourceDelete, boolean destDelete, int rootArtifact, int queryNumber) {
         this.artifactType = artifactType;
         this.sourceDelete = sourceDelete;
         this.destDelete = destDelete;
         this.rootArtifact = rootArtifact;
         this.queryNumber = queryNumber;
      }
   }

   public static void initializeConflictTest() throws Exception {
      // Create a new destination branch from the Branch with BranchID = 2
      cleanUpConflictTest();
      createConflictDefinitions();
      TransactionId parentTransactionId;
      Branch branch;
      try {
         branch = BranchPersistenceManager.getBranch("Block III Main");
      } catch (Exception ex) {
         branch = BranchPersistenceManager.getBranch("SAW_Bld_1");
      }
      parentTransactionId = transactionIdManager.getEditableTransactionId(branch);
      destBranch = BranchPersistenceManager.createWorkingBranch(parentTransactionId, null, DEST_BRANCH, null);

      Artifact rootArtifact = ArtifactQuery.getArtifactFromAttribute("Name", FOLDER, destBranch);

      // Add artifacts onto the destination Branch
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         ArtifactType artType = ArtifactTypeManager.getType(conflictDefs[i].artifactType);
         //               ConfigurationPersistenceManager.getArtifactSubtypeDescriptor(conflictDefs[i].artifactType);
         if (conflictDefs[i].rootArtifact > 0 && conflictDefs[i].rootArtifact < i) {
            destArtifacts[i] =
                  destArtifacts[conflictDefs[i].rootArtifact].addNewChild(artType, "Test Artifact Number " + i);
         } else {
            destArtifacts[i] = rootArtifact.addNewChild(artType, "Test Artifact Number " + i);
         }
         for (AttributeValue value : conflictDefs[i].newAttributes) {
            destArtifacts[i].addAttribute(value.attributeName, stringToObject(value.clas, value.sourceValue));
            destArtifacts[i].persistAttributes();
         }
      }
      // Create the source branch

      parentTransactionId = transactionIdManager.getEditableTransactionId(destBranch);
      sourceBranch = BranchPersistenceManager.createWorkingBranch(parentTransactionId, null, SOURCE_BRANCH, null);

      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         sourceArtifacts[i] = ArtifactQuery.getArtifactFromId(destArtifacts[i].getArtId(), sourceBranch);
      }
      // create attribute conflicts

      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         int numConflicts = 0;
         int numArtifacts = 0;
         for (AttributeValue value : conflictDefs[i].values) {
            if (value.sourceValue != null) {
               sourceArtifacts[i].setSoleAttributeValue(value.attributeName, stringToObject(value.clas,
                     value.sourceValue));
            }
            if (value.sourceValue != null && value.destValue != null) {
               numConflicts++;
               numArtifacts = 1;
            }
         }
         sourceArtifacts[i].persistAttributes();
         for (AttributeValue value : conflictDefs[i].values) {
            if (value.destValue != null) {
               destArtifacts[i].setSoleAttributeValue(value.attributeName, stringToObject(value.clas, value.destValue));
            }
         }
         destArtifacts[i].persistAttributes();

         if (conflictDefs[i].destDelete) {
            System.out.println("Deleting Artifact with ID " + destArtifacts[i].getArtId());
            destArtifacts[i].delete();
            numConflicts = 0;
            numArtifacts = 0;
            if (!conflictDefs[i].sourceDelete) {
               NUMBER_OF_CONFLICTS++;
               numArtifacts = 1;
            }
         }
         if (conflictDefs[i].sourceDelete) {
            System.out.println("Deleting Artifact with ID " + sourceArtifacts[i].getArtId());
            sourceArtifacts[i].delete();
            numConflicts = 0;
            if (!conflictDefs[i].destDelete) {
               NUMBER_OF_CONFLICTS++;
               numArtifacts = 1;
            }
         }
         NUMBER_OF_CONFLICTS += numConflicts;
         NUMBER_OF_ARTIFACTS_ON_BRANCH += numArtifacts;
      }
   }

   public static void cleanUpConflictTest() throws Exception {
      //delete the destination, source and merge branch's
      Branch sBranch = null;
      Branch dBranch = null;
      Branch mBranch = null;
      try {
         sBranch = BranchPersistenceManager.getBranch(SOURCE_BRANCH);
      } catch (Exception ex) {
      }
      if (sBranch == null) {
         for (Branch branch : BranchPersistenceManager.getArchivedBranches()) {
            if (branch.getBranchName().equals(SOURCE_BRANCH)) {
               sBranch = branch;
               break;
            }
         }
      }
      try {
         dBranch = BranchPersistenceManager.getBranch(DEST_BRANCH);
      } catch (Exception ex) {
      }
      if (dBranch == null) {
         for (Branch branch : BranchPersistenceManager.getArchivedBranches()) {
            if (branch.getBranchName().equals(DEST_BRANCH)) {
               dBranch = branch;
               break;
            }
         }
      }
      try {
         mBranch = BranchPersistenceManager.getMergeBranch(sBranch.getBranchId(), dBranch.getBranchId());
      } catch (Exception ex) {
      }

      if (mBranch != null) {
         BranchPersistenceManager.deleteBranch(mBranch).join();
      }
      if (sBranch != null) {
         BranchPersistenceManager.deleteBranch(sBranch).join();
      }
      if (dBranch != null) {
         BranchPersistenceManager.deleteBranch(dBranch).join();
      }
   }

   /**
    * @return the sourceBranchID
    */
   public static Branch getSourceBranch() {
      return sourceBranch;
   }

   /**
    * @return the destBranchID
    */
   public static Branch getDestBranch() {
      return destBranch;
   }

   /**
    * @return the sourceBranchID
    */
   public static Artifact getSourceArtifact(int position) {
      if (position >= 0 && position < NUMBER_OF_ARTIFACTS) return sourceArtifacts[position];
      return null;
   }

   /**
    * @return the destBranchID
    */
   public static Artifact getDestArtifact(int position) {
      if (position >= 0 && position < NUMBER_OF_ARTIFACTS) return destArtifacts[position];
      return null;
   }

   public static int numberOfConflicts() {
      return NUMBER_OF_CONFLICTS;
   }

   public static int numberOfArtifactsOnMergeBranch() {
      return NUMBER_OF_ARTIFACTS_ON_BRANCH;
   }

   public static boolean hasConflicts() {
      return NUMBER_OF_CONFLICTS > 0;
   }

   public static void resolveAttributeConflict(AttributeConflict conflict) throws Exception {
      int sourceArtifactId = conflict.getSourceArtifact().getArtId();
      String attributeName = conflict.getSourceAttribute().getAttributeType().getName();
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
               if (!stringToObject(value.clas, expected).toString().equals(
                     destArtifacts[i].getSoleAttributeValueAsString(value.attributeName, " "))) {
                  System.err.println("Expected the " + value.attributeName + " attribute to have a value of " + stringToObject(
                        value.clas, expected) + " but got " + destArtifacts[i].getSoleAttributeValueAsString(
                        value.attributeName, " ") + " for Artifact " + destArtifacts[i].getArtId());
                  return false;
               }
            }
         } else {
            if (conflictDefs[i].destDelete && !destArtifacts[i].isDeleted()) {
               System.err.println("Artifact " + destArtifacts[i] + " should be deleted but isn't");
               return false;
            }
         }
      }
      return true;
   }

   public static void createConflictDefinitions() {
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         conflictDefs[i] = new ConflictDefinition();
      }

      conflictDefs[0].setValues("Software Requirement", false, false, 0, 0);
      conflictDefs[1].setValues("Software Requirement", false, false, 0, 0);

      conflictDefs[2].setValues("Software Requirement", false, false, 0, 0);
      conflictDefs[2].values.add(new AttributeValue("Safety Criticality", "2", "3", "Destination",
            StringAttribute.class));
      conflictDefs[2].values.add(new AttributeValue("CSCI", "Sights", "Navigation", "Source", StringAttribute.class));
      conflictDefs[2].values.add(new AttributeValue("Subsystem", "Electrical", "Sights", "Navigation",
            StringAttribute.class));
      conflictDefs[2].values.add(new AttributeValue("Name", "Test Artifact Number 2 - Source",
            "Test Artifact Number 2 - Destination", "Test Artifact Number 2 - Merge", StringAttribute.class));

      conflictDefs[3].setValues("Software Requirement", true, false, 0, 0);
      conflictDefs[3].values.add(new AttributeValue("Safety Criticality", "2", "3", "Destination",
            StringAttribute.class));
      conflictDefs[3].values.add(new AttributeValue("Page Type", "Landscape", "Landscape", "Source",
            StringAttribute.class));
      conflictDefs[3].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[3].values.add(new AttributeValue("Name", "Test Artifact Number 3 - Source", null, "Destination",
            StringAttribute.class));

      conflictDefs[4].setValues("Software Requirement", false, false, 0, 0);

      conflictDefs[5].setValues("Software Requirement", false, true, 0, 0);
      conflictDefs[5].values.add(new AttributeValue("Safety Criticality", "1", "4", "Source", StringAttribute.class));
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
      conflictDefs[18].values.add(new AttributeValue("Name", "Test Artifact Number 14 - Parent", null, "Source",
            StringAttribute.class));
      conflictDefs[19].setValues("Software Requirement", false, false, 18, DELETION_ATTRIBUTE_TEST_QUERY);
      conflictDefs[19].values.add(new AttributeValue("Subsystem", "Electrical", null, "Source", StringAttribute.class));
      conflictDefs[19].values.add(new AttributeValue("Name", "Test Artifact Number 15 - Child", null, "Source",
            StringAttribute.class));

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
         return new Boolean(value.equals(BooleanAttribute.booleanChoices[0]));
      }
      if (clas.equals(IntegerAttribute.class)) {
         if (value.equals("")) return new Integer(0);
         return new Integer(value);
      }
      if (clas.equals(DateAttribute.class)) {
         if (value.equals("")) return new Date(1);
         return new Date(Long.parseLong(value));
      }
      if (clas.equals(FloatingPointAttribute.class)) {
         if (value.equals("")) return new Double(0);
         return new Double(value);
      }
      if (clas.equals(EnumeratedAttribute.class)) {
         return value;
      }
      if (clas.equals(StringAttribute.class)) {
         return value;
      }
      if (clas.equals(BlobWordAttribute.class)) {
         return value;
      }
      if (clas.equals(JavaObjectAttribute.class)) {
         return value;
      }
      if (clas.equals(CompressedContentAttribute.class)) {
         return value;
      }
      return value;
   }

}