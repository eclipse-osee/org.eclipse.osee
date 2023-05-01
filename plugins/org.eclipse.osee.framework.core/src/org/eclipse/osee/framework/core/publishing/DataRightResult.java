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

package org.eclipse.osee.framework.core.publishing;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * The data structure used to provide the data rights for a sequence of artifacts.
 *
 * @author Loren K. Ashley
 */

public class DataRightResult implements ToMessage {

   /**
    * An unordered-list of the data rights and sequence flags for the artifacts.
    *
    * @implNote A list is used instead of a map so that a custom JSON serializing/deserializing class does not have to
    * be implemented. Each {@link DataRightAnchorSkinny} contains the {@link ArtifactId} of the artifact it holds data
    * for. A {@link DataRightAnchorSkinny} contains the data right classification name instead of a {@link DataRight}
    * object with the Word ML footer. A map of {@link DataRightAnchor} objects by {@link ArtifactId} can be created from
    * the information in this list and the list of unique {@link DataRight} objects in the member {@link #dataRights}.
    */

   private List<DataRightAnchorSkinny> dataRightAnchorSkinnys;

   /**
    * A list of the unique data rights referenced by the {@link DataRightAnchorSkinny} objects by classification name.
    *
    * @implNote A list is used instead of a map so that a custom JSON serializing/deserializing class does not have to
    * be implemented. Each {@link DataRight} object contains a data right classification name and the associated Word ML
    * footer. A map of {@link DataRight} objects by data right classification name can be created from this list. The
    * Word ML footers can be large so they are consolidated into just the unique instances so they appear only once in
    * the JSON stream.
    */

   private List<DataRight> dataRights;

   /**
    * Creates a new empty {@link DataRightResults} for JSON deserialization.
    */

   public DataRightResult() {
      this.dataRightAnchorSkinnys = null;
      this.dataRights = null;
   }

   /**
    * Creates a new {@link DataRightResult} with data for JSON serialization.
    *
    * @param dataRightAnchorStream a {@link Stream} of the {@link DataRightAnchor} objects to be returned from the REST
    * API call.
    * @throws NullPointerException when the parameter <code>dataRightAnchorStream</code> is <code>null</code>.
    * @implNote There is a {@link DataRightAnchor} for each artifact in the sequence of artifacts that data rights were
    * requested for. Each artifact with the same data rights will have a {@link DataRightAnchor} with a string reference
    * to the full Word ML footer for that artifact. To prevent a full copy of the Word ML footers for each
    * {@link DataRIghtAnchor} object from getting serialized, the following is done:
    * <ul>
    * <li>the {@link DataRightAnchor} objects are converted to {@link DataRightAnchorSkinny} objects that contain the
    * artifact's data right classification name instead of the full {@link DataRight} with the classification name and
    * Word ML footer, and</li>
    * <li>a list of the unique {@link DataRight} objects is collected.</li>
    * </ul>
    * This allows each unique Word ML footer to be serialized only once while retaining the necessary data to create a
    * map of the full {@link DataRightAnchor} objects by {@link ArtifactId} after deserialization. It also allows all of
    * the {@link DataRightAnchor} objects to reference just the unique {@link DataRight} objects instead of each
    * {@link DataRightAnchor} having it's own instance of a {@link DataRight} object.
    */

   @JsonIgnore
   public DataRightResult(Stream<DataRightAnchor> dataRightAnchorStream) {

      //@formatter:off
      Objects.requireNonNull(dataRightAnchorStream, "DataRightResult::new, parameter \"dataRightAnchorStream\" cannot be null." );

      var dataRightsMap = new HashMap<String, DataRight>();

      this.dataRightAnchorSkinnys =
         dataRightAnchorStream
            .peek( ( dataRightAnchor ) -> this.collectUniqueDataRights( dataRightAnchor, dataRightsMap ) )
            .map( DataRightAnchorSkinny::new )
            .collect( Collectors.toList() );

      this.dataRights = new ArrayList<DataRight>(dataRightsMap.values());
   }

   /**
    * Adds the {@link DataRight} object within the provided {@link DataRightAnchor} to the map when the map doesn't already contain
    * a {@link DataRight} object with the same data right classification name.
    *
    * @param dataRightAnchor the {@link DataRightAnchor} to extract the {@link DataRight} from.
    * @param map the map used to consolidate {@link DataRight} objects.
    * @implNote It is assumed that two {@link DataRight} objects with the same data right classification name also contain the same Word ML footer.
    */

   @JsonIgnore
   private void collectUniqueDataRights(DataRightAnchor dataRightAnchor, Map<String, DataRight> map) {
      var dataRight = dataRightAnchor.getDataRight();
      map.putIfAbsent(dataRight.getClassification(), dataRight);
   }

   /**
    * Builds and returns an unmodifiable map of {@link DataRightAnchor} objects by {@link AritfactId}.
    *
    * @return a map of {@link DataRightAnchor} objects by {@link ArtifactId}.
    * @throws IllegalStateException when either member {@link #dataRightAnchorSkinnys} or {@link #dataRights} has not been set.
    */

