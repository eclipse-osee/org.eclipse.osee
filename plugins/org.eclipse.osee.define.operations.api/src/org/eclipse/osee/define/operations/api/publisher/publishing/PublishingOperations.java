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

package org.eclipse.osee.define.operations.api.publisher.publishing;

import java.util.List;
import java.util.Set;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.eclipse.osee.define.rest.api.publisher.publishing.LinkHandlerResult;
import org.eclipse.osee.define.rest.api.publisher.publishing.MsWordPreviewRequestData;
import org.eclipse.osee.define.rest.api.publisher.publishing.WordUpdateChange;
import org.eclipse.osee.define.rest.api.publisher.publishing.WordUpdateData;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.WordTemplateContentData;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * Interface for server methods for publishing documents.
 *
 * @author Morgan E. Cook
 * @author Loren K. Ashley
 */
public interface PublishingOperations {

   /**
    * Gets the Word ML content for an artifact and replaces the OSEE_LINK markers and legacy hyper-links with WordML
    * hyper links of the type specified by <code>linkType</code>.
    *
    * @param branchId the branch to get the artifact from.
    * @param viewId when valid if the artifact is not applicable to the view its content will not be obtained.
    * @param artifactId the identifier of the artifact to get the Word ML content from.
    * @param transactionId when specified the version of the artifact for the specified transaction will be used.
    * @param linkType the {@link LinkType} of the updated links to be put into the returned Word ML.
    * @param presentationType when the link destination artifact is historical and the <code>linkType</code> is
    * {@link LinkType#OSEE_SERVER_LINK}, the generated replacement link will contain the transaction identifier for the
    * historical destination artifact only when the <code>presentationType</code> is not {@link PresentationType#DIFF}
    * or {@link PresentationType#F5_DIFF}.
    * @return a {@link LinkHandlerResult} with:
    * <ul>
    * <li>The modified artifact content.</li>
    * <li>A set of the {@link ArtifactId}s of the linked artifacts that were found.</li>
    * <li>A set of {@link String}s of the ambiguous artifact identifier strings of the linked artifacts that were not
    * found.</li>
    * </ul>
    */

   LinkHandlerResult link(BranchId branchId, ArtifactId viewId, ArtifactId artifactId, TransactionId transactionId, LinkType linkType, PresentationType presentationType);

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

   @Deprecated
   public Attachment msWordPreview(BranchId branch, ArtifactId template, ArtifactId headArtifact, ArtifactId view);

   @Deprecated
   public Attachment msWordPreview(BranchId branch, ArtifactId template, List<ArtifactId> artifacts, ArtifactId view);

   public Attachment msWordPreview(MsWordPreviewRequestData msWordPreviewRequest);

   public Attachment msWordTemplatePublish(BranchId branch, ArtifactId template, ArtifactId headArtifact, ArtifactId view);

   /**
    * Publishes the content of an artifact's {@link CoreAttributeTypes.WholeWordContent} attribute.
    *
    * @param branchId the branch to get the artifact from.
    * @param viewId when valid if the artifact is not applicable to the view its content will not be obtained.
    * @param artifactId the identifier of the artifact to get the Word ML content from.
    * @param transactionId when specified the version of the artifact for the specified transaction will be used.
    * @param linkType the {@link LinkType} of the updated links to be put into the returned Word ML.
    * @param presentationType the type of presentation the artifact is being rendered for.
    * @param includeErrorLog when <code>true</code> the publishing error log will be appended to the rendered document.
    * @return an {@link Attachment} containing a stream with the published document.
    */

   Attachment msWordWholeWordContentPublish(BranchId branchId, ArtifactId viewId, ArtifactId artifactId, TransactionId transactionId, LinkType linkType, PresentationType presentationType, boolean includeErrorLog);

   public String renderPlainText(BranchId branchId, String data);

   public Pair<String, Set<String>> renderWordTemplateContent(WordTemplateContentData data);

   public String renderWordTemplateContentWithApplicability(BranchId branchId, ArtifactId viewId, String data);

   /**
    * Gets the artifact's WordML with the hyperlinks replaced with OSEE_LINK markers.
    *
    * @param branchId the branch to get the artifact from.
    * @param viewId when valid if the artifact is not applicable to the view its content will not be obtained.
    * @param artifactId the identifier of the artifact to get the Word ML content from.
    * @param transactionId when specified the version of the artifact for the specified transaction will be used.
    * @param linkType the {@link LinkType} of the updated links to be put into the returned Word ML.
    * @return a {@link LinkHandlerResult} with:
    * <ul>
    * <li>The modified artifact content.</li>
    * <li>A set of the {@link ArtifactId}s of the linked artifacts that were found.</li>
    * <li>A set of {@link String}s of the ambiguous artifact identifier strings of the linked artifacts that were not
    * found.</li>
    * </ul>
    */

   LinkHandlerResult unlink(BranchId branchId, ArtifactId viewId, ArtifactId artifactId, TransactionId transactionId, LinkType linkType);

   /**
    * For the specified branch, updates the {@link BranchId} in links of all artifacts with a
    * {@link CoreAttributeTypes.WholeWordContent}.
    *
    * @param branchId the {@link BranchId} of the branch to update artifacts on.
    * @return a message describing the results of the operation.
    * @implNote The method is provided for the {@link FixEmbeddedLinksBlam}.
    */

   String updateLinks(BranchId branchId);

   public WordUpdateChange updateWordArtifacts(WordUpdateData data);

}
