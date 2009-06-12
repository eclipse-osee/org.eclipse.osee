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
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.ArtifactDoubleClick;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.ImageCapture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.zest.core.viewers.GraphViewer;
import org.eclipse.zest.core.widgets.GraphItem;
import org.eclipse.zest.core.widgets.GraphNode;
import org.eclipse.zest.core.widgets.ZestStyles;

/**
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public class SkyWalkerView extends ViewPart {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerView";
   protected GraphViewer viewer;
   private static final String INPUT = "input";
   private static final String GUID = "guid";
   private static final String BRANCHID = "branchId";
   private String storedGuid;
   private String storedBrandId;
   private final SkyWalkerOptions options = new SkyWalkerOptions();
   private final List<Artifact> history = new LinkedList<Artifact>();
   private Action filterAction;
   private Composite viewerComp;
   protected SashForm sashForm;

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
    */
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

         public void doubleClick(DoubleClickEvent event) {
            IStructuredSelection selection = (IStructuredSelection) event.getSelection();
            Iterator<?> itemsIter = selection.iterator();
            while (itemsIter.hasNext()) {
               Object obj = itemsIter.next();
               if (!(obj instanceof Artifact)) continue;
               Artifact artifact = (Artifact) obj;
               explore(artifact);
            }
         }

      });

      Composite child1 = new Composite(sashForm, SWT.BORDER);
      child1.setLayout(new FillLayout());
      new SkyWalkerTabOptions(child1, SWT.NONE, options);

      options.addSkyWalkerOptionsChangeListener(new ISkyWalkerOptionsChangeListener() {
         /*
          * (non-Javadoc)
          * 
          * @see org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerOptionsChangeListener#modified(org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerOptionsChangeListener.ModType)
          */
         public void modified(ModType... modTypes) {
            List<ModType> modList = Arrays.asList(modTypes);
            // Don't redraw if artifact has been changed; else get in infinite loop
            if (modList.contains(ModType.Artifact)) return;
            if (modList.contains(ModType.Layout))
               viewer.setLayoutAlgorithm(options.getLayout(), true);
            else if (modList.contains(ModType.Show_Attribute)) {
               try {
                  // exploring another artifact and then the original forces a redraw of all the
                  // objects
                  // which is necessary for a node size change
                  Artifact art = (Artifact) viewer.getInput();
                  explore(UserManager.getUser(SystemUser.UnAssigned));
                  if (art != null) explore(art);
               } catch (Exception ex) {
                  // DO Nothing
               }
            } else
               redraw();
         }
      });

      sashForm.setWeights(new int[] {75, 25});

      createActions();
      viewer.setLayoutAlgorithm(options.getLayout());
      // Restore current artifact if stored upon shutdown
      try {
         if (storedGuid != null) {
            Artifact art =
                  ArtifactQuery.getArtifactFromId(storedGuid, BranchManager.getBranch(Integer.parseInt(storedBrandId)));
            if (art != null) explore(art);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
      }
   }

   protected void createActions() {

      IActionBars bars = getViewSite().getActionBars();
      // IMenuManager mm = bars.getMenuManager();
      IToolBarManager tbm = bars.getToolBarManager();

      filterAction = new Action("Enable Filters", Action.AS_CHECK_BOX) {
         @Override
         public void run() {
            options.setFilterEnabled(filterAction.isChecked());
            redraw();
         }
      };
      filterAction.setChecked(options.isFilterEnabled());
      filterAction.setToolTipText("Enable Filters");
      filterAction.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("filter.gif"));
      tbm.add(filterAction);

      Action action = new Action() {
         @Override
         public void run() {
            ArtifactDoubleClick.openArtifact(viewer.getSelection());
         }
      };
      action.setText("Open Selected");
      action.setToolTipText("Open Selected");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("edit2.gif"));
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
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("back.gif"));
      tbm.add(action);

      action = new Action() {
         @Override
         public void run() {
            ImageCapture imgCapture = new ImageCapture(viewerComp);
            imgCapture.popupDialog();
         }
      };
      action.setText("Print");
      action.setToolTipText("Print");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("print.gif"));
      tbm.add(action);

      action = new Action() {
         @Override
         public void run() {
            handleSaveOptions();
         }
      };
      action.setText("Save Options");
      action.setToolTipText("Save Options");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("save.gif"));
      tbm.add(action);

      action = new Action() {
         @Override
         public void run() {
            handleLoadOptions();
         }
      };
      action.setText("Load Options");
      action.setToolTipText("Load Options");
      action.setImageDescriptor(SkynetGuiPlugin.getInstance().getImageDescriptor("load.gif"));
      tbm.add(action);

      action = new Action() {
         @Override
         public void run() {
            redraw();
         }
      };
      action.setText("Refresh");
      action.setToolTipText("Refresh");
      action.setImageDescriptor(ImageManager.getImageDescriptor(FrameworkImage.REFRESH));
      tbm.add(action);
   }

   public void handleSaveOptions() {
      final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell().getShell(), SWT.SAVE);
      dialog.setFilterExtensions(new String[] {"*.sky"});
      String filename = dialog.open();
      if (filename != null) {
         try {
            Lib.writeStringToFile(options.toXml(), new File(filename));
            AWorkbench.popup("Saved", "Save Successful");
         } catch (IOException ex) {
            OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   }

   public void handleLoadOptions() {
      final FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell().getShell(), SWT.OPEN);
      dialog.setFilterExtensions(new String[] {"*.sky"});
      String filename = dialog.open();
      if (filename != null) {
         String xml = AFile.readFile(filename);
         options.fromXml(xml);
         explore(options.getArtifact());
      }
   }

   public void redraw() {
      if (viewer.getInput() != null) explore((Artifact) viewer.getInput());
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
    */
   @Override
   public void setFocus() {
      viewer.getControl().setFocus();
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.skynet.gui.ats.IActionable#getActionDescription()
    */
   public String getActionDescription() {
      return "";
   }

   public void explore(Artifact artifact) {
      explore(artifact, false);
   }

   private boolean inExplore = false;

   private void explore(Artifact artifact, boolean fromHistory) {
      // If already in explore method, don't respond to events trying to redraw
      if (inExplore) return;
      inExplore = true;
      options.setArtifact(artifact);

      // Add current artifact to history only if explore wasn't caused by going back in history
      if (!fromHistory && viewer.getInput() != null) {
         Artifact currArt = (Artifact) viewer.getInput();
         if (history.size() == 0)
            history.add(currArt);
         else if (history.size() > 0 && !history.get(history.size() - 1).equals(currArt)) history.add(currArt);
      }
      viewer.setInput(options.getArtifact());
      // Highlight center object
      GraphItem item = viewer.findGraphItem(options.getArtifact());
      if (item != null && (item instanceof GraphNode)) {
         GraphNode node = (GraphNode) item;
         node.setBackgroundColor(Display.getCurrent().getSystemColor(SWT.COLOR_CYAN));
         viewer.update(node, null);
      }
      setPartName("Sky Walker (" + artifact.getDescriptiveName() + ")");
      inExplore = false;
   }

   public static void exploreArtifact(Artifact artifact) {
      exploreArtifact(artifact.getDescriptiveName(), artifact);
   }

   public static void exploreArtifact(String name, Artifact artifact) {
      IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
      SkyWalkerView view;
      try {
         view =
               (SkyWalkerView) page.showView(SkyWalkerView.VIEW_ID, new GUID().toString(), IWorkbenchPage.VIEW_ACTIVATE);
         view.explore(artifact);
      } catch (Exception ex) {
         throw new RuntimeException(ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.ui.part.ViewPart#saveState(org.eclipse.ui.IMemento)
    */
   @Override
   public void saveState(IMemento memento) {
      super.saveState(memento);

      if (viewer.getInput() == null) return;
      Artifact artifact = (Artifact) viewer.getInput();
      memento = memento.createChild(INPUT);
      memento.putString(GUID, artifact.getGuid());
      memento.putString(BRANCHID, String.valueOf(artifact.getBranch().getBranchId()));
   }

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      try {
         if (memento != null) {
            memento = memento.getChild(INPUT);
            if (memento != null) {
               storedGuid = memento.getString(GUID);
               storedBrandId = memento.getString(BRANCHID);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.WARNING, "Sky Walker error on init: ", ex);
      }
   }

   /**
    * @return the options
    */
   public SkyWalkerOptions getOptions() {
      return options;
   }

}
