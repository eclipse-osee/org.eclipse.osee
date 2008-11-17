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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
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
import org.eclipse.osee.framework.ui.skynet.util.DbConnectionExceptionComposite;
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

   private static final String MAIN_HELP_CONTEXT = "quick_search_text";

   private enum SearchOption {
      Name_Only("quick_search_name_option", "When selected, searches only through the artifact's name attribute field.", true),
      By_Id("quick_search_by_id_option", "When selected, searches by GUID(s) or HRID(s). Accepts comma or space separated ids.", true),
      Match_Word_Order("quick_search_word_order_option", "When selected, match search string word order.", false),
      Include_Deleted("quick_search_deleted_option", "When selected, does not filter out deleted artifacts from search results.", false);

      private static String[] labels = null;
      private static String[] mutuallyExclusive = null;
      private final String helpContext;
      private final String toolTip;
      private final boolean isRadio;

      SearchOption(String helpContext, String toolTip, boolean isRadio) {
         this.helpContext = "";
         this.toolTip = toolTip;
         this.isRadio = isRadio;
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
      }
   }

   private void loadState() {
      if (searchComposite != null && memento != null) {
         String lastQuery = memento.getString(LAST_QUERY_KEY_ID);

         Map<String, Boolean> options = new HashMap<String, Boolean>();
         for (SearchOption option : SearchOption.values()) {
            options.put(option.asLabel(), new Boolean(memento.getString(OPTIONS_KEY_ID + option.name())));
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
         searchComposite.restoreWidgetValues(queries, lastQuery, options);
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
            new SearchComposite(panel, SWT.NONE, SearchOption.asLabels(), SearchOption.getMutuallyExclusiveOptions());
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
               NewSearchUI.runQueryInBackground(new RemoteArtifactSearch(searchComposite.getQuery(),
                     BranchManager.getDefaultBranch(),
                     searchComposite.isOptionSelected(SearchOption.Name_Only.asLabel()),
                     searchComposite.isOptionSelected(SearchOption.Include_Deleted.asLabel()),
                     searchComposite.isOptionSelected(SearchOption.Match_Word_Order.asLabel())));
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
}
