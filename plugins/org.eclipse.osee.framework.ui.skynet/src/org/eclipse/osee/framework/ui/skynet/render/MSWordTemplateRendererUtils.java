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

package org.eclipse.osee.framework.ui.skynet.render;

import java.util.Map;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.publishing.RendererMap;
import org.eclipse.osee.framework.core.publishing.RendererOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Class of static utility methods for OSEE Renderers.
 *
 * @author Loren K. Ashley
 */

public class MSWordTemplateRendererUtils {

   /**
    * The value of the {@link RendererOption#OPEN_OPTION} for opening a document with MS Word.
    */

   private static final String OPEN_IN_WORD = "MS Word";

   /**
    * Construction is private to prevent instantiation of the class.
    */

   private MSWordTemplateRendererUtils() {
   }

   /**
    * Generates a Renderer applicability rating for an {@link Artifact} as follows:
    * <p>
    * The following conditions are Knock Outs resulting in a NO_MATCH:
    * <dl>
    * <dt>Presentation Types</dt>
    * <dd>
    * <ul>
    * <li>{@link PresentationType.GENERALIZED_EDIT}</li>
    * <li>{@link PresentationType.GENERAL_REQUESTED}</li>
    * </ul>
    * </dd>
    * <dt>Artifact Types</dt>
    * <dd>
    * <ul>
    * <li>{@link CoreArtifactTypes.Markdown}</li>
    * <li>{@link CoreArtifactTypes.UserGroup}</li>
    * <li>{@link CoreArtifactTypes.AtsArtifact}</li>
    * <li>{@link CoreArtifactTypes.AtsConfigArtifact}</li>
    * </ul>
    * </dd>
    * <dt>Attribute Valid</dt>
    * <dd>
    * <ul>
    * <li>{@link CoreAttributeTypes.PrimaryAttribute}</li>
    * </ul>
    * </dd>
    * </dl>
    * The following conditions select a match value:
    *
    * <pre>
    * +---------------------------+---+---+
    * | Presentation  | Attribute | W | R |
    * | Type(T=|,F=&) | Valid     | o | O |
    * +---+---+-------+-----------+ r | = |
    * | D | P | P | D | W | W | N | d | O |
    * | E | R | R | I | o | h | a | T | P |
    * | F | E | E | F | r | o | t | e | E |
    * | A | V | V | F | d | l | i | m | N |
    * | U | I | I |   | T | e | v | p | _ |
    * | L | E | E |   | e | W | e | l | I |
    * | T | W | W |   | m | o | C | a | N |
    * | _ |   | _ |   | p | r | o | t | _ |
    * | O |   | S |   | l | d | n | e | W |
    * | p |   | E |   | a | C | t | C | O |
    * | E |   | R |   | t | o | e | o | R |
    * | N |   | V |   | e | n | n | n | D |
    * |   |   | E |   | C | t | t | t |   |
    * |   |   | R |   | o | e |   | e |   |
    * |   |   |   |   | n | n |   | n |   |
    * |   |   |   |   | t | t |   | t |   |
    * |   |   |   |   | e |   |   | > |   |
    * |   |   |   |   | n |   |   | 0 |   |
    * |   |   |   |   | t |   |   |   |   |
    * +---+---+---+---+---+---+---+---+---+
    * |   |   |   | T | F |   |   |   | T | -> PRESENTATION_TYPE_OPTION_MATCH (55)
    * +---+---+---+---+---+---+---+---+---+
    * |   | T | T |   | F | F | F |   | T | -> PRESENTATION_TYPE_OPTION_MATCH (55)
    * +---+---+---+---+---+---+---+---+---+
    * | F | F | F |   | T |   |   |   |   | -> PRESENTATION_SUBTYPE_MATCH (50)
    * +---+---+---+---+---+---+---+---+---+
    * | T | T | T |   | T |   |   | T |   | -> PRESENTATION_SUBTYPE_MATCH (50)
    * +---+---+---+---+---+---+---+---+---+
    * | T | T | T |   | T |   |   | F |   | -> SUBTYPE_TYPE_MATCH (30)
    * +---+---+---+---+---+---+---+---+---+
    * |   | T | T |   | F | F | F |   | F | -> BASE_MATCH (5)
    * +---+---+---+---+---+---+---+---+---+
    * |   |   |   | T | F |   |   |   | F | -> BASE_MATCH (5)
    * +---+---+---+---+---+---+---+---+---+
    * </pre>
    *
    * otherwise, {@link IRenderer#NO_MATCH}.
    *
    * @param presentationType the type of presentation to be made.
    * @param artifact the {@link Artifact} to be presented.
    * @param rendererOptions a {@link Map} of {@link RendererOption} key value pairs.
    * @return the determined applicability rating.
    */

