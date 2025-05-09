/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.demo.workflow.pr;

import org.eclipse.osee.ats.ide.editor.tab.bit.XBitXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class XBitXViewerFactoryDemo extends XBitXViewerFactory {

   public XBitXViewerFactoryDemo() {
      super(NAMESPACE);
      registerColumns(Program_Col, Build_Col, State_Col, Id_Col, Cr_State_Col, Cr_Type_Col, Cr_Title_Col);
   }

}
