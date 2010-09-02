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
package org.eclipse.osee.framework.skynet.core.event.filter;

import org.eclipse.osee.framework.core.model.event.IBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.IBasicGuidRelation;

/**
 * @author Donald G. Dunne
 */
public interface IEventFilter {

   /**
    * return true if events for this branch should be passed through to listeners
    */
   public boolean isMatch(String branchGuid);

   /**
    * return true if events for this this guid artifact should be passed through to listeners
    */
   public boolean isMatch(IBasicGuidArtifact guidArt);

   /**
    * return true if events for this guid relation should be passed through to listeners
    */
   public boolean isMatch(IBasicGuidRelation relArt);

}
