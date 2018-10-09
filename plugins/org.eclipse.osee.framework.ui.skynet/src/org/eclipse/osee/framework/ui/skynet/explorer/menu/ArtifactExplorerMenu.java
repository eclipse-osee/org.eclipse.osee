/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.explorer.menu;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.PresentationType;
import org.eclipse.osee.framework.core.model.access.PermissionStatus;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.AccessPolicy;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactContentProvider;
import org.eclipse.osee.framework.ui.skynet.ArtifactStructuredSelection;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.OpenContributionItem;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactNameConflictHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.ArtifactPasteOperation;
import org.eclipse.osee.framework.ui.skynet.branch.BranchSelectionDialog;
import org.eclipse.osee.framework.ui.skynet.dialogs.ArtifactPasteSpecialDialog;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.explorer.ArtifactExplorerLinkNode;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.internal.ServiceUtil;
import org.eclipse.osee.framework.ui.skynet.menu.ArtifactTreeViewerGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenu;
import org.eclipse.osee.framework.ui.skynet.menu.GlobalMenuPermissions;
import org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactClipboard;
import org.eclipse.osee.framework.ui.skynet.util.ArtifactPasteConfiguration;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredTreeArtifactTypeEntryDialog;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.MenuItems;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ExportResourcesAction;
import org.eclipse.ui.actions.ImportResourcesAction;

/**
 * @author Donald G. Dunne
 */
public class ArtifactExplorerMenu {

   private final TreeViewer treeViewer;
   private MenuItem createMenuItem;
   private CreateRelatedMenuItem createRelatedMenuItem;
   private MenuItem accessControlMenuItem;
   private MenuItem lockMenuItem;
   private MenuItem goIntoMenuItem;
   private MenuItem copyMenuItem;
   private MenuItem pasteMenuItem;
   private MenuItem pasteSpecialMenuItem;
   private MenuItem renameArtifactMenuItem;
   private MenuItem refreshMenuItem;
   private MenuItem findOnAnotherBranch;
   private IGlobalMenuHelper globalMenuHelper;
   private static final ArtifactClipboard artifactClipboard = new ArtifactClipboard(ArtifactExplorer.VIEW_ID);
   private NeedArtifactMenuListener needArtifactListener;
   private NeedProjectMenuListener needProjectListener;
   private final ArtifactExplorer artifactExplorer;
   private Text myTextBeingRenamed;

   public ArtifactExplorerMenu(ArtifactExplorer artifactExplorer) {
      this.artifactExplorer = artifactExplorer;
      treeViewer = artifactExplorer.getTreeViewer();
   }

   public void create() {
      globalMenuHelper = new ArtifactTreeViewerGlobalMenuHelper(treeViewer);
   }

   public void dispose() {
      artifactClipboard.dispose();

   }

   public void handleMenuShown(MenuEvent e) {
      // Use this menu listener until all menu items can be moved to GlobaMenu
      try {
         IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
         Object obj = selection.getFirstElement();
         AccessPolicy service = ServiceUtil.getAccessPolicy();
         boolean canModifyDH = false;
         boolean isArtifact = false;
         if (obj instanceof Artifact) {
            isArtifact = true;
            Artifact art = (Artifact) obj;
            canModifyDH = service.canRelationBeModified(art, null, CoreRelationTypes.Default_Hierarchical__Child,
               Level.FINE).matched();
         }
         boolean isBranchEditable =
            BranchManager.isEditable(getBranch()) && AccessControlManager.hasPermission(getBranch(),
               PermissionEnum.WRITE);

         GlobalMenuPermissions permiss = new GlobalMenuPermissions(globalMenuHelper);

         boolean locked = permiss.isLocked();
         if (isArtifact) {
            lockMenuItem.setText(locked ? "Unlock: (" + permiss.getSubjectFromLockedObjectName() + ")" : "Lock");
         }

         boolean writePermission = permiss.isWritePermission();
         boolean accessToRemoveLock = permiss.isAccessToRemoveLock();
         lockMenuItem.setEnabled(isArtifact && writePermission && (!locked || accessToRemoveLock));

         createMenuItem.setEnabled((obj == null || isBranchEditable) || (isArtifact && writePermission || canModifyDH));
         if (obj == null) {
            createMenuItem.setText("New Parent");
         } else if (isArtifact) {
            createMenuItem.setText("New Child");
         }

         goIntoMenuItem.setEnabled(isArtifact && permiss.isReadPermission());
         copyMenuItem.setEnabled(isArtifact && permiss.isReadPermission());

         boolean clipboardEmpty = artifactClipboard.isEmpty();
         boolean pasteEnabled = !clipboardEmpty && ((isArtifact && canModifyDH) || (obj == null && isBranchEditable));
         pasteMenuItem.setEnabled(pasteEnabled);
         pasteSpecialMenuItem.setEnabled(pasteEnabled);
         renameArtifactMenuItem.setEnabled(isArtifact && writePermission);
         findOnAnotherBranch.setEnabled(isArtifact);
         accessControlMenuItem.setEnabled(isArtifact);
         refreshMenuItem.setEnabled(isArtifact);

         createRelatedMenuItem.setCreateRelatedEnabled(obj, service);

      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }

   }

