/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;

/**
 * @author Donald G. Dunne
 */
public class OpenArtifactExplorerHandler extends AbstractHandler {

   @Override
   public Object execute(ExecutionEvent event) {
      ArtifactExplorer.exploreBranch(AtsApiService.get().getAtsBranch());
      return null;
   }
}