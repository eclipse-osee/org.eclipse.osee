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

package org.eclipse.osee.framework.ui.skynet.widgets.xx;

import java.util.Collection;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.DisplayHint;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.util.OseeDictionary;
import org.eclipse.osee.framework.ui.skynet.widgets.SkynetSpellModifyDictionary;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextSpellCheckListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextSpellModifyDictionary;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextUrlListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XTextWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.osgi.service.component.annotations.Component;

/**
 * Widget to handle a single or multi-line text box. This should NOT need to be extended for different cases. Options
 * can be provided through WidgetBuilder or default XWidget methods.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXTextWidget extends XAbstractXXWidget<String> {

   public static final WidgetId ID = WidgetId.XXTextWidget;
   protected StyledText sText; // Contains visible representation of text
   private int maxTextChars = 0;
   private boolean spellCheck = true;
   private int width = 0;
   private int height = 0;
   private XTextSpellCheckListener spellCheckListener;
   private XTextUrlListener urlListener;
   private static XTextSpellModifyDictionary modDict;
   private Font font;
   private boolean dynamicallyCreated = false;
   private final boolean fillText;

   public XXTextWidget() {
      this(ID, "", true);
   }

   public XXTextWidget(String label) {
      this(ID, label, true);
   }

   public XXTextWidget(WidgetId widgetId, String label) {
      this(widgetId, label, true);
   }

   public XXTextWidget(WidgetId widgetId, String label, boolean fillText) {
      super(widgetId, label);
      this.fillText = fillText;
   }

   @Override
   protected String getSentinel() {
      return "";
   }

   @Override
   protected boolean isTextWidget() {
      return true;
   }

   @Override
   public Collection<String> getSelected() {
      if (hasArtifact() && getAttributeType().isValid()) {
         selected = getArtifact().getAttributesToStringList(getAttributeType());
      }
      return super.getSelected();
   }

   @Override
   public boolean handleSelection() {
      if (hasArtifact() && getAttributeType().isValid()) {
         boolean singleLine = !getAttributeType().hasDisplayHint(DisplayHint.MultiLine);
         if (singleLine) {
            EntryDialog dialog = new EntryDialog("Enter " + getLabel(), "Enter " + getLabel());
            dialog.setEntry(getSelectedFirst());
            if (dialog.open() == Window.OK) {
               String newValue = dialog.getEntry();
               if (Strings.isInvalid(newValue)) {
                  AWorkbench.popup("New " + getLabel() + " must be valid string.");
                  return false;
               }
               setSelected(newValue);
               handleSelectedPersist();
            }
         }
         return true;
      }
      return false;
   }

   public void set(String text) {
      if (text == null) {
         selected.clear();
      } else {
         setSelected(text);
      }
      updateTextWidget();
   }

   public void set(XTextWidget text) {
      set(text.get());
   }

   public void append(String text) {
      setSelected(getSelectedFirst() + text);
      updateTextWidget();
   }

   @Override
   public void refresh() {
      updateTextWidget();
   }

   protected int getTextStyle() {
      int styleBase = SWT.BORDER;
      if (isEditable()) {
         styleBase |= SWT.READ_ONLY;
      }
      return styleBase | (isFillVertically() ? SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE);
   }

   @Override
   protected void createValueWidget() {
      try {
         sText = new StyledText(comp, getTextStyle());

         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.grabExcessHorizontalSpace = true;
         gd.horizontalAlignment = GridData.FILL;
         if (isFillVertically()) {
            gd.grabExcessVerticalSpace = true;
            gd.verticalAlignment = GridData.FILL;
            if (height > 0) {
               gd.heightHint = height;
            }
            if (dynamicallyCreated) {
               if (height > 0) {
                  gd.minimumHeight = height;
               } else {
                  gd.minimumHeight = 60;
               }
            }
         }
         if (isFillHorizontally() && dynamicallyCreated) {
            gd.grabExcessHorizontalSpace = true;
            gd.minimumWidth = 60;
         }

         sText.setLayoutData(gd);
         sText.setMenu(XXTextWidgetMenu.getDefaultMenu(sText));
         sText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
               if (Widgets.isAccessible(sText)) {
                  setSelected(sText.getText());
                  notifyXModifiedListeners();
               }
            }
         });
         if (Strings.isValidString(widData.getDefaultValueStr())) {
            setSelected(widData.getDefaultValueStr());
         }
         if (spellCheck) {
            spellCheckListener = new XTextSpellCheckListener(sText, OseeDictionary.getInstance());
            sText.addModifyListener(spellCheckListener);
            if (modDict == null) {
               modDict = new SkynetSpellModifyDictionary();
               spellCheckListener.addXTextSpellModifyDictionary(modDict);
            }
         }
         urlListener = new XTextUrlListener(sText);
         sText.addModifyListener(urlListener);

         if (width != 0 && height != 0) {
            sText.setSize(width, height);
         }

         if (maxTextChars > 0) {
            sText.setTextLimit(maxTextChars);
         }
         if (fillText) {
            updateTextWidget();
         }
         validate();
         sText.setEditable(false);
         if (font != null) {
            sText.setFont(font);
         }
         comp.layout();
      } finally {
         setNotificationsAllowed(true);
      }

   }

   protected void updateTextWidget() {
      if (Widgets.isAccessible(sText)) {
         if (!getSelectedFirst().equals(sText.getText())) {
            // Disable Listeners so not to fill Undo List
            sText.setText(getSelectedFirst());
            // Re-enable Listeners
            validate();
         }
      }
   }

   public void updateUiEnablementFromValue(String value) {
      if (value.length() > 1000) {
         spellCheck = false;

      }
   }

   public int getMaxTextChars() {
      return maxTextChars;
   }

   public void setMaxTextChars(int maxTextChars) {
      this.maxTextChars = maxTextChars;
   }

   public boolean isSpellCheck() {
      return spellCheck;
   }

   public void setSpellCheck(boolean spellCheck) {
      this.spellCheck = spellCheck;
   }

   public int getWidth() {
      return width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public XTextSpellCheckListener getSpellPaintListener() {
      return spellCheckListener;
   }

   public void setSpellPaintListener(XTextSpellCheckListener spellPaintListener) {
      this.spellCheckListener = spellPaintListener;
   }

   public boolean isDynamicallyCreated() {
      return dynamicallyCreated;
   }

   public void setDynamicallyCreated(boolean dynamicallyCreated) {
      this.dynamicallyCreated = dynamicallyCreated;
   }

   public Font getFont() {
      return font;
   }

   public void setFont(Font font) {
      this.font = font;
   }

   public StyledText getStyledText() {
      return sText;
   }

   public String get() {
      return getSelectedFirst();
   }

   public void selectAll() {
      sText.selectAll();
   }

}
