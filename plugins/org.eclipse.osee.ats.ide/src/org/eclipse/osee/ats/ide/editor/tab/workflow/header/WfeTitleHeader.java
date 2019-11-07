/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.editor.tab.workflow.header;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.api.IAtsWorkItem;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.event.IWfeEventHandle;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactStoredWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextDam;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Donald G. Dunne
 */
public class WfeTitleHeader extends Composite implements IWfeEventHandle {

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
         titleText.getLabelWidget().setFont(FontManager.getCourierNew12Bold());
         titleText.addXModifiedListener(xModListener);
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
         titleText = null;
      }

      refresh();
      editor.registerEvent(this, AtsAttributeTypes.Title);
   }

   @Override
   public void refresh() {
      // do nothing
   }

   @Override
   public IAtsWorkItem getWorkItem() {
      return workItem;
   }

   public XTextDam getTitleText() {
      return titleText;
   }

   public Result isXWidgetDirty() {
      return titleText.isDirty();
   }

   public Result isXWidgetSavable() {
      IStatus status = titleText.isValid();
      if (!status.isOK()) {
         return new Result(false, status.getMessage());
      }
      return Result.TrueResult;
   }

   public void getDirtyIArtifactWidgets(List<IArtifactStoredWidget> artWidgets) {
      if (titleText.isDirty().isTrue()) {
         artWidgets.add(titleText);
      }
   }

   public Collection<XWidget> getXWidgets(ArrayList<XWidget> widgets) {
      widgets.add(titleText);
      return widgets;
   }

}
