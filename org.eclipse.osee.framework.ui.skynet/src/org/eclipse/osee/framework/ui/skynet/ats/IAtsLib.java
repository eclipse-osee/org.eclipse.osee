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
package org.eclipse.osee.framework.ui.skynet.ats;

import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public interface IAtsLib {

   public void openATSAction(final Artifact art, final AtsOpenOption option);

   public void createATSAction(String initialDescription, String actionableItem);

   public void openArtifact(String guid, OseeAts.OpenView view);

   public void openArtifact(String guidOrHrid, Integer branchId, OseeAts.OpenView view);

   public void openInAtsWorld(String name, Collection<Artifact> artifacts);

   public boolean isAtsAdmin();
}
