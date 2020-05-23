/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet.internal;

import org.eclipse.osee.framework.ui.plugin.OseeUiActivator;

public class Activator extends OseeUiActivator {

   private static Activator instance;
   public static final String PLUGIN_ID = "org.eclipse.osee.framework.ui.skynet";
   public static final String CHANGE_REPORT_ATTRIBUTES_PREF =
      "org.eclipse.osee.framework.ui.skynet.changeReportAttributes";
   public static final String ARTIFACT_EXPLORER_ATTRIBUTES_PREF =
      "org.eclipse.osee.framework.ui.skynet.artifactExplorerAttributes";
   public static final String ARTIFACT_SEARCH_RESULTS_ATTRIBUTES_PREF =
      "org.eclipse.osee.framework.ui.skynet.artifactSearchResultsAttributes";

   public Activator() {
      super(PLUGIN_ID);
      instance = this;
   }

   public static Activator getInstance() {
      return instance;
   }

}