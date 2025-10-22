/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.ats.ide.search.widget;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.ide.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.skynet.ToStringViewerSorter;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractXComboViewerSearchWidget<ObjectType extends Object> extends AbstractSearchWidget<XComboViewer, Object> {

   public static final String CLEAR = "--clear--";

   public AbstractXComboViewerSearchWidget(String name, WorldEditorParameterSearchItem searchItem) {
      super(name, "XComboViewer", searchItem);
   }

   @SuppressWarnings("unchecked")
   public ObjectType get() {
      XComboViewer combo = getWidget();
      if (combo != null) {
         return (ObjectType) combo.getSelected();
      }
      Object obj = null;
      ObjectType object = (ObjectType) obj;
      return object;
   }

   @Override
   public XComboViewer getWidget() {
      return super.getWidget();
   }

   public abstract void set(AtsSearchData data);

   public abstract Collection<ObjectType> getInput();

   boolean listenerAdded = false;
   private XComboViewer comboWidget;

   public void setup(XWidget widget) {
      if (comboWidget == null && widget != null) {
         comboWidget = (XComboViewer) widget;
         List<Object> input = new ArrayList<>();
         input.addAll(Collections.castAll(getInput()));
         if (!input.contains(CLEAR)) {
            input.add(CLEAR);
         }
         comboWidget.setInput(input);
         comboWidget.setComparator(new ToStringViewerSorter(true));
         comboWidget.getCombo().setText(getInitialText());
         if (!listenerAdded) {
            listenerAdded = true;
            comboWidget.getLabelWidget().addMouseListener(new MouseAdapter() {

               @Override
               public void mouseDown(MouseEvent e) {
                  super.mouseDown(e);
                  if (e.button == 3) {
                     handleRightClickLabel();
                  }
               }

            });
            comboWidget.getLabelWidget().setToolTipText("Right-click to clear");
            comboWidget.addSelectionListener(new SelectionAdapter() {

               @Override
               public void widgetSelected(SelectionEvent e) {
                  super.widgetSelected(e);
                  if (comboWidget.getSelected().toString().equals(CLEAR)) {
                     handleRightClickLabel();
                  }
               }

            });
         }
      }
   }

   public void handleRightClickLabel() {
      clear();
   }

   protected void clear() {
      if (getWidget() != null) {
         setup(getWidget());
         XComboViewer combo = getWidget();
         combo.setSelected(Arrays.asList(""));
         if (Strings.isValid(getInitialText())) {
            combo.getCombo().setText(getInitialText());
         }
      }
   }

   public String getInitialText() {
      return "";
   }

}
