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

package org.eclipse.osee.define.operations.publishing.datarights;

import java.util.Objects;
import org.eclipse.osee.define.operations.publishing.WordCoreUtilServer;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.DataRightsClassification;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * Immutable record to save the data rights classification and page orientation determined for an artifact.
 *
 * @author Angel Avila
 * @aurhor Loren K. Ashley
 */

class DataRightEntry {

   /**
    * Saves the identifier of the artifact the data right classification and page orientation are for.
    */

   private final ArtifactId artifactId;

   /**
    * Saves the data right classification determined for the artifact.
    */

   private final String classification;

   /**
    * Saves the page orientation determined for the artifact.
    */

   private final WordCoreUtil.pageType orientation;

   /**
    * Creates a new record from the specified <code>artifactReadable</code> and <code>overrideClassification</code>.
    * <p>
    * When the <code>overrideClassification</code> is specified as a non-<code>null</code> non-empty value, it will be
    * set as the artifact's data right classification and the artifact's page orientation will be set to the
    * {@link DataRightConfiguration#defaultPageOrientation}.
    * <p>
    * When the <code>artifactReadable</code> is <code>null</code> or {@link ArtifactReadable#SENTINEL}, the artifact's
    * data right classification is set to {@link DataRightConfiguration#defaultClassification} and the artifact's page
    * orientation will be set to the {@link DataRightConfiguration#defaultPageOrientation}.
    *
    * @param artifactId the identifier of the artifact.
    * @param artifactReadable the {@link ArtifactReadable} for the artifact.
    * @param overrideClassification an override for the artifact's data right classification. This parameter may be
    * <code>null</code> or an empty {@link String}.
    * @throws NullPointerException when either of the parameters <code>artifactId</code> or
    * <code>artifactReadable</code> are <code>null</code>.
    * @throws OseeCoreException when <code>artifactReadable</code> is not {@link ArtifactReadable#SENTINEL} and the
    * parameters <code>artifactId</code> and <code>artifactReadable</code> specify different artifacts.
    */

   public DataRightEntry(ArtifactId artifactId, ArtifactReadable artifactReadable, String overrideClassification) {

      this.artifactId =
         Objects.requireNonNull(artifactId, "DataRightEntry::new, the parameter \"artifactId\" cannot be null.");

      Objects.requireNonNull(artifactReadable,
         "DataRightEntry::new, the parameter \"artifactReadable\" cannot be null.");

      //@formatter:off
      if (    !ArtifactReadable.SENTINEL.equals( artifactReadable )
           && !artifactId.getId().equals( artifactReadable.getId() ) ) {
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( "DataRightEntry::new, the parameters \"artifactId\" and \"artifactReadable\" specifiy different artifacts." )
                             .indentInc()
                             .segment( "artifactId", artifactId )
                             .segment( "artifactReadable", artifactReadable, Id::getId )
                             .toString()
                   );
      }

      this.classification =
         DataRightsClassification.isValid( overrideClassification )
            ? overrideClassification
            : this.extractClassification( artifactReadable );

      this.orientation =
         DataRightsClassification.isValid( overrideClassification )
            ? DataRightConfiguration.defaultPageOrientation
            : WordCoreUtilServer.getPageOrientation( artifactReadable );
      //@formatter:on
   }

   /**
    * Gets the data rights classification from the <code>ArtifactReadable</code>'s
    * {@link DataRightConfiguration#classificationAttribute}. The {@link DataRightConfiguration#defaultClassification}
    * will be returned if unable to read the artifact's attribute or if the artifact is
    * {@link ArtifactReadable#SENTINEL}.
    *
    * @param artifactReadable the artifact to extract the data rights classification from.
    * @return the data right classification.
    */

   private String extractClassification(ArtifactReadable artifactReadable) {

      if (artifactReadable.isInvalid()) {
         return DataRightConfiguration.defaultClassification;
      }

      try {
         return artifactReadable.getSoleAttributeAsString(DataRightConfiguration.classificationAttribute,
            DataRightConfiguration.defaultClassification);
      } catch (Exception e) {
         return DataRightConfiguration.defaultClassification;
      }
   }

   /**
    * Gets the identifier of the artifact the associated data right classification and page orientation are for.
    *
    * @return the artifact identifier.
    */

   public ArtifactId getId() {
      return artifactId;
   }

   /**
    * Gets the data right classification for the artifact.
    *
    * @return the data right classification.
    */

   public String getClassification() {
      return classification;
   }

   /**
    * Gets the page orientation for the artifact.
    *
    * @return the page orientation.
    */

   public WordCoreUtil.pageType getOrientation() {
      return orientation;
   }

}

/* EOF */
