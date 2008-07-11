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

import java.sql.SQLException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.access.AccessControlManager;
import org.eclipse.osee.framework.skynet.core.access.PermissionEnum;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.CacheArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.DefaultBranchChangedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.TransactionArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactModifiedEvent.ModType;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.event.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.ArtifactLockStatusChanged;
import org.eclipse.osee.framework.skynet.core.event.LocalCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.LocalTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteCommitBranchEvent;
import org.eclipse.osee.framework.skynet.core.event.RemoteTransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.skynet.core.event.TransactionEvent;
import org.eclipse.osee.framework.skynet.core.event.VisitorEvent;
import org.eclipse.osee.framework.skynet.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.CacheRelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.relation.RelationModifiedEvent;
import org.eclipse.osee.framework.skynet.core.relation.TransactionRelationModifiedEvent;
import org.eclipse.osee.framework.ui.plugin.event.Event;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.ArtifactExplorer;
import org.eclipse.osee.framework.ui.skynet.AttributesComposite;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
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
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Ryan D. Brooks
 */
public class ArtifactEditor extends MultiPageEditorPart implements IDirtiableEditor, IEventReceiver, IActionable {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor";
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactEditor.class);
   private static final SkynetEventManager eventManager = SkynetEventManager.getInstance();
   private static final BranchPersistenceManager branchManager = BranchPersistenceManager.getInstance();
   private int previewPageIndex;
   private int attributesPageIndex;
   private int newAttributesPageIndex;
   private int relationsPageIndex;
   private BrowserComposite previewComposite;
   private RelationsComposite relationsComposite;
   private AttributesComposite attributeComposite;
   private NewAttributesComposite newAttributeComposite;
   private ToolItem forward;
   private ToolItem back;

   // correspond to the indices of tool items on the toolbar
   private static final int REVEAL_ARTIFACT_INDEX = 2;
   private static final int EDIT_ARTIFACT_INDEX = 4;

   public ArtifactEditor() {
      super();
   }

   public RelationsComposite getRelationsComposite() {
      return relationsComposite;
   }

   public static void editArtifacts(final Collection<Artifact> artifacts) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               for (Artifact artifact : artifacts) {
                  if (!AccessControlManager.checkObjectPermission(SkynetAuthentication.getUser(), artifact,
                        PermissionEnum.READ)) {
                     OSEELog.logInfo(SkynetGuiPlugin.class,
                           "The user " + SkynetAuthentication.getUser() + " does not have read access to " + artifact,
                           true);
                  } else
                     AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
               }
            } catch (PartInitException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });
   }

   public static void editArtifact(final Artifact artifact) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            try {
               if (!AccessControlManager.checkObjectPermission(SkynetAuthentication.getUser(), artifact,
                     PermissionEnum.READ)) {
                  OSEELog.logInfo(SkynetGuiPlugin.class,
                        "The user " + SkynetAuthentication.getUser() + " does not have read access to " + artifact,
                        true);
               } else if (artifact != null) {
                  AWorkbench.getActivePage().openEditor(new ArtifactEditorInput(artifact), EDITOR_ID);
               }
            } catch (PartInitException ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
         }
      });
   }

   @Override
   public boolean isDirty() {
      Artifact artifact = getEditorInput().getArtifact();

      if (artifact.isDeleted()) return false;

      try {
         boolean dirty = !artifact.isReadOnly() && artifact.isDirty(true);
         if (dirty) return true;

         //TODO The new attribute composite dirty logic is always returning true ....
         if (false) {
            Result result = newAttributeComposite.isDirty();
            System.out.println("New Attribute Composite - isDirt => " + result);
            if (result.isTrue()) {
               return true;
            }
         }
      } catch (Exception ex) {
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }

      return false;
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
      SkynetContributionItem.addTo(this, true);

      previewPageIndex = createPreviewPage();
      setPageText(previewPageIndex, "Preview");

      attributesPageIndex = createAttributesPage();
      setPageText(attributesPageIndex, "Attributes");

      if (false && OseeProperties.isDeveloper()) {
         newAttributesPageIndex = createNewAttributesPage();
         setPageText(newAttributesPageIndex, "Attributes2");
      }

      relationsPageIndex = createRelationsPage();
      setPageText(relationsPageIndex, "Relations");
      setPartName(getEditorInput().getName());
      setTitleImage(getEditorInput().getArtifact().getImage());

      String guid = getEditorInput().getArtifact().getGuid();
      eventManager.register(CacheArtifactModifiedEvent.class, guid, this);
      eventManager.register(CacheRelationModifiedEvent.class, this);
      eventManager.register(RemoteTransactionEvent.class, this);
      eventManager.register(TransactionRelationModifiedEvent.class, this);
      eventManager.register(DefaultBranchChangedEvent.class, this);
      eventManager.register(TransactionArtifactModifiedEvent.class, this);
      eventManager.register(VisitorEvent.class, this);
      eventManager.register(LocalCommitBranchEvent.class, this);
      eventManager.register(RemoteCommitBranchEvent.class, this);
      eventManager.register(ArtifactLockStatusChanged.class, this);
      eventManager.register(LocalTransactionEvent.class, this);

   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      Artifact artifact = getEditorInput().getArtifact();
      try {
         artifact.persistAttributesAndRelations();
         firePropertyChange(PROP_DIRTY);
      } catch (SQLException ex) {
         onDirtied();
         OSEELog.logException(getClass(), ex, true);
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
      } else {
         super.setFocus();
      }
   }

   private int createPreviewPage() {

      renderPreviewPage();
      return addPage(previewComposite.getParent());
   }

   private void renderPreviewPage() {
      if (previewComposite == null) {
         Composite composite = createCommonPageComposite();
         previewComposite = new BrowserComposite(composite, SWT.BORDER, createToolBar(composite));
         if (getEditorInput().getArtifact().getAnnotations().size() > 0) {
            new AnnotationComposite(previewComposite, SWT.BORDER, getEditorInput().getArtifact());
         }
         previewComposite.addProgressListener(new BrowserProgressListener(previewComposite, back, forward));
      }

      RendererManager.getInstance().previewInComposite(previewComposite, getEditorInput().getArtifact());
   }

   private int createAttributesPage() {
      Composite composite = createCommonPageComposite();
      attributeComposite =
            new AttributesComposite(this, composite, SWT.NONE, getEditorInput().getArtifact(), createToolBar(composite));

      return addPage(composite);
   }

   private int createNewAttributesPage() {
      Composite composite = createCommonPageComposite();
      newAttributeComposite =
            new NewAttributesComposite(this, composite, SWT.NONE, getEditorInput().getArtifact(),
                  createToolBar(composite));

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
      relationsComposite =
            new RelationsComposite(this, composite, SWT.NONE, getEditorInput().getArtifact(), createToolBar(composite));

      return addPage(composite);
   }

   private ToolBar createToolBar(Composite parent) {
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

      OseeAts.addButtonToEditorToolBar(this, SkynetGuiPlugin.getInstance(), toolBar, EDITOR_ID, "Artifact Editor");

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(skynetGuiPlugin.getImage("edit.gif"));
      item.setToolTipText("Show this artifact in the Resource History");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            RevisionHistoryView.open(getEditorInput().getArtifact());
         }
      });

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(skynetGuiPlugin.getImage("magnify.gif"));
      item.setToolTipText("Reveal this artifact in the Artifact Explorer");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            Artifact artifact = getEditorInput().getArtifact();
            try {
               ArtifactExplorer.revealArtifact(artifact);
            } catch (Exception ex) {
               OSEELog.logException(getClass(), ex, true);
            }
         }
      });
      item.setEnabled(getEditorInput().getArtifact().getBranch().equals(branchManager.getDefaultBranch()));

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(skynetGuiPlugin.getImage("edit_artifact.gif"));
      item.setToolTipText("Present this artifact for editing");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            RendererManager.getInstance().editInJob(getEditorInput().getArtifact());
         }
      });
      item.setEnabled(!getEditorInput().getArtifact().isReadOnly() && getEditorInput().getArtifact().getBranch().equals(
            branchManager.getDefaultBranch()));

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(skynetGuiPlugin.getImage("preview_artifact.gif"));
      item.setToolTipText("Present this artifact for previewing (read-only)");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            RendererManager.getInstance().previewInJob(getEditorInput().getArtifact());
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("branch.gif"));
      item.setToolTipText("Reveal the branch this artifact is on in the Branch Manager");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            BranchView.revealBranch(getEditorInput().getArtifact().getBranch());
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      item = new ToolItem(toolBar, SWT.PUSH);
      item.setImage(SkynetGuiPlugin.getInstance().getImage("authenticated.gif"));
      item.setToolTipText("Access Control");
      item.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            PolicyDialog pd = new PolicyDialog(Display.getCurrent().getActiveShell(), getEditorInput().getArtifact());
            pd.open();
         }
      });

      item = new ToolItem(toolBar, SWT.SEPARATOR);

      // Add Navigation Browser Navigation Buttons
      back = new ToolItem(toolBar, SWT.NONE);
      back.setImage(skynetGuiPlugin.getImage("nav_backward.gif"));
      back.setToolTipText("Back to previous page");
      back.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            previewComposite.back();
         }
      });
      forward = new ToolItem(toolBar, SWT.NONE);
      forward.setImage(skynetGuiPlugin.getImage("nav_forward.gif"));
      forward.setToolTipText("Forward to the next page.");
      forward.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            previewComposite.forward();
         }
      });

      ToolItem refresh = new ToolItem(toolBar, SWT.NONE);
      refresh.setImage(skynetGuiPlugin.getImage("refresh.gif"));
      refresh.setToolTipText("Refresh the current page");
      refresh.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent event) {
            previewComposite.refresh();
         }
      });

      if (OseeProperties.isDeveloper()) {
         ToolItem snapshotSave = new ToolItem(toolBar, SWT.NONE);
         snapshotSave.setImage(skynetGuiPlugin.getImage("snashotSave.gif"));
         snapshotSave.setToolTipText("DEVELOPERS ONLY: Take a Snapshot of the preview");
         snapshotSave.addSelectionListener(new SelectionAdapter() {
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
      }

      Text artifactInfoLabel = new Text(toolBarComposite, SWT.END);
      artifactInfoLabel.setEditable(false);

      Artifact artifact = getEditorInput().getArtifact();
      artifactInfoLabel.setText("Type: \"" + artifact.getArtifactTypeName() + "\"  Guid: " + artifact.getGuid() + "  HRID: " + artifact.getHumanReadableId() + "  Art Id: " + artifact.getArtId());
      artifactInfoLabel.setToolTipText("The human readable id and database id for this artifact");

      return toolBar;
   }

   private void checkEnabledTooltems() {
      if (!attributeComposite.isDisposed()) {
         boolean areBranchesEqual = getEditorInput().getArtifact().getBranch().equals(branchManager.getDefaultBranch());
         boolean isEditAllowed = getEditorInput().getArtifact().isReadOnly() != true;

         previewComposite.getToolBar().getItem(REVEAL_ARTIFACT_INDEX).setEnabled(areBranchesEqual);
         previewComposite.getToolBar().getItem(EDIT_ARTIFACT_INDEX).setEnabled(isEditAllowed && areBranchesEqual);
         previewComposite.getToolBar().update();

         attributeComposite.getToolBar().getItem(REVEAL_ARTIFACT_INDEX).setEnabled(areBranchesEqual);
         attributeComposite.getToolBar().getItem(EDIT_ARTIFACT_INDEX).setEnabled(isEditAllowed && areBranchesEqual);
         attributeComposite.getToolBar().update();

         relationsComposite.getToolBar().getItem(REVEAL_ARTIFACT_INDEX).setEnabled(areBranchesEqual);
         relationsComposite.getToolBar().getItem(EDIT_ARTIFACT_INDEX).setEnabled(isEditAllowed && areBranchesEqual);
         relationsComposite.getToolBar().update();
      }
   }

   public String getActionDescription() {
      return "";
   }

   public ArtifactEditorInput getEditorInput() {
      return (ArtifactEditorInput) super.getEditorInput();
   }

   @Override
   public void dispose() {
      try {
         // If the artifact is dirty when the editor get's disposed, then it needs to be reverted
         Artifact artifact = getEditorInput().getArtifact();

         if (!artifact.isDeleted() && (artifact.isDirty(true))) {
            try {
               artifact.reloadAttributesAndRelations();
            } catch (SQLException ex) {
               SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
            }
         }

         eventManager.unRegisterAll(this);
         super.dispose();
      } catch (SQLException ex) {
         SkynetGuiPlugin.getLogger().log(Level.SEVERE, ex.getLocalizedMessage(), ex);
      }
   }

   public void onEvent(final Event event) {
      final ArtifactEditor editor = this;
      Artifact artifact = getEditorInput().getArtifact();

      if (event instanceof ArtifactEvent) {
         ArtifactEvent artifactEvent = (ArtifactEvent) event;
         Artifact eventArtifact = artifactEvent.getArtifact();

         if (eventArtifact != null && eventArtifact.equals(artifact)) {
            if (event instanceof ArtifactModifiedEvent) {
               ModType modType = ((ArtifactModifiedEvent) event).getType();

               if (modType == ModType.Deleted) {
                  AWorkbench.getActivePage().closeEditor(editor, false);
               } else if (modType == ModType.Added || modType == ModType.Changed || modType == ModType.Reverted) {

                  setPartName(getEditorInput().getName());
                  setTitleImage(artifact.getImage());
                  attributeComposite.refreshArtifact(artifact);

                  if (event instanceof CacheArtifactModifiedEvent) {
                     CacheArtifactModifiedEvent cachedEvent = (CacheArtifactModifiedEvent) event;
                     if (cachedEvent.getSender() instanceof WordAttribute) {
                        renderPreviewPage();
                     }
                  }

                  onDirtied();
               } else if (event instanceof VisitorEvent) {
                  firePropertyChange(PROP_DIRTY);
                  renderPreviewPage();
               } else if (event instanceof ArtifactLockStatusChanged) {
                  setTitleImage(getEditorInput().getArtifact().getImage());
               }
            }
         }
      } else if (event instanceof TransactionEvent) {
         ((TransactionEvent) event).fireSingleEvent(this);
      } else if (event instanceof RelationModifiedEvent) {
         onDirtied();

         if (!relationsComposite.isDisposed()) {
            Display.getDefault().asyncExec(new Runnable() {
               public void run() {
                  relationsComposite.refresh();
               }
            });
         }
      } else if (event instanceof RemoteTransactionEvent) {
         ((RemoteTransactionEvent) event).fireSingleEvent(editor);
      } else if (event instanceof DefaultBranchChangedEvent) {
         try {
            if (artifact.getBranch().equals(branchManager.getDefaultBranch()) != true && !artifact.isReadOnly()) {
               try {
                  changeToArtifact(ArtifactQuery.getArtifactFromId(artifact.getGuid(), branchManager.getDefaultBranch()));
               } catch (ArtifactDoesNotExist ex) {
                  System.err.println("Attention: Artifact " + artifact.getArtId() + " does not exist on new default branch. Closing the editor.");
                  AWorkbench.getActivePage().closeEditor(this, false);
               }
            }
            checkEnabledTooltems();
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
      } else if ((event instanceof LocalCommitBranchEvent) || (event instanceof RemoteCommitBranchEvent)) {
         try {
            changeToArtifact(ArtifactQuery.getArtifactFromId(artifact.getGuid(), branchManager.getDefaultBranch()));
         } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
            AWorkbench.getActivePage().closeEditor(this, false);
         }
      } else {
         throw new IllegalStateException("Not registered for legal event.");
      }
   }

   public boolean runOnEventInDisplayThread() {
      return true;
   }

   private void changeToArtifact(final Artifact artifact) {
      if (artifact == null || getEditorInput().getArtifact() == null) {
         AWorkbench.getActivePage().closeEditor(this, false);
         return;
      }

      // The events coming to this editor are based on guid, so it is important that this case is
      // always true.
      if (!artifact.getGuid().equals(getEditorInput().getArtifact().getGuid())) throw new IllegalArgumentException(
            "Can only change the editor to a different version of the Artifact being editted");

      Display.getDefault().asyncExec(new Runnable() {
         public void run() {

            ArtifactEditorInput input = new ArtifactEditorInput(artifact);
            setInput(input);
            setPartName(artifact.getDescriptiveName());
            setTitleImage(getEditorInput().getArtifact().getImage());

            attributeComposite.refreshArtifact(artifact);
            relationsComposite.refreshArtifact(artifact);
            renderPreviewPage();
         }
      });
   }

   private final class BrowserProgressListener implements ProgressListener {

      private BrowserComposite browserComposite;
      private ToolItem back;
      private ToolItem forward;

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
}