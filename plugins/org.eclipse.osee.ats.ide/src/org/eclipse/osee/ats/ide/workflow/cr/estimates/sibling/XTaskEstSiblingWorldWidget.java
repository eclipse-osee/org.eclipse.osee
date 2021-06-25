/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.workflow.cr.estimates.sibling;

import org.eclipse.osee.ats.ide.workflow.cr.sibling.XSiblingWorldWidget;
import org.eclipse.osee.ats.ide.world.WorldLabelProvider;
import org.eclipse.osee.ats.ide.world.WorldXViewer;

/**
 * @author Donald G. Dunne
 */
public abstract class XTaskEstSiblingWorldWidget extends XSiblingWorldWidget {

   public XTaskEstSiblingWorldWidget() {
      super(new XTaskEstSiblingXViewerFactory());

   }

   @Override
   protected WorldLabelProvider createWorldLabelProvider(WorldXViewer worldXViewer) {
      return new XTaskEstSiblingLabelProvider(worldXViewer);
   }

}
