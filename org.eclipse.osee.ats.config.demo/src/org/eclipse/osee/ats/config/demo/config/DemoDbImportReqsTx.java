/*
 * Created on May 15, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.config;

import java.io.File;
import java.sql.SQLException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.config.DemoDatabaseConfig.SawBuilds;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportJob;
import org.eclipse.osee.framework.ui.skynet.Import.IArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.Import.NewArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.Import.WordOutlineExtractor;
import org.eclipse.osee.framework.ui.skynet.handler.GeneralWordOutlineHandler;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class DemoDbImportReqsTx extends AbstractSkynetTxTemplate {

   public DemoDbImportReqsTx(Branch branch, boolean popup) {
      super(branch);
   }

   @Override
   protected void handleTxWork() throws OseeCoreException, SQLException {
      try {
         importRequirements(SawBuilds.SAW_Bld_1.name(), Requirements.SOFTWARE_REQUIREMENT + "s",
               Requirements.SOFTWARE_REQUIREMENT, "support/SAW-SoftwareRequirements.xml");
         importRequirements(SawBuilds.SAW_Bld_1.name(), Requirements.SYSTEM_REQUIREMENT + "s",
               Requirements.SYSTEM_REQUIREMENT, "support/SAW-SystemRequirements.xml");
         importRequirements(SawBuilds.SAW_Bld_1.name(), Requirements.SUBSYSTEM_REQUIREMENT + "s",
               Requirements.SUBSYSTEM_REQUIREMENT, "support/SAW-SubsystemRequirements.xml");
      } catch (Exception ex) {
         OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
      }
   }

   private void importRequirements(String buildName, String rootArtifactName, String requirementArtifactName, String filename) throws Exception {

      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
            "Importing \"" + rootArtifactName + "\" requirements on branch \"" + buildName + "\"", false);
      Branch branch = BranchPersistenceManager.getKeyedBranch(buildName);
      Artifact systemReq = ArtifactQuery.getArtifactFromTypeAndName("Folder", rootArtifactName, branch);

      File file = OseeAtsConfigDemoPlugin.getInstance().getPluginFile(filename);
      IArtifactImportResolver artifactResolver = new NewArtifactImportResolver();
      ArtifactType mainDescriptor = ArtifactTypeManager.getType(requirementArtifactName);
      ArtifactExtractor extractor =
            new WordOutlineExtractor(mainDescriptor, branch, 0, new GeneralWordOutlineHandler());
      Job job = new ArtifactImportJob(file, systemReq, extractor, branch, artifactResolver);
      job.setPriority(Job.LONG);
      job.schedule();
      job.join();
      // Validate that something was imported
      if (systemReq.getChildren().size() == 0) throw new IllegalStateException("Artifacts were not imported");

   }

}
