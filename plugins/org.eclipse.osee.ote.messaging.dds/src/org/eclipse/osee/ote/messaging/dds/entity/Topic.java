/*******************************************************************************
 * Copyright (c) 2004, 20079 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.messaging.dds.entity;

import java.util.Collection;
import java.util.Vector;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.StatusKind;
import org.eclipse.osee.ote.messaging.dds.listener.TopicListener;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;
import org.eclipse.osee.ote.messaging.dds.service.TypeSignature;
import org.eclipse.osee.ote.messaging.dds.status.InconsistentTopicStatus;

/**
 * Created on May 16, 2005 Provides a basic implementation of the <code>TopicDescription</code> interface for basic
 * topic support in the DDS system.
 * 
 * @see org.eclipse.osee.ote.messaging.dds.service.TopicDescription
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class Topic extends DomainEntity implements TopicDescription {

   private int count; // Keeps track of the number of times this Topic was "created"
   private InconsistentTopicStatus inconsistentTopicStatus;
   private Collection<DataReader> dataReaders;
   private Collection<DataWriter> dataWriters;

   // Local variables to implement the TopicDescription interface
   private DomainParticipant participant;
   private TypeSignature typeSignature;
   private String name;
   private String namespace;

   /**
    * Creates a topic with all of the necessary information. This constructor is only visible to the DDS system,
    * applications should use {@link DomainParticipant#createTopic(String, String, TopicListener)} to create a Topic.
    * 
    * @param participant
    * @param typeName
    * @param name
    */
   Topic(DomainParticipant participant, TypeSignature typeName, String name, String namespace, boolean enabled, TopicListener listener, EntityFactory parentFactory) {
      super(enabled, listener, parentFactory);

      this.participant = participant;
      this.typeSignature = typeName;
      this.name = name;
      this.namespace = namespace;

      dataReaders = new Vector<DataReader>();
      dataWriters = new Vector<DataWriter>();
      count = 1;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * @return Returns the participant.
    */
   public DomainParticipant getParticipant() {
      return participant;
   }

   /**
    * @return Returns the typeName.
    */
   public String getTypeName() {
      return typeSignature.getTypeName();
   }

   /**
    * @return Returns the typeSignature.
    */
   public TypeSignature getTypeSignature() {
      return typeSignature;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been
    * implemented or used.
    */
   public InconsistentTopicStatus getInconsistentTopicStatus() {
      return inconsistentTopicStatus;
   }

   /**
    * Returns the count for how many times this topic has been "created". This is done to meet the requirement of the
    * DDS specification that a <code>Topic</code> must be deleted as many times as it was created or found via (@link
    * DomainParticipant#findTopic(String, Duration)}.
    * 
    * @return integer count of the creations of this topic.
    */
   int getCount() {
      return count;
   }

   /**
    * Increment the creation count for this topic. The DDS system calls this internally each time this is necessary per
    * the DDS specification.
    */
   void incrementCount() {
      count++;
   }

   /**
    * Decrement the creation count for this topic. The DDS system calls this internally each time this
    * <code>Topic</code> is passed to {@link DomainParticipant#deleteTopic(Topic)}. When the count reaches zero, then
    * the DDS system knows it can be deleted.
    */
   void decrementCount() {
      count--;
   }

   /**
    * Retrieve access to the listener assigned to this <code>Topic</code>. The topic is not guaranteed to have a
    * listener, so this method may return <b>null</b>, and the user of this method should handle that appropriately.
    */
   public TopicListener getListener() {
      return (TopicListener) super.getBaseListener();
   }

   /**
    * Set the listener for this <code>Topic</code>. If another listener was already assigned then it will be overridden
    * with out any error. Additionally, <b>null</b> may be passed as the listener to remove the current listener from
    * this topic.
    * 
    * @param listener - The listener to invoke when events occur.
    * @param mask - Not implemented, <b>null</b> is acceptable.
    * @return {@link ReturnCode#OK}
    */
   public ReturnCode setListener(TopicListener listener, StatusKind mask) {
      return super.setBaseListener(listener, mask);
   }

   /**
    * @return true if this Topic is being used by a DataReader
    */
   boolean hasDataReaders() {
      return (!dataReaders.isEmpty());
   }

   /**
    * @return true if this Topic is being used by a DataWriter
    */
   boolean hasDataWriters() {
      return (!dataWriters.isEmpty());
   }

   /**
    * Used so the topic can keep track of the DataReaders that are created using this Topic. When the DataReader is
    * deleted, it should call removeDataReader.
    * 
    * @param reader The DataReader that added this topic
    */
   void addDataReader(DataReader reader) {
      dataReaders.add(reader);
   }

   /**
    * Used so the topic can keep track of the DataWriters that are created using this Topic. When the DataReader is
    * deleted, it should call removeDataWriter.
    * 
    * @param writer The DataWriter that added this topic
    */
   void addDataWriter(DataWriter writer) {
      dataWriters.add(writer);
   }

   /**
    * Used so the topic can keep track of the DataReaders that are created using this Topic.
    * 
    * @param reader The DataReader that was deleted and used this Topic
    */
   void removeDataReader(DataReader reader) {
      dataReaders.remove(reader);
   }

   /**
    * Used so the topic can keep track of the DataWriters that are created using this Topic.
    * 
    * @param writer The DataWriter that was deleted and used this Topic
    */
   void removeDataWriter(DataWriter writer) {
      dataWriters.remove(writer);
   }

   public boolean equals(Object obj) {
      if (obj instanceof Topic) {
         Topic topic = (Topic) obj;
         return name.equals(topic.getName()) && namespace.equals(topic.getNamespace());
      }
      return false;
   }

   @Override
   public int hashCode() {
      return name.hashCode() ^ namespace.hashCode();
   }

   public String getNamespace() {
      return this.namespace;
   }

   public void clearDataWriters() {
      dataWriters.clear();
   }

   public void clearDataReaders() {
      dataReaders.clear();
   }
}
