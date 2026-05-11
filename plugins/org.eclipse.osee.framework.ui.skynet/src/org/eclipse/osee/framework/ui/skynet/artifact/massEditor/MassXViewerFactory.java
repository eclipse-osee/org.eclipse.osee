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

package org.eclipse.osee.framework.ui.skynet.artifact.massEditor;

import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_A;
import static org.eclipse.osee.framework.core.enums.RelationSide.SIDE_B;
import static org.eclipse.osee.framework.ui.skynet.artifact.editor.action.XViewerRelatedArtifactsColumn.AS_TOKEN;
import java.util.Collection;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.OseeApiService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.action.XViewerRelatedArtifactsColumn;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.OseeTreeReportAdapter;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.SkynetXViewerFactory;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactNameColumnUI;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTokenColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ArtifactTypeColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.HierarchyIndexColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.IdColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedByColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedDateColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.LastModifiedTransactionCommentColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.MarkdownHtmlColumnUI;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.UserGroupsColumn;
import org.eclipse.osee.framework.ui.skynet.widgets.xviewer.skynet.column.ViewApplicabilityColumn;

/**
 * @author Donald G. Dunne
 */
public class MassXViewerFactory extends SkynetXViewerFactory {

   private static String NAMESPACE = "ArtifactXViewer";
   private static ArtifactNameColumnUI nameCol = new ArtifactNameColumnUI(true, true);

   public MassXViewerFactory(Collection<? extends Artifact> artifacts) {
      super(NAMESPACE, new OseeTreeReportAdapter("Table Report - Mass Editor"));
      registerColumns(nameCol);
      registerColumns(ArtifactTypeColumn.getInstance());
      registerColumns(HierarchyIndexColumn.getInstance());
      registerColumns(new IdColumn(true));
      registerColumns(new ViewApplicabilityColumn(true));
      registerColumns(new LastModifiedDateColumn(true));
      registerColumns(new LastModifiedByColumn(true));
      registerColumns(new LastModifiedTransactionColumn(true));
      registerColumns(new LastModifiedTransactionCommentColumn(true));
      registerColumns(new ArtifactTokenColumn());
      registerColumns(new UserGroupsColumn(false));
      registerAllAttributeColumnsForArtifacts(artifacts, true, true);
      registerColumns(MarkdownHtmlColumnUI.getInstance());
      registerRelatedColumnsBasedOnCustomizations();
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

   @Override
   public XViewerColumn getDefaultXViewerColumn(String id) {
      // Default to super; override only when we successfully resolve a relation column
      XViewerColumn xCol = super.getDefaultXViewerColumn(id);

      // If relation column, try to resolve, else keep default
      if (id.startsWith(XViewerRelatedArtifactsColumn.ID)) {

         // id = <relation id prefix>--<relTypeId>--<relTypeSide>--<AsToken or AsName>
         String[] split = id.split("--");
         if (split.length == 4) {
            String relTypeId = split[1];
            String relTypeSide = split[2];
            String asToken = split[3];

            if (Strings.isNumeric(relTypeId)) {
               Long relId = Long.valueOf(relTypeId);
               RelationTypeToken relationType = OseeApiService.tokenSvc().getRelationType(relId);

               if (relationType != null && (relTypeSide.equals(SIDE_A.name()) || relTypeSide.equals(SIDE_B.name()))) {
                  RelationTypeSide rts =
                     new RelationTypeSide(relationType, (relTypeSide.equals(SIDE_A.name()) ? SIDE_A : SIDE_B));
                  xCol = new XViewerRelatedArtifactsColumn(rts, asToken.equals(AS_TOKEN));
                  xCol.setName(id);
                  registerColumns(new XViewerRelatedArtifactsColumn(rts, true));
               }
            }
         } else {
            OseeLog.log(Activator.class, OseeLevel.WARNING,
               "Could not parse relation column id.\n\nNot showing column for: " + id);
         }
      }

      return xCol;
   }
}
