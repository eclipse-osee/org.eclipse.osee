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

package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ISelectedArtifacts;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.RelationOrderContributionItem.SelectionListener;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;

/**
 * @author Ryan D. Brooks
 */
public class RelationsComposite extends Composite implements ISelectedArtifacts {
   private static final String DELETE_ARTIFACTS = "Delete Artifact(s)";
   private TreeViewer treeViewer;
   private Tree tree;
   private NeedSelectedArtifactListener needSelectedArtifactListener;
   private final IDirtiableEditor editor;
   public static final String VIEW_ID = "osee.define.relation.RelationExplorer";
   public static final String[] columnNames =
      new String[] {"Type/Side/Name", "Art Id", "Rationale", "Id", "Gamma Id", "Order By", "Art Type"};
   public static final Integer[] columnLengths = new Integer[] {600, 50, 300, 50, 50, 50, 80};

   private MenuItem openMenuItem, viewRelationTreeItem, deleteRelationMenuItem, deleteArtifactMenuItem;

   private final Artifact artifact;
   private final RelationLabelProvider relationLabelProvider;
   private final ToolBar toolBar;
   private final OrcsTokenService tokenService;

   public RelationsComposite(IDirtiableEditor editor, Composite parent, int style, Artifact artifact) {
      this(editor, parent, style, artifact, null);
   }

   public RelationsComposite(IDirtiableEditor editor, Composite parent, int style, Artifact artifact, ToolBar toolBar) {
      super(parent, style);
      this.artifact = artifact;
      this.editor = editor;
      tokenService = ServiceUtil.getTokenService();
      this.relationLabelProvider = new RelationLabelProvider(artifact);
      this.toolBar = toolBar;

      createPartControl();
   }

   public TreeViewer getTreeViewer() {
      return treeViewer;
   }

   public void createPartControl() {
      setLayout(new GridLayout());
      setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createTreeArea(this);
      createColumns();
      packColumnData();

      needSelectedArtifactListener = new NeedSelectedArtifactListener();
      createPopupMenu();
      setHelpContexts();
   }

   private void createTreeArea(Composite parent) {
      treeViewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.NO_SCROLL);
      tree = treeViewer.getTree();
      tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      tree.setHeaderVisible(true);

