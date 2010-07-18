/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event2.artifact;

import org.eclipse.osee.framework.skynet.core.event.IEventFilteredListener;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.event2.ArtifactEvent;

/**
 * This listener will be called upon a artifact,relation,attribute change gets persisted to the database
 * 
 * @author Donald G. Dunne
 */
public interface IArtifactEventListener extends IEventFilteredListener {

   public void handleArtifactEvent(ArtifactEvent artifactEvent, Sender sender);

}
