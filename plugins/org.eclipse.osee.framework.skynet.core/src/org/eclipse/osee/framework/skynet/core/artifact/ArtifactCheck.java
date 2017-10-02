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

import java.util.Collection;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.internal.Activator;

/**
 * @author Donald G. Dunne
 */
public class ArtifactCheck implements IArtifactCheck {
   public static final IStatus OK_STATUS = new Status(IStatus.OK, Activator.PLUGIN_ID, null);

   @Override
   public IStatus isDeleteable(Collection<Artifact> artifacts)  {
      return OK_STATUS;
   }

   @Override
   public IStatus isRenamable(Collection<Artifact> artifacts)  {
      return OK_STATUS;
   }

   @Override
   public IStatus isDeleteableRelation(Artifact artifact, IRelationType relationType)  {
      return OK_STATUS;
   }
}