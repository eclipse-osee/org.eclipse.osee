/*
 * Created on May 29, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.workflow;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public class WorkFlowDefinitionFactory {

   public static WorkFlowDefinition getWorkFlowDefinition(Artifact artifact) throws Exception {

      // Check extensions for WorkFlowDefinition for given artifact
      for (IWorkDefinitionProvider provider : WorkDefinitionProvider.getWorkDefinitionProviders()) {
         WorkFlowDefinition workFlowDefinition = provider.getWorkFlowDefinition(artifact);
         if (workFlowDefinition != null) return workFlowDefinition;
      }
      throw new IllegalArgumentException(
            "No WorkFlowDefinition found for artifact " + artifact.getArtifactTypeName() + " - " + artifact.getHumanReadableId());
   }
}
