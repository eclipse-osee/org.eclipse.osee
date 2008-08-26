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
package org.eclipse.osee.framework.ui.skynet.blam.operation;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionId;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.word.WordUtil;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;

/**
 * @author Ryan D. Brooks
 */
public class InPlaceSmartTagRemover extends AbstractBlam {
   private static final String UPDATE_ATTRIBUTE = "UPDATE osee_define_attribute SET content = ? WHERE gamma_id = ?";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Artifact> artifacts = variableMap.getArtifacts("artifacts");
      int txNumber = Integer.parseInt(variableMap.getString("Transaction Number"));
      TransactionId transactionId = TransactionIdManager.getTransactionId(txNumber);
      ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();

      monitor.beginTask("Update Templates", artifacts.size());
      for (Artifact artifactTemp : artifacts) {
         Artifact artifact = artifactManager.getArtifactFromId(artifactTemp.getArtId(), transactionId);

         Collection<Attribute<String>> attributes =
               artifact.getAttributes(AttributeTypeManager.getTypeWithWordContentCheck(artifact,
                     WordAttribute.CONTENT_NAME).getName());
         for (Attribute<String> attribute : attributes) {
            String currentValue = attribute.getValue();
            String cleanValue = WordUtil.removeWordMarkupSmartTags(currentValue);
            if (!currentValue.equals(cleanValue)) {
               InputStream in = new ByteArrayInputStream(cleanValue.getBytes());
               ConnectionHandler.runPreparedUpdate(UPDATE_ATTRIBUTE, in, attribute.getGammaId());
            }
         }
         monitor.worked(1);
      }
   }

   /*
    * (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getXWidgetXml()
    */
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Transaction Number\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"artifacts\" /></xWidgets>";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#getDescriptionUsage()
    */
   public String getDescriptionUsage() {
      return "removes smart tags from the most current version of the word formmatted content attribute on the given artifacts in-place.";
   }
}