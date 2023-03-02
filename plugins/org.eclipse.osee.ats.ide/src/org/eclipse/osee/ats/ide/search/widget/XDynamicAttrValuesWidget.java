/*******************************************************************************
 * Copyright (c) 2022 Boeing.
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
package org.eclipse.osee.ats.ide.search.widget;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.api.util.AttributeValue;
import org.eclipse.osee.ats.api.util.AttributeValues;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.search.AttributeTypeFilteredDialog;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorWidget;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.FontManager;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.osee.framework.ui.swt.Widgets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Donald G. Dunne
 */
public class XDynamicAttrValuesWidget extends XWidget implements WorldEditorWidget {

   private Label label;
   private AttributeValues attrValues = new AttributeValues();
   private Composite comp;
   private final Map<AttributeTypeToken, Hyperlink> attrTypeToValuesLabel = new HashMap<>();
   private final Map<AttributeTypeToken, Composite> attrTypeToComp = new HashMap<>();
   private WorldEditor editor;
   private Composite attrComp;

   public XDynamicAttrValuesWidget() {
      super("Attribute Values");
   }

   @Override
   public Control getControl() {
      return null;
   }

   @Override
   protected void createControls(Composite parent, int horizontalSpan) {

      comp = new Composite(parent, SWT.NONE);
      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      comp.setLayoutData(gd);
      comp.setLayout(ALayout.getZeroMarginLayout(1, false));
      comp.setBackground(parent.getBackground());

      Composite labelComp = new Composite(comp, SWT.NONE);
      GridData layoutData = new GridData(SWT.LEFT, SWT.NONE, false, false);
      layoutData.horizontalSpan = 2;
      labelComp.setLayoutData(layoutData);
      labelComp.setLayout(ALayout.getZeroMarginLayout(2, false));
      labelComp.setBackground(parent.getBackground());

      label = new Label(labelComp, SWT.NONE);
      label.setText("Attribute Values: ");
      label.setBackground(Displays.getSystemColor(SWT.COLOR_WHITE));
      label.setToolTipText("Select \"Add\" to include other attribute types and their values in search criteria.");

      Hyperlink link = toolkit.createHyperlink(labelComp, "Add", SWT.NONE);
      link.setToolTipText("Select \"Add\" to include other attribute types and their values in search criteria.");
      link.addHyperlinkListener(new HyperlinkAdapter() {

         @Override
         public void linkActivated(HyperlinkEvent e) {
            List<AttributeTypeToken> searchableAttrTypes = new ArrayList<>();
            for (AttributeTypeToken type : AtsApiService.get().tokenService().getAttributeTypes()) {
               if (type.isTaggable()) {
                  searchableAttrTypes.add(type);
               }
            }
            AttributeTypeFilteredDialog diag =
               new AttributeTypeFilteredDialog(Collections.castAll(searchableAttrTypes));
            if (diag.open() == Window.OK) {
               if (diag.isNonExists()) {
                  attrValues.addAttrValue(diag.getSelectedElement(), true);
               } else {
                  attrValues.addAttrValue(diag.getSelected());
               }
               createUpdateAttached();
            }
         }
      });

      createUpdateAttached();

   }

   private void createUpdateAttached() {

      // Remove all for redraw
      for (Entry<AttributeTypeToken, Composite> attrVal : attrTypeToComp.entrySet()) {
         Composite comp = attrVal.getValue();
         if (Widgets.isAccessible(comp)) {
            comp.dispose();
         }
      }

      if (!Widgets.isAccessible(attrComp)) {
         attrComp = new Composite(comp, SWT.NONE);
         GridData gd = new GridData(GridData.FILL_HORIZONTAL);
         gd.horizontalIndent = 30;
         attrComp.setLayoutData(gd);
         attrComp.setLayout(ALayout.getZeroMarginLayout(2, false));
         attrComp.setBackground(comp.getBackground());
      }

      List<AttributeValue> attrVals = attrValues.getAttributes();
      for (AttributeValue attrValue : attrVals) {
         try {

            Composite lComp = new Composite(attrComp, SWT.NONE);
            attrTypeToComp.put(attrValue.getAttrType(), lComp);
            lComp.setLayout(new GridLayout(3, false));
            GridData gd = new GridData(GridData.FILL_HORIZONTAL);
            lComp.setLayoutData(gd);
            lComp.setBackground(attrComp.getBackground());

            Label typeLabel = new Label(lComp, SWT.NONE);
            typeLabel.setText(attrValue.getAttrType().getName() + ":   ");
            typeLabel.setFont(FontManager.getCourierNew12Bold());
            typeLabel.setBackground(attrComp.getBackground());
            final AttributeValue fAttrValue = attrValue;
            if (attrValue.isNotExists()) {
               toolkit.createLabel(lComp, "does not exist");
               attrTypeToValuesLabel.put(attrValue.getAttrType(), null);
            } else {
               Hyperlink valuesLink = toolkit.createHyperlink(lComp, "select to set values", SWT.NONE);
               valuesLink.setToolTipText("select to set values");
               valuesLink.setBackground(attrComp.getBackground());
               if (attrValue.hasValues()) {
                  valuesLink.setText(attrValue.getValues().iterator().next());
               }
               valuesLink.addListener(SWT.MouseUp, new Listener() {
                  @Override
                  public void handleEvent(Event event) {
                     handleEditValues(fAttrValue);
                  }
               });
               attrTypeToValuesLabel.put(attrValue.getAttrType(), valuesLink);
            }

            Label deleteLabel = new Label(lComp, SWT.NONE);
            deleteLabel.setImage(ImageManager.getImage(FrameworkImage.X_RED));
            deleteLabel.setBackground(attrComp.getBackground());
            deleteLabel.addListener(SWT.MouseUp, new Listener() {

               @Override
               public void handleEvent(Event event) {
                  handleDeleteEntry(fAttrValue);
               }

            });

         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.WARNING, "Error showing value " + attrValue.toString(), ex);
         }
      }

