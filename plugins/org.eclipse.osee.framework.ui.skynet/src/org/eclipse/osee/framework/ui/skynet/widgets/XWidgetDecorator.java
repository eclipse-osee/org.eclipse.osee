/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.framework.ui.skynet.widgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;

/**
 * @author Roberto E. Escobar
 */
public class XWidgetDecorator {
   private static final Comparator<DecorationProvider> PROVIDER_COMPARATOR = new Comparator<DecorationProvider>() {

      @Override
      public int compare(DecorationProvider o1, DecorationProvider o2) {
         return o1.getPriority() - o2.getPriority();
      }
   };

   private final int decorationPosition = SWT.LEFT | SWT.BOTTOM;
   private final Map<XWidget, Decorator> decoratorMap;
   private final List<DecorationProvider> providers;

   public static interface DecorationProvider {
      int getPriority();

      void onUpdate(XWidget widget, Decorator decorator);
   }

   public XWidgetDecorator() {
      decoratorMap = new HashMap<>();
      providers = new ArrayList<>();
   }

   public void addWidget(XWidget xWidget) {
      Control controlToDecorate = xWidget.getErrorMessageControl();
      Decorator decorator = new Decorator(controlToDecorate, decorationPosition);
      decoratorMap.put(xWidget, decorator);
   }

   public void addProvider(DecorationProvider provider) {
      providers.add(provider);
   }

   public void refresh() {
      Collections.sort(providers, PROVIDER_COMPARATOR);

      for (Entry<XWidget, Decorator> entry : decoratorMap.entrySet()) {
         for (DecorationProvider provider : providers) {
            provider.onUpdate(entry.getKey(), entry.getValue());
         }
      }
      update();
   }

   public void update() {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (Decorator decorator : decoratorMap.values()) {
               decorator.update();
            }
         }
      });
   }

   public void dispose() {
      for (Decorator decorator : decoratorMap.values()) {
         decorator.dispose();
      }
      decoratorMap.clear();
   }

   public final static class Decorator {
      private ControlDecoration decoration;
      private String description;
      private int position;
      private Image image;
      private boolean isVisible;
      private boolean requiresCreation;
      private final Control control;

      public Decorator(Control control, int position) {
         this.control = control;
         setPosition(position);
      }

      public void setDescription(String description) {
         this.description = description;
      }

      public void setImage(Image image) {
         this.image = image;
      }

      public void setPosition(int position) {
         if (getPosition() != position) {
            this.position = position;
            this.requiresCreation = true;
         }
      }

      public void setVisible(boolean isVisible) {
         this.isVisible = isVisible;
      }

      public boolean isVisible() {
         return isVisible;
      }

      public int getPosition() {
         return position;
      }

      public void dispose() {
         if (decoration != null) {
            decoration.dispose();
         }
      }

      public void update() {
         if (requiresCreation) {
            if (decoration != null) {
               decoration.dispose();
            }
            decoration = new ControlDecoration(control, position, control.getParent());
            requiresCreation = false;
         }

         if (isVisible()) {
            if (image != null) {
               if (decoration.getControl() != null) {
                  decoration.setImage(image);
               }
            }
            decoration.setDescriptionText(description);
            decoration.show();
         } else {
            decoration.setDescriptionText(null);
            decoration.hide();
         }
      }
   }
}
