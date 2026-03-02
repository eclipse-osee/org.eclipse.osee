/*******************************************************************************
 * Copyright (c) 2021 Boeing.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.bit;

import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.ui.skynet.widgets.EditorData;
import org.eclipse.osee.framework.ui.skynet.widgets.EditorWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XHyperlinkLabelWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XHyperlinkOpenBitTabWidget extends XHyperlinkLabelWidget implements EditorWidget {

   public static WidgetId ID = WidgetIdAts.XHyperlinkOpenBitTabWidget;

   private EditorData editorData;

   public XHyperlinkOpenBitTabWidget() {
      super(ID, "Open Build Impact Table", "", false);
   }

   @Override
   public void handleSelection() {
      WorkflowEditor wfeEditor = (WorkflowEditor) editorData;
      wfeEditor.openBitTab();
   }

   @Override
   public void setEditorData(EditorData editorData) {
      this.editorData = editorData;
   }

}
