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
package org.eclipse.osee.framework.core.data;

import java.util.ArrayList;

/**
 * @author Jeff C. Phillips
 */
public class ChangeReportResponse {

   private final ArrayList<ChangeItem> changeItems;

   public ArrayList<ChangeItem> getChangeItems() {
      return changeItems;
   }

   public ChangeReportResponse(ArrayList<ChangeItem> changeItems) {
      super();
      this.changeItems = changeItems;
   }

   public boolean wasSuccessful() {
      return !changeItems.isEmpty();
   }
}
