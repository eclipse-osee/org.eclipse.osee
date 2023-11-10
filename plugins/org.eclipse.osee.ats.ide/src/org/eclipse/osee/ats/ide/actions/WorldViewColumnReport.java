/*********************************************************************
 * Copyright (c) 2022 Boeing
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

package org.eclipse.osee.ats.ide.actions;

import java.util.List;
import org.eclipse.nebula.widgets.xviewer.core.model.ColumnData;
import org.eclipse.nebula.widgets.xviewer.core.model.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.core.model.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.world.WorldComposite;
import org.eclipse.osee.ats.ide.world.WorldXViewerFactory;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class WorldViewColumnReport extends AbstractAtsAction {

   private final AtsApi atsApi;
   private XResultData rd;
   private final WorldComposite worldComposite;

   public WorldViewColumnReport(WorldComposite worldComposite) {
      super();
      this.worldComposite = worldComposite;
      setText("Generate World View Column Report");
      setImageDescriptor(ImageManager.getImageDescriptor(AtsImage.WORK_PACKAGE));
      atsApi = AtsApiService.get();
   }

   @Override
   public void runWithException() {
      rd = new XResultData();
      rd.log(getText());

      try {
         WorldXViewerFactory factory = (WorldXViewerFactory) worldComposite.getWorldXViewer().getXViewerFactory();
         IXViewerCustomizations customizations = factory.getXViewerCustomizations();
         List<CustomizeData> custDatas = customizations.getSavedCustDatas();
         for (CustomizeData cust : custDatas) {
            rd.logf("\n\nCust Data [%s] Namespace [%s]\n", cust.getName(), cust.getNameSpace());
            ColumnData colData = cust.getColumnData();
            for (XViewerColumn xCol : colData.getColumns()) {
               XViewerColumn column = factory.getDefaultXViewerColumn(xCol.getId());
               rd.logf("--- ColId [%s]\n", xCol.getId());
               if (column == null) {
                  rd.errorf("------ Invalid ColId [%s]\n", xCol.getId());
               }
            }
         }
      } catch (Exception ex) {
         rd.error(Lib.exceptionToString(ex));
      }

      XResultDataUI.report(rd, getText(), Manipulations.ERROR_WARNING_HEADER, Manipulations.ALL);
   }

}
