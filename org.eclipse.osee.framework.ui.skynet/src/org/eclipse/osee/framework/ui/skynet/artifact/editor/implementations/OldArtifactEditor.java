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

package org.eclipse.osee.framework.ui.skynet.artifact.editor.implementations;

import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.AttributesComposite;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.RelationsComposite;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.AbstractEventArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorActionBarContributor;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditorInput;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.panels.DetailsBrowserComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;

/**
 * @author Ryan D. Brooks
 */
public class OldArtifactEditor extends AbstractEventArtifactEditor {
   public static final String EDITOR_ID = "org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor";
   private int attributesPageIndex;
   private int relationsPageIndex;
   private int detailsCompositeIndex;
   private final MultiPageEditorPart editor;
   private RelationsComposite relationsComposite;
   private AttributesComposite attributeComposite;
   private DetailsBrowserComposite detailsComposite;
   private ArtifactEditorActionBarContributor actionBarContributor;

   private static final int REVEAL_ARTIFACT_INDEX = 2;
   private static final int EDIT_ARTIFACT_INDEX = 4;

   public OldArtifactEditor() {
      super();
      editor = this;
   }

   public RelationsComposite getRelationsComposite() {
      return relationsComposite;
   }

   @Override
   public boolean isDirty() {
      return reportIsDirty().isTrue();
   }

   private Result reportIsDirty() {
      Artifact artifact = getEditorInput().getArtifact();
      if (artifact.isDeleted()) return Result.FalseResult;

      try {
         if (artifact.isReadOnly()) return Result.FalseResult;
         Result result = artifact.reportIsDirty(true);
         if (result.isTrue()) return result;
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }

      return Result.FalseResult;
   }

   protected void onDirty() {
      Display.getDefault().asyncExec(new Runnable() {

         public void run() {
            firePropertyChange(PROP_DIRTY);
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormEditor#addPages()
    */
   @Override
   protected void addPages() {
      OseeContributionItem.addTo(this, true);

      setPartName(getEditorInput().getName());
      Artifact artifact = getEditorInput().getArtifact();
      setTitleImage(artifact != null ? artifact.getImage() : null);

      attributesPageIndex = createAttributesPage();
      setPageText(attributesPageIndex, "Attributes");

      relationsPageIndex = createRelationsPage();
      setPageText(relationsPageIndex, "Relations");

      detailsCompositeIndex = createDetailsPage();
      setPageText(detailsCompositeIndex, "Details");
   }

   public ArtifactEditorActionBarContributor getActionBarContributor() {
      if (actionBarContributor == null) {
         actionBarContributor = new ArtifactEditorActionBarContributor(this);
      }
      return actionBarContributor;
   }

   @Override
   public void doSave(IProgressMonitor monitor) {
      try {
         getEditorInput().getArtifact().persistAttributesAndRelations();
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
      if (detailsComposite == null) {
         Composite composite = createCommonPageComposite();

         detailsComposite =
               new DetailsBrowserComposite(getEditorInput().getArtifact(), composite, SWT.BORDER,
                     createToolBar(composite));
      }
      return addPage(detailsComposite.getParent());
   }

   private int createAttributesPage() {
      Composite composite = createCommonPageComposite();
      attributeComposite =
            new AttributesComposite(this, composite, SWT.NONE, getEditorInput().getArtifact(), createToolBar(composite));

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
      toolBarComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 1, 1));
      GridLayout layout = new GridLayout(2, false);
      layout.marginHeight = 0;
      layout.marginWidth = 0;
      toolBarComposite.setLayout(layout);

      final ToolBar toolBar = new ToolBar(toolBarComposite, SWT.FLAT | SWT.RIGHT);
      ToolBarManager manager = new ToolBarManager(toolBar);
      toolBar.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, true, 1, 1));

      getActionBarContributor().contributeToToolBar(manager);
      manager.update(true);

      Text artifactInfoLabel = new Text(toolBarComposite, SWT.END);
      artifactInfoLabel.setEditable(false);

      Artifact artifact = getEditorInput().getArtifact();
      artifactInfoLabel.setText("Branch: \"" + artifact.getBranch().getBranchShortName() + "\"   Type: \"" + artifact.getArtifactTypeName() + "\"   HRID: " + artifact.getHumanReadableId());
      artifactInfoLabel.setToolTipText("The human readable id and database id for this artifact");

      return toolBar;
   }

   protected void checkEnabledTooltems() {
      if (!attributeComposite.isDisposed()) {
         Display.getDefault().asyncExec(new Runnable() {
            public void run() {
               boolean isEditAllowed = getEditorInput().getArtifact().isReadOnly() != true;

               if (attributeComposite.getToolBar() == null || attributeComposite.getToolBar().isDisposed()) {
                  return;
               }
               attributeComposite.getToolBar().getItem(REVEAL_ARTIFACT_INDEX).setEnabled(true);
               attributeComposite.getToolBar().getItem(EDIT_ARTIFACT_INDEX).setEnabled(isEditAllowed);
               attributeComposite.getToolBar().update();

               relationsComposite.getToolBar().getItem(REVEAL_ARTIFACT_INDEX).setEnabled(true);
               relationsComposite.getToolBar().getItem(EDIT_ARTIFACT_INDEX).setEnabled(isEditAllowed);
               relationsComposite.getToolBar().update();
            }
         });
      }
   }

   @Override
   public void dispose() {
      try {
         // If the artifact is dirty when the editor get's disposed, then it needs to be reverted
         Artifact artifact = getEditorInput().getArtifact();
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

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.MultiPageEditorPart#getAdapter(java.lang.Class)
    */
   @SuppressWarnings("unchecked")
   @Override
   public Object getAdapter(Class adapter) {
      if (IActionable.class.equals(adapter)) {
         return new IActionable() {
            @Override
            public String getActionDescription() {
               return "";
            }
         };
      }
      return super.getAdapter(adapter);
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.EditorPart#getEditorInput()
    */
   @Override
   public ArtifactEditorInput getEditorInput() {
      return (ArtifactEditorInput) super.getEditorInput();
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.forms.editor.FormEditor#init(org.eclipse.ui.IEditorSite, org.eclipse.ui.IEditorInput)
    */
   @Override
   public void init(IEditorSite site, IEditorInput input) throws PartInitException {
      super.init(site, input);
      ISelectionProvider provider = new ArtifactEditorSelectionProvider();
      provider.setSelection(new StructuredSelection(new Object[] {getEditorInput().getArtifact()}));
      getSite().setSelectionProvider(provider);
   }

   protected void refreshDirtyArtifact() {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            setPartName(getEditorInput().getName());
            Artifact artifact = getEditorInput().getArtifact();
            setTitleImage(artifact != null ? artifact.getImage() : null);
            attributeComposite.refreshArtifact(artifact);
            onDirtied();
         }
      });
   }

   protected void refreshRelations() {
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

   private final class ArtifactEditorSelectionProvider implements ISelectionProvider {
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
   }
}
