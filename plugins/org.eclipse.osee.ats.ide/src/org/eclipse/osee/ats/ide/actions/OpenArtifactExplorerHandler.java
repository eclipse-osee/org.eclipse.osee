/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;

/**
 * @author Donald G. Dunne
 */
public class OpenArtifactExplorerHandler extends AbstractHandler {

   @Override
   public Object execute(ExecutionEvent event) {
      ArtifactExplorer.exploreBranch(AtsClientService.get().getAtsBranch());
      return null;
   }
}