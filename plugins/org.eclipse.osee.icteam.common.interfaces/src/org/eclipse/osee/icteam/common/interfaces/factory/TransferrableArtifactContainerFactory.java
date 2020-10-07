/*********************************************************************
 * Copyright (c) 2020 Robert Bosch Engineering and Business Solutions Ltd India
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
package org.eclipse.osee.icteam.common.interfaces.factory;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifact;
import org.eclipse.osee.icteam.common.artifact.interfaces.ITransferableArtifactsContainer;

/**
 * @author Ajay Chandrahasan
 */
public class TransferrableArtifactContainerFactory {

   public static ITransferableArtifactsContainer createArtifact() {

      ITransferableArtifactsContainer container = new ITransferableArtifactsContainer() {

         @Override
         public List<ITransferableArtifact> getArtifactList() {
            // TODO Auto-generated method stub
            return null;
         }

         // @Override
         // public void addArtifact(final ITransferableArtifact art) {
         // // TODO Auto-generated method stub
         //
         // }

         @Override
         public void addAll(final List<ITransferableArtifact> listTras) {
            // TODO Auto-generated method stub

         }

         @Override
         public String getStatus() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public void setStatus(final String status) {
            // TODO Auto-generated method stub

         }

         @Override
         public String getProjectGUID() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public void setProjectGUID(final String projectGUID) {
            // TODO Auto-generated method stub

         }

         @Override
         public LinkedHashMap<String, String> getAttributes() {
            // TODO Auto-generated method stub
            return null;
         }

         // @Override
         // public void setAttributes(
         // LinkedHashMap<String, String> checkedAttributes) {
         // // TODO Auto-generated method stub
         //
         // }

         @Override
         public boolean isInclude() {
            // TODO Auto-generated method stub
            return false;
         }

         @Override
         public void setInclude(final boolean include) {
            // TODO Auto-generated method stub

         }

         @Override
         public Map<String, ? extends Object> getMetaInfo() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public void setMetaInfo(final Map<String, ? extends Object> metaInfo) {
            // TODO Auto-generated method stub

         }

         @Override
         public ITransferableArtifact getParentArtifact() {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         public void setParentArtifact(final ITransferableArtifact parentArtifact) {
            // TODO Auto-generated method stub

         }

         @Override
         public void setAttributes(final HashMap<String, String> checkedAttributes) {
            // TODO Auto-generated method stub

         }

      };
      return container;

   }
}
