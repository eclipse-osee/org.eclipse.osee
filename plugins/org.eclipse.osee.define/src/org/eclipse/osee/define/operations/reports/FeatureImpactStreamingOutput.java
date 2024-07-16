/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.define.operations.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.ws.rs.core.StreamingOutput;
import org.apache.commons.io.IOUtils;
import org.eclipse.osee.define.operations.api.DefineOperations;
import org.eclipse.osee.define.operations.api.publisher.publishing.PublishingOperations;
import org.eclipse.osee.define.rest.api.publisher.publishing.PublishingRequestData;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.applicability.ApplicabilityUseResultToken;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeType;
import org.eclipse.osee.framework.core.publishing.EnumRendererMap;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.core.util.LinkType;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Audrey E. Denk
 */
public final class FeatureImpactStreamingOutput implements StreamingOutput {

   private static final String rendererIdentifier = "org.eclipse.osee.define.operation.reports.featureimpact";
   private static final PublishingTemplateRequest publishingTemplateRequest =
      new PublishingTemplateRequest(FeatureImpactStreamingOutput.rendererIdentifier, null,
         PresentationType.PREVIEW.name(), null, FormatIndicator.WORD_ML);
   //@formatter:off
   private static final RendererMap commonPublishingRendererOptions =
      RendererMap.of
         (
            RendererOption.PUBLISH_IDENTIFIER, "Feature Impact Streaming Output",
            RendererOption.EXCLUDE_FOLDERS,    false,
            RendererOption.LINK_TYPE,          LinkType.INTERNAL_DOC_REFERENCE_USE_NAME,
            RendererOption.MAX_OUTLINE_DEPTH,  9,
            RendererOption.PUBLISHING_FORMAT,  FormatIndicator.WORD_ML
         );
   //@formatter:on

   private final Branch branchId;
   private final OrcsApi orcsApi;
   private final PublishingOperations publishingOperations;
   private final boolean publishUpdatedDocs;
   private final List<ArtifactTypeToken> artTypes;
   private final List<AttributeTypeToken> attrTypes;

   public FeatureImpactStreamingOutput(Branch branch, OrcsApi orcsApi, DefineOperations defineOperations, boolean publishUpdatedDocs, List<ArtifactTypeToken> artTypes, List<AttributeTypeToken> attrTypes) {
      this.branchId = branch;
      this.orcsApi = orcsApi;
      this.publishingOperations = defineOperations.getPublisherOperations().getPublishingOperations();
      this.publishUpdatedDocs = publishUpdatedDocs;
      this.artTypes = artTypes;
      this.attrTypes = attrTypes;
   }

