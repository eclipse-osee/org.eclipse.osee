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
import java.net.InetSocketAddress;

import org.eclipse.osee.ote.message.enums.DataType;
import org.eclipse.osee.ote.message.tool.MessageMode;


/**
 * @author Ken J. Aguilar
 */
public class SetMessageModeCmd implements Serializable{

   private static final long serialVersionUID = 4294009014724352978L;

   private final String name;
   private final DataType type;
   private final MessageMode oldMode;
   private final MessageMode newMode;
   private final InetSocketAddress address;
   
   public SetMessageModeCmd(String name, DataType type, MessageMode oldMode, MessageMode newMode, InetSocketAddress address) {
      this.name = name;
      this.type = type;
      this.oldMode = oldMode;
      this.newMode = newMode;
      this.address = address;
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
   public InetSocketAddress getAddress() {
      return address;
   }

   /**
    * @return Returns the type.
    */
   public DataType getType() {
      return type;
   }

   

}
