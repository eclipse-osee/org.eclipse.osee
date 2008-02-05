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
package org.eclipse.osee.framework.ui.skynet.artifact.annotation;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.annotation.ArtifactAnnotation;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.swt.ALayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Donald G. Dunne
 */
public class AnnotationComposite extends Composite {

   /**
    * @param parent
    * @param style
    */
   public AnnotationComposite(Composite parent, int style, Artifact artifact) {
      this(null, parent, style, artifact);
   }

   /**
    * @param parent
    * @param style
    */
   public AnnotationComposite(FormToolkit toolkit, Composite parent, int style, Artifact artifact) {
      super(parent, style);

      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = 4;
      setLayoutData(gd);
      setLayout(ALayout.getZeroMarginLayout(2, false));

      for (ArtifactAnnotation.Type type : ArtifactAnnotation.Type.getOrderedTypes()) {
         for (ArtifactAnnotation notify : artifact.getAnnotations()) {
            if (notify.getType() != type) continue;
            if (notify.getType() == ArtifactAnnotation.Type.None) {
               OSEELog.logException(SkynetGuiPlugin.class, new IllegalStateException(
                     "None is an invalid annotation type - " + artifact.getHumanReadableId()), false);
               continue;
            }
            Label iconLabel = toolkit != null ? toolkit.createLabel(this, "") : new Label(this, SWT.NONE);
            iconLabel.setImage(notify.getType().getImage());

            Label alertLabel = toolkit != null ? toolkit.createLabel(this, "") : new Label(this, SWT.NONE);
            alertLabel.setText(notify.getType().name() + ": " + notify.getContent());
         }
      }
      if (toolkit != null) toolkit.adapt(this);

   }
}
