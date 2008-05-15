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
package org.eclipse.osee.framework.ui.skynet.zest;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.EntityConnectionData;

/**
 * @author Robert A. Fisher
 */
public class ArtifactGraphLabelProvider implements ILabelProvider {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactGraphLabelProvider.class);

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
    */
   public Image getImage(Object element) {
      if (element instanceof Artifact) {
         return ((Artifact) element).getImage();
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
    */
   public String getText(Object element) {
      if (element instanceof Artifact) {
         return ((Artifact) element).getDescriptiveName();
      } else if (element instanceof EntityConnectionData) {
         Object obj1 = ((EntityConnectionData) element).dest;
         Object obj2 = ((EntityConnectionData) element).source;
         if (obj1 instanceof Artifact && obj2 instanceof Artifact) {
            Artifact dest = (Artifact) obj1;
            Artifact source = (Artifact) obj2;
            try {
               Collection<RelationLink> links = dest.getRelations(source);

               Collection<String> linkNames = new ArrayList<String>(links.size());
               for (RelationLink link : links) {
                  linkNames.add(link.getRelationType().getTypeName());
               }
               return Collections.toString("\n", linkNames);
            } catch (SQLException ex) {
               logger.log(Level.SEVERE, ex.toString(), ex);
            }
         }
      }
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void addListener(ILabelProviderListener listener) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
    */
   public void dispose() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
    */
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void removeListener(ILabelProviderListener listener) {
   }

}
