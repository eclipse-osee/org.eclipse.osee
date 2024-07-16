/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.ArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidgetUtility;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class WfeTitleHeader extends Composite {

   private final IAtsWorkItem workItem;
   private XTextDam titleText;

   public WfeTitleHeader(Composite parent, int style, final IAtsWorkItem workItem, final WorkflowEditor editor, XModifiedListener xModListener) {
      super(parent, style);
      this.workItem = workItem;
      setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
      setLayout(ALayout.getZeroMarginLayout(1, true));
      editor.getToolkit().adapt(this);

      try {
         titleText = new XTextDam("Title");
         titleText.setAttributeType((Artifact) workItem.getStoreObject(), CoreAttributeTypes.Name);
         titleText.createWidgets(this, 1);
         titleText.adaptControls(editor.getToolkit());
         XWidgetUtility.setLabelFontsBold(titleText);
         titleText.addXModifiedListener(xModListener);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         titleText = null;
      }

      refresh();
   }

   public void refresh() {
      titleText.setAttributeType((Artifact) workItem.getStoreObject(), CoreAttributeTypes.Name);
   }

   public XTextDam getTitleText() {
      return titleText;
   }

   public XResultData isXWidgetDirty(XResultData rd) {
      if (titleText != null) {
         if (titleText.isDirty().isTrue()) {
            rd.error("Title is dirty");
         }
      }
      return rd;
   }

   public Result isXWidgetSavable() {
      IStatus status = titleText.isValid();
      if (!status.isOK()) {
         return new Result(false, status.getMessage());
      }
      return Result.TrueResult;
   }

   public void getDirtyIArtifactWidgets(List<ArtifactStoredWidget> artWidgets) {
      if (titleText.isDirty().isTrue()) {
         artWidgets.add(titleText);
      }
   }

   public Collection<XWidget> getXWidgets(ArrayList<XWidget> widgets) {
      widgets.add(titleText);
      return widgets;
   }

}