   public void setupPopupMenu() {

      Menu popupMenu = new Menu(treeViewer.getTree().getParent());
      needArtifactListener = new NeedArtifactMenuListener(artifactExplorer);
      needProjectListener = new NeedProjectMenuListener(artifactExplorer);
      popupMenu.addMenuListener(needArtifactListener);
      popupMenu.addMenuListener(needProjectListener);

      OpenContributionItem openWithMenu = new OpenContributionItem(getClass().getSimpleName() + ".open");
      openWithMenu.fill(popupMenu, -1);
      needArtifactListener.add(popupMenu.getItem(0));
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createFindOnDifferentBranchItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createNewChildMenuItem(popupMenu);
      createNewRelatedMenuItem(popupMenu);
      createGoIntoMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      new GlobalMenu(popupMenu, globalMenuHelper);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createRenameArtifactMenuItem(popupMenu);
      createRefreshMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createImportExportMenuItems(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createLockMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createCopyMenuItem(popupMenu);
      createPasteMenuItem(popupMenu);
      createPasteSpecialMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createExpandAllMenuItem(popupMenu);
      createCollapseAllMenuItem(popupMenu);

      createSelectAllMenuItem(popupMenu);
      new MenuItem(popupMenu, SWT.SEPARATOR);

      createAccessControlMenuItem(popupMenu);
      treeViewer.getTree().setMenu(popupMenu);
   }

   private BranchId getBranch() {
      return artifactExplorer.getBranch();
   }

   private void createNewChildMenuItem(Menu parentMenu) {
      createMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      needProjectListener.add(createMenuItem);
      createMenuItem.setText("&New Child");
      createMenuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

               Artifact parent = null;
               // If artifact is selected, check permissions of artifact
               if (selection.size() == 1) {
                  parent = getParent();

                  AccessPolicy policy = ServiceUtil.getAccessPolicy();

                  PermissionStatus status = policy.canRelationBeModified(parent, null,
                     CoreRelationTypes.Default_Hierarchical__Child, Level.FINE);
                  if (!status.matched()) {
                     MessageDialog.openError(AWorkbench.getActiveShell(), "New Child Error",
                        "Access control has restricted this action. The current user does not have sufficient permission to create relations on this artifact.");
                     return;
                  }
               }
               // check branch permissions
               else {
                  boolean isBranchEditable =
                     BranchManager.isEditable(getBranch()) && AccessControlManager.hasPermission(getBranch(),
                        PermissionEnum.WRITE);
                  if (!isBranchEditable) {
                     MessageDialog.openError(AWorkbench.getActiveShell(), "New Child Error",
                        "Access control has restricted this action. The current user does not have sufficient permission to create relations on this artifact.");
                     return;
                  }

                  parent = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(getBranch());
               }
               handleCreateChild(parent, treeViewer);
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

         private Artifact getParent() throws OseeCoreException {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

            if (selection.size() > 1) {
               throw new OseeCoreException("Please select a single artifact to create a new child.");
            }

            Iterator<?> itemsIter = selection.iterator();
            Artifact parent;
            if (!itemsIter.hasNext()) {
               parent = getExplorerRoot();
            } else {
               parent = (Artifact) itemsIter.next();
            }

            return parent;
         }
      });
   }

   private static Artifact handleCreateChild(Artifact parent, TreeViewer treeViewer) {
      return handleCreateChild(parent, ArtifactTypeManager.getConcreteArtifactTypes(parent.getBranch()), treeViewer,
         CoreRelationTypes.Default_Hierarchical__Child);
   }

   public static Artifact handleCreateChild(Artifact parent, Collection<? extends IArtifactType> validArtifactTypes, TreeViewer treeViewer, RelationTypeSide relationTypeSide) {
      FilteredTreeArtifactTypeEntryDialog dialog = getDialog(validArtifactTypes);
      if (dialog.open() == Window.OK) {
         IArtifactType type = dialog.getSelection();
         String name = dialog.getEntryValue();

         if (type == null) {
            AWorkbench.popup("Type not selected.");
            return null;
         } else if (!Strings.isValid(name)) {
            AWorkbench.popup("Name can not be empty.");
            return null;
         }

         SkynetTransaction transaction = TransactionManager.createTransaction(parent.getBranch(),
            String.format("Created new %s \"%s\" in artifact explorer", type.getName(), name));

         Artifact newChildArt = ArtifactTypeManager.addArtifact(type, parent.getBranch(), name);
         parent.addRelation(relationTypeSide, newChildArt);
         parent.persist(transaction);
         transaction.execute();
         RendererManager.open(newChildArt, PresentationType.GENERALIZED_EDIT);
         treeViewer.refresh();
         treeViewer.refresh(false);
         return newChildArt;
      }
      return null;
   }

   private static FilteredTreeArtifactTypeEntryDialog getDialog(Collection<? extends IArtifactType> validArtifactTypes) throws OseeCoreException {
      List<IArtifactType> artifactTypes = new ArrayList<>();
      for (IArtifactType artifactType : validArtifactTypes) {
         if (!((ArtifactType) artifactType).isAbstract() && ArtifactTypeManager.isUserCreationAllowed(artifactType)) {
            artifactTypes.add(artifactType);
         }
      }

      FilteredTreeArtifactTypeEntryDialog dialog = new FilteredTreeArtifactTypeEntryDialog("New Child",
         "Enter name and select Artifact type to create", "Artifact Name", artifactTypes);
      return dialog;
   }

   private Artifact getExplorerRoot() {
      return artifactExplorer.getExplorerRoot();
   }

   private void createRefreshMenuItem(Menu parentMenu) {
      refreshMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      refreshMenuItem.setText("Refresh");
      needArtifactListener.add(refreshMenuItem);

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      refreshMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent mySelectionEvent) {
            for (Artifact artifact : getSelection().toList()) {
               treeViewer.refresh(artifact);
            }
         }
      });
   }

   protected ArtifactStructuredSelection getSelection() {
      return artifactExplorer.getSelection();
   }

   /**
    * @author Jeff C. Phillips
    */
   public class ArtifactMenuListener implements MenuListener {

      @Override
      public void menuHidden(MenuEvent e) {
         // do nothing
      }

      @Override
      public void menuShown(MenuEvent e) {
         handleMenuShown(e);
      }

   }

   private void createRenameArtifactMenuItem(Menu parentMenu) {
      renameArtifactMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      renameArtifactMenuItem.setText("Rename Artifact");
      needArtifactListener.add(renameArtifactMenuItem);

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      renameArtifactMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent mySelectionEvent) {
            handleRenameArtifactSelectionEvent(mySelectionEvent);
         }
      });
   }

   private void handleRenameArtifactSelectionEvent(SelectionEvent mySelectionEvent) {
      // Clean up any previous editor control
      Control oldEditor = getMyTreeEditor().getEditor();

      if (oldEditor != null) {
         oldEditor.dispose();
      }

      // Identify the selected row, only allow input if there is a single
      // selected row
      Tree myTree = treeViewer.getTree();
      TreeItem[] selection = myTree.getSelection();

      if (selection.length != 1) {
         return;
      }

      final TreeItem myTreeItem = selection[0];

      if (myTreeItem == null) {
         return;
      }
      myTextBeingRenamed = new Text(myTree, SWT.BORDER);
      Object myTreeItemObject = myTreeItem.getData();
      myTextBeingRenamed.setText(((Artifact) myTreeItemObject).getName());
      myTextBeingRenamed.addFocusListener(new FocusAdapter() {
         @Override
         public void focusLost(FocusEvent e) {
            updateText(myTextBeingRenamed.getText(), myTreeItem);
            myTextBeingRenamed.dispose();

         }

         @Override
         public void focusGained(FocusEvent e) {
            // do nothing
         }
      });

      myTextBeingRenamed.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            if (e.character == SWT.CR) {
               updateText(myTextBeingRenamed.getText(), myTreeItem);
               myTextBeingRenamed.dispose();
            } else if (e.keyCode == SWT.ESC) {
               myTextBeingRenamed.dispose();
            }
         }
      });
      myTextBeingRenamed.selectAll();
      myTextBeingRenamed.setFocus();
      getMyTreeEditor().setEditor(myTextBeingRenamed, myTreeItem);
   }

   private void updateText(String newLabel, TreeItem item) {
      getMyTreeEditor().getItem().setText(newLabel);
      Object myTreeItemObject = item.getData();
      if (myTreeItemObject instanceof Artifact) {
         Artifact myArtifact = (Artifact) myTreeItemObject;
         try {
            myArtifact.setName(newLabel);
            myArtifact.persist(getClass().getSimpleName());
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
      treeViewer.refresh();
   }

   private TreeEditor getMyTreeEditor() {
      return artifactExplorer.getMyTreeEditor();
   }

   private void createNewRelatedMenuItem(Menu parentMenu) {
      createRelatedMenuItem = new CreateRelatedMenuItem(parentMenu, artifactExplorer);
      needProjectListener.add(createRelatedMenuItem.getMenuItem());
   }

   private void createGoIntoMenuItem(Menu parentMenu) {
      goIntoMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      goIntoMenuItem.setText("&Go Into");
      needArtifactListener.add(goIntoMenuItem);

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      goIntoMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent ev) {

            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            Iterator<?> itemsIter = selection.iterator();
            if (itemsIter.hasNext()) {
               try {
                  Object[] expanded = treeViewer.getExpandedElements();
                  artifactExplorer.explore((Artifact) itemsIter.next());
                  treeViewer.setExpandedElements(expanded);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      });
   }

   private void createFindOnDifferentBranchItem(Menu parentMenu) {
      findOnAnotherBranch = new MenuItem(parentMenu, SWT.PUSH);
      findOnAnotherBranch.setText("Reveal On Another Branch");
      needArtifactListener.add(findOnAnotherBranch);

      ArtifactMenuListener listener = new ArtifactMenuListener();
      parentMenu.addMenuListener(listener);
      findOnAnotherBranch.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent ev) {
            BranchId branch = BranchSelectionDialog.getBranchFromUser();
            if (branch != null) {
               for (Artifact artifact : getSelection().toList()) {
                  try {
                     ArtifactExplorer.revealArtifact(ArtifactQuery.getArtifactFromId(artifact.getArtId(), branch));
                  } catch (OseeCoreException ex) {
                     OseeLog.logf(Activator.class, OseeLevel.SEVERE_POPUP,
                        "Could not find Artifact \'%s\' on Branch \'%s\'", artifact.getName(), branch);
                  }
               }

            }
         }
      });
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

   private void createImportExportMenuItems(Menu parentMenu) {
      MenuItems.createMenuItem(parentMenu, SWT.PUSH, new ImportResourcesAction(getViewSite().getWorkbenchWindow()));
      MenuItems.createMenuItem(parentMenu, SWT.PUSH, new ExportResourcesAction(getViewSite().getWorkbenchWindow()));
   }

   private IViewSite getViewSite() {
      return artifactExplorer.getViewSite();
   }

   private void createAccessControlMenuItem(Menu parentMenu) {
      accessControlMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      accessControlMenuItem.setImage(ImageManager.getImage(FrameworkImage.AUTHENTICATED));
      accessControlMenuItem.setText("&Access Control ");
      // accessControlMenuItem.setEnabled(false);
      accessControlMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            Artifact selectedArtifact = (Artifact) selection.getFirstElement();
            try {
               if (selectedArtifact != null) {
                  PolicyDialog pd = new PolicyDialog(Displays.getActiveShell(), selectedArtifact);
                  pd.open();
                  artifactExplorer.refreshBranchWarning();
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
      });
   }

   private void createLockMenuItem(Menu parentMenu) {
      lockMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      lockMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            Iterator<?> iterator = selection.iterator();
            Set<Artifact> lockArtifacts = new HashSet<>();
            Set<Artifact> unlockArtifacts = new HashSet<>();
            while (iterator.hasNext()) {
               try {
                  Artifact object = (Artifact) iterator.next();
                  if (new GlobalMenuPermissions(object).isLocked()) {
                     unlockArtifacts.add(object);
                  } else {
                     lockArtifacts.add(object);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
               }
            }

            try {
               if (!unlockArtifacts.isEmpty()) {
                  AccessControlManager.unLockObjects(unlockArtifacts, UserManager.getUser());
               }
               if (!lockArtifacts.isEmpty()) {
                  AccessControlManager.lockObjects(lockArtifacts, UserManager.getUser());
               }
            } catch (Exception ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }

      });
   }

   private void createCopyMenuItem(Menu parentMenu) {
      copyMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      copyMenuItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_COPY));
      copyMenuItem.setText("Copy \tCtrl+C");
      copyMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               performCopy();
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   private void performCopy() throws OseeCoreException {
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      List<Artifact> artifactTransferData = new ArrayList<>();
      Artifact artifact;

      if (selection != null && !selection.isEmpty()) {
         for (Object object : selection.toArray()) {
            if (object instanceof Artifact) {
               artifact = (Artifact) object;
               if (!ArtifactTypeManager.isUserCreationAllowed(artifact.getArtifactType())) {
                  throw new OseeArgumentException("Artifact Type [%s] can not be copied",
                     artifact.getArtifactTypeName());
               }
               artifactTransferData.add(artifact);
            }
         }
         artifactClipboard.setArtifactsToClipboard(ServiceUtil.getAccessPolicy(), artifactTransferData);
      }
   }

   private void createPasteMenuItem(Menu parentMenu) {
      pasteMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      pasteMenuItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_PASTE));
      pasteMenuItem.setText("Paste \tCtrl+V");
      pasteMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            performPasteOperation(false);
         }
      });
   }

   private void createPasteSpecialMenuItem(Menu parentMenu) {
      pasteSpecialMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      pasteSpecialMenuItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_PASTE));
      pasteSpecialMenuItem.setText("Paste Special... \tCtrl+S");
      pasteSpecialMenuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            performPasteOperation(true);
         }

      });
   }

   private void performPasteOperation(boolean isPasteSpecial) {
      boolean performPaste = true;
      Artifact destinationArtifact = null;
      IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      if (selection != null) {
         if (selection.isEmpty()) {
            destinationArtifact =
               ArtifactQuery.getArtifactFromToken(CoreArtifactTokens.DefaultHierarchyRoot, getBranch());
         } else if (selection.getFirstElement() instanceof Artifact) {
            Object object = selection.getFirstElement();
            Artifact artifact = (Artifact) object;
            if (!ArtifactTypeManager.isUserCreationAllowed(artifact.getArtifactType())) {
               throw new OseeArgumentException("Artifact Type [%s] can not be copied", artifact.getArtifactTypeName());
            }
            destinationArtifact = (Artifact) object;
         }
      }

      ArtifactPasteConfiguration config = new ArtifactPasteConfiguration();

      if (isPasteSpecial) {
         Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
         List<Artifact> copiedArtifacts = artifactClipboard.getCopiedContents();
         ArtifactPasteSpecialDialog dialog =
            new ArtifactPasteSpecialDialog(shell, config, destinationArtifact, copiedArtifacts);
         performPaste = dialog.open() == Window.OK;
      }

      if (performPaste) {
         Operations.executeAsJob(new ArtifactPasteOperation(config, destinationArtifact,
            artifactClipboard.getCopiedContents(), new ArtifactNameConflictHandler()), true);
      }
   }

   private void createExpandAllMenuItem(Menu parentMenu) {
      MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
      menuItem.setImage(ImageManager.getImage(FrameworkImage.EXPAND_ALL));
      menuItem.setText("Expand All");
      menuItem.addSelectionListener(new ExpandListener());
   }

   public class ExpandListener extends SelectionAdapter {
      @Override
      public void widgetSelected(SelectionEvent event) {
         expandAll((IStructuredSelection) treeViewer.getSelection());
      }
   }

   private void expandAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         Object obj = iter.next();
         expandAll(obj);
      }
   }

   private void expandAll(Object object) {
      if (!(object instanceof ArtifactExplorerLinkNode)) {
         treeViewer.expandToLevel(object, 1);
         for (Object child : ((ArtifactContentProvider) treeViewer.getContentProvider()).getChildren(object)) {
            expandAll(child);
         }
      }
   }

   private void createCollapseAllMenuItem(Menu parentMenu) {
      MenuItem menuItem = new MenuItem(parentMenu, SWT.PUSH);
      menuItem.setImage(ImageManager.getImage(FrameworkImage.COLLAPSE_ALL));
      menuItem.setText("Collapse All\tCtrl-");
      menuItem.addSelectionListener(new CollapseListener());
   }

   public class CollapseListener extends SelectionAdapter {
      @Override
      public void widgetSelected(SelectionEvent event) {
         collapseAll((IStructuredSelection) treeViewer.getSelection());
      }
   }

   private void collapseAll(IStructuredSelection selection) {
      Iterator<?> iter = selection.iterator();
      while (iter.hasNext()) {
         Object obj = iter.next();
         collapseAll(obj);
      }
   }

   private void collapseAll(Object object) {
      if (!(object instanceof ArtifactExplorerLinkNode)) {
         treeViewer.collapseToLevel(object, 1);
         for (Object child : ((ArtifactContentProvider) treeViewer.getContentProvider()).getChildren(object)) {
            collapseAll(child);
         }
      }
   }

}
