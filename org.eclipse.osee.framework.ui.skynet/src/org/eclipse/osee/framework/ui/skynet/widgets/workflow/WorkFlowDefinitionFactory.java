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
