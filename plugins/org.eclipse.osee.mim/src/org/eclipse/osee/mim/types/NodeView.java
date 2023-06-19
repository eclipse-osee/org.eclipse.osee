/*********************************************************************
 * Copyright (c) 2021 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.mim.types;

/**
 * @author Luciano T. Vaglienti
 */
public class NodeView {

   private String id = "";
   private String label = "";
   private InterfaceNode data;

   public NodeView(InterfaceNode node) {
      this(node.getIdString(), node.getName());
      this.setLabel(node.getName());
      this.setData(node);
   }

   public NodeView(String id, String name) {
      this.setId(id);
   }

   public NodeView() {
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   /**
    * @return the label
    */
   public String getLabel() {
      return label;
   }

   /**
    * @param label the label to set
    */
   public void setLabel(String label) {
      this.label = label;
   }

   /**
    * @return the data
    */
   public InterfaceNode getData() {
      return data;
   }

   /**
    * @param data the data to set
    */
   public void setData(InterfaceNode data) {
      this.data = data;
   }

}
