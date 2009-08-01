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
package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactNameColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerGuidColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.XViewerHridColumn;

/**
 * @author Donald G. Dunne
 */
public class MassXViewerFactory extends SkynetXViewerFactory {

   private static String NAMESPACE = "org.eclipse.osee.framework.ui.skynet.massEditor.ArtifactXViewer";
   private static XViewerArtifactNameColumn nameCol = new XViewerArtifactNameColumn("Name");

   public MassXViewerFactory(Collection<? extends Artifact> artifacts) {
      super(NAMESPACE);
      registerColumns(nameCol);
      registerAllAttributeColumnsForArtifacts(artifacts, true);
      registerColumns(new XViewerHridColumn("ID"));
      registerColumns(new XViewerGuidColumn("GUID"));
      registerColumns(new XViewerArtifactTypeColumn("Artifact Type"));
   }

   @Override
   public CustomizeData getDefaultTableCustomizeData() {
      CustomizeData custData = new CustomizeData();
      custData.getSortingData().setSortingNames(nameCol.getId());
      custData.getColumnData().setColumns(getColumns());
      custData.setNameSpace(getNamespace());
      custData.setName("Artifacts");
      return custData;
   }

}
