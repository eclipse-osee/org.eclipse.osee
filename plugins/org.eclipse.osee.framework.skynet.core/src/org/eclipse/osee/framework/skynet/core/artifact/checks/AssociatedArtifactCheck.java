/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.artifact.checks;

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactCheck;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;

/**
 * @author John Misinco
 */
public class AssociatedArtifactCheck extends ArtifactCheck {

   @Override
   public IStatus isDeleteable(Collection<Artifact> artifacts)  {
      return BranchManager.isDeleteable(artifacts);
   }
}
