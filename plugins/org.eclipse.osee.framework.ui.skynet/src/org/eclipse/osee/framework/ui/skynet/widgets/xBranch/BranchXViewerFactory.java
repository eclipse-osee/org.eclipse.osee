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
import org.eclipse.nebula.widgets.xviewer.XViewerSorter;
import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Jeff C. Phillips
 */
public class BranchXViewerFactory extends SkynetXViewerFactory {

   public final static XViewerColumn branchName = new XViewerColumn("framework.branch.branchName", "Branch Name", 250,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn branchType =
      new XViewerColumn("framework.branch.type", "Type", 100, XViewerAlign.Left, true, SortDataType.String, true, null);
   public final static XViewerColumn branchState = new XViewerColumn("framework.branch.state", "State", 100,
      XViewerAlign.Left, true, SortDataType.String, true, null);
   public final static XViewerColumn createdDate = new XViewerColumn("framework.branch.itemType", "Created Date", 150,
      XViewerAlign.Left, true, SortDataType.Date, false, null);
   public final static XViewerColumn author = new XViewerColumn("framework.branch.author", "Author", 100,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn comment = new XViewerColumn("framework.branch.comment", "Comment", 250,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn associatedArtifact = new XViewerColumn("framework.branch.assocArt",
      "Associated Artifact", 100, XViewerAlign.Left, false, SortDataType.String, false, null);
   public final static XViewerColumn branchId = new XViewerColumn("framework.branch.uuid", "Branch Id", 200,
      XViewerAlign.Left, false, SortDataType.Long, false, null);
   public final static XViewerColumn parentBranch = new XViewerColumn("framework.branch.parentBranch", "Parent Branch",
      100, XViewerAlign.Left, false, SortDataType.String, false, null);
   public final static XViewerColumn archivedState = new XViewerColumn("framework.branch.archived", "Archived State",
      100, XViewerAlign.Left, false, SortDataType.String, true, null);
   public final static XViewerColumn inheritAccessControl = new XViewerColumn("framework.branch.inherit.access.control",
      "Inherit Access Control", 100, XViewerAlign.Left, false, SortDataType.Boolean, false, null);
   public final static XViewerColumn transaction = new XViewerColumn("framework.branch.transaction", "Transaction", 100,
      XViewerAlign.Left, true, SortDataType.Integer, false, null);
   public final static XViewerColumn branchAccessContextId = new XViewerColumn("framework.branch.access",
      "Branch Access Context Id", 100, XViewerAlign.Left, false, SortDataType.String, false, null);

   public final static String NAMESPACE = "osee.skynet.gui.BranchXViewer";

   public BranchXViewerFactory(IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
      registerColumns(branchName, branchType, branchState, createdDate, author, comment, associatedArtifact, parentBranch,
         branchId, archivedState, branchAccessContextId, inheritAccessControl);
   }

   @Override
   public XViewerSorter createNewXSorter(XViewer xViewer) {
      return new XViewerSorter(xViewer);
   }

   public boolean isBranchManager() {
      return true;
   }

}
