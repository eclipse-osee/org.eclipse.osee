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

package org.eclipse.osee.orcs.core.util;

import java.util.Objects;
import org.eclipse.osee.define.rest.api.publisher.templatemanager.PublishingTemplateRequest;

/**
 * Defines a Publishing Template Match Criterion for the creation of a Publishing Template.
 *
 * @author Loren K. Ashley
 */

public class PublishingTemplateMatchCriterion {

   /**
    * Saves the "option" portion of the template match criteria string. This member may be <code>null</code>.
    */

   private final String option;

   /**
    * Saves the "presentation type" portion of the template match criteria string.
    */

   private final String presentationType;

   /**
    * Saves the Renderer identification string.
    */

   private final String rendererIdentifier;

   /**
    * Creates a new {@link PublishingTemplateMatchCriterion} with the specified parameters.
    *
    * @param rendererIdentifier the identifier as returned by {@link IRenderer#getIdentifier} of the renderer.
    * @param presentationType the type of presentation the match criterion is for.
    * @throws NullPointerException when either of the parameters <code>rendererIdentifier</code> or
    * <code>presentationType</code> are <code>null</code>.
    * @throws IllegalArgumentException when either of the parameters <code>rendererIdentifier</code> or
    * <code>presentationType</code> are blank.
    */

   public PublishingTemplateMatchCriterion(String rendererIdentifier, String presentationType) {
      this(rendererIdentifier, presentationType, null);
   }

   /**
    * Creates a new {@link PublishingTemplateMatchCriterion} with the specified parameters.
    *
    * @param rendererIdentifier the identifier as returned by {@link IRenderer#getIdentifier} of the renderer.
    * @param presentationType the type of presentation the match criterion is for.
    * @param option the optional trailing portion of the match criterion.
    * @throws NullPointerException when either of the parameters <code>rendererIdentifier</code> or
    * <code>presentationType</code> are <code>null</code>.
    * @throws IllegalArgumentException when:
    * <ul>
    * <li>the parameter <code>rendererIdentifier</code> is blank, or</li>
    * <li>the parameter <code>presentationType</code> is blank, or</li>
    * <li>the parameter <code>option</code> is specified and blank.</li>
    * </ul>
    */

   public PublishingTemplateMatchCriterion(String rendererIdentifier, String presentationType, String option) {
      Objects.requireNonNull(rendererIdentifier,
         "SetupPublishing.PublishingTemplateMatchCriterion::new, parameter \"rendererIdentifier\" cannot be null.");
      if (rendererIdentifier.isBlank()) {
         throw new IllegalArgumentException(
            "SetupPublishing.PublishingTemplateMatchCriterion::new, parameter \"rendererIdentifier\" cannot be blank.");
      }
      Objects.requireNonNull(presentationType,
         "SetupPublishing.PublishingTemplateMatchCriterion::new, parameter \"presentationType\" cannot be null.");
      if (presentationType.isBlank()) {
         throw new IllegalArgumentException(
            "SetupPublishing.PublishingTemplateMatchCriterion::new, parameter \"presentationType\" cannot be blank.");
      }
      if (Objects.nonNull(option) && option.isBlank()) {
         throw new IllegalArgumentException(
            "SetupPublishing.PublishingTemplateMatchCriterion::new, parameter \"option\" cannot be blank.");
      }
      this.rendererIdentifier = rendererIdentifier;
      this.presentationType = presentationType;
      this.option = option;
   }

   /**
    * Creates a {@link PublishingTemplateRequest} that can be used to obtain the Publishing Template from the Publishing
    * Template Manager using this set of Match Criteria.
    *
    * @return a {@link PublishingTemplateRequest} object.
    */

   PublishingTemplateRequest getPublishingTemplateRequest() {
      //@formatter:off
      var publishingTemplateRequest =
         new PublishingTemplateRequest
                (
                   this.rendererIdentifier,
                   "",
                   this.presentationType,
                   this.option
                );
      //@formatter:on
      return publishingTemplateRequest;
   }

   /**
    * Gets the Match Criteria as a {@link String} suitable for setting in the Match Criteria Attribute of a Publishing
    * Template Artifact.
    *
    * @return the match criteria as a {@link String}.
    */

   public String getTemplateMatchCriteria() {
      //@formatter:off
         var matchCriteria =
            new StringBuilder
                   (
                        this.rendererIdentifier.length()
                      + this.presentationType.length()
                      + ( Objects.nonNull( this.option ) ? this.option.length() : 0 )
                      + 8
                   )
               .append( this.rendererIdentifier )
               .append( " " )
               .append( this.presentationType );

         if( Objects.nonNull( this.option ) ) {
            matchCriteria
               .append( " " )
               .append( this.option );
         }
         //@formatter:on

      return matchCriteria.toString();
   }

}

/* EOF */
