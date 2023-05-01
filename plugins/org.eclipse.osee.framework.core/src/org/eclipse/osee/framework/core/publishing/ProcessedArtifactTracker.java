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

package org.eclipse.osee.framework.core.publishing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Message;

/**
 * Tracks artifacts that have been processed for a publish.
 *
 * @author Loren K. Ashley
 */

public class ProcessedArtifactTracker {

   /**
    * Private inner class used to store tracking details for an artifact.
    */

   private class ArtifactRecord {

      /**
       * The {@link ArtifactId} of the tracked artifact. This member contains an {@link ArtifactId} implementation that
       * only contains the artifact identifier so that the tracking collection does not hang on to references to full
       * {@link ArtifactReadable} or {@link ArtifactToken} objects preventing their garbage collection.
       */

      ArtifactId artifactId;

      /**
       * A count of the attributes that have been rendered for the artifact. This count is used to determine if the
       * artifact was included in the publish or processed but excluded.
       */

      int attributeCount;

      /**
       * A flag to indicate if the artifact has been bookmarked. Only bookmarked artifacts can be linked to.
       */

      boolean bookmarked;

      /**
       * This flag is set when the word content for the artifact has been processed and cached for later publishing.
       */

      boolean cached;

      /**
       * Saves the GUID of the tracked artifact. Old style artifact links reference the linked artifact by it's GUID
       * instead of by its {@link ArtifactId}.
       */

      String guid;

      /**
       * This flag is set when the processing for the artifact has completed. It does not indicated that the artifact
       * was included in the publish.
       */

      boolean ok;

      /**
       * Creates a new {@link ArtifactRecord} for tracking an artifact.
       *
       * @param artifactId the {@link ArtifactId} of the artifact to be tracked.
       * @param guid the GUID of the artifact to be tracked.
       */

      ArtifactRecord(ArtifactId artifactId, String guid) {

         //@formatter:off
         assert
                 Objects.nonNull( artifactId )
              && !( artifactId instanceof ArtifactReadable )
              && !( artifactId instanceof ArtifactToken    )
            : "ArtifactRecord::new, parameter \"artifactId\" is null or a reference to a full artifact.";

         assert
              Objects.nonNull( guid )
            : "ArtifactRecord::new, parameter \"guid\" cannot be null.";
         //@formatter:on

         this.artifactId = artifactId;
         this.guid = guid;
         this.cached = false;
         this.ok = false;
         this.bookmarked = false;
         this.attributeCount = 0;
      }

      /**
       * Predicate to determine if the tracked artifact is bookmarked.
       *
       * @return <code>true</code> when the tracked artifact has been bookmarked; otherwise <code>false</code>.
       */

      boolean isBookmarked() {
         return this.bookmarked;
      }

      boolean isCached() {
         return this.cached;
      }

      /**
       * Predicate to determine if the tracked artifact has been included in the publish.
       *
       * @return <code>true</code> when the tracked artifact has been included in the publish; otherwise
       * <code>false</code>.
       */

      boolean isPublished() {
         return this.attributeCount > 0;
      }

      /**
       * Sets the &quot;is bookmarked&quot; flag.
       */

      void setBookmarked() {
         this.bookmarked = true;
      }

      void setCached() {
         this.cached = true;
      }
   }

   /**
    * Array of exception message titles for adding artifacts to the tracker.
    */

   //@formatter:off
   private static final String[] addErrorTitles =
   {
      "ProcessedArtifactTracker:add, artifact identifier and GUID have already been recorded.",
      "ProcessedArtifactTracker:add, artifact GUID has already been recorded.",
      "ProcessedArtifactTracker:add, artifact identifier has already been recorded."
   };
   //@formatter:on

   /**
    * The initial size for the collections used to track the artifacts.
    */

   private static final int initialMapSize = 2048;

   /**
    * {@link Map} of {@link ArtifactRecord} objects for tracked artifacts by their {@link ArtifactId}.
    */

   private final Map<ArtifactId, ArtifactRecord> artifactRecordByArtifactId;

   /**
    * {@link Map} of {@link ArtifactRecord} objects for tracked artifacts by their GUIDs.
    */

   private final Map<String, ArtifactRecord> artifactRecordByGuid;

   /**
    * Creates a new empty {@link ProcessedArtifactTracker}.
    */

