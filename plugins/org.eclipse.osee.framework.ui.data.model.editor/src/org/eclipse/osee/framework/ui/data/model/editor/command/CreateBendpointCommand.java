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
package org.eclipse.osee.framework.ui.data.model.editor.command;

import org.eclipse.draw2d.AbsoluteBendpoint;
import org.eclipse.draw2d.Bendpoint;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;
import org.eclipse.osee.framework.ui.data.model.editor.model.ConnectionModel;

/**
 * @author Roberto E. Escobar
 */
public class CreateBendpointCommand extends Command {

   private final ConnectionModel link;
   private final Bendpoint point;
   private final int index = -1;

   public CreateBendpointCommand(ConnectionModel link, Point location, int index) {
      super("Create Bendpoint");
      this.link = link;
      point = new AbsoluteBendpoint(location);
   }

   @Override
   public void undo() {
      link.getBendpoints().remove(index);
   }

}
