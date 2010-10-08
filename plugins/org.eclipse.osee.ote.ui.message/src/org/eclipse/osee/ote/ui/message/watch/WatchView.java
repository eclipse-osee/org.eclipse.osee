/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ote.ui.message.watch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.nebula.widgets.xviewer.XViewer;
import org.eclipse.osee.connection.service.IServiceConnector;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.util.AFile;
import org.eclipse.osee.framework.jdk.core.util.benchmark.Benchmark;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.IActionable;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.ui.plugin.OseeUiActions;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.ote.client.msg.IOteMessageService;
import org.eclipse.osee.ote.message.interfaces.ITestEnvironmentMessageSystem;
import org.eclipse.osee.ote.message.tool.IUdpTransferListener;
import org.eclipse.osee.ote.message.tool.TransferConfig;
import org.eclipse.osee.ote.service.ConnectionEvent;
import org.eclipse.osee.ote.service.IMessageDictionary;
import org.eclipse.osee.ote.service.IMessageDictionaryListener;
import org.eclipse.osee.ote.service.IOteClientService;
import org.eclipse.osee.ote.service.ITestConnectionListener;
import org.eclipse.osee.ote.ui.message.OteMessageImage;
import org.eclipse.osee.ote.ui.message.internal.Activator;
import org.eclipse.osee.ote.ui.message.messageXViewer.MessageXViewer;
import org.eclipse.osee.ote.ui.message.tree.AbstractTreeNode;
import org.eclipse.osee.ote.ui.message.tree.ElementNode;
import org.eclipse.osee.ote.ui.message.tree.INodeVisitor;
import org.eclipse.osee.ote.ui.message.tree.MessageNode;
import org.eclipse.osee.ote.ui.message.tree.MessageWatchLabelProvider;
import org.eclipse.osee.ote.ui.message.tree.RootNode;
import org.eclipse.osee.ote.ui.message.tree.WatchList;
import org.eclipse.osee.ote.ui.message.tree.WatchedMessageNode;
import org.eclipse.osee.ote.ui.message.util.ClientMessageServiceTracker;
import org.eclipse.osee.ote.ui.message.util.IOteMessageClientView;
import org.eclipse.osee.ote.ui.message.watch.action.ClearUpdatesAction;
import org.eclipse.osee.ote.ui.message.watch.action.DeleteSelectionAction;
import org.eclipse.osee.ote.ui.message.watch.action.SendMessageAction;
import org.eclipse.osee.ote.ui.message.watch.action.SetDataSourceMenu;
import org.eclipse.osee.ote.ui.message.watch.action.SetMessageModeMenu;
import org.eclipse.osee.ote.ui.message.watch.action.SetValueAction;
import org.eclipse.osee.ote.ui.message.watch.action.WatchElementAction;
import org.eclipse.osee.ote.ui.message.watch.action.ZeroizeElementAction;
import org.eclipse.osee.ote.ui.message.watch.action.ZeroizeMessageAction;
import org.eclipse.osee.ote.ui.message.watch.recording.RecordingWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;

/**
 * A view that allows the monitoring of messages and their associated elements
 * 
 * @author Ken J. Aguilar
 */
public final class WatchView extends ViewPart implements IActionable, IMessageDictionaryListener, ITestConnectionListener, IOteMessageClientView {
   private MessageXViewer treeViewer;

   private final ClientMessageServiceTracker msgServiceTracker;
   private Label statusTxt;

   private final File watchFile;
   private Button recordButton;
   private final Benchmark benchMark = new Benchmark("Message Watch Update Time");

   private static final Pattern elmPattern = Pattern.compile("^(osee\\.test\\.core\\.message\\.[^.]+\\..+)\\.(.+)$");
   private static final Pattern msgPattern = Pattern.compile("^(osee\\.test\\.core\\.message\\.[^.]+\\..+)$");

   public static final String VIEW_ID = "org.eclipse.osee.ote.ui.message.watch.WatchView";

