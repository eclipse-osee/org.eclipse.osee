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

package org.eclipse.osee.framework.ui.skynet.search.page;

import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.jface.viewers.StyledString.Styler;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.PlatformUI;

/**
 * @author Roberto E. Escobar
 */
public class DecoratingArtifactSearchLabelProvider extends DecoratingStyledCellLabelProvider implements IPropertyChangeListener, ILabelProvider {

   private static final String HIGHLIGHT_BG_COLOR_NAME = "org.eclipse.jdt.ui.ColoredLabels.match_highlight"; //$NON-NLS-1$

   public static final Styler HIGHLIGHT_STYLE = StyledString.createColorRegistryStyler(null, HIGHLIGHT_BG_COLOR_NAME);

   public DecoratingArtifactSearchLabelProvider(ArtifactSearchLabelProvider provider) {
      super(provider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), null);
   }

   @Override
   public void initialize(ColumnViewer viewer, ViewerColumn column) {
      PlatformUI.getPreferenceStore().addPropertyChangeListener(this);
      JFaceResources.getColorRegistry().addListener(this);

      setOwnerDrawEnabled(showColoredLabels());

      super.initialize(viewer, column);
   }

   @Override
   public void dispose() {
      super.dispose();
      PlatformUI.getPreferenceStore().removePropertyChangeListener(this);
      JFaceResources.getColorRegistry().removeListener(this);
   }

   private void refresh() {
      ColumnViewer viewer = getViewer();

      if (viewer == null) {
         return;
      }
      boolean showColoredLabels = showColoredLabels();
      if (showColoredLabels != isOwnerDrawEnabled()) {
         setOwnerDrawEnabled(showColoredLabels);
         viewer.refresh();
      } else if (showColoredLabels) {
         viewer.refresh();
      }
   }

   @Override
   protected StyleRange prepareStyleRange(StyleRange styleRange, boolean applyColors) {
      if (!applyColors && styleRange.background != null) {
         styleRange = super.prepareStyleRange(styleRange, applyColors);
         styleRange.borderStyle = SWT.BORDER_DOT;
         return styleRange;
      }
      return super.prepareStyleRange(styleRange, applyColors);
   }

   public static boolean showColoredLabels() {
      return PlatformUI.getPreferenceStore().getBoolean(IWorkbenchPreferenceConstants.USE_COLORED_LABELS);
   }

   @Override
   public void propertyChange(PropertyChangeEvent event) {
      String property = event.getProperty();
      if (property.equals(JFacePreferences.QUALIFIER_COLOR) || property.equals(
         JFacePreferences.COUNTER_COLOR) || property.equals(JFacePreferences.DECORATIONS_COLOR) || property.equals(
            HIGHLIGHT_BG_COLOR_NAME) || property.equals(IWorkbenchPreferenceConstants.USE_COLORED_LABELS)) {
         Displays.ensureInDisplayThread(new Runnable() {
            @Override
            public void run() {
               refresh();
            }
         });
      }
   }

   @Override
   public String getText(Object element) {
      return getStyledText(element).getString();
   }

}