   @Override
   public void write(OutputStream output) {

      final Branch branch =
         orcsApi.getQueryFactory().branchQuery().andId(branchId).getResults().getAtMostOneOrDefault(Branch.SENTINEL);
      final BranchId parentBranchIdentifier = branch.getParentBranch();

      ZipOutputStream zipOut = new ZipOutputStream(output);

      List<ChangeItem> changes = orcsApi.getBranchOps().compareBranch(branch);
      List<ArtifactToken> viewsForBranch =
         orcsApi.getQueryFactory().applicabilityQuery().getViewsForBranch(branch).stream().map(
            a -> a.getToken()).collect(Collectors.toList());

      TreeMap<ApplicabilityToken, TreeMap<ArtifactToken, String>> featureViewsImpacted =
         new TreeMap<ApplicabilityToken, TreeMap<ArtifactToken, String>>();
      TreeMap<String, Pair<ArtifactReadable, ArtifactToken>> artViewsToCompare =
         new TreeMap<String, Pair<ArtifactReadable, ArtifactToken>>();
      List<ChangeItem> collect = changes.stream().filter(a -> a.getChangeType().equals(
         ChangeType.Tuple) && a.getItemTypeId().equals(CoreTupleTypes.ViewApplicability)).collect(Collectors.toList());
      for (ChangeItem changeItem : collect) {
         // Format of Value in ChangeItem: Tuple2|200047, 96487380754354476
         String value = changeItem.getNetChange().getValue();
         String viewStr = value.substring(value.indexOf("|") + 1, value.indexOf(","));
         ArtifactToken view = viewsForBranch.stream().filter(a -> a.getIdString().equals(viewStr)).findFirst().get();
         String appId = value.substring(value.indexOf(",") + 1).trim();
         ApplicabilityToken app =
            ApplicabilityToken.valueOf(Long.parseLong(appId), orcsApi.getKeyValueOps().getByKey(Id.valueOf(appId)));
         if (featureViewsImpacted.get(app) == null) {
            featureViewsImpacted.put(app, new TreeMap<ArtifactToken, String>());
            featureViewsImpacted.get(app).put(view, changeItem.getNetChange().getModType().getName());
         } else {
            featureViewsImpacted.get(app).put(view, changeItem.getNetChange().getModType().getName());
         }
      }
      for (Entry<ApplicabilityToken, TreeMap<ArtifactToken, String>> entry : featureViewsImpacted.entrySet()) {

         for (ApplicabilityUseResultToken usage : orcsApi.getQueryFactory().applicabilityQuery().getApplicabilityUsage(
            branchId, entry.getKey().getName(), artTypes, attrTypes)) {
            for (ArtifactReadable impactedArtifact : usage.getArts()) {

               for (Entry<ArtifactToken, String> viewEntry : entry.getValue().entrySet()) {
                  artViewsToCompare.put(impactedArtifact.getName() + "_" + viewEntry.getKey().getName(),
                     new Pair<ArtifactReadable, ArtifactToken>(impactedArtifact, viewEntry.getKey()));
               }
            }
         }
      }

      try (Writer writer = new OutputStreamWriter(zipOut)) {

         if (!artViewsToCompare.isEmpty()) {
            zipOut.putNextEntry(new ZipEntry("SummaryOfChanges.csv"));
            PrintWriter summary = new PrintWriter(writer);
            summary.println("Applicability Modified, Views Impacted (change type)");
            summary.println();
            for (Entry<ApplicabilityToken, TreeMap<ArtifactToken, String>> entry2 : featureViewsImpacted.entrySet()) {
               summary.print(entry2.getKey().getName() + ",");
               for (Entry<ArtifactToken, String> pair : entry2.getValue().entrySet()) {
                  summary.print(pair.getKey() + " (" + pair.getValue().replace("New", "Added") + "); ");
               }
               summary.println();
            }
            summary.println();
            summary.println("Artifact Impacted, Artifact Name, Artifact Applicability");
            for (ArtifactReadable art : artViewsToCompare.entrySet().stream().map(
               a -> a.getValue().getFirst()).distinct().collect(Collectors.toList())) {
               summary.println(art.getIdString() + "," + art.getName() + "," + art.getApplicabilityToken().getName());
            }
            summary.flush();
            zipOut.closeEntry();
            if (publishUpdatedDocs) {
               for (Entry<String, Pair<ArtifactReadable, ArtifactToken>> viewEntry : artViewsToCompare.entrySet()) {
                  final var previewHeadArtifactIdentifier = viewEntry.getValue().getFirst();
                  final var previewViewArtifactIdentifier = ArtifactId.valueOf(viewEntry.getValue().getSecond());
                  final var workingDocBranchWithoutView = BranchId.valueOf(branch.getId());
                  final var baselineDocBranchWithoutView = BranchId.valueOf(parentBranchIdentifier.getId());
                  final var workingDocRendererOptions =
                     new EnumRendererMap(FeatureImpactStreamingOutput.commonPublishingRendererOptions);
                  workingDocRendererOptions.setRendererOption(RendererOption.BRANCH, workingDocBranchWithoutView);
                  workingDocRendererOptions.setRendererOption(RendererOption.VIEW, previewViewArtifactIdentifier);
                  final var baselineDocRendererOptions =
                     new EnumRendererMap(FeatureImpactStreamingOutput.commonPublishingRendererOptions);
                  baselineDocRendererOptions.setRendererOption(RendererOption.BRANCH, baselineDocBranchWithoutView);
                  baselineDocRendererOptions.setRendererOption(RendererOption.VIEW, previewViewArtifactIdentifier);
                  final var workingDocMsWordPreviewRequestData =
                     new PublishingRequestData(FeatureImpactStreamingOutput.publishingTemplateRequest,
                        workingDocRendererOptions, List.of(previewHeadArtifactIdentifier));
                  final var baselineDocMsWordPreviewRequestData =
                     new PublishingRequestData(FeatureImpactStreamingOutput.publishingTemplateRequest,
                        baselineDocRendererOptions, List.of(previewHeadArtifactIdentifier));
                  final var workingDocStream = this.publishingOperations.msWordPreview(
                     workingDocMsWordPreviewRequestData).getDataHandler().getInputStream();
                  final var baselineDocStream = this.publishingOperations.msWordPreview(
                     baselineDocMsWordPreviewRequestData).getDataHandler().getInputStream();
                  workingDocStream.mark(0);
                  boolean equal = IOUtils.contentEquals(workingDocStream, baselineDocStream);
                  baselineDocStream.close();
                  if (!equal) {
                     PrintWriter docWriter = new PrintWriter(writer);
                     workingDocStream.reset();

                     zipOut.putNextEntry(new ZipEntry(viewEntry.getKey() + ".xml"));
                     try {
                        InputStreamReader reader = new InputStreamReader(workingDocStream, "UTF-8");
                        BufferedReader br = new BufferedReader(reader);
                        String l;
                        while ((l = br.readLine()) != null) {
                           docWriter.println(l);
                        }
                        docWriter.flush();
                        br.close();
                        reader.close();
                        workingDocStream.close();
                     } catch (IOException ex) {
                        OseeCoreException.wrapAndThrow(ex);
                     }
                     zipOut.closeEntry();
                  } else {
                     workingDocStream.close();
                  }
               }
            }
         }
      } catch (IOException ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }
}
