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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.zest.core.viewers.IGraphEntityContentProvider;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.LinkManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationLinkGroup;

/**
 * @author Robert A. Fisher
 */
public class ArtifactGraphContentProvider implements IGraphEntityContentProvider {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(ArtifactGraphContentProvider.class);
   //   private static final Collection<Artifact>EMPTY_LIST = new ArrayList<Artifact>(0);
   private Artifact input;
   int levels;

   /**
    * @param levels
    */
   public ArtifactGraphContentProvider(int levels) {
      super();
      this.levels = levels;
   }

   /**
    * @return the levels
    */
   public int getLevels() {
      return levels;
   }

   /**
    * @param levels the levels to set
    */
   public void setLevels(int levels) {
      this.levels = levels;
   }

   /* (non-Javadoc)
    * @see org.eclipse.mylar.zest.core.viewers.IGraphEntityContentProvider#getConnectedTo(java.lang.Object)
    */
   public Object[] getConnectedTo(Object entity) {
      if (entity == input) {
         List<Artifact> otherItems = new LinkedList<Artifact>();

         try {
            LinkManager linkManager = input.getLinkManager();
            for (RelationLinkGroup linkGroup : linkManager.getGroups()) {
               otherItems.addAll(linkGroup.getArtifacts());
            }
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.toString(), ex);
         }
         return otherItems.toArray();
      } else {
         return new Object[] {input};
      }

      //      if (entity instanceof Artifact) {
      //         return ((Artifact)entity).getLinkManager().getOtherSideArtifacts().toArray();
      //      }
      //      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.mylar.zest.core.viewers.IGraphEntityContentProvider#getElements(java.lang.Object)
    */
   public Object[] getElements(Object inputElement) {
      if (true) return new Object[] {input};
      //         return input.getLinkManager().getOtherSideArtifacts().toArray();
      if (inputElement instanceof Artifact) {
         LinkedList<Artifact> artifacts = new LinkedList<Artifact>();
         getDescendants(artifacts, (Artifact) inputElement, levels);
         return artifacts.toArray();
      }
      return null;
   }

   private void getDescendants(Collection<Artifact> artifacts, Artifact artifact, int level) {
      if (level == 0) {
         return;
      } else {
         try {
            for (Artifact child : artifact.getLinkManager().getOtherSideArtifacts()) {
               artifacts.add(child);
               getDescendants(artifacts, artifact, level - 1);
            }
         } catch (SQLException ex) {
            logger.log(Level.SEVERE, ex.getLocalizedMessage(), ex);
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.mylar.zest.core.viewers.IGraphEntityContentProvider#getWeight(java.lang.Object, java.lang.Object)
    */
   public double getWeight(Object entity1, Object entity2) {
      return 0;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IContentProvider#dispose()
    */
   public void dispose() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
    */
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
      if (newInput instanceof Artifact) {
         input = (Artifact) newInput;
      } else {
         throw new IllegalArgumentException("newInput must be instance of Artifact");
      }
   }

}
