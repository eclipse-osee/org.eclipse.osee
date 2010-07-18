/*
 * Created on May 6, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config;

import java.util.Collections;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.AtsFolderUtil;
import org.eclipse.osee.ats.util.AtsFolderUtil.AtsFolder;
import org.eclipse.osee.ats.util.AtsRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkItemDefinitionFactory;

/**
 * @author Donald G. Dunne
 */
public class AtsLoadConfigArtifactsOperation extends AbstractOperation {
   private static boolean loaded = false;

   public AtsLoadConfigArtifactsOperation() {
      super("ATS Loading Configuration", AtsPlugin.PLUGIN_ID);
   }

   public synchronized void ensureLoaded() throws OseeCoreException {
      if (!loaded) {
         loaded = true;
         OseeLog.log(SkynetGuiPlugin.class, Level.INFO, "Loading ATS Configuration");
         Artifact headingArt = AtsFolderUtil.getFolder(AtsFolder.Ats_Heading);
         // Loading artifacts will cache them in ArtifactCache
         RelationManager.getRelatedArtifacts(Collections.singleton(headingArt), 8,
               CoreRelationTypes.Default_Hierarchical__Child, AtsRelationTypes.TeamDefinitionToVersion_Version);
         // Load Work Definitions
         WorkItemDefinitionFactory.loadDefinitions();
         loaded = true;
      }
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      ensureLoaded();
   }

   public static boolean isLoaded() {
      return loaded;
   }

}
