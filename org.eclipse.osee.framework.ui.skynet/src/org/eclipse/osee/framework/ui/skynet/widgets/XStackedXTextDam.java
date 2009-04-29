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
package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;

public class XStackedXTextDam extends XStackedWidget<String> implements IArtifactWidget {

   private Artifact artifact;
   private String attributeTypeName;
   private final Map<String, XText> xWidgets;
   private final XModifiedListener xModifiedListener;

   public XStackedXTextDam(String displayLabel) {
      super(displayLabel);
      this.xWidgets = new LinkedHashMap<String, XText>();
      this.artifact = null;
      this.xModifiedListener = new XModifiedListener() {
         public void widgetModified(XWidget widget) {
            notifyXModifiedListeners();
         };
      };
   }

   public void setArtifact(Artifact artifact, String attributeTypeName) throws OseeCoreException {
      this.artifact = artifact;
      this.attributeTypeName = attributeTypeName;
      AttributeType attributeType = AttributeTypeManager.getType(attributeTypeName);

      int minOccurrence = attributeType.getMinOccurrences();
      int maxOccurrence = attributeType.getMaxOccurrences();

      setPageRange(minOccurrence, maxOccurrence);
   }

   @Override
   public void createWidgets(final Composite parent, int horizontalSpan) {
      super.createWidgets(parent, horizontalSpan);
      try {
         for (String value : getStored()) {
            addPage(value);
         }
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex.getLocalizedMessage(), ex);
      }
   }

   public List<String> getInput() {
      List<String> data = new ArrayList<String>();
      for (XText widget : xWidgets.values()) {
         data.add(widget.get());
      }
      return data;
   }

   public Collection<String> getStored() throws OseeCoreException {
      return artifact.getAttributesToStringList(attributeTypeName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#isDirty()
    */
   @Override
   public Result isDirty() throws OseeCoreException {
      try {
         Collection<String> enteredValues = new ArrayList<String>();//getSelected();
         Collection<String> storedValues = getStored();
         if (!Collections.isEqual(enteredValues, storedValues)) {
            return new Result(true, attributeTypeName + " is dirty");
         }
      } catch (NumberFormatException ex) {
         // do nothing
      }
      return Result.FalseResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#revert()
    */
   @Override
   public void revert() throws OseeCoreException {
      setArtifact(artifact, attributeTypeName);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget#saveToArtifact()
    */
   @Override
   public void saveToArtifact() throws OseeCoreException {
      artifact.setAttributeValues(attributeTypeName, getInput());
   }

   @Override
   protected void createPage(String id, Composite parent, String initialInput) {
      if (!xWidgets.containsKey(id)) {
         Label label = new Label(parent, SWT.NONE);
         label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
         label.setText(String.format("Page: %s", id));

         XText xTextWidget = new XTextInternalWidget("");
         xTextWidget.addXTextSpellModifyDictionary(new SkynetSpellModifyDictionary());
         if (Strings.isValid(initialInput)) {
            xTextWidget.setText(initialInput);
         }
         xTextWidget.setFillHorizontally(false);
         xTextWidget.setFillVertically(true);
         xTextWidget.createWidgets(parent, 2, true);
         xWidgets.put(id, xTextWidget);

         xTextWidget.addXModifiedListener(xModifiedListener);
      }
   }

   @Override
   protected void onRemovePage(String id) {
      xWidgets.remove(id);
   }

   private final class XTextInternalWidget extends XText {

      public XTextInternalWidget(String label) {
         super(label);
      }

      protected int getTextStyle() {
         int styleBase = SWT.NONE;
         if (isEditable()) {
            styleBase |= SWT.READ_ONLY;
         }
         return styleBase | (fillVertically ? SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE);
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.ui.skynet.widgets.XText#createWidgets(org.eclipse.swt.widgets.Composite, int, boolean)
       */
      @Override
      public void createWidgets(Composite parent, int horizontalSpan, boolean fillText) {
         super.createWidgets(parent, horizontalSpan, true);
         final GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
         gd.widthHint = 200;
         sText.setLayoutData(gd);
         sText.setWordWrap(true);
         sText.setEditable(isEditable());
         if (!isEditable()) {
            sText.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND));
         }
      }
   }
}
