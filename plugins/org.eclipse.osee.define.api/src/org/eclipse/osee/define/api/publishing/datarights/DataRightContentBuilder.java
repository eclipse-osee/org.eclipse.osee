/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.define.api.publishing.datarights;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.PageOrientation;
import org.eclipse.osee.framework.core.util.ReportConstants;

/**
 * Encapsulates a {@link Map} of {@link DataRightAnchor} objects by {@link ArtifactId} for the sequence of artifacts
 * processed by the Data Rights manager for a REST API or {@link DataRightsOperations} call. The class provides the
 * functionality to build the footer Word ML content for each artifact from the encapsulated data.
 *
 * @author Angel Avila
 * @author Loren K. Ashley
 */

public class DataRightContentBuilder {

   /**
    * Saves an immutable map of {@link DataRightAnchor} objects associated with each {@link ArtifactId} for the
    * artifacts to be published.
    */

   private final Map<ArtifactId, DataRightAnchor> dataRightAnchors;

   /**
    * Creates a new {@link DataRightContentBuilder} from the {@link DataRightAnchor} {@link Map} returned from a Data
    * Rights manager REST API call or a {@link DataRightsOperations} call. The saved {@link DataRightAnchor} {@link Map}
    * is wrapped in an immutable map wrapper.
    *
    * @param dataRightResult result from the Data Rights manager REST API call or a {@link DataRightsOperations} call.
    */

   public DataRightContentBuilder(DataRightResult dataRightResult) {

      //@formatter:off
      Objects.requireNonNull
         ( dataRightResult,
           "DataRightContentBuilder::new, the parameter \"dataRightAnchorsResult\" cannot be null."
         );

      var map = dataRightResult.getDataRightAnchors();

      assert
           Objects.nonNull( map )
         : "DataRightContentBuilder::new, \"dataRightAnchorsResult.getDataRightAnchors()\" returned null.";

      this.dataRightAnchors = Collections.unmodifiableMap( map );
      //@formatter:on
   }

   /**
    * Gets the Word ML footer content for the artifact specified by the <code>artifactId</code>. Footer content
    * transitions are based upon the page orientation from the {@link CoreAttributeTypes.PageOrientation} of the
    * attributes in the sequence that were processed by the Data Rights manager. If the provided
    * <code>orientation</code> does match the {@link CoreAttributeTypes.PageOrientation} attribute of the artifact
    * specified by the <code>artifactId</code>, the resulting footer content will reflect the specified
    * <code>orientation</code> only when a footer content transition would have occurred. The footer content is built as
    * follows:
    * <dl>
    * <dt>Specified {@link ArtifactId} is <code>null</code> or {@link ArtifactId#SENTINEL}</dt>
    * <dd>Footer content is an empty string</dd>
    * <dt>Specified {@link ArtifactId} is not for a processed artifact:</dt>
    * <dd>Footer content is an empty string.</dd>
    * <dt>The artifact specified by {@link ArtifactId} was the first artifact or has the same data rights classification
    * as the previous artifact:</dt>
    * <dd>Footer content is the data rights Word ML for the artifact's classification along with the Word ML for the
    * specified page <code>orientation</code> wrapped in the {@link ReportConstants#NEW_PAGE_TEMPLATE}.</dd>
    * <dt>The artifact specified by {@link ArtifactId} has a different classification or page orientation than the next
    * artifact in the sequence:</dt>
    * <dd>Footer content is the Word ML for the specified page <code>orientation</code> wrapped in the
    * {@link ReportConstants#NEW_PAGE_TEMPLATE}.</dd>
    * </dl>
    *
    * @param artifactId the identifier of an artifact from the sequence that was processed by the Data Rights manger.
    * @param orientation the {@link PageOrientation}.
    * @return the Word ML footer content to be inserted into the publish after the artifact content.
    */

   public String getContent(ArtifactId artifactId, PageOrientation orientation) {

      if (Objects.isNull(artifactId) || ArtifactId.SENTINEL.equals(artifactId)) {

         /*
          * No artifact specified.
          */

         return "";
      }

      var dataRightAnchor = this.dataRightAnchors.get(artifactId);

      if (Objects.isNull(dataRightAnchor)) {

         /*
          * Artifact not processed, no footer content
          */

         return "";
      }

      if (dataRightAnchor.getNewFooter()) {

         /*
          * First artifact or this artifact has the same classification as the previous artifact
          */

         var dataRight = dataRightAnchor.getDataRight();

         //@formatter:off
         assert
              Objects.nonNull( dataRight)
            : "DataRightContentBuilder::getContent, \"DataRightAnchor\" has null \"DataRight\" and should never.";
         //@formatter:on

         var footer = dataRight.getContent();
         footer = String.format(ReportConstants.NEW_PAGE_TEMPLATE, footer + this.getPageAdds(orientation));

         return footer;
      }

      if (!dataRightAnchor.getIsContinuous()) {

         /*
          * Artifact classification or the page orientation has changed. Set page break since next footer differs.
          */

         var footer = String.format(ReportConstants.NEW_PAGE_TEMPLATE, this.getPageAdds(orientation));

         return footer;
      }

      return "";
   }

   /**
    * Gets the Word ML for the page orientation and page margins from {@link ReportConstants} definitions.
    *
    * @return the page orientation and page margins Word ML as a {@link String}.
    */

   private String getPageAdds(PageOrientation orientation) {
      //@formatter:off
      return
         String.format
            (
               ReportConstants.PAGE_ADDS,
               orientation.isLandscape()
                  ? ReportConstants.LANDSCAPE_ORIENT
                  : ReportConstants.PORTRAIT_ORIENT
            );
      //@formatter:on
   }

}

/* EOF */