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
package org.eclipse.osee.framework.ui.skynet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Ryan D. Brooks
 */
public class AttributeContentProvider implements IStructuredContentProvider {

   private Collection<String> ignoreAttributes;
   private Collection<DynamicAttributeManager> attributeTypes;
   private Object[] dummyArray = new Object[0];

   /**
    * 
    */
   public AttributeContentProvider(Collection<String> ignoreAttributes) {
      super();
      this.ignoreAttributes = ignoreAttributes;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
    */
   public Object[] getElements(Object inputElement) {

      if (inputElement instanceof Artifact) {

         Artifact artifact = (Artifact) inputElement;

         ArrayList<Attribute> elements = new ArrayList<Attribute>();
         try {
            Collection<DynamicAttributeManager> types = populateAttributeTypes(artifact);
            for (DynamicAttributeManager type : types) {
               for (Attribute attribute : type.getAttributes()) {
                  elements.add(attribute);
               }
            }
            return elements.toArray();
         } catch (SQLException ex) {
            OSEELog.logException(SkynetGuiPlugin.class, ex, true);
         }
      }
      return dummyArray;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IContentProvider#dispose()
    */
   public void dispose() {
   }

   public Collection<DynamicAttributeManager> populateAttributeTypes(Artifact artifact) throws SQLException {
      Collection<DynamicAttributeManager> attrTypes = null;
      if (ignoreAttributes == null)
         attrTypes = artifact.getAttributes();
      else {
         attrTypes = new ArrayList<DynamicAttributeManager>();
         for (DynamicAttributeManager udat : artifact.getAttributes())
            if (!ignoreAttributes.contains(udat.getDescriptor().getName())) attrTypes.add(udat);
      }
      return attrTypes;
   }

   /* (non-Javadoc)
    * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
    */
   public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

   }

   /**
    * @return Returns the attributeTypes.
    */
   public Collection<DynamicAttributeManager> getAttributeTypes() {
      return attributeTypes;
   }
}
