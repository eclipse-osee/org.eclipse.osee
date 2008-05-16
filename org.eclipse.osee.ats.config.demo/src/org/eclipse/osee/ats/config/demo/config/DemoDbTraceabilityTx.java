/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.util.Requirements;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class DemoDbTraceabilityTx extends AbstractSkynetTxTemplate {

   public DemoDbTraceabilityTx(Branch branch, boolean popup) {
      super(branch);
   }

   @Override
   protected void handleTxWork() throws Exception {
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
            systemArt.relate(RelationSide.REQUIREMENT_TRACE__LOWER_LEVEL, subSystemArts, true);

            for (Artifact subSystemArt : subSystemArts) {
               subSystemArt.relate(RelationSide.REQUIREMENT_TRACE__LOWER_LEVEL, softArts, true);
            }
         }

         // Relate System, SubSystem and Software Requirements to Componets
         for (Artifact art : systemArts)
            art.relate(RelationSide.ALLOCATION__COMPONENT, component, true);
         for (Artifact art : subSystemArts)
            art.relate(RelationSide.ALLOCATION__COMPONENT, component, true);
         for (Artifact art : softArts)
            art.relate(RelationSide.ALLOCATION__COMPONENT, component, true);

         // Create Test Script Artifacts
         Set<Artifact> verificationTests = new HashSet<Artifact>();
         Artifact verificationHeader =
               ArtifactQuery.getArtifactFromTypeAndName("Folder", "Verification Tests",
                     BranchPersistenceManager.getInstance().getDefaultBranch());
         if (verificationHeader == null) throw new IllegalStateException("Could not find Verification Tests header");
         for (String str : new String[] {"A", "B", "C"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_SCRIPT, verificationHeader.getBranch(),
                        "Verification Test " + str);
            verificationTests.add(newArt);
            verificationHeader.relate(RelationSide.DEFAULT_HIERARCHICAL__CHILD, newArt, true);
            newArt.persist(true);
         }
         Artifact verificationTestsArray[] = verificationTests.toArray(new Artifact[verificationTests.size()]);

         // Create Validation Test Procedure Artifacts
         Set<Artifact> validationTests = new HashSet<Artifact>();
         Artifact validationHeader =
               ArtifactQuery.getArtifactFromTypeAndName("Folder", "Validation Tests",
                     BranchPersistenceManager.getInstance().getDefaultBranch());
         if (validationHeader == null) throw new IllegalStateException("Could not find Validation Tests header");
         for (String str : new String[] {"1", "2", "3"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_PROCEDURE, validationHeader.getBranch(),
                        "Validation Test " + str);
            validationTests.add(newArt);
            validationHeader.relate(RelationSide.DEFAULT_HIERARCHICAL__CHILD, newArt, true);
            newArt.persist(true);
         }
         Artifact validationTestsArray[] = validationTests.toArray(new Artifact[validationTests.size()]);

         // Create Integration Test Procedure Artifacts
         Set<Artifact> integrationTests = new HashSet<Artifact>();
         Artifact integrationHeader =
               ArtifactQuery.getArtifactFromTypeAndName("Folder", "Integration Tests",
                     BranchPersistenceManager.getInstance().getDefaultBranch());
         if (integrationHeader == null) throw new IllegalStateException("Could not find integration Tests header");
         for (String str : new String[] {"X", "Y", "Z"}) {
            Artifact newArt =
                  ArtifactTypeManager.addArtifact(Requirements.TEST_PROCEDURE, integrationHeader.getBranch(),
                        "integration Test " + str);
            integrationTests.add(newArt);
            integrationHeader.relate(RelationSide.DEFAULT_HIERARCHICAL__CHILD, newArt, true);
            newArt.persist(true);
         }
         Artifact integrationTestsArray[] = integrationTests.toArray(new Artifact[integrationTests.size()]);

         // Relate Software Artifacts to Tests
         Artifact softReqsArray[] = softArts.toArray(new Artifact[softArts.size()]);
         softReqsArray[0].relate(RelationSide.Validation__Validator, verificationTestsArray[0], true);
         softReqsArray[0].relate(RelationSide.Validation__Validator, verificationTestsArray[1], true);
         softReqsArray[1].relate(RelationSide.Validation__Validator, verificationTestsArray[0], true);
         softReqsArray[1].relate(RelationSide.Validation__Validator, validationTestsArray[1], true);
         softReqsArray[2].relate(RelationSide.Validation__Validator, validationTestsArray[0], true);
         softReqsArray[2].relate(RelationSide.Validation__Validator, integrationTestsArray[1], true);
         softReqsArray[3].relate(RelationSide.Validation__Validator, integrationTestsArray[0], true);
         softReqsArray[4].relate(RelationSide.Validation__Validator, integrationTestsArray[2], true);
         softReqsArray[5].relate(RelationSide.Validation__Validator, validationTestsArray[2], true);

      } catch (Exception ex) {
         OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
      }
   }

}
