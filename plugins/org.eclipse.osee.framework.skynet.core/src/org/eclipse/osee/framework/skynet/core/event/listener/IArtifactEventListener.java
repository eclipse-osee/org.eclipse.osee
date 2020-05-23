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

package org.eclipse.osee.framework.skynet.core.event.listener;

import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;

/**
 * This listener will be called upon a artifact,relation,attribute change gets persisted to the database
 * 
 * @author Donald G. Dunne
 */
public interface IArtifactEventListener extends IEventFilteredListener {

   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender);

}
