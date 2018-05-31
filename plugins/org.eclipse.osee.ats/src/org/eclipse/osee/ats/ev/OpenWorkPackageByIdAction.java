/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.ev;

import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.ats.AtsImage;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.artifact.massEditor.MassArtifactEditor;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.EntryDialog;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * @author Donald G. Dunne
 */
public class OpenWorkPackageByIdAction extends Action {

   public static final AttributeTypeToken FinancialSystemId =
      AttributeTypeToken.valueOf(72063457009467630L, "Financial System Id");

   public OpenWorkPackageByIdAction() {
      this("Open Work Package by ID(s)");
   }

   public OpenWorkPackageByIdAction(String name) {
      super(name);
      setToolTipText(getText());
   }

   @Override
   public void run() {
      EntryDialog dialog = new EntryDialog(getText(), "Enter Work Package id, activity id or financial id");
      if (dialog.open() == Window.OK) {
         final List<String> ids = new LinkedList<String>();
         for (String str : dialog.getEntry().split(",")) {
            str = str.replaceAll("^\\s+", "");
            str = str.replaceAll("\\s+$", "");
            if (Strings.isNumeric(str)) {
               ids.add(str);
            }
            Job searchWps = new Job(getText()) {

               @Override
               protected IStatus run(IProgressMonitor monitor) {
                  List<Artifact> results = new LinkedList<>();
                  if (!ids.isEmpty()) {
                     results.addAll(ArtifactQuery.getArtifactListFromAttributeValues(AtsAttributeTypes.ActivityId, ids,
                        AtsClientService.get().getAtsBranch(), 5));
                     AttributeType type = AttributeTypeManager.getType(FinancialSystemId);
                     if (type != null) {
                        results.addAll(ArtifactQuery.getArtifactListFromAttributeValues(FinancialSystemId, ids,
                           AtsClientService.get().getAtsBranch(), 5));
                     }
                  }
                  if (results.isEmpty()) {
                     AWorkbench.popup("No Work Packages found with id(s): " + dialog.getEntry());
                  } else {
                     MassArtifactEditor.editArtifacts(getName(), results);
                  }
                  return Status.OK_STATUS;
               }
            };
            Jobs.startJob(searchWps, true);
         }
      }
   }

   @Override
   public ImageDescriptor getImageDescriptor() {
      return ImageManager.getImageDescriptor(AtsImage.WORK_PACKAGE);
   }

}
