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
package org.eclipse.osee.ats.ide.workflow.cr.sibling;

import org.eclipse.osee.ats.ide.world.mini.MiniWorldXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class XSiblingXViewerFactory extends MiniWorldXViewerFactory {

   public final static String NAMESPACE = "SiblingXViewer";

   public XSiblingXViewerFactory() {
      super(NAMESPACE);
   }

}
