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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.StringFormat;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.OseeContributionItem;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.ats.IActionable;
import org.eclipse.osee.framework.ui.skynet.ats.OseeAts;
import org.eclipse.osee.framework.ui.skynet.panels.SearchComposite;
import org.eclipse.osee.framework.ui.skynet.panels.SearchComposite.IOptionConfigurationHandler;
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.AttributeTypeCheckTreeDialog;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Robert A. Fisher
 * @author Ryan D. Brooks
 */
public class QuickSearchView extends ViewPart implements IActionable, Listener, IBranchEventListener {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.QuickSearchView";

   private static final String ENTRY_SEPARATOR = "##";
   private static final String LAST_QUERY_KEY_ID = "lastQuery";
   private static final String QUERY_HISTORY_KEY_ID = "queryHistory";
   private static final String OPTIONS_KEY_ID = "searchOption";
   private static final String OPTION_CONFIGS_KEY_ID = "optionConfigs";

   private static final String MAIN_HELP_CONTEXT = "quick_search_text";

   private enum SearchOption {
      Attribute_Type_Filter("quick_search_attribute_type_filter", "When selected, searches only through the artifact's containing the selected attribute types.", true, new AttributeTypeFilterConfigHandler()),
      By_Id("quick_search_by_id_option", "When selected, searches by GUID(s) or HRID(s). Accepts comma or space separated ids.", true),
      Match_Word_Order("quick_search_word_order_option", "When selected, match search string word order.", false),
      Include_Deleted("quick_search_deleted_option", "When selected, does not filter out deleted artifacts from search results.", false),
      All_Match_Locations("quick_search_all_match_locations_option", "When selected, returns all match locations. NOTE: If the search matches many artifact, performance may be slow.", false);

      private static String[] labels = null;
      private static String[] mutuallyExclusive = null;
      private static Map<String, IOptionConfigurationHandler> configurable = null;
      private final String helpContext;
      private final String toolTip;
      private final boolean isRadio;
      private final IOptionConfigurationHandler configHandler;

      SearchOption(String helpContext, String toolTip, boolean isRadio) {
         this(helpContext, toolTip, isRadio, null);
      }

