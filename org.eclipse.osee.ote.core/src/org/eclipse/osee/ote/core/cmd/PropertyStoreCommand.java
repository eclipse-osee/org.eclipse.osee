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
package org.eclipse.osee.ote.core.cmd;

import java.io.Serializable;
import org.eclipse.osee.framework.jdk.core.type.IPropertyStore;
import org.eclipse.osee.framework.jdk.core.type.PropertyStore;

/**
 * @author Andrew M. Finkbeiner
 *
 */
public class PropertyStoreCommand implements Command, Serializable {

   private static final long serialVersionUID = 7685494212621558501L;

   private CommandId commandId;
   private Source source;
   private IPropertyStore store;
   
   public PropertyStoreCommand(CommandId commandId, Source source){
      this.commandId = commandId;
      this.source = source;
      store = new PropertyStore("org.eclipse.osee.ote.core.cmd.PropertyStoreCommand");
   }
   
   public CommandId getId() {
      return commandId;
   }

   public Source getSource() {
      return source;
   }

   public IPropertyStore getPropertyStore(){
      return store;
   }
   
}