   public ProcessedArtifactTracker() {
      this.artifactRecordByArtifactId = new HashMap<>(ProcessedArtifactTracker.initialMapSize);
      this.artifactRecordByGuid = new HashMap<>(ProcessedArtifactTracker.initialMapSize);
   }

   /**
    * Adds a new tracking record for the specified artifact with the following:
    * <dl>
    * <dt>{@link ArtifactRecord#bookmarked}</dt>
    * <dd>flag set to <code>false</code> indicating the artifact has not yet been bookmarked.</dd>
    * <dt>{@link ArtifactRecord#attributeCount}</dt>
    * <dd>set to zero indicating the artifact has not yet been included in the publish.</dd>
    * <dt>{@link ArtifactRecord#ok}</dt>
    * <dd>set to <code>false</code> indicating processing for the artifact has not yet completed.</dd>
    * </dl>
    *
    * @param artifact the {@link ArtifactReadable} to be tracked.
    * @throws NullPointerException when the parameter <code>artifact</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>artifact</code> is {@link ArtifactReadable#SENTINEL}.
    * @throws OseeCoreException when an artifact with the same {@link ArtifactId} or GUID has already been added to the
    * {@link ProcessedArtifactTracker}.
    */

   public void add(ArtifactReadable artifact) {

      Objects.requireNonNull(artifact, "ProcessedArtifactTracker::add, parameter \"artifact\" cannot be null.");

      if (artifact.getId().equals(9519889L)) {
         System.out.println("SNOOPY");
      }

      if (artifact.getId().equals(9519885L)) {
         System.out.println("SNOOPY-2");
      }

      if (ArtifactReadable.SENTINEL.equals(artifact)) {
         throw new IllegalArgumentException(
            "ProcessedArtifactTracker::add, parameter \"artifact\" cannot be SENTINEL.");
      }

      if (this.isCachedAndNotDone(artifact)) {
         return;
      }

      var artifactId = ArtifactId.create(artifact);

      var guid = artifact.getGuid();
      var artifactRecord = new ArtifactRecord(artifactId, guid);

      //@formatter:off
      var titleIndex =
           ( Objects.isNull( this.artifactRecordByArtifactId.put( artifactId, artifactRecord ) ) ? 1 : 0 )
         + ( Objects.isNull( this.artifactRecordByGuid.put( guid, artifactRecord ) )             ? 2 : 0 );

      if( titleIndex < 3 ) {
         //@formatter:off
         throw
            new OseeCoreException
                   (
                      new Message()
                             .title( ProcessedArtifactTracker.addErrorTitles[ titleIndex ] )
                             .indentInc()
                             .segment( "Artifact Id", artifactId )
                             .segment( "GUID", guid )
                             .toString()
                   );
         //@formatter:on
      }

   }

   /**
    * Adds each artifact in the {@link Collection} to the tracker as if {@link #add(ArtifactReadable)} had been called
    * for each artifact.
    *
    * @param artifacts the {@link Collection} of {@link ArtifactReadable} objects to add to the tracker.
    * @throws NullPointerException when the parameter <code>artifacts</code> is <code>null</code> or contains a
    * <code>null</code> entry.
    * @throws OseeCoreException when an artifact with the same {@link ArtifactId} or GUID has already been added to the
    * {@link ProcessedArtifactTracker}.
    */

   public void add(Collection<ArtifactReadable> artifacts) {

      Objects.requireNonNull(artifacts, "ProcessedArtifactTracker::add, parameter \"artifacts\" cannot be null.");

      artifacts.forEach(this::add);
   }

   /**
    * Removes the tracking data for all artifacts.
    *
    * @implNote This method is used by the client side for reentrant diff processing.
    */

   public void clear() {
      this.artifactRecordByArtifactId.clear();
      this.artifactRecordByGuid.clear();
   }

   /**
    * Predicate to determine if an artifact with the specified {@link ArtifactId} has been tracked.
    *
    * @param artifactId the {@link ArtifactId} of the artifact to check for.
    * @return <code>true</code>, when the artifact has a tracking entry; otherwise, <code>false</code>.
    * @throws NullPointerException when the parameter <code>artifactId</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>artifactId</code> is {@link ArtifactId#SENTINEL}.
    */