      CellEditor[] editors = new CellEditor[columnNames.length];
      editors[1] = new TextCellEditor(tree);
      editors[2] = new TextCellEditor(tree);
      treeViewer.setCellEditors(editors);
      treeViewer.setCellModifier(new RelationCellModifier(treeViewer, editor));
      treeViewer.setColumnProperties(columnNames);
      treeViewer.setContentProvider(new RelationContentProvider(tokenService));
      treeViewer.setLabelProvider(relationLabelProvider);
      treeViewer.setUseHashlookup(true);
      treeViewer.setInput(artifact);
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            AWorkbench.getActivePage().getActivePart().getSite().setSelectionProvider(treeViewer);
         }
      });

      treeViewer.addDoubleClickListener(new DoubleClickListener());
      tree.addMouseListener(new MouseAdapter() {

         @Override
         public void mouseUp(MouseEvent e) {
            packColumnData();
         }

      });
      tree.addKeyListener(new KeySelectedListener());

      expandItemsThatHaveChildren();
      new RelationSkynetDragAndDrop(VIEW_ID, treeViewer, artifact, editor);
   }

   private void expandItemsThatHaveChildren() {
      //expand items that have children
      if (artifact.isHistorical()) {
         return;
      }
      Object[] types = ((ITreeContentProvider) treeViewer.getContentProvider()).getChildren(treeViewer.getInput());
      for (Object obj : types) {
         if (obj instanceof RelationTypeToken) {
            RelationTypeToken type = (RelationTypeToken) obj;
            try {
               if (RelationManager.getRelatedArtifactsCount(artifact, type, null) > 0) {
                  treeViewer.expandToLevel(obj, 1);
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }

   private void createColumns() {
      for (int index = 0; index < columnNames.length; index++) {
         TreeColumn column = new TreeColumn(tree, SWT.LEFT, index);
         column.setText(columnNames[index]);
         column.setWidth(columnLengths[index]);
      }
   }

   private void packColumnData() {
      TreeColumn[] columns = treeViewer.getTree().getColumns();
      for (TreeColumn column : columns) {
         column.pack();
      }
   }

   public void createPopupMenu() {
      Menu popupMenu = new Menu(treeViewer.getTree().getParent());
      popupMenu.addMenuListener(needSelectedArtifactListener);

      createOpenMenuItem(popupMenu);

      OpenContributionItem contrib = new OpenContributionItem(getClass().getSimpleName() + ".open", this);
      contrib.fill(popupMenu, -1, false);

      new MenuItem(popupMenu, SWT.SEPARATOR);
      createViewRelationTreeMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      if (Widgets.isAccessible(popupMenu)) {
         RelationOrderContributionItem contributionItem = new RelationOrderContributionItem(treeViewer);
         contributionItem.addListener(new SelectionListener() {

            @Override
            public void onSelected(RelationTypeSideSorter sorter, RelationSorter wasId, RelationSorter isId) {
               editor.onDirtied();
               treeViewer.refresh();
            }
         });
         contributionItem.fill(popupMenu, 0);
      }

      new MenuItem(popupMenu, SWT.SEPARATOR);
      createDeleteRelationMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createExpandAllMenuItem(popupMenu);
      createSelectAllMenuItem(popupMenu);

      new MenuItem(popupMenu, SWT.SEPARATOR);
      createDeleteArtifactMenuItem(popupMenu);

      tree.setMenu(popupMenu);
   }

   public class DoubleClickListener implements IDoubleClickListener {
      @Override
      public void doubleClick(DoubleClickEvent event) {
         openViewer();
      }
   }

   private void createDeleteRelationMenuItem(final Menu parentMenu) {
      deleteRelationMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      deleteRelationMenuItem.setText("&Delete Relation");
      deleteRelationMenuItem.setImage(ImageManager.getImage(FrameworkImage.DELETE));
      needSelectedArtifactListener.add(deleteRelationMenuItem);
      deleteRelationMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            performDeleteRelation();
         }
      });

      deleteRelationMenuItem.setEnabled(true);
   }

   private void createDeleteArtifactMenuItem(final Menu parentMenu) {
      deleteArtifactMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      deleteArtifactMenuItem.setText("&Delete Artifact");
      deleteArtifactMenuItem.setImage(ImageManager.getImage(FrameworkImage.DELETE));
      needSelectedArtifactListener.add(deleteArtifactMenuItem);
      deleteArtifactMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            performDeleteArtifact();
         }
      });

      deleteArtifactMenuItem.setEnabled(true);
   }

   private void createViewRelationTreeMenuItem(Menu menu) {
      viewRelationTreeItem = new MenuItem(menu, SWT.PUSH);
      viewRelationTreeItem.setText("&View Relation Table Report");
      viewRelationTreeItem.setImage(ImageManager.getImage(FrameworkImage.REPORT));
      viewRelationTreeItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            TreeViewerReport report =
               new TreeViewerReport("Relation View Report for " + artifact.getName(), treeViewer);
            new ArrayList<>();
            report.open();
         }
      });
   }

   private void createOpenMenuItem(Menu parentMenu) {
      openMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      openMenuItem.setText("Open");
      needSelectedArtifactListener.addArtifactEnabled(openMenuItem);
      openMenuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            openViewer();
         }
      });
   }

   private void openViewer() {
      for (Object object : ((IStructuredSelection) treeViewer.getSelection()).toArray()) {
         if (object instanceof WrapperForRelationLink) {
            WrapperForRelationLink link = (WrapperForRelationLink) object;
            try {
               RendererManager.open(link.getOther(), PresentationType.DEFAULT_OPEN);
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      }
   }

   @Override
   public Set<Artifact> getSelectedArtifacts() {
      Set<Artifact> selectedArtifacts = new HashSet<>();
      Iterator<?> iter = ((IStructuredSelection) treeViewer.getSelection()).iterator();
      while (iter.hasNext()) {
         Object object = iter.next();
         if (object instanceof WrapperForRelationLink) {
            WrapperForRelationLink wrapped = (WrapperForRelationLink) object;
            selectedArtifacts.add(wrapped.getOther());
         }
      }
      return selectedArtifacts;
   }

   private void createExpandAllMenuItem(Menu parentMenu) {
      MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
      menuItem.setText("Expand All\tCtrl+X");
      menuItem.setImage(ImageManager.getImage(FrameworkImage.EXPAND_ALL));
      menuItem.addSelectionListener(new ExpandListener());
   }

   public class ExpandListener extends SelectionAdapter {
      @Override
      public void widgetSelected(SelectionEvent event) {
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
         Iterator<?> iter = selection.iterator();
         while (iter.hasNext()) {
            treeViewer.expandToLevel(iter.next(), AbstractTreeViewer.ALL_LEVELS);
         }
         packColumnData();
      }
   }

   public class NeedSelectedArtifactListener implements MenuListener {
      Collection<MenuItem> accessControlitems;
      Collection<MenuItem> artEnabledOnlyitems;

      public NeedSelectedArtifactListener() {
         accessControlitems = new LinkedList<>();
         artEnabledOnlyitems = new LinkedList<>();
      }

      public void addArtifactEnabled(MenuItem item) {
         artEnabledOnlyitems.add(item);
      }

      public void add(MenuItem item) {
         accessControlitems.add(item);
      }

      @Override
      public void menuHidden(MenuEvent e) {
         // do nothing
      }

      @Override
      public void menuShown(MenuEvent e) {
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

         boolean isRelationType = false;
         if (selection.getFirstElement() instanceof RelationTypeSide || selection.getFirstElement() instanceof RelationTypeToken) {
            isRelationType = true;
         }
         boolean valid = selection.getFirstElement() instanceof WrapperForRelationLink;

         if (selection.getFirstElement() instanceof WrapperForRelationLink) {
            WrapperForRelationLink data = (WrapperForRelationLink) selection.getFirstElement();
            try {

               RelationTypeSide relationTypeSide = new RelationTypeSide(data.getRelationType(), data.getRelationSide());
               Set<Artifact> relArts = java.util.Collections.singleton(
                  data.getArtifactA().equals(artifact) ? data.getArtifactB() : data.getArtifactA());

               valid = ServiceUtil.accessControlService().hasRelationTypePermission(artifact, relationTypeSide, relArts,
                  PermissionEnum.WRITE, null).isSuccess();

            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

         for (MenuItem item : accessControlitems) {
            item.setEnabled(valid);
         }

         // Do not enable items for Relations
         for (MenuItem item : artEnabledOnlyitems) {
            item.setEnabled(!isRelationType);
         }
      }
   }

   private void createSelectAllMenuItem(Menu parentMenu) {
      MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
      menuItem.setText("&Select All\tCtrl+A");
      menuItem.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            treeViewer.getTree().selectAll();
         }
      });
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), AbstractTreeViewer.ALL_LEVELS);
      }
      packColumnData();
   }

   private void performDeleteArtifact() {
      try {
         Set<Artifact> artifactsToBeDeleted = getSelectedArtifacts();

         XResultData rd =
            ArtifactPersistenceManager.performDeleteArtifactChecks(artifactsToBeDeleted, new XResultData());
         if (rd.isErrors()) {
            XResultDataUI.report(rd, DELETE_ARTIFACTS);
            return;
         }

         //Ask if they are sure they want all artifacts to be deleted
         if (!artifactsToBeDeleted.isEmpty()) {
            SkynetTransaction transaction =
               TransactionManager.createTransaction(artifact.getBranch(), DELETE_ARTIFACTS);
            if (MessageDialog.openConfirm(Displays.getActiveShell(), DELETE_ARTIFACTS,
               "Delete Artifact (s)?\n\n\"" + Collections.toString(",",
                  artifactsToBeDeleted) + "\"\n\nNOTE: This will delete the artifact from the system.  Use \"Delete Relation\" to remove this artifact from the relation.")) {

               for (Artifact artifact : artifactsToBeDeleted) {
                  artifact.deleteAndPersist(transaction);
               }
            }
            transaction.execute();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      refresh();
   }

   private void performDeleteRelation() {
      try {
         Object[] objects = ((IStructuredSelection) treeViewer.getSelection()).toArray();
         for (Object object : objects) {
            if (hasWriteRelationTypePermission(artifact, object)) {
               if (object instanceof WrapperForRelationLink) {
                  WrapperForRelationLink wrapper = (WrapperForRelationLink) object;
                  RelationManager.deleteRelation(wrapper.getRelationType(), wrapper.getArtifactA(),
                     wrapper.getArtifactB());
                  Object parent = ((ITreeContentProvider) treeViewer.getContentProvider()).getParent(wrapper);
                  try {
                     refreshParent(parent);
                  } catch (org.eclipse.swt.SWTException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               } else if (object instanceof RelationTypeSideSorter) {
                  RelationTypeSideSorter group = (RelationTypeSideSorter) object;
                  RelationManager.deleteRelations(artifact, group.getRelationType(), group.getSide());
                  try {
                     refreshParent(group);
                  } catch (OseeCoreException ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, ex);
                  }
               }
            }
         }
         editor.onDirtied();
      } catch (Exception ex) {
         XResultData rd = new XResultData();
         rd.log("<h3>Error Deleting Relation</h3>");
         rd.logf("%s\n\n", ex.getLocalizedMessage());
         rd.logf("%s\n\n", Lib.exceptionToString(ex));
         ResultsEditor.open("Error Deleting Relation", rd);
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public void refresh() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!treeViewer.getTree().isDisposed()) {
               treeViewer.refresh();
               expandItemsThatHaveChildren();
               packColumnData();
            }
         }
      });
   }

   public void refreshParent(Object parent) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (parent != null) {
               treeViewer.update(parent, null);
               treeViewer.refresh(parent);
            } else {
               treeViewer.refresh();
            }
         }
      });
   }

   private final class KeySelectedListener implements KeyListener {
      @Override
      public void keyPressed(KeyEvent e) {
         // do nothing
      }

      @Override
      public void keyReleased(KeyEvent e) {
         if (e.keyCode == SWT.DEL) {
            performDeleteRelation();
         }
         if (e.keyCode == 'a' && e.stateMask == SWT.CONTROL) {
            treeViewer.getTree().selectAll();
         }
         if (e.keyCode == 'x' && e.stateMask == SWT.CONTROL) {
            expandAll((IStructuredSelection) treeViewer.getSelection());
         }
      }
   }

   public Artifact getArtifact() {
      return artifact;
   }

   public void refreshArtifact(Artifact newArtifact) {
      relationLabelProvider.setArtifact(newArtifact);
      treeViewer.setInput(newArtifact);
      expandItemsThatHaveChildren();
      refresh();
   }

   public static boolean hasWriteRelationTypePermission(Artifact toCheck, Object object) {
      boolean hasPermission = false;
      try {
         RelationTypeSide relationTypeSide = null;
         ArrayList<Artifact> artifacts = new ArrayList<>();
         if (object instanceof WrapperForRelationLink) {//used for ordering
            WrapperForRelationLink targetLink = (WrapperForRelationLink) object;
            relationTypeSide = new RelationTypeSide(targetLink.getRelationType(), targetLink.getRelationSide());
            artifacts.add(
               toCheck.equals(targetLink.getArtifactA()) ? targetLink.getArtifactB() : targetLink.getArtifactA());
         } else if (object instanceof RelationTypeSideSorter) {
            RelationTypeSideSorter group = (RelationTypeSideSorter) object;
            relationTypeSide = new RelationTypeSide(group.getRelationType(), group.getSide());
            artifacts.add(group.getArtifact());
         }

         hasPermission = ServiceUtil.accessControlService().hasRelationTypePermission(toCheck, relationTypeSide,
            artifacts, PermissionEnum.WRITE, null).isSuccess();
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
      return hasPermission;
   }

   private void setHelpContexts() {
      HelpUtil.setHelp(treeViewer.getControl(), OseeHelpContext.ARTIFACT_EDITOR__RELATIONS);
   }

   public ToolBar getToolBar() {
      return toolBar;
   }

}
