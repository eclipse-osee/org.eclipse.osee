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

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.osee.framework.ui.data.model.editor.command.CreateBendpointCommand;
import org.eclipse.osee.framework.ui.data.model.editor.command.DeleteBendpointCommand;
import org.eclipse.osee.framework.ui.data.model.editor.command.MoveBendpointCommand;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;

/**
 * @author Roberto E. Escobar
 */
public class LinkBendpointEditPolicy extends BendpointEditPolicy {

   protected Command getCreateBendpointCommand(BendpointRequest request) {
      Point loc = request.getLocation();
      getConnection().translateToRelative(loc);
      return new CreateBendpointCommand((ConnectionModel) request.getSource().getModel(), loc, request.getIndex());
   }

   protected Command getDeleteBendpointCommand(BendpointRequest request) {
      return new DeleteBendpointCommand((ConnectionModel) getHost().getModel(), request.getIndex());
   }

   protected Command getMoveBendpointCommand(BendpointRequest request) {
      Point loc = request.getLocation();
      getConnection().translateToRelative(loc);
      return new MoveBendpointCommand((ConnectionModel) request.getSource().getModel(), loc, request.getIndex());
   }

}
