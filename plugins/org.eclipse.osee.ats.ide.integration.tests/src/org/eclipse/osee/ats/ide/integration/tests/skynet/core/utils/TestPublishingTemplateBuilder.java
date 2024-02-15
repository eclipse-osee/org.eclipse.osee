/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.skynet.core.utils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.ide.util.ServiceUtil;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.publishing.FormatIndicator;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.httpRequests.PublishingRequestHandler;
import org.eclipse.osee.orcs.core.util.PublishingTemplate;

public class TestPublishingTemplateBuilder {

   private final Class<?> fileLocatorClass;

   public TestPublishingTemplateBuilder(Class<?> fileLocaterClass) {
      this.fileLocatorClass = fileLocaterClass;
   }

   /**
    * Creates the Publishing Template Artifacts on the Common Branch, deletes the Publishing Template Manager's cache,
    * and then loads the Publishing Templates from the Publishing Template Manager.
    */

   public Map<String, org.eclipse.osee.framework.core.publishing.PublishingTemplate> buildPublishingTemplates(
      Supplier<List<PublishingTemplate>> publishingTemplatesSupplier) {
      //@formatter:off
      var relationEndpoint = ServiceUtil.getOseeClient().getRelationEndpoint( CoreBranches.COMMON );

      var publishingTemplateSetter = new PublishingTemplateSetterImpl( relationEndpoint );

      var publishingTemplateList =
         PublishingTemplate
            .load
               (
                  publishingTemplatesSupplier,
                  publishingTemplateSetter::set,
                  this.fileLocatorClass,
                  false
               );

      PublishingRequestHandler.deletePublishingTemplateCache();

      final var publishingTemplateMap =
         publishingTemplateList
            .stream()
            .map( PublishingTemplate::getIdentifier )
            .filter( Objects::nonNull )
            .map
               (
                  ( publishingTemplateIdentifier ) -> new PublishingTemplateRequest
                                                             (
                                                                Conditions.requireNonNull( publishingTemplateIdentifier ),
                                                                FormatIndicator.WORD_ML
                                                             )
               )
            .filter( Objects::nonNull )
            .map( PublishingRequestHandler::getPublishingTemplate )
            .filter( org.eclipse.osee.framework.core.publishing.PublishingTemplate::isNotSentinel )
            .collect
               (
                  Collectors.toMap
                     (
                        org.eclipse.osee.framework.core.publishing.PublishingTemplate::getName,
                        Function.identity()
                     )
               );
      //@formatter:on

      return publishingTemplateMap;
   }

}
