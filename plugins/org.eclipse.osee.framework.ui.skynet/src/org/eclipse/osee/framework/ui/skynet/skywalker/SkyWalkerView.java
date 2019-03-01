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

package org.eclipse.osee.framework.ui.skynet.skywalker;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.PluginUiImage;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.widgets.GenericViewPart;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;

/**
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public class SkyWalkerView extends GenericViewPart {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView";
   protected GraphViewer viewer;
   private static final String INPUT_KEY = "input";
   private static final String GUID_KEY = "guid";
   private static final String BRANCHID_KEY = "branchUuid";
   private String storedGuid;
   private String storedBranchId;
   private final SkyWalkerOptions options = new SkyWalkerOptions();
   private final List<Artifact> history = new LinkedList<>();
   private Action filterAction;
   private Composite viewerComp;
   protected SashForm sashForm;

   @Override
   public void createPartControl(Composite parent) {

      sashForm = new SashForm(parent, SWT.HORIZONTAL);
      sashForm.setLayout(new FillLayout());

      viewerComp = new Composite(sashForm, SWT.NONE);
      viewerComp.setLayout(new FillLayout());

      viewer = new GraphViewer(viewerComp, ZestStyles.NONE);
      viewer.setContentProvider(new ArtifactGraphContentProvider(options));
      viewer.setLabelProvider(new ArtifactGraphLabelProvider(options));
      viewer.setConnectionStyle(ZestStyles.CONNECTIONS_SOLID);
      viewer.setNodeStyle(ZestStyles.NODES_NO_LAYOUT_RESIZE);
      viewer.addDoubleClickListener(new IDoubleClickListener() {

         @Override
         public void doubleClick(DoubleClickEvent event) {
            handleDoubleClick(event);
         }

         public void handleDoubleClick(DoubleClickEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Iterator<?> itemsIter = selection.iterator();
            while (itemsIter.hasNext()) {
               Object obj = itemsIter.next();
               if (!(obj instanceof Artifact)) {
                  continue;
               }
               Artifact artifact = (Artifact) obj;
               explore(artifact);
            }
         }

      });

      Composite child1 = new Composite(sashForm, SWT.BORDER);
      child1.setLayout(new FillLayout());
      SkyWalkerTabOptions tabOptions = new SkyWalkerTabOptions(child1, SWT.NONE, options);

      options.addSkyWalkerOptionsChangeListener(new ISkyWalkerOptionsChangeListener() {
         @Override
         public void modified(ModType... modTypes) {
            List<ModType> modList = Arrays.asList(modTypes);
            // Don't redraw if artifact has been changed; else get in infinite loop
            if (modList.contains(ModType.Artifact)) {
               return;
            }
            if (modList.contains(ModType.Layout)) {
               viewer.setLayoutAlgorithm(options.getLayout(), true);
            } else if (modList.contains(ModType.Show_Attribute)) {
               try {
                  // exploring another artifact and then the original forces a redraw of all the
                  // objects
                  // which is necessary for a node size change
                  Artifact art = (Artifact) viewer.getInput();
                  explore(UserManager.getUser(SystemUser.UnAssigned));
                  if (art != null) {
                     explore(art);
                  }
               } catch (Exception ex) {
                  // DO Nothing
               }
            } else {
               redraw();
            }
         }
      });

      sashForm.setWeights(new int[] {75, 25});

      createActions();
      viewer.setLayoutAlgorithm(options.getLayout());
      // Restore current artifact if stored upon shutdown
      try {
         if (storedGuid != null) {
            Artifact art = ArtifactQuery.getArtifactFromId(storedGuid, BranchId.valueOf(storedBranchId));
            if (art != null) {
               explore(art);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }

      HelpUtil.setHelp(viewer.getControl(), OseeHelpContext.SKY_WALKER_VIEW);
      HelpUtil.setHelp(tabOptions.getControl(), OseeHelpContext.SKY_WALKER_VIEW);

      setFocusWidget(viewer.getControl());
   }

   protected void createActions() {

      IActionBars bars = getViewSite().getActionBars();
      // IMenuManager mm = bars.getMenuManager();
      IToolBarManager tbm = bars.getToolBarManager();

      filterAction = new Action("Enable Filters", IAction.AS_CHECK_BOX) {
         @Override
         public void run() {
            options.setFilterEnabled(filterAction.isChecked());
            redraw();
         }
      };
      filterAction.setChecked(options.isFilterEnabled());
      filterAction.setToolTipText("Enable Filters");
      filterAction.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.FILTERS));
      tbm.add(filterAction);

      Action action = new Action() {
         @Override
         public void run() {
            ArtifactDoubleClick.openArtifact(viewer.getSelection());
         }
      };
      action.setText("Open Selected");
      action.setToolTipText("Open Selected");
      action.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.EDIT2));
      tbm.add(action);

      action = new Action() {
         @Override
         public void run() {
            if (history.size() > 0) {
               Artifact art = history.get(history.size() - 1);
               history.remove(history.size() - 1);
               explore(art, true);
            }
         }
      };
      action.setText("Back");
      action.setToolTipText("Back");
      action.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.BACK));
      tbm.add(action);

      action = new Action() {
         @Override
         public void run() {
            handleSaveOptions();
         }
      };
      action.setText("Save Options");
      action.setToolTipText("Save Options");
      action.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.SAVE));
      tbm.add(action);

      action = new Action() {
         @Override
         public void run() {
            handleLoadOptions();
         }
      };
      action.setText("Load Options");
      action.setToolTipText("Load Options");
      action.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.LOAD));
      tbm.add(action);

      action = new Action() {
         @Override
         public void run() {
            redraw();
         }
      };
      action.setText("Refresh");
      action.setToolTipText("Refresh");
      action.setImageDescriptor(ImageManager.getImageDescriptor(PluginUiImage.REFRESH));
      tbm.add(action);
   }

   public void handleSaveOptions() {
      final FileDialog dialog = new FileDialog(Displays.getActiveShell().getShell(), SWT.SAVE);
      dialog.setFilterExtensions(new String[] {"*.sky"});
      String filename = dialog.open();
      if (filename != null) {
         try {
            Lib.writeStringToFile(options.toXml(), new File(filename));
            AWorkbench.popup("Saved", "Save Successful");
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   private void handleLoadOptions() {
      final FileDialog dialog = new FileDialog(Displays.getActiveShell().getShell(), SWT.OPEN);
      dialog.setFilterExtensions(new String[] {"*.sky"});
      String filename = dialog.open();
      if (filename != null) {
         try {
            String xml = Lib.fileToString(new File(filename));
            options.fromXml(xml);
            explore(options.getArtifact());
         } catch (IOException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   public void redraw() {
      if (viewer.getInput() != null) {
         explore((Artifact) viewer.getInput());
      }
   }

   public void explore(Artifact artifact) {
      explore(artifact, false);
   }

   private boolean inExplore = false;

   private void explore(Artifact artifact, boolean fromHistory) {
      // If already in explore method, don't respond to events trying to redraw
      if (inExplore) {
         return;
      }
      inExplore = true;
      options.setArtifact(artifact);

      // Add current artifact to history only if explore wasn't caused by going back in history
      if (!fromHistory && viewer.getInput() != null) {
         Artifact currArt = (Artifact) viewer.getInput();
         if (history.isEmpty()) {
            history.add(currArt);
         } else if (history.size() > 0 && !history.get(history.size() - 1).equals(currArt)) {
            history.add(currArt);
         }
      }
      viewer.setInput(options.getArtifact());
      // Highlight center object
      GraphItem item = viewer.findGraphItem(options.getArtifact());
      if (item != null && item instanceof GraphNode) {
         GraphNode node = (GraphNode) item;
         node.setBackgroundColor(Displays.getSystemColor(SWT.COLOR_CYAN));
         viewer.update(node, null);
      }
      setPartName("Sky Walker (" + artifact.getName() + ")");
      inExplore = false;
   }

   public static void exploreArtifact(Artifact artifact) {
      exploreArtifact(artifact.getName(), artifact);
   }

   public static void exploreArtifact(String name, Artifact artifact) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      SkyWalkerView view;
      try {
         String id = GUID.create();
         view = (SkyWalkerView) page.showView(SkyWalkerView.VIEW_ID, id, IWorkbenchPage.VIEW_ACTIVATE);
         view.explore(artifact);
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);

      if (viewer.getInput() == null) {
         return;
      }
      Artifact artifact = (Artifact) viewer.getInput();
      memento = memento.createChild(INPUT_KEY);
      memento.putString(GUID_KEY, artifact.getGuid());
      try {
         memento.putString(BRANCHID_KEY, artifact.getBranch().getIdString());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.WARNING, "Sky Walker error on save: ", ex);
      }
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         if (memento != null) {
            memento = memento.getChild(INPUT_KEY);
            if (memento != null) {
               storedGuid = memento.getString(GUID_KEY);
               storedBranchId = memento.getString(BRANCHID_KEY);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.WARNING, "Sky Walker error on init: ", ex);
      }
   }

   public SkyWalkerOptions getOptions() {
      return options;
   }

}