   public boolean contains(ArtifactId artifactId) {

      Objects.requireNonNull(artifactId,
         "ProcessedArtifactTracker::contains, parameter \"artifactId\" cannot be null.");

      if (ArtifactId.SENTINEL.equals(artifactId)) {
         throw new IllegalArgumentException(
            "ProcessedArtifactTracker::contains, parameter \"artifactId\" cannot be SENTINEL.");
      }

      return this.artifactRecordByArtifactId.containsKey(artifactId);
   }

   /**
    * Predicate to determine if an artifact with the specified GUID has been tracked.
    *
    * @param artifactGuid the GUID of the artifact to check for.
    * @return <code>true</code>, when the artifact has a tracking entry; otherwise, <code>false</code>.
    * @throws NullPointerException when the parameter <code>artifactGuid</code> is <code>null</code>.
    */

   public boolean contains(String artifactGuid) {

      Objects.requireNonNull(artifactGuid,
         "ProcessedArtifactTracker::contains, parameter \"artifactGuid\" cannot be null.");

      return this.artifactRecordByGuid.containsKey(artifactGuid);
   }

   /**
    * Predicate to determine if an artifact with the specified <code>linkReference</code> has been tracked. A link
    * reference is either the string representation of an {@link ArtifactId} or an artifact GUID.
    *
    * @param linkReference the artifact to check for.
    * @return <code>true</code>, when the artifact has a tracking entry; otherwise, <code>false</code>.
    * @throws NullPointerException when the parameter <code>linkReference</code> is <code>null</code>.
    */

   public boolean containsByLinkReference(String linkReference) {

      Objects.requireNonNull(linkReference,
         "ProcessedArtifactTracker::containsByLinkReference, parameter \"linkReference\" cannot be null.");

      //@formatter:off
      return
         WordCoreUtil.isLinkReferenceAnArtifactId( linkReference )
            ? this.artifactRecordByArtifactId.containsKey( ArtifactId.valueOf( linkReference ) )
            : this.artifactRecordByGuid.containsKey( linkReference );
      //@formatter:on
   }

   /**
    * Gets the GUID of the tracked artifact specified by {@link ArtifactId}.
    *
    * @param artifactId the tracked artifact to get the GUID of.
    * @return if the artifact specified by <code>artifactId</code> has been tracked, an {@link Optional} containing the
    * artifact's GUID; otherwise, an empty {@link Optional}.
    * @throws NullPointerException when the parameter <code>artifactId</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>artifactId</code> is {@link ArtifactId#SENTINEL}.
    */

   public Optional<String> get(ArtifactId artifactId) {

      Objects.requireNonNull(artifactId, "ProcessedArtifactTracker::get, parameter \"artifactId\" cannot be null.");

      if (ArtifactId.SENTINEL.equals(artifactId)) {
         throw new IllegalArgumentException(
            "ProcessedArtifactTracker::get, parameter \"artifactId\" cannot be SENTINEL.");
      }

      var artifactRecord = this.artifactRecordByArtifactId.get(artifactId);

      return Objects.nonNull(artifactRecord) ? Optional.ofNullable(artifactRecord.guid) : Optional.empty();
   }

   /**
    * Gets the {@link ArtifactId} of the tracked artifact specified by GUID.
    *
    * @param artifactId the tracked artifact to get the {@link ArtifactId} of.
    * @return if the artifact specified by GUID has been tracked, an {@link Optional} containing the artifact's
    * {@link ArtifactId}; otherwise, an empty {@link Optional}.
    * @throws NullPointerException when the parameter <code>artifactGuid</code> is <code>null</code>.
    */

   public Optional<ArtifactId> get(String artifactGuid) {

      Objects.requireNonNull(artifactGuid, "ProcessedArtifactTracker::get, parameter \"artifactGuid\" cannot be null.");

      var artifactRecord = this.artifactRecordByGuid.get(artifactGuid);

      return Objects.nonNull(artifactRecord) ? Optional.ofNullable(artifactRecord.artifactId) : Optional.empty();
   }

   /**
    * Gets the {@link ArtifactRecord} associated with the <code>linkReference</code>. A link reference is either the
    * {@link String} representation of an {@link ArtifactId} or an artifact GUID.
    *
    * @param linkReference the link reference of the {@link ArtifactRecord} to get.
    * @return when the artifact specified by <code>linkRefernce</code> has a tracking entry, an {@link Optional}
    * containing the {@link ArtifactRecord} for the specified artifact; otherwise, an empty {@link Optional}.
    * @throws NullPointerException when the parameter <code>linkReference</code> is <code>null</code>.
    */

