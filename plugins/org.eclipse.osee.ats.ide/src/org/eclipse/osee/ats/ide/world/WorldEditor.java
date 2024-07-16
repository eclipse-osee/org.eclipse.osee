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

package org.eclipse.osee.ats.ide.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.help.ui.AtsHelpContext;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.task.TaskEditorProvider;
import org.eclipse.osee.ats.ide.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.OseeStatusContributionItemFactory;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.IDirtiableEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.FormEditor;

/**
 * @author Donald G. Dunne
 */
public class WorldEditor extends FormEditor implements IWorldEditor, IDirtiableEditor, IAtsMetricsProvider {
   public static final String EDITOR_ID = "org.eclipse.osee.ats.ide.world.WorldEditor";
   protected WorldXWidgetActionPage worldXWidgetActionPage;
   public static final int TITLE_MAX_LENGTH = 80;

   @Override
   public void doSave(IProgressMonitor monitor) {
      // do nothing
   }

   public static void open(String name, Collection<IAtsWorkItem> workItems) {
      WorldEditor.open(new WorldEditorSimpleProvider(workItems, name));
   }

   public static void open(final IWorldEditorProvider provider) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            IWorkbenchPage page = AWorkbench.getActivePage();
            try {
               page.openEditor(new WorldEditorInput(provider), EDITOR_ID);
            } catch (PartInitException ex) {
               OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
            }
         }
      });
   }

   public static Collection<WorldEditor> getEditors() {
      final List<WorldEditor> editors = new ArrayList<>();
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               editors.add((WorldEditor) editor.getEditor(false));
            }
         }
      });
      return editors;
   }

   public static void closeAll() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IEditorReference editor : AWorkbench.getEditors(EDITOR_ID)) {
               AWorkbench.getActivePage().closeEditor(editor.getEditor(false), false);
            }
         }
      });
   }

   @Override
   public boolean isSaveOnCloseNeeded() {
      return isDirty();
   }

   @Override
   public void dispose() {
      if (worldXWidgetActionPage != null && worldXWidgetActionPage.getWorldComposite() != null) {
         worldXWidgetActionPage.getWorldComposite().disposeComposite();
      }
      super.dispose();
   }

   @Override
   public String getCurrentTitleLabel() {
      return worldXWidgetActionPage.getCurrentTitleLabel();
   }

   @Override
   public void setTableTitle(final String title, final boolean warning) {
      worldXWidgetActionPage.setTableTitle(title, warning);
   }

   @Override
   public boolean isDirty() {
      return false;
   }

   @Override
   protected void addPages() {
      try {
         OseeStatusContributionItemFactory.addTo(this, true);

         IWorldEditorProvider provider = getWorldEditorProvider();
         setPartName(provider.getSelectedName(SearchType.Search));
         if (getWorldEditorInput().isReload()) {
            createReloadTab();
            setActivePage(0);
         } else {
            if (provider instanceof IWorldEditorConsumer) {
               ((IWorldEditorConsumer) provider).setWorldEditor(this);
            }
            createMainTab();
            setActivePage(WorldXWidgetActionPage.ID);
         }

         if (!getWorldEditorInput().isReload()) {
            getSite().setSelectionProvider(getWorldComposite().getXViewer());
            // Until WorldEditor has different help, just use WorldView's help
            HelpUtil.setHelp(worldXWidgetActionPage.getWorldComposite().getControl(), AtsHelpContext.WORLD_VIEW);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   public WorldComposite getWorldComposite() {
      return worldXWidgetActionPage.getWorldComposite();
   }

   public WorldXWidgetActionPage getWorldXWidgetActionPage() {
      return worldXWidgetActionPage;
   }

   public void setEditorTitle(final String str) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            setPartName(str);
            firePropertyChange(IWorkbenchPart.PROP_TITLE);
         }
      });
   }

   @Override
   public IWorldEditorProvider getWorldEditorProvider() {
      WorldEditorInput worldEditorInput = getWorldEditorInput();
      worldEditorInput.setEditor(this);
      return worldEditorInput.getIWorldEditorProvider();
   }

   public WorldEditorInput getWorldEditorInput() {
      IEditorInput editorInput = getEditorInput();
      if (!(editorInput instanceof WorldEditorInput)) {
         throw new OseeArgumentException("Editor Input not WorldEditorInput");
      }
      return (WorldEditorInput) editorInput;
   }

   @Override
   public void reSearch() {
      worldXWidgetActionPage.reSearch();
   }

   public boolean isReloadTabShown() {
      return getActivePageInstance() instanceof WorldReloadTab;
   }

   private void createReloadTab() throws PartInitException {
      addPage(new WorldReloadTab(this, (WorldEditorReloadProvider) getWorldEditorProvider()));
   }

   private void createMainTab() throws PartInitException {
      worldXWidgetActionPage = new WorldXWidgetActionPage(this);
      addPage(worldXWidgetActionPage);
   }

   public List<Artifact> getLoadedArtifacts() {
      if (worldXWidgetActionPage == null || worldXWidgetActionPage.getWorldComposite() == null) {
         return Collections.emptyList();
      }
      return worldXWidgetActionPage.getWorldComposite().getLoadedArtifacts();
   }

   @Override
   public Collection<? extends Artifact> getMetricsWorkItems() {
      return getLoadedArtifacts();
   }

   @Override
   public IAtsVersion getMetricsVersion() {
      IAtsVersion verArt = getWorldEditorProvider().getTargetedVersionArtifact();
      if (verArt != null) {
         return verArt;
      }
      for (Artifact artifact : getLoadedArtifacts()) {
         IAtsWorkItem workItem = (IAtsWorkItem) artifact;
         if (artifact instanceof IAtsWorkItem && AtsApiService.get().getVersionService().hasTargetedVersion(workItem)) {
            return AtsApiService.get().getVersionService().getTargetedVersion(workItem);
         }
      }
      return null;
   }

   @Override
   public double getManHoursPerDayPreference() {
      return WorldComposite.getManHoursPerDayPreference(getLoadedArtifacts());
   }

   @Override
   public void reflow() {
      getWorldXWidgetActionPage().reflow();
   }

   @Override
   public void createToolBarPulldown(Menu menu) {
      new MenuItem(menu, SWT.SEPARATOR);
      try {
         for (IAtsWorldEditorItem item : AtsWorldEditorItems.getItems()) {
            for (final Action action : item.getWorldEditorMenuActions(getWorldEditorProvider(), this)) {
               actionToMenuItem(menu, action, SWT.PUSH);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
   }

   @Override
   public void doSaveAs() {
      // do nothing
   }

   @Override
   public boolean isSaveAsAllowed() {
      return false;
   }

   @Override
   public void onDirtied() {
      // do nothing
   }

   public boolean isTaskEditor() {
      return getWorldEditorInput().getIWorldEditorProvider() instanceof TaskEditorProvider;
   }

   protected MenuItem actionToMenuItem(Menu menu, final Action action, final int buttonType) {
      final Action fAction = action;
      MenuItem item = new MenuItem(menu, buttonType);
      item.setText(action.getText());
      if (action.getImageDescriptor() != null) {
         item.setImage(action.getImageDescriptor().createImage());
      }
      item.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            if (buttonType == SWT.CHECK) {
               action.setChecked(!action.isChecked());
            }
            fAction.run();
         }
      });
      return item;
   }

   public void reflowParameterSection() {
      worldXWidgetActionPage.getManagedForm().reflow(true);
   }

}
