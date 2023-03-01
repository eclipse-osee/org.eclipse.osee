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

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.util.RendererOption;
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
    * <table border="1">
    * <tr>
    * <th>Artifact has {@link CoreAttributeTypes#WordTemplateContent} Attribute Type</th>
    * <th>Artifact has {@link CoreAttributeTyeps#WholeWordContent} Attribute Type</th>
    * <th>Artifact has {@link CoreAttributeTyeps#NativeContent} Attribute Type</th>
    * <th>{@link CoreAttributeTypes#WordTemplateContent} Attribute Count</th>
    * <th>Presentation Type</th>
    * <th>Renderer Open Option</th>
    * <th>Applicability Rating</th>
    * </tr>
    * <tr>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>{@link PresentationType#GENERALIZED_EDIT}</td>
    * <td>N/A</td>
    * <td>{@link IRenderer#NO_MATCH}</td>
    * </tr>
    * <tr>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>{@link PresentationType#GENERALIZED_REQUESTED}</td>
    * <td>N/A</td>
    * <td>{@link IRenderer#NO_MATCH}</td>
    * </tr>
    * <tr>
    * <td>true</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>&gt; 0</td>
    * <td>{@link PresentationType#DEFAULT_OPEN}<br>
    * OR {@link PresentationType#PREVIEW}<br>
    * OR {@link PresentationType#PREVIEW_SERVER}</td>
    * <td>N/A</td>
    * <td>{@link IRenderer#PRESENTATION_SUBTYPE_MATCH}</td>
    * </tr>
    * <tr>
    * <td>true</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>&lt;= 0</td>
    * <td>{@link PresentationType#DEFAULT_OPEN}<br>
    * OR {@link PresentationType#PREVIEW}<br>
    * OR {@link PresentationType#PREVIEW_SERVER}</td>
    * <td>N/A</td>
    * <td>{@link IRenderer#SUBTYPE_TYPE_MATCH}</td>
    * </tr>
    * <tr>
    * <td>true</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>NOT {@link PresentationType#DEFAULT_OPEN}<br>
    * AND NOT {@link PresentationType#PREVIEW}<br>
    * AND NOT {@link PresentationType#PREVIEW_SERVER}</td>
    * <td>N/A</td>
    * <td>{@link IRenderer#PRESENTATION_SUBTYPE_MATCH}</td>
    * </tr>
    * <tr>
    * <td>false</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>{@link PresentationType#DIFF}</td>
    * <td>"MS Word"</td>
    * <td>{@link IRenderer#PRESENTATION_TYPE_OPTION_MATCH}</td>
    * </tr>
    * <tr>
    * <td>false</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>N/A</td>
    * <td>{@link PresentationType#DIFF}</td>
    * <td>NOT "MS Word"</td>
    * <td>{@link IRenderer#BASE_MATCH}</td>
    * </tr>
    * <tr>
    * <td>false</td>
    * <td>false</td>
    * <td>false</td>
    * <td>N/A</td>
    * <td>{@link PresentationType#PREVIEW}</td>
    * <td>"MS Word"</td>
    * <td>{@link IRenderer#PRESENTATION_TYPE_OPTION_MATCH}</td>
    * </tr>
    * <tr>
    * <td>false</td>
    * <td>false</td>
    * <td>false</td>
    * <td>N/A</td>
    * <td>{@link PresentationType#PREVIEW}</td>
    * <td>NOT "MS Word"</td>
    * <td>{@link IRenderer#BASE_MATCH}</td>
    * </tr>
    * </table>
    * otherwise, {@link IRenderer#NO_MATCH}.
    *
    * @param presentationType the type of presentation to be made.
    * @param artifact the {@link Artifact} to be presented.
    * @param rendererOptions a {@link Map} of {@link RendererOption} key value pairs.
    * @return the determined applicability rating.
    */

   public static int getApplicabilityRating(PresentationType presentationType, Artifact artifact, Map<RendererOption, Object> rendererOptions) {
      //@formatter:off
      if (presentationType.matches( PresentationType.GENERALIZED_EDIT, PresentationType.GENERAL_REQUESTED )) {
         return IRenderer.NO_MATCH;
      }

      if (!artifact.isAttributeTypeValid(CoreAttributeTypes.PrimaryAttribute) && artifact.isAttributeTypeValid(CoreAttributeTypes.WordTemplateContent)) {
         return
            presentationType.matches(PresentationType.DEFAULT_OPEN, PresentationType.PREVIEW, PresentationType.PREVIEW_SERVER)
               ? ( artifact.getAttributeCount(CoreAttributeTypes.WordTemplateContent) > 0 )
                    ? IRenderer.PRESENTATION_SUBTYPE_MATCH
                    : IRenderer.SUBTYPE_TYPE_MATCH
               : IRenderer.PRESENTATION_SUBTYPE_MATCH;
      }

      if(    presentationType.matches( PresentationType.DIFF )
          || (      presentationType.matches( PresentationType.PREVIEW )
                && !artifact.isAttributeTypeValid( CoreAttributeTypes.WholeWordContent )
                && !artifact.isAttributeTypeValid( CoreAttributeTypes.NativeContent ) ) ) {
         return
            MSWordTemplateRendererUtils.OPEN_IN_WORD.equals( rendererOptions.get( RendererOption.OPEN_OPTION ) )
               ? IRenderer.PRESENTATION_TYPE_OPTION_MATCH
               : IRenderer.BASE_MATCH;
      }

      return IRenderer.NO_MATCH;
      //@formatter:on
   }

   /**
    * Predicate to determine if all the {@link Artifact} objects on the {@link List} reside on the same OSEE branch.
    *
    * @param artifacts the {@link List} of {@link Artifact} objects to check.
    * @return <code>true</code> when all the artifacts are on the same branch; otherwise, <code>false</code>.
    */

   public static boolean artifactsOnSameBranch(List<Artifact> artifacts) {
      return artifacts.stream().map(Artifact::getBranchToken).distinct().count() == 1;
   }
}

/* EOF */
