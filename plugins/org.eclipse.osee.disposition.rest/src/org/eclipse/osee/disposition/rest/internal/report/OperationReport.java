/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.disposition.rest.internal.report;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Angel Avila
 */

public class OperationReport {

   private final Map<String, String> itemToSummaryMap = new HashMap<String, String>();
   private final Set<String> newItems = new HashSet<String>();
   private String summary;
   private final Set<String> otherMessages = new HashSet<String>();

   public OperationReport() {

   }

   public String getSummary() {
      return summary;
   }

   public Set<String> getNewItems() {
      return newItems;
   }

   public Map<String, String> getItemToSummaryMap() {
      return itemToSummaryMap;
   }

   public Set<String> getOtherMessages() {
      return otherMessages;
   }

   public void setSummary(String summary) {
      this.summary = summary;
   }

   public void addOtherMessage(String message, Object... args) {
      otherMessages.add(String.format(message, args));
   }

   public void addNewItem(String name) {
      newItems.add(name);
   }

   public void addMessageForItem(String itemName, String message, Object... args) {
      String itemMessage = itemToSummaryMap.get(itemName);
      if (itemMessage == null) {
         itemMessage = "";
      }
      itemMessage = itemMessage.concat(String.format(message, args));

      itemToSummaryMap.put(itemName, itemMessage);
   }
}
