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
import org.eclipse.osee.framework.core.data.IRelationType;

/**
 * @author Donald G. Dunne
 */
public interface IArtifactCheck {

   /**
    * Checks set of artifacts for validation prior to delete
    * 
    * @return IStatus with a description of which artifact and why it can not be deleted or Status.OK_STATUS
    */
   IStatus isDeleteable(Collection<Artifact> artifacts) ;

   /**
    * Checks set of artifacts for validation prior to rename
    * 
    * @return IStatus with a description of which artifact and why it can not be renamed or Status.OK_STATUS
    */
   IStatus isRenamable(Collection<Artifact> artifacts) ;

   /**
    * Check that relation can be deleted from given artifact
    * 
    * @return IStatus with a description of which artifact and why relation can not be deleted or Status.OK_STATUS
    */
   IStatus isDeleteableRelation(Artifact artifact, IRelationType relationType) ;

}
