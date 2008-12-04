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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

/**
 * @author Roberto E. Escobar
 */
public class SearchComposite extends Composite implements Listener {
   private static final Image CONFIG_IMAGE = SkynetGuiPlugin.getInstance().getImage("gear.gif");
   private static final String CLEAR_HISTORY_TOOLTIP = "Clears search history";
   private static final String SEARCH_BUTTON_TOOLTIP = "Executes search";
   private static final String SEARCH_COMBO_TOOLTIP =
         "Enter word(s) to search for or select historical value from pull-down on the right.";
   private static final String CONFIG_BUTTON_TOOLTIP = "Select to configure option.";

   private Set<Listener> listeners;
   private Combo searchArea;
   private Button executeSearch;
   private Button clear;
   private Map<String, Boolean> optionsMap;
   private Map<String, Button> optionsButtons;
   private Map<String, Text> textAreas;
   private Set<String> mutuallyExclusiveOptionSet;
   private Map<String, IOptionConfigurationHandler> configurableOptionSet;
   private boolean entryChanged;
   private Group optionGroup;

   public SearchComposite(Composite parent, int style, String[] options, String[] mutuallyExclusiveOptions, Map<String, IOptionConfigurationHandler> configurableOptions) {
      super(parent, style);
      this.listeners = new HashSet<Listener>();
      this.optionsMap = new LinkedHashMap<String, Boolean>();
      this.optionsButtons = new LinkedHashMap<String, Button>();
      this.textAreas = new HashMap<String, Text>();
      this.mutuallyExclusiveOptionSet = new HashSet<String>();
      this.configurableOptionSet = new HashMap<String, IOptionConfigurationHandler>();
      if (options != null) {
         for (String option : options) {
            this.optionsMap.put(option, false);
         }
      }
      if (mutuallyExclusiveOptions != null) {
         for (String option : mutuallyExclusiveOptions) {
            this.optionsMap.put(option, false);
            this.mutuallyExclusiveOptionSet.add(option);
         }
      }

      if (configurableOptions != null) {
         for (String option : configurableOptions.keySet()) {
            this.optionsMap.put(option, false);
            this.configurableOptionSet.put(option, configurableOptions.get(option));
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
      group.setLayout(new GridLayout(2, false));
      group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      group.setText("Enter Search String");

      this.searchArea = new Combo(group, SWT.BORDER);
      this.searchArea.setFont(getFont());
      this.searchArea.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      this.searchArea.addKeyListener(new KeyAdapter() {
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

      this.searchArea.addModifyListener(new ModifyListener() {
         @Override
         public void modifyText(ModifyEvent e) {
            updateWidgetEnablements();
         }
      });

      this.searchArea.setToolTipText(SEARCH_COMBO_TOOLTIP);
      createButtonBar(group);
   }

   private void createButtonBar(Composite parent) {
      this.clear = new Button(parent, SWT.NONE);
      this.clear.setText("Clear History");
      this.clear.addSelectionListener(new SelectionAdapter() {
         public void widgetSelected(SelectionEvent e) {
            if (searchArea.getItemCount() > 0) {
               searchArea.removeAll();
               for (String option : optionsMap.keySet()) {
                  Button button = getOrCreateOptionsButton(option);
                  button.setSelection(false);
                  optionsMap.put(option, false);
               }
            }
         }
      });
      this.clear.addListener(SWT.Selection, this);
      this.clear.setEnabled(false);
      this.clear.setFont(getFont());
      this.clear.setToolTipText(CLEAR_HISTORY_TOOLTIP);

      Composite composite = new Composite(parent, SWT.NONE);
      GridLayout gL = new GridLayout();
      gL.marginWidth = 0;
      gL.marginHeight = 0;
      composite.setLayout(gL);
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      this.executeSearch = new Button(composite, SWT.NONE);
      this.executeSearch.setText("Search");
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
         toReturn = createButton(this.optionGroup, option);
         this.optionsButtons.put(option, toReturn);
      }
      return toReturn;
   }

   private Button createButton(Composite parent, String option) {
      final IOptionConfigurationHandler configHandler = configurableOptionSet.get(option);
      Composite mainComposite = parent;
      if (configHandler != null) {
         mainComposite = new Composite(parent, SWT.NONE);
         GridLayout layout = new GridLayout(4, false);
         layout.marginHeight = 0;
         layout.marginWidth = 0;
         mainComposite.setLayout(layout);
         mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      }

      Button toReturn = new Button(mainComposite, SWT.CHECK);
      toReturn.setData(option);
      toReturn.setFont(getFont());
      toReturn.addListener(SWT.Selection, this);
      toReturn.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Object object = e.getSource();
            if (object instanceof Button) {
               Button button = (Button) object;
               if (mutuallyExclusiveOptionSet.contains((String) button.getData())) {
                  if (button.getSelection()) {
                     for (String entry : mutuallyExclusiveOptionSet) {
                        Button other = optionsButtons.get(entry);
                        if (!other.equals(button)) {
                           other.setSelection(false);
                        }
                        optionsMap.put((String) other.getData(), other.getSelection());
                     }
                  }
               }
               optionsMap.put((String) button.getData(), button.getSelection());
            }
         }
      });

      if (configHandler != null) {
         Label label = new Label(mainComposite, SWT.NONE);
         label.setText(option + ":");

         final Text text = new Text(mainComposite, SWT.READ_ONLY | SWT.BORDER);
         text.setText(StringFormat.separateWith(configHandler.getConfiguration(), ", "));
         GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
         data.minimumWidth = 100;
         text.setLayoutData(data);
         textAreas.put(option, text);

         Button filterConfig = new Button(mainComposite, SWT.PUSH);
         String configToolTip = configHandler.getConfigToolTip();
         filterConfig.setToolTipText(Strings.isValid(configToolTip) ? configToolTip : CONFIG_BUTTON_TOOLTIP);
         filterConfig.setImage(CONFIG_IMAGE);
         filterConfig.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               if (configHandler != null) {
                  configHandler.configure();
                  updateTextArea(text, configHandler.getConfiguration());
               }
            }
         });
      } else {
         toReturn.setText(option);
      }
      return toReturn;
   }

   private void updateTextArea(Text text, String[] data) {
      text.setText(StringFormat.separateWith(data, ", "));
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
      if (isWidgetAccessible(this.searchArea)) {
         String value = this.searchArea.getText();
         if (value != null) {
            value = value.trim();
         }
         if (isWidgetAccessible(this.executeSearch)) {
            this.executeSearch.setEnabled(Strings.isValid(value));
         }
         if (isWidgetAccessible(this.clear)) {
            this.clear.setEnabled(this.searchArea.getItemCount() > 0);
         }
      }
   }

   private void initializeOptions(Map<String, Boolean> options) {
      for (String option : options.keySet()) {
         Boolean isSelected = options.get(option);
         Button button = getOrCreateOptionsButton(option);
         button.setSelection(isSelected);
         this.optionsMap.put(option, isSelected);
      }
   }

   public Map<String, Boolean> getOptions() {
      return optionsMap;
   }

   public Map<String, String[]> getConfigurations() {
      Map<String, String[]> toReturn = new HashMap<String, String[]>();
      for (String key : configurableOptionSet.keySet()) {
         toReturn.put(key, configurableOptionSet.get(key).getConfiguration());
      }
      return toReturn;
   }

   public boolean isOptionSelected(String key) {
      Boolean value = optionsMap.get(key);
      return value != null ? value.booleanValue() : false;
   }

   public String[] getConfiguration(String key) {
      IOptionConfigurationHandler handler = this.configurableOptionSet.get(key);
      if (handler != null) {
         return handler.getConfiguration();
      } else {
         return new String[0];
      }
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
      return isWidgetAccessible(this.searchArea) ? this.searchArea.getItems() : new String[0];
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
            querySearches = new ArrayList<String>();
            querySearches.add(lastSelected);
         } else {
            querySearches = Collections.emptyList();
         }
      }
      setCombo(querySearches, lastSelected);

      initializeOptions(options);
      initializeConfigurations(configs);
   }

   private void initializeConfigurations(Map<String, String[]> items) {
      for (String key : items.keySet()) {
         IOptionConfigurationHandler handler = configurableOptionSet.get(key);
         if (handler != null) {
            handler.setConfiguration(items.get(key));
            Text text = textAreas.get(key);
            if (text != null) {
               updateTextArea(text, handler.getConfiguration());
            }
         }
      }
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
      if (isWidgetAccessible(this.searchArea)) {
         this.searchArea.setToolTipText(toolTip);
      }
   }

   public void setHelpContext(String helpContext) {
      if (isWidgetAccessible(this.searchArea) && isWidgetAccessible(this.executeSearch) && isWidgetAccessible(this.clear)) {
         SkynetGuiPlugin.getInstance().setHelp(searchArea, helpContext);
         SkynetGuiPlugin.getInstance().setHelp(executeSearch, helpContext);
         SkynetGuiPlugin.getInstance().setHelp(clear, helpContext);
      }
   }

   public void setHelpContextForOption(String optionId, String helpContext) {
      Control control = getOrCreateOptionsButton(optionId);
      if (isWidgetAccessible(control)) {
         SkynetGuiPlugin.getInstance().setHelp(control, helpContext);
      }
   }

   public void setToolTipForOption(String optionId, String toolTip) {
      Control control = getOrCreateOptionsButton(optionId);
      if (isWidgetAccessible(control)) {
         control.setToolTipText(toolTip);
      }
   }

   public interface IOptionConfigurationHandler {

      public void setConfiguration(String[] strings);

      public void configure();

      public String[] getConfiguration();

      public String getConfigToolTip();

   }
}
