/*
 * Created on Nov 2, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.util.SMAMetrics;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xbargraph.XBarGraphTable;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

/**
 * @author Donald G. Dunne
 */
public class WorldMetricsComposite extends ScrolledComposite {

   private Composite toolBarComposite;
   private Composite metricsComposite;
   private final WorldComposite worldComposite;
   private final Color BACKGROUND_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
   private final Color FOREGROUND_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);
   private final Composite mainComp;

   /**
    * @param parent
    * @param style
    */
   public WorldMetricsComposite(WorldComposite worldComposite, Composite parent, int style) {
      super(parent, style | SWT.V_SCROLL | SWT.H_SCROLL);
      this.worldComposite = worldComposite;

      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      mainComp = new Composite(this, SWT.NONE);
      mainComp.setLayout(new GridLayout());
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      adapt(mainComp);

      adapt(this);
      creatToolBar(mainComp);

      setContent(mainComp);
      setExpandHorizontal(true);
      setExpandVertical(true);
      layout();
   }

   private void creatToolBar(Composite composite) {
      toolBarComposite = new Composite(composite, SWT.NONE);
      toolBarComposite.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 1, 1));
      toolBarComposite.setLayout(new GridLayout(1, false));
      adapt(toolBarComposite);

      ToolBar toolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);
      toolBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1));

      ToolItem refreshAction = new ToolItem(toolBar, SWT.PUSH | SWT.BORDER);
      refreshAction.setText("Display/Refresh Metrics");
      refreshAction.setImage(AtsPlugin.getInstance().getImage("refresh.gif"));
      refreshAction.setToolTipText("Recalculate and Update Metrics");
      adapt(refreshAction.getControl());
      refreshAction.addSelectionListener(new SelectionAdapter() {
         /* (non-Javadoc)
          * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
          */
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               handleUpdateMetrics();
            } catch (OseeCoreException ex) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
      });

   }

   public void handleUpdateMetrics() throws OseeCoreException {
      if (metricsComposite != null) {
         metricsComposite.dispose();
      }
      metricsComposite = new Composite(mainComp, SWT.NONE);
      metricsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
      metricsComposite.setLayout(ALayout.getZeroMarginLayout(1, true));
      adapt(metricsComposite);

      addSpace();
      SMAMetrics sMet = new SMAMetrics(worldComposite.getLoadedArtifacts(), null);
      Label label = new Label(metricsComposite, SWT.NONE);
      label.setText(sMet.toStringLong());
      adapt(label);
      addSpace();
      createFullPercentChart(sMet, metricsComposite);
      addSpace();
      createCompletedByAssigneesChart(sMet, metricsComposite);

      mainComp.layout();
      computeScrollSize();
   }

   public void computeScrollSize() {
      this.computeScrollSize(mainComp);
   }

   private void computeScrollSize(Composite viewableArea) {
      this.setMinSize(viewableArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
   }

   private void addSpace() {
      Label label = new Label(metricsComposite, SWT.NONE);
      label.setText(" ");
      adapt(label);
   }

   public void createFullPercentChart(SMAMetrics sMet, Composite parent) {
      Map<String, Integer> itemToValueMap = new HashMap<String, Integer>();
      itemToValueMap.put(
            "By Team Percents (" + sMet.getCummulativePercentComplete() + "/" + sMet.getNumObjects() + ")",
            (int) sMet.getPercentCompleteByTeamPercents());
      itemToValueMap.put(
            "By Team Workflow (" + sMet.getCompletedTeamWorkflows().size() + "/" + sMet.getNumTeamWfs() + ")",
            (int) sMet.getPercentCompleteByWorkflow());
      XBarGraphTable table = new XBarGraphTable("Total Percent Complete", "", "Percent Complete", itemToValueMap);
      table.setFillHorizontally(true);
      table.createWidgets(parent, 1);
      adapt(table);
   }

   public void createCompletedByAssigneesChart(SMAMetrics sMet, Composite parent) {
      Map<String, Integer> itemToValueMap = new HashMap<String, Integer>();
      for (User user : sMet.getAssigneesAssignedOrCompleted()) {
         int completed =
               sMet.getUserToCompletedSmas().containsKey(user) ? sMet.getUserToCompletedSmas().getValues(user).size() : 0;
         int inWork =
               sMet.getUserToAssignedSmas().containsKey(user) ? sMet.getUserToAssignedSmas().getValues(user).size() : 0;
         int total = completed + inWork;
         int percentComplete = 0;
         if (completed == total) {
            percentComplete = 100;
         } else if (completed != 0 && total != 0) {
            double percent = new Double(completed) / total * 100.0;
            percentComplete = (int) percent;
         }
         itemToValueMap.put(user.getName() + " (" + completed + "/" + total + ")", percentComplete);
      }
      XBarGraphTable table =
            new XBarGraphTable("Completed by Assignee", "User", "Percent Complete", itemToValueMap, "%");
      table.setFillHorizontally(true);
      table.createWidgets(parent, 1);
      adapt(table);
   }

   public void disposeComposite() {
   }

   public void adapt(XWidget xWidget) {
      adapt(xWidget.getControl());
      adapt(xWidget.getLabelWidget());
   }

   public void adapt(Control control) {
      if (control == null) return;
      control.setBackground(BACKGROUND_COLOR);
      control.setForeground(FOREGROUND_COLOR);
   }

   public void adapt(Composite composite) {
      composite.setBackground(BACKGROUND_COLOR);
   }

}
