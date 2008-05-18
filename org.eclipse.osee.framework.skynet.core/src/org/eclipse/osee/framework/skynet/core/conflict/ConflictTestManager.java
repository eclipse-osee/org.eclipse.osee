/*
 * Created on May 7, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.conflict;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.Date;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;

/**
 * @author Theron Virgin
 */
public class ConflictTestManager {
   private static Branch sourceBranch;
   private static Branch destBranch;
   private static final int NUMBER_OF_ARTIFACTS = 10;
   private static final int NUMBER_OF_VERSION_ARTIFACTS = 3;
   private static Artifact[] destArtifacts = new Artifact[NUMBER_OF_ARTIFACTS];
   private static Artifact[] sourceArtifacts = new Artifact[NUMBER_OF_ARTIFACTS];
   private static final BranchPersistenceManager branchPersistenceManager = BranchPersistenceManager.getInstance();
   private static final TransactionIdManager transactionIdManager = TransactionIdManager.getInstance();
   private static final ArtifactPersistenceManager artifactPersistenceManager =
         ArtifactPersistenceManager.getInstance();
   private static int NUMBER_OF_CONFLICTS = 6;
   private static int NUMBER_OF_ARTIFACTS_ON_BRANCH = 3;

   public static void initializeConflictTest() throws Exception {
      // Create a new destination branch from the Branch with BranchID = 2

      TransactionId parentTransactionId;
      Branch branch = branchPersistenceManager.getBranch(2);
      parentTransactionId = transactionIdManager.getEditableTransactionId(branch);
      destBranch =
            branchPersistenceManager.createWorkingBranch(parentTransactionId, null, "Conflict_Test_Destination_Branch",
                  null);

      // Add several artifacts onto the destination Branch
      ArtifactSubtypeDescriptor descriptor =
            ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor("Software Requirement");
      Collection<Artifact> artifacts =
            artifactPersistenceManager.getArtifactsFromAttribute("Name", "Software Requirements", destBranch);
      //      Artifact rootArtifact = artifactPersistenceManager.getDefaultHierarchyRootArtifact(destBranch);
      Artifact rootArtifact = artifacts.iterator().next();
      if (rootArtifact == null) {
         throw new Exception("Could not find the Root Artifact");
      }
      // If you fail here you probably don't have your resource server running
      for (int i = 0; i < NUMBER_OF_ARTIFACTS - NUMBER_OF_VERSION_ARTIFACTS; i++) {
         destArtifacts[i] = rootArtifact.addNewChild(descriptor, "Test Artifact Number " + i);
      }
      descriptor = ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor("Version");
      for (int i = NUMBER_OF_ARTIFACTS - NUMBER_OF_VERSION_ARTIFACTS; i < NUMBER_OF_ARTIFACTS; i++) {
         destArtifacts[i] = rootArtifact.addNewChild(descriptor, "Test Artifact Number " + i);
         destArtifacts[i].addAttribute("ats.Release Date", new Date(1));
         destArtifacts[i].persistAttributes();
      }
      // Add attributes to test the full suite of attributes

      // Create the source branch

      parentTransactionId = transactionIdManager.getEditableTransactionId(destBranch);
      sourceBranch =
            branchPersistenceManager.createWorkingBranch(parentTransactionId, null, "Conflict_Test_Source_Branch", null);

      TransactionId tranId = transactionIdManager.getStartEndPoint(sourceBranch).getValue();
      ;
      tranId.setHead(true);
      for (int i = 0; i < NUMBER_OF_ARTIFACTS; i++) {
         sourceArtifacts[i] = artifactPersistenceManager.getArtifactFromId(destArtifacts[i].getArtId(), tranId);
         sourceArtifacts[i].setIds(sourceArtifacts[i].getArtId(), sourceArtifacts[i].getGammaId(), 0);
      }
      // create attribute conflicts

      // Attribute Conflicts for Artifact # 2 A bunch of changes
      sourceArtifacts[2].setSoleAttributeValue("Safety Criticality", "2");
      sourceArtifacts[2].setSoleAttributeValue("Page Type", "Landscape");
      sourceArtifacts[2].setSoleAttributeValue("Subsystem", "Electrical");
      sourceArtifacts[2].setSoleAttributeValue("Name", "Test Artifact Number 2 - Source");
      sourceArtifacts[2].persistAttributes();
      destArtifacts[2].setSoleAttributeValue("Safety Criticality", "3");
      destArtifacts[2].setSoleAttributeValue("Page Type", "Landscape");
      destArtifacts[2].setSoleAttributeValue("Subsystem", "Sights");
      destArtifacts[2].setSoleAttributeValue("Name", "Test Artifact Number 2 - Destination");
      destArtifacts[2].persistAttributes();
      // Attribute Conflicts for Artifact # 8 A Date change
      sourceArtifacts[8].setSoleAttributeValue("ats.Release Date", new Date(20000));
      sourceArtifacts[8].persistAttributes();
      destArtifacts[8].setSoleAttributeValue("ats.Release Date", new Date(50000));
      destArtifacts[8].persistAttributes();
      // create artifact conflicts

      sourceArtifacts[5].setSoleAttributeValue("Safety Criticality", "2");
      sourceArtifacts[5].setSoleAttributeValue("Page Type", "Landscape");
      sourceArtifacts[5].setSoleAttributeValue("Subsystem", "Electrical");
      sourceArtifacts[5].setSoleAttributeValue("Name", "Test Artifact Number 5 - Source");
      sourceArtifacts[5].persistAttributes();
      destArtifacts[5].setSoleAttributeValue("Safety Criticality", "3");
      destArtifacts[5].setSoleAttributeValue("Page Type", "Landscape");
      destArtifacts[5].delete();
      destArtifacts[5].persistAttributes();

      sourceArtifacts[3].setSoleAttributeValue("Safety Criticality", "2");
      sourceArtifacts[3].setSoleAttributeValue("Page Type", "Landscape");
      sourceArtifacts[3].setSoleAttributeValue("Subsystem", "Electrical");
      sourceArtifacts[3].setSoleAttributeValue("Name", "Test Artifact Number 3 - Source");
      sourceArtifacts[3].delete();
      sourceArtifacts[3].persistAttributes();
      destArtifacts[3].setSoleAttributeValue("Safety Criticality", "3");
      destArtifacts[3].setSoleAttributeValue("Page Type", "Landscape");
      destArtifacts[3].persistAttributes();
      // **** create relation conflicts *** //

   }

   public static void cleanUpConflictTest() throws Exception {
      //delete the destination, source and merge branch's
      if (sourceBranch != null && destBranch != null) {
         ConnectionHandlerStatement chStmt = null;

         try {
            String GET_MERGE_BRANCH =
                  "SELECT merge_branch_id FROM osee_define_merge Where source_branch_id = ? and dest_branch_id = ?";
            chStmt =
                  ConnectionHandler.runPreparedQuery(GET_MERGE_BRANCH, SQL3DataType.INTEGER,
                        sourceBranch.getBranchId(), SQL3DataType.INTEGER, destBranch.getBranchId());
            ResultSet resultSet = chStmt.getRset();
            if (resultSet.next()) {
               branchPersistenceManager.deleteBranch(branchPersistenceManager.getBranch(resultSet.getInt("merge_branch_id")));
            }
         } finally {
            DbUtil.close(chStmt);
         }
      }
      Thread.sleep(1000);

      if (sourceBranch != null) {
         branchPersistenceManager.deleteBranch(sourceBranch);
      }
      Thread.sleep(1000);
      if (destBranch != null) {
         branchPersistenceManager.deleteBranch(destBranch);
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
}
