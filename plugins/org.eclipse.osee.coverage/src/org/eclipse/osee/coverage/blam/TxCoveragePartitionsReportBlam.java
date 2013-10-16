/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.coverage.blam;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.nebula.widgets.xviewer.Activator;
import org.eclipse.osee.coverage.store.CoverageArtifactTypes;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.widgets.XBranchSelectWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XList;
import org.eclipse.osee.framework.ui.skynet.widgets.XList.XListItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.SwtXWidgetRenderer;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Shawn F. Cook
 */
public class TxCoveragePartitionsReportBlam extends AbstractBlam {

   private static final String GREEDYCB_WIDGET_NAME = "Analyze ALL artifacts? Note: this will take a long time";
   private static final String BRANCH_WIDGET_NAME = "Select a branch";
   private static final String TXID_WIDGET_NAME = "Transaction ID";
   private XList txIdListWidget = null;
   private XBranchSelectWidget branchWidget = null;
   private final String SELECT_TXS_BY_BRANCH =
      "select distinct txs.transaction_id, txds.osee_comment, txds.time  from osee_attribute attr, osee_txs txs, osee_tx_details txds where txs.branch_id = ? and attr.gamma_id = txs.gamma_id and txs.transaction_id = txds.transaction_id order by txs.transaction_id";
   private final String SELECT_ARTS_BY_BRANCH_AND_TX =
      "select distinct attr.art_id  from osee_attribute attr, osee_txs txs where txs.branch_id = ? and txs.transaction_id = ? and attr.gamma_id = txs.gamma_id order by txs.transaction_id";
   private final String TASKITEMDELIM = "  - ";

   @Override
   public String getXWidgetsXml() {
      StringBuilder builder = new StringBuilder();
      builder.append("<xWidgets>");
      builder.append("<XWidget xwidgetType=\"XCheckBox\" displayName=\"" + GREEDYCB_WIDGET_NAME + "\" />");
      builder.append("<XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"" + BRANCH_WIDGET_NAME + "\"/>");
      builder.append("<XWidget xwidgetType=\"XList()\" displayName=\"" + TXID_WIDGET_NAME + "\" />");
      builder.append("</xWidgets>");
      return builder.toString();
   }

   private String getCoveragePartitionType(Artifact art) throws OseeCoreException {
      if (art == null) {
         return null;
      }

      if (!art.isOfType(CoverageArtifactTypes.CoverageFolder, CoverageArtifactTypes.CoveragePackage,
         CoverageArtifactTypes.CoverageUnit)) {
         return null;
      }

      if (art.isOfType(CoverageArtifactTypes.CoveragePackage)) {
         return art.getName();
      }

      Artifact parent = art.getParent();
      return getCoveragePartitionType(parent);

   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws OseeCoreException, IOException {
      Collection<XListItem> txIds = variableMap.getCollection(XListItem.class, TXID_WIDGET_NAME);
      IOseeBranch branch = variableMap.getBranch(BRANCH_WIDGET_NAME);
      boolean runGreedy = variableMap.getBoolean(GREEDYCB_WIDGET_NAME);

      if (branch == null) {
         monitor.setCanceled(true);
         return;
      }

      CharBackedInputStream charBak = new CharBackedInputStream();
      ISheetWriter excelWriter = new ExcelXmlWriter(charBak.getWriter());

      excelWriter.startSheet("Tx_CvgPartitions", 4);
      excelWriter.writeRow("TX ID", "TX Comment", "Partition", "TX Time");

      monitor.beginTask("Creating TX Coverage Partitions Report", txIds.size());
      for (XListItem txItem : txIds) {
         String txStr = txItem.toString();
         if (monitor.isCanceled()) {
            return;
         }
         monitor.setTaskName(txStr);
         monitor.worked(1);

         if (Strings.isValid(txStr)) {
            String[] tokens = txStr.split(TASKITEMDELIM);
            String txId = "";
            String txComment = "";
            String txTime = "";
            if (tokens.length >= 1) {
               txId = tokens[0];
            }
            if (tokens.length >= 2) {
               txComment = tokens[1];
            }
            if (tokens.length >= 3) {
               int lastTokenIndex = tokens.length - 1;
               txTime = tokens[lastTokenIndex];
            }
            IOseeStatement chStmt = null;
            try {
               chStmt = ConnectionHandler.getStatement();
               chStmt.runPreparedQuery(SELECT_ARTS_BY_BRANCH_AND_TX, BranchManager.getBranchId(branch), txId);

               while (chStmt.next()) {
                  int artId = chStmt.getInt("art_id");
                  monitor.setTaskName(txStr + " " + artId);
                  Artifact art = ArtifactQuery.getArtifactFromId(artId, branch);
                  String partition = getCoveragePartitionType(art);
                  if (partition == null) {
                     //                     OseeLog.log(Activator.class, Level.INFO,
                     //                        "NON-Coverage modification found in transaction:" + txId);
                     partition = "NON-Coverage modification.";
                  }
                  excelWriter.writeRow(txId, txComment, partition, txTime);
                  if (!runGreedy) {
                     break;
                  }
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
            } finally {
               if (chStmt != null) {
                  chStmt.close();
               }
            }
         }
      }

      excelWriter.endSheet();
      excelWriter.endWorkbook();

      IFile iFile = OseeData.getIFile("CoveragePartitionByTxID_" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
      monitor.done();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, SwtXWidgetRenderer dynamicXWidgetLayout, XModifiedListener modListener, boolean isEditable) throws OseeCoreException {
      super.widgetCreated(xWidget, toolkit, art, dynamicXWidgetLayout, modListener, isEditable);
      if (xWidget.getLabel().equals(BRANCH_WIDGET_NAME)) {
         branchWidget = (XBranchSelectWidget) xWidget;
         branchWidget.addListener(new Listener() {
            @Override
            public void handleEvent(Event event) {
               if (txIdListWidget != null) {
                  PopulateTxDataOperation operation =
                     new PopulateTxDataOperation("PopulateTxDataOperation", Activator.PLUGIN_ID);
                  try {
                     Operations.executeWorkAndCheckStatus(operation);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex.getMessage(), ex);
                  }
               }
            }
         });
      }
      if (xWidget.getLabel().equals(TXID_WIDGET_NAME)) {
         txIdListWidget = (XList) xWidget;
      }
   }

   private class PopulateTxDataOperation extends org.eclipse.osee.framework.core.operation.AbstractOperation {

      public PopulateTxDataOperation(String operationName, String pluginId) {
         super(operationName, pluginId);
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         IOseeBranch branch = branchWidget.getData();
         IOseeStatement chStmt = null;
         try {
            chStmt = ConnectionHandler.getStatement();
            chStmt.runPreparedQuery(SELECT_TXS_BY_BRANCH, BranchManager.getBranchId(branch));
            txIdListWidget.removeAll();
            while (chStmt.next()) {
               txIdListWidget.add(chStmt.getString("transaction_id") + TASKITEMDELIM + chStmt.getString("OSEE_COMMENT") + TASKITEMDELIM + chStmt.getString("time"));
            }
            txIdListWidget.refresh();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex.toString(), ex);
         } finally {
            if (chStmt != null) {
               chStmt.close();
            }
         }
      }

   }

   @Override
   public String getName() {
      return "Tx Coverage Partitions Report Blam";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Admin");
   }

}
