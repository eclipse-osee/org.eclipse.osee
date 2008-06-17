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

package org.eclipse.osee.framework.ui.skynet.render.word.template;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeType;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * @author b1528444
 */
public final class BasicTemplateAttributeHandler implements ITemplateAttributeHandler {

   private final Set<String> ignoreAttributeExtensions;

   public BasicTemplateAttributeHandler() {
      this.ignoreAttributeExtensions = new HashSet<String>();
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeHandler#process(org.eclipse.osee.framework.skynet.core.artifact.Artifact,
    *      org.eclipse.osee.framework.ui.skynet.render.word.template.TemplateAttribute)
    */
   @Override
   public void process(WordMLProducer wordMl, Artifact artifact, TemplateAttribute templateAttribute) throws SQLException, IllegalStateException, IOException {
	   AttributeType attributeType = AttributeTypeManager.getTypeWithWordContentCheck(artifact,templateAttribute.getName());
	   Collection<Attribute<Object>> attributes = artifact.getAttributes(attributeType.getName());
       if (!attributes.isEmpty()) {
         Attribute<Object> attribute = attributes.iterator().next();
         attributeType = attribute.getAttributeType();

         
         
         // check if the attribute descriptor name is in the ignore list.
         if (ignoreAttributeExtensions.contains(attributeType.getName())) {
            return;
         }

         if (templateAttribute.isParagrapthWrap()) {
            wordMl.startParagraph();
         }
         // assumption: the label is of the form <w:r><w:t> text </w:t></w:r>
         //         if (allAttrs) {
         if (templateAttribute.hasLabel()) {
            wordMl.addWordMl("<w:r><w:t> " + templateAttribute.getName() + ": </w:t></w:r>");
         }
         //         } else {
         //            if (templateAttribute.hasLabel()) {
         //               wordMl.addParagraph(templateAttribute.getLabel());
         //            }
         //    
         String valueList;
         if(attributeType.getName().equals(WordAttribute.WORD_TEMPLATE_CONTENT)){
			wordMl.addWordMl((String)attribute.getValue());
         } else {
        	 valueList = Collections.toString(", ", artifact.getAttributes(templateAttribute.getName()));
        	 if (templateAttribute.hasFormatting()) {
                 if (templateAttribute.getFormat().contains(">x<")) {
                    wordMl.addWordMl(templateAttribute.getFormat().replace(">x<", ">" + valueList + "<"));
                 }
              } else {
                 wordMl.addTextInsideParagraph(valueList);
              }
         }
         if (templateAttribute.isParagrapthWrap()) {
            wordMl.endParagraph();
         }

      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeNameHandler#canHandle(org.eclipse.osee.framework.skynet.core.artifact.Artifact,
    *      org.eclipse.osee.framework.ui.skynet.render.word.template.TemplateAttribute)
    */
   @Override
   public boolean canHandle(Artifact artifact, TemplateAttribute attribute) {
      return true;
   }

}
