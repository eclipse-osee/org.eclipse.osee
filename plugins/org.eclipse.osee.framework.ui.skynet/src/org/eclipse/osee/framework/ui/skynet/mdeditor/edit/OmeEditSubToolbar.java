/*********************************************************************
 * Copyright (c) 2023 Boeing
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
package org.eclipse.osee.framework.ui.skynet.mdeditor.edit;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class OmeEditSubToolbar extends Composite {

   private static final String BOLD_TIP = "<b>bold</b> or **bold**";
   private static final String UNDERLINE_TIP = "<u>underline</u>";
   private static final String ITALICS_TIP = "<i>italics</i> or *italics*";
   private static final String BULLET_LIST_TIP = "* one\n* one\n\t* two\n\t* two\n\t\t* three\n\t\t* three";
   private static final String NUMBER_LIST_TIP = "1. one\n1. one\n\t1. two\n\t1. two\n\t\t1. three\n\t\t1. three";
   private static final String TASK_LIST_TIP =
      "- [x] Complete task 1\n- [ ] Incomplete task 2\n\t- [x] Complete sub-task 1\n\t- [ ] Incomplete sub-task 2";
   private static final String HEADING_TIP = "## Heading";
   private static final String TABLE_TIP =
      "| Col 1 | Col 2 | Col 3 |\n| :--- | :---: | ---: |\n| col 1 is | left-aligned | $1600 |\n| col 2 is | centered | $12 |\n| col 3 is | right-aligned | $1 |";
   private static final String LINK_TIP = "[Google](https://www.google.com)";

   private final OmeEditTab omeEditTab;
   private final Map<String, String> nameToMd = new LinkedHashMap<>();

   public OmeEditSubToolbar(Composite parent, OmeEditTab omeEditTab) {
      super(parent, SWT.NONE);
      this.omeEditTab = omeEditTab;
      setLayout(new GridLayout(10, false));
      setLayoutData(new GridData());

      nameToMd.put("bold", BOLD_TIP);
      nameToMd.put("underline", UNDERLINE_TIP);
      nameToMd.put("italics", ITALICS_TIP);
      nameToMd.put("bullet list", BULLET_LIST_TIP);
      nameToMd.put("number list", NUMBER_LIST_TIP);
      nameToMd.put("task list", TASK_LIST_TIP);
      nameToMd.put("heading", HEADING_TIP);
      nameToMd.put("table", TABLE_TIP);
      nameToMd.put("link", LINK_TIP);
   }

   public void create() {

      for (Entry<String, String> entry : nameToMd.entrySet()) {
         Button bold = new Button(this, SWT.PUSH);
         bold.setToolTipText(String.format("Insert \"%s\" Markdown", entry.getKey()));
         bold.setText(entry.getKey());
         bold.addSelectionListener(new SelectionAdapter() {

            @Override
            public void widgetSelected(SelectionEvent e) {
               omeEditTab.appendText(entry.getValue());
            }
         });
      }

   }

}
