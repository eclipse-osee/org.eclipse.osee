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

package org.eclipse.osee.framework.ui.skynet.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.AttributeTypeCheckTreeDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.HidingComposite;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IMemento;

/**
 * @author Roberto E. Escobar
 */
public class QuickSearchOptionComposite extends Composite {
   private static final String CONFIG_BUTTON_TOOLTIP = "Select to configure option.";

   private static final String OPTIONS_KEY_ID = "searchOption";
   private static final String OPTION_CONFIGS_KEY_ID = "optionConfigs";
   private static final String ENTRY_SEPARATOR = "##";

   private Group optionGroup;
   private final Map<String, Button> optionsButtons;
   private final Map<String, Text> textAreas;
   private final Map<String, Boolean> optionsMap;

   private final Set<String> mutuallyExclusiveOptionSet;
   private final Map<String, IOptionConfigurationHandler<?>> configurableOptionSet;
   private Composite wordOrderComposite;

   public QuickSearchOptionComposite(Composite parent, int style) {
      super(parent, style);
      this.optionsButtons = new LinkedHashMap<String, Button>();
      this.textAreas = new HashMap<String, Text>();
      this.optionsMap = new LinkedHashMap<String, Boolean>();
      this.mutuallyExclusiveOptionSet = new HashSet<String>();
      this.configurableOptionSet = new HashMap<String, IOptionConfigurationHandler<?>>();

      for (String option : SearchOption.asLabels()) {
         this.optionsMap.put(option, false);
      }
      for (String option : SearchOption.getMutuallyExclusiveOptions()) {
         this.optionsMap.put(option, false);
         this.mutuallyExclusiveOptionSet.add(option);
      }
      for (String option : SearchOption.getConfigurableOptions().keySet()) {
         this.optionsMap.put(option, false);
         this.configurableOptionSet.put(option, SearchOption.getConfigurableOptions().get(option));
      }
      createControl(this);
   }

   private void createControl(Composite parent) {
      this.optionGroup = new Group(parent, SWT.NONE);
      this.optionGroup.setLayout(new GridLayout());
      this.optionGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      this.optionGroup.setText("Options");

      initializeOptions(optionsMap);

      for (SearchOption option : SearchOption.values()) {
         setHelpContextForOption(option.asLabel(), option.getHelpContext());
         setToolTipForOption(option.asLabel(), option.getToolTip());
      }

      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
   }

   public boolean isOptionSelected(String key) {
      Boolean value = optionsMap.get(key);
      return value != null ? value.booleanValue() : false;
   }

   public IOptionConfigurationHandler<?> getConfiguration(String key) {
      return this.configurableOptionSet.get(key);
   }

   private void initializeOptions(Map<String, Boolean> options) {
      for (String option : options.keySet()) {
         Boolean isSelected = options.get(option);
         Button button = getOrCreateOptionsButton(option);
         button.setSelection(isSelected);
         this.optionsMap.put(option, isSelected);
      }
      updateMatchWordOrderOptions();
   }

