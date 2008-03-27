/*
 * Created on Mar 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.word.template;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.DynamicAttributeManager;
import org.eclipse.osee.framework.skynet.core.attribute.WordAttribute;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;
import org.eclipse.osee.framework.ui.skynet.render.word.WordTemplateProcessor;

/**
 * @author b1528444
 */
public final class WordAttributeTypeAttributeHandler implements ITemplateAttributeHandler {

   private final Set<String> ignoreAttributeExtensions;

   public WordAttributeTypeAttributeHandler() {
      this.ignoreAttributeExtensions = new HashSet<String>();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeHandler#process(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.render.word.template.TemplateAttribute)
    */
   @Override
   public void process(WordMLProducer wordMl, Artifact artifact, TemplateAttribute templateAttribute) throws SQLException, IllegalStateException, IOException {
      DynamicAttributeManager dynamicAttributeManager = artifact.getAttributeManager(templateAttribute.getName());
      Collection<Attribute<Object>> attributes = dynamicAttributeManager.getAttributes();
//if(true)return;
      if (!attributes.isEmpty()) {
         Attribute<Object> attribute = attributes.iterator().next();
         DynamicAttributeDescriptor attributeType = attribute.getAttributeType();

         // check if the attribute descriptor name is in the ignore list.
         if (ignoreAttributeExtensions.contains(attributeType.getName())) {
            return;
         }

         if (templateAttribute.hasLabel()) {
            wordMl.addParagraph(templateAttribute.getLabel());
         }

         if(false){
         WordTemplateProcessor.writeXMLMetaDataWrapper(wordMl,
                  WordTemplateProcessor.elementNameFor(attributeType.getName()),
                  "ns0:guid=\"" + artifact.getGuid() + "\"", "ns0:attrId=\"" + attributeType.getAttrTypeId() + "\"",
                  attribute.toString());
         } else {
        	 wordMl.addWordMl(attribute.toString());
         }
         wordMl.resetListValue();
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateAttributeNameHandler#canHandle(org.eclipse.osee.framework.skynet.core.artifact.Artifact, org.eclipse.osee.framework.ui.skynet.render.word.template.TemplateAttribute)
    */
   @Override
   public boolean canHandle(Artifact artifact, TemplateAttribute attribute) throws SQLException {
      try {
         DynamicAttributeManager dam = artifact.getAttributeManager(attribute.getName());
         return dam.getAttributeType().getName().equals(WordAttribute.CONTENT_NAME);
      } catch (IllegalArgumentException ex) {
         return false;
      }

   }

}
