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
package org.eclipse.osee.ote.messaging.dds.test;
import org.eclipse.osee.ote.messaging.dds.DataStoreItem;
import org.eclipse.osee.ote.messaging.dds.IDestination;
import org.eclipse.osee.ote.messaging.dds.ISource;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;
import org.eclipse.osee.ote.messaging.dds.entity.DomainParticipant;
import org.eclipse.osee.ote.messaging.dds.entity.DomainParticipantFactory;
import org.eclipse.osee.ote.messaging.dds.entity.Publisher;
import org.eclipse.osee.ote.messaging.dds.entity.Subscriber;
import org.eclipse.osee.ote.messaging.dds.entity.Topic;
import org.eclipse.osee.ote.messaging.dds.listener.DomainParticipantListener;
import org.eclipse.osee.ote.messaging.dds.service.DomainId;
import org.eclipse.osee.ote.messaging.dds.status.InconsistentTopicStatus;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessChangedStatus;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessLostStatus;
import org.eclipse.osee.ote.messaging.dds.status.OfferedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.OfferedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.PublicationMatchStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleLostStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleRejectedStatus;
import org.eclipse.osee.ote.messaging.dds.status.SubscriptionMatchStatus;
import org.eclipse.osee.ote.messaging.dds.test.data.IntMessageReader;
import org.eclipse.osee.ote.messaging.dds.test.data.IntMessageTypeSupport;
import org.eclipse.osee.ote.messaging.dds.test.data.IntMessageWriter;
import org.eclipse.osee.ote.messaging.dds.test.data.IntegerData;


