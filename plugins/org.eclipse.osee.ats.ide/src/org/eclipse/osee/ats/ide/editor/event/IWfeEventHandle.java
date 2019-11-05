/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.event;

import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;

/**
 * Implement to be notified of change to attr or rel type
 *
 * @author Donald G. Dunne
 */
public interface IWfeEventHandle {

   IAtsWorkItem getWorkItem();

   default void refresh() {
      // do nothing
   }

   default void refresh(ArtifactEvent artifactEvent) {
      // do nothing
   }

}
