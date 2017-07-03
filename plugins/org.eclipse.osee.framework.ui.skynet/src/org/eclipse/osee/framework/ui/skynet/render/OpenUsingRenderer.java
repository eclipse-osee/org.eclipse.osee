/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.render;

import static org.eclipse.osee.framework.core.enums.PresentationType.SPECIALIZED_EDIT;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

/**
 * @author Ryan D. Brooks
 */
public final class OpenUsingRenderer extends AbstractOperation {
   private final Collection<Artifact> artifacts;
   private final Map<RendererOption, Object> rendererOptions;
   private final PresentationType presentationType;
   private String resutPath;

   public OpenUsingRenderer(Collection<Artifact> artifacts, PresentationType presentationType, Map<RendererOption, Object> rendererOptions) {
      super(String.format("Open for %s using renderer", presentationType), Activator.PLUGIN_ID);
      this.artifacts = artifacts;
      this.rendererOptions = rendererOptions;
      this.presentationType = presentationType;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      if (presentationType == SPECIALIZED_EDIT && !ArtifactGuis.checkOtherEdit(artifacts)) {
         return;
      }
      HashCollection<IRenderer, Artifact> rendererArtifactMap =
         RendererManager.createRenderMap(presentationType, artifacts, rendererOptions);

      for (IRenderer renderer : rendererArtifactMap.keySet()) {
         renderer.open((LinkedList<Artifact>) rendererArtifactMap.getValues(renderer), presentationType);
         if (renderer instanceof DefaultArtifactRenderer) {
            resutPath =
               (String) ((DefaultArtifactRenderer) renderer).getRendererOptionValue(RendererOption.RESULT_PATH_RETURN);
         }
      }
   }

   public String getResultPath() {
      return resutPath;
   }
}