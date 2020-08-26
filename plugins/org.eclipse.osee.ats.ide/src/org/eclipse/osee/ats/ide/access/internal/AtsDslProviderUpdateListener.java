/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.ide.access.internal;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.dsl.integration.OseeDslProvider;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.filter.ArtifactTypeEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.BranchIdEventFilter;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IArtifactEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * @author Roberto E. Escobar
 */
public final class AtsDslProviderUpdateListener implements IArtifactEventListener {

   //@formatter:off
   private final List<? extends IEventFilter> eventFilters =
      Arrays.asList(
         new ArtifactTypeEventFilter(CoreArtifactTypes.AccessControlModel),
         new BranchIdEventFilter(AtsApiService.get().getAtsBranch())
         );
   //@formatter:on

   private final OseeDslProvider dslProvider;

   public AtsDslProviderUpdateListener(OseeDslProvider dslProvider) {
      this.dslProvider = dslProvider;
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return eventFilters;
   }

   @Override
   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender) {
      try {
         dslProvider.loadDsl();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }
}