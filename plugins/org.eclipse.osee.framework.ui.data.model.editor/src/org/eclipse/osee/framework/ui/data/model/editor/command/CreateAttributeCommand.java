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

import org.eclipse.gef.commands.Command;
import org.eclipse.osee.framework.ui.data.model.editor.model.ArtifactDataType;
import org.eclipse.osee.framework.ui.data.model.editor.model.AttributeDataType;

/**
 * @author Roberto E. Escobar
 */
public class CreateAttributeCommand extends Command {

   private AttributeDataType attribute;
   private ArtifactDataType parent;

   public CreateAttributeCommand(AttributeDataType attribute, ArtifactDataType parent) {
      super("Create Attribute");
      this.attribute = attribute;
      this.parent = parent;
   }

   public boolean canExecute() {
      return attribute != null && parent != null;
   }

   public void execute() {
      redo();
   }

   public void redo() {
      parent.add(attribute);
   }

   public void undo() {
      parent.remove(attribute);
   }

}
