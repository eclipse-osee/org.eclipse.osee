/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.define.api;

import java.util.List;
import java.util.Set;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Morgan E. Cook
 */
public interface MSWordOperations {

   public WordUpdateChange updateWordArtifacts(WordUpdateData data);

   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data);

   public String renderWordTemplateContentWithApplicability(BranchId branchId, ArtifactId viewId, String data);

   public Response msWordTemplatePublish(BranchId branch, ArtifactId template, ArtifactId headArtifact, ArtifactId view);

   public Response msWordPreview(BranchId branch, ArtifactId template, List<ArtifactId> artifacts, ArtifactId view);

   public String renderPlainText(BranchId branchId, String data);

   /**
    * Gets all artifacts under a shared publishing folder (artifact) of a specific type with a specific attribute value.
    * When the type is specified as {@link ArtifactId#SENTINEL} all artifacts regardless of type with the specified
    * value in the specified attribute will be included.
    *
    * @param branch the branch to search for the shared publishing folder.
    * @param view the branch view to search for the shared publishing folder. This parameter maybe
    * {@link ArtifactId#SENTINEL}.
    * @param sharedFolder the artifact identifier of the shared publishing folder.
    * @param artifactType the type of child artifacts to get. This parameter maybe {@link ArtifactTypeToken#SENTINEL}.
    * @param attributeType the child artifact attribute to be checked.
    * @param attributeValue the required child artifact value.
    * @return a list of the artifacts of the specified type with the specified attribute value.
    * @throws BadRequestException when an error occurs finding the shared publishing folder or the shared artifacts.
    */

   public List<ArtifactToken> getSharedPublishingArtifacts(BranchId branch, ArtifactId view, ArtifactId sharedFolder, ArtifactTypeToken artifactType, AttributeTypeToken attributeType, String attributeValue);

}
