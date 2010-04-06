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
package org.eclipse.osee.framework.messaging.event.res.event;

import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;

/**
 * @author Donald G. Dunne
 */
public abstract class FrameworkArtifactEventBase extends FrameworkEventBase {

   private final Collection<? extends DefaultBasicGuidArtifact> defaultBasicGuidArtifacts;

   public FrameworkArtifactEventBase(String eventGuid, DefaultBasicGuidArtifact defaultBasicGuidArtifact, NetworkSender networkSender) {
      super(networkSender, eventGuid);
      this.defaultBasicGuidArtifacts = Arrays.asList(defaultBasicGuidArtifact);
   }

   public FrameworkArtifactEventBase(String eventGuid, Collection<? extends DefaultBasicGuidArtifact> defaultBasicGuidArtifacts, NetworkSender networkSender) {
      super(networkSender, eventGuid);
      this.defaultBasicGuidArtifacts = defaultBasicGuidArtifacts;
   }

   public FrameworkArtifactEventBase(FrameworkArtifactEventBase base) {
      super(base.getNetworkSender(), base.getEventGuid());
      this.defaultBasicGuidArtifacts = base.getDefaultBasicGuidArtifacts();
   }

   public Collection<? extends DefaultBasicGuidArtifact> getDefaultBasicGuidArtifacts() {
      return defaultBasicGuidArtifacts;
   }

}