   private DetailsBox detailsBox;
   final IUdpTransferListener recBtnListener = new IUdpTransferListener() {

      @Override
      public void onTransferComplete(final TransferConfig config) {
         OseeLog.log(Activator.class, Level.INFO, "file transfer complete");
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               openInfo("Message Recorder",
                  "Message recording file " + config.getFileName() + " is now ready for opening");
            }
         });
      }

      @Override
      public void onTransferException(final TransferConfig config, final Throwable t) {
         OseeLog.log(Activator.class, Level.SEVERE, "problems writing to recorder output file " + config.getFileName(),
            t);
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               recordButton.setSelection(false);
               openInfo("Message Recorder",
                  "An exception occurred while writing to recorder output file " + config.getFileName());
            }
         });
      }
   };

   private IOteMessageService messageService = null;

   private final SelectionListener recBtnHandler = new SelectionListener() {

      @Override
      public void widgetDefaultSelected(SelectionEvent e) {
         widgetSelected(e);
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
         if (recordButton.getSelection()) {

            RecordingWizard recordingWizard = new RecordingWizard(watchList);
            final WizardDialog recdialog = new WizardDialog(Displays.getActiveShell(), recordingWizard);
            int recResult = recdialog.open();
            if (Window.OK == recResult) {
               try {
                  saveWatchFile();
                  messageService.startRecording(recordingWizard.getFileName(),
                     recordingWizard.getFilteredMessageRecordDetails()).addListener(recBtnListener);
               } catch (FileNotFoundException ex) {
                  MessageDialog.openError(Displays.getActiveShell(), "Recording Error",
                     "Failed to open file for writing. " + "Make sure its not being used by another application");
                  recordButton.setSelection(false);
               } catch (Throwable ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Failed to start message recording", ex);
                  MessageDialog.openError(Displays.getActiveShell(), "Recording Error",
                     "Exception ocurred while recording. see error log");
                  recordButton.setSelection(false);
               }
            } else {
               recordButton.setSelection(false);
            }
         } else {
            try {
               messageService.stopRecording();
            } catch (IOException ioe) {
               OseeLog.log(Activator.class, Level.WARNING, "problem when attempting to stop recording", ioe);
            } catch (Throwable t) {
               OseeLog.log(Activator.class, Level.SEVERE, "problem when attempting to stop recording", t);
            }
         }
      }
   };

   private static enum Status {
      /**
       * no active test manager
       */
      NO_TEST_MANAGER("No test manager running"),
      /**
       * active test manager but not connected to a host
       */
      NOT_CONNECTED("%s: Not connected to a host"),
      /**
       * active test manager and connected to a host
       */
      CONNECTED("Connected to %s (%s)");

      private final String txt;

      Status(final String txt) {
         this.txt = txt;
      }

      public String asString(Object... args) {
         return String.format(txt, args);
      }

   }

   private Composite parentComposite;
   private WatchList watchList;

   public WatchView() {
      watchFile = OseeData.getFile("msgWatch.txt");
      msgServiceTracker = new ClientMessageServiceTracker(Activator.getDefault().getBundle().getBundleContext(), this);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void createPartControl(Composite parent) {
      final int numColumns = 4;

      parentComposite = parent;

      GridLayout layout = new GridLayout();
      layout.numColumns = numColumns;
      layout.verticalSpacing = 0;
      layout.marginWidth = 5;
      layout.marginHeight = 5;
      parent.setLayout(layout);

      Label label = new Label(parent, SWT.RIGHT);
      label.setText("Status:");
      Widgets.positionGridItem(label, false, false, SWT.END, SWT.CENTER, 1);

      statusTxt = new Label(parent, SWT.READ_ONLY);
      statusTxt.setText(Status.NO_TEST_MANAGER.asString());
      Widgets.positionGridItem(statusTxt, false, false, SWT.FILL, SWT.BEGINNING, 3);

      Composite buttons = new Composite(parent, SWT.NONE);
      buttons.setLayout(new RowLayout(SWT.HORIZONTAL));

      recordButton = new Button(buttons, SWT.TOGGLE);
      recordButton.setText("REC");
      recordButton.setToolTipText("Record the messages and elements currently shown in Message Watch.");
      recordButton.addSelectionListener(recBtnHandler);
      recordButton.setEnabled(false);

      IExtension[] extensions =
         Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.osee.ote.ui.message.ToolbarItem").getExtensions();
      for (IExtension ext : extensions) {
         for (IConfigurationElement el : ext.getConfigurationElements()) {
            if (el.getName().equals("ToolbarItem")) {
               String actionClass = el.getAttribute("Action");
               String icon = el.getAttribute("Icon");
               String btnLabel = el.getAttribute("Label");
               String tooltip = el.getAttribute("Tooltip");

               Class<? extends Action> clazz;
               try {
                  Bundle bundle = Platform.getBundle(el.getContributor().getName());
                  clazz = bundle.loadClass(actionClass).asSubclass(Action.class);

                  Action action = clazz.newInstance();
                  ActionButton btn =
                     new ActionButton(buttons, SWT.PUSH, action, btnLabel, el.getContributor().getName());
                  btn.setToolTipText(tooltip);

                  if (icon != null) {
                     URL url = bundle.getEntry(icon);
                     if (url == null) {
                        throw new OseeArgumentException("Invalid icon path [{%s}/%s]", el.getContributor().getName(),
                           icon);
                     } else {
                        ImageDescriptor desc = ImageDescriptor.createFromURL(url);
                        Image img = desc.createImage();
                        if (img == null) {
                           throw new OseeArgumentException("Unable to create Image from [{%s}/%s]",
                              el.getContributor().getName(), icon);
                        } else {
                           btn.setImage(img);
                        }
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      }

      final SashForm sashForm = new SashForm(parent, SWT.VERTICAL);
      // sashForm.SASH_WIDTH = 1;
      Widgets.positionGridItem(sashForm, true, true, SWT.FILL, SWT.FILL, numColumns);

      Composite comp = new Composite(sashForm, SWT.NONE);
      comp.setLayout(new GridLayout(1, false));
      GridData gd = new GridData(GridData.FILL_BOTH);
      comp.setLayoutData(gd);

      // Create the tree treeViewer as a child of the composite parent
      treeViewer =
         new MessageXViewer(comp,
            SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.DOUBLE_BUFFERED);
      GridData layoutData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL | GridData.FILL_BOTH);
      layoutData.horizontalSpan = numColumns;
      treeViewer.getControl().setLayoutData(layoutData);
      watchList = new WatchList(this);
      treeViewer.setContentProvider(watchList);
      treeViewer.setLabelProvider(new MessageWatchLabelProvider(treeViewer));
      treeViewer.setUseHashlookup(true);

      treeViewer.getTree().setHeaderVisible(true);
      treeViewer.getTree().setLinesVisible(true);

      detailsBox = new DetailsBox(sashForm);

      sashForm.setWeights(new int[] {75, 25});
      // Add Listeners to the Tree
      treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
         private AbstractTreeNode lastNodeSelected = null;

         @Override
         public void selectionChanged(SelectionChangedEvent event) {
            final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();

            final AbstractTreeNode node = (AbstractTreeNode) selection.getFirstElement();
            if (node != null && node != lastNodeSelected) {
               if (lastNodeSelected != null) {
                  lastNodeSelected.setSelected(false);
               }
               node.setSelected(true);
               try {
                  setDetailText(node);
               } catch (ArrayIndexOutOfBoundsException t) {
                  // throw if there is an error in the message jar
                  // (usually... )
                  final String msg =
                     String.format("Problems occurred when trying to display details for %s: (See Error Log)",
                        node.getName());
                  OseeLog.log(Activator.class, Level.SEVERE, "Error while displaying details for " + node.getName(), t);
                  openInfo("Possible Message JAR Error", msg);
               }
               lastNodeSelected = node;
            }
         }
      });
      treeViewer.getTree().addMouseListener(new MouseAdapter() {

         @Override
         public void mouseDown(MouseEvent e) {
            if (e.button == 3) {
               showContextMenu(new Point(e.x, e.y));
            }
         }
      });

      // Create menu, toolbars, filters, sorters.
      createToolBar();

      getSite().setSelectionProvider(treeViewer);

      treeViewer.addCustomizeToViewToolbar(this);
      OseeUiActions.addBugToViewToolbar(this, this, Activator.PLUGIN_ID, VIEW_ID, "Message Watch");

      createMenuActions();

      setHelpContexts();
      setNoLibraryStatus();
      IOteClientService clientService = Activator.getDefault().getOteClientService();
      if (clientService == null) {
         throw new IllegalStateException("cannot acquire ote client service");
      }
      msgServiceTracker.open(true);
      clientService.addDictionaryListener(this);
      clientService.addConnectionListener(this);
      loadWatchFile();
   }

   @Override
   public void dispose() {
      if (detailsBox != null) {
         detailsBox.dispose();
      }
      msgServiceTracker.close();
      Activator.getDefault().getOteClientService().removeConnectionListener(WatchView.this);
      Activator.getDefault().getOteClientService().removeDictionaryListener(WatchView.this);
      super.dispose();
   }

   public void createToolBar() {
      Action expandAction = new Action("Expand All") {

         @Override
         public void run() {
            treeViewer.getTree().setRedraw(false);
            treeViewer.expandAll();
            treeViewer.getTree().setRedraw(true);
         }
      };
      expandAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.EXPAND_STATE));
      expandAction.setToolTipText("Expand All");

      Action showNameAction = new Action("Show Names", SWT.TOGGLE) {

         @Override
         public void run() {
            treeViewer.refresh();
         }
      };
      showNameAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.SHOW_NAMES));
      showNameAction.setToolTipText("Show Message Names");

      Action collapseAction = new Action("Collapse All") {

         @Override
         public void run() {
            treeViewer.getTree().setRedraw(false);
            treeViewer.collapseAll();
            treeViewer.getTree().setRedraw(true);
         }
      };
      collapseAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.COLLAPSE_STATE));
      collapseAction.setToolTipText("Collapse All");

      Action deleteAction = new Action("Delete") {
         @Override
         public void run() {
            final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
            watchList.deleteSelection(selection);
         }
      };

      deleteAction.setToolTipText("Delete");

      deleteAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.DELETE));

      Action deleteAllAction = new Action("Delete All") {

         @Override
         public void run() {
            if (MessageDialog.openConfirm(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               "Delete All", "Delete All Watch Items?")) {
               watchList.deleteAll();
               saveWatchFile();
            }
         }
      };
      deleteAllAction.setToolTipText("Delete All");
      deleteAllAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.DELETE_ALL));

      Action refreshAction = new Action("Refresh") {
         @Override
         public void run() {
            treeViewer.refresh();
         }
      };
      refreshAction.setToolTipText("refresh");
      refreshAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.REFRESH));

      Action saveAction = new Action("Save Items") {
         private String saveFilePath = null;
         private String lastSaveFileName = null;

         @Override
         public void run() {
            final FileDialog dialog = new FileDialog(treeViewer.getTree().getShell(), SWT.SAVE);
            dialog.setFilterExtensions(new String[] {"*.mwi"});
            if (saveFilePath == null) {
               saveFilePath = OseeData.getPath().toOSString();
            }
            if (lastSaveFileName == null) {
               lastSaveFileName = "msgWatchItems.mwi";
            }
            dialog.setFilterPath(saveFilePath);
            dialog.setFileName(lastSaveFileName);

            String selectedFile = dialog.open();
            if (selectedFile != null) {
               if (!selectedFile.endsWith(".mwi")) {
                  selectedFile += ".mwi";
               }
               final File saveFile = new File(selectedFile);
               saveFilePath = saveFile.getAbsolutePath();
               lastSaveFileName = saveFile.getName();
               saveWatchFile(saveFile);
            }
         }
      };
      saveAction.setToolTipText("Save Watch Items");
      saveAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.SAVE));

      Action loadAction = new Action("Load Items") {
         private String loadFilePath = null;
         private String lastLoadFileName = null;

         @Override
         public void run() {
            final FileDialog dialog = new FileDialog(treeViewer.getTree().getShell(), SWT.OPEN);
            dialog.setFilterExtensions(new String[] {"*.mwi"});
            if (loadFilePath == null) {
               loadFilePath = OseeData.getPath().toOSString();
            }
            if (lastLoadFileName != null) {
               dialog.setFileName(lastLoadFileName);
            }

            dialog.setFilterPath(loadFilePath);

            String selectedFile = dialog.open();
            if (selectedFile != null) {
               if (!selectedFile.endsWith(".mwi")) {
                  selectedFile += ".mwi";
               }
               final File loadFile = new File(selectedFile);
               loadFilePath = loadFile.getAbsolutePath();
               lastLoadFileName = loadFile.getName();
               loadWatchFile(loadFile);
            }
         }
      };
      loadAction.setToolTipText("Load Watch Items");
      loadAction.setImageDescriptor(ImageManager.getImageDescriptor(OteMessageImage.OPEN));
      IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
      toolbarManager.add(showNameAction);
      toolbarManager.add(refreshAction);
      toolbarManager.add(expandAction);
      toolbarManager.add(collapseAction);
      toolbarManager.add(deleteAction);
      toolbarManager.add(deleteAllAction);
      toolbarManager.add(saveAction);
      toolbarManager.add(loadAction);
   }

   private void setHelpContexts() {
      HelpUtil.setHelp(parentComposite, "messageWatch", "org.eclipse.osee.framework.help.ui");
   }

   @Override
   public void setFocus() {
      // Set focus so that context sensitive help will work as soon as this
      // view is selected.
      parentComposite.setFocus();
   }

   /**
    * display details about specified node
    * 
    * @param node node whose details will be displayed in the detail window of the GUI
    */
   public void setDetailText(final AbstractTreeNode node) {
      detailsBox.setDetailText(node);
   }

   /**
    * shows a context menu depending on the point
    */
   void showContextMenu(Point p) {
      final Tree tree = treeViewer.getTree();
      final Menu contextMenu = getPopupMenu(tree.getParent());
      if (contextMenu != null) {
         p = tree.toDisplay(p);
         contextMenu.setLocation(p);
         contextMenu.setVisible(true);
      }
   }

   public void addWatchMessage(final AddWatchParameter parameter) {
      for (String message : parameter.getMessages()) {
         Collection<ElementPath> elements = parameter.getMessageElements(message);
         OseeLog.log(Activator.class, Level.FINEST, String.format("Watch request for message %s", message));
         try {
            if (elements == null) {
               elements = new ArrayList<ElementPath>();
            }
            watchList.createElements(message, elements);
         } catch (ClassNotFoundException ex1) {
            if (openProceedWithProcessing("Could not find a class definition for " + message + "\n Do you wish to continue")) {
               continue;
            } else {
               return;
            }
         } catch (InstantiationException ex1) {
            if (openProceedWithProcessing("failed to instantiate " + message + "\n Do you wish to continue")) {
               continue;
            } else {
               return;
            }
         } catch (Exception ex1) {
            OseeLog.log(Activator.class, Level.SEVERE, "failed to create message node", ex1);
            if (openProceedWithProcessing("Error processing " + message + ". See Error Log for details.\n Do you wish to continue")) {
               continue;
            } else {
               return;
            }
         }
      }
      treeViewer.refresh();
   }

   public void refresh() {
      treeViewer.refresh();
      saveWatchFile();
   }

   /**
    * Convienence method. Opens an info dialog
    */
   private void openInfo(final String title, final String message) {
      MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), title, message);
   }

   private boolean openProceedWithProcessing(final String message) {
      MessageDialog dialog =
         new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Proceed?", null, message,
            MessageDialog.QUESTION, new String[] {"Continue processing with next message", "End message processing"}, 0);
      return dialog.open() == Window.OK;
   }

   private void onDisconnected() {
      OseeLog.log(Activator.class, Level.INFO, "Entered onDisconnected()");
      if (!recordButton.isDisposed()) {
         recordButton.setSelection(false);
      }
      try {
         if (Benchmark.isBenchmarkingEnabled()) {
            OseeLog.log(
               Activator.class,
               Level.INFO,
               String.format("%s: # samples=%d, max=%d, min=%d, avg=%d", benchMark.getName(),
                  benchMark.getTotalSamples(), benchMark.getLongestSample(), benchMark.getShortestSample(),
                  benchMark.getAverage()));
         }
      } catch (Throwable t) {
         OseeLog.log(Activator.class, Level.WARNING, "Exception during disconnect", t);
      }

   }

   private Menu getPopupMenu(final Composite composite) {
      Menu menu = treeViewer.getMenuManager().createContextMenu(composite);
      return menu;
   }

   public void loadWatchFile() {
      loadWatchFile(watchFile);
   }

   public void loadWatchFile(final File watchFile) {
      if (watchFile != null && watchFile.exists()) {
         final Job job = new Job("Loading watch file") {

            @Override
            protected IStatus run(IProgressMonitor monitor) {
               String text = AFile.readFile(watchFile);
               final String[] msgs = text.split("\n");
               monitor.beginTask("loading watch elements", msgs.length + 10);
               try {
                  if (msgs.length > 0) {
                     if (msgs[0].equals("version=2.0")) {
                        final Map<String, ArrayList<ElementPath>> pathsToAdd =
                           new HashMap<String, ArrayList<ElementPath>>();
                        final List<ElementPath> recBodyElementsToAdd = new ArrayList<ElementPath>();
                        final List<ElementPath> recHeaderElementsToAdd = new ArrayList<ElementPath>();
                        final Set<String> recHeaderHex = new HashSet<String>();
                        final Set<String> recBodyHex = new HashSet<String>();
                        for (int i = 1; i < msgs.length; i++) {
                           if (msgs[i].startsWith("#rec#")) {
                              // #rec#,message,[body|header|bodyHex|
                              // headerHex],[boolean|path]
                              String[] els = msgs[i].split(",");
                              if (els.length == 4) {
                                 String message = els[1];
                                 String type = els[2];
                                 String value = els[3];
                                 if (type.equals("body")) {
                                    recBodyElementsToAdd.add(new ElementPath(value));
                                 } else if (type.equals("header")) {
                                    recHeaderElementsToAdd.add(new ElementPath(value));
                                 } else if (type.equals("headerHex")) {
                                    if (Boolean.parseBoolean(value)) {
                                       recHeaderHex.add(message);
                                    }
                                 } else if (type.equals("bodyHex")) {
                                    if (Boolean.parseBoolean(value)) {
                                       recBodyHex.add(message);
                                    }
                                 }
                              }
                              monitor.worked(1);
                           } else {
                              ElementPath path = new ElementPath(msgs[i]);
                              ArrayList<ElementPath> collection = pathsToAdd.get(path.getMessageName());
                              if (collection == null) {
                                 collection = new ArrayList<ElementPath>();
                                 pathsToAdd.put(path.getMessageName(), collection);
                              }
                              collection.add(path);
                              monitor.worked(1);
                           }
                        }
                        Displays.pendInDisplayThread(new Runnable() {
                           @Override
                           public void run() {
                              try {
                                 treeViewer.getTree().setRedraw(false);
                                 addWatchMessage(new AddWatchParameter(pathsToAdd));
                                 for (ElementPath path : recBodyElementsToAdd) {
                                    WatchedMessageNode msgNode = watchList.getMessageNode(path.getMessageName());
                                    if (msgNode != null) {
                                       msgNode.getRecordingState().addBody(path);
                                    }
                                 }
                                 for (ElementPath path : recHeaderElementsToAdd) {
                                    WatchedMessageNode msgNode = watchList.getMessageNode(path.getMessageName());
                                    if (msgNode != null) {
                                       msgNode.getRecordingState().addHeader(path);
                                    }
                                 }
                                 for (String msg : recBodyHex) {
                                    WatchedMessageNode msgNode = watchList.getMessageNode(msg);
                                    if (msgNode != null) {
                                       msgNode.getRecordingState().setBodyDump(true);
                                    }
                                 }
                                 for (String msg : recHeaderHex) {
                                    WatchedMessageNode msgNode = watchList.getMessageNode(msg);
                                    if (msgNode != null) {
                                       msgNode.getRecordingState().setHeaderDump(true);
                                    }
                                 }
                              } finally {
                                 treeViewer.getTree().setRedraw(true);
                                 treeViewer.refresh();
                              }
                           }
                        });

                        Displays.pendInDisplayThread(new Runnable() {
                           @Override
                           public void run() {
                              saveWatchFile();
                              treeViewer.refresh();
                           }
                        });
                        monitor.worked(10);
                     } else {

                        for (String msg : msgs) {
                           // final Matcher jarMatch =
                           // jarPattern.matcher(msg);
                           final Matcher elmMatch = elmPattern.matcher(msg);
                           final Matcher msgMatch = msgPattern.matcher(msg);

                           if (elmMatch.find()) {
                              Displays.pendInDisplayThread(new Runnable() {
                                 @Override
                                 public void run() {
                                    String msg = elmMatch.group(1);
                                    String elm = elmMatch.group(2);
                                    ElementPath element = new ElementPath(msg, elm);
                                    AddWatchParameter parameter = new AddWatchParameter(elmMatch.group(1), element);
                                    addWatchMessage(parameter);
                                 }
                              });
                           } else if (msgMatch.find()) {
                              Displays.pendInDisplayThread(new Runnable() {
                                 @Override
                                 public void run() {
                                    addWatchMessage(new AddWatchParameter(msgMatch.group(1)));
                                 }
                              });
                           }
                           monitor.worked(1);
                        }
                        Displays.pendInDisplayThread(new Runnable() {
                           @Override
                           public void run() {
                              saveWatchFile();
                              treeViewer.refresh();
                           }
                        });
                        monitor.worked(10);

                     }
                  }
               } catch (Throwable t) {
                  OseeLog.log(Activator.class, Level.SEVERE, "error loading watch file", t);
                  return org.eclipse.core.runtime.Status.CANCEL_STATUS;
               } finally {
                  monitor.done();
               }
               return org.eclipse.core.runtime.Status.OK_STATUS;
            }
         };
         Jobs.startJob(job);
      }
   }

   public void saveWatchFile() {
      saveWatchFile(watchFile);
   }

   public void saveWatchFile(File watchFile) {
      if (watchFile == null) {
         return;
      }
      try {
         final FileWriter fw = new FileWriter(watchFile);
         fw.write("version=2.0\n");
         for (MessageNode treeItem : watchList.getMessages()) {
            WatchedMessageNode msg = (WatchedMessageNode) treeItem;
            msg.getRecordingState().write(fw);
            LinkedList<ElementNode> descendants = new LinkedList<ElementNode>();
            msg.collectDescendants(descendants);
            if (descendants.isEmpty()) {
               fw.write(msg.getMessageClassName());
               fw.write('\n');
            } else {
               for (ElementNode el : descendants) {
                  fw.write(el.getElementPath().asString());
                  fw.write('\n');
               }
            }
         }
         fw.close();
      } catch (Exception e) {
         OseeLog.log(Activator.class, Level.SEVERE, "failed to write watch file at " + watchFile.getAbsolutePath(), e);
      }
   }

   public TreeViewer getTreeViewer() {
      return treeViewer;
   }

   @Override
   public String getActionDescription() {
      return "";
   }

   public void updateMenuActions(final IMenuManager mm) {

      final IStructuredSelection selection = (IStructuredSelection) treeViewer.getSelection();
      if (selection.size() > 0) {
         final AbstractTreeNode node = (AbstractTreeNode) selection.getFirstElement();
         node.visit(new INodeVisitor<Object>() {

            @Override
            public Object elementNode(ElementNode node) {
               if (selection.size() == 1) {
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new SetValueAction(node));
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new ZeroizeElementAction(node));
               }
               return null;
            }

            @Override
            public Object messageNode(MessageNode node) {
               if (selection.size() == 1) {
                  WatchedMessageNode msgNode = (WatchedMessageNode) node;
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new WatchElementAction(WatchView.this,
                     (WatchedMessageNode) node));
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, SetDataSourceMenu.createMenu(msgNode));
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, SetMessageModeMenu.createMenu(msgNode));
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new SendMessageAction(msgNode));
                  mm.insertBefore(XViewer.MENU_GROUP_PRE, new ZeroizeMessageAction(msgNode));
               }
               return null;
            }

            @Override
            public Object rootNode(RootNode node) {
               return null;
            }

         });
         mm.insertBefore(XViewer.MENU_GROUP_PRE, new ClearUpdatesAction(watchList, selection));
         mm.insertBefore(XViewer.MENU_GROUP_PRE, new DeleteSelectionAction(watchList, selection));
      }
      mm.insertBefore(XViewer.MENU_GROUP_PRE, new Separator());
   }

   public void createMenuActions() {
      MenuManager mm = treeViewer.getMenuManager();
      mm.createContextMenu(treeViewer.getControl());
      mm.addMenuListener(new IMenuListener() {
         @Override
         public void menuAboutToShow(IMenuManager manager) {
            updateMenuActions(manager);
         }
      });

   }

   @Override
   public void onPostConnect(final ConnectionEvent event) {
      OseeLog.log(Activator.class, Level.INFO, "Entered onConnectionEstablished()");

      if (event.getEnvironment() instanceof ITestEnvironmentMessageSystem) {
         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {
               recordButton.setEnabled(true);
               watchList.clearAllUpdateCounters();
            }
         });
      }
   }

   @Override
   public void onPreDisconnect(ConnectionEvent event) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            recordButton.setEnabled(false);
            onDisconnected();
         }
      });
   }

   @Override
   public void onDictionaryLoaded(final IMessageDictionary dictionary) {

      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               statusTxt.setText("loaded with " + dictionary.getMessageLibraryVersion());
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, "exception while processing library", ex);
            }
         }
      });
   }

   @Override
   public void onDictionaryUnloaded(IMessageDictionary dictionary) {
      // our node factory's dictionary is being unloaded
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            setNoLibraryStatus();
         }
      });
   }

   private void setNoLibraryStatus() {
      statusTxt.setText("no library detected");
   }

   @Override
   public void onConnectionLost(IServiceConnector connector) {
   }

   @Override
   public void oteMessageServiceAcquired(final IOteMessageService service) {
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            messageService = service;
            recordButton.setEnabled(true);
            treeViewer.setInput(service);
         }
      });
   }

   @Override
   public void oteMessageServiceReleased() {
      Displays.pendInDisplayThread(new Runnable() {
         @Override
         public void run() {
            if (!recordButton.isDisposed()) {
               recordButton.setEnabled(false);
            }
            if (!treeViewer.getControl().isDisposed()) {
               treeViewer.setInput(null);
            }
            messageService = null;
         }
      });
   }
}