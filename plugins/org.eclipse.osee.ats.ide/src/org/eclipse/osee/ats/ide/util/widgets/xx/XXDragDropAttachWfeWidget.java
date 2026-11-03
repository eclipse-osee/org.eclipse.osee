/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.ats.ide.util.widgets.xx;

import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.editor.tab.workflow.header.WfeDragAndDrop;
import org.eclipse.osee.ats.ide.util.widgets.util.XXWorkItemData;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXLabelValueWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXDragDropAttachWfeWidget extends XXLabelValueWidget {

   public static final WidgetId ID = WidgetIdAts.XXDragDropAttachWfeWidget;
   private static Color DROP_BORDER = null;
   private static Color DROP_BACKGROUND = null;
   private static String TOOLTIP = "- Drag file from desktop to import and attach.\n" //
      + "- OR, Click here and drag to another Workflow to relate.\n" //
      + "- OR, Drop here from another Workflow / Artifact (on Common) to relate.";
   private static final String DROP_PROMPT = "Drag From or Drop Here";

   protected XXWorkItemData xxWid = XXWorkItemData.SENTINEL;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXDragDropAttachWfeWidget() {
      super(ID, "Drag/Drop Related Here", " ");
      set(XOption.READ);
      setToolTip(TOOLTIP);
      set(XOption.NO_CLEAR);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createValueWidget() {
      if (xxWid.isWorkItem()) {
         // Distinctive "drop zone" box: a composite with a dashed rounded border, a subtle background,
         // and centered prompt text. The whole box is the drag/drop target.
         final Composite dropZone = new Composite(comp, SWT.NONE);
         GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
         gd.widthHint = 220;
         gd.heightHint = 28;
         dropZone.setLayoutData(gd);
         dropZone.setBackground(getDropBackgroundColor());
         dropZone.setToolTipText(TOOLTIP);

         // Paint the dashed rounded border and the centered prompt text directly so the whole box is a single
         // drop target with no child control intercepting drop events.
         dropZone.addPaintListener(new PaintListener() {
            @Override
            public void paintControl(PaintEvent e) {
               Rectangle area = dropZone.getClientArea();
               e.gc.setForeground(getDropBorderColor());
               e.gc.setLineWidth(1);
               e.gc.setLineStyle(SWT.LINE_DASH);
               e.gc.drawRoundRectangle(area.x, area.y, area.width - 1, area.height - 1, 8, 8);
               e.gc.setLineStyle(SWT.LINE_SOLID);
               Point textSize = e.gc.textExtent(DROP_PROMPT);
               int textX = area.x + (area.width - textSize.x) / 2;
               int textY = area.y + (area.height - textSize.y) / 2;
               e.gc.drawText(DROP_PROMPT, textX, textY, true);
            }
         });

         new WfeDragAndDrop(dropZone, (AbstractWorkflowArtifact) getArtifact(), WorkflowEditor.EDITOR_ID);
      }
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getDisplayValue() {
      return "                                   ";
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   private static Color getDropBorderColor() {
      if (DROP_BORDER == null) {
         DROP_BORDER = Displays.getColor(90, 130, 190);
      }
      return DROP_BORDER;
   }

   private static Color getDropBackgroundColor() {
      if (DROP_BACKGROUND == null) {
         DROP_BACKGROUND = Displays.getColor(235, 241, 250);
      }
      return DROP_BACKGROUND;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      xxWid = XXWorkItemData.get(artifact);
      super.setArtifact(artifact);
   }

}
