/*********************************************************************
 * Copyright (c) 2021 Robert Bosch Engineering and Business Solutions Ltd India
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Robert Bosch Engineering and Business Solutions Ltd India - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.icteam.reqif.util;

import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.framework.core.enums.RelationSorter;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * Utility class to create requirements for module
 *
 * @author Manjunath Sangappa
 */
public class ModuleUtil {

   /**
    * Create a child req for the parent module in OSEE.
    *
    * @param newChildArt
    * @param branch
    * @param string
    * @param prefix
    */
   public static void addRequirementChildForModule(final Artifact newChildArt, final Branch branch, String string, final String prefix) {

      try {
         Artifact parent = newChildArt;

         SkynetTransaction transaction = TransactionManager.createTransaction(branch,
            String.format("Created new %s \"%s\" in artifact explorer", "Requirement Document", string));
         Artifact newChild =
            parent.addNewChild(RelationSorter.PREEXISTING, AtsArtifactTypes.RequirementDocument, string);

         newChild.setSoleAttributeFromString(AtsAttributeTypes.Prefix, prefix);

         parent.persist(transaction);
         transaction.execute();

      } catch (Exception exception) {
         exception.printStackTrace();
      }

   }

}
