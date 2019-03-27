/*******************************************************************************
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

package org.eclipse.osee.framework.ui.skynet.widgets.xHistory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.widgets.xviewer.customize.XViewerCustomMenu;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.enums.TransactionDetailsType;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.change.AttributeChange;
import org.eclipse.osee.framework.skynet.core.change.Change;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.filter.IEventFilter;
import org.eclipse.osee.framework.skynet.core.event.listener.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEvent;
import org.eclipse.osee.framework.skynet.core.event.model.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.model.Sender;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OpenContributionItem;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.skynet.action.EditTransactionComment;
import org.eclipse.osee.framework.ui.skynet.action.ITransactionRecordSelectionProvider;
import org.eclipse.osee.framework.ui.skynet.action.WasIsCompareEditorAction;
import org.eclipse.osee.framework.ui.skynet.action.WasIsCompareEditorWithReplaceAction;
import org.eclipse.osee.framework.ui.skynet.change.ChangeUiUtil;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.menu.CompareArtifactAction;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.SkynetViews;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;

/**
 * Displays persisted changes made to an artifact.
 *
 * @author Jeff C. Phillips
 */
public class HistoryView extends GenericViewPart implements IBranchEventListener, ITransactionRecordSelectionProvider, IRebuildMenuListener {

   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView";
   private static final String ART_ID = "artifactId";
   private static final String ART_NAME = "artifactName";
   private static final String BRANCH_ID = "branchId";

   private XHistoryWidget xHistoryWidget;
   private Artifact artifact;

   public HistoryView() {
      super();
   }

   @Override
   public void dispose() {
      super.dispose();
      OseeEventManager.removeListener(this);
      if (xHistoryWidget != null) {
         xHistoryWidget.dispose();
      }
   }

   @Override
   public void createPartControl(Composite parent) {

      if (DbConnectionExceptionComposite.dbConnectionIsOk(parent)) {

         GridLayout layout = new GridLayout();
         layout.numColumns = 1;
         layout.verticalSpacing = 0;
         layout.marginWidth = 0;
         layout.marginHeight = 0;
         parent.setLayout(layout);
         parent.setLayoutData(new GridData(GridData.FILL_BOTH));

         xHistoryWidget = new XHistoryWidget() {

            @Override
            protected void onRefresh() {
               refreshTitle();
            }
         };
         xHistoryWidget.setDisplayLabel(false);
         xHistoryWidget.createWidgets(parent, 1);

         MenuManager menuManager = new MenuManager();
         menuManager.setRemoveAllWhenShown(true);
         menuManager.addMenuListener(new IMenuListener() {
            @Override
            public void menuAboutToShow(IMenuManager manager) {
               MenuManager menuManager = (MenuManager) manager;
               menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
            }
         });

         menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

         xHistoryWidget.getXViewer().getTree().setMenu(
            menuManager.createContextMenu(xHistoryWidget.getXViewer().getTree()));

         getSite().registerContextMenu(VIEW_ID, menuManager, xHistoryWidget.getXViewer());
         getSite().setSelectionProvider(xHistoryWidget.getXViewer());

         HelpUtil.setHelp(parent, OseeHelpContext.HISTORY_VIEW);

         OseeStatusContributionItemFactory.addTo(this, true);

         setupMenus();

         setFocusWidget(xHistoryWidget.getXViewer().getControl());

         OseeEventManager.addListener(this);
      }
   }

