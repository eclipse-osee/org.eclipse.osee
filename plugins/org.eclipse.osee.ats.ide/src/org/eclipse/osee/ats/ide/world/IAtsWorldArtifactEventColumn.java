/*******************************************************************************
 * Copyright (c) 2022 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.world;

import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;

/**
 * @author Donald G. Dunne
 */
public interface IAtsWorldArtifactEventColumn {

   public void handleArtifactEvent(ArtifactEvent artifactEvent, WorldXViewer xViewer);

}
