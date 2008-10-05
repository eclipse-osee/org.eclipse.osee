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
package org.eclipse.osee.framework.ui.skynet.widgets;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Result;

/**
 * Used by XWidgets that perform external data storage
 * 
 * @author Donald G. Dunne
 */
public interface IArtifactWidget {
   /**
    * Set artifact used as storage for this widget
    * 
    * @throws Exception TODO
    */
   public void setArtifact(Artifact artifact, String attrName) throws OseeCoreException;

   /**
    * Save data changes to artifact
    * 
    * @throws Exception TODO
    */
   public void saveToArtifact() throws OseeCoreException;

   /**
    * Revert changes to widget data back to what was in artifact
    * 
    * @throws Exception TODO
    */
   public void revert() throws OseeCoreException;

   /**
    * Return true if storage data different than widget data
    * 
    * @return
    * @throws Exception TODO
    */
   public Result isDirty() throws OseeCoreException;

}
