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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.HelpContext;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.help.ui.OseeHelpContext;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.HelpUtil;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.panels.SearchComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxArtifactTypeDialog;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.FilteredCheckboxAttributeTypeDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
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
   private final Map<SearchOption, Button> optionsButtons;
   private final Map<SearchOption, Text> textAreas;
   private final Map<SearchOption, Boolean> optionsMap;

   private final Map<SearchOption, IOptionConfigurationHandler<?>> configurableOptionSet;

   private Text attributeSearchText;
   private Text artifactSearchText;

   private SearchComposite attrSearchComposite;

   public QuickSearchOptionComposite(Composite parent, int style) {
      super(parent, style);
      this.optionsButtons = new LinkedHashMap<>();
      this.textAreas = new HashMap<>();
      this.optionsMap = new LinkedHashMap<>();
      this.configurableOptionSet = new HashMap<>();

      for (SearchOption option : SearchOption.values()) {
         this.optionsMap.put(option, false);
      }
      for (SearchOption option : SearchOption.getConfigurableOptions().keySet()) {
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
         setHelpContextForOption(option, option.getHelpContext());
         setToolTipForOption(option, option.getToolTip());
      }

      Composite composite = new Composite(parent, SWT.NONE);
      composite.setLayout(ALayout.getZeroMarginLayout());
      composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
   }

   public boolean isOptionSelected(SearchOption option) {
      Boolean value = optionsMap.get(option);
      return value != null ? value.booleanValue() : false;
   }

   public IOptionConfigurationHandler<?> getConfiguration(SearchOption option) {
      return this.configurableOptionSet.get(option);
   }

   private void initializeOptions(Map<SearchOption, Boolean> options) {
      for (SearchOption option : options.keySet()) {
         Boolean isSelected = options.get(option);
         Button button = getOrCreateOptionsButton(option);
         button.setSelection(isSelected);
         this.optionsMap.put(option, isSelected);
      }
      updateExactMatchOptions();
   }

   private void updateSearchEnablement() {
      if (Widgets.isAccessible(attrSearchComposite)) {
         attrSearchComposite.updateWidgetEnablements();
      }
   }

   private void updateExactMatchOptions() {
      Button caseBtn = optionsButtons.get(SearchOption.Case_Sensitive);
      Button mwoBtn = optionsButtons.get(SearchOption.Match_Word_Order);

      boolean exactMatch = isExactMatchEnabled();
      caseBtn.setEnabled(!exactMatch);
      mwoBtn.setEnabled(!exactMatch);
   }

   private Button getOrCreateOptionsButton(SearchOption option) {
      Button toReturn = this.optionsButtons.get(option);
      if (toReturn == null) {
         toReturn = createButton(this.optionGroup, option);
         this.optionsButtons.put(option, toReturn);
      }
      return toReturn;
   }

   private Button createButton(Composite parent, SearchOption option) {
      final IOptionConfigurationHandler<?> configHandler = configurableOptionSet.get(option);
      Composite mainComposite = parent;
      if (configHandler != null) {
         mainComposite = new Composite(parent, SWT.NONE);
         mainComposite.setLayout(ALayout.getZeroMarginLayout(4, false));
         mainComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
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
               optionsMap.put((SearchOption) button.getData(), button.getSelection());

               updateExactMatchOptions();
               updateSearchEnablement();
            }
         }
      });

      if (configHandler != null) {
         Label label = new Label(mainComposite, SWT.NONE);
         label.setText(option.asLabel() + ":");
         if (option == SearchOption.Attribute_Types) {
            label.addMouseListener(new MouseAdapter() {

               @Override
               public void mouseUp(org.eclipse.swt.events.MouseEvent event) {
                  if (event.button == 3) {
                     try {
                        // check search by attribute
                        toReturn.setSelection(true);
                        optionsMap.put(SearchOption.Attribute_Types, true);
                        updateExactMatchOptions();
                        // set attribute search by Name
                        attributeSearchText.setText(CoreAttributeTypes.Name.getName());
                        configHandler.loadFrom(new String[] {CoreAttributeTypes.Name.getIdString()});
                     } catch (OseeCoreException ex) {
                        OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
                     }
                  }
               }
            });
         }

         Button filterConfig = new Button(mainComposite, SWT.PUSH);
         String configToolTip = configHandler.getConfigToolTip();
         filterConfig.setToolTipText(Strings.isValid(configToolTip) ? configToolTip : CONFIG_BUTTON_TOOLTIP);
         filterConfig.setImage(ImageManager.getImage(FrameworkImage.GEAR));
         filterConfig.addSelectionListener(new TypeSelectionFilter(option, configHandler));

         Text searchText = new Text(mainComposite, SWT.READ_ONLY | SWT.BORDER);
         searchText.setText(configHandler.toString());
         GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
         data.minimumWidth = 100;
         searchText.setLayoutData(data);
         if (option.equals(SearchOption.Attribute_Types)) {
            attributeSearchText = searchText;
         } else if (option.equals(SearchOption.Artifact_Types)) {
            artifactSearchText = searchText;
         }
         textAreas.put(option, searchText);

      } else {
         toReturn.setText(option.asLabel());
      }
      return toReturn;
   }

   private class TypeSelectionFilter extends SelectionAdapter {
      private final SearchOption option;
      private final IOptionConfigurationHandler<?> configHandler;

      private TypeSelectionFilter(SearchOption option, IOptionConfigurationHandler<?> configHandler) {
         this.option = option;
         this.configHandler = configHandler;
      }

      @Override
      public void widgetSelected(SelectionEvent e) {
         configHandler.configure();
         if (option.equals(SearchOption.Attribute_Types)) {
            attributeSearchText.setText(configHandler.toString());

         } else if (option.equals(SearchOption.Artifact_Types)) {
            artifactSearchText.setText(configHandler.toString());
         }
         updateSearchEnablement();
      }
   }

   public boolean isMatchWordOrderEnabled() {
      return isOptionSelected(SearchOption.Match_Word_Order);
   }

   public boolean isExactMatchEnabled() {
      return isOptionSelected(SearchOption.Exact_Match);
   }

   public boolean isCaseSensitiveEnabled() {
      return isOptionSelected(SearchOption.Case_Sensitive);
   }

   public boolean isAttributeTypeFilterEnabled() {
      return isOptionSelected(SearchOption.Attribute_Types);
   }

   public boolean isArtifactTypeFilterEnabled() {
      return isOptionSelected(SearchOption.Artifact_Types);
   }

   public AttributeTypeId[] getAttributeTypeFilter() {
      IOptionConfigurationHandler<?> handler = getConfiguration(SearchOption.Attribute_Types);
      AttributeTypeId[] types = (AttributeTypeId[]) handler.getConfigData();
      return isAttributeTypeFilterEnabled() ? types : new AttributeTypeId[0];
   }

   public IArtifactType[] getArtifactTypeFilter() {
      IOptionConfigurationHandler<?> handler = getConfiguration(SearchOption.Artifact_Types);
      IArtifactType[] types = (IArtifactType[]) handler.getConfigData();
      return isArtifactTypeFilterEnabled() ? types : new IArtifactType[0];
   }

   public void saveState(IMemento memento) {
      for (SearchOption option : optionsMap.keySet()) {
         memento.putString(OPTIONS_KEY_ID + option.name(), optionsMap.get(option).toString());
      }

      for (Entry<SearchOption, IOptionConfigurationHandler<?>> entry : configurableOptionSet.entrySet()) {
         IOptionConfigurationHandler<?> handler = entry.getValue();
         String[] config = handler.toStore();
         if (config != null && config.length > 0) {
            memento.putString(OPTION_CONFIGS_KEY_ID + entry.getKey().name(), StringUtils.join(config, ENTRY_SEPARATOR));
         }
      }
   }

   public void loadState(IMemento memento) {
      Map<SearchOption, String[]> configs = new HashMap<>();
      Map<SearchOption, Boolean> options = new HashMap<>();

      for (SearchOption option : SearchOption.values()) {
         options.put(option, new Boolean(memento.getString(OPTIONS_KEY_ID + option.name())));

         if (option.isConfigurable()) {
            String configuration = memento.getString(OPTION_CONFIGS_KEY_ID + option.name());
            if (Strings.isValid(configuration)) {
               String[] values = configuration.split(ENTRY_SEPARATOR);
               configs.put(option, values);
            }
         }
      }
      initializeOptions(options);
      initializeConfigurations(configs);
      updateSearchEnablement();
   }

   private void initializeConfigurations(Map<SearchOption, String[]> items) {
      for (SearchOption option : items.keySet()) {
         IOptionConfigurationHandler<?> handler = configurableOptionSet.get(option);
         if (handler != null) {
            handler.loadFrom(items.get(option));
            Text text = textAreas.get(option);
            if (text != null) {
               text.setText(handler.toString());
            }
         }
      }
   }

   private void setHelpContextForOption(SearchOption option, HelpContext helpContext) {
      Control control = getOrCreateOptionsButton(option);
      if (Widgets.isAccessible(control)) {
         HelpUtil.setHelp(control, helpContext);
      }
   }

   private void setToolTipForOption(SearchOption option, String toolTip) {
      Control control = getOrCreateOptionsButton(option);
      if (Widgets.isAccessible(control)) {
         control.setToolTipText(toolTip);
      }
   }

   private interface IOptionConfigurationHandler<T> {

      public void loadFrom(String[] strings);

      public String[] toStore();

      @Override
      public String toString();

      public void configure();

      public T[] getConfigData();

      public String getConfigToolTip();

   }

   private enum SearchOption {
      Attribute_Types(OseeHelpContext.QUICK_SEARCH_TYPE_FILTER, "Searches only the selected attribute types.", new AttributeTypeFilterConfigHandler()),
      Artifact_Types(OseeHelpContext.QUICK_SEARCH_TYPE_FILTER, "Searches only the selected artifact types.", new ArtifactTypeFilterConfigHandler()),
      Match_Word_Order(OseeHelpContext.QUICK_SEARCH_WORD_ORDER, "Matches text containing search words in same order as input."),
      Case_Sensitive(OseeHelpContext.QUICK_SEARCH_CASE_SENSITIVE, "Matches text containing the same case as input."),
      Exact_Match(OseeHelpContext.QUICK_SEARCH_EXACT_MATCH, "Matches each input character exactly, including case and order.");

      private static Map<SearchOption, IOptionConfigurationHandler<?>> configurable = null;
      private final HelpContext helpContext;
      private final String toolTip;
      private final IOptionConfigurationHandler<?> configHandler;

      SearchOption(HelpContext helpContext, String toolTip) {
         this(helpContext, toolTip, null);
      }

      SearchOption(HelpContext helpContext, String toolTip, IOptionConfigurationHandler<?> configHandler) {
         this.helpContext = helpContext;
         this.toolTip = toolTip;
         this.configHandler = configHandler;
      }

      public String asLabel() {
         return name().replaceAll("_", " ");
      }

      public HelpContext getHelpContext() {
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

      public static Map<SearchOption, IOptionConfigurationHandler<?>> getConfigurableOptions() {
         if (configurable == null) {
            configurable = new HashMap<>();
            for (SearchOption option : SearchOption.values()) {
               if (option.isConfigurable()) {
                  configurable.put(option, option.getConfigHandler());
               }
            }
         }
         return configurable;
      }
   }

   private final static class AttributeTypeComparator implements Comparator<AttributeTypeToken> {

      @Override
      public int compare(AttributeTypeToken o1, AttributeTypeToken o2) {
         return o1.getName().compareTo(o2.getName());
      }

   }
   private final static class ArtifactTypeComparator implements Comparator<IArtifactType> {

      @Override
      public int compare(IArtifactType o1, IArtifactType o2) {
         return o1.getName().compareTo(o2.getName());
      }

   }
   private final static class AttributeTypeFilterConfigHandler implements IOptionConfigurationHandler<AttributeTypeId> {
      private final List<AttributeTypeToken> configuration;
      private final Comparator<AttributeTypeToken> attrTypeComparator;

      public AttributeTypeFilterConfigHandler() {
         this.attrTypeComparator = new AttributeTypeComparator();
         this.configuration = new ArrayList<>();
         this.configuration.add(getDefault());
      }

      @Override
      public void configure() {
         try {
            Collection<AttributeTypeId> taggableItems = AttributeTypeManager.getTaggableTypes();
            FilteredCheckboxAttributeTypeDialog dialog = new FilteredCheckboxAttributeTypeDialog(
               "Attribute Type Filter Selection", "Select attribute types to search in.");
            dialog.setSelectable(taggableItems);
            dialog.setShowSelectButtons(true);
            dialog.setInput(taggableItems);

            List<AttributeTypeId> selectedElements = new ArrayList<>();
            for (AttributeTypeId type : taggableItems) {
               if (configuration.contains(type)) {
                  selectedElements.add(type);
               }
            }
            dialog.setInitialSelections(selectedElements);

            int result = dialog.open();
            if (result == Window.OK) {
               configuration.clear();
               Collection<AttributeTypeToken> results = dialog.getChecked();
               for (AttributeTypeToken selected : results) {
                  configuration.add(selected);
               }
               if (configuration.isEmpty()) {
                  configuration.add(getDefault());
               }
            }
            Collections.sort(configuration, attrTypeComparator);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      @Override
      public String getConfigToolTip() {
         return "Select to configure attribute type filter.";
      }

      @Override
      public AttributeTypeId[] getConfigData() {
         if (configuration.isEmpty()) {
            configuration.add(getDefault());
         }
         return configuration.toArray(new AttributeTypeId[configuration.size()]);
      }

      public AttributeTypeToken getDefault() {
         return CoreAttributeTypes.Name;
      }

      @Override
      public void loadFrom(String[] items) {
         if (items != null && items.length > 0) {
            configuration.clear();
            for (String entry : items) {
               try {
                  Long id = Long.parseLong(entry);
                  AttributeType type = AttributeTypeManager.getTypeById(id);
                  configuration.add(type);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
         if (configuration.isEmpty()) {
            configuration.add(getDefault());
         }
      }

      @Override
      public String toString() {
         Collection<AttributeTypeId> taggableItems;
         try {
            taggableItems = AttributeTypeManager.getTaggableTypes();
            if (taggableItems.size() == configuration.size()) {
               return "All";
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         return StringUtils.join(configuration, ", ");
      }

      @Override
      public String[] toStore() {
         String[] guids = new String[configuration.size()];
         int index = 0;
         for (AttributeTypeToken type : configuration) {
            guids[index++] = type.getIdString();
         }
         return guids;
      }
   }
   private final static class ArtifactTypeFilterConfigHandler implements IOptionConfigurationHandler<IArtifactType> {
      private final List<IArtifactType> configuration;
      private final Comparator<IArtifactType> artTypeComparator;

      public ArtifactTypeFilterConfigHandler() {
         this.artTypeComparator = new ArtifactTypeComparator();
         this.configuration = new ArrayList<>();
      }

      @Override
      public void configure() {
         try {
            Collection<ArtifactType> artifactTypes = ArtifactTypeManager.getAllTypes();
            FilteredCheckboxArtifactTypeDialog dialog = new FilteredCheckboxArtifactTypeDialog(
               "Artifact Type Filter Selection", "Select artifact types to search in.");
            dialog.setShowSelectButtons(true);
            dialog.setInput(artifactTypes);

            List<IArtifactType> selectedElements = new ArrayList<>();
            for (ArtifactType type : artifactTypes) {
               if (configuration.contains(type)) {
                  selectedElements.add(type);
               }
            }
            dialog.setInitialSelections(selectedElements);

            int result = dialog.open();
            if (result == Window.OK) {
               configuration.clear();
               Collection<IArtifactType> results = dialog.getChecked();
               for (IArtifactType selected : results) {
                  configuration.add(selected);
               }
            }
            Collections.sort(configuration, artTypeComparator);
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
      }

      @Override
      public String getConfigToolTip() {
         return "Select to configure artifact type filter.";
      }

      @Override
      public IArtifactType[] getConfigData() {
         return configuration.toArray(new IArtifactType[configuration.size()]);
      }

      @Override
      public void loadFrom(String[] items) {
         if (items != null && items.length > 0) {
            configuration.clear();
            for (String entry : items) {
               try {
                  Long id = Long.parseLong(entry);
                  ArtifactType type = ArtifactTypeManager.getType(id);
                  configuration.add(type);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
      }

      @Override
      public String toString() {
         Collection<AttributeTypeId> taggableItems;
         try {
            taggableItems = AttributeTypeManager.getTaggableTypes();
            if (taggableItems.size() == configuration.size()) {
               return "All";
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
         }
         return StringUtils.join(configuration, ", ");
      }

      @Override
      public String[] toStore() {
         String[] guids = new String[configuration.size()];
         int index = 0;
         for (IArtifactType type : configuration) {
            guids[index++] = String.valueOf(type.getGuid());
         }
         return guids;
      }
   }

   public void setAttrSearchComposite(SearchComposite attrSearchComposite) {
      this.attrSearchComposite = attrSearchComposite;
   }

}
