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

package org.eclipse.osee.ats.world;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.BulkLoadAtsCache;
import org.eclipse.osee.ats.world.search.VersionTargetedForTeamSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * ATS World View provides a tree-table view of the ATS object results loaded from searches
 * 
 * @see ViewPart
 * @author Donald G. Dunne
 */
public class WorldView extends ViewPart implements IPartListener, IAtsMetricsProvider, IActionable {
   public static final String VIEW_ID = "org.eclipse.osee.ats.world.WorldView";
   public static final String HELP_CONTEXT_ID = "atsWorldView";
   WorldComposite worldComposite;
   private TabFolder tabFolder;

   /**
    * The constructor.
    */
   public WorldView() {
   }

   @Override
   public void setFocus() {
   }

   public static WorldView getWorldView() {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      try {
         return (WorldView) page.showView(WorldView.VIEW_ID);
      } catch (PartInitException e1) {
         MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Launch Error",
               "Couldn't Launch OSEE World View " + e1.getMessage());
      }
      return null;
   }

   @Override
   public void createPartControl(Composite parent) {

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;
      BulkLoadAtsCache.run(false);

      tabFolder = new TabFolder(parent, SWT.BOTTOM);
      tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));
      tabFolder.setLayout(ALayout.getZeroMarginLayout(1, true));

      TabItem tab = new TabItem(tabFolder, SWT.NONE);
      worldComposite = new WorldComposite(VIEW_ID, getViewSite(), tabFolder, SWT.NONE);
      tab.setControl(worldComposite);
      tab.setText("Actions");

      TabItem tab2 = new TabItem(tabFolder, SWT.NONE);
      AtsMetricsComposite atsMetricsComposite = new AtsMetricsComposite(this, tabFolder, SWT.NONE);
      tab2.setControl(atsMetricsComposite);
      tab2.setText("Metrics");

      tabFolder.setSelection(tab);

      OseeAts.addBugToViewToolbar(this, this, AtsPlugin.getInstance(), VIEW_ID, "ATS World");
      AtsPlugin.getInstance().setHelp(worldComposite.getControl(), HELP_CONTEXT_ID);
      OseeContributionItem.addTo(this, false);
   }

   public String getActionDescription() {
      if (worldComposite.getLastSearchItem() != null) return String.format("Search Item: %s",
            worldComposite.getLastSearchItem().getSelectedName(SearchType.Search));
      return "";
   }

   public static void loadIt(final String name, final Collection<? extends Artifact> arts, final TableLoadOption... tableLoadOption) {
      final Set<TableLoadOption> options = new HashSet<TableLoadOption>();
      options.addAll(Arrays.asList(tableLoadOption));
      options.add(TableLoadOption.ClearLastSearchItem);
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         public void run() {
            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            try {
               WorldView worldView = (WorldView) page.showView(WorldView.VIEW_ID);
               worldView.load(name, arts, options.toArray(new TableLoadOption[options.size()]));
            } catch (PartInitException e1) {
               OSEELog.logSevere(AtsPlugin.class, "Couldn't Launch XViewer Dev View ", true);
            }
         }
      }, options.contains(TableLoadOption.ForcePend));
   }

   public static ArrayList<Artifact> getLoadedArtifacts() {
      return WorldView.getWorldView().getXViewer().getLoadedArtifacts();
   }

   public void loadTable(WorldSearchItem searchItem, TableLoadOption... tableLoadOptions) throws InterruptedException, OseeCoreException {
      worldComposite.loadTable(searchItem, tableLoadOptions);
   }

   public void loadTable(WorldSearchItem searchItem, SearchType searchType, TableLoadOption... tableLoadOptions) throws InterruptedException, OseeCoreException {
      worldComposite.loadTable(searchItem, searchType, tableLoadOptions);
   }

   public void load(final String name, final Collection<? extends Artifact> arts, TableLoadOption... tableLoadOption) {
      worldComposite.load(name, arts, tableLoadOption);
   }

   public void partActivated(IWorkbenchPart part) {
   }

   public void partBroughtToTop(IWorkbenchPart part) {
   }

   public void partClosed(IWorkbenchPart part) {
      if (part.equals(this)) {
         worldComposite.disposeComposite();
      }
   }

   public void partDeactivated(IWorkbenchPart part) {
   }

   public void partOpened(IWorkbenchPart part) {
   }

   public WorldXViewer getXViewer() {
      return worldComposite.getXViewer();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#dispose()
    */
   @Override
   public void dispose() {
      if (worldComposite != null) worldComposite.disposeComposite();
      super.dispose();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getMetricsArtifacts()
    */
   @Override
   public Collection<? extends Artifact> getMetricsArtifacts() {
      return worldComposite.getLoadedArtifacts();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.world.IAtsMetricsProvider#getMetricsVersionArtifact()
    */
   @Override
   public VersionArtifact getMetricsVersionArtifact() {
      if (worldComposite.getLastSearchItem() instanceof VersionTargetedForTeamSearchItem) {
         return ((VersionTargetedForTeamSearchItem) worldComposite.getLastSearchItem()).getSearchVersionArtifact();
      }
      return null;
   }

}