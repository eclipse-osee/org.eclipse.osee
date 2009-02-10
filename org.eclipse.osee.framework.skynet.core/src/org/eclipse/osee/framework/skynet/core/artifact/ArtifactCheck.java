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
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public class ArtifactCheck implements IArtifactCheck {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.IArtifactCheck#isDeleteable(java.util.Collection)
    */
   @Override
   public Result isDeleteable(Collection<Artifact> artifacts) throws OseeCoreException {
      return Result.TrueResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.IArtifactCheck#isModifiable(java.util.Collection, java.util.Collection)
    */
   @Override
   public Result isRenamable(Collection<Artifact> artifacts) throws OseeCoreException {
      return Result.TrueResult;
   }

}
