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
import org.eclipse.osee.ote.messaging.dds.ReturnCode;
import org.eclipse.osee.ote.messaging.dds.entity.DataWriter;
import org.eclipse.osee.ote.messaging.dds.entity.EntityFactory;
import org.eclipse.osee.ote.messaging.dds.entity.Publisher;
import org.eclipse.osee.ote.messaging.dds.entity.Topic;
import org.eclipse.osee.ote.messaging.dds.listener.DataWriterListener;

/**
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class IntMessageWriter extends DataWriter {
   private IntegerData buffer;
   /**
    * @param topic
    * @param publisher
    * @param enabled
    * @param listener
    * @param parentFactory
    */
   public IntMessageWriter(Topic topic, Publisher publisher, Boolean enabled, DataWriterListener listener, EntityFactory parentFactory) {
      super(topic, publisher, enabled, listener, parentFactory);
      buffer = new IntegerData(0);
   }

   public ReturnCode write(int value) {
      buffer.setTheInt(value);
      return super.write(null, null, buffer, null);
   }
}