   @JsonIgnore
   public Map<ArtifactId, DataRightAnchor> getDataRightAnchors() {

      if (Objects.isNull(this.dataRightAnchorSkinnys) || Objects.isNull(this.dataRights)) {
         throw new IllegalStateException(
            "DataRightResult::getDataRightAnchors, member \"dataRightAnchors\" and/or \"dataRights\" has not been set.");
      }

      //@formatter:off
      var dataRightsMap = this.dataRights.stream().collect( Collectors.toMap( DataRight::getClassification, Function.identity() ) );

      return
         Collections.unmodifiableMap
            (
              this.dataRightAnchorSkinnys.stream()
                 .map( ( dataRightAnchorSkinny ) -> new DataRightAnchor( dataRightAnchorSkinny, dataRightsMap ) )
                 .collect( Collectors.toMap( DataRightAnchor::getArtifactId, Function.identity() ) )
            );
      //@formatter:on
   }

   /**
    * Gets the list of {@link DataRightAnchorSkinny} objects.
    *
    * @return list of {@link DataRightAnchorSkinny} objects.
    * @throws IllegalStateException when the member {@link #dataRightAnchorSkinnys} has not been set.
    */

   public List<DataRightAnchorSkinny> getDataRightAnchorSkinnys() {
      if (Objects.isNull(this.dataRightAnchorSkinnys)) {
         throw new IllegalStateException(
            "DataRightResult::getDataRightAnchorSkinnys, member \"dataRightAnchorSkinnys\" has not been set.");
      }

      return this.dataRightAnchorSkinnys;
   }

   /**
    * Gets the list of unique {@link DataRight} objects.
    *
    * @return list of {@link DataRight} objects.
    * @throws IllegalStateException when the member {@link #dataRights} has not been set.
    */

   public List<DataRight> getDataRights() {
      if (Objects.isNull(this.dataRights)) {
         throw new IllegalStateException("DataRightResult::getDataRights, member \"dataRights\" has not been set.");
      }

      return this.dataRights;
   }

   /**
    * Predicate to test the validity of the {@link DataRightResult} object.
    *
    * @return <code>true</code>, when all members are non-<code>null</code> and valid; otherwise, <code>false</code>.
    */

   public boolean isValid() {
      //@formatter:off
      return
            Objects.nonNull( this.dataRightAnchorSkinnys ) && this.dataRightAnchorSkinnys.stream().allMatch( DataRightAnchorSkinny::isValid )
         && Objects.nonNull( this.dataRights             ) && this.dataRights.stream().allMatch( DataRight::isValid )
         ;
      //@formatter:on
   }

   /**
    * Sets the list of {@link DataRightAnchorSkinny} objects.
    *
    * @param dataRightAnchorSkinnys list of {@link DataRightAnchorSkinny} objects.
    * @throws IllegalStateException when an attempt is made to set the {@link #dataRightAnchorSkinnys} list for a
    * {@link DataRightResult} that has already been set.
    * @throws NullPointerException when the parameter <code>dataRightAnchorSkinnys</code> is <code>null</code>.
    */

   public void setDataRightAnchorSkinnys(List<DataRightAnchorSkinny> dataRightAnchorSkinnys) {
      if (Objects.nonNull(this.dataRightAnchorSkinnys)) {
         throw new IllegalStateException(
            "DataRightResult::setDataRightAnchors, member \"dataRightAnchorSkinnys\" has already been set.");
      }

      this.dataRightAnchorSkinnys = Objects.requireNonNull(dataRightAnchorSkinnys,
         "DataRightResult::setDataRightAnchorSkinnys, parameter \"dataRightAnchorSkinnys\" cannot be null.");
   }

   /**
    * Sets the list of {@link DataRight} objects.
    *
    * @param dataRights list of {@link DataRight} objects.
    * @throws IllegalStateException when an attempt is made to set the {@link #dataRights} list for a
    * {@link DataRightResult} that has already been set.
    * @throws NullPointerException when the parameter <code>dataRights</code> is <code>null</code>.
    */

   public void setDataRights(List<DataRight> dataRights) {
      if (Objects.nonNull(this.dataRights)) {
         throw new IllegalStateException(
            "DataRightResult::setDataRightAnchors, member \"dataRightAnchorSkinnys\" has already been set.");
      }

      this.dataRights =
         Objects.requireNonNull(dataRights, "DataRightResult::setDataRights, parameter \"dataRights\" cannot be null.");
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public Message toMessage(int indent, Message message) {
      var outMessage = Objects.nonNull(message) ? message : new Message();

      //@formatter:off
      outMessage
         .indent( indent )
         .title( "Data Right Anchors Result" )
         .indentInc()
         .segmentIndexedList( "dataRightAnchorSkinnys", this.dataRightAnchorSkinnys )
         .segmentIndexedList( "dataRights",             this.dataRights             )
         .indentDec()
         ;
      //@formatter:on

      return outMessage;
   }

   /**
    * {@inheritDoc}
    */

   @Override
   public String toString() {
      return this.toMessage(0, null).toString();
   }
}

/* EOF */
