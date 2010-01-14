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
package org.eclipse.osee.ats.health.change;

/**
 * @author Jeff C. Phillips
 */
public class AttributeChangeReportComparer extends DataChangeReportComparer{
   private static String ATTR_START_TAG = "<attrId>";
   private static String ATTR_END_TAG = "</attrId>";
   private int artId;
   private int attrId;
   
   public AttributeChangeReportComparer(String content) {
      super(content);
   }

   @Override
   public void processContent(String content) {
      artId = Integer.parseInt(content.substring(content.indexOf(ArtifactChangeReportComparer.ART_START_TAG)
                                                 + ArtifactChangeReportComparer.ART_START_TAG.length(), 
                                                 content.indexOf(ArtifactChangeReportComparer.ART_END_TAG)));
      
      attrId = Integer.parseInt(content.substring(content.indexOf(ATTR_START_TAG)
                                                 + ATTR_START_TAG.length(), content.indexOf(ATTR_END_TAG)));
   }

   @Override
   public int compareTo(Object obj) {
      int compareResults = -1;
      
      if(obj instanceof AttributeChangeReportComparer){
         AttributeChangeReportComparer comparer = (AttributeChangeReportComparer)obj;
         
         if(this.artId == comparer.artId){
            if(this.attrId == comparer.attrId){
               compareResults = 0;
            } else if(this.attrId > comparer.attrId){
               compareResults = 1;
            }
         } else if(this.artId > comparer.artId){
            compareResults = 1;            
         }
      }
      return compareResults ;
   }
}
