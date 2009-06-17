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

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.StatusKind;
import org.eclipse.osee.ote.messaging.dds.listener.DataReaderListener;
import org.eclipse.osee.ote.messaging.dds.listener.Listener;
import org.eclipse.osee.ote.messaging.dds.listener.SubscriberListener;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;
import org.eclipse.osee.ote.messaging.dds.status.SampleLostStatus;

/**
 * Provides functionality for applications to create <code>DataReader</code>'s to receive the appropriate published data. Maintains the data which is made
 * available to those <code>DataReader</code> 's.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class Subscriber extends DomainEntity implements EntityFactory {

   private final ConcurrentHashMap<TopicDescription, CopyOnWriteArrayList<DataReader>> topicMap = new ConcurrentHashMap<TopicDescription, CopyOnWriteArrayList<DataReader>>(512);
   private DomainParticipant participant;
   private final ExecutorService threadService;
   /**
    * Constructor for <code>Subscriber</code> with the provided listener attached, and enabled status set as passed. This constructor is only visible to the
    * DDS system, applications should use {@link DomainParticipant#createSubscriber(SubscriberListener)}to create a <code>Subscriber</code>.
    * 
    * @param participant The participant creating this <code>Subscriber</code>. This is also used as the parentEntity for enabling purposes.
    * @param enabled If <b>true </b>, the created <code>Subscriber</code> will be enabled iff the parentEntity is enabled.
    * @param listener The listener to attach to the created subscriber.
    */
   Subscriber(DomainParticipant participant, boolean enabled, Listener listener) {
      super(enabled, listener, participant);
      this.participant = participant;
      threadService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);
   }

   /**
    * Gets the attached listener.
    * 
    * @return The <code>SubscriberListener</code> attached to this.
    * @see Entity#getBaseListener()
    */
   public SubscriberListener getListener() {
      return (SubscriberListener) super.getBaseListener();
   }

   /**
    * Sets the <code>SubscriberListener</code> attached to this. If a listener is already set, this will replace it.
    * 
    * @see Entity#setBaseListener(Listener, StatusKind)
    */
   public ReturnCode setListener(SubscriberListener listener, StatusKind mask) {
      return super.setBaseListener(listener, mask);
   }

   /**
    * Creates a <code>DataReader</code> for the given <code>Topic</code> and adds it to this <code>Subscriber</code>.
    * 
    * @param topicDescription The <code>Topic</code> associated with the <code>DataReader</code> to be created.
    * @param listener The <code>DataReaderListener</code> to be attached to the created <code>DataReader</code>.
    * @return The <code>DataReader</code> created, or null if it could not be created.
    */
   public DataReader createDataReader(TopicDescription topicDescription, DataReaderListener listener) {
      DataReader dataReader = null;

      if (true != (topicDescription instanceof Topic)) {
         return null;
      }

      //TUNE - get the classloader & constructor during Topic creation instead of here
      try {
//         Class readerClass = topic.getTypeSignature().getClassLoader().loadClass(topic.getTypeSignature().getReaderName());
//
//         Constructor constructor = readerClass.getConstructor(new Class[] {TopicDescription.class, Subscriber.class, Boolean.class, DataReaderListener.class,
//               EntityFactory.class});
//
//         dataReader = (DataReader) constructor.newInstance(new Object[] {topicDescription, this, new Boolean(this.isEnabled()), listener, this});
         
         
         dataReader = new DataReader(topicDescription, this, new Boolean(this.isEnabled()), listener, this);
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
      }
      catch (SecurityException ex) {
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
      if (dataReader != null) {
    	  CopyOnWriteArrayList<DataReader> readers = topicMap.get(topicDescription);
    	  if (readers == null) {
    		  readers = new CopyOnWriteArrayList<DataReader>();
    		  topicMap.put(topicDescription, readers);
    	  }
    	  readers.add(dataReader);
      }
      return dataReader;
   }

   /**
    * Removes the <code>DataReader</code> from this <code>Subscriber</code>. If the reader was already deleted, or was not created by this
    * <code>Subscriber</code>, an error is returned.
    * 
    * @param dataReader The reader to delete.
    * @return {@link ReturnCode#OK}if the reader was successfully deleted, otherwise {@link ReturnCode#NOT_ENABLED}if this <code>Subscriber</code> is not
    *         enabled, or {@link ReturnCode#ALREADY_DELETED}if the reader has already been deleted, or {@link ReturnCode#PRECONDITION_NOT_MET}if dataReader
    *         was not created by this <code>Subscriber</code>.
    */
   public ReturnCode deleteDataReader(DataReader dataReader) {

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      // Check that the writer is not already marked as deleted (in case others kept a reference to it
      if (dataReader.isDeleted())
         return ReturnCode.ALREADY_DELETED;


      
//      boolean found = dataReaders.remove(dataReader);

      if (topicMap.remove(dataReader.getTopicDescription()) != null) {
    	  dataReader.markAsDeleted();
          return ReturnCode.OK;
      } else {
         return ReturnCode.PRECONDITION_NOT_MET;
      }

   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been implemented or used.
    */
   public DataReader lookupDataReader(String topicName) {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();
      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been implemented or used.
    */
   public ReturnCode beginAccess() {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been implemented or used.
    */
   public ReturnCode endAccess() {
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been implemented or used.
    */
   public ReturnCode getDataReaders(Collection<DataReader> dataReaders, Collection<?> sampleStateKind, Collection<?> viewStateKind, Collection<?> instanceStateKind) {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been implemented or used.
    */
   public void notifyDataReaders() {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but has not been implemented or used.
    */
   public SampleLostStatus getSampleLostStatus() {
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();
      return null;
   }

   /**
    * @return the <code>DomainParticipant</code> to which this belongs.
    */
   public DomainParticipant getParticipant() {
      // NOTE this method is part of the DDS spec.
      return participant;
   }

   /**
    * Deletes all of the <code>DataReader</code> objects currently in this <code>Subscriber</code>.
    * 
    * @return {@link ReturnCode#OK}if the readers were successfully deleted, otherwise {@link ReturnCode#NOT_ENABLED}if this <code>Subscriber</code> is not
    *         enabled.
    */
   public ReturnCode deleteContainedEntities() {
      /*
       * PARTIAL per the DDS spec, this should also delete the ReadCondition and QueryCondition objects stored in the <code> DataReader </code> 's, however
       * because we haven't implemented the conditions they are not deleted here.
       */

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;



      for (CopyOnWriteArrayList<DataReader> readers : topicMap.values()) {
    	  for (DataReader reader : readers) {
    		  reader.markAsDeleted();
    	  }
    	  readers.clear();
      }
      topicMap.clear();
      return ReturnCode.OK;
   }

   /**
    * @return <b>true </b> iff this <code>Subscriber</code> currently has any data readers.
    */
   public boolean hasDataReaders() {
      return !topicMap.isEmpty();
   }

   /**
    * Processes new data received. If this Subscriber has at least one DataReader who is interested in this data, and the <code>Subscriber</code> has an
    * attached listener, notify it via {@link SubscriberListener#onDataOnReaders(CopyOfSubscriber)}. Otherwise, if there is no <code>SubscriberListener</code> then
    * notify all of the interested <code>DataReader</code>'s via {@link DataReaderListener#onDataAvailable(DataReader)}. The data is stored so that it is
    * available for a reader to read or take.
    * 
    * @param dataStoreItem The newly published data.
    */
   void processNewData(final DataStoreItem dataStoreItem) { // This has package scope since it is a system-level type call
	   final Collection<DataReader> readers = topicMap.get(dataStoreItem.getTheTopicDescription());
	   if (readers  !=null && !readers.isEmpty()) {

		   // SPEC NOTE: The following listener logic is based on paragraph "Listener access to Read Communication Status"
		   //            in the DDS specification.

		   // Invoke the SubscriberListener if available
		   SubscriberListener listener = getListener();
		   if (listener != null) {
			   listener.onDataOnReaders(this);

			   // Otherwise, invoke the DataReaderListeners
		   } else {
			   // Check all DataReader objects for listeners
			   for (final DataReader reader : readers){
				   reader.processNewData(dataStoreItem);
				   /*
				   threadService.submit(new Runnable() {
					   public void run() {
						   reader.processNewData(dataStoreItem);
					   }

				   });
				   */
				   
			   }
		   }
	   }
   }
}