   public static int getApplicabilityRating(PresentationType presentationType, Artifact artifact,
      RendererMap rendererOptions) {
      //@formatter:off

      /*
       * Knock Outs
       */

      if(
             presentationType.matches
                (
                   PresentationType.GENERAL_REQUESTED,
                   PresentationType.GENERALIZED_EDIT
                )
          || artifact.isOfType
                (
                   CoreArtifactTypes.Markdown,
                   CoreArtifactTypes.UserGroup,
                   AtsArtifactTypes.AtsArtifact,
                   AtsArtifactTypes.AtsConfigArtifact
                )
          || artifact.isAttributeTypeValid( CoreAttributeTypes.PrimaryAttribute )
        ) {
         return IRenderer.NO_MATCH;
      }


      /*
       * PRESENTATION_TYPE_OPTION_MATCH
       */

      if(
             !artifact.isAttributeTypeValid( CoreAttributeTypes.WordTemplateContent )
          &&  MSWordTemplateRendererUtils.OPEN_IN_WORD.equals( rendererOptions.getRendererOptionValue( RendererOption.OPEN_OPTION ) )
        )
      {
         if(
                   presentationType.matches( PresentationType.DIFF )
            ) {
            return IRenderer.PRESENTATION_TYPE_OPTION_MATCH;
         }

         if(
                   presentationType.matches( PresentationType.PREVIEW, PresentationType.PREVIEW_SERVER )
               && !artifact.isAttributeTypeValid( CoreAttributeTypes.WholeWordContent )
               && !artifact.isAttributeTypeValid( CoreAttributeTypes.NativeContent )
           ) {
            return IRenderer.PRESENTATION_TYPE_OPTION_MATCH;
         }
      }

      /*
       * PRESENTATION_SUB_TYPE_MATCH
       */

      if(
          artifact.isAttributeTypeValid( CoreAttributeTypes.WordTemplateContent )
        )
      {
         if(
             !presentationType.matches( PresentationType.DEFAULT_OPEN, PresentationType.PREVIEW, PresentationType.PREVIEW_SERVER )
           ) {
            return IRenderer.PRESENTATION_SUBTYPE_MATCH;
         }

         if(
                 presentationType.matches( PresentationType.DEFAULT_OPEN, PresentationType.PREVIEW, PresentationType.PREVIEW_SERVER )
             &&  ( artifact.getAttributeCount( CoreAttributeTypes.WordTemplateContent ) > 0 )
           ) {
           return IRenderer.PRESENTATION_SUBTYPE_MATCH;
         }
      }

      /*
       * SUBTYPE_TYPE_MATCH
       */

      if(
              presentationType.matches( PresentationType.DEFAULT_OPEN, PresentationType.PREVIEW, PresentationType.PREVIEW_SERVER )
           && artifact.isAttributeTypeValid( CoreAttributeTypes.WordTemplateContent )
           && ( artifact.getAttributeCount( CoreAttributeTypes.WordTemplateContent ) == 0 )
        ) {
         return IRenderer.SUBTYPE_TYPE_MATCH;
      }

      /*
       * BASE_MATCH
       */

      if(
             !artifact.isAttributeTypeValid( CoreAttributeTypes.WordTemplateContent )
          && !MSWordTemplateRendererUtils.OPEN_IN_WORD.equals( rendererOptions.getRendererOptionValue( RendererOption.OPEN_OPTION ) )
        )
      {
         if(
                 presentationType.matches( PresentationType.PREVIEW, PresentationType.PREVIEW_SERVER )
             && !artifact.isAttributeTypeValid( CoreAttributeTypes.WholeWordContent )
             && !artifact.isAttributeTypeValid( CoreAttributeTypes.NativeContent )
           ) {
            return IRenderer.BASE_MATCH;
         }

         if(
             presentationType.matches( PresentationType.DIFF )
           ) {
            return IRenderer.BASE_MATCH;
         }
      }

      return IRenderer.NO_MATCH;
      //@formatter:on

   }

}

/* EOF */
