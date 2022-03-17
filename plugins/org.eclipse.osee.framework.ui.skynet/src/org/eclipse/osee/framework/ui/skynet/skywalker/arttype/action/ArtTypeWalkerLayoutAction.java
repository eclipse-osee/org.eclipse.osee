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
package org.eclipse.osee.framework.ui.skynet.skywalker.arttype.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.eclipse.jface.action.Action;
import org.eclipse.osee.framework.ui.skynet.FrameworkImage;
import org.eclipse.osee.framework.ui.skynet.skywalker.arttype.ArtifactTypeWalker;
import org.eclipse.osee.framework.ui.swt.ImageManager;
import org.eclipse.zest.layouts.LayoutStyles;
import org.eclipse.zest.layouts.algorithms.AbstractLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.RadialLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm;
import org.eclipse.zest.layouts.algorithms.TreeLayoutAlgorithm;

/**
 * @author Donald G. Dunne
 */
public class ArtTypeWalkerLayoutAction extends Action {

   private HashMap<String, AbstractLayoutAlgorithm> layouts;
   private static final String RADIAL_RIGHT_LAYOUT = "Radial - Right";
   private static final String RADIAL_DOWN_LAYOUT = "Radial - Down";
   private final List<String> layoutNames = new ArrayList<>();
   private final ArtifactTypeWalker view;
   private static final String defaultLayout = RADIAL_DOWN_LAYOUT;
   private String currentLayout = defaultLayout;

   public ArtTypeWalkerLayoutAction(ArtifactTypeWalker view) {
      super("Change Layout", ImageManager.getImageDescriptor(FrameworkImage.ARROW_DOWN_YELLOW));
      this.view = view;
   }

   @Override
   public void run() {
      nextLayout();
      setText("Change Layout (" + getCurrentLayoutName() + ")");
   }

   public void nextLayout() {
      ensureLoaded();
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
      view.refreshCurrent();
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

}
