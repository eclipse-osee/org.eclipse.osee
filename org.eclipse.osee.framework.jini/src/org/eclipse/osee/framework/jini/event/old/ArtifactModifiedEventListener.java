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

public class ArtifactModifiedEventListener implements IOseeRemoteEventListener {

   public void notify(OseeRemoteEvent event) {

      if (event.eventData.getEventType().equals(ArtifactModifiedRemoteType.class.getCanonicalName())) {
         try {
            //            final long seqNum = event.sequenceNumber;
            final ArtifactModifiedRemoteType data = (ArtifactModifiedRemoteType) event.eventData;

            System.err.println("Received guid changed event => " + data.getChangedGuid());
            System.err.println("Received guid changed type => " + data.getType());
         } catch (ClassCastException ex) {
            ex.printStackTrace();
         }
      }
   }
}
