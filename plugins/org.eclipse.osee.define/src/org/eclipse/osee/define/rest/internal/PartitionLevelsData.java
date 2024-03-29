/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.define.rest.internal;

/**
 * @author David W. Miller
 */

// pojo for storing level data and converting from json
public class PartitionLevelsData {
   private String partition;
   private String level;

   public String getPartition() {
      return partition;
   }

   public void setPartition(String partition) {
      this.partition = partition;
   }

   public String getLevel() {
      return level;
   }

   public void setLevel(String level) {
      this.level = level;
   }

}