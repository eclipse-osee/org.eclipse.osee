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

package org.eclipse.osee.framework.ui.skynet.skywalker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.ui.skynet.ImageManager;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.skywalker.SkyWalkerOptions.LinkName;
import org.eclipse.swt.graphics.Image;
import org.eclipse.zest.core.viewers.EntityConnectionData;

/**
 * @author Robert A. Fisher
 * @author Donald G. Dunne
 */
public class ArtifactGraphLabelProvider implements ILabelProvider {
   private final SkyWalkerOptions options;

   public ArtifactGraphLabelProvider(SkyWalkerOptions options) {
      this.options = options;

   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
    */
   public Image getImage(Object element) {
      if (element instanceof Artifact) {
         return ImageManager.getImage((Artifact) element);
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
    */
   public String getText(Object element) {
      try {
         if (element instanceof Artifact) {
            return ((Artifact) element).getDescriptiveName() + options.getExtendedName((Artifact) element);
         } else if (element instanceof EntityConnectionData) {
            Object obj1 = ((EntityConnectionData) element).dest;
            Object obj2 = ((EntityConnectionData) element).source;
            if (obj1 instanceof Artifact && obj2 instanceof Artifact) {
               Artifact dest = (Artifact) obj1;
               Artifact source = (Artifact) obj2;

               Collection<RelationLink> links = dest.getRelations(source);

               Collection<String> linkNames = new ArrayList<String>(links.size());
               for (RelationLink link : links) {
                  if (options.getLinkName() == LinkName.Phrasing_A_to_B) {
                     if (link.getArtifactA().equals(source)) {
                        linkNames.add(source + " (" + link.getSidePhrasingFor(source) + ") " + dest);
                     } else {
                        linkNames.add(dest + " (" + link.getSidePhrasingFor(dest) + ") " + source);
                     }
                  } else if (options.getLinkName() == LinkName.Phrasing_B_to_A) {
                     if (link.getArtifactA().equals(source)) {
                        linkNames.add(dest + " (" + link.getSidePhrasingFor(dest) + ") " + source);
                     } else {
                        linkNames.add(source + " (" + link.getSidePhrasingFor(source) + ") " + dest);
                     }
                  } else if (options.getLinkName() == LinkName.Link_Name)
                     linkNames.add(link.getRelationType().getTypeName());
                  else if (options.getLinkName() == LinkName.Full_Link_Name)
                     linkNames.add(link.getRelationType().toString());
                  else if (options.getLinkName() == LinkName.Other_Side_Name) {
                     if (link.getArtifactA().equals(source)) {

                        linkNames.add(source + " (" + link.getSideNameFor(source) + ")" + " <--> " + dest + " (" + link.getSideNameFor(dest) + ")");
                     } else {
                        linkNames.add(dest + " (" + link.getSideNameFor(dest) + ")" + " <--> " + source + " (" + link.getSideNameFor(source) + ")");
                     }
                  } else
                     linkNames.add("");
               }
               return Collections.toString("\n", linkNames);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         return ex.getLocalizedMessage();
      }
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void addListener(ILabelProviderListener listener) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
    */
   public void dispose() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
    *      java.lang.String)
    */
   public boolean isLabelProperty(Object element, String property) {
      return false;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
    */
   public void removeListener(ILabelProviderListener listener) {
   }

}
