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
package org.eclipse.osee.ote.messaging.dds;

import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;
import org.eclipse.osee.ote.messaging.dds.service.TopicDescription;

/**
 * Provides a coupling of items used for propogating the publication of data.
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class DataStoreItem {
   private final DataSample theDataSample;
   private final TopicDescription theTopicDescription;
   private final DataWriter theWriter;

   /**
    * Create a new <code>DataStoreItem</code> that stores references to all of the necessary information
    * for the system to propogate the data.
    * 
    * @param theData
    * @param theTopicDescription
    * @param theWriter
    */
   public DataStoreItem(DataSample theData, TopicDescription theTopicDescription, DataWriter theWriter) {
      super();
      this.theDataSample = theData;
      this.theTopicDescription = theTopicDescription;
      this.theWriter = theWriter;
   }

   /**
    * @return Returns the theData.
    */
   public DataSample getTheDataSample() {
      return theDataSample;
   }

   /**
    * @return Returns the theTopic.
    */
   public TopicDescription getTheTopicDescription() {
      return theTopicDescription;
   }

   /**
    * @return Returns the theWriter.
    */
   public DataWriter getTheWriter() {
      return theWriter;
   }
}
