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
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.DefaultBranchChangedEvent;
import org.eclipse.osee.framework.skynet.core.event.SkynetEventManager;
import org.eclipse.osee.framework.ui.plugin.event.IEventReceiver;
import org.eclipse.osee.framework.ui.skynet.SkynetContributionItem;
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
public class QuickSearchView extends ViewPart implements IActionable, Listener, IEventReceiver {
   public static final String VIEW_ID = "org.eclipse.osee.framework.ui.skynet.QuickSearchView";

   private static final String ENTRY_SEPARATOR = "##";
   private static final String LAST_QUERY_KEY_ID = "lastQuery";
   private static final String QUERY_HISTORY_KEY_ID = "queryHistory";
   private static final String OPTIONS_KEY_ID = "searchOption";

   private static final String[] SEARCH_OPTIONS = new String[] {"Include Deleted"};

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
            memento.putString(OPTIONS_KEY_ID + option.replaceAll(" ", ""), options.get(option).toString());
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
         for (String option : SEARCH_OPTIONS) {
            options.put(option, new Boolean(memento.getString(OPTIONS_KEY_ID + option.replaceAll(" ", ""))));
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

      SkynetContributionItem.addTo(this, true);

      createActions();

      SkynetEventManager.getInstance().register(DefaultBranchChangedEvent.class, this);
      updateWidgetEnablements();

      Composite panel = new Composite(parent, SWT.NONE);
      GridLayout gL = new GridLayout();
      gL.marginHeight = 0;
      gL.marginWidth = 0;
      panel.setLayout(gL);
      panel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

      searchComposite = new SearchComposite(panel, SWT.NONE, SEARCH_OPTIONS);
      searchComposite.addListener(this);
      //      searchComposite.setHelpContexts();
      loadState();
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
         branchLabel.setText("Searching on current default branch \"" + BranchPersistenceManager.getDefaultBranch() + "\"");
      }
   }

   public void handleEvent(Event event) {
      updateWidgetEnablements();
      if (searchComposite != null && searchComposite.isExecuteSearchEvent(event)) {
         NewSearchUI.activateSearchResultView();
         NewSearchUI.runQueryInBackground(new RemoteArtifactSearch(searchComposite.getQuery(),
               BranchPersistenceManager.getDefaultBranch().getBranchId(), searchComposite.getOptions()));
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#onEvent(org.eclipse.osee.framework.ui.plugin.event.Event)
    */
   public void onEvent(org.eclipse.osee.framework.ui.plugin.event.Event event) {
      updateWidgetEnablements();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.plugin.event.IEventReceiver#runOnEventInDisplayThread()
    */
   public boolean runOnEventInDisplayThread() {
      return true;
   }
}
