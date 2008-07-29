/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class DemoDbTraceabilityTx extends AbstractSkynetTxTemplate {

   public DemoDbTraceabilityTx(Branch branch, boolean popup) {
      super(branch);
   }

   private void relate(IRelationEnumeration relationSide, Artifact artifact, Collection<Artifact> artifacts) throws SQLException {
      for (Artifact otherArtifact : artifacts) {
         artifact.addRelation(relationSide, otherArtifact);
      }
   }

   @Override
   protected void handleTxWork() throws OseeCoreException, SQLException {
      try {
         Collection<Artifact> systemArts = DemoDbUtil.getArtTypeRequirements(Requirements.SYSTEM_REQUIREMENT, "Robot");

         Collection<Artifact> component = DemoDbUtil.getArtTypeRequirements(Requirements.COMPONENT, "API");
         component.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.COMPONENT, "Hardware"));
         component.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.COMPONENT, "Sensor"));

         Collection<Artifact> subSystemArts =
               DemoDbUtil.getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Robot");
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Video"));
         subSystemArts.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Interface"));

         Collection<Artifact> softArts = DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, "Robot");
         softArts.addAll(DemoDbUtil.getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, "Interface"));

         // Relate System to SubSystem to Software Requirements
         for (Artifact systemArt : systemArts) {
            relate(CoreRelationEnumeration.REQUIREMENT_TRACE__LOWER_LEVEL, systemArt, subSystemArts);
            systemArt.persistRelations();

            for (Artifact subSystemArt : subSystemArts) {
               relate(CoreRelationEnumeration.REQUIREMENT_TRACE__LOWER_LEVEL, subSystemArt, softArts);
               subSystemArt.persistRelations();
            }
         }

         // Relate System, SubSystem and Software Requirements to Componets
         for (Artifact art : systemArts) {
            relate(CoreRelationEnumeration.ALLOCATION__COMPONENT, art, component);
            art.persistRelations();
         }
         for (Artifact art : subSystemArts) {
            relate(CoreRelationEnumeration.ALLOCATION__COMPONENT, art, component);
            art.persistRelations();
         }
         for (Artifact art : softArts) {
            relate(CoreRelationEnumeration.ALLOCATION__COMPONENT, art, component);
         }

         // Create Test Script Artifacts
         Set<Artifact> verificationTests = new HashSet<Artifact>();
         Artifact verificationHeader =
               ArtifactQuery.getArtifactFromTypeAndName("Folder", "Verification Tests",
                     BranchPersistenceManager.getDefaultBranch());
         if (verificationHeader == null) throw new IllegalStateException("Could not find Verification Tests header");
         for (String str : new String[] {"A", "B", "C"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_SCRIPT, verificationHeader.getBranch(),
                        "Verification Test " + str);
            verificationTests.add(newArt);
            verificationHeader.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, newArt);
            newArt.persistAttributesAndRelations();
         }
         Artifact verificationTestsArray[] = verificationTests.toArray(new Artifact[verificationTests.size()]);

         // Create Validation Test Procedure Artifacts
         Set<Artifact> validationTests = new HashSet<Artifact>();
         Artifact validationHeader =
               ArtifactQuery.getArtifactFromTypeAndName("Folder", "Validation Tests",
                     BranchPersistenceManager.getDefaultBranch());
         if (validationHeader == null) throw new IllegalStateException("Could not find Validation Tests header");
         for (String str : new String[] {"1", "2", "3"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_PROCEDURE, validationHeader.getBranch(),
                        "Validation Test " + str);
            validationTests.add(newArt);
            validationHeader.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, newArt);
            newArt.persistAttributesAndRelations();
         }
         Artifact validationTestsArray[] = validationTests.toArray(new Artifact[validationTests.size()]);

         // Create Integration Test Procedure Artifacts
         Set<Artifact> integrationTests = new HashSet<Artifact>();
         Artifact integrationHeader =
               ArtifactQuery.getArtifactFromTypeAndName("Folder", "Integration Tests",
                     BranchPersistenceManager.getDefaultBranch());
         if (integrationHeader == null) throw new IllegalStateException("Could not find integration Tests header");
         for (String str : new String[] {"X", "Y", "Z"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_PROCEDURE, integrationHeader.getBranch(),
                        "integration Test " + str);
            integrationTests.add(newArt);
            integrationHeader.addRelation(CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD, newArt);
            newArt.persistAttributesAndRelations();
         }
         Artifact integrationTestsArray[] = integrationTests.toArray(new Artifact[integrationTests.size()]);

         // Relate Software Artifacts to Tests
         Artifact softReqsArray[] = softArts.toArray(new Artifact[softArts.size()]);
         softReqsArray[0].addRelation(CoreRelationEnumeration.Validation__Validator, verificationTestsArray[0]);
         softReqsArray[0].addRelation(CoreRelationEnumeration.Validation__Validator, verificationTestsArray[1]);
         softReqsArray[1].addRelation(CoreRelationEnumeration.Validation__Validator, verificationTestsArray[0]);
         softReqsArray[1].addRelation(CoreRelationEnumeration.Validation__Validator, validationTestsArray[1]);
         softReqsArray[2].addRelation(CoreRelationEnumeration.Validation__Validator, validationTestsArray[0]);
         softReqsArray[2].addRelation(CoreRelationEnumeration.Validation__Validator, integrationTestsArray[1]);
         softReqsArray[3].addRelation(CoreRelationEnumeration.Validation__Validator, integrationTestsArray[0]);
         softReqsArray[4].addRelation(CoreRelationEnumeration.Validation__Validator, integrationTestsArray[2]);
         softReqsArray[5].addRelation(CoreRelationEnumeration.Validation__Validator, validationTestsArray[2]);

         for (Artifact artifact : softArts) {
            artifact.persistAttributesAndRelations();
         }

      } catch (Exception ex) {
         OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
      }
   }
}
