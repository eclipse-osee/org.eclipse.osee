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

package org.eclipse.osee.framework.ui.skynet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactData;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.relation.DynamicRelationLink;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLink;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.RelationLinkGroup;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.relation.RelationPersistenceManager;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.artifact.RelationGroupDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.relation.explorer.RelationExplorerWindow;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;

/**
 * @author Ryan D. Brooks
 */
public class RelationsComposite extends Composite implements IEventReceiver {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(RelationsComposite.class);
   private TreeViewer treeViewer;
   private Tree tree;
   private NeedSelectedArtifactListener needSelectedArtifactListener;
   private NeedArtifactMenuListener needArtifactListener;
   private IDirtiableEditor editor;
   public static final String VIEW_ID = "osee.define.relation.RelationExplorer";
   public static final String[] columnNames = new String[] {" ", "Rationale"};
   // the index of column order
   private static int COLUMN_ORDER = 1;

   private MenuItem openMenuItem;
   private MenuItem editMenuItem;
   private MenuItem newMenuItem;
   private MenuItem viewRelationTreeItem;
   private MenuItem deleteMenuItem;
   private MenuItem massEditMenuItem;
   private Artifact artifact;
   private SkynetEventManager eventManager;
   private final boolean readOnly;
   private RelationLabelProvider relationLabelProvider;
   private ToolBar toolBar;

   private Map<Integer, IRelationLink> artifactToLinkMap;

   public RelationsComposite(IDirtiableEditor editor, Composite parent, int style, Artifact artifact) {
      this(editor, parent, style, artifact, false, null);
   }

   public RelationsComposite(IDirtiableEditor editor, Composite parent, int style, Artifact artifact, ToolBar toolBar) {
      this(editor, parent, style, artifact, false, toolBar);
   }

   public RelationsComposite(IDirtiableEditor editor, Composite parent, int style, Artifact artifact, boolean readOnly, ToolBar toolBar) {
      super(parent, style);
      this.readOnly = readOnly;

      if (artifact == null) throw new IllegalArgumentException("Can not edit a null artifact");

      this.artifact = artifact;
      this.editor = editor;
      this.relationLabelProvider = new RelationLabelProvider(artifact);
      this.artifactToLinkMap = new HashMap<Integer, IRelationLink>();

      createPartControl();
      eventManager = SkynetEventManager.getInstance();
      eventManager.register(RelationModifiedEvent.class, artifact, this);

      this.toolBar = toolBar;
   }

   public TreeViewer getTreeViewer() {
      return treeViewer;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.IWorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    */
   public void createPartControl() {
      this.setLayout(new GridLayout());
      this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createTreeArea(this);
      createColumns();
      packColumnData();

      needSelectedArtifactListener = new NeedSelectedArtifactListener();
      needArtifactListener = new NeedArtifactMenuListener();
      tree.setMenu(getPopupMenu());

      setHelpContexts();
   }

   private void createTreeArea(Composite parent) {
      treeViewer = new TreeViewer(parent, SWT.FULL_SELECTION | SWT.MULTI);
      tree = treeViewer.getTree();
      tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      tree.setHeaderVisible(true);

      CellEditor[] editors = new CellEditor[columnNames.length];
      editors[1] = new TextCellEditor(tree);
      treeViewer.setCellEditors(editors);
      treeViewer.setCellModifier(new RelationCellModifier(treeViewer));
      treeViewer.setColumnProperties(columnNames);
      treeViewer.setContentProvider(new RelationContentProvider(this));
      treeViewer.setLabelProvider(relationLabelProvider);
      // treeViewer.addSelectionChangedListener(this.getParent());
      treeViewer.setSorter(new LabelSorter() {
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            if (e1 instanceof IRelationLink && e2 instanceof IRelationLink) {
               IRelationLink link1 = (IRelationLink) e1;
               IRelationLink link2 = (IRelationLink) e2;

               float val;
               if (link1.getArtifactA() == artifact)
                  val = link1.getAOrder() - link2.getAOrder();
               else
                  val = link1.getBOrder() - link2.getBOrder();

               if (val > 0)
                  return 1;
               else
                  return -1;
            }
            return super.compare(viewer, e1, e2);
         }
      });
      treeViewer.setUseHashlookup(true);
      treeViewer.setInput(artifact);

      treeViewer.addDoubleClickListener(new DoubleClickListener());
      tree.addKeyListener(new keySelectedListener());
      treeViewer.addTreeListener(new ITreeViewerListener() {

         public void treeCollapsed(TreeExpansionEvent event) {
            Display.getCurrent().asyncExec(new Runnable() {
               public void run() {
                  packColumnData();
               }
            });

         }

         public void treeExpanded(TreeExpansionEvent event) {
            Display.getCurrent().asyncExec(new Runnable() {
               public void run() {
                  packColumnData();
               }
            });
         }

      });

      tree.addMouseMoveListener(new MouseMoveListener() {

         public void mouseMove(MouseEvent e) {
            // System.out.println("MouseEvent at " + e.x + "," + e.y);
            // TreeItem item = tree.getItem(new Point(e.x, e.y));
            // if (item != null)
            // System.out.println("WOOT");
            // tree.setInsertMark(item, true);

         }
      });

      new RelationSkynetDragAndDrop(tree, VIEW_ID);
   }

