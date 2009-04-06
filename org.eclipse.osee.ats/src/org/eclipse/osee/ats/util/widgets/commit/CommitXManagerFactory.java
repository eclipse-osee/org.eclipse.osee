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

   public static XViewerColumn Type_Col =
         new XViewerColumn("osee.commit.type", "Type", 100, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Action_Col =
         new XViewerColumn("osee.commit.action", "Action", 100, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Status_Col =
         new XViewerColumn("osee.commit.status", "Status", 100, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Name_Col =
         new XViewerColumn("osee.commit.name", "Name", 450, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn Short_Name_Col =
         new XViewerColumn("osee.commit.shortName", "Short Name", 200, SWT.LEFT, true, SortDataType.String, false, null);

   public CommitXManagerFactory() {
      super("osee.skynet.gui.CommitXViewer");
      registerColumn(Type_Col, Status_Col, Action_Col, Name_Col, Short_Name_Col);
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations#isFilterUiAvailable()
    */
   @Override
   public boolean isFilterUiAvailable() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations#isHeaderBarAvailable()
    */
   @Override
   public boolean isHeaderBarAvailable() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations#isLoadedStatusLabelAvailable()
    */
   @Override
   public boolean isLoadedStatusLabelAvailable() {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations#isSearchUiAvailable()
    */
   @Override
   public boolean isSearchUiAvailable() {
      return false;
   }

}
