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

import java.util.Collection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Donald G. Dunne
 */
public class RelTypeContentProvider implements ITreeContentProvider {

   /**
    * 
    */
   public RelTypeContentProvider() {
      super();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
    */
   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection) return ((Collection) parentElement).toArray();
      if (parentElement instanceof RelationType) {
         return new Object[] {new RelationLinkDescriptorSide((RelationType) parentElement, true),
               new RelationLinkDescriptorSide((RelationType) parentElement, false)};
      }
      return new Object[] {};
   }

   public static class RelationLinkDescriptorSide implements IRelationEnumeration {
      private final RelationType desc;
      private final Boolean sideA;

      public RelationLinkDescriptorSide(RelationType desc, boolean sideA) {
         this.desc = desc;
         this.sideA = sideA;
      }

      @Override
      public String toString() {
         return (sideA ? desc.getSideAName() : desc.getSideBName()) + " - " + desc.getTypeName();
      }

      /**
       * @return the sideA
       */
      public boolean isSideA() {
         return sideA;
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Object#equals(java.lang.Object)
       */
      @Override
      public boolean equals(Object obj) {
         if (obj instanceof RelationLinkDescriptorSide) {
            return (((RelationLinkDescriptorSide) obj).getDesc().equals(desc) && ((RelationLinkDescriptorSide) obj).isSideA() == sideA);
         }
         return super.equals(obj);
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Object#hashCode()
       */
      @Override
      public int hashCode() {
         return desc.hashCode() + sideA.hashCode();
      }

      /**
       * @return the desc
       */
      public RelationType getDesc() {
         return desc;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getRelationType()
       */
      @Override
      public RelationType getRelationType() throws OseeTypeDoesNotExist, OseeDataStoreException {
         return desc;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getSide()
       */
      @Override
      public RelationSide getSide() {
         return sideA ? RelationSide.SIDE_A : RelationSide.SIDE_B;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getSideName()
       */
      @Override
      public String getSideName() throws OseeTypeDoesNotExist, OseeDataStoreException {
         return getRelationType().getSideName(getSide());
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#getTypeName()
       */
      @Override
      public String getTypeName() {
         try {
            return getRelationType().getNamespace();
         } catch (Exception ex) {
            return ex.getLocalizedMessage();
         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration#isThisType(org.eclipse.osee.framework.skynet.core.relation.RelationLink)
       */
      @Override
      public boolean isThisType(RelationLink link) {
         return link.getRelationType().getTypeName().equals(getTypeName());
      }

   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
    */
   public Object getParent(Object element) {
      return null;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
    */
   public boolean hasChildren(Object element) {
      return (element instanceof RelationType);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
    */
   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IContentProvider#dispose()
    */
   public void dispose() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer,
    *      java.lang.Object, java.lang.Object)
    */
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

}
