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
package org.eclipse.osee.framework.ui.skynet.widgets.xBranch;

import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Jeff C. Phillips
 */
public class BranchXViewerFactory extends SkynetXViewerFactory {
   public static XViewerColumn branch_name =
         new XViewerColumn("framework.branch.branchName", "Branch Name", 250, SWT.LEFT, true, SortDataType.String,
               false, null);
   public static XViewerColumn time_stamp =
         new XViewerColumn("framework.branch.itemType", "Time Stamp", 150, SWT.LEFT, true, SortDataType.String, false,
               null);
   public static XViewerColumn author =
         new XViewerColumn("framework.branch.author", "Author", 100, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn comment =
         new XViewerColumn("framework.branch.comment", "Comment", 250, SWT.LEFT, true, SortDataType.String, false, null);
   public static XViewerColumn associatedArtifact =
         new XViewerColumn("framework.branch.assocArt", "Associated Artifact", 100, SWT.LEFT, false,
               SortDataType.String, false, null);
   public static XViewerColumn branchState =
         new XViewerColumn("framework.branch.state", "State", 100, SWT.LEFT, false, SortDataType.String, false, null);
   public static XViewerColumn branchType =
         new XViewerColumn("framework.branch.type", "Type", 100, SWT.LEFT, false, SortDataType.String, false, null);

   public static String NAMESPACE = "osee.skynet.gui.BranchXViewer";

   public BranchXViewerFactory() {
      super(NAMESPACE);
      registerColumn(branch_name, time_stamp, author, comment, associatedArtifact, branchState, branchType);
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

}
