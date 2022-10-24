/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.define.operations.publishing.templatemanager;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Function;

/**
 * Enumeration of the primary map keys used by the {@link AbstractPublishingTemplateCache} for indexing publishing
 * templates.
 *
 * @author Loren K. Ashley
 */

public enum PublishingTemplateCacheKey {

   /**
    * Publishing templates are hashed by identifiers under this primary key.
    */

   IDENTIFIER(PublishingTemplateInternal::getIdentifierKeyExtractor),

   /**
    * Publishing templates are hashed by safe name under this primary key.
    */

   SAFE_NAME(PublishingTemplateInternal::getSafeNameKeyExtractor),

   /**
    * Publishing templates are hashed by their template match criteria under this primary key.
    */

   MATCH_CRITERIA(PublishingTemplateInternal::getTemplateMatchCriteriaKeyExtractor),

   /**
    * Publishing templates are hashed by name under this primary key.
    */

   NAME(PublishingTemplateInternal::getNameKeyExtractor);

   /**
    * Saves a {@link Function} implementation for extracting the secondary key from a {@link PublishingTemplateInternal}
    * object for the primary key type.
    */

   private Function<PublishingTemplateInternal, Iterator<String>> keyExtractor;

   /**
    * Enumeration member constructor.
    *
    * @param keyExtractor the {@link Function} for extracting the secondary keys that can be used under the primary key
    * represented by the enumeration member.
    */

   private PublishingTemplateCacheKey(Function<PublishingTemplateInternal, Iterator<String>> keyExtractor) {
      this.keyExtractor = Objects.requireNonNull(keyExtractor);
   }

   /**
    * Extracts a secondary key from a {@link PublishingTemplateInternal} object that can be used under the primary key
    * represented by the enumeration member.
    *
    * @param publishingTemplateInternal the publishing template to extract a secondary key from.
    * @return the secondary key.
    */

   Iterator<String> extractKey(PublishingTemplateInternal publishingTemplateInternal) {
      return this.keyExtractor.apply(publishingTemplateInternal);
   }
}

/* EOF */
