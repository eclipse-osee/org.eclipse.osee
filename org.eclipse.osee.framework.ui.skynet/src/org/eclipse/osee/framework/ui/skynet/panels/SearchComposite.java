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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Roberto E. Escobar
 */
public class SearchComposite extends Composite implements Listener {

   private Set<Listener> listeners;
   private Combo searchArea;
   private Button executeSearch;
   private Button clear;
   private Map<String, Boolean> optionsMap;
   private Map<String, Button> optionsButtons;
   private boolean entryChanged;
   private Group optionGroup;

   public SearchComposite(Composite parent, int style) {
      this(parent, style, null);
   }

   public SearchComposite(Composite parent, int style, String[] options) {
      super(parent, style);
      this.listeners = new HashSet<Listener>();
      this.optionsMap = new HashMap<String, Boolean>();
      this.optionsButtons = new HashMap<String, Button>();
      if (options != null) {
         for (String option : options) {
            this.optionsMap.put(option, false);
         }
      }
      this.entryChanged = false;
      createControl(this);
   }

   private void createControl(Composite parent) {
      GridLayout gL = new GridLayout();
      gL.marginHeight = 0;
      gL.marginWidth = 0;
      parent.setLayout(gL);
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      createSearchInputArea(parent);
      createOptionsArea(parent);
   }

   private void createSearchInputArea(Composite parent) {
      Group group = new Group(parent, SWT.NONE);
      group.setLayout(new GridLayout());
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Enter Search String");

      this.searchArea = new Combo(group, SWT.BORDER);
      this.searchArea.setFont(getFont());
      this.searchArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      this.searchArea.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            updateFromSourceField();
         }
      });

      this.searchArea.addKeyListener(new KeyAdapter() {
         public void keyPressed(KeyEvent event) {
            // If there has been a key pressed then mark as dirty
            entryChanged = true;

            if (event.character == '\r') {
               if (executeSearch.getEnabled()) {
                  Event sendEvent = new Event();
                  sendEvent.widget = event.widget;
                  sendEvent.character = event.character;
                  sendEvent.type = SWT.KeyUp;
                  notifyListener(sendEvent);
               }
            }
         }
      });
      this.searchArea.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            updateWidgetEnablements();
         }
      });

      this.searchArea.addFocusListener(new FocusAdapter() {
         public void focusLost(FocusEvent e) {
            // Clear the flag to prevent constant update
            if (entryChanged) {
               entryChanged = false;
               updateFromSourceField();
            }
         }
      });
      createButtonBar(group);
   }

   private void createButtonBar(Composite parent) {
      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gL = new GridLayout(2, false);
      gL.marginWidth = 0;
      gL.marginHeight = 0;
      composite.setLayout(gL);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      this.executeSearch = new Button(composite, SWT.NONE);
      this.executeSearch.setText("Search");
      this.executeSearch.addListener(SWT.Selection, this);
      this.executeSearch.setEnabled(false);
      this.executeSearch.setFont(getFont());

      this.clear = new Button(composite, SWT.NONE);
      this.clear.setText("Clear History");
      this.clear.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            if (searchArea.getItemCount() > 0) {
               searchArea.removeAll();
            }
         }
      });
      this.clear.addListener(SWT.Selection, this);
      this.clear.setEnabled(false);
      this.clear.setFont(getFont());
   }

   private void createOptionsArea(Composite parent) {
      this.optionGroup = new Group(parent, SWT.NONE);
      this.optionGroup.setLayout(new GridLayout());
      this.optionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
      this.optionGroup.setText("Options");

      initializeOptions(optionsMap);
   }

   private Button getOrCreateOptionsButton(String option) {
      Button toReturn = this.optionsButtons.get(option);
      if (toReturn == null) {
         toReturn = new Button(this.optionGroup, SWT.CHECK);
         toReturn.setText(option);
         toReturn.setData(option);
         toReturn.setFont(getFont());
         toReturn.addListener(SWT.Selection, this);
         toReturn.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               Object object = e.getSource();
               if (object instanceof Button) {
                  Button button = (Button) object;
                  optionsMap.put((String) button.getData(), button.getSelection());
               }
            }
         });
         this.optionsButtons.put(option, toReturn);
      }
      return toReturn;
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
      if (isWidgetAccessible(this.searchArea)) {
         String query = this.searchArea.getText();
         if (Strings.isValid(query)) {
            toReturn = query;
         }
      }
      return toReturn;
   }

   private boolean isWidgetAccessible(Widget widget) {
      return widget != null && widget.isDisposed() != true;
   }

   private void updateWidgetEnablements() {
      if (isWidgetAccessible(this.searchArea) && isWidgetAccessible(this.executeSearch) && isWidgetAccessible(this.clear)) {
         String value = this.searchArea.getText();
         if (value != null) {
            value = value.trim();
         }
         this.executeSearch.setEnabled(Strings.isValid(value));
         this.clear.setEnabled(this.searchArea.getItemCount() > 0);
      }
   }

   private void initializeOptions(Map<String, Boolean> options) {
      for (String option : options.keySet()) {
         Button button = getOrCreateOptionsButton(option);
         button.setSelection(options.get(option));
      }
   }

   public Map<String, Boolean> getOptions() {
      return optionsMap;
   }

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
      return this.searchArea.getItems();
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

   public void restoreWidgetValues(List<String> querySearches, String lastSelected, Map<String, Boolean> options) {
      String currentSearch = getQuery();

      // Add stored directories into selector
      if (Strings.isValid(lastSelected) == false && currentSearch != null) {
         lastSelected = currentSearch;
      }

      if (querySearches == null || querySearches.isEmpty()) {
         if (Strings.isValid(lastSelected)) {
            querySearches = new ArrayList<String>();
            querySearches.add(lastSelected);
         } else {
            querySearches = Collections.emptyList();
         }
      }
      setCombo(querySearches, lastSelected);

      initializeOptions(options);
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
}
