/*******************************************************************************
 * Copyright (c) 2020 Boeing.
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
import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.WholeWordContent;
import java.util.regex.Matcher;
import org.eclipse.osee.define.api.PublishingErrorElement;
import org.eclipse.osee.define.api.PublishingOptions;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.util.WordMLProducer;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Branden W. Phillips
 */
public class MSWordTemplatePublisher extends AbstractMSWordTemplatePublisher {

   /**
    * Basic server publishing process. Using a head artifact to publish a hierarchy on the given template. Includes an
    * error log to be printed on the end.
    */
   public MSWordTemplatePublisher(PublishingOptions publishingOptions, Log logger, OrcsApi orcsApi) {
      super(publishingOptions, logger, orcsApi);
   }

   @Override
   public String publish(ArtifactId artifactId, ArtifactId templateArtId) {
      ArtifactReadable artifact = getArtifactHead(artifactId);
      String template = setUpTemplateWithOptions(templateArtId);

      if (!artifact.equals(ArtifactReadable.SENTINEL)) {
         if (!template.equals("") && elementType.equals(ARTIFACT)) {
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
      processArtifact(headArtifact, wordMl);

      addErrorLogToWordMl(wordMl);
   }

   @Override
   protected void processArtifact(ArtifactReadable artifact, WordMLProducer wordMl) {
      if (!artifact.isAttributeTypeValid(WholeWordContent) && !artifact.isAttributeTypeValid(NativeContent)) {
         if (!processedArtifacts.contains(artifact)) {
            boolean ignoreArtifact = (publishingOptions.excludeFolders && artifact.isOfType(Folder));
            boolean startedSection = false;

            if (!ignoreArtifact) {
               if (outlining) {
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
         errorElements.add(new PublishingErrorElement(artifact.getId(), artifact.getName(), artifact.getArtifactType(),
            "Only artifacts of type Word Template Content are supported in this case"));
      }
   }
}
