/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.search.widget;

import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.ui.skynet.widgets.XButtonPush;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;

/**
 * @author Donald G. Dunne
 */
public class GenerateBuildMemoWidget {

   public static final String BUILD_MEMO = "Build Memo";
   private final WorldEditorParameterSearchItem searchItem;
   private final String memoName;

   public GenerateBuildMemoWidget(WorldEditorParameterSearchItem searchItem) {
      this(searchItem, BUILD_MEMO);

   }

   public GenerateBuildMemoWidget(WorldEditorParameterSearchItem searchItem, String memoName) {
      this.searchItem = searchItem;
      this.memoName = memoName;
   }

   public void addWidget() {
      addWidget(0);
   }

   public String getGenerateLabel(String memoName) {
      return "Generate " + memoName;
   }

   public String getExportLabel(String memoName) {
      return "Export " + memoName;
   }

   public void addWidget(int beginComposite) {
      searchItem.addWidgetXml(
         "<XWidget xwidgetType=\"XButtonPush\" displayLabel=\"false\" displayName=\"" + getGenerateLabel(
            memoName) + "\" />" //
      );
      searchItem.addWidgetXml(
         "<XWidget xwidgetType=\"XButtonPush\" displayLabel=\"false\" displayName=\"" + getExportLabel(
            memoName) + "\" />" //
      );
   }

   public XButtonPush getWidget() {
      return (XButtonPush) searchItem.getxWidgets().get(getGenerateLabel(memoName));
   }

   public void set(AtsSearchData data) {
      // do nothing
   }

   public void setup(XWidget widget) {
      // do nothing
   }

}
