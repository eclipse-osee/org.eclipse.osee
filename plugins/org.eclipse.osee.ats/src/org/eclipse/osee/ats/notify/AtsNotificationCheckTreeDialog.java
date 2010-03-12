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
package org.eclipse.osee.ats.notify;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.CheckedTreeSelectionDialog;

/**
 * @author Donald G. Dunne
 */
public class AtsNotificationCheckTreeDialog extends CheckedTreeSelectionDialog {
   private static Object[] EMPTY_ARRAY = new Object[0];
   private Button sendNotificationsButton;
   private boolean sendNotifications;

   public AtsNotificationCheckTreeDialog() {
      super(Display.getCurrent().getActiveShell(), labelProvider, treeContentProvider);
      setTitle("Select ATS Notifications");
      setMessage("Select Desired ATS Notifications");
      try {
         setInput(AtsNotification.getInstance().getAtsNotifications());
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public Collection<IAtsNotification> getSelectedAtsNotifications() {
      ArrayList<IAtsNotification> notifications = new ArrayList<IAtsNotification>();
      for (Object obj : getResult())
         notifications.add((IAtsNotification) obj);
      return notifications;
   }

   @Override
   protected Control createDialogArea(Composite container) {
      Control c = super.createDialogArea(container);
      getTreeViewer().setSorter(new ViewerSorter() {
         @SuppressWarnings("unchecked")
         @Override
         public int compare(Viewer viewer, Object e1, Object e2) {
            return getComparator().compare(((IAtsNotification) e1).getNotificationName(),
                  ((IAtsNotification) e2).getNotificationName());
         }
      });

      sendNotificationsButton = new Button(container, SWT.CHECK);
      sendNotificationsButton.setText("Send Notifications (Otherwise, only report will display)");
      sendNotificationsButton.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            sendNotifications = sendNotificationsButton.getSelection();
         }
      });
      return c;
   }

   public void setArtifacts(Collection<? extends Artifact> artifacts) {
      setInput(artifacts);
   }

   static ILabelProvider labelProvider = new ILabelProvider() {

      public Image getImage(Object element) {
         return null;
      }

      public String getText(Object element) {
         if (element instanceof IAtsNotification) return ((IAtsNotification) element).getNotificationName();
         return "Unknown";
      }

      public void addListener(ILabelProviderListener listener) {
      }

      public void dispose() {
      }

      public boolean isLabelProperty(Object element, String property) {
         return false;
      }

      public void removeListener(ILabelProviderListener listener) {
      }

   };
   static ITreeContentProvider treeContentProvider = new ITreeContentProvider() {
      @SuppressWarnings("unchecked")
      public Object[] getElements(Object inputElement) {
         if (inputElement instanceof Collection) {
            return ((Collection) inputElement).toArray();
         }
         return EMPTY_ARRAY;
      };

      @SuppressWarnings("unchecked")
      public Object[] getChildren(Object parentElement) {
         if (parentElement instanceof Collection) {
            return ((Collection) parentElement).toArray();
         }
         return EMPTY_ARRAY;
      };

      public boolean hasChildren(Object element) {
         return getChildren(element).length > 0;
      }

      public Object getParent(Object element) {
         return null;
      }

      public void dispose() {
      }

      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      };
   };

   /**
    * @return the sendNotifications
    */
   public boolean isSendNotifications() {
      return sendNotifications;
   }
}
