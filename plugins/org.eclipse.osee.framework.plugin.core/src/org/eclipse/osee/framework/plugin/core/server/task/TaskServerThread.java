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

import static org.eclipse.osee.framework.plugin.core.server.task.Parameter.BYTE;
import static org.eclipse.osee.framework.plugin.core.server.task.Parameter.CHAR;
import static org.eclipse.osee.framework.plugin.core.server.task.Parameter.DOUBLE;
import static org.eclipse.osee.framework.plugin.core.server.task.Parameter.INT;
import static org.eclipse.osee.framework.plugin.core.server.task.Parameter.LONG;
import static org.eclipse.osee.framework.plugin.core.server.task.Parameter.SHORT;
import static org.eclipse.osee.framework.plugin.core.server.task.Parameter.STRING;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;

/**
 * Protocol 2-byte 1-byte 1-byte n-bytes <command_id> <n_params> [<type_id> <parma_value>]* For each type, the protocol
 * explicitly defines the format of the paramater value.
 *
 * @author Ryan D. Brooks
 */
public class TaskServerThread implements Runnable {
   private final int magicNumber;
   private Socket socket = null;
   private ObjectInputStream inFromClient;
   private PrintWriter out;
   private final HashMap<Integer, Command> commands;
   private final boolean running;

   public TaskServerThread(int magicNumber, Socket socket) {
      this.socket = socket;
      this.magicNumber = magicNumber;
      this.commands = new HashMap<>();
      running = true;
   }

   @Override
   public void run() {
      try {
         out = new PrintWriter(socket.getOutputStream(), true);
         inFromClient = new ObjectInputStream(socket.getInputStream()); // this is a blocking call

         int code = inFromClient.readInt();
         if (code != magicNumber) {
            System.out.println("look what you did!");
            return;
         }
         while (running) {
            try {
               int commandId = inFromClient.readUnsignedShort();
               Object[] parameters = parseParameters();

               Command command = commands.get(commandId);
               sendResultToClient(command.invoke(parameters));
            } catch (Exception ex) {
               System.out.println(ex);
            }
         }

         inFromClient.close();
         out.close();
         socket.close();
      } catch (IOException ex) {
         ex.printStackTrace();
      }
   }

   private void sendResultToClient(Object result) {
      if (result == null) {
         return;
      }
   }

   public void registerCommand(int commandId, Command command) {
      commands.put(commandId, command);
   }

   /*
    * Protocol 2-byte 1-byte 1-byte n-bytes <command_id> <n_params> [<type_id> <param_value>]
    */
   private Object[] parseParameters() throws IOException {
      int parametersCount = inFromClient.readUnsignedByte();
      if (parametersCount == 0) {
         return null;
      }
      Object[] parameters = new Object[parametersCount];

      for (int i = 0; i < parametersCount; i++) {
         parameters[i] = readNextParameter();
      }
      return parameters;
   }

   private Object readNextParameter() throws IOException {
      return readNextParameter(Parameter.values()[inFromClient.readUnsignedByte()]);
   }

   /**
    * type type_id format boolean 0 unsigned byte byte 1 signed byte short 2 signed 2-bytes char 3 unsigned 2-bytes int
    * 4 signed 4-bytes long 5 signed 8-bytes float 6 4-bytes double 7 8-bytes string 8 unsigned 2-byte len, n-bytes char
    * data boolean_array 9 unsigned 2-byte array element count, element data byte_array 10 unsigned 2-byte array element
    * count, element data short_array 11 unsigned 2-byte array element count, element data char_array 12 unsigned 2-byte
    * array element count, element data int_array 13 unsigned 2-byte array element count, element data long_array 14
    * unsigned 2-byte array element count, element data float_array 15 unsigned 2-byte array element count, element data
    * double_array 16 unsigned 2-byte array element count, element data string_array 17 unsigned 2-byte array element
    * count, element data
    *
    * @return Return next parameter reference
    */
   private Object readNextParameter(Parameter typeId) {
      try {
         switch (typeId) {
            case BOOLEAN:
               return inFromClient.readBoolean();
            case BYTE:
               return inFromClient.readByte();
            case SHORT:
               return inFromClient.readShort();
            case CHAR:
               return inFromClient.readChar();
            case INT:
               return inFromClient.readInt();
            case LONG:
               return inFromClient.readLong();
            case FLOAT:
               return inFromClient.readFloat();
            case DOUBLE:
               return inFromClient.readDouble();
            case STRING:
               return inFromClient.readUTF();
            case BYTE_ARRAY:
               byte[] byteArray = new byte[inFromClient.readUnsignedShort()];
               for (int i = 0; i < byteArray.length; i++) {
                  byteArray[i] = (Byte) readNextParameter(BYTE);
               }
               return byteArray;
            case SHORT_ARRAY:
               short[] shortArray = new short[inFromClient.readUnsignedShort()];
               for (int i = 0; i < shortArray.length; i++) {
                  shortArray[i] = (Short) readNextParameter(SHORT);
               }
               return shortArray;
            case CHAR_ARRAY:
               char[] charArray = new char[inFromClient.readUnsignedShort()];
               for (int i = 0; i < charArray.length; i++) {
                  charArray[i] = (Character) readNextParameter(CHAR);
               }
               return charArray;
            case INT_ARRAY:
               int[] intArray = new int[inFromClient.readUnsignedShort()];
               for (int i = 0; i < intArray.length; i++) {
                  intArray[i] = (Integer) readNextParameter(INT);
               }
               return intArray;
            case LONG_ARRAY:
               long[] longArray = new long[inFromClient.readUnsignedShort()];
               for (int i = 0; i < longArray.length; i++) {
                  longArray[i] = (Long) readNextParameter(LONG);
               }
               return longArray;
            case FLOAT_ARRAY:
               int[] floatArray = new int[inFromClient.readUnsignedShort()];
               for (int i = 0; i < floatArray.length; i++) {
                  floatArray[i] = (Integer) readNextParameter(INT);
               }
               return floatArray;
            case DOUBLE_ARRAY:
               double[] doubleArray = new double[inFromClient.readUnsignedShort()];
               for (int i = 0; i < doubleArray.length; i++) {
                  doubleArray[i] = (Double) readNextParameter(DOUBLE);
               }
               return doubleArray;
            case STRING_ARRAY:
               String[] stringArray = new String[inFromClient.readUnsignedShort()];
               for (int i = 0; i < stringArray.length; i++) {
                  stringArray[i] = (String) readNextParameter(STRING);
               }
               return stringArray;
            default:
               throw new IllegalArgumentException("invalid parameter type id");
         }
      } catch (IOException ex) {
         ex.printStackTrace();
         return null;
      }
   }
}