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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;
import org.eclipse.osee.framework.ui.plugin.util.AbstractSelectionEnabledHandler;
import org.eclipse.osee.framework.ui.plugin.util.Commands;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener;
import org.eclipse.osee.framework.ui.skynet.search.AbstractArtifactSearchViewPage;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.util.SkynetDragAndDrop;
import org.eclipse.search.ui.text.Match;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * @author Jeff C. Phillips
 */
public class ArtifactSearchViewPage extends AbstractArtifactSearchViewPage implements IRebuildMenuListener, IFrameworkTransactionEventListener, IArtifactsPurgedEventListener {
   private static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynetd.ArtifactSearchView";
   private IHandlerService handlerService;
   private TableViewer viewer;
   private ArtifactLabelProvider artifactLabelProvider;

   public static class DecoratorIgnoringViewerSorter extends ViewerSorter {
      private final ILabelProvider aLabelProvider;

      public DecoratorIgnoringViewerSorter(ILabelProvider labelProvider) {
         super(null); // lazy initialization
         aLabelProvider = labelProvider;
      }

      @Override
      @SuppressWarnings("unchecked")
      public int compare(Viewer viewer, Object e1, Object e2) {
         String name1 = aLabelProvider.getText(e1);
         String name2 = aLabelProvider.getText(e2);
         if (name1 == null) name1 = "";
         if (name2 == null) name2 = "";
         return getComparator().compare(name1, name2);
      }
   }

   private ArtifactListContentProvider aContentProvider;

   public ArtifactSearchViewPage() {
   }

   @Override
   protected void configureTableViewer(final TableViewer viewer) {
      viewer.setUseHashlookup(true);
      this.viewer = viewer;

      artifactLabelProvider = new ArtifactLabelProvider();

      viewer.setLabelProvider(new DecoratingLabelProvider(artifactLabelProvider,
            PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));

      aContentProvider = new ArtifactListContentProvider(this);
      viewer.setContentProvider(aContentProvider);
      viewer.setSorter(new DecoratorIgnoringViewerSorter(artifactLabelProvider));
      viewer.addDoubleClickListener(new ArtifactDoubleClick());

      createContextMenu(viewer.getControl());

      new SearchDragAndDrop(viewer.getTable(), VIEW_ID);

      OseeContributionItem.addTo(this, false);
      getSite().getActionBars().updateActionBars();
      OseeEventManager.addListener(this);
   }

   private void createContextMenu(Control menuOnwer) {
      MenuManager menuManager = new MenuManager();
      menuManager.setRemoveAllWhenShown(true);
      menuManager.addMenuListener(new IMenuListener() {
         public void menuAboutToShow(IMenuManager manager) {
            MenuManager menuManager = (MenuManager) manager;
            menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
         }
      });

      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      viewer.getTable().setMenu(menuManager.createContextMenu(viewer.getTable()));
      getSite().registerContextMenu("org.eclipse.osee.framework.ui.skynet.ArtifactSearchView", menuManager, viewer);

      getSite().setSelectionProvider(viewer);
      // The additions group is a standard group
      menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
      //      createOpenInAtsWorldHandler(menuManager, viewer);
      //      createOpenInAtsTaskHandler(menuManager, viewer);
   }

   private String addOpenInAtsTaskHandler(MenuManager menuManager, final TableViewer viewer) {
      CommandContributionItem openInAtsTaskCommand =
            Commands.getLocalCommandContribution("org.eclipse.osee.framework.ui.skynet.openInAtsTaskEditor", getSite(),
                  null, null, null, null, null, null, null, null);
      menuManager.add(openInAtsTaskCommand);

      return openInAtsTaskCommand.getId();
   }

   private void createOpenInAtsTaskHandler(MenuManager menuManager, final TableViewer viewer) {
      handlerService.activateHandler(addOpenInAtsTaskHandler(menuManager, viewer),

      new AbstractSelectionEnabledHandler(menuManager) {
         @Override
         public Object execute(ExecutionEvent event) throws ExecutionException {
            try {
               if (OseeAts.getAtsLib() != null) OseeAts.getAtsLib().openInAtsTaskEditor("Tasks",
                     getSelectedArtifacts(viewer));
            } catch (Exception ex) {
               OSEELog.logException(SkynetGuiPlugin.class, ex, true);
            }
            return null;
         }

         @Override
         public boolean isEnabledWithException() throws OseeCoreException {
            return true;
         }
      });
   }

   @Override
   protected void elementsChanged(Object[] objects) {
      if (aContentProvider != null) {
         aContentProvider.elementsChanged(objects);
      }
   }

   private List<Artifact> getSelectedArtifacts(TableViewer viewer) {
      IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();
      Object[] objects = selection.toArray();
      LinkedList<Artifact> artifacts = new LinkedList<Artifact>();

      if (objects.length == 0) return new ArrayList<Artifact>(0);

      if (objects[0] instanceof Match) {
         for (Object object : objects) {
            artifacts.add((Artifact) ((Match) object).getElement());
         }
      }
      return artifacts;
   }

   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   private class SearchDragAndDrop extends SkynetDragAndDrop {

      public SearchDragAndDrop(Table table, String viewId) {
         super(table, viewId);
      }

      @Override
      public Artifact[] getArtifacts() {
         IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

         Object[] matches = selection.toArray();
         Artifact[] artifacts = new Artifact[matches.length];

         for (int index = 0; index < matches.length; index++) {
            Match match = (Match) matches[index];
            artifacts[index] = (Artifact) match.getElement();
         }

         return artifacts;
      }

      @Override
      public void performDragOver(DropTargetEvent event) {
         event.detail = DND.DROP_NONE;
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.menu.IGlobalMenuHelper#getSelectedArtifacts()
    */
   public Collection<Artifact> getSelectedArtifacts() {
      return getSelectedArtifacts(viewer);
   }

   /**
    * @return the viewer
    */
   @Override
   public TableViewer getViewer() {
      return viewer;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.ui.plugin.event.Sender.Source, org.eclipse.osee.framework.skynet.core.eventx.FrameworkTransactionData)
    */
   @Override
   public void handleFrameworkTransactionEvent(Sender sender, final FrameworkTransactionData transData) throws OseeCoreException {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            if (viewer != null) {
               viewer.remove(transData.cacheDeletedArtifacts);
               viewer.refresh();
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener#handleArtifactsPurgedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, java.util.Collection, java.util.Collection)
    */
   @Override
   public void handleArtifactsPurgedEvent(Sender sender, final LoadedArtifacts loadedArtifacts) {
      Displays.ensureInDisplayThread(new Runnable() {
         /* (non-Javadoc)
          * @see java.lang.Runnable#run()
          */
         @Override
         public void run() {
            try {
               if (viewer != null) {
                  viewer.remove(loadedArtifacts.getLoadedArtifacts());
                  viewer.refresh();
               }
            } catch (Exception ex) {
               OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
            }
         }
      });
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.listener.IRebuildMenuListener#rebuildMenu()
    */
   @Override
   public void rebuildMenu() {
   }
}
