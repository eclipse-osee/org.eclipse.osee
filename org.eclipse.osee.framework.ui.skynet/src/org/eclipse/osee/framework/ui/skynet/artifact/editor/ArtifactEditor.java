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

import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.db.connection.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModType;
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
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.access.PolicyDialog;
import org.eclipse.osee.framework.ui.skynet.artifact.annotation.AnnotationComposite;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.branch.BranchView;
import org.eclipse.osee.framework.ui.skynet.history.RevisionHistoryView;
import org.eclipse.osee.framework.ui.skynet.render.RendererManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactEditor extends MultiPageEditorPart implements IDirtiableEditor, IArtifactsPurgedEventListener, IBranchEventListener, IAccessControlEventListener, IArtifactModifiedEventListener, IArtifactsChangeTypeEventListener, IRelationModifiedEventListener, IFrameworkTransactionEventListener, IActionable {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor";
   private int previewPageIndex;
   private int attributesPageIndex;
   private int newAttributesPageIndex;
   private int relationsPageIndex;
   private int detailsCompositeIndex;
   private Artifact artifact;
   private final MultiPageEditorPart editor;
   private BrowserComposite previewComposite;
   private RelationsComposite relationsComposite;
   private AttributesComposite attributeComposite;
   private NewAttributesComposite newAttributeComposite;
   private DetailsBrowserComposite detailsComposite;
   private ToolItem forward;
   private ToolItem back;

   // correspond to the indices of tool items on the toolbar
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
                     OSEELog.logSevere(SkynetGuiPlugin.class,
                           "The user " + UserManager.getUser() + " does not have read access to " + artifact, true);
                  } else
                     AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
               }
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });
   }

   public static void editArtifact(final Artifact artifact) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               if (!AccessControlManager.checkObjectPermission(artifact, PermissionEnum.READ)) {
                  OSEELog.logSevere(SkynetGuiPlugin.class,
                        "The user " + UserManager.getUser() + " does not have read access to " + artifact, true);
               } else if (artifact != null) {
                  AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
               }
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
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

      previewPageIndex = createPreviewPage();
      setPageText(previewPageIndex, "Preview");

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

      if (activePage == previewPageIndex) {
         previewComposite.setFocus();
      } else if (activePage == attributesPageIndex) {
         attributeComposite.setFocus();
      } else if (activePage == relationsPageIndex) {
         relationsComposite.setFocus();
      } else if (activePage == detailsCompositeIndex) {
         detailsComposite.setFocus();
      } else {
         super.setFocus();
      }
   }

   private int createPreviewPage() {

      renderPreviewPage();
      return addPage(previewComposite.getParent());
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

   private void renderPreviewPage() {
      if (previewComposite == null) {
         Composite composite = createCommonPageComposite();
         previewComposite = new BrowserComposite(composite, SWT.BORDER, createPreviewToolBar(composite));
         if (artifact.getAnnotations().size() > 0) {
            new AnnotationComposite(previewComposite, SWT.BORDER, artifact);
         }
         previewComposite.addProgressListener(new BrowserProgressListener(previewComposite, back, forward));
      }

      RendererManager.previewInComposite(previewComposite, artifact);
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
      return createToolBar(parent, this, artifact);
   }

   private ToolBar createPreviewToolBar(Composite parent) {
      ToolBar toolBar = createToolBar(parent, this, artifact);

      // Add Navigation Browser Navigation Buttons
      back = new ToolItem(toolBar, SWT.NONE);
      back.setImage(SkynetGuiPlugin.getInstance().getImage("nav_backward.gif"));
      back.setToolTipText("Back to previous page");
      back.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            previewComposite.back();
         }
      });
      forward = new ToolItem(toolBar, SWT.NONE);
      forward.setImage(SkynetGuiPlugin.getInstance().getImage("nav_forward.gif"));
      forward.setToolTipText("Forward to the next page.");
      forward.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            previewComposite.forward();
         }
      });

      ToolItem refresh = new ToolItem(toolBar, SWT.NONE);
      refresh.setImage(SkynetGuiPlugin.getInstance().getImage("refresh.gif"));
      refresh.setToolTipText("Refresh the current page");
      refresh.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            previewComposite.refresh();
         }
      });

      ToolItem isDirty = new ToolItem(toolBar, SWT.NONE);
      isDirty.setImage(SkynetGuiPlugin.getInstance().getImage("dirty.gif"));
      isDirty.setToolTipText("Show what attribute or relation making editor dirty.");
      isDirty.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            Result result = reportIsDirty();
            AWorkbench.popup("Dirty Report", result.isFalse() ? "Not Dirty" : "Dirty -> " + result.getText());
         }
      });

      ToolItem snapshotSave = new ToolItem(toolBar, SWT.NONE);
      snapshotSave.setImage(SkynetGuiPlugin.getInstance().getImage("snapshotSave.gif"));
      snapshotSave.setToolTipText("Store a snapshot of the preview by rendering the artifact and storing a copy for others to use in their preview window.");
      snapshotSave.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent event) {
            final String oldUrl = previewComposite.getUrl();
            if (oldUrl.contains("GET.ARTIFACT") && !oldUrl.contains("&force=true")) {
               previewComposite.setUrl(oldUrl + "&force=true");
               Job job = new Job("Update Preview") {
                  @Override
                  protected IStatus run(IProgressMonitor monitor) {
                     renderPreviewPage();
                     return Status.OK_STATUS;
                  }
               };
               job.setUser(false);
               job.setPriority(Job.SHORT);
               job.schedule(2000);
            }
         }
      });
      return toolBar;

   }

   private ToolBar createToolBar(Composite parent, IActionable actionable, final Artifact artifact) {
      Composite toolBarComposite = new Composite(parent, SWT.BORDER);
      GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1);
      toolBarComposite.setLayoutData(gridData);
      GridLayout layout = new GridLayout(2, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      toolBarComposite.setLayout(layout);

      ToolBar toolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);

      gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1);
      toolBar.setLayoutData(gridData);
      SkynetGuiPlugin skynetGuiPlugin = SkynetGuiPlugin.getInstance();
      ToolItem item;

      if (actionable != null) {
         OseeAts.addButtonToEditorToolBar(actionable, SkynetGuiPlugin.getInstance(), toolBar, EDITOR_ID,
               "Artifact Editor");
      }

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(skynetGuiPlugin.getImage("edit.gif"));
      item.setToolTipText("Show this artifact in the Resource History");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            RevisionHistoryView.open(artifact);
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
               OSEELog.logException(getClass(), ex, true);
            }
         }
      });
      item.setEnabled(artifact.getBranch().equals(BranchManager.getDefaultBranch()));

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(skynetGuiPlugin.getImage("edit_artifact.gif"));
      item.setToolTipText("Present this artifact for editing");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               RendererManager.editInJob(artifact);
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });
      item.setEnabled(!artifact.isReadOnly() && artifact.getBranch().equals(BranchManager.getDefaultBranch()));

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(skynetGuiPlugin.getImage("preview_artifact.gif"));
      item.setToolTipText("Present this artifact for previewing (read-only)");
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            try {
               RendererManager.previewInJob(artifact);
            } catch (OseeCoreException ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });

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

      Text artifactInfoLabel = new Text(toolBarComposite, SWT.END);
      artifactInfoLabel.setEditable(false);

      artifactInfoLabel.setText("Type: \"" + artifact.getArtifactTypeName() + "\"   HRID: " + artifact.getHumanReadableId());
      artifactInfoLabel.setToolTipText("The human readable id and database id for this artifact");

      return toolBar;
   }
   private final class DeleteArtifactAction extends Action {

      private final Artifact artifact;

      public DeleteArtifactAction(Artifact artifact) {
         super("&Delete Artifact\tDelete", Action.AS_PUSH_BUTTON);
         this.artifact = artifact;
      }

      @Override
      public void run() {
         try {
            MessageDialog dialog =
                  new MessageDialog(Display.getCurrent().getActiveShell(), "Confirm Artifact Deletion", null,
                        " Are you sure you want to delete this artifact and all of the default hierarchy children?",
                        MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
            if (dialog.open() == Window.OK) {
               artifact.delete();
            }
         } catch (Exception ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   }

   private void checkEnabledTooltems() {
      if (!attributeComposite.isDisposed()) {
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {
               boolean areBranchesEqual = artifact.getBranch().equals(BranchManager.getDefaultBranch());
               boolean isEditAllowed = artifact.isReadOnly() != true;

               previewComposite.getToolBar().getItem(REVEAL_ARTIFACT_INDEX).setEnabled(areBranchesEqual);
               previewComposite.getToolBar().getItem(EDIT_ARTIFACT_INDEX).setEnabled(isEditAllowed && areBranchesEqual);
               previewComposite.getToolBar().update();

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
            renderPreviewPage();
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

   private final class BrowserProgressListener implements ProgressListener {

      private final BrowserComposite browserComposite;
      private final ToolItem back;
      private final ToolItem forward;

      private BrowserProgressListener(BrowserComposite browserComposite, ToolItem back, ToolItem forward) {
         this.browserComposite = browserComposite;
         this.back = back;
         this.forward = forward;
      }

      private void updateBackNextBusy() {
         back.setEnabled(browserComposite.isBackEnabled());
         forward.setEnabled(browserComposite.isForwardEnabled());
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.swt.browser.ProgressListener#changed(org.eclipse.swt.browser.ProgressEvent)
       */
      public void changed(ProgressEvent event) {
         if (event.total != 0) {
            updateBackNextBusy();
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.swt.browser.ProgressListener#completed(org.eclipse.swt.browser.ProgressEvent)
       */
      public void completed(ProgressEvent event) {
         updateBackNextBusy();
      }
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
      renderPreviewPage();
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