      updateLayout();

   }

   private void updateLayout() {
      comp.layout(true, true);
      comp.getParent().layout(true, true);
      if (editor != null) {
         editor.reflowParameterSection();
      }
   }

   private void handleDeleteEntry(AttributeValue attrValue) {
      attrValues.getAttributes().remove(attrValue);
      createUpdateAttached();
   }

   private void handleEditValues(AttributeValue attrValue) {
      AttributeTypeToken attrType = attrValue.getAttrType();
      String asFormatType = "String";
      if (attrType.isEnumerated()) {
         EntryEnumDialog diag = new EntryEnumDialog("Enter Enumeration", "Enter Enumeration", attrType);
         if (diag.open() == 0) {
            String value = diag.getEntry();
            setAttrValueAndUpdateLink(attrValue, attrType, value);
         }
      } else if (attrType.isArtifactId() || attrType.isString() || //
         attrType.isDouble() || attrType.isInteger() || attrType.isLong()) {
         NumberFormat numFormat = null;
         if (attrType.isDouble()) {
            asFormatType = "Double";
            numFormat = NumberFormat.getInstance();
         } else if (attrType.isInteger()) {
            asFormatType = "Integer";
            numFormat = NumberFormat.getIntegerInstance();
         } else if (attrType.isLong()) {
            asFormatType = "Long";
            numFormat = NumberFormat.getNumberInstance();
         } else if (attrType.isArtifactId()) {
            asFormatType = "ArtifactId (Long)";
            numFormat = NumberFormat.getNumberInstance();
         } else if (attrType.isBranchId()) {
            asFormatType = "BranchId (Long)";
            numFormat = NumberFormat.getNumberInstance();
         }
         EntryDialog dialog = new EntryDialog("Enter " + attrType.getName(),
            "Enter " + attrType.getName() + "  (As " + asFormatType + ")\n\nNote: String search is a whole word search");
         dialog.setNumberFormat(numFormat);
         if (!attrValue.getValues().isEmpty()) {
            dialog.setEntry(attrValue.getValues().iterator().next());
         }
         if (dialog.open() == Window.OK) {
            String value = dialog.getEntry();
            setAttrValueAndUpdateLink(attrValue, attrType, value);
         }
      } else if (attrType.isBoolean()) {
         asFormatType = "Boolean";
         String[] buttonLabels = new String[] {"true", "false", "Cancel"};
         MessageDialog dialog = new MessageDialog(Displays.getActiveShell(), attrType.getName(), null,
            attrType.getName() + "\n\nAs " + asFormatType, MessageDialog.QUESTION, 3, buttonLabels);
         int selectedNum = dialog.open();
         if (selectedNum == 0) {
            setAttrValueAndUpdateLink(attrValue, attrType, "true");
         } else if (selectedNum == 1) {
            setAttrValueAndUpdateLink(attrValue, attrType, "false");
         }
      } else {
         AWorkbench.popup(String.format("Unhandled Attr Type %s", attrType.toStringWithId()));
      }
   }

   private void setAttrValueAndUpdateLink(AttributeValue attrValue, AttributeTypeToken attrType, String value) {
      attrValue.setValues(Collections.asList(value));
      Hyperlink hyperLinkLabel = attrTypeToValuesLabel.get(attrType);
      hyperLinkLabel.setText(value);
      updateLayout();
   }

   @Override
   public void refresh() {
      if (Widgets.isAccessible(comp)) {
         createUpdateAttached();
      }
   }

   @Override
   public void setFocus() {
      // do nothing
   }

   @Override
   public boolean isEmpty() {
      return false;
   }

   public AttributeValues getAttrValues() {
      return attrValues;
   }

   public void setAttrValues(AttributeValues attrValues) {
      this.attrValues = attrValues;
      refresh();
   }

   @Override
   public void setEditor(WorldEditor editor) {
      this.editor = editor;
   }

}
