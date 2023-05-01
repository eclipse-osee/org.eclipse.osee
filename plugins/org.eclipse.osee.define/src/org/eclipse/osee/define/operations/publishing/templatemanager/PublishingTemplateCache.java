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

import java.util.List;
import java.util.Optional;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateKeyGroups;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateKeyType;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;

/**
 * A common Publishing Template Cache interface to be used by caching {@link PublishingTemplateProvider}
 * implementations. The abstract class {@link AbstractPublishingTemplateCache} provides a skeletal implementation of the
 * interface to minimize the effort required to implement this interface.
 *
 * @author Loren K. Ashley
 */

interface PublishingTemplateCache {

   /**
    * Deletes the contents of the cache. After this method is invoked calls to any of the other interface methods will
    * cause the cache to be reloaded.
    */

   void deleteCache();

   /**
    * The match criteria on the <code>matchCriteria</code> {@link List} are sequentially used to search the cache for a
    * a {@link PublishingTemplateInternal} with a matching match criteria.
    *
    * @param matchCriteria a {@link List} of match criteria {@link String}s.
    * @return when found, an {@link Optional} containing the first found {@link PublishingTemplateInternal}; otherwise,
    * an empty {@link Optional}.
    */

   Optional<PublishingTemplateInternal> findFirstTemplateByMatchCriteria(List<String> matchCriteria);

   /**
    * Gets a publishing template by a primary and secondary key pair. In the case where more than one publishing
    * template has the same key pair, the first publishing template found is returned. The following key pair types are
    * supported:
    * <dl>
    * <dt>Primary Key: &quot;NAME&quot;</dt>
    * <dd>Secondary Key: the publishing template name.</dd>
    * <dt>Primary Key: &quot;SAFE_NAME&quot;</dt>
    * <dd>Secondary Key: the publishing template safe name.</dd>
    * <dt>Primary Key: &quot;IDENTIFIER&quot;</dt>
    * <dd>Secondary Key: the publishing template identifier.</dd>
    * </dl>
    *
    * @param primaryKey the primary search key.
    * @param secondaryKey the secondary search key.
    * @return the first found {@link PublishingTemplate}.
    */

   Optional<PublishingTemplateInternal> findFirstTemplate(PublishingTemplateKeyType primaryKey, String secondaryKey);

   /**
    * Gets an unordered unmodifiable {@link List} of the {@link PublishingTemplateKeyGroups} for each publishing
    * template held by the cache.
    *
    * @return an unmodifiable {@link List} of the {@link PublishingTemplateKeyGroups} held by the cache.
    */

   PublishingTemplateKeyGroups getPublishingTemplateKeyGroups();
}

/* EOF */
