/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.framework.core.model.tabledataframework.example;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.osee.framework.core.model.tabledataframework.ColumnAbstract;

/**
 * @author Shawn F. Cook
 */
public class ColumnErrors extends ColumnAbstract {

   private final static String HEADER_STRING = "Errors (CSV)";
   private final Collection<String> msgs = new ArrayList<>();

   public ColumnErrors(boolean isVisible) {
      super(HEADER_STRING, isVisible);
   }

   @Override
   public Object getData() {
      StringBuilder sb = new StringBuilder();
      for (String msg : msgs) {
         sb.append(msg);
         sb.append(", ");
      }
      sb.append(" ");
      return sb.toString();
   }

   public void addErrorMessage(String msg) {
      msgs.add(msg);
   }

   public void addAllErrorMessages(Collection<String> newMsgs) {
      msgs.addAll(newMsgs);
   }

   public Collection<String> getAllErrorMsgs() {
      return msgs;
   }

   public void clearAllMsgs() {
      msgs.clear();
   }
}
