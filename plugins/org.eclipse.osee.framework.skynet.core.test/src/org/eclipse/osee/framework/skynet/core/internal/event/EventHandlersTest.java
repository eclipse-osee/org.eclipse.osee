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
package org.eclipse.osee.framework.skynet.core.internal.event;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBranchEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteBroadcastEvent1;
import org.eclipse.osee.framework.messaging.event.res.msgs.RemoteTransactionEvent1;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.ArtifactEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.BranchRemoteEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.TransactionEventHandler;
import org.eclipse.osee.framework.skynet.core.internal.event.handlers.TransactionRemoteEventHandler;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for @{link EventHandlers}
 *
 * @author Roberto E. Escobar
 */
public class EventHandlersTest {

   @Test
   public void test() {
      EventHandlers handlers = new EventHandlers();

      EventHandlerLocal<?, ?> local1 = new TransactionEventHandler();
      EventHandlerLocal<?, ?> local2 = new ArtifactEventHandler();

      EventHandlerRemote<?> remote1 = new TransactionRemoteEventHandler();
      EventHandlerRemote<?> remote2 = new BranchRemoteEventHandler();

      handlers.addLocalHandler(TransactionEvent.class, local1);
      Assert.assertEquals(1, handlers.sizeLocal());
      Assert.assertEquals(0, handlers.sizeRemote());

      handlers.addLocalHandler(ArtifactEvent.class, local2);
      Assert.assertEquals(2, handlers.sizeLocal());
      Assert.assertEquals(0, handlers.sizeRemote());

      handlers.addRemoteHandler(RemoteTransactionEvent1.class, remote1);
      Assert.assertEquals(2, handlers.sizeLocal());
      Assert.assertEquals(1, handlers.sizeRemote());

      handlers.addRemoteHandler(RemoteBranchEvent1.class, remote2);
      Assert.assertEquals(2, handlers.sizeLocal());
      Assert.assertEquals(2, handlers.sizeRemote());

      Assert.assertEquals(local1, handlers.getLocalHandler(new TransactionEvent()));
      Assert.assertEquals(local2, handlers.getLocalHandler(new ArtifactEvent(CoreBranches.COMMON)));
      Assert.assertNull(handlers.getLocalHandler(new BranchEvent(BranchEventType.Added, BranchId.valueOf(345L))));

      Assert.assertEquals(remote1, handlers.getRemoteHandler(new RemoteTransactionEvent1()));
      Assert.assertEquals(remote2, handlers.getRemoteHandler(new RemoteBranchEvent1()));
      Assert.assertNull(handlers.getRemoteHandler(new RemoteBroadcastEvent1()));

      handlers.removeLocalHandler(TransactionEvent.class);
      Assert.assertEquals(1, handlers.sizeLocal());
      Assert.assertEquals(2, handlers.sizeRemote());

      handlers.removeLocalHandler(ArtifactEvent.class);
      Assert.assertEquals(0, handlers.sizeLocal());
      Assert.assertEquals(2, handlers.sizeRemote());

      handlers.removeRemoteHandler(RemoteTransactionEvent1.class);
      Assert.assertEquals(0, handlers.sizeLocal());
      Assert.assertEquals(1, handlers.sizeRemote());

      handlers.removeRemoteHandler(RemoteBranchEvent1.class);
      Assert.assertEquals(0, handlers.sizeLocal());
      Assert.assertEquals(0, handlers.sizeRemote());
   }
}
