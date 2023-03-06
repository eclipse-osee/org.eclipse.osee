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
package org.eclipse.osee.framework.ui.skynet.markedit.edit;

import java.util.HashMap;
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

   private final OmeEditTab omeEditTab;
   private final Map<String, String> nameToMd = new HashMap<>();

   public OmeEditSubToolbar(Composite parent, OmeEditTab omeEditTab) {
      super(parent, SWT.NONE);
      this.omeEditTab = omeEditTab;
      setLayout(new GridLayout(10, false));
      setLayoutData(new GridData());

      nameToMd.put("bold", "<b>bold</b> or **bold**");
      nameToMd.put("bullets", "* one\n* two\n* three");
      nameToMd.put("heading", "## Heading");
      nameToMd.put("tasks",
         "- [x] Completed task\n- [~] Inapplicable task\n- [ ] Incomplete task\n   - [x] Sub-task 1\n   - [~] Sub-task 2\n   - [ ] Sub-task 3\n");
      nameToMd.put("numbered",
         "1. [x] Completed task\n1. [~] Inapplicable task\n1. [ ] Incomplete task\n   1. [x] Sub-task 1\n   1. [~] Sub-task 2\n   1.   [ ] Sub-task 3\n");
      nameToMd.put("link", "[Google](https://www.google.com)");
      nameToMd.put("table",
         "| Tables        | Are           | Cool  |\n| ------------- |:-------------:| -----:|\n| col 3 is      | right-aligned | $1600 |\n| col 2 is      | centered      |   $12 |\n| zebra stripes | are neat      |    $1 |\n\n");
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
