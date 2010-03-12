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
package org.eclipse.osee.framework.skynet.core.event;

import java.util.Collection;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Event that represents a collection of Artifacts that were reloaded from DB. This means current attributes and
 * relations were hard-reloaded. This is a LOCAL only event.
 * 
 * @author Donald G. Dunne
 */
public interface IArtifactReloadEventListener extends IEventListener {

   /**
    * Notification of all artifact that were reloaded.
    */
   public void handleReloadEvent(Sender sender, Collection<? extends Artifact> artifacts) throws OseeCoreException;

}
