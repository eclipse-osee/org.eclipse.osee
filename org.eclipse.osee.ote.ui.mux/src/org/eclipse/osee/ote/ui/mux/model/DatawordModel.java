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
package org.eclipse.osee.ote.ui.mux.model;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.util.HashMap;
import org.eclipse.osee.ote.ui.mux.datatable.DataNode;

/**
 * @author Ky Komadino
 *
 */
public class DatawordModel {
   private final static Object[] EMPTY_ARRAY = new Object[0];
   private HashMap<String, DataNode> dataNodes;
   private final CharBuffer buffer = ByteBuffer.allocate(16).asCharBuffer();
   private String currentNode = null;
   
   public DatawordModel() {
      dataNodes = new HashMap<String, DataNode>();
   };
   
   /**
    * @param muxId - message ID
    * @param node - node to add to list
    */
   public void addNode(String muxId, DataNode node) {
      dataNodes.put(muxId, node);
   }
   
   /**
    * 
    * @param node - currently selected node
    */
   public void setCurrentNode(String node) {
      currentNode = node;
   }
   
   /**
    * @return - values in list
    */
   public Object[] getChildren() {
      if (currentNode != null) {
         DataNode node = dataNodes.get(currentNode);
         Object nodes[] = new Object[] {node.getRow(1), node.getRow(2), node.getRow(3), node.getRow(4)};
         return nodes;
      }
      return EMPTY_ARRAY;
   }
   
   public void removeDatawords() {
      dataNodes.clear();
   }
   
   public void onDataAvailable(ByteBuffer data) {
      buffer.clear();
      buffer.append(String.format("%02d", ((short)(data.array()[1] & 0x00F8)) >> 3));
      final char transmitReceive = (data.array()[1] & 0x04) >> 2 == 1 ? 'T' : 'R';
      buffer.append(transmitReceive);
      buffer.append(String.format("%02d", (((short)(data.array()[1] & 0x0003)) << 3) +
                                          (((short)(data.array()[2] & 0x00E0)) >> 5)));
      String muxId = buffer.flip().toString();
      DataNode receiveData = dataNodes.get(muxId);
      if (receiveData == null) {
         receiveData = new DataNode();
         addNode(muxId, receiveData);
      }
      receiveData.setData(data);
   }
}
