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
package org.eclipse.osee.framework.ui.service.control.wizards.launcher.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.jini.JiniPlugin;
import net.jini.core.lookup.ServiceRegistrar;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jini.discovery.EclipseJiniClassloader;
import org.eclipse.osee.framework.jini.discovery.IRegistrarListener;
import org.eclipse.osee.framework.ui.service.control.managers.ReggieCache;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class JiniGroupSelector implements IRegistrarListener {

   private Set<String> availableGroups;
   private String[] jiniGroup;
   private ComboViewer comboViewer;
   private ReggieCache reggieCache;

   public JiniGroupSelector() {
      this.comboViewer = null;
      this.availableGroups = new TreeSet<String>();
      this.jiniGroup = OseeProperties.getInstance().getOseeJiniServiceGroups();
      if (jiniGroup == null || jiniGroup.length < 1) {
         jiniGroup = new String[1];
      }
      this.reggieCache = ReggieCache.getEclipseInstance(EclipseJiniClassloader.getInstance());
   }

   public void createJiniGroupWidget(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Register Service with the following Jini Group: ");

      comboViewer = new ComboViewer(group, SWT.BORDER | SWT.SINGLE);
      comboViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      comboViewer.getCombo().addModifyListener(new ModifyListener() {

         public void modifyText(ModifyEvent e) {
            jiniGroup[0] = comboViewer.getCombo().getText();
         }
      });

      comboViewer.addSelectionChangedListener(new ISelectionChangedListener() {

         public void selectionChanged(SelectionChangedEvent event) {
            IStructuredSelection selection = (IStructuredSelection) comboViewer.getSelection();
            Object obj = selection.getFirstElement();
            if (null != obj) {
               String value = (String) obj;
               jiniGroup[0] = value;
            }
         }
      });
      this.reggieCache.addListener(this);
   }

   private void populateGroups() {
      PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {

         public void run() {
            availableGroups.clear();
            availableGroups.addAll(reggieCache.getAvailableJiniGroups());
            if (comboViewer != null && true != comboViewer.getCombo().isDisposed()) {
               List<String> list = new ArrayList<String>(availableGroups);

               String temp = jiniGroup[0];
               comboViewer.getCombo().removeAll();
               comboViewer.add(list.toArray());

               jiniGroup[0] = temp;
               if (true != Strings.isValid(jiniGroup[0])) {
                  jiniGroup = JiniPlugin.getInstance().getJiniVersion();

                  int result = list.indexOf(jiniGroup[0]);
                  if (result < 0) {
                     for (int index = 0; index < list.size(); index++) {
                        String value = list.get(index);
                        if (false != value.contains(jiniGroup[0])) {
                           jiniGroup[0] = value;
                           break;
                        }
                     }
                  }
               }

               comboViewer.getCombo().select(list.indexOf(jiniGroup[0]));
            }
         }
      });
   }

   public String getJiniGroupVmArg() {
      return StringFormat.commaSeparate(jiniGroup);
   }

   public void dispose() {
      if (comboViewer != null && false != comboViewer.getCombo().isDisposed()) {
         comboViewer.getCombo().dispose();
      }
      this.reggieCache.removeListener(this);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jini.discovery.IRegistrarListener#reggieAdded(java.util.List)
    */
   public void reggieAdded(List<ServiceRegistrar> serviceRegistrars) {
      populateGroups();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jini.discovery.IRegistrarListener#reggieChanged(java.util.List)
    */
   public void reggieChanged(List<ServiceRegistrar> serviceRegistrars) {
      populateGroups();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.jini.discovery.IRegistrarListener#reggieRemoved(java.util.List)
    */
   public void reggieRemoved(List<ServiceRegistrar> serviceRegistrars) {
      populateGroups();
   }
}
