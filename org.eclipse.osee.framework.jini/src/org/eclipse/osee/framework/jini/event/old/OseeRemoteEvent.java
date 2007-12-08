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
package org.eclipse.osee.framework.jini.event.old;

import java.io.Serializable;
import java.rmi.MarshalledObject;

public class OseeRemoteEvent implements Serializable {

   public final long sequenceNumber;
   public final OseeRemoteEventInstance eventData;
   public final MarshalledObject handback;
   public final String publisherGUID;

   private static final long serialVersionUID = -8680199233419549125L;

   public OseeRemoteEvent(OseeRemoteEventInstance eventData, long sequenceNumber, MarshalledObject handback, String publisherGUID) {
      this.eventData = eventData;
      this.sequenceNumber = sequenceNumber;
      this.handback = handback;
      this.publisherGUID = publisherGUID;
   }

}