   private Optional<ArtifactRecord> getByLinkReference(String linkReference) {

      Objects.requireNonNull(linkReference,
         "ProcessedArtifactTracker::setBookmaked, parameter \"linkReference\" cannot be null.");

      //@formatter:off
      var artifactRecord =
         WordCoreUtil.isLinkReferenceAnArtifactId( linkReference )
            ? this.artifactRecordByArtifactId.get( ArtifactId.valueOf( linkReference ) )
            : this.artifactRecordByGuid.get( linkReference );
      //@formatter:on

      return Optional.ofNullable(artifactRecord);
   }

   /**
    * Increments the published attribute count of the specified artifact.
    *
    * @param artifactId the artifact to increment the published artifact count for.
    * @throws NullPointerException when the parameter <code>artifactId</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>artifactId</code> is {@link ArtifactId#SENTINEL}.
    * @throws IllegalStateException when the artifact specified by <code>artifactId</code> is not tracked.
    */

   public void incrementAttributeCount(ArtifactId artifactId) {

      Objects.requireNonNull(artifactId,
         "ProcessedArtifactTracker::contains, parameter \"artifactId\" cannot be null.");

      if (ArtifactId.SENTINEL.equals(artifactId)) {
         throw new IllegalArgumentException(
            "ProcessedArtifactTracker::contains, parameter \"artifactId\" cannot be SENTINEL.");
      }

      var artifactRecord = this.artifactRecordByArtifactId.get(artifactId);

      if (Objects.isNull(artifactRecord)) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      new Message()
                             .title( "ProcessedArtifactTracker::incrementAttributeCount, attempt to get artifact record for an untracked artifact." )
                             .indentInc()
                             .segment( "Artifact Id", artifactId.getIdString() )
                             .toString()
                   );
      }
      artifactRecord.attributeCount++;
    }

   /**
    * Predicate to determine if a tracked artifact has been bookmarked.
    *
    * @param linkReference either the {@link ArtifactId#getIdString()} or the GUID of the artifact to test.
    * @return <code>true</code>, when the tracked artifact has been bookmarked; otherwise, <code>false</code>.
    * @throws NullPointerException when the parameter <code>linkReference</code> is <code>null</code>.
    */

   public boolean isBookmarked(String linkReference) {

      //@formatter:off
      return
         this.getByLinkReference( linkReference )
            .map( ArtifactRecord::isBookmarked )
            .orElse( false );
      //@formatter:on
   }

   public boolean isCachedAndNotDone(ArtifactId artifactId) {
      ArtifactRecord artifactRecord;
      //@formatter:off
      return
            Objects.nonNull(artifactId)
         && !ArtifactId.SENTINEL.equals(artifactId)
         && Objects.nonNull( artifactRecord = this.artifactRecordByArtifactId.get(artifactId) )
               ? artifactRecord.cached && !artifactRecord.ok
               : false;
      //@formatter:on
   }

   /**
    * Predicate to determine if a tracked artifact has not been bookmarked.
    *
    * @param linkReference either the {@link ArtifactId#getIdString()} or the GUID of the artifact to test.
    * @return <code>true</code>, when the tracked artifact has not been bookmarked; otherwise, <code>false</code>.
    * @throws NullPointerException when the parameter <code>linkReference</code> is <code>null</code>.
    */

   public boolean isNotBookmarked(Map.Entry<String, ArtifactReadable> hyperlinkEntry) {
      return !this.isBookmarked(hyperlinkEntry.getKey());
   }

   public boolean isOk(ArtifactId artifactId) {
      ArtifactRecord artifactRecord;
      //@formatter:off
      return
            Objects.nonNull(artifactId)
         && !ArtifactId.SENTINEL.equals(artifactId)
         && Objects.nonNull( artifactRecord = this.artifactRecordByArtifactId.get(artifactId) )
               ? artifactRecord.ok
               : false;
      //@formatter:on
   }

   /**
    * Predicate to determine if a tracked artifact has been published.
    *
    * @param linkReference either the {@link ArtifactId#getIdString()} or the GUID of the artifact to test.
    * @return <code>true</code>, when the tracked artifact has been published; otherwise, <code>false</code>.
    * @throws NullPointerException when the parameter <code>linkReference</code> is <code>null</code>.
    */

   public boolean isPublished(String linkReference) {

      //@formatter:off
      return
         this.getByLinkReference( linkReference )
            .map( ArtifactRecord::isPublished )
            .orElse( false );
      //@formatter:on
   }

   /**
    * Sets the is bookmarked flag for the specified artifact.
    *
    * @param linkReference either the {@link ArtifactId#getIdString()} or the GUID of the artifact to set.
    * @throws NullPointerException when the parameter <code>linkReference</code> is <code>null</code>.
    */

   public void setBookmarked(String linkReference) {
      //@formatter:off
      this.getByLinkReference(linkReference)
         .ifPresentOrElse
            (
               ArtifactRecord::setBookmarked,
               () ->
               {
                  throw
                     new IllegalStateException
                            (
                               new Message()
                                      .title( "ProcessedArtifactTracker::setBookemarked, attepmt to get artifact record by link reference for an untracked artifact." )
                                      .indentInc()
                                      .segment( "Link Reference", linkReference )
                                      .toString()
                            );
               }
            );
      //@formatter:on
   }

   public void setCached(ArtifactId artifactId) {

      Objects.requireNonNull(artifactId,
         "ProcessedArtifactTracker::setChached, parameter \"artifactId\" cannot be null.");

      if (ArtifactId.SENTINEL.equals(artifactId)) {
         throw new IllegalArgumentException(
            "ProcessedArtifactTracker::setCached, parameter \"artifactId\" cannot be SENTINEL.");
      }

      var artifactRecord = this.artifactRecordByArtifactId.get(artifactId);

      if (Objects.isNull(artifactRecord)) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      new Message()
                             .title( "ProcessedArtifactTracker::setCached, attempt to get artifact record for an untracked artifact.")
                             .indentInc()
                             .segment( "Artifact Id", artifactId )
                             .toString()
                   );
         //@formatter:on
      }

      artifactRecord.setCached();
   }

   /**
    * Sets the processing complete flag for the specified artifact.
    *
    * @param artifactId the {@link ArtifactId} of the artifact to set.
    * @throws NullPointerException when the parameter <code>artifactId</code> is <code>null</code>.
    * @throws IllegalArgumentException when the parameter <code>artifactId</code> is {@link ArtifactId#SENTINEL}.
    * @throws IllegalStateException when the artifact specified by <code>artifactId</code> has not been tracked.
    */

   public void setOk(ArtifactId artifactId) {

      Objects.requireNonNull(artifactId, "ProcessedArtifactTracker::setOk, parameter \"artifactId\" cannot be null.");

      if (ArtifactId.SENTINEL.equals(artifactId)) {
         throw new IllegalArgumentException(
            "ProcessedArtifactTracker::setOk, parameter \"artifactId\" cannot be SENTINEL.");
      }

      var artifactRecord = this.artifactRecordByArtifactId.get(artifactId);

      if (Objects.isNull(artifactRecord)) {
         //@formatter:off
         throw
            new IllegalStateException
                   (
                      new Message()
                             .title( "ProcessedArtifactTracker::add, attempt to set OK status for untracked artifact." )
                             .indentInc()
                             .segment( "ArtifactId", artifactId )
                             .toString()
                   );
         //@formatter:on
      }

      artifactRecord.ok = true;
   }

   /**
    * Sets the processing complete flag for each artifact in the {@link Collection} as if {@link #setOk(ArtifactId)} had
    * been called for each artifact.
    *
    * @param artifactIds the {@link Collection} of {@link ArtifactId} for the artifacts to set the processing complete
    * flag for.
    * @throws NullPointerException when the parameter <code>artifactIds</code> is <code>null</code> or contains a
    * <code>null</code> entry.
    * @throws IllegalStateException when a specified artifact has not been tracked.
    */

   public void setOk(Collection<? extends ArtifactId> artifactIds) {

      Objects.requireNonNull(artifactIds, "ProcessedArtifactTracker::setOk, parameter \"artifactIds\" cannot be null.");

      artifactIds.forEach(this::setOk);
   }

   public int size() {
      return this.artifactRecordByArtifactId.size();
   }
}

/* EOF */