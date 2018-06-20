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

package org.eclipse.osee.framework.ui.skynet.panels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.HelpContext;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.search.QuickSearchOptionComposite;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Roberto E. Escobar
 */
public class SearchComposite extends Composite implements Listener {
   private static final String SEARCH_BUTTON_TOOLTIP = "Executes GUID or ArtId search";
   private static final String SEARCH_COMBO_TOOLTIP =
      "Enter word(s) to search for or select historical value from pull-down on the right.";

   private final Set<Listener> listeners;
   private Combo searchArea;
   private Button executeSearch;
   private Button clear;
   private boolean entryChanged;
   private final String buttonText, groupBoxText;
   private QuickSearchOptionComposite optionsComposite;

   public SearchComposite(Composite parent, int style, String buttonText, String groupBoxText) {
      super(parent, style);
      this.listeners = new HashSet<>();
      this.entryChanged = false;
      this.buttonText = buttonText;
      this.groupBoxText = groupBoxText;
      createControl(this);
   }

   private void createControl(Composite parent) {
      GridLayout gL = new GridLayout();
      gL.marginHeight = 0;
      gL.marginWidth = 0;
      parent.setLayout(gL);
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createSearchInputArea(parent);
   }

   private void createSearchInputArea(Composite parent) {
      Composite parentToUse = null;
      if (Strings.isValid(groupBoxText)) {
         Group group = new Group(parent, SWT.NONE);
         group.setText(groupBoxText);
         group.setLayout(new GridLayout(2, false));
         parentToUse = group;
      } else {
         parentToUse = new Composite(parent, SWT.NONE);
         parentToUse.setLayout(ALayout.getZeroMarginLayout(2, false));
      }
      parentToUse.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      createButtonBar(parentToUse);

      this.searchArea = new Combo(parentToUse, SWT.BORDER);
      this.searchArea.setFont(getFont());
      this.searchArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      this.searchArea.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent event) {
            // If there has been a key pressed then mark as dirty
            entryChanged = true;

            if (event.character == '\r') {
               if (executeSearch.getEnabled()) {
                  if (entryChanged) {
                     entryChanged = false;
                     updateFromSourceField();
                  }

                  Event sendEvent = new Event();
                  sendEvent.widget = event.widget;
                  sendEvent.character = event.character;
                  sendEvent.type = SWT.KeyUp;
                  notifyListener(sendEvent);
               }
            }
         }
      });
      addContextMenu(searchArea);

      this.searchArea.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            updateWidgetEnablements();
         }
      });

      this.searchArea.setToolTipText(SEARCH_COMBO_TOOLTIP);
   }

   private void createButtonBar(Composite parent) {
      this.executeSearch = new Button(parent, SWT.NONE);
      this.executeSearch.setText(buttonText);
      this.executeSearch.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            if (entryChanged) {
               entryChanged = false;
               updateFromSourceField();
            }
         }
      });
      this.executeSearch.addListener(SWT.Selection, this);
      this.executeSearch.setEnabled(false);
      this.executeSearch.setFont(getFont());
      this.executeSearch.setToolTipText(SEARCH_BUTTON_TOOLTIP);
   }

   public void clearHistory() {
      if (searchArea.getItemCount() > 0) {
         searchArea.removeAll();
      }
   }

   private void updateFromSourceField() {
      setSearchQuery(getQuery());
      updateWidgetEnablements();
   }

   private void setSearchQuery(String query) {
      if (Strings.isValid(query)) {
         String[] currentItems = this.searchArea.getItems();
         int selectionIndex = -1;
         for (int i = 0; i < currentItems.length; i++) {
            if (currentItems[i].equals(query)) {
               selectionIndex = i;
            }
         }
         if (selectionIndex < 0) {
            int oldLength = currentItems.length;
            String[] newItems = new String[oldLength + 1];
            System.arraycopy(currentItems, 0, newItems, 0, oldLength);
            newItems[oldLength] = query;
            this.searchArea.setItems(newItems);
            selectionIndex = oldLength;
         }
         this.searchArea.select(selectionIndex);
      }
   }

   public String getQuery() {
      String toReturn = "";
      if (Widgets.isAccessible(this.searchArea)) {
         String query = this.searchArea.getText();
         if (Strings.isValid(query)) {
            toReturn = query;
         }
      }
      return toReturn;
   }

   public void updateWidgetEnablements() {
      if (Widgets.isAccessible(this.searchArea)) {
         boolean artTypeSelected = optionsComposite != null && optionsComposite.getArtifactTypeFilter().length > 0;
         boolean attrTypeSelected = optionsComposite != null && optionsComposite.getAttributeTypeFilter().length > 0;
         String value = this.searchArea.getText();
         if (value != null) {
            value = value.trim();
         }
         if (Widgets.isAccessible(this.executeSearch)) {
            if (isAttributeComposite() && !Strings.isValid(value) && artTypeSelected && !attrTypeSelected) {
               this.executeSearch.setEnabled(true);
            } else {
               this.executeSearch.setEnabled(Strings.isValid(value));
            }
         }
         if (Widgets.isAccessible(this.clear)) {
            this.clear.setEnabled(this.searchArea.getItemCount() > 0);
         }
      }
   }

   private boolean isAttributeComposite() {
      Composite parent = getParent();
      boolean isAttribute =
         (parent != null && (parent instanceof Group) && ((Group) parent).getText().contains("Attribute"));
      return isAttribute;
   }

   @Override
   public void handleEvent(Event event) {
      updateWidgetEnablements();
      notifyListener(event);
   }

   public void addListener(Listener listener) {
      synchronized (listeners) {
         this.listeners.add(listener);
      }
   }

   public void removeListener(Listener listener) {
      synchronized (listeners) {
         this.listeners.remove(listener);
      }
   }

   private void notifyListener(Event event) {
      synchronized (listeners) {
         for (Listener listener : listeners) {
            listener.handleEvent(event);
         }
      }
   }

   public String[] getQueryHistory() {
      return Widgets.isAccessible(this.searchArea) ? this.searchArea.getItems() : new String[0];
   }

   private void setCombo(List<String> values, String lastSelected) {
      int toSelect = 0;
      for (int i = 0; i < values.size(); i++) {
         String toStore = values.get(i);
         if (Strings.isValid(toStore)) {
            this.searchArea.add(toStore);
            if (toStore.equals(lastSelected)) {
               toSelect = i;
               this.searchArea.select(toSelect);
            }
         }
      }
   }

   public void restoreWidgetValues(List<String> querySearches, String lastSelected, Map<String, Boolean> options, Map<String, String[]> configs) {
      String currentSearch = getQuery();

      // Add stored directories into selector
      if (Strings.isValid(lastSelected) == false && currentSearch != null) {
         lastSelected = currentSearch;
      }

      if (querySearches == null || querySearches.isEmpty()) {
         if (Strings.isValid(lastSelected)) {
            querySearches = new ArrayList<>();
            querySearches.add(lastSelected);
         } else {
            querySearches = Collections.emptyList();
         }
      }
      setCombo(querySearches, lastSelected);
   }

   public boolean isExecuteSearchEvent(Event event) {
      boolean toReturn = false;
      Widget widget = event.widget;
      if (widget != null) {
         if (widget.equals(this.executeSearch)) {
            toReturn = true;
         } else if (widget.equals(this.searchArea) && event.type == SWT.KeyUp && event.character == '\r') {
            toReturn = true;
         }
      }
      return toReturn;
   }

   public void setToolTipForSearchCombo(String toolTip) {
      if (Widgets.isAccessible(this.searchArea)) {
         this.searchArea.setToolTipText(toolTip);
      }
   }

   public void setHelpContext(HelpContext helpContext) {
      if (Widgets.isAccessible(this.searchArea) && Widgets.isAccessible(this.executeSearch) && Widgets.isAccessible(
         this.clear)) {
         HelpUtil.setHelp(searchArea, helpContext);
         HelpUtil.setHelp(executeSearch, helpContext);
         HelpUtil.setHelp(clear, helpContext);
      }
   }

   public List<Control> getSearchChildren() {
      List<Control> children = new ArrayList<>();
      if (Widgets.isAccessible(this.searchArea) && Widgets.isAccessible(this.executeSearch) && Widgets.isAccessible(
         this.clear)) {
         children = Arrays.asList(this, searchArea, executeSearch, clear);
      }
      return children;
   }

   public void setOptionsComposite(QuickSearchOptionComposite optionsComposite) {
      this.optionsComposite = optionsComposite;
   }

   /**
    * Since adding new menu replaces the default menu, we must re-create the default copy/paste options
    */
   private void addContextMenu(final Combo searchArea) {
      Menu menu = new Menu(searchArea);
      MenuItem item = new MenuItem(menu, SWT.PUSH);
      item.setText("Cut");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            searchArea.cut();
         }
      });
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Copy");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            searchArea.copy();
         }
      });
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Paste");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            searchArea.paste();
         }
      });
      // Add Paste-and-Go menu option
      item = new MenuItem(menu, SWT.PUSH);
      item.setText("Paste-and-Go");
      item.addListener(SWT.Selection, new Listener() {
         @Override
         public void handleEvent(Event event) {
            searchArea.setText("");
            searchArea.paste();
            executeSearch.notifyListeners(SWT.Selection, null);
         }
      });

      searchArea.setMenu(menu);
   }

   public QuickSearchOptionComposite getOptionsComposite() {
      return optionsComposite;
   }

   public Combo getSearchArea() {
      return searchArea;
   }

   public Button getExecuteSearch() {
      return executeSearch;
   }

}
