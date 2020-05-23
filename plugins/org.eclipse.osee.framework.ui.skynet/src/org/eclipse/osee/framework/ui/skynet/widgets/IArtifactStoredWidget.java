/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * Used by XWidgets that perform external data storage
 * 
 * @author Roberto E. Escobar
 */
public interface IArtifactStoredWidget {

   /**
    * @return the artifact
    */
   Artifact getArtifact();

   /**
    * Save data changes to artifact
    */
   public void saveToArtifact();

   /**
    * Revert changes to widget data back to what was in artifact
    */
   public void revert();

   /**
    * Return true if storage data different than widget data
    */
   public Result isDirty();

}
