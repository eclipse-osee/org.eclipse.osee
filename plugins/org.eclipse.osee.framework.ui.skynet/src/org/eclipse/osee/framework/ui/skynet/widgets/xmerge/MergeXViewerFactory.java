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
package org.eclipse.osee.framework.ui.skynet.widgets.xmerge;

import org.eclipse.nebula.widgets.xviewer.core.model.SortDataType;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerAlign;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.IOseeTreeReportProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;

/**
 * @author Donald G. Dunne
 */
public class MergeXViewerFactory extends SkynetXViewerFactory {

   public final static String NAMESPACE = "MergeXViewer";

   public final static XViewerColumn Conflict_Resolved = new XViewerColumn("framework.merge.conflictResolved",
      "Conflict Resolution", 43, XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Artifact_Name = new XViewerColumn("framework.merge.artifactName", "Artifact Name",
      200, XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Type = new XViewerColumn("framework.merge.artifactType", "Artifact Type", 150,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Change_Item = new XViewerColumn("framework.merge.conflictingItem",
      "Conflicting Item", 150, XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Source = new XViewerColumn("framework.merge.sourceValue", "Source Value", 100,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Destination = new XViewerColumn("framework.merge.destinationValue",
      "Destination Value", 100, XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Merged = new XViewerColumn("framework.merge.mergedValue", "Merged Value", 100,
      XViewerAlign.Left, true, SortDataType.String, false, null);
   public final static XViewerColumn Art_Id = new XViewerColumn("framework.merge.artId", "Artifact Id", 75,
      XViewerAlign.Left, true, SortDataType.String, false, null);

   public MergeXViewerFactory(IOseeTreeReportProvider reportProvider) {
      super(NAMESPACE, reportProvider);
      registerColumns(Conflict_Resolved, Artifact_Name, Type, Change_Item, Source, Destination, Merged, Art_Id);
      registerAllAttributeColumns();
   }

   @Override
   public XViewerCustomMenu getXViewerCustomMenu() {
      return new MergeCustomMenu();
   }

}