/**
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class Driver implements DomainParticipantListener{
   private DomainParticipant domainParticipant;
   private Publisher publisher;
   private Subscriber subscriber;
   private Publisher myMiddlewarePublisher;
   
   public Driver() {
      // Get a domain participant so that we can participate in DDS
      DomainParticipantFactory domainParticipantFactory = DomainParticipantFactory.getInstance();
      domainParticipant = domainParticipantFactory.createParticipant(new DomainId(12), this, false);
      
      // Get some publishers and subscribers
      subscriber = domainParticipant.createSubscriber(null);
      publisher = domainParticipant.createPublisher(this);
      myMiddlewarePublisher = domainParticipant.getMiddlewarePublisherInstance(this);
      
      // Register a type, and get some topics built from that type
      new IntMessageTypeSupport().registerType(domainParticipant, "IntMessage");
      Topic anIntTopic = domainParticipant.createTopic("Daves topic", "default", "IntMessage", this);
      Topic anotherIntTopic = domainParticipant.createTopic("Robs topic", "default", "IntMessage", this);

      
      // Get some readers and writers for communication via the generated topics
      IntMessageWriter davesWriter = (IntMessageWriter)publisher.createDataWriter(anIntTopic, this);
      IntMessageReader davesReader = (IntMessageReader)subscriber.createDataReader(anIntTopic, this);
      IntMessageWriter robsWriter = (IntMessageWriter)publisher.createDataWriter(anotherIntTopic, this);
      IntMessageReader robsReader = (IntMessageReader)subscriber.createDataReader(anotherIntTopic, this);
      
      // A reference for read data
      IntegerData readData;
      
      System.out.println("Write 8 to daves message from middleware");
      myMiddlewarePublisher.publishData(new IntegerData(8), anIntTopic.getName(), anIntTopic.getNamespace());
      readData = davesReader.takeNextSample();
      System.out.println("Daves reader sees: " + readData.getTheInt());
      System.out.println("Robs reader sees: " + robsReader.takeNextSample().getTheInt());

      System.out.println("\n");
      System.out.println("Write 54875 to daves message");
      davesWriter.write(5);
      readData = davesReader.takeNextSample();
      System.out.println("Daves reader sees: " + readData.getTheInt());
      System.out.println("Robs reader sees: " + robsReader.takeNextSample().getTheInt());
      System.out.println("Write 10 to daves message, but check the previous data is still 5");
      davesWriter.write(10);
      System.out.println("Daves previous data is: " + readData.getTheInt());
      
      System.out.println("\n");
      System.out.println("Write 12 to robs message");
      robsWriter.write(12);
      System.out.println("Daves reader sees: " + davesReader.takeNextSample().getTheInt());
      System.out.println("Robs reader sees: " + robsReader.takeNextSample().getTheInt());
      
      System.out.println("\n");
      System.out.println("Write 15 to robs message");
      robsWriter.write(15);
      System.out.println("Daves reader sees: " + davesReader.takeNextSample().getTheInt());
      System.out.println("Robs reader sees: " + robsReader.takeNextSample().getTheInt());
   }
   
   public static void main(String[] args) {
      System.out.println("Starting the driver ...");
      System.out.println("---------------------------------------------------");
      new Driver();
      System.out.println("---------------------------------------------------");
      System.out.println("Driver finished");
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.TopicListener#onInconsistentTopic(osee.messaging.dds.entity.Topic, osee.messaging.dds.status.InconsistentTopicStatus)
    */
   public void onInconsistentTopic(Topic theTopic, InconsistentTopicStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.SubscriberListener#onDataOnReaders(osee.messaging.dds.entity.Subscriber)
    */
   public void onDataOnReaders(Subscriber theSubscriber) {
      System.out.println ("++onDataOnReaders was called");
      //theSubscriber.getDataReaders();
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataReaderListener#onDataAvailable(osee.messaging.dds.entity.DataReader)
    */
   public void onDataAvailable(DataReader theReader) {
      System.out.println("**Data available from system: " + theReader.getTopicDescription().getName());
      
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataReaderListener#onSampleRejected(osee.messaging.dds.entity.DataReader, osee.messaging.dds.status.SampleRejectedStatus)
    */
   public void onSampleRejected(DataReader theReader, SampleRejectedStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataReaderListener#onLivelinessChanged(osee.messaging.dds.entity.DataReader, osee.messaging.dds.status.LivelinessChangedStatus)
    */
   public void onLivelinessChanged(DataReader theReader, LivelinessChangedStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataReaderListener#onRequestedDeadlineMissed(osee.messaging.dds.entity.DataReader, osee.messaging.dds.status.RequestedDeadlineMissedStatus)
    */
   public void onRequestedDeadlineMissed(DataReader theReader, RequestedDeadlineMissedStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataReaderListener#onRequestedIncompatibleQos(osee.messaging.dds.entity.DataReader, osee.messaging.dds.status.RequestedIncompatibleQosStatus)
    */
   public void onRequestedIncompatibleQos(DataReader theReader, RequestedIncompatibleQosStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataReaderListener#onSubscriptionMatch(osee.messaging.dds.entity.DataReader, osee.messaging.dds.status.SubscriptionMatchStatus)
    */
   public void onSubscriptionMatch(DataReader theReader, SubscriptionMatchStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataReaderListener#onSampleLost(osee.messaging.dds.entity.DataReader, osee.messaging.dds.status.SampleLostStatus)
    */
   public void onSampleLost(DataReader theReader, SampleLostStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataWriterListener#onLivelinessLost(osee.messaging.dds.entity.DataWriter, osee.messaging.dds.status.LivelinessLostStatus)
    */
   public void onLivelinessLost(DataWriter theWriter, LivelinessLostStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataWriterListener#onOfferedDeadlineMissed(osee.messaging.dds.entity.DataWriter, osee.messaging.dds.status.OfferedDeadlineMissedStatus)
    */
   public void onOfferedDeadlineMissed(DataWriter theWriter, OfferedDeadlineMissedStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataWriterListener#onOfferedIncompatibleQos(osee.messaging.dds.entity.DataWriter, osee.messaging.dds.status.OfferedIncompatibleQosStatus)
    */
   public void onOfferedIncompatibleQos(DataWriter theWriter, OfferedIncompatibleQosStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataWriterListener#onPublicationMatch(osee.messaging.dds.entity.DataWriter, osee.messaging.dds.status.PublicationMatchStatus)
    */
   public void onPublicationMatch(DataWriter theWriter, PublicationMatchStatus status) {
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DataWriterListener#onDataTaken()
    */
   public void onDataSentToMiddleware(DataWriter theWriter) {
      System.out.println("@@onDataTaken called");
      
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.listener.DomainParticipantListener#onPublishNotifyMiddleware(osee.messaging.dds.DataStoreItem)
    */
   public void onPublishNotifyMiddleware(IDestination destination, ISource source, DataStoreItem dataStoreItem) {
      System.out.println("--Middleware notified on publish of: " + dataStoreItem.getTheTopicDescription().getName() + "-" + dataStoreItem.getTheTopicDescription().getTypeName());
      
   }
}
