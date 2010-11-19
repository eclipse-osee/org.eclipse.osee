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
package org.eclipse.osee.ats.editor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn;
import org.eclipse.nebula.widgets.xviewer.XViewerColumn.SortDataType;
import org.eclipse.nebula.widgets.xviewer.XViewerFactory;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.nebula.widgets.xviewer.customize.IXViewerCustomizations;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomizations;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.artifact.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.artifact.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.internal.workflow.SMAState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.revision.ChangeManager;
import org.eclipse.osee.framework.skynet.core.utility.Artifacts;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.results.table.IResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsEditorTableTab;
import org.eclipse.osee.framework.ui.skynet.results.table.ResultsXViewerRow;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewer;
import org.eclipse.osee.framework.ui.skynet.results.table.xresults.ResultsXViewerLabelProvider;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.WorkPageType;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Donald G. Dunne
 */
public class SMAHistorySection extends SectionPart {

   private final SMAEditor editor;
   private boolean sectionCreated = false;

   public SMAHistorySection(SMAEditor editor, Composite parent, FormToolkit toolkit, int style) {
      super(parent, toolkit, style | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR);
      this.editor = editor;
   }

   @Override
   public void initialize(IManagedForm form) {
      super.initialize(form);
      Section section = getSection();
      section.setText("History");
      section.setLayout(new GridLayout());
      section.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      // Only load when users selects section
      section.addListener(SWT.Activate, new Listener() {

         @Override
         public void handleEvent(Event e) {
            createSection();
         }
      });
   }

