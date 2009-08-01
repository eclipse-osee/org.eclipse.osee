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
package org.eclipse.osee.framework.ui.skynet;

import java.util.Arrays;
import java.util.Comparator;
import org.eclipse.jface.viewers.ContentViewer;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

public class LabelSorter extends ViewerSorter {

   @Override
   public int category(Object element) {
      return super.category(element);
   }

   @SuppressWarnings("unchecked")
   @Override
   public int compare(Viewer viewer, Object e1, Object e2) {

      Artifact art1 = null;
      Artifact art2 = null;

      if (e1 instanceof Artifact) art1 = (Artifact) e1;
      if (e2 instanceof Artifact) art2 = (Artifact) e2;

      boolean e1IsHeading = (art1 != null && art1.getArtifactTypeName().equals("Heading"));
      boolean e2IsHeading = (art2 != null && art2.getArtifactTypeName().equals("Heading"));
      boolean e1IsNarrative =
            (art1 != null && !e1IsHeading && art1.getArtifactTypeName().equals("Narrative"));
      boolean e2IsNarrative =
            (art2 != null && !e2IsHeading && art2.getArtifactTypeName().equals("Narrative"));

      if (e1IsHeading ^ e2IsHeading) return (e1IsHeading ? -1 : 1);

      if (e1IsNarrative ^ e2IsNarrative) return (e1IsNarrative ? -1 : 1);

      int cat1 = category(e1);
      int cat2 = category(e2);

      if (cat1 != cat2) return cat1 - cat2;

      String name1;
      String name2;

      if (viewer == null || !(viewer instanceof ContentViewer)) {
         name1 = e1.toString();
         name2 = e2.toString();
      } else {
         IBaseLabelProvider prov = ((ContentViewer) viewer).getLabelProvider();
         if (prov instanceof ILabelProvider) {
            ILabelProvider lprov = (ILabelProvider) prov;
            name1 = lprov.getText(e1);
            name2 = lprov.getText(e2);
         } else {
            name1 = e1.toString();
            name2 = e2.toString();
         }
      }
      if (name1 == null) name1 = "";
      if (name2 == null) name2 = "";
      return getComparator().compare(name1, name2);
   }

   @Override
   public boolean isSorterProperty(Object element, String property) {
      return super.isSorterProperty(element, property);
   }

   @SuppressWarnings("unchecked")
   @Override
   public void sort(final Viewer viewer, Object[] elements) {
      Arrays.sort(elements, new Comparator() {
         public int compare(Object a, Object b) {
            return LabelSorter.this.compare(viewer, a, b);
         }
      });
   }
}
