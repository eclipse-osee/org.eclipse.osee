/*********************************************************************
 * Copyright (c) 2004, 2007, 2022 Boeing
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

import java.util.Optional;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateKeyGroups;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateKeyType;
import org.eclipse.osee.define.api.publishing.templatemanager.PublishingTemplateRequest;
import org.eclipse.osee.framework.core.publishing.PublishingTemplate;

/**
 * Implementations of this interface can offer to provide {@link PublishingTemplate}s for publishing requests. The
 * method {@link #getApplicabilityRating} is used to determine an integer score for the
 * {@link PublishingTemplateProvider} and a {@link PublishingRequest}. The Template Manager will request a publishing
 * template from each {@link PublishingTemplateProvider} in order of their applicability ratings, starting with the
 * highest value. The {@link #getApplicabilityRating} method must be implemented in context of the existing
 * {@link PublishingTemplateProvider} implementations to obtain the desired behavior.
 * <p>
 * The Template Manager locates and registers all classes in the {@link org.eclipse.osee.define.rest} OSGI bundle with
 * the annotation {@link IsPublishingTemplateProvider} that also implement this interface.
 *
 * @author Roberto E. Escobar
 * @author Loren K. Ashley
 */

interface PublishingTemplateProvider {

   /**
    * When the Publishing Template Request is by identifier and the identifier prefix matches the identifier prefix for
    * the {@link PublishingTemplateProvider} the highest applicability rating is returned.
    */

   static final int IDENTIFIER_PREFIX_MATCH = 1000;

   /**
    * This {@link PublishingTemplateProvider} may provide a publishing template when no other
    * {@link PublishingTemplateProvider} implementations have a higher applicability rating.
    */

   static final int DEFAULT_MATCH = 100;

   /**
    * This {@link PublishingTemplateProvider} does not provide publishing templates for the specified
    * {@link PublishingTemplateRequest} and the method {@link #getTemplate} should not be called.
    */

   static final int NO_MATCH = -1;

   /**
    * Deletes any cached Publishing Templates held by the {@link PublishingTemplateProvider} implementation.
    */

   void deleteCache();

   /**
    * Tries to find a {@link PublishingTemplateInternal} with the following steps:
    * <ul>
    * <li>First, tries to get a {@link PublishingTemplateInternal} with a name that matches
    * {@link PublishingTemplateRequest#getOption}.</li>
    * <li>Second, finds the best fit {@link PublishingTemplateInternal} for the {@link PublishingTemplateRequest}
    * options and the OSEE server "osee.publish.no.tags" flag.</li>
    * </ul>
    *
    * @param publishingTemplateRequest the {@link PublishingTemplateRequest} options.
    * @param isNoTags the OSEEE server "osee.publish.no.tags" flag.
    * @return the best match {@link PublsihingTemplateInternal}.
    */

   Optional<PublishingTemplateInternal> getTemplate(PublishingTemplateRequest publishingTemplateRequest, boolean isNoTags);

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
    * <dt>Primary Key" &quot;MATCH_CRITERIA&quot;</dt>
    * <dd>Secondary Key: the publishing template match criteria.</dd>
    * </dl>
    *
    * @param primaryKey the primary search key.
    * @param secondaryKey the secondary search key.
    * @return the first found {@link PublishingTemplate}.
    */

   Optional<PublishingTemplateInternal> getTemplate(PublishingTemplateKeyType primarykey, String secondaryKey);

   /**
    * Determines the applicability of the {@link PublishingTemplateProvider} implementation to the
    * {@link PublishingTemplateRequest}. A <code>null</code> or invalid ({@link PublishingTemplateRequest#isValid})
    * {@link PublishingTemplateRequest} will result in an applicability rating of
    * {@link PublishingTempateProvider#NO_MATCH}.
    *
    * @param publishingTemplateRequest the publishing template request parameters.
    * @return an applicability rating for the {@link PublishingTemplateProvider} implementation.
    */

   int getApplicabilityRating(PublishingTemplateRequest publishingTemplateRequest);

   /**
    * Gets a list of all the Publishing Template Key Groups for the Publishing Templates that maybe provided by this
    * {@link PublishingTemplateProvider}.
    *
    * @return a list of the Publishing Template Key Groups.
    */

   PublishingTemplateKeyGroups getPublishingTemplateKeyGroups();
}

/* EOF */
