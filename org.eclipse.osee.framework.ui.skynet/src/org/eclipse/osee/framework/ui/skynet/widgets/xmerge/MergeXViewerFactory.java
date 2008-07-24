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

import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.swt.SWT;

/**
 * @author Donald G. Dunne
 */
public class MergeXViewerFactory extends SkynetXViewerFactory {

   public static String COLUMN_NAMESPACE = "framework.change.";
   public static XViewerColumn Conflict_Resolved =
         new XViewerColumn(COLUMN_NAMESPACE + "conflictResolved", "Conflict Resolution", 43, SWT.LEFT, true,
               SortDataType.String, false, null);
   public static XViewerColumn Artifact_Name =
         new XViewerColumn(COLUMN_NAMESPACE + "artifactName", "Artifact Name", 200, SWT.LEFT, true,
               SortDataType.String, false, null);
   public static XViewerColumn Type =
         new XViewerColumn(COLUMN_NAMESPACE + "artifactType", "Artifact Type", 150, SWT.LEFT, true,
               SortDataType.String, false, null);
   public static XViewerColumn Change_Item =
         new XViewerColumn(COLUMN_NAMESPACE + "conflictingItem", "Conflicting Item", 150, SWT.LEFT, true,
               SortDataType.String, false, null);
   public static XViewerColumn Source =
         new XViewerColumn(COLUMN_NAMESPACE + "sourceValue", "Source Value", 100, SWT.LEFT, true, SortDataType.String,
               false, null);
   public static XViewerColumn Destination =
         new XViewerColumn(COLUMN_NAMESPACE + "destinationValue", "Destination Value", 100, SWT.LEFT, true,
               SortDataType.String, false, null);
   public static XViewerColumn Merged =
         new XViewerColumn(COLUMN_NAMESPACE + "mergedValue", "Merged Value", 100, SWT.LEFT, true, SortDataType.String,
               false, null);

   public MergeXViewerFactory() {
      super("osee.skynet.gui.MergeXViewer");
      registerColumn(Conflict_Resolved, Artifact_Name, Type, Change_Item, Source, Destination, Merged);
   }

}
