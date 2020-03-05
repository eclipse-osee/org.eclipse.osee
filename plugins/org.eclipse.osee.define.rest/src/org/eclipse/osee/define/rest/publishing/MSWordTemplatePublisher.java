/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.define.rest.publishing;

import static org.eclipse.osee.framework.core.enums.CoreArtifactTypes.Folder;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.NativeContent;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.PublishInline;
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WholeWordContent;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import org.eclipse.osee.define.api.PublishingArtifactError;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Branden W. Phillips
 */
public class MSWordTemplatePublisher extends AbstractMSWordTemplatePublisher {

   /**
    * This subclass of the AbstractMSWordTemplatePublisher is the publishing process for publishing artifacts similarly
    * to the old client "Publish With Specified Template" BLAM. This publishes via the hierarchy under the given
    * artifact, using the given template. This REST Operation is currently not in use anywhere.
    */
   public MSWordTemplatePublisher(PublishingOptions publishingOptions, Log logger, OrcsApi orcsApi) {
      super(publishingOptions, logger, orcsApi);
   }

   @Override
   public String publish(ArtifactId templateArtId, ArtifactId headArtId) {
      ArtifactReadable artifact = getArtifactHead(headArtId);
      String template = setUpTemplateWithOptions(templateArtId);

      if (!includeEmptyHeaders) {
         List<ArtifactReadable> artifacts = new LinkedList<>();
         artifacts.add(artifact);
         populateEmptyHeaders(artifacts);
      }

      if (artifact.isValid()) {
         if (Strings.isValid(template) && elementType.equals(ARTIFACT)) {
            StringBuilder wordMlOutput = applyContentToTemplate(artifact, template);
            return wordMlOutput.toString();
         } else {
            //TODO handle critical error for invalid template
            return null;
         }
      } else {
         //TODO handle critical error for invalid artifact head
         return null;
      }
   }

   @Override
   protected StringBuilder applyContentToTemplate(ArtifactReadable headArtifact, String templateContent) {
      getDataRightsOverride();

      StringBuilder strBuilder = new StringBuilder();
      WordMLProducer wordMl = new WordMLProducer(strBuilder);

      templateContent = setUpTemplateContent(wordMl, headArtifact, templateContent);

      int lastEndIndex = 0;
      Matcher matcher = headElementsPattern.matcher(templateContent);
      while (matcher.find()) {
         lastEndIndex = handleStartOfTemplate(wordMl, templateContent, matcher);
         processContent(headArtifact, wordMl);
      }

      handleEndOfTemplate(wordMl, templateContent, lastEndIndex);

      return strBuilder;
   }

   @Override
   protected void processContent(ArtifactReadable headArtifact, WordMLProducer wordMl) {
      setDataRightResponse(headArtifact);

      processArtifact(headArtifact, wordMl);
      if (publishingOptions.updateParagraphNumbers) {
         updateParagraphNumbers();
      }

      addErrorLogToWordMl(wordMl);
   }

   @Override
   protected void processArtifact(ArtifactReadable artifact, WordMLProducer wordMl) {
      if (!artifact.isAttributeTypeValid(WholeWordContent) && !artifact.isAttributeTypeValid(NativeContent)) {
         if (!processedArtifacts.contains(artifact)) {
            boolean ignoreArtifact =
               (publishingOptions.excludeFolders && artifact.isOfType(Folder)) || emptyFolders.contains(artifact);
            boolean publishInline = artifact.getSoleAttributeValue(PublishInline, false);
            boolean startedSection = false;

            if (!ignoreArtifact) {
               if (outlining && !publishInline) {
                  setArtifactOutlining(artifact, wordMl);
                  startedSection = true;
               }
               processMetadata(artifact, wordMl);
               processAttributes(artifact, wordMl);
            }
            if (recurseChildren) {
               for (ArtifactReadable childArtifact : artifact.getChildren()) {
                  processArtifact(childArtifact, wordMl);
               }
            }

            if (startedSection) {
               wordMl.endOutlineSubSection();
            }
            processedArtifacts.add(artifact);
         }
      } else {
         errorLog.add(new PublishingArtifactError(artifact.getId(), artifact.getName(), artifact.getArtifactType(),
            "Only artifacts of type Word Template Content are supported in this case"));
      }
   }

   @Override
   protected String getArtifactFooter(ArtifactReadable artifact) {
      String orientationStr = null;
      if (artifact.isAttributeTypeValid(CoreAttributeTypes.PageOrientation)) {
         orientationStr = artifact.getSoleAttributeValue(CoreAttributeTypes.PageOrientation, "Portrait");
      }
      PageOrientation orientation = PageOrientation.fromString(orientationStr);
      String footer = response.getContent(artifact, orientation);
      return footer;
   }
}
