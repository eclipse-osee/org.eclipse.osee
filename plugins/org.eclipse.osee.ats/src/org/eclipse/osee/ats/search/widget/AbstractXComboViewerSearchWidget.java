/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.search.widget;

import java.util.Collection;
import org.eclipse.osee.ats.api.query.AtsSearchData;
import org.eclipse.osee.ats.world.WorldEditorParameterSearchItem;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.ui.skynet.widgets.XComboViewer;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractXComboViewerSearchWidget<ObjectType extends Object> extends AbstractSearchWidget<XComboViewer, Object> {

   public AbstractXComboViewerSearchWidget(String name, WorldEditorParameterSearchItem searchItem) {
      super(name, "XComboViewer", searchItem);
   }

   @SuppressWarnings("unchecked")
   public ObjectType get() {
      XComboViewer combo = getWidget();
      if (combo != null) {
         return (ObjectType) combo.getSelected();
      }
      return null;
   }

   @Override
   public XComboViewer getWidget() {
      return super.getWidget();
   }

   public abstract void set(AtsSearchData data);

   public abstract Collection<ObjectType> getInput();

   boolean listenerAdded = false;

   public void setup(XWidget widget) {
      if (widget != null) {
         XComboViewer combo = (XComboViewer) widget;
         combo.setInput(Collections.castAll(getInput()));
         combo.getCombo().setText(getInitialText());
         if (!listenerAdded) {
            listenerAdded = true;
            combo.getLabelWidget().addMouseListener(new MouseAdapter() {

               @Override
               public void mouseDown(MouseEvent e) {
                  super.mouseDown(e);
                  if (e.button == 3) {
                     handleRightClickLabel();
                  }
               }

            });
            combo.getLabelWidget().setToolTipText("Right-click to clear");
         }
      }
   }

   public void handleRightClickLabel() {
      set(new AtsSearchData());
   }

   public String getInitialText() {
      return "";
   }

}
