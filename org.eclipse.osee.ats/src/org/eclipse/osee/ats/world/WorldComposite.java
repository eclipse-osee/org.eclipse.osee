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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.nebula.widgets.xviewer.customize.CustomizeData;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorAction.IOpenNewAtsWorldEditorHandler;
import org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorSelectedAction.IOpenNewAtsWorldEditorSelectedHandler;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.world.search.WorldSearchItem;
import org.eclipse.osee.ats.world.search.WorldSearchItem.SearchType;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.action.RefreshAction.IRefreshActionHandler;
import org.eclipse.osee.framework.ui.skynet.artifact.editor.ArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.util.ElapsedTime;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * @author Donald G. Dunne
 */
public class WorldComposite extends ScrolledComposite implements IOpenNewAtsWorldEditorHandler, IOpenNewAtsWorldEditorSelectedHandler, IRefreshActionHandler {

   private final WorldXViewer worldXViewer;
   private final Set<Artifact> worldArts = new HashSet<Artifact>(200);
   private final Set<Artifact> otherArts = new HashSet<Artifact>(200);
   private final IWorldEditor iWorldEditor;
   private final Composite mainComp;

   public WorldComposite(IWorldEditor worldEditor, Composite parent, int style) {
      super(parent, style);
      this.iWorldEditor = worldEditor;

      setLayout(new GridLayout(1, true));
      setLayoutData(new GridData(GridData.FILL_BOTH));

      mainComp = new Composite(this, SWT.NONE);
      mainComp.setLayout(ALayout.getZeroMarginLayout());
      mainComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      if (!DbConnectionExceptionComposite.dbConnectionIsOk(this)) {
         worldXViewer = null;
         return;
      }

      worldXViewer = new WorldXViewer(mainComp, SWT.MULTI | SWT.BORDER | SWT.FULL_SELECTION);
      worldXViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));

      worldXViewer.setContentProvider(new WorldContentProvider(worldXViewer));
      worldXViewer.setLabelProvider(new WorldLabelProvider(worldXViewer));

      new WorldViewDragAndDrop(this, WorldEditor.EDITOR_ID);

      setContent(mainComp);
      setExpandHorizontal(true);
      setExpandVertical(true);
      layout();

   }

   public double getManHoursPerDayPreference() throws OseeCoreException {
      if (worldArts.size() > 0) {
         Artifact artifact = worldArts.iterator().next();
         if (artifact instanceof ActionArtifact) {
            artifact = ((ActionArtifact) artifact).getTeamWorkFlowArtifacts().iterator().next();
         }
         return ((StateMachineArtifact) artifact).getManHrsPerDayPreference();
      }
      return StateMachineArtifact.DEFAULT_HOURS_PER_WORK_DAY;
   }

   public void setCustomizeData(CustomizeData customizeData) {
      worldXViewer.getCustomizeMgr().loadCustomization(customizeData);
   }

   public Control getControl() {
      return worldXViewer.getControl();
   }

   public void load(final String name, final Collection<? extends Artifact> arts, TableLoadOption... tableLoadOption) {
      load(name, arts, null, tableLoadOption);
   }

   public void load(final String name, final Collection<? extends Artifact> arts, final CustomizeData customizeData, TableLoadOption... tableLoadOption) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            worldArts.clear();
            otherArts.clear();
            for (Artifact art : arts) {
               if (art instanceof IWorldViewArtifact) {
                  worldArts.add(art);
               } else {
                  otherArts.add(art);
               }
            }
            if (customizeData != null && !worldXViewer.getCustomizeMgr().generateCustDataFromTable().equals(
                  customizeData)) {
               setCustomizeData(customizeData);
            }
            if (arts.size() == 0) {
               setTableTitle("No Results Found - " + name, true);
            } else {
               setTableTitle(name, false);
            }
            ElapsedTime elapsedTime = new ElapsedTime("WorldComposite - setInput");
            worldXViewer.setInput(worldArts);
            worldXViewer.updateStatusLabel();
            elapsedTime.end();
            if (otherArts.size() > 0) {
               if (MessageDialog.openConfirm(
                     Display.getCurrent().getActiveShell(),
                     "Open in Artifact Editor?",
                     otherArts.size() + " Non-WorldView Artifacts were returned from request.\n\nOpen in Artifact Editor?")) {
                  ArtifactEditor.editArtifacts(otherArts);
               }
            }
            worldXViewer.getTree().setFocus();
         }
      }, true);
      // Need to reflow the managed page based on the results.  Don't put this in the above thread.
      iWorldEditor.reflow();
   }

   public static class FilterLabelProvider implements ILabelProvider {

      public Image getImage(Object arg0) {
         return null;
      }

      public String getText(Object arg0) {
         try {
            return ((WorldSearchItem) arg0).getSelectedName(SearchType.Search);
         } catch (OseeCoreException ex) {
            return ex.getLocalizedMessage();
         }
      }

      public void addListener(ILabelProviderListener arg0) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object arg0, String arg1) {
         return false;
      }

      public void removeListener(ILabelProviderListener arg0) {
      }
   }

   public static class FilterContentProvider implements IStructuredContentProvider {
      public Object[] getElements(Object arg0) {
         return ((ArrayList<?>) arg0).toArray();
      }

      public void dispose() {
      }

      public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
      }
   }

   public void setTableTitle(final String title, final boolean warning) {
      Displays.ensureInDisplayThread(new Runnable() {
         public void run() {
            iWorldEditor.setTableTitle(title, warning);
            worldXViewer.setReportingTitle(title + " - " + XDate.getDateNow());
         };
      });
   }

   public ArrayList<Artifact> getLoadedArtifacts() {
      return getXViewer().getLoadedArtifacts();
   }

   public void disposeComposite() {
      if (worldXViewer != null && !worldXViewer.getTree().isDisposed()) {
         worldXViewer.dispose();
      }
   }

   /**
    * @return the xViewer
    */
   public WorldXViewer getXViewer() {
      return worldXViewer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.actions.IRefreshActionHandler#refreshActionHandler()
    */
   @Override
   public void refreshActionHandler() {
      try {
         iWorldEditor.reSearch();
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.actions.OpenNewAtsWorldEditor.IOpenNewAtsWorldEditorHandler#getCustomizeDataCopy()
    */
   @Override
   public CustomizeData getCustomizeDataCopy() throws OseeCoreException {
      return worldXViewer.getCustomizeMgr().generateCustDataFromTable();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.actions.OpenNewAtsWorldEditor.IOpenNewAtsWorldEditorHandler#getWorldEditorProviderCopy()
    */
   @Override
   public IWorldEditorProvider getWorldEditorProviderCopy() throws OseeCoreException {
      return iWorldEditor.getWorldEditorProvider().copyProvider();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.ats.actions.OpenNewAtsWorldEditorSelected.IOpenNewAtsWorldEditorSelectedHandler#getSelectedArtifacts()
    */
   @Override
   public ArrayList<Artifact> getSelectedArtifacts() throws OseeCoreException {
      return worldXViewer.getSelectedArtifacts();
   }

}
