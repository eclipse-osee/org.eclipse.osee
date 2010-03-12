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
package org.eclipse.osee.ats.util.widgets.commit;

import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class CommitXManagerFactory extends SkynetXViewerFactory {

   public static XViewerColumn Empty_Col =
         new XViewerColumn("osee.commit.empty", "Empty", 0, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Action_Col =
         new XViewerColumn("osee.commit.action", "Action", 180, SWT.LEFT, true, SortDataType.String, false,
               "Provides the action(s) available.  Double click row to perform action.");
   public static XViewerColumn Status_Col =
         new XViewerColumn("osee.commit.status", "Status", 180, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Merge_Col =
         new XViewerColumn("osee.commit.merge", "Merge Branch Exists", 30, SWT.LEFT, true, SortDataType.String, false,
               "Will show merge icon if merge branch exists");
   public static XViewerColumn Dest_Branch_Col =
         new XViewerColumn("osee.commit.name", "Destination Branch", 450, SWT.LEFT, true, SortDataType.String, false,
               null);
   public static XViewerColumn Version_Col =
         new XViewerColumn("osee.commit.shortName", "Destination Version", 200, SWT.LEFT, true, SortDataType.String,
               false, null);
   public static XViewerColumn Configuring_Object_Col =
         new XViewerColumn("osee.commit.configObj", "Configuring Object", 200, SWT.LEFT, true, SortDataType.String,
               false, null);

   public CommitXManagerFactory() {
      super("osee.skynet.gui.CommitXViewer");
      registerColumns(Empty_Col, Action_Col, Status_Col, Merge_Col, Dest_Branch_Col, Version_Col, Configuring_Object_Col);
   }

   @Override
   public boolean isFilterUiAvailable() {
      return false;
   }

   @Override
   public boolean isHeaderBarAvailable() {
      return false;
   }

   @Override
   public boolean isLoadedStatusLabelAvailable() {
      return false;
   }

   @Override
   public boolean isSearchUiAvailable() {
      return false;
   }

}
