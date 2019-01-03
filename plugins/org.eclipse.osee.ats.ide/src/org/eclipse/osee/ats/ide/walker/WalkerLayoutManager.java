/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ide.walker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * @author Donald G. Dunne
 */
public class WalkerLayoutManager {

   private HashMap<String, AbstractLayoutAlgorithm> layouts;
   private static final String RADIAL_RIGHT_LAYOUT = "Radial - Right";
   private static final String RADIAL_DOWN_LAYOUT = "Radial - Down";
   private final List<String> layoutNames = new ArrayList<>();
   private final ActionWalkerView view;
   private static final String defaultLayout = RADIAL_DOWN_LAYOUT;
   private String currentLayout = defaultLayout;

   public WalkerLayoutManager(ActionWalkerView actionWalkerView) {
      this.view = actionWalkerView;
   }

   public void start() {
      ensureLoaded();
      view.setLayout(layouts.get(currentLayout));
      view.refresh();
   }

   public void nextLayout() {
      int index = layoutNames.indexOf(currentLayout);
      if (index == -1) {
         currentLayout = defaultLayout;
      }
      String nextLayout = defaultLayout;
      if (layoutNames.size() > index + 1) {
         nextLayout = layoutNames.get(index + 1);
      }
      currentLayout = nextLayout;
      view.setLayout(layouts.get(currentLayout));
      view.refresh();
   }

   public String getCurrentLayoutName() {
      return currentLayout;
   }

   public void ensureLoaded() {
      if (layouts == null) {
         layouts = new HashMap<>();

         RadialLayoutAlgorithm radLayout = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
         radLayout.setRangeToLayout(0, 180 * Math.PI / 360);
         layouts.put(RADIAL_DOWN_LAYOUT, radLayout);
         layoutNames.add(RADIAL_DOWN_LAYOUT);

         radLayout = new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING);
         radLayout.setRangeToLayout(-90 * Math.PI / 360, 90 * Math.PI / 360);
         layouts.put(RADIAL_RIGHT_LAYOUT, radLayout);
         layoutNames.add(RADIAL_RIGHT_LAYOUT);

         layouts.put("Radial - Full", new RadialLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
         layoutNames.add("Radial - Full");

         layouts.put("Spring", new SpringLayoutAlgorithm());
         layoutNames.add("Spring");

         layouts.put("Tree", new TreeLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING));
         layoutNames.add("Tree");
      }
   }

   public void init(IViewSite site, IMemento memento) {
      if (memento != null) {
         String layout = memento.getString("Layout");
         if (Strings.isValid(layout)) {
            currentLayout = layout;
         }
      }
   }

   public void saveState(IMemento memento) {
      if (memento != null && Strings.isValid(currentLayout)) {
         memento.putString("Layout", currentLayout);
      }
   }

}
