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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.ui.plugin.util.Jobs;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.TagArtifactsJob;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
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
   private MenuItem tagMenuItem;
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(GlobalMenu.class);
   public static enum GlobalMenuItem {
      DeleteArtifacts, PurgeArtifacts, TagArtifacts;

      public static List<GlobalMenuItem> ALL = Arrays.asList(GlobalMenuItem.values());
   };
   private ArrayList<GlobalMenuListener> listeners = new ArrayList<GlobalMenuListener>();

   public GlobalMenu(Menu parentMenu, IGlobalMenuHelper globalMenuHelper) {
      this.globalMenuHelper = globalMenuHelper;
      if (parentMenu != null) {
         parentMenu.addMenuListener(new EnablementMenuListener());
         if (globalMenuHelper.getValidMenuItems().contains(GlobalMenuItem.DeleteArtifacts)) createDeleteMenuItem(parentMenu);
         if (globalMenuHelper.getValidMenuItems().contains(GlobalMenuItem.PurgeArtifacts)) createPurgeMenuItem(parentMenu);
         if (globalMenuHelper.getValidMenuItems().contains(GlobalMenuItem.TagArtifacts)) createTagMenuItem(parentMenu);
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

      public void menuHidden(MenuEvent e) {
      }

      public void menuShown(MenuEvent e) {
         GlobalMenuPermissions permiss = new GlobalMenuPermissions(globalMenuHelper);

         if (deleteMenuItem != null) deleteMenuItem.setEnabled(permiss.isFullAccess());
         if (purgeMenuItem != null) purgeMenuItem.setEnabled(permiss.isHasArtifacts() && OseeProperties.getInstance().isDeveloper());
         if (tagMenuItem != null) tagMenuItem.setEnabled(permiss.isHasArtifacts() && permiss.isFullAccess());
      }
   }

   private void createTagMenuItem(Menu parentMenu) {
      tagMenuItem = new MenuItem(parentMenu, SWT.CASCADE);
      tagMenuItem.setText("&Tag Artifact(s)");

      tagMenuItem.addSelectionListener(new SelectionAdapter() {
         @Override
         public void widgetSelected(SelectionEvent e) {
            tagArtifactsAction.run();
         }
      });
   }

   private Action tagArtifactsAction = new Action("&Tag Artifact(s)", Action.AS_PUSH_BUTTON) {
      @Override
      public void run() {
         Jobs.startJob(new TagArtifactsJob(globalMenuHelper.getArtifacts()));
      }
   };

   // Provided for addition to Menus
   private void createDeleteMenuItem(Menu parentMenu) {
      deleteMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      deleteMenuItem.setText(deleteArtifactAction.getText());
      deleteMenuItem.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            deleteArtifactAction.run();
         };
      });
   }

   private Action deleteArtifactAction = new Action("&Delete Artifact\tDelete", Action.AS_PUSH_BUTTON) {
      @Override
      public void run() {
         try {
            final Collection<Artifact> artifactsToBeDeleted = globalMenuHelper.getArtifacts();
            MessageDialog dialog =
                  new MessageDialog(Display.getCurrent().getActiveShell(), "Confirm Artifact Deletion", null,
                        " Are you sure you want to delete this artifact and all of the default hierarchy children?",
                        MessageDialog.QUESTION, new String[] {IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL}, 1);
            if (dialog.open() == 0) {
               try {
                  for (GlobalMenuListener listener : listeners) {
                     Result result = listener.actioning(GlobalMenuItem.DeleteArtifacts, artifactsToBeDeleted);
                     if (result.isFalse()) {
                        result.popup();
                        return;
                     }
                  }
               } catch (Exception ex) {
                  OSEELog.logException(SkynetGuiPlugin.class, ex, false);
               }
               ArtifactPersistenceManager.getInstance().deleteArtifact(
                     artifactsToBeDeleted.toArray(Artifact.EMPTY_ARRAY));
               try {
                  for (GlobalMenuListener listener : listeners) {
                     listener.actioned(GlobalMenuItem.DeleteArtifacts, artifactsToBeDeleted);
                  }
               } catch (Exception ex) {
                  OSEELog.logException(SkynetGuiPlugin.class, ex, false);
               }
            }
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
   };

   private Action purgeArtifactAction = new Action("&Purge Artifact(s)", Action.AS_PUSH_BUTTON) {
      @Override
      public void run() {
         final Collection<Artifact> artifactsToBePurged = globalMenuHelper.getArtifacts();

         if (MessageDialog.openConfirm(
               PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
               "Confirm Artifact Purge ",
               " Are you sure you want to purge this artifact, all of " + "its children and all history associated with these artifacts from the database ?")) {
            Job job = new Job("Purge artifact") {

               @Override
               protected IStatus run(IProgressMonitor monitor) {
                  IStatus toReturn = Status.CANCEL_STATUS;

                  // Notify and confirm that menus should be actioned
                  try {
                     for (GlobalMenuListener listener : listeners) {
                        Result result = listener.actioning(GlobalMenuItem.PurgeArtifacts, artifactsToBePurged);
                        if (result.isFalse()) {
                           return new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, Status.OK, result.getText(), null);
                        }
                     }
                  } catch (Exception ex) {
                     OSEELog.logException(SkynetGuiPlugin.class, ex, false);
                  }
                  monitor.beginTask("Purge artifact", artifactsToBePurged.size());
                  final IProgressMonitor fMonitor = monitor;

                  AbstractSkynetTxTemplate purgeTx =
                        new AbstractSkynetTxTemplate(artifactsToBePurged.iterator().next().getBranch()) {
                           @Override
                           protected void handleTxWork() throws Exception {
                              for (Artifact artifactToPurge : artifactsToBePurged) {
                                 if (!artifactToPurge.isDeleted()) {
                                    fMonitor.setTaskName("Purge: " + artifactToPurge.getDescriptiveName());
                                    artifactToPurge.purge();
                                 }
                                 fMonitor.worked(1);
                              }
                              fMonitor.done();
                           }
                        };

                  // Perform the purge transaction
                  try {
                     purgeTx.execute();
                     toReturn = Status.OK_STATUS;
                  } catch (Exception ex) {
                     logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
                     toReturn = new Status(Status.ERROR, SkynetActivator.PLUGIN_ID, -1, ex.getMessage(), ex);
                  } finally {
                     monitor.done();
                  }

                  // Notify Listeners that menu was actioned
                  try {
                     for (GlobalMenuListener listener : listeners) {
                        listener.actioned(GlobalMenuItem.PurgeArtifacts, artifactsToBePurged);
                     }
                  } catch (Exception ex) {
                     OSEELog.logException(SkynetGuiPlugin.class, ex, false);
                  }

                  return toReturn;
               }
            };

            Jobs.startJob(job);
         }
      }
   };

   private void createPurgeMenuItem(Menu parentMenu) {
      purgeMenuItem = new MenuItem(parentMenu, SWT.PUSH);
      purgeMenuItem.setText(purgeArtifactAction.getText());
      purgeMenuItem.addSelectionListener(new SelectionAdapter() {
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
