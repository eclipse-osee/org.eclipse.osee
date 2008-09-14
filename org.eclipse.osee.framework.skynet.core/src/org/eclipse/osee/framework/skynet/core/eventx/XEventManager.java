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
package org.eclipse.osee.framework.skynet.core.eventx;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactChangeTypeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkArtifactPurgeEvent;
import org.eclipse.osee.framework.messaging.event.skynet.event.NetworkTransactionDeletedEvent;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.RemoteEventManager;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.skynet.core.utility.RemoteArtifactEventFactory;
import org.eclipse.osee.framework.ui.plugin.event.Sender;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedArtifact;
import org.eclipse.osee.framework.ui.plugin.event.Sender.Source;

/**
 * @author Donald G. Dunne
 */
public class XEventManager {

   private static final HashCollection<Object, IXEventListener> listenerMap =
         new HashCollection<Object, IXEventListener>(false, HashSet.class, 100);
   public static final Collection<UnloadedArtifact> EMPTY_UNLOADED_ARTIFACTS = Collections.emptyList();

   /**
    * Kick local and remote purged event depending on sender
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   public static void kickArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      // Kick Local
      for (IXEventListener listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactsPurgedEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IArtifactsPurgedEventListener) listener).handleArtifactsPurgedEvent(sender,
                     loadedArtifacts.getLoadedArtifacts(), EMPTY_UNLOADED_ARTIFACTS);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If source was Local)
      try {
         if (sender.getSource() == Source.Local) {
            RemoteEventManager.kick(new NetworkArtifactPurgeEvent(
                  loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getBranchId(),
                  loadedArtifacts.getAllArtifactIds(), loadedArtifacts.getAllArtifactTypeIds(),
                  RemoteArtifactEventFactory.getAuthor()));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Kick local and remote artifact change type depending on sender
    * 
    * @param sender local if kicked from internal; remote if from external
    * @param toArtifactTypeId
    * @param loadedArtifacts
    * @throws OseeCoreException
    */
   public static void kickArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, LoadedArtifacts loadedArtifacts) throws OseeCoreException {
      // Kick Local
      for (IXEventListener listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactsChangeTypeEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((IArtifactsChangeTypeEventListener) listener).handleArtifactsChangeTypeEvent(sender, toArtifactTypeId,
                     loadedArtifacts.getLoadedArtifacts(), EMPTY_UNLOADED_ARTIFACTS);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If source was Local)
      try {
         if (sender.getSource() == Source.Local) {
            RemoteEventManager.kick(new NetworkArtifactChangeTypeEvent(
                  loadedArtifacts.getLoadedArtifacts().iterator().next().getBranch().getBranchId(),
                  loadedArtifacts.getAllArtifactIds(), loadedArtifacts.getAllArtifactTypeIds(), toArtifactTypeId,
                  sender.getAuthor()));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /**
    * Kick local and remote transaction deleted event
    * 
    * @param sender local if kicked from internal; remote if from external
    * @throws OseeCoreException
    */
   public static void kickTransactionsDeletedEvent(Sender sender, int[] transactionIds) throws OseeCoreException {
      // Kick Local
      for (IXEventListener listener : listenerMap.getValues()) {
         if (listener instanceof IArtifactsChangeTypeEventListener) {
            // Don't fail on any one listener's exception
            try {
               ((ITransactionsDeletedEventListener) listener).handleTransactionsDeletedEvent(sender, transactionIds);
            } catch (Exception ex) {
               SkynetActivator.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }
      }
      // Kick Remote (If source was Local)
      try {
         if (sender.getSource() == Source.Local) {
            RemoteEventManager.kick(new NetworkTransactionDeletedEvent(sender.getAuthor(), transactionIds));
         }
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public static void addListener(Object key, IXEventListener listener) {
      listenerMap.put(key, listener);
   }

   public static void removeListeners(Object key, IXEventListener listener) {
      listenerMap.removeValue(key, listener);
   }

   public static void removeListeners(Object key) {
      for (IXEventListener listener : listenerMap.getValues(key)) {
         listenerMap.removeValue(key, listener);
      }
   }
}
