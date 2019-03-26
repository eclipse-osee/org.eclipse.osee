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
package org.eclipse.osee.framework.ui.skynet.menu;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.PurgeArtifacts;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * Provided so all OSEE context menus (and programatic manipulations) can share the same menu items, dialogs,
 * permissions checks and code.
 *
 * @author Donald G. Dunne
 */
public class GlobalMenu {

   private final IGlobalMenuHelper globalMenuHelper;

   private MenuItem deleteMenuItem;
   private MenuItem purgeMenuItem;
   public static enum GlobalMenuItem {
      DeleteArtifacts,
      PurgeArtifacts;

      public static final Collection<GlobalMenuItem> ALL = Arrays.asList(GlobalMenuItem.values());
   };
   private final ArrayList<GlobalMenuListener> listeners = new ArrayList<>();

   public GlobalMenu(Menu parentMenu, IGlobalMenuHelper globalMenuHelper) {
      this.globalMenuHelper = globalMenuHelper;
      if (parentMenu != null) {
         parentMenu.addMenuListener(new EnablementMenuListener());
         if (globalMenuHelper.getValidMenuItems().contains(GlobalMenuItem.DeleteArtifacts)) {
            createDeleteMenuItem(parentMenu);
         }
         if (globalMenuHelper.getValidMenuItems().contains(GlobalMenuItem.PurgeArtifacts)) {
            createPurgeMenuItem(parentMenu);
         }
      }
   }

   public GlobalMenu(IGlobalMenuHelper globalMenuHelper) {
      this(null, globalMenuHelper);
   }

   public void addGlobalMenuListener(GlobalMenuListener listener) {
      listeners.add(listener);
   }

   public void removeGlobalMenuListener(GlobalMenuListener listener) {
      listeners.remove(listener);
   }

   /**
    * @author Donald G. Dunne
    */
   public class EnablementMenuListener implements MenuListener {

      @Override
      public void menuHidden(MenuEvent e) {
         // do nothing
      }

      @Override
      public void menuShown(MenuEvent e) {
         try {
            GlobalMenuPermissions permiss = new GlobalMenuPermissions(globalMenuHelper);
            Collection<Artifact> artifacts = globalMenuHelper.getArtifacts();
            if (deleteMenuItem != null) {
               deleteMenuItem.setEnabled(!artifacts.isEmpty() && permiss.isWritePermission());
            }
            if (purgeMenuItem != null) {
               purgeMenuItem.setEnabled(
                  !artifacts.isEmpty() && permiss.isHasArtifacts() && permiss.isWritePermission() && AccessControlManager.isOseeAdmin());
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }

   }

   // Provided for addition to Menus
   private void createDeleteMenuItem(Menu parentMenu) {
      deleteMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      deleteMenuItem.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE));
      deleteMenuItem.setText(deleteArtifactAction.getText());
      deleteMenuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            deleteArtifactAction.run();
         };
      });
   }

   private final Action deleteArtifactAction = new Action("&Delete Artifact\tDelete", Action.AS_PUSH_BUTTON) {
      @Override
      public void run() {
         try {
            final Collection<Artifact> artifactsToBeDeleted = globalMenuHelper.getArtifacts();
            MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), "Confirm Artifact Deletion", null,
               " Are you sure you want to delete this artifact and all of the default hierarchy children?",
               MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
            if (dialog.open() == 0) {
               try {
                  for (GlobalMenuListener listener : listeners) {
                     Result result = listener.actioning(GlobalMenuItem.DeleteArtifacts, artifactsToBeDeleted);
                     if (result.isFalse()) {
                        AWorkbench.popup(result);
                        return;
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }

               Artifact[] artifactsArray = artifactsToBeDeleted.toArray(new Artifact[artifactsToBeDeleted.size()]);
               SkynetTransaction transaction =
                  TransactionManager.createTransaction(artifactsArray[0].getBranch(), "Delete artifact action");
               ArtifactPersistenceManager.deleteArtifact(transaction, false, artifactsArray);
               transaction.execute();

               try {
                  for (GlobalMenuListener listener : listeners) {
                     listener.actioned(GlobalMenuItem.DeleteArtifacts, artifactsToBeDeleted);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
      }
   };

   private final Action purgeArtifactAction = new Action("&Purge Artifact(s)", Action.AS_PUSH_BUTTON) {
      @Override
      public void run() {
         purgeArtifactsMethod(globalMenuHelper.getArtifacts(), listeners);
      }

   };

   public static void purgeArtifactsMethod(Collection<Artifact> artifactsToBePurged, Collection<GlobalMenuListener> listeners) {

      final MessageDialogWithToggle dialog = MessageDialogWithToggle.openOkCancelConfirm(
         PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Confirm Artifact Purge ",
         " Are you sure you want to purge this artifact and all history associated from the database? (cannot be undone)",
         "Purge selected artifact's children?", false, null, null);

      if (dialog.getReturnCode() == Window.OK) {
         final boolean recusivePurge =
            MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               "Recursive Purge", "Recurse and purge from child branches?");
         Job job = new Job("Purge artifact") {

            @Override
            protected IStatus run(final IProgressMonitor monitor) {
               IStatus toReturn = Status.CANCEL_STATUS;

               // Notify and confirm that menus should be actioned
               try {
                  for (GlobalMenuListener listener : listeners) {
                     Result result = listener.actioning(GlobalMenuItem.PurgeArtifacts, artifactsToBePurged);
                     if (result.isFalse()) {
                        return new Status(IStatus.ERROR, Activator.PLUGIN_ID, IStatus.OK, result.getText(), null);
                     }
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
               monitor.beginTask("Purge artifact", artifactsToBePurged.size());

               try {
                  boolean recurseChildren = dialog.getToggleState();
                  Collection<Artifact> toPurge = new LinkedHashSet<>();
                  for (Artifact artifactToPurge : artifactsToBePurged) {
                     if (!artifactToPurge.isDeleted()) {
                        toPurge.add(artifactToPurge);
                        if (recurseChildren) {
                           toPurge.addAll(artifactToPurge.getDescendants());
                        }
                     }
                  }
                  monitor.setTaskName("Purging " + toPurge.size() + " artifacts");
                  Operations.executeWorkAndCheckStatus(new PurgeArtifacts(toPurge, recusivePurge));
                  monitor.worked(toPurge.size());
                  toReturn = Status.OK_STATUS;
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
                  toReturn = new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1, ex.getMessage(), ex);
               } finally {
                  monitor.done();
               }

               // Notify Listeners that menu was actioned
               try {
                  for (GlobalMenuListener listener : listeners) {
                     listener.actioned(GlobalMenuItem.PurgeArtifacts, artifactsToBePurged);
                  }
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }

               return toReturn;
            }
         };

         Jobs.startJob(job);
      }
   }

   private void createPurgeMenuItem(Menu parentMenu) {
      purgeMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      purgeMenuItem.setImage(ImageManager.getImage(FrameworkImage.TRASH));
      purgeMenuItem.setText(purgeArtifactAction.getText());
      purgeMenuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            purgeArtifactAction.run();
         };
      });
   }

   /**
    * This method is provided for programatic access to delete artifact. No permissions are checked. Preferred use is by
    * adding item as menu item.
    *
    * @return the deleteArtifactAction
    */
   public Action getDeleteArtifactAction() {
      return deleteArtifactAction;
   }

   /**
    * @return the listeners
    */
   public ArrayList<GlobalMenuListener> getGlobalMenuListeners() {
      return listeners;
   }
}