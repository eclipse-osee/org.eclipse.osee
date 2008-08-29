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

import java.sql.SQLException;
import java.util.Collection;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * @author Donald G. Dunne
 */
public interface IArtifactCheck {

   /**
    * Checks set of artifacts for validation prior to delete
    * 
    * @param artifacts
    * @return Result with description of which artifact and why can't delete
    * @throws OseeCoreException
    * @throws SQLException
    */
   public Result isDeleteable(Collection<Artifact> artifacts) throws OseeCoreException, SQLException;

   /**
    * Checks set of artifacts for validation prior to rename
    * @param artifacts
    * 
    * @return Result with description of which artifact and why can't rename
    * @throws OseeCoreException
    * @throws SQLException
    */
   public Result isRenamable(Collection<Artifact> artifacts) throws OseeCoreException, SQLException;

}
