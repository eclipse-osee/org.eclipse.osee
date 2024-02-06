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

package org.eclipse.osee.define.operations.publisher.datarights;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.jdk.core.util.Message;
import org.eclipse.osee.framework.jdk.core.util.ToMessage;

/**
 * Encapsulates an ordered list of {@link DataRightEntry} objects. Once the list is closed, no more entries may be added
 * and the list is made immutable.
 *
 * @author Angel Avila
 * @author Loren K. Ashley
 */

class DataRightEntryList implements AutoCloseable, Iterable<DataRightEntry>, ToMessage {

   /**
    * Save a list of {@link DataRightEntry} objects. The list is made unmodifiable when the object is closed.
    */

   private List<DataRightEntry> dataRightEntries;

   /**
    * Flag indicates if the object is open.
    */

   private boolean isOpen;

   /**
    * Saves the publishing data rights override classification.
    */

   private final String overrideClassification;

   /**
    * Creates a new open {@link DataRightEntryList} object. Entries can be added until the object is closed.
    */

   public DataRightEntryList(String overrideClassification) {
      this.overrideClassification = Objects.requireNonNull(overrideClassification);
      this.dataRightEntries = new LinkedList<>();
      this.isOpen = true;
   }

   /**
    * Adds a new {@link DataRightEntry} to the list. Once the object is closed, this method will throw an exception.
    *
    * @param artifactId the identifier of the artifact to obtain a data rights result for.
    * @param artifactReadable the artifact associated with the parameter <code>artifactId</code>. This parameter may be
    * {@link ArtifactReadable#SENTINEL} when an artifact with the identifier specified by <code>artifactId</code> cannot
    * be loaded from the database.
    * @throws IllegalStateException when the {@link DataRightEntryList} has been closed with a call to {@link #close}.
    */

   public void add(ArtifactId artifactId, ArtifactReadable artifactReadable) {
      if (!this.isOpen) {
         throw new IllegalStateException(
            "DataRightEntryList::add, attempt to add an entry after the \"DataRightEntryList\" has been closed.");
      }

      var dataRightEntry = new DataRightEntry(artifactId, artifactReadable, this.overrideClassification);
      this.dataRightEntries.add(dataRightEntry);
   }

   /**
    * Transitions the object into an immutable state. After this method is called, any calls to the method {@link #add}
    * will result in an {@link IllegalStateException} being thrown.
    *
    * @throws IllegalStateException when the {@link DataRightEntryList} has been closed with a call to {@link #close}.
    */

   @Override
   public void close() {
      if (!this.isOpen) {
         throw new IllegalStateException(
            "DataRightEntryList::close, attempt to close the \"DataRightEntryList\" after it has been closed.");
      }

      this.isOpen = false;
      this.dataRightEntries = Collections.unmodifiableList(this.dataRightEntries);
   }

   /**
    * Predicate to determine if the {@link DataRightEntryList} is empty.
    *
    * @return <code>true</code> when the list is empty; otherwise, <code>false</code>.
    */

   public boolean isEmpty() {
      return this.dataRightEntries.isEmpty();
   }

   /**
    * Obtains an {@link Iterator} over the {@link DataRightEntry} objects in the list.
    *
    * @return an {@link Iterator} over the {@link DataRightEntry} objects in the list.
    * @throws IllegalStateException when the {@link DataRightEntryList} has not yet been closed with a call to
    * {@link #close}.
    */

   @Override
   public Iterator<DataRightEntry> iterator() {
      if (this.isOpen) {
         throw new IllegalStateException(
            "DataRightEntryList::iterator, attempt to get an \"Iterator\" for a \"DataRightEntryList\" that is still open.");
      }
      return this.dataRightEntries.iterator();
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
         .title( "DataRightEntryList" )
         .indentInc()
         .segment( "isOpen",                 this.isOpen                 )
         .segment( "overrideClassification", this.overrideClassification )
         .segmentIndexed( "dataRightEntries", this.dataRightEntries )
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