   private void setupMenus() {
      Menu popupMenu = new Menu(xHistoryWidget.getXViewer().getTree().getParent());
      OpenOnShowListener openListener = new OpenOnShowListener();
      popupMenu.addMenuListener(openListener);

      OpenContributionItem contributionItem = new OpenContributionItem(getClass().getSimpleName() + ".open");
      contributionItem.fill(popupMenu, -1);
      openListener.add(popupMenu.getItem(0));
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createChangeReportMenuItem(popupMenu);

      new MenuItem(popupMenu, SWT.SEPARATOR);
      createReplaceAttributeWithVersionMenuItem(popupMenu);

      IAction action = new CompareArtifactAction("Compare two Artifacts", xHistoryWidget.getXViewer());
      new ActionContributionItem(action).fill(popupMenu, 3);

      new ActionContributionItem(new EditTransactionComment(this)).fill(popupMenu, 3);
      new ActionContributionItem(new WasIsCompareEditorWithReplaceAction()).fill(popupMenu, 3);
      openListener.add(popupMenu.getItem(3));
      new ActionContributionItem(new WasIsCompareEditorAction()).fill(popupMenu, 3);
      openListener.add(popupMenu.getItem(3));

      XViewerCustomMenu xMenu = new XViewerCustomMenu(xHistoryWidget.getXViewer());
      new MenuItem(popupMenu, SWT.SEPARATOR);
      xMenu.createTableCustomizationMenuItem(popupMenu);
      xMenu.createViewTableReportMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      xMenu.addCopyViewMenuBlock(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      xMenu.addFilterMenuBlock(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      xHistoryWidget.getXViewer().getTree().setMenu(popupMenu);
   }

   private class OpenOnShowListener implements MenuListener {
      private final List<MenuItem> items = new LinkedList<>();

      public void add(MenuItem item) {
         items.add(item);
      }

      @Override
      public void menuShown(MenuEvent e) {
         for (MenuItem item : items) {
            item.setEnabled(!xHistoryWidget.getXViewer().getSelection().isEmpty());
            if (item.getText().startsWith("View Was") || item.getText().startsWith("Set Was")) {
               item.setEnabled(WasIsCompareEditorAction.isEnabledStatic());
            }
         }
      }

      @Override
      public void menuHidden(MenuEvent e) {
         // nothing
      }
   }

   private void createReplaceAttributeWithVersionMenuItem(Menu popupMenu) {
      final MenuItem replaceWithMenu = new MenuItem(popupMenu, SWT.CASCADE);
      replaceWithMenu.setText("&Replace Attribute with Version");
      try {
         replaceWithMenu.setEnabled(AccessControlManager.isOseeAdmin());
      } catch (Exception ex) {
         replaceWithMenu.setEnabled(false);
      }
      popupMenu.addMenuListener(new MenuAdapter() {

         @Override
         public void menuShown(MenuEvent e) {
            List<?> selections = ((IStructuredSelection) xHistoryWidget.getXViewer().getSelection()).toList();
            replaceWithMenu.setEnabled(
               selections.size() == 1 && selections.iterator().next() instanceof AttributeChange);
         }

      });

      replaceWithMenu.addSelectionListener(new SelectionAdapter() {

         @SuppressWarnings("deprecation")
         @Override
         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) xHistoryWidget.getXViewer().getSelection();
            Object selectedObject = selection.getFirstElement();

            if (selectedObject instanceof AttributeChange) {
               try {
                  AttributeChange attributeChange = (AttributeChange) selectedObject;
                  Artifact artifact =
                     ArtifactQuery.getArtifactFromId(attributeChange.getArtId(), attributeChange.getBranch());

                  for (Attribute<?> attribute : artifact.getAttributes(attributeChange.getAttributeType())) {
                     if (attribute.getId() == attributeChange.getAttrId().getId().intValue()) {
                        attribute.replaceWithVersion(attributeChange.getGamma());
                        break;
                     }
                  }

                  artifact.persist("Replace attribute with version");
                  artifact.reloadAttributesAndRelations();

               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }

      });
   }

   private void createChangeReportMenuItem(Menu popupMenu) {
      final MenuItem changeReportMenuItem = new MenuItem(popupMenu, SWT.CASCADE);
      changeReportMenuItem.setText("&Change Report");
      changeReportMenuItem.setImage(ImageManager.getImage(FrameworkImage.BRANCH_CHANGE));
      popupMenu.addMenuListener(new MenuAdapter() {

         @Override
         public void menuShown(MenuEvent e) {
            List<?> selections = ((IStructuredSelection) xHistoryWidget.getXViewer().getSelection()).toList();
            TransactionId tx = ((Change) selections.iterator().next()).getTxDelta().getStartTx();
            changeReportMenuItem.setEnabled(selections.size() == 1 && TransactionManager.getTransaction(
               tx).getTxType() != TransactionDetailsType.Baselined);
         }

      });

      changeReportMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) xHistoryWidget.getXViewer().getSelection();
            Object selectedObject = selection.getFirstElement();

            if (selectedObject instanceof Change) {
               try {
                  ChangeUiUtil.open(((Change) selectedObject).getTxDelta().getStartTx());
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         }

      });
   }

