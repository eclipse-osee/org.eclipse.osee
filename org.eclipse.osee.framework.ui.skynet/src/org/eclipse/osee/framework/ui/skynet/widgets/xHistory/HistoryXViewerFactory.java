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
package org.eclipse.osee.framework.ui.skynet.widgets.xHistory;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Jeff C. Phillips
 */
public class HistoryXViewerFactory extends SkynetXViewerFactory {
   public static XViewerColumn transaction =
         new XViewerColumn("framework.history.transaction", "Transaction", 100, SWT.LEFT, true, SortDataType.String,
               false, null);
   public static XViewerColumn gamma =
         new XViewerColumn("framework.history.gamma", "Gamma", 70, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn itemType =
         new XViewerColumn("framework.history.itemType", "Item Type", 150, SWT.LEFT, true, SortDataType.String,
               false, null);
   public static XViewerColumn was =
         new XViewerColumn("framework.history.was", "Was", 250, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn is =
         new XViewerColumn("framework.history.is", "Is", 250, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn timeStamp =
         new XViewerColumn("framework.history.timeStamp", "Time Stamp", 110, SWT.LEFT, true, SortDataType.String,
               false, null);
   public static XViewerColumn author =
         new XViewerColumn("framework.history.author", "Author", 100, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn comment =
         new XViewerColumn("framework.history.comment", "Comment", 70, SWT.LEFT, true, SortDataType.String, false, null);

   public static String NAMESPACE = "osee.skynet.gui.HisotryXViewer";

   public HistoryXViewerFactory() {
      super(NAMESPACE);
      registerColumn(transaction, gamma, itemType, was, is, timeStamp, author, comment);
      registerAllAttributeColumns();
   }

   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

}
