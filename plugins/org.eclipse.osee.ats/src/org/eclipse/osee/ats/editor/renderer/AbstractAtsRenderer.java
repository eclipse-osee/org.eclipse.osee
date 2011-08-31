/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.editor.renderer;

import org.eclipse.osee.framework.ui.skynet.render.DefaultArtifactRenderer;
import org.eclipse.osee.framework.ui.skynet.render.IRenderer;

public abstract class AbstractAtsRenderer extends DefaultArtifactRenderer {

   protected AbstractAtsRenderer() {
      super();
   }

   @Override
   public int minimumRanking() {
      return IRenderer.GENERAL_MATCH;
   }
}
