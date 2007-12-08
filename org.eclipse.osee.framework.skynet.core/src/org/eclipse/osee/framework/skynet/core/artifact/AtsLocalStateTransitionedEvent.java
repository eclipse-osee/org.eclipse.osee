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
package org.eclipse.osee.framework.skynet.core.artifact;

import org.eclipse.osee.framework.skynet.core.event.GuidEvent;

/**
 * @author Donald G. Dunne
 */
public class AtsLocalStateTransitionedEvent extends GuidEvent {

   /**
    * @param branch TODO
    * @param sender TODO
    * @param artifact
    * @param propogateRemotely - propogate event to all OSEE platforms true if change has been persisted false if change
    *           is in local jvm only (unsaved)
    * @param type
    */

   public AtsLocalStateTransitionedEvent(String guid, Branch branch, Object sender) {
      super(sender);
      setGuid(guid, branch);
   }

   public AtsLocalStateTransitionedEvent(Artifact artifact, Object sender) {
      super(sender);
      setGuid(artifact.getGuid(), artifact.getBranch());
   }

}
