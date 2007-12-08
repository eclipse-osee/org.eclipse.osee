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
package org.eclipse.osee.framework.jdk.core.reportdata;

import java.io.Serializable;
import java.util.ArrayList;
import org.eclipse.osee.framework.jdk.core.util.GUID;

public class ReportDataItem implements Serializable {

   private static final long serialVersionUID = 7072248922173369711L;

   private GUID guid;
   private ArrayList<String> cells;

   public ReportDataItem(GUID guid, ArrayList<String> items) {
      this.guid = guid;
      this.cells = items;
   }

   /**
    * @return Returns the guid.
    */
   public GUID getGuid() {
      return guid;
   }

   /**
    * @return Returns the cells.
    */
   public ArrayList<String> getCells() {
      return cells;
   }
}
