/*********************************************************************
 * Copyright (c) 2020 Boeing
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

package org.eclipse.osee.ats.ide.workflow.transition;

import org.eclipse.osee.ats.api.util.AtsUtil;
import org.eclipse.osee.ats.api.workflow.transition.TransitionResults;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.ui.skynet.results.ResultsEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.HtmlDialog;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Donald G. Dunne
 */
public class TransitionResultsUi {

   private TransitionResultsUi() {
      // Utility class
   }

   public static void report(String name, TransitionResults transResults) {
      String html = transResults.getResultString();
      html = AHTML.textToHtml(html);
      ResultsEditor.open("results", name, AHTML.simplePage(html));
   }

   public static void reportDialog(String name, TransitionResults transResults) {
      if (AtsUtil.isInTest()) {
         return;
      }
      String html = transResults.getResultString();
      final String fHtml = AHTML.textToHtml(html);
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            HtmlDialog dialog = new HtmlDialog(name, name, fHtml);
            dialog.open();
         }
      });
   }

}
