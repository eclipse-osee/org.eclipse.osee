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

package org.eclipse.osee.define.operations.publisher.datarights;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.publishing.DataRight;
import org.eclipse.osee.framework.core.publishing.DataRightAnchor;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Encapsulates a map of {@link DataRightAnchor} objects indexed by {@link ArtifcatId}. Once the map is closed, no more
 * entries may be added and the map is made immutable.
 *
 * @author Loren K. Ashley
 */

class DataRightAnchors implements AutoCloseable, ToMessage {

   /**
    * Saves a map of {@link DataRightAnchor} objects indexed by {@link ArtifactId}. The map is made unmodifiable when
    * the object is closed.
    */

   private Map<ArtifactId, DataRightAnchor> dataRightAnchors;

   /**
    * Flag indicates if the object is open.
    */

   private boolean isOpen;

   /**
    * Creates a new open {@Link DataRightAnchors} object. Entries can be added until the object is closed.
    */

   public DataRightAnchors() {
      this.dataRightAnchors = new LinkedHashMap<>(128);
      this.isOpen = true;
   }

   /**
    * Creates a new {@link DataRightAnchor} with the provided parameters and adds it as an entry. If the
    * {@link DataRightAnchors} object already contains and entry for the specified {@link ArtifactId}, the existing
    * entry will be replaced and discarded.
    *
    * @param artifactId the identifier of the associated artifact.
    * @param dataRight the {@link DataRight} for the artifact.
    * @param isSetDataRightFooter flag to indicate the artifact is the first in a sub-sequence with the specified
    * {@link DataRight}.
    * @return the newly created {@link DataRight}.
    * @throws IllegalStateException when the {@link DataRightAnchor} object has been closed.
    * @throws NullPointerException when any of the parameters <code>artifactId</code>, <code>dataRight</code>, or
    * <code>isSetDataRightFooter</code> are <code>null</code>.
    */

   public DataRightAnchor add(ArtifactId artifactId, DataRight dataRight, boolean isSetDataRightFooter) {
      if (!this.isOpen) {
         throw new IllegalStateException(
            "DataRightAnchor::add, attempt to add an entry after the \"DataRightAnchors\" object has been closed.");
      }

      var dataRightAnchor = new DataRightAnchor(artifactId, dataRight, isSetDataRightFooter);

      this.dataRightAnchors.put(artifactId, dataRightAnchor);

      return dataRightAnchor;
   }

   /**
    * Transitions the object into an immutable state. After this method is called, any calls to the method {@link #add}
    * will result in an {@link IllegalStateException} being thrown.
    *
    * @throws IllegalStateException when the {@link DataRightAnchors} object has been closed with a call to
    * {@link #close}.
    */

   @Override
   public void close() {
      if (!this.isOpen) {
         throw new IllegalStateException(
            "DataRightAnchor::close, attempt to close the \"DataRightAnchors\" object after it has been closed.");
      }

      this.dataRightAnchors = Collections.unmodifiableMap(this.dataRightAnchors);
      this.isOpen = false;
   }

   /**
    * Obtains a {@link Stream} of the {@link DataRightAnchor} objects.
    *
    * @return a {@link Stream} of the {@link DataRightAnchor} objects.
    * @throws IllegalStateException when the {@link DataRightAnchors} object has not yet been closed with a call to
    * {@link #close}.
    */

   public Stream<DataRightAnchor> stream() {
      if (this.isOpen) {
         throw new IllegalStateException(
            "DataRightAnchor::iterator, attempt to get a \"Stream\" for a \"DataRightAnchors\" object that is still open.");
      }
      return this.dataRightAnchors.values().stream();
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
         .title( "DataRightAnchors" )
         .indentInc()
         .segment( "isOpen", this.isOpen )
         .segmentMap( "dataRightAncors", this.dataRightAnchors )
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
