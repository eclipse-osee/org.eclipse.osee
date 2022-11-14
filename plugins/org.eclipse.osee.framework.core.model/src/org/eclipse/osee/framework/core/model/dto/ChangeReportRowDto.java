/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.framework.core.model.dto;

/**
 * @author Ryan T. Baldwin
 */
public class ChangeReportRowDto {

   private String ids = "";
   private String names = "";
   private String itemType = "";
   private String itemKind = "";
   private String changeType = "";
   private String isValue = "";
   private String wasValue = "";

   public ChangeReportRowDto() {
   }

   public ChangeReportRowDto(String ids, String names, String itemType, String itemKind, String changeType, String isValue, String wasValue) {
      this.ids = ids;
      this.names = names;
      this.itemType = itemType;
      this.itemKind = itemKind;
      this.changeType = changeType;
      this.isValue = isValue;
      this.wasValue = wasValue;
   }

   public String getIds() {
      return ids;
   }

   public void setIds(String ids) {
      this.ids = ids;
   }

   public String getNames() {
      return names;
   }

   public void setNames(String names) {
      this.names = names;
   }

   public String getItemType() {
      return itemType;
   }

   public void setItemType(String itemType) {
      this.itemType = itemType;
   }

   public String getItemKind() {
      return itemKind;
   }

   public void setItemKind(String itemKind) {
      this.itemKind = itemKind;
   }

   public String getChangeType() {
      return changeType;
   }

   public void setChangeType(String changeType) {
      this.changeType = changeType;
   }

   public String getIsValue() {
      return isValue;
   }

   public void setIsValue(String isValue) {
      this.isValue = isValue;
   }

   public String getWasValue() {
      return wasValue;
   }

   public void setWasValue(String wasValue) {
      this.wasValue = wasValue;
   }

}