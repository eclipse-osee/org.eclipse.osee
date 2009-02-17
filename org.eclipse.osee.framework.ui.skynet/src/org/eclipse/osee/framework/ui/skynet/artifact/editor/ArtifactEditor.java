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

package org.eclipse.osee.framework.ui.skynet.artifact.editor;

import java.net.URL;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactURL;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.AccessControlEventType;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IAccessControlEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactModifiedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsChangeTypeEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.IRelationModifiedEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationModType;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.AttributesComposite;
import org.eclipse.osee.framework.ui.skynet.OpenWithMenuListener;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.branch.BranchView;
import org.eclipse.osee.framework.ui.skynet.render.PresentationType;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.widgets.xHistory.HistoryView;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactEditor extends MultiPageEditorPart implements IDirtiableEditor, IArtifactsPurgedEventListener, IBranchEventListener, IAccessControlEventListener, IArtifactModifiedEventListener, IArtifactsChangeTypeEventListener, IRelationModifiedEventListener, IFrameworkTransactionEventListener, IActionable {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor";
   private int attributesPageIndex;
   private int newAttributesPageIndex;
   private int relationsPageIndex;
   private int detailsCompositeIndex;
   private Artifact artifact;
   private final MultiPageEditorPart editor;
   private RelationsComposite relationsComposite;
   private AttributesComposite attributeComposite;
   private NewAttributesComposite newAttributeComposite;
   private DetailsBrowserComposite detailsComposite;

   private static final int REVEAL_ARTIFACT_INDEX = 2;
   private static final int EDIT_ARTIFACT_INDEX = 4;

   public ArtifactEditor() {
      super();
      editor = this;
   }

   public RelationsComposite getRelationsComposite() {
      return relationsComposite;
   }

   public static void editArtifacts(final Collection<Artifact> artifacts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               for (Artifact artifact : artifacts) {
                  if (!AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ)) {
                     OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                           "The user " + UserManager.getUser() + " does not have read access to " + artifact);
                  } else
                     AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   public static void editArtifact(final Artifact artifact) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               if (!AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ)) {
                  OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP,
                        "The user " + UserManager.getUser() + " does not have read access to " + artifact);
               } else if (artifact != null) {
                  AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   @Override
   public boolean isDirty() {
      return reportIsDirty().isTrue();
   }

   public Result reportIsDirty() {
      if (artifact.isDeleted()) return Result.FalseResult;

      try {
         if (artifact.isReadOnly()) return Result.FalseResult;
         Result result = artifact.reportIsDirty(true);
         if (result.isTrue()) return result;

         //TODO The new attribute composite dirty logic is always returning true ....
         if (false) {
            result = newAttributeComposite.isDirty();
            System.out.println("New Attribute Composite - isDirt => " + result);
            if (result.isTrue()) {
               return result;
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      return Result.FalseResult;
   }

   public void onDirtied() {
      Display.getDefault().asyncExec(new Runnable() {

         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.MultiPageEditorPart#createPages()
    */
   @Override
   protected void createPages() {
      OseeContributionItem.addTo(this, true);

      attributesPageIndex = createAttributesPage();
      setPageText(attributesPageIndex, "Attributes");

      if (false) {
         newAttributesPageIndex = createNewAttributesPage();
         setPageText(newAttributesPageIndex, "Attributes2");
      }

      relationsPageIndex = createRelationsPage();
      setPageText(relationsPageIndex, "Relations");

      detailsCompositeIndex = createDetailsPage();
      setPageText(detailsCompositeIndex, "Details");

      setPartName(getEditorInput().getName());
      setTitleImage(artifact.getImage());

      OseeEventManager.addListener(this);

   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         artifact.persistAttributesAndRelations();
         firePropertyChange(PROP_DIRTY);
      } catch (OseeCoreException ex) {
         onDirtied();
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void doSaveAs() {
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   @Override
   public void setFocus() {
      int activePage = getActivePage();

      if (activePage == attributesPageIndex) {
         attributeComposite.setFocus();
      } else if (activePage == relationsPageIndex) {
         relationsComposite.setFocus();
      } else if (activePage == detailsCompositeIndex) {
         detailsComposite.setFocus();
      } else {
         super.setFocus();
      }
   }

   private int createDetailsPage() {
      renderDetailsPage();
      return addPage(detailsComposite.getParent());
   }

   private void renderDetailsPage() {
      if (detailsComposite == null) {
         Composite composite = createCommonPageComposite();
         detailsComposite = new DetailsBrowserComposite(artifact, composite, SWT.BORDER, createToolBar(composite));
      }
   }

   private int createAttributesPage() {
      Composite composite = createCommonPageComposite();
      attributeComposite = new AttributesComposite(this, composite, SWT.NONE, artifact, createToolBar(composite));

      return addPage(composite);
   }

   private int createNewAttributesPage() {
      Composite composite = createCommonPageComposite();
      newAttributeComposite = new NewAttributesComposite(this, composite, SWT.NONE, artifact, createToolBar(composite));

      return addPage(composite);
   }

   private Composite createCommonPageComposite() {
      Composite composite = new Composite(getContainer(), SWT.NONE);
      GridLayout layout = new GridLayout(1, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      layout.verticalSpacing = 0;
      composite.setLayout(layout);

      return composite;
   }

   private int createRelationsPage() {
      Composite composite = createCommonPageComposite();
      relationsComposite = new RelationsComposite(this, composite, SWT.NONE, artifact, createToolBar(composite));

      return addPage(composite);
   }

   public ToolBar createToolBar(Composite parent) {
      return createToolBar(parent, this, artifact, new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1), getSite(),
            EDITOR_ID, "Artifact Editor");
   }

   public static ToolBar createToolBar(Composite parent, IActionable actionable, final Artifact artifact, Object layoutData, IWorkbenchPartSite site, final String editorId, final String actionableItemName) {
      ISelectionProvider provider = new ISelectionProvider() {
         private ISelection selection;

         @Override
         public void addSelectionChangedListener(ISelectionChangedListener listener) {
         }

         @Override
         public ISelection getSelection() {
            return selection;
         }

         @Override
         public void removeSelectionChangedListener(ISelectionChangedListener listener) {
         }

         @Override
         public void setSelection(ISelection selection) {
            this.selection = selection;
         }
      };
      provider.setSelection(new StructuredSelection(new Object[] {artifact}));
      site.setSelectionProvider(provider);
      Composite toolBarComposite = new Composite(parent, SWT.BORDER);
      toolBarComposite.setLayoutData(layoutData);
      GridLayout layout = new GridLayout(2, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      toolBarComposite.setLayout(layout);

      final ToolBar toolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);

      GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1);
      toolBar.setLayoutData(gridData);
      SkynetGuiPlugin skynetGuiPlugin = SkynetGuiPlugin.getInstance();
      ToolItem item;

      if (actionable != null) {
         OseeAts.addButtonToEditorToolBar(actionable, SkynetGuiPlugin.getInstance(), toolBar, editorId,
               actionableItemName);
      }

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("DBiconBlueEdit.GIF"));
      item.setToolTipText("Show this artifact in the Resource History");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               HistoryView.open(artifact);
            } catch (Exception ex) {
               OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(skynetGuiPlugin.getImage("magnify.gif"));
      item.setToolTipText("Reveal this artifact in the Artifact Explorer");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               ArtifactExplorer.revealArtifact(artifact);
            } catch (Exception ex) {
               OseeLog.log(getClass(), OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
      item.setEnabled(artifact.getBranch().equals(BranchManager.getDefaultBranch()));

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      //Edit menu Item 
      //      final Menu editMenu = new Menu(parent.getShell(), SWT.POP_UP);

      List<Artifact> artifacts = new LinkedList<Artifact>();
      artifacts.add(artifact);
      final Menu previewMenu = new Menu(parent.getShell(), SWT.POP_UP);
      boolean previewable = false;
      try {
         if (RendererManager.getApplicableRenderers(PresentationType.PREVIEW, artifact, null).size() > 1) {
            previewable = true;
         }

         if (previewable) {
            if (OpenWithMenuListener.loadMenuItems(previewMenu, PresentationType.PREVIEW, artifacts)) {
               new MenuItem(previewMenu, SWT.SEPARATOR);
            }
         }
         OpenWithMenuListener.loadMenuItems(previewMenu, PresentationType.SPECIALIZED_EDIT, artifacts);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      } catch (NotDefinedException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
      final ToolItem previewItem = new ToolItem(toolBar, SWT.DROP_DOWN);
      previewItem.setImage(skynetGuiPlugin.getImage("open.gif"));
      previewItem.setToolTipText("Open the Artifact");
      final boolean previwableFinal = previewable;
      previewItem.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            if (event.detail == SWT.ARROW) {
               Rectangle rect = previewItem.getBounds();
               Point pt = new Point(rect.x, rect.y + rect.height);
               pt = toolBar.toDisplay(pt);
               previewMenu.setLocation(pt.x, pt.y);
               previewMenu.setVisible(true);
            }
            if (event.detail == 0) {
               try {
                  if (previwableFinal) {
                     RendererManager.previewInJob(artifact);
                  } else {
                     RendererManager.openInJob(artifact, PresentationType.SPECIALIZED_EDIT);
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(ArtifactEditor.class, Level.SEVERE, ex.getMessage());
               }
            }
         }
      });

      boolean itemzEnabled = false;
      for (MenuItem menuItems : previewMenu.getItems()) {
         if (menuItems.isEnabled()) {
            itemzEnabled = true;
         }
      }
      previewItem.setEnabled(itemzEnabled);

      final DeleteArtifactAction deleteAction = new DeleteArtifactAction(artifact);
      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(skynetGuiPlugin.getImage("delete.gif"));
      item.setToolTipText(deleteAction.getText());
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            deleteAction.run();
         }
      });
      item.setEnabled(!artifact.isReadOnly() && artifact.getBranch().equals(BranchManager.getDefaultBranch()));

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("branch.gif"));
      item.setToolTipText("Reveal the branch this artifact is on in the Branch Manager");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            BranchView.revealBranch(artifact.getBranch());
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("authenticated.gif"));
      item.setToolTipText("Access Control");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            PolicyDialog pd = new PolicyDialog(Display.getCurrent().getActiveShell(), artifact);
            pd.open();
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("copyToClipboard.gif"));
      item.setToolTipText("Copy artifact url link to clipboard. NOTE: This is a link pointing to the latest version of the artifact.");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (artifact != null) {
               Clipboard clipboard = null;
               try {
                  URL url = ArtifactURL.getExternalArtifactLink(artifact);
                  clipboard = new Clipboard(null);
                  clipboard.setContents(new Object[] {url.toString()}, new Transfer[] {TextTransfer.getInstance()});
               } catch (Exception ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, String.format(
                        "Error obtaining url for - guid: [%s] branch:[%s]", artifact.getGuid(), artifact.getBranch()),
                        ex);
               } finally {
                  if (clipboard != null && !clipboard.isDisposed()) {
                     clipboard.dispose();
                     clipboard = null;
                  }
               }
            }
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      Text artifactInfoLabel = new Text(toolBarComposite, SWT.END);
      artifactInfoLabel.setEditable(false);

      artifactInfoLabel.setText("Type: \"" + artifact.getArtifactTypeName() + "\"   HRID: " + artifact.getHumanReadableId());
      artifactInfoLabel.setToolTipText("The human readable id and database id for this artifact");

      return toolBar;
   }

   private void checkEnabledTooltems() {
      if (!attributeComposite.isDisposed()) {
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {
               boolean areBranchesEqual = artifact.getBranch().equals(BranchManager.getDefaultBranch());
               boolean isEditAllowed = artifact.isReadOnly() != true;

               if (attributeComposite.getToolBar() == null || attributeComposite.getToolBar().isDisposed()) {
                  return;
               }
               attributeComposite.getToolBar().getItem(REVEAL_ARTIFACT_INDEX).setEnabled(areBranchesEqual);
               attributeComposite.getToolBar().getItem(EDIT_ARTIFACT_INDEX).setEnabled(
                     isEditAllowed && areBranchesEqual);
               attributeComposite.getToolBar().update();

               relationsComposite.getToolBar().getItem(REVEAL_ARTIFACT_INDEX).setEnabled(areBranchesEqual);
               relationsComposite.getToolBar().getItem(EDIT_ARTIFACT_INDEX).setEnabled(
                     isEditAllowed && areBranchesEqual);
               relationsComposite.getToolBar().update();
            }
         });
      }
   }

   public String getActionDescription() {
      return "";
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      try {
         // If the artifact is dirty when the editor get's disposed, then it needs to be reverted
         if (!artifact.isDeleted() && (artifact.isDirty(true))) {
            try {
               artifact.reloadAttributesAndRelations();
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
         relationsComposite.disposeRelationsComposite();
         super.dispose();
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private void changeToArtifact(final Artifact artifact) {
      if (artifact == null || artifact == null) {
         closeEditor();
         return;
      }

      // The events coming to this editor are based on guid, so it is important that this case is
      // always true.
      if (!artifact.getGuid().equals(artifact.getGuid())) throw new IllegalArgumentException(
            "Can only change the editor to a different version of the Artifact being editted");

      Display.getDefault().asyncExec(new Runnable() {
         public void run() {

            ArtifactEditorInput input = new ArtifactEditorInput(artifact);
            setInput(input);
            setPartName(artifact.getDescriptiveName());
            setTitleImage(artifact.getImage());

            attributeComposite.refreshArtifact(artifact);
            relationsComposite.refreshArtifact(artifact);
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.EditorPart#setInput(org.eclipse.ui.IEditorInput)
    */
   @Override
   protected void setInput(IEditorInput input) {
      super.setInput(input);
      this.artifact = ((ArtifactEditorInput) input).getArtifact();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, org.eclipse.osee.framework.skynet.core.artifact.Branch, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (branchModType == BranchEventType.Committed) {
         try {
            changeToArtifact(ArtifactQuery.getArtifactFromId(artifact.getGuid(), BranchManager.getDefaultBranch()));
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            closeEditor();
         }
      }
      if (branchModType == BranchEventType.DefaultBranchChanged) {
         try {
            if (artifact.getBranch().equals(BranchManager.getDefaultBranch()) != true && !artifact.isReadOnly()) {
               try {
                  changeToArtifact(ArtifactQuery.getArtifactFromId(artifact.getGuid(), BranchManager.getDefaultBranch()));
               } catch (ArtifactDoesNotExist ex) {
                  System.err.println("Attention: Artifact " + artifact.getArtId() + " does not exist on new default branch. Closing the editor.");
                  closeEditor();
               }
            }
            checkEnabledTooltems();
         } catch (Exception ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData transData) throws OseeCoreException {
      if (!transData.isHasEvent(artifact)) {
         return;
      }
      if (transData.isDeleted(artifact)) {
         closeEditor();
      }
      if (transData.isRelAddedChangedDeleted(artifact)) {
         refreshRelationsComposite();
      }
      if (transData.isChanged(artifact)) {
         refreshDirtyArtifact();
      }
      onDirtied();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IArtifactModifiedEventListener#handleArtifactModifiedEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ArtifactModType, org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public void handleArtifactModifiedEvent(Sender sender, ArtifactModType artifactModType, Artifact artifact) {
      if (!this.artifact.equals(artifact)) return;
      if (artifactModType == ArtifactModType.Added || artifactModType == ArtifactModType.Changed || artifactModType == ArtifactModType.Reverted) {
         refreshDirtyArtifact();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IArtifactsPurgedEventListener#handleArtifactsPurgedEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.getLoadedArtifacts().contains(artifact)) {
            closeEditor();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IArtifactsChangeTypeEventListener#handleArtifactsChangeTypeEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, int, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, LoadedArtifacts loadedArtifacts) {
      try {
         if (loadedArtifacts.getLoadedArtifacts().contains(artifact)) {
            closeEditor();
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   public void handleRelationModifiedEvent(Sender sender, RelationModType relationModType, RelationLink link, Branch branch, String relationType) {
      try {
         if (link.getArtifactA().equals(artifact) || link.getArtifactB().equals(artifact)) {
            refreshRelationsComposite();
            onDirtied();
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   private void refreshDirtyArtifact() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            setPartName(getEditorInput().getName());
            setTitleImage(artifact.getImage());
            attributeComposite.refreshArtifact(artifact);
            onDirtied();
         }
      });
   }

   private void refreshRelationsComposite() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            if (!relationsComposite.isDisposed()) {
               relationsComposite.refresh();
               onDirtied();
            }
         }
      });
   }

   public void closeEditor() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            AWorkbench.getActivePage().closeEditor(editor, false);
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IAccessControlEventListener#handleAccessControlArtifactsEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.eventx.AccessControlModType, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
    */
   @Override
   public void handleAccessControlArtifactsEvent(Sender sender, AccessControlEventType accessControlEventType, LoadedArtifacts loadedArtifacts) {
      try {
         if (accessControlEventType == AccessControlEventType.ArtifactsLocked || accessControlEventType == AccessControlEventType.ArtifactsLocked) {
            if (loadedArtifacts.getLoadedArtifacts().contains(artifact)) {
               Displays.ensureInDisplayThread(new Runnable() {
                  /* (non-Javadoc)
                   * @see java.lang.Runnable#run()
                   */
                  @Override
                  public void run() {
                     setTitleImage(artifact.getImage());
                  }
               });
            }
         }
      } catch (Exception ex) {
         // do nothing
      }
   }

}