      SearchOption(String helpContext, String toolTip, boolean isRadio, IOptionConfigurationHandler configHandler) {
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

      public IOptionConfigurationHandler getConfigHandler() {
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

      public static Map<String, IOptionConfigurationHandler> getConfigurableOptions() {
         if (configurable == null) {
            configurable = new HashMap<String, IOptionConfigurationHandler>();
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

   private Label branchLabel;
   private SearchComposite searchComposite;
   private IMemento memento;

   @Override
   public void init(IViewSite site, IMemento memento) throws PartInitException {
      super.init(site, memento);
      if (memento != null) {
         this.memento = memento;
      }
   }

   @Override
   public void saveState(IMemento memento) {
      if (searchComposite != null && memento != null) {
         memento.putString(LAST_QUERY_KEY_ID, searchComposite.getQuery());
         Map<String, Boolean> options = searchComposite.getOptions();
         for (String option : options.keySet()) {
            memento.putString(OPTIONS_KEY_ID + option.replaceAll(" ", "_"), options.get(option).toString());
         }
         StringBuilder builder = new StringBuilder();
         String[] queries = searchComposite.getQueryHistory();
         for (int index = 0; index < queries.length; index++) {
            try {
               builder.append(URLEncoder.encode(queries[index], "UTF-8"));
               if (index + 1 < queries.length) {
                  builder.append(ENTRY_SEPARATOR);
               }
            } catch (UnsupportedEncodingException ex) {
               // DO NOTHING
            }
         }
         memento.putString(QUERY_HISTORY_KEY_ID, builder.toString());

         Map<String, String[]> data = searchComposite.getConfigurations();
         for (String key : data.keySet()) {
            String[] config = data.get(key);
            if (config != null && config.length > 0) {
               memento.putString(OPTION_CONFIGS_KEY_ID + key.replaceAll(" ", "_"), StringFormat.separateWith(config,
                     ENTRY_SEPARATOR));
            }
         }
      }
   }

   private void loadState() {
      if (searchComposite != null && memento != null) {
         String lastQuery = memento.getString(LAST_QUERY_KEY_ID);

         Map<String, Boolean> options = new HashMap<String, Boolean>();
         Map<String, String[]> configs = new HashMap<String, String[]>();

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

         List<String> queries = new ArrayList<String>();
         String rawHistory = memento.getString(QUERY_HISTORY_KEY_ID);
         if (rawHistory != null) {
            String[] values = rawHistory.split(ENTRY_SEPARATOR);
            for (String value : values) {
               try {
                  queries.add(URLDecoder.decode(value, "UTF-8"));
               } catch (UnsupportedEncodingException ex) {
                  // DO NOTHING
               }
            }
         }
         searchComposite.restoreWidgetValues(queries, lastQuery, options, configs);
      }
   }

   @Override
   public void createPartControl(Composite parent) {
      if (!DbConnectionExceptionComposite.dbConnectionIsOk(parent)) return;

      parent.setLayout(new GridLayout());
      parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

      branchLabel = new Label(parent, SWT.NONE);
      branchLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      OseeContributionItem.addTo(this, true);

      createActions();

      OseeEventManager.addListener(this);
      updateWidgetEnablements();

      Composite panel = new Composite(parent, SWT.NONE);
      GridLayout gL = new GridLayout();
      gL.marginHeight = 0;
      gL.marginWidth = 0;
      panel.setLayout(gL);
      panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      searchComposite =
            new SearchComposite(panel, SWT.NONE, SearchOption.asLabels(), SearchOption.getMutuallyExclusiveOptions(),
                  SearchOption.getConfigurableOptions());
      searchComposite.addListener(this);

      loadState();

      searchComposite.setHelpContext(MAIN_HELP_CONTEXT);
      for (SearchOption option : SearchOption.values()) {
         searchComposite.setHelpContextForOption(option.asLabel(), option.getHelpContext());
         searchComposite.setToolTipForOption(option.asLabel(), option.getToolTip());
      }
   }

   private void createActions() {
      OseeAts.addBugToViewToolbar(this, this, SkynetGuiPlugin.getInstance(), VIEW_ID, "Quick Search");
   }

   @Override
   public void setFocus() {
      if (searchComposite != null) searchComposite.setFocus();
   }

   public String getActionDescription() {
      return "";
   }

   private void updateWidgetEnablements() {
      if (branchLabel != null && branchLabel.isDisposed() != true) {
         branchLabel.setText("Searching on current default branch \"" + BranchManager.getDefaultBranch() + "\"");
      }
   }

   public void handleEvent(Event event) {
      updateWidgetEnablements();
      if (searchComposite != null) {
         if (searchComposite.isExecuteSearchEvent(event)) {
            NewSearchUI.activateSearchResultView();
            if (searchComposite.isOptionSelected(SearchOption.By_Id.asLabel())) {
               NewSearchUI.runQueryInBackground(new IdArtifactSearch(searchComposite.getQuery(),
                     BranchManager.getDefaultBranch(),
                     searchComposite.isOptionSelected(SearchOption.Include_Deleted.asLabel())));
            } else {
               NewSearchUI.runQueryInBackground(new RemoteArtifactSearch(
                     searchComposite.getQuery(),
                     BranchManager.getDefaultBranch(),
                     searchComposite.isOptionSelected(SearchOption.Include_Deleted.asLabel()),
                     searchComposite.isOptionSelected(SearchOption.Match_Word_Order.asLabel()),
                     searchComposite.isOptionSelected(SearchOption.All_Match_Locations.asLabel()),
                     searchComposite.isOptionSelected(SearchOption.Attribute_Type_Filter.asLabel()) ? searchComposite.getConfiguration(SearchOption.Attribute_Type_Filter.asLabel()) : null));
            }
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.ui.part.WorkbenchPart#dispose()
    */
   @Override
   public void dispose() {
      OseeEventManager.removeListener(this);
      super.dispose();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.ui.plugin.event.Sender, org.eclipse.osee.framework.skynet.core.artifact.BranchModType, int)
    */
   @Override
   public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      if (branchModType == BranchEventType.DefaultBranchChanged) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               updateWidgetEnablements();
            }
         });
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.eventx.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.ui.plugin.event.Sender)
    */
   @Override
   public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
   }

   private final static class AttributeTypeFilterConfigHandler implements IOptionConfigurationHandler {
      private List<String> configuration;

      public AttributeTypeFilterConfigHandler() {
         this.configuration = new ArrayList<String>();
         this.configuration.add(getDefault());
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.panels.SearchComposite.IOptionConfigurationHandler#configure()
       */
      @Override
      public void configure() {
         try {
            Collection<AttributeType> taggableItems = AttributeTypeManager.getTaggableTypes();
            AttributeTypeCheckTreeDialog dialog = new AttributeTypeCheckTreeDialog(taggableItems);
            dialog.setTitle("Attribute Type Filter Selection");
            dialog.setMessage("Select attribute types to search in.");

            List<AttributeType> selectedElements = new ArrayList<AttributeType>();
            if (configuration.contains("All")) {
               selectedElements.addAll(taggableItems);
            } else {
               for (AttributeType type : taggableItems) {
                  if (configuration.contains(type.getName())) {
                     selectedElements.add(type);
                  }
               }
            }
            dialog.setInitialElementSelections(selectedElements);

            int result = dialog.open();
            if (result == Window.OK) {
               configuration.clear();
               Collection<AttributeType> results = dialog.getSelection();
               if (org.eclipse.osee.framework.jdk.core.util.Collections.setComplement(taggableItems, results).isEmpty()) {
                  // All were selected
                  configuration.add("All");
               } else {
                  for (AttributeType selected : results) {
                     configuration.add(selected.getName());
                  }
                  if (configuration.isEmpty()) {
                     configuration.add(getDefault());
                  }
               }
            }
            Collections.sort(configuration);
         } catch (OseeCoreException ex) {
            OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.panels.SearchComposite.IOptionConfigurationHandler#getConfigToolTip()
       */
      @Override
      public String getConfigToolTip() {
         return "Select to configure attribute type filter.";
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.panels.SearchComposite.IOptionConfigurationHandler#getConfiguration()
       */
      @Override
      public String[] getConfiguration() {
         if (configuration.isEmpty()) {
            configuration.add(getDefault());
         }
         return configuration.toArray(new String[configuration.size()]);
      }

      public String getDefault() {
         return "Name";
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.panels.SearchComposite.IOptionConfigurationHandler#setConfiguration(java.lang.String[])
       */
      @Override
      public void setConfiguration(String[] items) {
         if (items != null) {
            configuration.clear();
            for (String entry : items) {
               configuration.add(entry);
            }
         }
         if (configuration.isEmpty()) {
            configuration.add(getDefault());
         }
      }

   }
}
