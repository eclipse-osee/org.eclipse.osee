/*
 * Created on Nov 2, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ReviewSMArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xbargraph.XBarGraphTable;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
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
public class WorldMetricsComposite extends Composite {

   private Composite toolBarComposite;
   private Composite metricsComposite;
   private final WorldComposite worldComposite;
   private final Color BACKGROUND_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
   private final Color FOREGROUND_COLOR = Display.getCurrent().getSystemColor(SWT.COLOR_BLUE);

   /**
    * @param parent
    * @param style
    */
   public WorldMetricsComposite(WorldComposite worldComposite, Composite parent, int style) {
      super(parent, style);
      this.worldComposite = worldComposite;

      setLayout(new GridLayout(1, false));
      setLayoutData(new GridData(GridData.FILL_BOTH));
      adapt(this);

      creatToolBar(this);
      //      try {
      //         handleUpdateMetrics();
      //      } catch (OseeCoreException ex) {
      //         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      //      }
   }

   private void creatToolBar(Composite composite) {
      toolBarComposite = new Composite(composite, SWT.NONE);
      toolBarComposite.setLayoutData(new GridData(SWT.LEFT, SWT.NONE, false, false, 1, 1));
      toolBarComposite.setLayout(ALayout.getZeroMarginLayout(1, false));
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
      metricsComposite = new Composite(this, SWT.NONE);
      metricsComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
      metricsComposite.setLayout(ALayout.getZeroMarginLayout(1, false));
      adapt(metricsComposite);

      addSpace();
      Label label = new Label(metricsComposite, SWT.NONE);
      label.setText("Number of Workflows: " + getLoadedWorkflowArtifacts().size());
      adapt(label);
      addSpace();
      createFullPercentChart(metricsComposite);
      addSpace();
      createAssigneesChart(metricsComposite);

      layout();
   }

   private Collection<TeamWorkFlowArtifact> getLoadedWorkflowArtifacts() throws OseeCoreException {
      Set<TeamWorkFlowArtifact> teams = new HashSet<TeamWorkFlowArtifact>();
      for (Artifact art : worldComposite.getLoadedArtifacts()) {
         if (art instanceof ActionArtifact) {
            teams.addAll(((ActionArtifact) art).getTeamWorkFlowArtifacts());
         }
         if (art instanceof TeamWorkFlowArtifact) {
            teams.add((TeamWorkFlowArtifact) art);
         }
         if (art instanceof TaskArtifact) {
            teams.add(((TaskArtifact) art).getParentTeamWorkflow());
         }
         if (art instanceof ReviewSMArtifact) {
            teams.add(((ReviewSMArtifact) art).getParentTeamWorkflow());
         }
      }
      return teams;
   }

   private void addSpace() {
      Label label = new Label(metricsComposite, SWT.NONE);
      label.setText(" ");
      adapt(label);
   }

   public void createFullPercentChart(Composite parent) {
      Map<String, Integer> itemToValueMap = new HashMap<String, Integer>();
      itemToValueMap.put("All", 85);
      XBarGraphTable table = new XBarGraphTable("Complete", "Loaded", "Complete", itemToValueMap);
      table.createWidgets(parent, 1);
      adapt(table);
   }

   public void createAssigneesChart(Composite parent) {
      Map<String, Integer> itemToValueMap = new HashMap<String, Integer>();
      itemToValueMap.put("Don", 85);
      itemToValueMap.put("Ryan", 35);
      itemToValueMap.put("Andy", 4);
      XBarGraphTable table = new XBarGraphTable("Complete by Assignee", "User", "Complete", itemToValueMap);
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
