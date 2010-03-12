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
package org.eclipse.osee.ote.message.tool;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
public final class TransferConfig {
   public static enum Direction {
      SOCKET_TO_FILE(SelectionKey.OP_READ),
      FILE_TO_SOCKET(SelectionKey.OP_WRITE);
      
      private int accessType;

      /**
       * @param accessType
       */
      private Direction(int accessType) {
         this.accessType = accessType;
      }
      
      public int getSelectionAccessOperation() {
         return accessType;
      }
      
   }
   private final String fileName;
   private final InetSocketAddress sourceAddress;
   private final InetSocketAddress destinationAddress;
   private final Direction direction;
   private final int blockCount;
   /**
    * @param fileChannel
    * @param sourceAddress
    * @param port
    * @param direction
    */
   public TransferConfig(
         final String fileName, 
         final InetSocketAddress sourceAddress, 
         final InetSocketAddress destinationAddress, 
         final Direction direction,
         final int blockCount) {
      super();
      this.fileName = fileName;
      this.sourceAddress = sourceAddress;
      this.destinationAddress = destinationAddress;
      this.direction = direction;
      this.blockCount = blockCount;
      
   }
   /**
    * @return the direction
    */
   public Direction getDirection() {
      return direction;
   }
   /**
    * @return the fileChannel
    */
   public String getFileName() {
      return fileName;
   }
   /**
    * @return the destination of the data
    */
   public InetSocketAddress getDestinationAddress() {
      return destinationAddress;
   }
   /**
    * @return the sourceAddress
    */
   public InetSocketAddress getSourceAddress() {
      return sourceAddress;
   }
   /**
    * @return the blockCount
    */
   public int getBlockCount() {
      return blockCount;
   }

   
}