   private void explore(final Artifact artifact, boolean loadHistory) {
      this.artifact = artifact;

      if (isInitialized()) {
         setPartName(String.format("History: (%s) - [%s]", artifact.getId(), artifact.getName()));
         xHistoryWidget.setInputData(artifact, loadHistory);
      }
   }

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);
      if (artifact != null) {
         memento.putString(ART_ID, artifact.getIdString());
         memento.putString(ART_NAME, artifact.getName());
         memento.putString(BRANCH_ID, artifact.getBranch().getIdString());
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         if (memento != null) {
            String id = memento.getString(ART_ID);
            String name = memento.getString(ART_NAME);
            String branchId = memento.getString(BRANCH_ID);
            if (Strings.isNumeric(id) && Strings.isValid(name) && Strings.isNumeric(branchId)) {
               ArtifactToken artTok =
                  ArtifactToken.valueOf(Long.valueOf(id), name, BranchId.valueOf(Long.valueOf(branchId)));
               openViewUpon(artTok, false);
            } else {
               closeView();
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.WARNING, "History View error on init", ex);
      }
   }

   private void closeView() {
      SkynetViews.closeView(VIEW_ID, getViewSite().getSecondaryId());
   }

   @Override
   public void rebuildMenu() {
      setupMenus();
   }

   @Override
   public void handleBranchEvent(Sender sender, BranchEvent branchEvent) {
      BranchEventType branchModType = branchEvent.getEventType();
      if (branchModType == BranchEventType.Deleting || branchModType == BranchEventType.Deleted || branchModType == BranchEventType.Purging || branchModType == BranchEventType.Purged) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               closeView();
            }
         });
         return;
      } else if (branchModType == BranchEventType.Committed) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               try {
                  explore(artifact, true);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }
         });
         // refresh view with new branch and transaction id
      }
   }

   @Override
   public List<? extends IEventFilter> getEventFilters() {
      return artifact != null ? OseeEventManager.getEventFiltersForBranch(
         artifact.getBranch()) : Collections.<IEventFilter> emptyList();
   }

   @Override
   public List<TransactionId> getSelectedTransactionRecords() {
      return isInitialized() ? xHistoryWidget.getSelectedTransactionRecords() : Collections.<TransactionId> emptyList();
   }

   @Override
   public void refreshUI(List<TransactionId> records) {
      if (isInitialized()) {
         setPartName("History: " + artifact.getName());
         xHistoryWidget.refresh();
      }
   }

   private void refreshTitle() {
      if (isInitialized()) {
         setPartName("History: " + artifact.getName());
      }
   }

   private boolean isInitialized() {
      return xHistoryWidget != null && artifact != null;
   }

   public static void open(Artifact artifact) {
      Conditions.checkNotNull(artifact, "artifact");
      HistoryView.openViewUpon(artifact, true);
   }

   private static void openViewUpon(final ArtifactToken artifactTok, final boolean loadHistory) {
      Conditions.checkNotNull(artifactTok, "artifact");
      Job job = new UIJob("Open History: " + artifactTok.getName()) {

         @Override
         public IStatus runInUIThread(IProgressMonitor monitor) {
            try {
               IWorkbenchPage page = AWorkbench.getActivePage();
               HistoryView historyView = (HistoryView) page.showView(VIEW_ID,
                  artifactTok.getIdString() + artifactTok.getBranch().getIdString(), IWorkbenchPage.VIEW_ACTIVATE);
               try {
                  Artifact artifact = ArtifactQuery.getArtifactFromId(artifactTok, artifactTok.getBranch(),
                     DeletionFlag.INCLUDE_DELETED);
                  historyView.explore(artifact, loadHistory);
               } catch (Exception ex) {
                  historyView.closeView();
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
            return Status.OK_STATUS;
         }
      };
      Jobs.startJob(job);
   }
}
