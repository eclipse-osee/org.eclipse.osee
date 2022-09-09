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

package org.eclipse.osee.framework.ui.skynet.artifact.annotation;

import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.ArtifactImageManager;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
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

   public AnnotationComposite(Composite parent, int style, Artifact artifact) {
      this(null, parent, style, artifact);
   }

   public AnnotationComposite(FormToolkit toolkit, Composite parent, int style, Artifact artifact) {
      super(parent, style);

      GridData gd = new GridData(GridData.FILL_HORIZONTAL);
      gd.horizontalSpan = 4;
      setLayoutData(gd);
      setLayout(ALayout.getZeroMarginLayout(2, false));

      for (ArtifactAnnotation.Type type : ArtifactAnnotation.Type.getOrderedTypes()) {
         try {
            for (ArtifactAnnotation notify : AttributeAnnotationManager.get(artifact).getAnnotations()) {
               if (notify.getType() != type) {
                  continue;
               }
               if (notify.getType() == ArtifactAnnotation.Type.None) {
                  OseeLog.log(Activator.class, Level.SEVERE,
                     new OseeStateException("None is an invalid annotation type on artifact [%s]", artifact.getGuid()));
                  continue;
               }
               Label iconLabel = toolkit != null ? toolkit.createLabel(this, "") : new Label(this, SWT.NONE);
               iconLabel.setImage(ArtifactImageManager.getAnnotationImage(notify.getType()));

               Label alertLabel = toolkit != null ? toolkit.createLabel(this, "") : new Label(this, SWT.NONE);
               alertLabel.setText(notify.getType().name() + ": " + notify.getContent());
            }
         } catch (OseeCoreException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, "Exception resolving annotations", ex);
         }
      }
      if (toolkit != null) {
         toolkit.adapt(this);
      }
   }
}
