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
package org.eclipse.osee.ats.ide.world;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IWorldViewerEventHandler {

   public WorldXViewer getWorldXViewer();

   public void relationsModifed(Collection<Artifact> relModifiedArts, Collection<Artifact> goalMemberReordered, Collection<Artifact> sprintMemberReordered);

   public boolean isDisposed();

}
