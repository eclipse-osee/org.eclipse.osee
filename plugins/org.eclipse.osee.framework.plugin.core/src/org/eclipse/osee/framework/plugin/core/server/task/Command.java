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
package org.eclipse.osee.framework.plugin.core.server.task;

import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * @author Ryan D. Brooks
 */
public abstract class Command {
   private final int commandId;

   public Command(int commandId) {
      this.commandId = commandId;
   }

   public void sendCommand(ObjectOutputStream toServer, Object... parameters) throws IOException {
      toServer.writeInt(commandId);
      toServer.writeByte((byte) parameters.length); // number of parameters

      for (Object parameter : parameters) {
         writeParameter(toServer, parameter);
      }

      toServer.flush();
   }

   private void writeParameter(ObjectOutputStream toServer, Object parameter) throws IOException {
      if (parameter instanceof Boolean) {
         toServer.writeByte((byte) Parameter.BOOLEAN.ordinal());
         toServer.writeByte(((Boolean) parameter) ? 1 : 0);
      }
      if (parameter instanceof Byte) {
         toServer.writeByte((byte) Parameter.BYTE.ordinal());
         toServer.writeByte((Byte) parameter);
      }
      if (parameter instanceof Short) {
         toServer.writeByte((byte) Parameter.SHORT.ordinal());
         toServer.writeShort((Short) parameter);
      }
      if (parameter instanceof Character) {
         toServer.writeByte((byte) Parameter.CHAR.ordinal());
         toServer.writeChar((Character) parameter);
      }
      if (parameter instanceof Integer) {
         toServer.writeByte((byte) Parameter.INT.ordinal());
         toServer.writeInt((Integer) parameter);
      }
      if (parameter instanceof Long) {
         toServer.writeByte((byte) Parameter.LONG.ordinal());
         toServer.writeLong((Long) parameter);
      }
      if (parameter instanceof Float) {
         toServer.writeByte((byte) Parameter.FLOAT.ordinal());
         toServer.writeFloat((Float) parameter);
      }
      if (parameter instanceof Double) {
         toServer.writeByte((byte) Parameter.DOUBLE.ordinal());
         toServer.writeDouble((Double) parameter);
      }
      if (parameter instanceof String) {
         toServer.writeByte((byte) Parameter.STRING.ordinal());
         toServer.writeUTF((String) parameter);
      }
      /*      if (parameter.getClass().isArray()) {
               for (Object obj : parameter)  {
                  toServer.writeShort(Array.getLength(obj));  // write array length
                  writeParameter(toServer, obj);
               }
            }*/
   }

   public abstract Object invoke(Object... parameters) throws Exception;
}