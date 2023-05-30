/*********************************************************************
 * Copyright (c) 2023 Boeing
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

import java.util.LinkedList;
import java.util.List;

/**
 * @author Ryan T. Baldwin
 */
public class ClusterView {

   private String id;
   private String label;
   private List<String> childNodeIds;

   public ClusterView() {
      setId("");
      setLabel("");
      setChildNodeIds(new LinkedList<>());
   }

   public ClusterView(String id, String label) {
      setId(id);
      setLabel(label);
      setChildNodeIds(new LinkedList<>());
   }

   public String getId() {
      return id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getLabel() {
      return label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public List<String> getChildNodeIds() {
      return childNodeIds;
   }

   public void setChildNodeIds(List<String> childNodeIds) {
      this.childNodeIds = childNodeIds;
   }

}