   private void createColumns() {
      for (int index = 0; index < columnNames.length; index++) {
         TreeColumn column = new TreeColumn(tree, SWT.LEFT, index);
         column.setText(columnNames[index]);
      }
   }

   private void packColumnData() {
      TreeColumn[] columns = treeViewer.getTree().getColumns();
      for (TreeColumn column : columns) {
         column.pack();
      }
   }

   public Menu getPopupMenu() {
      Menu popupMenu = new Menu(this);
      popupMenu.addMenuListener(needSelectedArtifactListener);
      popupMenu.addMenuListener(needArtifactListener);

      createOpenMenuItem(popupMenu);
      if (!readOnly) {
         createEditMenuItem(popupMenu);
         createMassEditMenuItem(popupMenu);
      }
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createNewMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createViewRelationTreeMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);
      createDeleteMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createExpandAllMenuItem(popupMenu);
      createSelectAllMenuItem(popupMenu);

      popupMenu.addMenuListener(new RelationMenuListener());
      return popupMenu;
   }

   public class DoubleClickListener implements IDoubleClickListener {
      public void doubleClick(DoubleClickEvent event) {
         openViewer((IStructuredSelection) event.getSelection());
      }
   }

   private class CreateNewRelationSelectedListener implements Listener {
      private Shell shell;

      public CreateNewRelationSelectedListener(Shell shell) {
         this.shell = shell;
      }

      public void widgetDefaultSelected(SelectionEvent e) {
      }

      public void handleEvent(Event event) {
         try {
            boolean isNewRelationCreated = false;
            IRelationLinkDescriptor selectedDescriptor = (IRelationLinkDescriptor) ((MenuItem) event.widget).getData();

            boolean canBeOnSideA = canBeOnSide(selectedDescriptor, true);
            boolean canBeOnSideB = canBeOnSide(selectedDescriptor, false);

            if (canBeOnSideA && canBeOnSideB) {
               RelationGroupDialog dialog =
                     new RelationGroupDialog(shell, "Select New Relation Side", null,
                           "Please select the relation side onto which you intend to drag related artifacts.",
                           selectedDescriptor, artifact);
               int response = dialog.open();
               if (response == 0) isNewRelationCreated = true;

            } else if (canBeOnSideA || canBeOnSideB) {
               artifact.getLinkManager().ensureRelationGroupExists(selectedDescriptor, canBeOnSideB);
               isNewRelationCreated = true;
            }

            if (isNewRelationCreated) {
               treeViewer.refresh();
               treeViewer.expandToLevel(selectedDescriptor, 1);
               editor.onDirtied();
            }

            packColumnData();
         } catch (SQLException ex) {
            SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
      }
   }

   private boolean canBeOnSide(IRelationLinkDescriptor descriptor, boolean sideA) throws SQLException {
      int sideMax = descriptor.getRestrictionSizeFor(artifact.getArtTypeId(), sideA);
      RelationLinkGroup otherSideGroup = artifact.getLinkManager().getSideGroup(descriptor, !sideA);

      return sideMax > 0 && otherSideGroup == null;
   }

   private void createDeleteMenuItem(final Menu parentMenu) {
      deleteMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      deleteMenuItem.setText("&Delete");
      deleteMenuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

            performDelete(selection);
         }
      });

      deleteMenuItem.setEnabled(true);
   }

   private void createMassEditMenuItem(final Menu parentMenu) {
      massEditMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      massEditMenuItem.setText("&Mass Edit");
      massEditMenuItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

            performMassEdit(selection);
         }
      });

      massEditMenuItem.setEnabled(true);
   }

   private void createViewRelationTreeMenuItem(Menu menu) {
      viewRelationTreeItem = new MenuItem(menu, SWT.PUSH);
      viewRelationTreeItem.setText("&View Relation Table Report");
      viewRelationTreeItem.addSelectionListener(new SelectionAdapter() {

         public void widgetSelected(SelectionEvent e) {
            TreeViewerReport report =
                  new TreeViewerReport("Relation View Report for " + artifact.getDescriptiveName(), treeViewer);
            ArrayList<Integer> ignoreCols = new ArrayList<Integer>();
            ignoreCols.add(COLUMN_ORDER);
            report.setIgnoreColumns(ignoreCols);
            report.open();
         }
      });
   }

   private void createNewMenuItem(final Menu parentMenu) {
      newMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      newMenuItem.setText("&New Relation");
      Menu newMenu = new Menu(parentMenu.getShell(), SWT.DROP_DOWN);
      boolean isRelatable = false;

      for (IRelationLinkDescriptor relationDescriptor : RelationPersistenceManager.getInstance().getIRelationLinkDescriptors(
            artifact.getDescriptor())) {
         MenuItem mItem = new MenuItem(newMenu, SWT.PUSH);
         mItem.setData(relationDescriptor);
         mItem.setText(relationDescriptor.getName());
         mItem.addListener(SWT.Selection, new CreateNewRelationSelectedListener(parentMenu.getShell()));

         isRelatable = true;
      }

      if (isRelatable) {
         newMenuItem.setText("&New Relation");
      } else {
         newMenuItem.setText("New Relation - No valid relations");
      }

      newMenuItem.setEnabled(isRelatable);
      newMenuItem.setMenu(newMenu);

      newMenu.addMenuListener(new MenuAdapter() {

         public void menuShown(MenuEvent e) {
            try {
               Menu dropDown = (Menu) e.getSource();
               MenuItem[] items = dropDown.getItems();
               boolean canBeOnSideA;
               boolean canBeOnSideB;

               for (MenuItem item : items) {
                  IRelationLinkDescriptor descriptor = (IRelationLinkDescriptor) item.getData();
                  canBeOnSideA = canBeOnSide(descriptor, true);
                  canBeOnSideB = canBeOnSide(descriptor, false);

                  String title = descriptor.getName();
                  if (canBeOnSideA && canBeOnSideB)
                     title += "...";
                  else if (canBeOnSideA)
                     title += " (" + descriptor.getSideBName() + ")";
                  else if (canBeOnSideB) title += " (" + descriptor.getSideAName() + ")";

                  item.setText(title);

                  item.setEnabled(canBeOnSideA || canBeOnSideB);
               }
            } catch (SQLException ex) {
               SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }

      });
   }

   private void createOpenMenuItem(Menu parentMenu) {
      openMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      openMenuItem.setText("Open");

      needArtifactListener.add(openMenuItem);
      needSelectedArtifactListener.add(openMenuItem);
      openMenuItem.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            openViewer((IStructuredSelection) treeViewer.getSelection());
         }
      });
   }

   private void openViewer(IStructuredSelection selection) {
      // TODO: check permission
      Object object = selection.getFirstElement();
      Artifact selectedArtifact;

      if (object instanceof IRelationLink) {
         IRelationLink link = (IRelationLink) object;
         selectedArtifact = (link.getArtifactA() == artifact) ? link.getArtifactB() : link.getArtifactA();

         ArtifactEditor.editArtifact(selectedArtifact);
      }
   }

   private void performMassEdit(IStructuredSelection selection) {
      // TODO: check permission
      Set<Artifact> selectedArtifacts = new HashSet<Artifact>();
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         Object object = iter.next();
         if (object instanceof IRelationLink) {
            IRelationLink link = (IRelationLink) object;
            selectedArtifacts.add(link.getArtifactB());
         }
      }
      MassArtifactEditor.editArtifacts("Mass Edit", selectedArtifacts);
   }

   private void createEditMenuItem(Menu parentMenu) {
      editMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      editMenuItem.setText("&Edit");

      needArtifactListener.add(editMenuItem);
      editMenuItem.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            Object object = selection.getFirstElement();

            if (object instanceof Artifact) {
               RendererManager.getInstance().editInJob((Artifact) object);
            }
         }
      });
   }

   private void createExpandAllMenuItem(Menu parentMenu) {
      MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
      menuItem.setText("Expand All\tCtrl+X");
      menuItem.addSelectionListener(new ExpandListener());
   }

   public class ExpandListener extends SelectionAdapter {
      public void widgetSelected(SelectionEvent event) {
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
         Iterator<?> iter = selection.iterator();
         while (iter.hasNext()) {
            treeViewer.expandToLevel(iter.next(), TreeViewer.ALL_LEVELS);
         }
         packColumnData();
      }
   }

   public class NeedSelectedArtifactListener implements MenuListener {
      Collection<MenuItem> items;

      public NeedSelectedArtifactListener() {
         this.items = new LinkedList<MenuItem>();
      }

      public void add(MenuItem item) {
         items.add(item);
      }

      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
         boolean valid = selection.getFirstElement() instanceof Artifact;
         for (MenuItem item : items)
            item.setEnabled(valid);
      }
   }

   public class NeedArtifactMenuListener implements MenuListener {
      Collection<MenuItem> items;

      public NeedArtifactMenuListener() {
         this.items = new LinkedList<MenuItem>();
      }

      public void add(MenuItem item) {
         items.add(item);
      }

      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         boolean valid = treeViewer.getInput() instanceof Artifact;
         for (MenuItem item : items)
            item.setEnabled(valid);
      }
   }

   public class RelationMenuListener implements MenuListener {
      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.swt.events.ArmListener#widgetArmed(org.eclipse.swt.events.ArmEvent)
       */
      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         // check permission
      }
   }

   private void createSelectAllMenuItem(Menu parentMenu) {
      MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
      menuItem.setText("&Select All\tCtrl+A");
      menuItem.addListener(SWT.Selection, new Listener() {
         public void handleEvent(org.eclipse.swt.widgets.Event event) {
            treeViewer.getTree().selectAll();
         }
      });
   }

   @Override
   public void dispose() {
      super.dispose();
      eventManager.unRegisterAll(this);
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         treeViewer.expandToLevel(iter.next(), TreeViewer.ALL_LEVELS);
      }
      this.packColumnData();
   }

   /**
    * Performs the deletion functionality
    * 
    * @param selection
    */
   private void performDelete(IStructuredSelection selection) {
      Object object = selection.getFirstElement();

      try {
         if (object instanceof IRelationLink) {
            ((IRelationLink) object).delete();
         }

         else if (object instanceof IRelationLinkDescriptor) {
            IRelationLinkDescriptor descriptor = (IRelationLinkDescriptor) object;
            artifact.getLinkManager().deleteGroups(descriptor);
         }

         else if (object instanceof RelationLinkGroup) {
            RelationLinkGroup group = (RelationLinkGroup) object;
            artifact.getLinkManager().deleteGroupSide(group);
         }
      } catch (SQLException ex) {
         OSEELog.logException(SkynetGuiPlugin.class, ex, true);
      }

      refresh();
   }

   public void refresh() {
      if (!treeViewer.getTree().isDisposed()) {
         treeViewer.refresh();
         packColumnData();
      }
   }

   private class keySelectedListener implements KeyListener {
      public void keyPressed(KeyEvent e) {
         if (e.keyCode == SWT.DEL) {
            performDelete((IStructuredSelection) treeViewer.getSelection());
         }
         if (e.keyCode == 'a' && e.stateMask == SWT.CONTROL) {
            treeViewer.getTree().selectAll();
         }
         if (e.keyCode == 'x' && e.stateMask == SWT.CONTROL) {
            expandAll((IStructuredSelection) treeViewer.getSelection());
         }
      }

      public void keyReleased(KeyEvent e) {
      }
   }

   /**
    * @return Returns the artifact.
    */
   public Artifact getArtifact() {
      return artifact;
   }

   public void onEvent(org.eclipse.osee.framework.ui.plugin.event.Event event) {
      if (treeViewer != null && treeViewer.getInput() instanceof Artifact) refresh();
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }

   public void refreshArtifact(Artifact newArtifact) {
      relationLabelProvider.setArtifact(newArtifact);
      treeViewer.setInput(newArtifact);

      refresh();
   }

   private class RelationSkynetDragAndDrop extends SkynetDragAndDrop {
      boolean isFeedbackAfter = false;

      public RelationSkynetDragAndDrop(Tree tree, String viewId) {
         super(tree, viewId);
      }

      @Override
      public Artifact[] getArtifacts() {
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
         Iterator<?> i = selection.iterator();
         Artifact[] artifacts = null;

         if (i.hasNext()) {
            Artifact selectedArtifact = null;
            Object object = i.next();

            // get other artifact from link
            if (object instanceof DynamicRelationLink) {
               DynamicRelationLink link;
               String sideName;

               Object[] objects = selection.toArray();
               artifacts = new Artifact[objects.length];

               for (int index = 0; index < objects.length; index++) {
                  link = (DynamicRelationLink) objects[index];
                  sideName = link.getSideNameForOtherArtifact(artifact);
                  selectedArtifact = link.getArtifact(sideName);
                  artifacts[index] = selectedArtifact;

                  artifactToLinkMap.put(selectedArtifact.getArtId(), link);
               }
            }
         }
         return artifacts;
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         Tree tree = treeViewer.getTree();
         TreeItem selected = tree.getItem(treeViewer.getTree().toControl(event.x, event.y));

         event.feedback = DND.FEEDBACK_EXPAND;
         event.detail = DND.DROP_NONE;

         if (selected != null && selected.getData() instanceof RelationLinkGroup) {
            event.detail = DND.DROP_COPY;
            tree.setInsertMark(null, false);
         } else if (selected != null && selected.getData() instanceof IRelationLink) {
            IRelationLink targetLink = (IRelationLink) selected.getData();
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            Object obj = selection.getFirstElement();
            if (obj instanceof IRelationLink) {
               IRelationLink dropTarget = (IRelationLink) obj;

               // the links must be in the same group
               if ((targetLink.getLinkDescriptor().getName() + targetLink.getSideNameForOtherArtifact(artifact)).equals(dropTarget.getLinkDescriptor().getName() + dropTarget.getSideNameForOtherArtifact(artifact))) {
                  if (isFeedbackAfter) {
                     event.feedback = DND.FEEDBACK_INSERT_AFTER;
                  } else {
                     event.feedback = DND.FEEDBACK_INSERT_BEFORE;
                  }
                  event.detail = DND.DROP_MOVE;
               }
            }
         } else {
            tree.setInsertMark(null, false);
         }
      }

      @Override
      public void operationChanged(DropTargetEvent event) {
         if (!isCtrlPressed(event)) {
            isFeedbackAfter = false;
         }
      }

      private boolean isCtrlPressed(DropTargetEvent event) {
         boolean ctrPressed = (event.detail == 1);

         if (ctrPressed) {
            isFeedbackAfter = true;
         }
         return ctrPressed;
      }

      @Override
      public void performDrop(DropTargetEvent event) {
         TreeItem selected = treeViewer.getTree().getItem(treeViewer.getTree().toControl(event.x, event.y));
         Object object = selected.getData();

         if (object instanceof IRelationLink) {
            IRelationLink targetLink = (IRelationLink) object;
            Artifact transferredArtifact = ((ArtifactData) event.data).getArtifacts()[0];
            IRelationLink dropLink = artifactToLinkMap.remove(transferredArtifact.getArtId());
            RelationLinkGroup group;

            try {
               group =
                     artifact.getLinkManager().getSideGroup(dropLink.getLinkDescriptor(),
                           transferredArtifact.equals(dropLink.getArtifactA()));

               group.moveLink(targetLink, dropLink, !isFeedbackAfter);
               treeViewer.refresh();
               editor.onDirtied();
            } catch (SQLException ex) {
               logger.log(Level.SEVERE, ex.toString(), true);
            }
         } else {
            RelationLinkGroup group = (RelationLinkGroup) selected.getData();
            RelationExplorerWindow window = new RelationExplorerWindow(treeViewer, group);

            ArtifactDragDropSupport.performDragDrop(event, window,
                  PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
            window.createArtifactInformationBox(null);
         }

         isFeedbackAfter = false;
      }
   }

   private void setHelpContexts() {
      SkynetGuiPlugin.getInstance().setHelp(treeViewer.getControl(), "relation_page_tree_viewer");
   }

   /**
    * @return the toolBar
    */
   public ToolBar getToolBar() {
      return toolBar;
   }
}