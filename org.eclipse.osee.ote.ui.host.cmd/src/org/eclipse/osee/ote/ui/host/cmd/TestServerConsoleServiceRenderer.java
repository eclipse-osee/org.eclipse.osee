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
package org.eclipse.osee.ote.ui.host.cmd;

import java.util.Hashtable;
import java.util.logging.Level;
import net.jini.core.lookup.ServiceItem;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.jdk.core.type.InputManager;
import org.eclipse.osee.framework.jdk.core.type.TreeParent;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.service.control.renderer.IServiceRenderer;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.osee.ote.core.environment.interfaces.IHostTestEnvironment;
import org.eclipse.osee.ote.core.environment.interfaces.IRemoteCommandConsole;
import org.eclipse.osee.ote.core.environment.interfaces.ITestEnvironment;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

/**
 * Created on Oct 9, 2006
 */
public class TestServerConsoleServiceRenderer implements IServiceRenderer {
   private IHostTestEnvironment testService;
   private Group composite;
   private Hashtable<ITestEnvironment, IRemoteCommandConsole> consoles;
   private volatile IRemoteCommandConsole selectedConsole;
   private Text outputTxt;
   private Text inputTxt;
   private Button sendCmdBtn;
   private EnvironmentViewer envViewer;
   private InputManager<TreeParent> inputManager;

   public TestServerConsoleServiceRenderer() {
      this.consoles = new Hashtable<ITestEnvironment, IRemoteCommandConsole>(24);
      this.selectedConsole = null;
      this.inputManager = new InputManager<TreeParent>();
   }

   public void refresh() {
      Display.getCurrent().asyncExec(new Runnable() {

         public void run() {
            if (composite != null && !composite.isDisposed() && testService != null) {
               cleanupService();
               try {
                  ITestEnvironment[] envs = testService.getRemoteEnvironments();
                  consoles.clear();
                  if (envs.length > 0) {
                     for (ITestEnvironment env : envs) {
                        consoles.put(env, env.getCommandConsole());
                     }
                     selectedConsole = consoles.get(envs[0]);
                  } else {
                     selectedConsole = null;
                  }

                  envViewer.setInput(inputManager.getInputList());
                  TreeBuilder.buildTree(inputManager, testService, consoles);
                  envViewer.refresh();

               } catch (Throwable t) {
                  OseeLog.log(UiPlugin.class, Level.SEVERE, "exception getting hosts", t);
               }
            }
         }
      });
   }

   public void setService(ServiceItem serviceItem) {
      OseeLog.log(UiPlugin.class, Level.INFO, "setting test environment service");
      testService = (IHostTestEnvironment) serviceItem.service;
      if (outputTxt != null && !outputTxt.isDisposed()) {
         outputTxt.setText("");
      }
   }

   public void disconnect() {
      consoles.clear();
   }

   public void dispose() {
      cleanupService();
      Widgets.disposeWidgets(composite, outputTxt, inputTxt, sendCmdBtn);
   }

   public Control renderInComposite(Composite parent) {
      composite = new Group(parent, SWT.NONE);
      composite.setLayout(new GridLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      composite.setText("Test Server Console");

      SashForm sashForm = new SashForm(composite, SWT.NONE);
      sashForm.setLayout(new GridLayout());
      sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      sashForm.setOrientation(SWT.VERTICAL);
      sashForm.SASH_WIDTH = 3;

      envViewer = new EnvironmentViewer(sashForm, SWT.NONE);
      envViewer.getViewer().addDoubleClickListener(new IDoubleClickListener() {

         public void doubleClick(DoubleClickEvent event) {
            Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
            if (element != null && element instanceof ConsoleNode) {
               selectedConsole = ((ConsoleNode) element).getConsole();
               outputTxt.setText("");
            }
         }

      });
      createConsoleArea(sashForm);

      sashForm.setWeights(new int[] {3, 7});
      return composite;
   }

   private void createConsoleArea(Composite parent) {
      Group composite = new Group(parent, SWT.NONE);
      Widgets.setFormLayout(composite, 5, 5);
      composite.setText("Interact");

      outputTxt =
            Widgets.createTxt(composite, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.READ_ONLY | SWT.BORDER, "");
      outputTxt.setTabs(4);
      outputTxt.setBackground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLACK));
      outputTxt.setForeground(PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_GREEN));
      outputTxt.setFont(new Font(Display.getDefault(), "Courier", 10, SWT.NORMAL));
      Widgets.attachToParent(outputTxt, SWT.TOP, 0, 5);
      Widgets.attachToParent(outputTxt, SWT.BOTTOM, 75, 0);
      Widgets.attachToParent(outputTxt, SWT.LEFT, 0, 5);
      Widgets.attachToParent(outputTxt, SWT.RIGHT, 100, -5);

      inputTxt = Widgets.createTxt(composite, SWT.SINGLE | SWT.BORDER, "");
      inputTxt.addTraverseListener(new TraverseListener() {

         public void keyTraversed(TraverseEvent event) {
            if (event.detail == SWT.TRAVERSE_RETURN) {
               sendCmd(inputTxt.getText());
            }
         }

      });
      Widgets.attachToControl(inputTxt, outputTxt, SWT.LEFT, SWT.LEFT, 0);
      sendCmdBtn = Widgets.createBtn(composite, SWT.PUSH, "Execute");
      sendCmdBtn.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent arg0) {
            sendCmd(inputTxt.getText());
         }

      });
      Widgets.attachToControl(sendCmdBtn, outputTxt, SWT.TOP, SWT.BOTTOM, 5);
      Widgets.attachToControl(sendCmdBtn, outputTxt, SWT.RIGHT, SWT.RIGHT, 0);
      Widgets.attachToControl(inputTxt, sendCmdBtn, SWT.RIGHT, SWT.LEFT, -5);
      Widgets.attachToControl(inputTxt, sendCmdBtn, SWT.TOP, SWT.CENTER, 0);
   }

   private void sendCmd(String cmd) {
      try {
         if (selectedConsole != null) {
            outputTxt.append(selectedConsole.doCommand(cmd));
            inputTxt.setText("");
         } else {
            outputTxt.append("No Consoles Available\n");
         }
      } catch (Throwable t) {
         OseeLog.log(UiPlugin.class, Level.SEVERE, "Exception trying to execute test service console command " + cmd, t);
         outputTxt.append("Exception ocurred when executing command\n");

      }
   }

   private void cleanupService() {
      consoles.clear();
      inputManager.removeAll();
   }
}
