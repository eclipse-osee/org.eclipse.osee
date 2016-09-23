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

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osee.framework.core.data.RelationSorter;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationTypeSideSorter;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ArmEvent;
import org.eclipse.swt.events.ArmListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * @author Roberto E. Escobar
 */
public class RelationOrderContributionItem extends ContributionItem {

   public static final String ID = Activator.PLUGIN_ID + ".RelationOrder";
   private static final String MENU_TEXT = "&Order Relations";

   public static interface SelectionListener {
      public void onSelected(final RelationTypeSideSorter sorter, final RelationSorter wasId, final RelationSorter isId);
   }

   private final ISelectionProvider selectionProvider;
   private final Collection<SelectionListener> listeners = new CopyOnWriteArrayList<>();

   public RelationOrderContributionItem(ISelectionProvider selectionProvider) {
      super("relation.order.contribution.item");
      this.selectionProvider = selectionProvider;
   }

   @Override
   public void fill(Menu menu, int index) {
      final MenuItem relationOrderMenuItem = new MenuItem(menu, SWT.CASCADE);
      relationOrderMenuItem.setText(MENU_TEXT);

      final Menu subMenu = new Menu(menu);
      relationOrderMenuItem.setMenu(subMenu);

      List<RelationSorter> orderTypes = RelationManager.getRelationOrderTypes();
      for (RelationSorter id : orderTypes) {
         createMenuItem(subMenu, id);
      }

      menu.addListener(SWT.Show, new Listener() {

         @Override
         public void handleEvent(Event event) {
            RelationTypeSideSorter sorter = getSelected();
            boolean isEnabled = false;
            if (sorter != null) {
               try {
                  Artifact artifact = sorter.getArtifact();
                  isEnabled = !artifact.isReadOnly();
               } catch (OseeCoreException ex) {
                  // Do Nothing;
               }
            }
            relationOrderMenuItem.setEnabled(isEnabled);
         }
      });

      relationOrderMenuItem.addArmListener(new ArmListener() {

         @Override
         public void widgetArmed(ArmEvent e) {
            RelationSorter sorterId = getSelectedSorterId();
            if (sorterId != null) {
               String orderGuid = sorterId.getGuid();
               for (MenuItem item : subMenu.getItems()) {
                  Object data = item.getData();
                  if (data instanceof String) {
                     String itemGuid = (String) data;
                     boolean matches = orderGuid.equals(itemGuid);
                     item.setSelection(matches);
                  }
               }
            }
         }
      });
   }

   public boolean addListener(SelectionListener listener) {
      boolean result = false;
      if (listener != null) {
         result = listeners.add(listener);
      }
      return result;
   }

   public boolean removeListener(SelectionListener listener) {
      boolean result = false;
      if (listener != null) {
         result = listeners.remove(listener);
      }
      return result;
   }

   private void notifyListeners(final RelationTypeSideSorter sorter, final RelationSorter wasId, final RelationSorter isId) {
      for (SelectionListener listener : listeners) {
         try {
            listener.onSelected(sorter, wasId, isId);
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
   }

   @Override
   public boolean isDynamic() {
      return true;
   }

   private RelationSorter getSelectedSorterId() {
      RelationSorter sorterId = null;
      RelationTypeSideSorter sorter = getSelected();
      if (sorter != null) {
         try {
            sorterId = sorter.getSorterId();
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }
      return sorterId;
   }

   private RelationTypeSideSorter getSelected() {
      RelationTypeSideSorter selectedSorter = null;
      IStructuredSelection selection = (IStructuredSelection) selectionProvider.getSelection();
      if (selection.size() == 1) {
         Object object = selection.getFirstElement();
         if (object instanceof RelationTypeSideSorter) {
            selectedSorter = (RelationTypeSideSorter) object;
         }
      }
      return selectedSorter;
   }

   private void createMenuItem(final Menu menu, final RelationSorter id) {
      final MenuItem menuItem = new MenuItem(menu, SWT.CHECK);
      menuItem.setText(id.getName());
      menuItem.setData(id.getGuid());
      menuItem.setSelection(false);
      menuItem.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            RelationTypeSideSorter sorter = getSelected();
            if (sorter != null) {
               try {
                  RelationSorter sorterId = sorter.getSorterId();
                  if (!id.getGuid().equals(sorterId.getGuid())) {
                     Artifact artifact = sorter.getArtifact();
                     artifact.setRelationOrder(sorter, id);
                     notifyListeners(sorter, sorterId, id);
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }

      });
   }
}
