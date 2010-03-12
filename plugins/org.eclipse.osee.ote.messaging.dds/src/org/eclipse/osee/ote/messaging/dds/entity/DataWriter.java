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
import org.eclipse.osee.ote.messaging.dds.Data;
import org.eclipse.osee.ote.messaging.dds.DataSample;
import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.osee.ote.messaging.dds.InstanceHandle;
import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.StatusKind;
import org.eclipse.osee.ote.messaging.dds.Time;
import org.eclipse.osee.ote.messaging.dds.listener.DataWriterListener;
import org.eclipse.osee.ote.messaging.dds.listener.TopicListener;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessLostStatus;
import org.eclipse.osee.ote.messaging.dds.status.OfferedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.OfferedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.PublicationMatchStatus;

/**
 * The base class which all application specific data writers must extend. Access to write
 * information in the DDS system is all made possible by this class.
 * 
 * This class is partially implemented.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class DataWriter extends DomainEntity {
   private Topic topic;
   private Publisher publisher;
   private boolean deleted;
   private boolean publishBackToLocalDDSReaders = true;

   /**
    * Creates a reader with all of the necessary information. This constructor is only visible to the DDS system, applications 
    * should use {@link DomainParticipant#createTopic(String, String, TopicListener)} to create a Topic.
    */
   public DataWriter(Topic topic, Publisher publisher, Boolean enabled, DataWriterListener listener, EntityFactory parentFactory) {
      super(enabled.booleanValue(), listener, parentFactory);
      this.topic = topic;
      this.publisher = publisher;
      this.deleted = false;
      
      topic.addDataWriter(this);
   }

   /**
    * Provides access to the deletion flag for this <code>DataReader</code>.
    * 
    * @return <b>true</b> iff this has been marked as deleted.
    */
   boolean isDeleted() { // package scope since this is system-level code
      return deleted;
   }

   /**
    * Set this item as deleted. This should only be called on the item by the factory
    * which created this reader, and when the <code>deleteWriter()</code> method is called.
    */
   void markAsDeleted() { // package scope so that factories of this item can mark it as deleted
      topic.removeDataWriter(this);
      deleted = true;
   }

   /**
    * Retrieve access to the listener assigned to this <code>DataWriter</code>. The writer
    * is not guaranteed to have a listener, so this method may return <b>null</b>, and the
    * user of this method should handle that appropriately.
    */
   public DataWriterListener getListener() {
      return (DataWriterListener) super.getBaseListener();
   }

   /**
    * Set the listener for this <code>DataWriter</code>. If another listener was already assigned
    * then it will be overridden with out any error. Additionally, <b>null</b> may be passed as the
    * listener to remove the current listener from this writer.
    * 
    * @param listener - The listener to invoke when events occur.
    * @param mask - Not implemented, <b>null</b> is acceptable.
    * 
    * @return {@link ReturnCode#OK}
    */
   public ReturnCode setListener(DataWriterListener listener, StatusKind mask) {
      return super.setBaseListener(listener, mask);
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public InstanceHandle register(Data data) {
      
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public InstanceHandle registerWithTimestamp(Data data, Time timestamp) {
      
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public InstanceHandle unregister(Data data, InstanceHandle handle) {
      
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public InstanceHandle unregisterWithTimestamp(Data data, InstanceHandle handle, Time timestamp) {
      
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public ReturnCode getKeyValue(Data keyHolder, InstanceHandle handle) {
      
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;
      
      return ReturnCode.ERROR;
   }

   // PARTIAL InstanceHandle is not used 
   /**
    * Method for writing data to the DDS system. This should be overridden to provide
    * type-specific methods for writing data to the system.
 * @param destination TODO
 * @param source TODO
    * 
    * @return <ul>
    *         <li>{@link ReturnCode#NOT_ENABLED} if this writer is not enabled.
    *         <li>{@link ReturnCode#OK} otherwise.
    *         </ul>
    */
   public ReturnCode write(IDestination destination, ISource source, Data data, InstanceHandle handle) {
      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;
      //TUNE find a way to reuse the DataSample and DataStoreItem objects instead new'ing them everytime
      DataSample dataSample = new DataSample(data, new SampleInfo ());
      publisher.publishData(destination, source, new DataStoreItem(dataSample, topic, this));
      
      return ReturnCode.OK;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public ReturnCode writeWithTimestamp(Data data, InstanceHandle handle, Time timestamp) {
      
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;
      
      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public ReturnCode dispose(Data data, InstanceHandle handle) {
      
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;
      
      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public ReturnCode diposeWithTimestamp(Data data, InstanceHandle handle, Time timestamp) {
      
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;
      
      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public LivelinessLostStatus getLivelinessLostStatus() {
      
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public OfferedDeadlineMissedStatus getOfferedDeadlineMissedStatus() {
      
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public OfferedIncompatibleQosStatus getOfferedIncompatibleQosStatus() {
      
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public PublicationMatchStatus getPublicationMatchStatus() {
      
      // DONT_NEED This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
      return null;
   }

   /**
    * @return Returns the topic.
    */
   public Topic getTopic() {
      return topic;
   }

   /**
    * @return Returns the publisher.
    */
   public Publisher getPublisher() {
      return publisher;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public void assertLiveliness() {
      
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public ReturnCode getMatchedSubscriptionData(SubscriptionBuiltinTopicData subscriptionData, InstanceHandle subscriptionHandle) {
      
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;
      
      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification
    * but has not been implemented or used.
    */
   public ReturnCode getMatchedSubscriptions(Collection<?> subscriptionHandles) {
      
      // UNSURE This method has not been implemented, but is called out in the spec
      if (true) throw new NotImplementedException();
      

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;
      
      return ReturnCode.ERROR;
   }

   /**
    * @return the publishBackToLocalDDSReaders
    */
   public boolean isPublishBackToLocalDDSReaders() {
      return publishBackToLocalDDSReaders;
   }

   /**
    * @param publishBackToLocalDDSReaders the publishBackToLocalDDSReaders to set
    */
   public void setPublishBackToLocalDDSReaders(boolean publishBackToLocalDDSReaders) {
      this.publishBackToLocalDDSReaders = publishBackToLocalDDSReaders;
   }
}
