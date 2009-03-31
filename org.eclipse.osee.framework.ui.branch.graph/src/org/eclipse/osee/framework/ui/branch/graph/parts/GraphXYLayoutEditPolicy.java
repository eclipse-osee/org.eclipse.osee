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
package org.eclipse.osee.framework.ui.branch.graph.parts;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * @author Roberto E. Escobar
 */
public class GraphXYLayoutEditPolicy extends XYLayoutEditPolicy {

   /*
    * @see org.eclipse.gef.editpolicies.ConstrainedLayoutTEditPolicy#createChangeConstraintCommand(org.eclipse.gef.EditPart,
    *      java.lang.Object)
    */
   @Override
   protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
      return null;
   }

   /*
    * @see org.eclipse.gef.editpolicies.LayoutEditPolicy#getCreateCommand(org.eclipse.gef.requests.CreateRequest)
    */
   @Override
   protected Command getCreateCommand(CreateRequest request) {
      return null;
   }

}
