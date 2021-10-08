/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.framework.ui.skynet.render;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.util.RendererOption;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.templates.TemplateManager;

/**
 * This WordRenderer is used for the purpose of making a REST Call to the RenderEndpoint for previewing artifacts.
 *
 * @author Branden W. Phillips
 */
public class MSWordRestRenderer extends WordRenderer {

   public MSWordRestRenderer(Map<RendererOption, Object> options) {
      super(options);
   }

   public MSWordRestRenderer() {
      this(new HashMap<RendererOption, Object>());
   }

   @Override
   public MSWordRestRenderer newInstance() {
      return new MSWordRestRenderer(new HashMap<RendererOption, Object>());
   }

   @Override
   public MSWordRestRenderer newInstance(Map<RendererOption, Object> rendererOptions) {
      return new MSWordRestRenderer(rendererOptions);
   }

   @Override
   public InputStream getRenderInputStream(PresentationType presentationType, List<Artifact> artifacts) {
      // Get the branch
      Artifact firstArtifact = artifacts.get(0);
      BranchId branchId = firstArtifact.getBranch();

      // Get the template used for this publish
      String option = (String) getRendererOptionValue(RendererOption.TEMPLATE_OPTION);
      Artifact template = TemplateManager.getTemplate(this, artifacts.get(0), presentationType, option);
      ArtifactId templateId = ArtifactId.valueOf(template.getId());

      // Create the list of artifact ids to be published
      List<ArtifactId> artifactIds = new LinkedList<>();
      for (Artifact art : artifacts) {
         artifactIds.add(ArtifactId.valueOf(art.getId()));
      }

      // Get the view for the publish
      ArtifactId viewId = ((ArtifactId) getRendererOptionValue(RendererOption.VIEW));

      Response msWordPreview =
         ServiceUtil.getOseeClient().getRenderEndpoint().msWordPreview(branchId, templateId, artifactIds, viewId);
      String contents = msWordPreview.readEntity(String.class);
      if (contents != null && !contents.isEmpty() && contents.endsWith(WordCoreUtil.END_DOCUMENT)) {
         try {
            return IOUtils.toInputStream(contents, "UTF-8");
         } catch (IOException ex) {
            throw new OseeCoreException(
               "There was a problem reading the document contents from the server, please try a different publish");
         }
      } else {
         throw new OseeCoreException(
            "There was a problem with the document contents received by the server, please try a different publish");
      }

   }

   /**
    * This renderer should only be used for the commands specifically calling out the PREVIEW_SERVER PresentationType.
    */
   @Override
   public int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      int rating = NO_MATCH;
      if (presentationType.equals(PresentationType.PREVIEW_SERVER)) {
         rating = SPECIALIZED_KEY_MATCH;
      }
      return rating;
   }

   /**
    * This method is forced to be implemented but currently should never be used.
    */
   @Override
   protected IOperation getUpdateOperation(File file, List<Artifact> artifacts, BranchId branch, PresentationType presentationType) {
      return new UpdateArtifactOperation(file, artifacts, branch, false);
   }

}
