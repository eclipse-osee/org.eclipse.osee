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
package org.eclipse.osee.ote.messaging.dds.test.data;
import org.eclipse.osee.ote.messaging.dds.DataSample;
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.entity.DataReader;
import org.eclipse.osee.ote.messaging.dds.entity.EntityFactory;
import org.eclipse.osee.ote.messaging.dds.entity.Subscriber;
import org.eclipse.osee.ote.messaging.dds.listener.DataReaderListener;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;
import org.eclipse.osee.ote.messaging.dds.status.LivelinessChangedStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedDeadlineMissedStatus;
import org.eclipse.osee.ote.messaging.dds.status.RequestedIncompatibleQosStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleLostStatus;
import org.eclipse.osee.ote.messaging.dds.status.SampleRejectedStatus;
import org.eclipse.osee.ote.messaging.dds.status.SubscriptionMatchStatus;

/**
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class IntMessageReader extends DataReader {

   DataSample dataSample;
   private IntMessage intMessage;
   /**
    * @param topicDescription
    * @param subscriber
    * @param enabled
    * @param listener
    * @param parentFactory
    */
   public IntMessageReader(TopicDescription topicDescription, Subscriber subscriber, Boolean enabled, DataReaderListener listener, EntityFactory parentFactory) {
      super(topicDescription, subscriber, enabled, listener, parentFactory);
      
      intMessage = new IntMessage();
      dataSample = new DataSample(intMessage);
      this.setListener(new IntMessageListener(), null);
   }
   
   public class IntMessageListener implements DataReaderListener {

      public synchronized void  onDataAvailable(DataReader theReader) {
         System.out.println ("onDataAvailable Called for " + theReader.getTopicDescription().getName());
      }
      public void onSampleRejected(DataReader theReader, SampleRejectedStatus status) {      }
      public void onLivelinessChanged(DataReader theReader, LivelinessChangedStatus status) {      }
      public void onRequestedDeadlineMissed(DataReader theReader, RequestedDeadlineMissedStatus status) {      }
      public void onRequestedIncompatibleQos(DataReader theReader, RequestedIncompatibleQosStatus status) {      }
      public void onSubscriptionMatch(DataReader theReader, SubscriptionMatchStatus status) {      }
      public void onSampleLost(DataReader theReader, SampleLostStatus status) {      }
   }

   /*
    * (non-Javadoc)
    * @see osee.messaging.dds.entity.DataReader#takeNextSample(osee.messaging.dds.DataSample)
    */
   public IntegerData takeNextSample() {
      ReturnCode code = super.takeNextSample(dataSample);
      
      System.out.println("Result of take is: " + code.getDescription());
      if (dataSample.getData() != null) {
	      IntegerData data = (IntegerData)dataSample.getData();
	      return data;
      } else {
         return new IntegerData(-1);
      }
   }
}
