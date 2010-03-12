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

import java.nio.ByteBuffer;
import java.util.Collection;

import org.eclipse.osee.ote.messaging.dds.Data;
import org.eclipse.osee.ote.messaging.dds.DataSample;
import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.Duration;
import org.eclipse.osee.ote.messaging.dds.InstanceHandle;
import org.eclipse.osee.ote.messaging.dds.NotImplementedException;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.SampleStateKind;
import org.eclipse.osee.ote.messaging.dds.StatusKind;
import org.eclipse.osee.ote.messaging.dds.ViewStateKind;
import org.eclipse.osee.ote.messaging.dds.condition.QueryCondition;
import org.eclipse.osee.ote.messaging.dds.condition.ReadCondition;
import org.eclipse.osee.ote.messaging.dds.listener.DataReaderListener;
import org.eclipse.osee.ote.messaging.dds.listener.TopicListener;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessChangedStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleRejectedStatus;
import org.eclipse.osee.ote.messaging.dds.status.SubscriptionMatchStatus;

/**
 * The base class which all application specific data readers must extend. Access to read
 * information in the DDS system is all made possible by this class.
 * 
 * This class is partially implemented.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class DataReader extends DomainEntity {
   private final TopicDescription topicDescription;
   private final Subscriber subscriber;
   private ByteBuffer dataBuffer;
   private boolean deleted;
   private DataStoreItem item;
   
   /**
    * Creates a reader with all of the necessary information. This constructor is only visible to
    * the DDS system, applications should use
    * {@link DomainParticipant#createTopic(String, String, TopicListener)} to create a Topic.
    */
   public DataReader(TopicDescription topicDescription, Subscriber subscriber, Boolean enabled, DataReaderListener listener, EntityFactory parentFactory) {
      super(enabled.booleanValue(), listener, parentFactory);
      this.topicDescription = topicDescription;
      this.subscriber = subscriber;
      this.dataBuffer = ByteBuffer.wrap(new byte[0]);
      this.deleted = false;
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
    * Set this item as deleted. This should only be called on the item by the factory which created
    * this reader, and when the <code>deleteReader()</code> method is called.
    */
   void markAsDeleted() { // package scope so that factories of this item can mark it as deleted
      deleted = true;
   }

   /**
    * Retrieve access to the listener assigned to this <code>DataReader</code>. The reader is not
    * guaranteed to have a listener, so this method may return <b>null</b>, and the user of this
    * method should handle that appropriately.
    */
   public DataReaderListener getListener() {
      return (DataReaderListener) super.getBaseListener();
   }

   /**
    * Set the listener for this <code>DataReader</code>. If another listener was already assigned
    * then it will be overridden with out any error. Additionally, <b>null</b> may be passed as the
    * listener to remove the current listener from this reader.
    * 
    * @param listener - The listener to invoke when events occur.
    * @param mask - Not implemented, <b>null</b> is acceptable.
    * 
    * @return {@link ReturnCode#OK}
    */
   public ReturnCode setListener(DataReaderListener listener, StatusKind mask) {
      return super.setBaseListener(listener, mask);
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode read(Collection<?> dataSamples, long maxSamples, Collection<?> sampleStates, Collection<?> viewStates, Collection<?> instanceStates) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode take(Collection<?> dataSamples, long maxSamples, Collection<?> sampleStates, Collection<?> viewStates, Collection<?> instanceStates) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode readWithCondition(Collection<?> dataSamples, long maxSamples, ReadCondition readCondition) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode takeWithCondition(Collection<?> dataSamples, long maxSamples, ReadCondition readCondition) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * Method for reading or taking the next available <code>DataSample</code> for this reader.
    * 
    * @param dataSample - A <code>DataSample</code> which the information will be placed in.
    * @param isTake - a <code>boolean</code> stating whether the data should be taken by this
    *            call.
    * 
    * @return
    * <ul>
    * <li>{@link ReturnCode#NOT_ENABLED} if this reader is not enabled.
    * <li>{@link ReturnCode#NO_DATA} if no data is available to read or take.
    * <li>{@link ReturnCode#OK} otherwise.
    * </ul>
    */
   private ReturnCode readOrTakeNextSample(DataSample dataSample, boolean isTake) {

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      DataSample retDataSample = item.getTheDataSample();

      if (retDataSample == null)
         return ReturnCode.NO_DATA;

      // Copy our information in to the supplied buffers
      //dataSample.getData().setFromByteArray(retDataSample.getData().toByteArray());
      dataSample.getData().copyFrom(retDataSample.getData());
      dataSample.setSampleInfo(retDataSample.getSampleInfo());

      // update the local references sample info accordingly
      dataSample.getSampleInfo().setSampleStateKind(SampleStateKind.READ);
      dataSample.getSampleInfo().setViewStateKind(ViewStateKind.NOT_NEW);
      if (isTake) {
    	  item = null;
      }
      return ReturnCode.OK;

   }

   /**
    * Method for reading the next available <code>DataSample</code> for this reader.
    * 
    * @param dataSample - A <code>DataSample</code> which the information will be placed in.
    * 
    * @return
    * <ul>
    * <li>{@link ReturnCode#NOT_ENABLED} if this reader is not enabled.
    * <li>{@link ReturnCode#NO_DATA} if no data is available to read or take.
    * <li>{@link ReturnCode#OK} otherwise.
    * </ul>
    */
   public ReturnCode readNextSample(DataSample dataSample) {
      return readOrTakeNextSample(dataSample, false);
   }

   /**
    * Method for taking the next available <code>DataSample</code> for this reader.
    * 
    * @param dataSample - A <code>DataSample</code> which the information will be placed in.
    * 
    * @return
    * <ul>
    * <li>{@link ReturnCode#NOT_ENABLED} if this reader is not enabled.
    * <li>{@link ReturnCode#NO_DATA} if no data is available to read or take.
    * <li>{@link ReturnCode#OK} otherwise.
    * </ul>
    */
   public ReturnCode takeNextSample(DataSample dataSample) {
      return readOrTakeNextSample(dataSample, true);
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode readInstance(Collection<?> dataSamples, long maxSamples, InstanceHandle instanceHandle, Collection<?> sampleStates, Collection<?> viewStates, Collection<?> instanceStates) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode takeInstance(Collection<?> dataSamples, long maxSamples, InstanceHandle instanceHandle, Collection<?> sampleStates, Collection<?> viewStates, Collection<?> instanceStates) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode readNextInstance(Collection<?> dataSamples, long maxSamples, InstanceHandle instanceHandle, Collection<?> sampleStates, Collection<?> viewStates, Collection<?> instanceStates) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode takeNextInstance(Collection<?> dataSamples, long maxSamples, InstanceHandle instanceHandle, Collection<?> sampleStates, Collection<?> viewStates, Collection<?> instanceStates) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode readNextInstance(Collection<?> dataSamples, long maxSamples, InstanceHandle instanceHandle, ReadCondition readCondition) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode takeNextInstance(Collection<?> dataSamples, long maxSamples, InstanceHandle instanceHandle, ReadCondition readCondition) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode returnLoan(Collection<?> dataSamples) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode getKeyValue(Data keyHolder, InstanceHandle instanceHandle) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReadCondition createReadCondition(Collection<?> sampleStates, Collection<?> viewStates, Collection<?> instanceStates) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public QueryCondition createQueryCondition(Collection<?> sampleStates, Collection<?> viewStates, Collection<?> instanceStates, String queryExpression, Collection<?> queryParameters) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode deleteReadCondition(ReadCondition readCondition) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public LivelinessChangedStatus getLivelinessChangedStatus() {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public RequestedDeadlineMissedStatus getRequestedDeadlineMissedStatus() {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public RequestedIncompatibleQosStatus getRequestedIncompatibleQosStatus() {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public SampleRejectedStatus getSampleRejectedStatus() {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      return null;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public SubscriptionMatchStatus getSubscriptionMatchStatus() {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      return null;
   }

   /**
    * @return Returns the topicDescription.
    */
   public TopicDescription getTopicDescription() {
      return topicDescription;
   }

   /**
    * @return Returns the subscriber.
    */
   public Subscriber getSubscriber() {
      return subscriber;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode waitForHistoricalData(Duration duration) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode getMatchedPublicationData(PublicationBuiltinTopicData publicationData, InstanceHandle instanceHandle) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This method is here for future functionality that is described in the DDS specification but
    * has not been implemented or used.
    */
   public ReturnCode getMatchedPublications(Collection<?> instanceHandles) {

      // UNSURE This method has not been implemented, but is called out in the spec
      if (true)
         throw new NotImplementedException();

      // Check that the Entity is enabled before proceeding (See description of enable on the Entity
      // object in the DDS spec)
      if (!isEnabled())
         return ReturnCode.NOT_ENABLED;

      return ReturnCode.ERROR;
   }

   /**
    * This is intended to be called whenever new data is "published" to the Service. Calls
    * onDataAvailable on the attached <code>DataReaderListener</code> as appropriate.
    * 
    * @param theData
    */
   void store(Data theData) { // package scope
      // Get the data portion from theData
      ByteBuffer incomingBuffer = theData.toByteBuffer();

      // Check the size, increase the size of dataBuffer only if needed
      if (incomingBuffer.remaining() != dataBuffer.remaining()) {
         dataBuffer = ByteBuffer.wrap(new byte[incomingBuffer.remaining()]);
      }
      
      incomingBuffer.mark();
      dataBuffer.put(incomingBuffer);
      incomingBuffer.reset();
      dataBuffer.rewind();
      getListener().onDataAvailable(this);
   }
   
   public synchronized void processNewData(DataStoreItem item) {
	   this.item = item;
	   DataReaderListener listener = getListener();
	   if (listener != null) {
		   listener.onDataAvailable(this);
	   }
   }
}
