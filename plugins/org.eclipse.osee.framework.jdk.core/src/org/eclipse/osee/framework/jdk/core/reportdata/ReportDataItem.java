/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.jdk.core.reportdata;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Roberto E. Escobar
 */
public class ReportDataItem implements Serializable {

   private static final long serialVersionUID = 7072248922173369711L;

   private final String guid;
   private final ArrayList<String> cells;

   public ReportDataItem(String guid, ArrayList<String> items) {
      this.guid = guid;
      this.cells = items;
   }

   public String getGuid() {
      return guid;
   }

   public ArrayList<String> getCells() {
      return cells;
   }
}
