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

import static org.eclipse.osee.framework.core.widget.XOption.BORDER;
import static org.eclipse.osee.framework.core.widget.XOption.FILL_VERT;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.core.widget.validate.IValueValidator;
import org.eclipse.osee.framework.core.widget.validate.XXNoOpValidator;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.util.OseeDictionary;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XXTextWidgetMenu;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.XWidgetUtility;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XTextUrlListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.spell.SkynetSpellModifyDictionary;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.spell.XTextSpellCheckListener;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.spell.XTextSpellModifyDictionary;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;

/**
 * Widget to handle a single or multi-line text box. This should NOT need to be extended for different cases. Options
 * can be provided through WidgetBuilder or default XWidget methods.
 *
 * @author Donald G. Dunne
 */
public abstract class XXAbstractSTextWidget extends XXAbstractBaseXWidget<String> {

   private static XTextSpellModifyDictionary modDict;

   private int maxTextChars = 0;
   private boolean spellCheck = true;
   private int width = 0;
   private int height = 0;
   private XTextSpellCheckListener spellCheckListener;
   private XTextUrlListener urlListener;
   private Font font;
   private boolean dynamicallyCreated = false;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXAbstractSTextWidget(WidgetId widgetId, String label) {
      super(widgetId, label);
   }

   //////////////////////////////////////////
   // UI Construction
   //////////////////////////////////////////

   @Override
   protected void createValueWidget() {
      try {
         valueSText = new StyledText(comp, getTextStyle());
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.grabExcessHorizontalSpace = true;
         gd.horizontalAlignment = GridData.FILL;
         if (isFillVertically()) {
            gd.grabExcessVerticalSpace = true;
            gd.verticalAlignment = GridData.FILL;
            // widthHint constrains the preferred width so SWT.WRAP can take effect
            gd.widthHint = 100;
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
         valueSText.setLayoutData(gd);
         valueSText.setMenu(XXTextWidgetMenu.getDefaultMenu(valueSText));
         // Add scroll propagation for multi-line text widgets
         if (isFillVertically()) {
            XWidgetUtility.addScrollPropagation(valueSText);
         }
         if (is(XOption.INLINE_EDIT)) {
            // Inline edit requires setSelected and validate on each keystroke
            // One of the reasons for going to hyperlink-edit
            valueSText.addModifyListener(new ModifyListener() {
               @Override
               public void modifyText(ModifyEvent e) {
                  if (Widgets.isAccessible(valueSText)) {
                     String sText = valueSText.getText();
                     if (Strings.isValid(sText)) {
                        setSelected(sText, false);
                     } else {
                        clear(false);
                     }
                     // because this is in-line edit, validate
                     validate();
                  }
                  if (isListenerRefresh()) {
                     notifyXModifiedListeners();
                  }
               }
            });
         }
         if (widData.getDefaultValue() instanceof String) {
            setSelected((String) widData.getDefaultValue());
         }
         if (spellCheck && isNot(XOption.NO_SPELL)) {
            spellCheckListener = new XTextSpellCheckListener(this, valueSText, OseeDictionary.getInstance());
            valueSText.addModifyListener(spellCheckListener);
            if (modDict == null) {
               modDict = new SkynetSpellModifyDictionary();
               spellCheckListener.addXTextSpellModifyDictionary(modDict);
            }
         }
         urlListener = new XTextUrlListener(valueSText);
         valueSText.addModifyListener(urlListener);
         if (width != 0 && height != 0) {
            valueSText.setSize(width, height);
         }
         if (maxTextChars > 0) {
            valueSText.setTextLimit(maxTextChars);
         }
         validate();
         valueSText.setEditable(widData.is(XOption.INLINE_EDIT));
         if (!widData.is(XOption.INLINE_EDIT)) {
            if (isFillVertically()) {
               valueSText.setBackground(comp.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
            } else {
               valueSText.setBackground(comp.getBackground());
            }
            valueSText.setCaret(null);
         }
         if (font != null) {
            valueSText.setFont(font);
         }
         comp.layout();
      } finally {
         setNotificationsAllowed(true);
      }
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public String getSentinel() {
      return "";
   }

   //////////////////////////////////////////
   // Validation
   //////////////////////////////////////////

   @Override
   protected IValueValidator getValidator() {
      return XXNoOpValidator.instance;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   public void append(String text) {
      setSelected(getSelectedFirst() + text);
   }

   protected int getTextStyle() {
      int styleBase;
      if (widData.is(XOption.INLINE_EDIT) && widData.isAny(XOption.FILL_BOTH, FILL_VERT, BORDER)) {
         styleBase = SWT.BORDER;
      } else if (!widData.is(XOption.INLINE_EDIT) && isFillVertically()) {
         styleBase = SWT.BORDER;
      } else {
         styleBase = SWT.NONE;
      }
      return styleBase | (isFillVertically() ? SWT.MULTI | SWT.WRAP | SWT.H_SCROLL | SWT.V_SCROLL : SWT.SINGLE);
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
      return valueSText;
   }

   public String get() {
      return getSelectedFirst();
   }

   public void selectAll() {
      valueSText.selectAll();
   }

}