   private synchronized void createSection() {
      if (sectionCreated) {
         return;
      }

      AbstractWorkflowArtifact sma = editor.getSma();
      final FormToolkit toolkit = getManagedForm().getToolkit();
      Composite composite = toolkit.createComposite(getSection(), toolkit.getBorderStyle() | SWT.WRAP);
      composite.setLayout(ALayout.getZeroMarginLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      try {
         ResultsEditorTableTab tableTab = createLogTable(sma);
         tableTab.createTab(composite, null);
         tableTab.getResultsXViewer().setLabelProvider(new HistoryXViewerLabelProvider(tableTab.getResultsXViewer()));
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }

      Label button = toolkit.createLabel(composite, "   ", SWT.NONE);
      button.setText("    ");
      final AbstractWorkflowArtifact fSma = sma;
      button.addListener(SWT.MouseDoubleClick, new Listener() {
         @Override
         public void handleEvent(Event event) {
            try {
               RendererManager.open(fSma, PresentationType.GENERALIZED_EDIT);
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });

      getSection().setClient(composite);
      toolkit.paintBordersFor(composite);
      sectionCreated = true;

   }
   private static enum Columns {
      Transaction,
      Event,
      State,
      Message,
      User,
      Date;
   };

   public XViewerColumn TransactionColumn = new XViewerColumn(Columns.Transaction.name(), Columns.Transaction.name(),
      80, SWT.LEFT, true, SortDataType.Integer, false, "");

   public ResultsEditorTableTab createLogTable(AbstractWorkflowArtifact aba) throws OseeCoreException {
      List<XViewerColumn> columns =
         Arrays.asList(//
            TransactionColumn,// 
            new XViewerColumn(Columns.Event.name(), Columns.Event.name(), 100, SWT.LEFT, true, SortDataType.String,
               false, ""),// 
            new XViewerColumn(Columns.State.name(), Columns.State.name(), 100, SWT.LEFT, true, SortDataType.String,
               false, ""),// 
            new XViewerColumn(Columns.Message.name(), Columns.Message.name(), 180, SWT.LEFT, true, SortDataType.String,
               false, ""),// 
            new XViewerColumn(Columns.User.name(), Columns.User.name(), 160, SWT.LEFT, true, SortDataType.String,
               false, ""),// 
            new XViewerColumn(Columns.Date.name(), Columns.Date.name(), 150, SWT.LEFT, true, SortDataType.Date, false,
               "")//
         );

      List<IResultsXViewerRow> rows = new ArrayList<IResultsXViewerRow>();
      XResultData rd = new XResultData(false);
      fillRows(aba, rows, rd);

      ResultsEditorTableTab tab = new ResultsEditorTableTab("Data", columns, rows);
      tab.setxViewerFactory(new HistoryXViewerFactory(columns));
      return tab;
   }

   public void fillRows(AbstractWorkflowArtifact aba, List<IResultsXViewerRow> rows, XResultData rd) throws OseeCoreException {
      Collection<Change> changes = ChangeManager.getChangesPerArtifact(aba, null);
      for (Change change : changes) {
         if (change.getItemTypeName().equals(AtsAttributeTypes.CurrentState.getName())) {
            processCurrentStateChange(change, rows, rd);
         }
         if (change.getItemTypeName().equals(AtsAttributeTypes.CurrentStateType.getName())) {
            processCurrentStateTypeChange(change, rows, rd);
         }
      }
   }

   public void processCurrentStateTypeChange(Change change, List<IResultsXViewerRow> rows, XResultData rd) {
      try {
         if (change.getIsValue().equals(WorkPageType.Completed.name())) {
            rows.add(new ResultsXViewerRow(new String[] {
               String.valueOf(change.getTxDelta().getEndTx().getId()),
               "Completed",
               "",
               "",
               UserManager.getUserNameById(change.getTxDelta().getEndTx().getAuthor()),
               DateUtil.getMMDDYYHHMM(change.getTxDelta().getEndTx().getTimeStamp())}));

         } else if (change.getIsValue().equals(WorkPageType.Cancelled.name())) {
            rows.add(new ResultsXViewerRow(new String[] {
               String.valueOf(change.getTxDelta().getEndTx().getId()),
               "Cancelled",
               "",
               "",
               UserManager.getUserNameById(change.getTxDelta().getEndTx().getAuthor()),
               DateUtil.getMMDDYYHHMM(change.getTxDelta().getEndTx().getTimeStamp())}));
         }
      } catch (Exception ex) {
         rd.logError("Error processing change type" + change.toString());
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   public void processCurrentStateChange(Change change, List<IResultsXViewerRow> rows, XResultData rd) {
      try {
         SMAState was = new SMAState();
         was.setFromXml(change.getWasValue());
         SMAState is = new SMAState();
         is.setFromXml(change.getIsValue());
         if (change.getWasValue().equals("")) {
            rows.add(new ResultsXViewerRow(new String[] {
               String.valueOf(change.getTxDelta().getEndTx().getId()),
               "Created",
               is.getName(),
               " -> " + is.getName(),
               UserManager.getUserNameById(change.getTxDelta().getEndTx().getAuthor()),
               DateUtil.getMMDDYYHHMM(change.getTxDelta().getEndTx().getTimeStamp())}));
         } else if (!was.getName().equals(is.getName())) {
            rows.add(new ResultsXViewerRow(new String[] {
               String.valueOf(change.getTxDelta().getEndTx().getId()),
               "Transition",
               is.getName(),
               was.getName() + " -> " + is.getName(),
               UserManager.getUserNameById(change.getTxDelta().getEndTx().getAuthor()),
               DateUtil.getMMDDYYHHMM(change.getTxDelta().getEndTx().getTimeStamp())}));
         }
         if (was.getName().equals(is.getName()) && (was.getPercentComplete() != is.getPercentComplete() || !was.getHoursSpentStr().equals(
            is.getHoursSpentStr()))) {
            rows.add(new ResultsXViewerRow(new String[] {
               String.valueOf(change.getTxDelta().getEndTx().getId()),
               "Metrics",
               is.getName(),
               "%" + is.getPercentComplete() + " - " + is.getHoursSpentStr() + " hrs",
               UserManager.getUserNameById(change.getTxDelta().getEndTx().getAuthor()),
               DateUtil.getMMDDYYHHMM(change.getTxDelta().getEndTx().getTimeStamp())}));
         }
         Collection<User> wasAssignees = was.getAssignees();
         Collection<User> isAssignees = is.getAssignees();
         Set<User> assigned = new HashSet<User>();
         Set<User> unAssigned = new HashSet<User>();
         for (User isAssignee : isAssignees) {
            if (!wasAssignees.contains(isAssignee)) {
               assigned.add(isAssignee);
            }
         }
         for (User wasAssignee : wasAssignees) {
            if (!isAssignees.contains(wasAssignee)) {
               unAssigned.add(wasAssignee);
            }
         }
         if (unAssigned.size() > 0) {
            rows.add(new ResultsXViewerRow(new String[] {
               String.valueOf(change.getTxDelta().getEndTx().getId()),
               "UnAssigned",
               is.getName(),
               Artifacts.toString("; ", unAssigned),
               UserManager.getUserNameById(change.getTxDelta().getEndTx().getAuthor()),
               DateUtil.getMMDDYYHHMM(change.getTxDelta().getEndTx().getTimeStamp())}));
         }
         if (assigned.size() > 0) {
            rows.add(new ResultsXViewerRow(new String[] {
               String.valueOf(change.getTxDelta().getEndTx().getId()),
               "Assigned",
               is.getName(),
               Artifacts.toString("; ", assigned),
               UserManager.getUserNameById(change.getTxDelta().getEndTx().getAuthor()),
               DateUtil.getMMDDYYHHMM(change.getTxDelta().getEndTx().getTimeStamp())}));
         }
      } catch (Exception ex) {
         rd.logError("Error processing change " + change.toString());
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }
   // Provide own factory so can get default sorting
   public class HistoryXViewerFactory extends XViewerFactory {

      public HistoryXViewerFactory(List<XViewerColumn> columns) {
         super("xviewer.test");
         for (XViewerColumn xCol : columns) {
            registerColumns(xCol);
         }
      }

      @Override
      public IXViewerCustomizations getXViewerCustomizations() {
         return new XViewerCustomizations();
      }

      @Override
      public boolean isAdmin() {
         return true;
      }

      @Override
      public String getNamespace() {
         return "org.eclipse.osee.ats.history";
      }

      @Override
      public CustomizeData getDefaultTableCustomizeData() {
         CustomizeData customizeData = super.getDefaultTableCustomizeData();
         for (XViewerColumn xCol : customizeData.getColumnData().getColumns()) {
            if (xCol.getId() == TransactionColumn.getId()) {
               xCol.setSortForward(false);
            }
         }
         customizeData.getSortingData().setSortingNames(TransactionColumn.getId());
         return customizeData;
      }

   }

   public class HistoryXViewerLabelProvider extends ResultsXViewerLabelProvider {

      public HistoryXViewerLabelProvider(ResultsXViewer resultsXViewer) {
         super(resultsXViewer);
      }

      @Override
      public Image getColumnImage(Object element, XViewerColumn col, int columnIndex) throws Exception {
         if (col.getName().equals("Event")) {
            String text = getColumnText(element, col, columnIndex);
            if (text.equals("Assigned") || text.equals("UnAssigned")) {
               return ImageManager.getImage(FrameworkImage.USERS);
            } else if (text.equals("Metrics")) {
               return ImageManager.getImage(FrameworkImage.GREEN_PLUS);
            } else if (text.equals("Transition")) {
               return ImageManager.getImage(AtsImage.TRANSITION);
            } else if (text.equals("Created")) {
               return ImageManager.getImage(AtsImage.ACTION);
            } else if (text.equals("Completed")) {
               return ImageManager.getImage(FrameworkImage.DOT_GREEN);
            } else if (text.equals("Cancelled")) {
               return ImageManager.getImage(FrameworkImage.X_RED);
            }
         }
         return super.getColumnImage(element, col, columnIndex);
      }

   }
}
