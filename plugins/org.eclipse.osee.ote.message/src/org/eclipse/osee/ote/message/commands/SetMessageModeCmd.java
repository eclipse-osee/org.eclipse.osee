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
package org.eclipse.osee.ote.message.commands;

import java.io.Serializable;
import org.eclipse.osee.ote.message.enums.MemType;
import org.eclipse.osee.ote.message.interfaces.IMsgToolServiceClient;
import org.eclipse.osee.ote.message.tool.MessageMode;


/**
 * @author Ken J. Aguilar
 */
public class SetMessageModeCmd implements Serializable{

   private static final long serialVersionUID = 4294009014724352978L;

   private final String name;
   private final MemType type;
   private final MessageMode oldMode;
   private final MessageMode newMode;
   private final IMsgToolServiceClient client;
   
   public SetMessageModeCmd(String name, MemType type, MessageMode oldMode, MessageMode newMode, IMsgToolServiceClient client) {
      this.name = name;
      this.type = type;
      this.oldMode = oldMode;
      this.newMode = newMode;
      this.client = client;
   }

   /**
    * @return Returns the serialVersionUID.
    */
   public static long getSerialVersionUID() {
      return serialVersionUID;
   }

   /**
    * @return Returns the name.
    */
   public String getName() {
      return name;
   }

   /**
    * @return Returns the newMode.
    */
   public MessageMode getNewMode() {
      return newMode;
   }

   /**
    * @return Returns the oldMode.
    */
   public MessageMode getOldMode() {
      return oldMode;
   }

   /**
    * @return Returns the client.
    */
   public IMsgToolServiceClient getClient() {
      return client;
   }

   /**
    * @return Returns the type.
    */
   public MemType getType() {
      return type;
   }

   

}
