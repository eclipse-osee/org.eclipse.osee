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

import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;

/**
 * @author Donald G. Dunne
 */
public class NetworkAccessControlArtifactsEvent extends FrameworkArtifactEventBase {
   private final String accessControlModType;

   public static String GUID = "Aylfa1pAo0QntOBC7TgA";

   public NetworkAccessControlArtifactsEvent(String accessControlModType, String branchGuid, String artGuid, String artTypeGuid, NetworkSender networkSender) {
      super(GUID, new DefaultBasicGuidArtifact(branchGuid, artTypeGuid, artGuid), networkSender);
      this.accessControlModType = accessControlModType;
   }

   public NetworkAccessControlArtifactsEvent(NetworkAccessControlArtifactsEvent base) {
      super(base);
      this.accessControlModType = base.getAccessControlModTypeName();
   }

   public String getAccessControlModTypeName() {
      return accessControlModType;
   }

}
