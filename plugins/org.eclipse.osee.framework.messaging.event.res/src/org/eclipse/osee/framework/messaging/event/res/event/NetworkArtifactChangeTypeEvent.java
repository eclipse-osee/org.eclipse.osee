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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;

/**
 * @author Donald G. Dunne
 */
public class NetworkArtifactChangeTypeEvent extends FrameworkArtifactEventBase {
   public static String GUID = "Aylfa1qhw2qCbzMIU8gA";
   private final String toArtTypeGuid;

   public NetworkArtifactChangeTypeEvent(String toArtTypeGuid, Collection<? extends DefaultBasicGuidArtifact> defaultBasicGuidArtifacts, NetworkSender networkSender) {
      super(GUID, defaultBasicGuidArtifacts, networkSender);
      this.toArtTypeGuid = toArtTypeGuid;
   }

   public String getToArtTypeGuid() {
      return toArtTypeGuid;
   }

}
