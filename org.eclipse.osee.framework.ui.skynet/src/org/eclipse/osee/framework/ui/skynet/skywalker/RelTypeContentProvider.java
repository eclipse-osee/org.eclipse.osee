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
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.exception.OseeTypeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author Donald G. Dunne
 */
public class RelTypeContentProvider implements ITreeContentProvider {

   public RelTypeContentProvider() {
      super();
   }

   @SuppressWarnings("unchecked")
   public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Collection)
         return ((Collection) parentElement).toArray();
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
         return (sideA ? desc.getSideAName() : desc.getSideBName()) + " - " + desc.getName();
      }

      /**
       * @return the sideA
       */
      public boolean isSideA() {
         return sideA;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj instanceof RelationLinkDescriptorSide) {
            return (((RelationLinkDescriptorSide) obj).getDesc().equals(desc) && ((RelationLinkDescriptorSide) obj).isSideA() == sideA);
         }
         return super.equals(obj);
      }

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

      @Override
      public RelationType getRelationType() throws OseeTypeDoesNotExist, OseeDataStoreException {
         return desc;
      }

      @Override
      public RelationSide getSide() {
         return sideA ? RelationSide.SIDE_A : RelationSide.SIDE_B;
      }

      @Override
      public String getSideName() throws OseeTypeDoesNotExist, OseeDataStoreException {
         return getRelationType().getSideName(getSide());
      }

      @Override
      public String getName() {
         try {
            return getRelationType().getName();
         } catch (Exception ex) {
            return ex.getLocalizedMessage();
         }
      }

      @Override
      public boolean isThisType(RelationLink link) {
         return link.getRelationType().getName().equals(getName());
      }

   }

   public Object getParent(Object element) {
      return null;
   }

   public boolean hasChildren(Object element) {
      return (element instanceof RelationType);
   }

   public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
   }

   public void dispose() {
   }

   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
   }

}