   private void updateMatchWordOrderOptions() {
      boolean setEnabled = isMatchWordOrderEnabled();
      if (Widgets.isAccessible(wordOrderComposite)) {
         wordOrderComposite.setVisible(setEnabled);
         wordOrderComposite.setEnabled(setEnabled);
         if (!setEnabled) {
            optionsMap.put(SearchOption.Case_Sensitive.asLabel(), setEnabled);
            optionsMap.put(SearchOption.All_Match_Locations.asLabel(), setEnabled);
         } else {
            optionsMap.put(SearchOption.Case_Sensitive.asLabel(), optionsButtons.get(
                  SearchOption.Case_Sensitive.asLabel()).getSelection());
            optionsMap.put(SearchOption.All_Match_Locations.asLabel(), optionsButtons.get(
                  SearchOption.All_Match_Locations.asLabel()).getSelection());
         }
         optionGroup.getParent().layout();
      }
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
      final IOptionConfigurationHandler<?> configHandler = configurableOptionSet.get(option);
      Composite mainComposite = parent;
      if (configHandler != null) {
         mainComposite = new Composite(parent, SWT.NONE);
         mainComposite.setLayout(ALayout.getZeroMarginLayout(4, false));
         mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
      } else if (option.equals(SearchOption.All_Match_Locations.asLabel()) || option.equals(SearchOption.Case_Sensitive.asLabel())) {
         if (wordOrderComposite == null) {
            wordOrderComposite = new HidingComposite(parent, SWT.NONE);
            GridLayout layout = new GridLayout();
            layout.marginLeft = 15;
            wordOrderComposite.setLayout(layout);
            wordOrderComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
         }
         mainComposite = wordOrderComposite;
      }

      Button toReturn = new Button(mainComposite, SWT.CHECK);
      toReturn.setData(option);
      toReturn.setFont(getFont());
      toReturn.addSelectionListener(new SelectionAdapter() {

         @Override
         public void widgetSelected(SelectionEvent e) {
            Object object = e.getSource();
            if (object instanceof Button) {
               Button button = (Button) object;
               if (mutuallyExclusiveOptionSet.contains(button.getData())) {
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

               Object data = button.getData();
               boolean selection = optionsMap.get(SearchOption.By_Id.asLabel()).booleanValue();
               if (data.equals(SearchOption.By_Id.asLabel()) && selection) {
                  optionsMap.put(SearchOption.Match_Word_Order.asLabel(), false);
                  optionsButtons.get(SearchOption.Match_Word_Order.asLabel()).setSelection(false);
               } else if (data.equals(SearchOption.Match_Word_Order.asLabel()) && selection) {
                  optionsMap.put(SearchOption.By_Id.asLabel(), false);
                  optionsButtons.get(SearchOption.By_Id.asLabel()).setSelection(false);
               }
               updateMatchWordOrderOptions();
            }
         }
      });

      if (configHandler != null) {
         Label label = new Label(mainComposite, SWT.NONE);
         label.setText(option + ":");

         final Text text = new Text(mainComposite, SWT.READ_ONLY | SWT.BORDER);
         text.setText(configHandler.toString());
         GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
         data.minimumWidth = 100;
         text.setLayoutData(data);
         textAreas.put(option, text);

         Button filterConfig = new Button(mainComposite, SWT.PUSH);
         String configToolTip = configHandler.getConfigToolTip();
         filterConfig.setToolTipText(Strings.isValid(configToolTip) ? configToolTip : CONFIG_BUTTON_TOOLTIP);
         filterConfig.setImage(ImageManager.getImage(FrameworkImage.GEAR));
         filterConfig.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
               if (configHandler != null) {
                  configHandler.configure();
                  text.setText(configHandler.toString());
               }
            }
         });
      } else {
         toReturn.setText(option);
      }
      return toReturn;
   }

   public boolean isSearchByIdEnabled() {
      return isOptionSelected(SearchOption.By_Id.asLabel());
   }

   public boolean isIncludeDeletedEnabled() {
      return isOptionSelected(SearchOption.Include_Deleted.asLabel());
   }

   public boolean isMatchWordOrderEnabled() {
      return isOptionSelected(SearchOption.Match_Word_Order.asLabel());
   }

   public boolean isMatchAllLocationsEnabled() {
      return isOptionSelected(SearchOption.All_Match_Locations.asLabel());
   }

   public boolean isCaseSensitiveEnabled() {
      return isOptionSelected(SearchOption.Case_Sensitive.asLabel());
   }

   public boolean isAttributeTypeFilterEnabled() {
      return isOptionSelected(SearchOption.Attribute_Type_Filter.asLabel());
   }

   public IAttributeType[] getAttributeTypeFilter() {
      IOptionConfigurationHandler<?> handler = getConfiguration(SearchOption.Attribute_Type_Filter.asLabel());
      IAttributeType[] types = (IAttributeType[]) handler.getConfigData();
      return isAttributeTypeFilterEnabled() ? types : new IAttributeType[0];
   }

   public void saveState(IMemento memento) {
      for (String option : optionsMap.keySet()) {
         memento.putString(OPTIONS_KEY_ID + option.replaceAll(" ", "_"), optionsMap.get(option).toString());
      }

      for (Entry<String, IOptionConfigurationHandler<?>> entry : configurableOptionSet.entrySet()) {
         IOptionConfigurationHandler<?> handler = entry.getValue();
         String[] config = handler.toStore();
         if (config != null && config.length > 0) {
            memento.putString(OPTION_CONFIGS_KEY_ID + entry.getKey().replaceAll(" ", "_"), StringUtils.join(config,
                  ENTRY_SEPARATOR));
         }
      }
   }

   public void loadState(IMemento memento) {
      Map<String, String[]> configs = new HashMap<String, String[]>();
      Map<String, Boolean> options = new HashMap<String, Boolean>();

      for (SearchOption option : SearchOption.values()) {
         options.put(option.asLabel(), new Boolean(memento.getString(OPTIONS_KEY_ID + option.name())));

         if (option.isConfigurable()) {
            String configuration = memento.getString(OPTION_CONFIGS_KEY_ID + option.name());
            if (Strings.isValid(configuration)) {
               String[] values = configuration.split(ENTRY_SEPARATOR);
               configs.put(option.asLabel(), values);
            }
         }
      }
      initializeOptions(options);
      initializeConfigurations(configs);
   }

   private void initializeConfigurations(Map<String, String[]> items) {
      for (String key : items.keySet()) {
         IOptionConfigurationHandler<?> handler = configurableOptionSet.get(key);
         if (handler != null) {
            handler.loadFrom(items.get(key));
            Text text = textAreas.get(key);
            if (text != null) {
               text.setText(handler.toString());
            }
         }
      }
   }

   private void setHelpContextForOption(String optionId, String helpContext) {
      Control control = getOrCreateOptionsButton(optionId);
      if (Widgets.isAccessible(control)) {
         SkynetGuiPlugin.getInstance().setHelp(control, helpContext, "org.eclipse.osee.framework.help.ui");
      }
   }

   private void setToolTipForOption(String optionId, String toolTip) {
      Control control = getOrCreateOptionsButton(optionId);
      if (Widgets.isAccessible(control)) {
         control.setToolTipText(toolTip);
      }
   }

   private interface IOptionConfigurationHandler<T> {

      public void loadFrom(String[] strings);

      public String[] toStore();

      public String toString();

      public void configure();

      public T[] getConfigData();

      public String getConfigToolTip();

   }

   private enum SearchOption {
      Attribute_Type_Filter(
            "quick_search_attribute_type_filter",
            "When selected, searches only through the artifact's containing the selected attribute types.",
            true,
            new AttributeTypeFilterConfigHandler()),
      By_Id(
            "quick_search_by_id_option",
            "When selected, searches by GUID(s) or HRID(s). Accepts comma or space separated ids.",
            true),
      Include_Deleted(
            "quick_search_deleted_option",
            "When selected, does not filter out deleted artifacts from search results.",
            false),
      Match_Word_Order("quick_search_word_order_option", "When selected, search will match query word order.", false),
      All_Match_Locations(
            "quick_search_all_match_locations_option",
            "When selected, returns all match locations. NOTE: If the search matches many artifacts, performance may be slow.",
            false),
      Case_Sensitive(
            "quick_search_case_sensitive_option",
            "When selected, performs a case sensitive search. NOTE: This is only applicable if match word order is also selected.",
            false);

      private static String[] labels = null;
      private static String[] mutuallyExclusive = null;
      private static Map<String, IOptionConfigurationHandler<?>> configurable = null;
      private final String helpContext;
      private final String toolTip;
      private final boolean isRadio;
      private final IOptionConfigurationHandler<?> configHandler;

      SearchOption(String helpContext, String toolTip, boolean isRadio) {
         this(helpContext, toolTip, isRadio, null);
      }

      SearchOption(String helpContext, String toolTip, boolean isRadio, IOptionConfigurationHandler<?> configHandler) {
         this.helpContext = "";
         this.toolTip = toolTip;
         this.isRadio = isRadio;
         this.configHandler = configHandler;
      }

      public String asLabel() {
         return name().replaceAll("_", " ");
      }

      public String getHelpContext() {
         return helpContext;
      }

      public String getToolTip() {
         return toolTip;
      }

      public boolean isConfigurable() {
         return configHandler != null;
      }

      public IOptionConfigurationHandler<?> getConfigHandler() {
         return configHandler;
      }

      public static String[] getMutuallyExclusiveOptions() {
         if (mutuallyExclusive == null) {
            List<String> exclusiveOptions = new ArrayList<String>();
            for (SearchOption option : SearchOption.values()) {
               if (option.isRadio) {
                  exclusiveOptions.add(option.asLabel());
               }
            }
            mutuallyExclusive = exclusiveOptions.toArray(new String[exclusiveOptions.size()]);
         }
         return mutuallyExclusive;
      }

      public static Map<String, IOptionConfigurationHandler<?>> getConfigurableOptions() {
         if (configurable == null) {
            configurable = new HashMap<String, IOptionConfigurationHandler<?>>();
            for (SearchOption option : SearchOption.values()) {
               if (option.isConfigurable()) {
                  configurable.put(option.asLabel(), option.getConfigHandler());
               }
            }
         }
         return configurable;
      }

      public static String[] asLabels() {
         if (labels == null) {
            SearchOption[] options = SearchOption.values();
            labels = new String[options.length];
            for (int index = 0; index < options.length; index++) {
               labels[index] = options[index].asLabel();
            }
         }
         return labels;
      }
   }

   private final static class AttributeTypeComparator implements Comparator<IAttributeType> {

      @Override
      public int compare(IAttributeType o1, IAttributeType o2) {
         return o1.getName().compareTo(o2.getName());
      }

   }
   private final static class AttributeTypeFilterConfigHandler implements IOptionConfigurationHandler<IAttributeType> {
      private final List<IAttributeType> configuration;
      private final Comparator<IAttributeType> attrTypeComparator;

      public AttributeTypeFilterConfigHandler() {
         this.attrTypeComparator = new AttributeTypeComparator();
         this.configuration = new ArrayList<IAttributeType>();
         this.configuration.add(getDefault());
      }

      @Override
      public void configure() {
         try {
            Collection<AttributeType> taggableItems = AttributeTypeManager.getTaggableTypes();
            AttributeTypeCheckTreeDialog dialog = new AttributeTypeCheckTreeDialog(taggableItems);
            dialog.setTitle("Attribute Type Filter Selection");
            dialog.setMessage("Select attribute types to search in.");

            List<AttributeType> selectedElements = new ArrayList<AttributeType>();
            for (AttributeType type : taggableItems) {
               if (configuration.contains(type.getName())) {
                  selectedElements.add(type);
               }
            }
            dialog.setInitialElementSelections(selectedElements);

            int result = dialog.open();
            if (result == Window.OK) {
               configuration.clear();
               Collection<AttributeType> results = dialog.getSelection();
               for (AttributeType selected : results) {
                  configuration.add(selected);
               }
               if (configuration.isEmpty()) {
                  configuration.add(getDefault());
               }
            }
            Collections.sort(configuration, attrTypeComparator);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }

      @Override
      public String getConfigToolTip() {
         return "Select to configure attribute type filter.";
      }

      @Override
      public IAttributeType[] getConfigData() {
         if (configuration.isEmpty()) {
            configuration.add(getDefault());
         }
         return configuration.toArray(new IAttributeType[configuration.size()]);
      }

      public IAttributeType getDefault() {
         return CoreAttributeTypes.NAME;
      }

      @Override
      public void loadFrom(String[] items) {
         if (items != null) {
            configuration.clear();
            for (String entry : items) {
               try {
                  AttributeType type = AttributeTypeManager.getTypeByGuid(entry);
                  configuration.add(type);
               } catch (OseeCoreException ex) {
                  OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
               }
            }
         }
         if (configuration.isEmpty()) {
            configuration.add(getDefault());
         }
      }

      @Override
      public String toString() {
         Collection<AttributeType> taggableItems;
         try {
            taggableItems = AttributeTypeManager.getTaggableTypes();
            if (taggableItems.size() == configuration.size()) {
               return "All";
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
         return StringUtils.join(configuration, ", ");
      }

      @Override
      public String[] toStore() {
         String[] guids = new String[configuration.size()];
         int index = 0;
         for (IAttributeType type : configuration) {
            guids[index++] = type.getGuid();
         }
         return guids;
      }
   }

}
