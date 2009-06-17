/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.messaging.dds.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.osee.ote.messaging.dds.Data;
import org.eclipse.osee.ote.messaging.dds.DataSample;
import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.StatusKind;
import org.eclipse.osee.ote.messaging.dds.listener.DataWriterListener;
import org.eclipse.osee.ote.messaging.dds.listener.Listener;
import org.eclipse.osee.ote.messaging.dds.listener.PublisherListener;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;

/**
 * Provides functionality for applications to create <code>DataWriter</code> 's, and control when data written from
 * the writers is published.
 * <p>
 * This class also provides the middleware the ability to publish data directly (i.e. without a <code>DataWriter</code>).
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class Publisher extends DomainEntity implements EntityFactory {

   private DomainParticipant participant;
   private boolean publicationsAllowed;
   private final CopyOnWriteArrayList<DataWriter> dataWriters;
   private final Collection<DataStoreItem> publicationQueue; // Stores pending publications to be processed, possibly be a seperate thread
   private final Collection<DataStoreItem> pendingQueue; // Stores publications while publications are suspended

   /**
    * Constructor for <code>Publisher</code> with the provided listener attached, and enabled status set as passed.
    * This constructor is only visible to the DDS system, applications should use
    * {@link DomainParticipant#createPublisher(PublisherListener)}to create a <code>Publisher</code>..
    * 
    * @param participant The participant creating this <code>Publisher</code>. This is also used as the parentEntity
    *           for enabling purposes.
    * @param enabled If <b>true </b>, the created <code>Publisher</code> will be enabled iff the parentEntity is
    *           enabled.
    * @param listener The listener to attach to the created publisher.
    */
   Publisher(DomainParticipant participant, boolean enabled, PublisherListener listener) {
      super(enabled, listener, participant);

      this.participant = participant;
      publicationsAllowed = true;
      dataWriters = new CopyOnWriteArrayList<DataWriter>();
      publicationQueue = new ArrayList<DataStoreItem>();
      pendingQueue = new ArrayList<DataStoreItem>();
   }

   /**
    * Gets the attached listener.
    * 
    * @return The <code>PublisherListener</code> attached to this.
    * @see Entity#getBaseListener()
    */
   public PublisherListener getListener() {
      return (PublisherListener) super.getBaseListener();
   }

   /**
    * Sets the <code>PublisherListener</code> attached to this. If a listener is already set, this will replace it.
    * 
    * @see Entity#setBaseListener(Listener, StatusKind)
    */
   public ReturnCode setListener(PublisherListener listener, StatusKind mask) {
      return super.setBaseListener(listener, mask);
   }

   /**
    * Creates a <code>DataWriter</code> for the given <code>Topic</code> and adds it to this <code>Publisher</code>.
    * 
    * @param topic The <code>Topic</code> associated with the <code>DataWriter</code> to be created.
    * @param listener The <code>DataWriterListener</code> to be attached to created <code>DataWriter</code>.
    * @return The <code>DataWriter</code> created, or null if it could not be created.
    */
   public DataWriter createDataWriter(Topic topic, DataWriterListener listener) {
      DataWriter dataWriter = null;

      //TUNE - get the classloader & constructor during Topic creation instead of here
      try {
         //         Class writerClass = topic.getTypeSignature().getClassLoader().loadClass(topic.getTypeSignature().getWriterName());
         //
         //         Constructor constructor = writerClass.getConstructor(new Class[] {Topic.class, Publisher.class, Boolean.class, DataWriterListener.class,
         //               EntityFactory.class});

         //         dataWriter = (DataWriter) constructor.newInstance(new Object[] {topic, this, new Boolean(this.isEnabled()), listener, this});
         dataWriter = new DataWriter(topic, this, new Boolean(this.isEnabled()), listener, this);
      }
      //      catch (InstantiationException ex) {
      //         ex.printStackTrace();
      //      }
      //      catch (IllegalAccessException ex) {
      //         ex.printStackTrace();
      //      }
      //      catch (ClassNotFoundException ex) {
      //         ex.printStackTrace();
      //      }
      catch (IllegalArgumentException ex) {
         ex.printStackTrace();
      } catch (SecurityException ex) {
         ex.printStackTrace();
      }
      //      catch (InvocationTargetException ex) {
      //         ex.printStackTrace();
      //      }
      //      catch (NoSuchMethodException ex) {
      //         ex.printStackTrace();
      //      }
      catch (OutOfMemoryError er) {
         er.printStackTrace();
      }

      // Only keep this writer if it was successfully created.
      if (dataWriter != null) {
         dataWriters.add(dataWriter);
      }
      return dataWriter;
   }

   /**
    * Removes the <code>DataWriter</code> from this <code>Publisher</code>. If the writer was already deleted, or
    * was not created by this <code>Publisher</code>, an error is returned.
    * 
    * @param dataWriter The writer to delete.
    * @return {@link ReturnCode#OK}if the writer was successfully deleted, otherwise {@link ReturnCode#NOT_ENABLED}if
    *         this <code>Publisher</code> is not enabled, or {@link ReturnCode#ALREADY_DELETED}if the writer has
    *         already been deleted, or {@link ReturnCode#PRECONDITION_NOT_MET}if dataWriter was not created by this
    *         <code>Publisher</code>.
    */
   public ReturnCode deleteDataWriter(DataWriter dataWriter) {

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) return ReturnCode.NOT_ENABLED;

      // Check that the writer is not already marked as deleted (in case others kept a reference to it
      if (dataWriter.isDeleted()) return ReturnCode.ALREADY_DELETED;

      if (dataWriters.remove(dataWriter)) {
         dataWriter.markAsDeleted();
         return ReturnCode.OK;
      } else {
         // If the data writer wasn't found, then return PRECONDITION_NOT_MET
         return ReturnCode.PRECONDITION_NOT_MET;
      }
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been
    * implemented or used.
    */
   public DataWriter lookupDataWriter(String topicName) {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      return null;
   }

   /**
    * Suspends published data from being processed (sent to the subscribers) until <code>resumePublications()</code>
    * is called.
    * 
    * @return {@link ReturnCode#OK}if publications are successfully suspended, or {@link ReturnCode#NOT_ENABLED}if
    *         this <code>Publisher</code> has not been enabled.
    */
   public ReturnCode suspendPublications() {

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) return ReturnCode.NOT_ENABLED;

      publicationsAllowed = false;
      return ReturnCode.OK;
   }

   /**
    * Resumes publications that were suspended by calling <code>suspendPublications()</code>.
    * 
    * @return {@link ReturnCode#OK}if publications are successfully suspended, or {@link ReturnCode#NOT_ENABLED}if
    *         this <code>Publisher</code> has not been enabled, or {@link ReturnCode#PRECONDITION_NOT_MET}if
    *         publications were not suspended.
    */
   public ReturnCode resumePublications() {

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) return ReturnCode.NOT_ENABLED;

      if (publicationsAllowed) return ReturnCode.PRECONDITION_NOT_MET;

      // Move the pending items in to the publishQueue
      synchronized (pendingQueue) {
         publicationQueue.addAll(pendingQueue);
         pendingQueue.clear();

         // Now that the queues have been updated, mark publications as allowed
         publicationsAllowed = true;
      }

      publishQueuedData();

      return ReturnCode.OK;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been
    * implemented or used.
    */
   public ReturnCode beginCoherentChanges() {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been
    * implemented or used.
    */
   public ReturnCode endCoherentChanges() {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * @return the <code>DomainParticipant</code> to which this belongs.
    */
   public DomainParticipant getParticipant() {
      // NOTE this method is part of the DDS spec.
      return participant;
   }

   /**
    * Deletes all of the <code>DataWriter</code> objects currently in this <code>Publisher</code>.
    * 
    * @return {@link ReturnCode#OK}if the writers were successfully deleted, otherwise {@link ReturnCode#NOT_ENABLED}if
    *         this <code>Publisher</code> is not enabled.
    */
   public ReturnCode deleteContainedEntities() {

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled()) return ReturnCode.NOT_ENABLED;

      for (DataWriter writer : dataWriters) {
         writer.markAsDeleted();
      }
      dataWriters.clear();
      return ReturnCode.OK;
   }

   /**
    * @return <b>true </b> iff this <code>Publisher</code> currently has any data writers.
    */
   public boolean hasDataWriters() {
      return !dataWriters.isEmpty();
   }

   /**
    * Allows the MiddlewarePublisher to publish data without providing the <code>Topic</code> object, but instead
    * specifying the Topic as a string. This method will only publish data when called by the
    * <code>MiddlewarePublisher</code> of the <code>DomainParticipant</code>. This is intended for an outside
    * System to be able to publish any data it wants, regardless of if the <code>Topic</code> has been created in the
    * DDS system. If a <code>Topic</code> with the provided name does not exist in this publisher's
    * <code>DomainParticipant</code>, the data will not be published.
    * 
    * @param theData The Data to be published
    * @param namespace The name of the Topic associated with theData
    * @throws IllegalStateException When this method is called by any publisher other than the MiddlewarePublisher
    */
   public void publishData(Data theData, String namespace, String topic) {
      if (theData == null) {
         throw new NullPointerException("data cannot be null");
      }
      if (namespace == null) {
         throw new NullPointerException("namespace cannot be null");
      }
      if (topic == null) {
         throw new NullPointerException("topic cannot be null");
      }
      if (this.participant.getMiddlewarePublisher() == this) {
         TopicDescription theTopicDescription = this.participant.lookupTopicDescription(namespace, topic);
         if (theTopicDescription != null) {
            DataSample dataSample = new DataSample(theData, new SampleInfo());
            publishData(null, null, new DataStoreItem(dataSample, theTopicDescription, null));
         }
      } else {
         // This should never be called by anything other than the middleware publisher, so throw an exception
         throw new IllegalStateException("Must be the MiddlewarePublisher to call this method!");
      }
   }

   /**
    * If threading is enabled and publications are not suspended, places the data on the queue to be processed by the
    * publication thread. If threading is not enabled and publications are not suspended, sends the data to the
    * <code>DomainParticipant</code> to be sent to be immediately made available to the subscribers. If publications
    * are suspended the data is queued so it can be processed once publications are resumed.
 * @param destination TODO
 * @param source TODO
 * @param dataStoreItem The published data to be processed
    */
   void publishData(IDestination destination, ISource source, DataStoreItem dataStoreItem) { // package scope

      // If publications are not allowed, then add the item to the queue, and return
      synchronized (pendingQueue) {
         if (!publicationsAllowed) {
            pendingQueue.add(dataStoreItem);
            return;
         }
      }

      // Otherwise, publications are allowed, so process appropriately

      participant.processPublishedData(destination, source, dataStoreItem);
   }

   /**
    * Processes data queued either because we are using a publication thread or because publications were suspended and
    * have now been resumed. In either case, the data queued is immediately ready to be published and will be completely
    * processed even if publications are again suspended before processing has completed.
    */
   void publishQueuedData() { // package scope so that the publisher thread can make use of this
      // Only process if there are items in the queue - don't do synchronize if not necessary
      if (!publicationQueue.isEmpty()) {

         synchronized (publicationQueue) {
            Iterator<DataStoreItem> iter = publicationQueue.iterator();
            while (iter.hasNext()) {
               participant.processPublishedData(null, null, iter.next());
            }
            publicationQueue.clear();
         }
      }
   }

   public void dispose() {
      if (deleteContainedEntities() == ReturnCode.NOT_ENABLED) {
         System.err.println("failed to delete publisher");
      }
      publicationQueue.clear();
      pendingQueue.clear();
   }
}
