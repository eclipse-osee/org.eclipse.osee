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
package org.eclipse.osee.framework.ui.data.model.editor.policy;

import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.osee.framework.ui.data.model.editor.figure.SelectableLabel;

/**
 * @author Roberto E. Escobar
 */
public class LabelSelectionEditPolicy extends NonResizableEditPolicy {

   protected SelectableLabel getLabel() {
      return (SelectableLabel) getHostFigure();
   }

   /**
    * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#hideFocus()
    */
   @Override
   protected void hideFocus() {
      getLabel().setFocus(false);
   }

   /**
    * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#hideSelection()
    */
   @Override
   protected void hideSelection() {
      getLabel().setSelected(false);
      getLabel().setFocus(false);

   }

   /**
    * @see org.eclipse.gef.editpolicies.NonResizableEditPolicy#showFocus()
    */
   @Override
   protected void showFocus() {
      getLabel().setFocus(true);
   }

   /**
    * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#showSelection()
    */
   @Override
   protected void showPrimarySelection() {
      getLabel().setSelected(true);
      getLabel().setFocus(true);
   }

   /**
    * @see org.eclipse.gef.editpolicies.SelectionHandlesEditPolicy#showSelection()
    */
   @Override
   protected void showSelection() {
      getLabel().setSelected(true);
      getLabel().setFocus(false);
   }
}
