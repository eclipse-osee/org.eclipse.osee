/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.skynet.core.internal.event;

import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.EventQosType;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactTopicEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.IEventListener;
import org.eclipse.osee.framework.skynet.core.event.listener.ITransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactTopicEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.event.model.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.topic.event.filter.ITopicEventFilter;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test Case for @{link EventListenerRegistry}
 *
 * @author Roberto E. Escobar
 */
public class EventListenerRegistryTest {

   @Test
   public void testRegistration() {
      BranchId branch = BranchId.valueOf(345L);
      EventListenerRegistry registry = new EventListenerRegistry();
      Delegate1 delegate1 = new Delegate1();
      Delegate2 delegate2 = new Delegate2();

      Assert.assertEquals(0, registry.size());

      registry.addListener(EventQosType.PRIORITY, delegate1);
      Assert.assertEquals(1, registry.size());
      Assert.assertEquals(2, registry.size(EventQosType.PRIORITY)); // needs fix when ArtifactEvent is removed, back to 1
      Assert.assertEquals(0, registry.size(EventQosType.NORMAL));

      registry.addListener(EventQosType.NORMAL, delegate2);
      Assert.assertEquals(2, registry.size());
      Assert.assertEquals(2, registry.size(EventQosType.PRIORITY)); // same as above
      Assert.assertEquals(2, registry.size(EventQosType.NORMAL));

      Collection<IEventListener> listener0 = registry.getListeners(EventQosType.PRIORITY, new TransactionEvent());
      Assert.assertEquals(0, listener0.size());

      Collection<IEventListener> listener =
         registry.getListeners(EventQosType.PRIORITY, new BranchEvent(BranchEventType.Added, branch));
      Assert.assertEquals(0, listener.size());

      Collection<IEventListener> listener2 =
         registry.getListeners(EventQosType.PRIORITY, new ArtifactEvent(CoreBranches.COMMON));
      Assert.assertEquals(1, listener2.size());
      Assert.assertEquals(delegate1, listener2.iterator().next());

      Collection<IEventListener> listener3 =
         registry.getListeners(EventQosType.NORMAL, new BranchEvent(BranchEventType.Added, BranchId.valueOf(3L)));
      Assert.assertEquals(1, listener3.size());
      Assert.assertEquals(delegate2, listener3.iterator().next());

      Collection<IEventListener> listener4 = registry.getListeners(EventQosType.NORMAL, new TransactionEvent());
      Assert.assertEquals(1, listener4.size());
      Assert.assertEquals(delegate2, listener4.iterator().next());

      registry.removeListener(delegate2);
      Assert.assertEquals(1, registry.size());
      Assert.assertEquals(2, registry.size(EventQosType.PRIORITY)); // same as above
      Assert.assertEquals(0, registry.size(EventQosType.NORMAL));

      Collection<IEventListener> listener5 = registry.getListeners(EventQosType.NORMAL, new TransactionEvent());
      Assert.assertEquals(0, listener5.size());

      Collection<IEventListener> listener6 =
         registry.getListeners(EventQosType.PRIORITY, new BranchEvent(BranchEventType.Added, branch));
      Assert.assertEquals(0, listener6.size());

      registry.removeListener(delegate1);
      Assert.assertEquals(0, registry.size());
      Assert.assertEquals(0, registry.size(EventQosType.PRIORITY));
      Assert.assertEquals(0, registry.size(EventQosType.NORMAL));
   }

   private static final class Delegate2 implements ITransactionEventListener, IBranchEventListener {

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return null;
      }

      @Override
      public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
         //
      }

      @Override
      public void handleTransactionEvent(Sender sender, TransactionEvent transEvent) {
         //
      }

   }

   private static final class Delegate1 implements IArtifactEventListener, IArtifactTopicEventListener {

      @Override
      public List<? extends IEventFilter> getEventFilters() {
         return null;
      }

      @Override
      public List<? extends ITopicEventFilter> getTopicEventFilters() {
         return null;
      }

      @Override
      public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
         //
      }

      @Override
      public void handleArtifactTopicEvent(ArtifactTopicEvent artifactEvent, Sender sender) {
         //
      }

   }
}