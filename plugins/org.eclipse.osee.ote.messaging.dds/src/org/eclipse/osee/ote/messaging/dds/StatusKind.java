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

/**
 * The class which stores all of the available kinds of status. The name
 * of any of the values can be acquired from the <code>getKindName()</code>
 * method inherited from <code>Kind</code>.
 * 
 * @see org.eclipse.osee.ote.messaging.dds.Kind
 * 
 * @author Robert A. Fisher
 * @author David Diepenbrock
 */
public class StatusKind extends Kind {
   public final static StatusKind INCONSISTENT_TOPIC_STATUS = new StatusKind("INCONSISTENT_TOPIC_STATUS", 0x0001 << 0);
   public final static StatusKind OFFERED_DEADLINE_MISSED_STATUS = new StatusKind("OFFERED_DEADLINE_MISSED_STATUS", 0x0001 << 1);
   public final static StatusKind REQUESTED_DEADLINE_MISSED_STATUS = new StatusKind("REQUESTED_DEADLINE_MISSED_STATUS", 0x0001 << 2);
   public final static StatusKind OFFERED_INCOMPATIBLE_QOS_STATUS = new StatusKind("OFFERED_INCOMPATIBLE_QOS_STATUS", 0x0001 << 5);
   public final static StatusKind REQUESTED_INCOMPATIBLE_QOS_STATUS = new StatusKind("REQUESTED_INCOMPATIBLE_QOS_STATUS", 0x0001 << 6);
   public final static StatusKind SAMPLE_LOST_STATUS = new StatusKind("SAMPLE_LOST_STATUS", 0x0001 << 7);
   public final static StatusKind SAMPLE_REJECTED_STATUS = new StatusKind("SAMPLE_REJECTED_STATUS", 0x0001 << 8);
   public final static StatusKind DATA_ON_READERS_STATUS = new StatusKind("DATA_ON_READERS_STATUS", 0x0001 << 9);
   public final static StatusKind DATA_AVAILABLE_STATUS = new StatusKind("DATA_AVAILABLE_STATUS", 0x0001 << 10);
   public final static StatusKind LIVELINESS_LOST_STATUS = new StatusKind("LIVELINESS_LOST_STATUS", 0x0001 << 11);
   public final static StatusKind LIVELINESS_CHANGED_STATUS = new StatusKind("LIVELINESS_CHANGED_STATUS", 0x0001 << 12);
   public final static StatusKind PUBLICATION_MATCH_STATUS = new StatusKind("PUBLICATION_MATCH_STATUS", 0x0001 << 13);
   public final static StatusKind SUBSCRIPTION_MATCH_STATUS = new StatusKind("SUBSCRIPTION_MATCH_STATUS", 0x0001 << 14);

   /**
    * Local constructor for creating <code>StatusKind</code> objects.
    * 
    * @param kindName The name of the kind
    * @param kindId The id value of the kind
    */
   private StatusKind(String kindName, long kindId) {
      super(kindName, kindId);
   }

}
