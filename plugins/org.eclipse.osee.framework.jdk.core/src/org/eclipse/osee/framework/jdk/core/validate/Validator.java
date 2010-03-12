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
package org.eclipse.osee.framework.jdk.core.validate;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Validator {

   private InputStream fileToRead;
   private Map<String, List<IValidator>> validatorMap;

   private enum EntryEnum {
      field;
   }

   private enum FieldAttributes {
      name;
   }

   private enum FieldEntries {
      validator;
   }

   private enum FieldValidatorAttributes {
      type;
   }

   private enum FieldValidatorEntries {
      message, param;
   }

   private enum ParamAttributes {
      name;
   }

   private enum ValidationTypes {
      requiredstring, required, expression;
   }

   public Validator(InputStream fileToRead) throws Exception {
      this.fileToRead = fileToRead;
      this.validatorMap = new HashMap<String, List<IValidator>>();
      parseFile();
   }

   private void storeValidator(String fieldName, IValidator validator) {
      List<IValidator> list = null;
      if (!validatorMap.containsKey(fieldName)) {
         list = new ArrayList<IValidator>();
         validatorMap.put(fieldName, list);
      } else {
         list = validatorMap.get(fieldName);
      }
      list.add(validator);
   }

   private void parseMessage(Element element, FieldValidator validator) {
      NodeList messageList = element.getElementsByTagName(FieldValidatorEntries.message.name());
      if (messageList.getLength() == 1) {
         Node node = messageList.item(0);
         if (node != null) {
            String message = node.getTextContent();
            if (message != null && !message.equals("")) {
               validator.setMessage(message.trim());
            }
         }
      }
   }

   private void parseParams(Element element, FieldValidator validator) {
      NodeList paramList = element.getElementsByTagName(FieldValidatorEntries.param.name());
      for (int i = 0; i < paramList.getLength(); i++) {
         Element paramElement = (Element) paramList.item(i);
         String paramName = paramElement.getAttribute(ParamAttributes.name.name());
         if (paramName != null && !paramName.equals("")) {
            String content = paramElement.getTextContent();
            if (content != null && !content.equals("")) {
               validator.addParam(paramName.trim(), content.trim());
            }
         }
      }
   }

   private void parseValidator(Element element, String fieldName, String validatorType) {
      FieldValidator validator = validatorFactory(fieldName, validatorType);
      if (validator != null) {
         parseMessage(element, validator);
         parseParams(element, validator);
         storeValidator(fieldName, validator);
      }
   }

   private void parseFile() throws Exception {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(fileToRead);

      NodeList fields = document.getElementsByTagName(EntryEnum.field.name());

      for (int i = 0; i < fields.getLength(); i++) {

         Element fieldElement = (Element) fields.item(i);
         String fieldName = fieldElement.getAttribute(FieldAttributes.name.name());
         if (fieldName != null && !fieldName.equals("")) {

            NodeList validators = fieldElement.getElementsByTagName(FieldEntries.validator.name());
            for (int j = 0; j < validators.getLength(); j++) {
               Element validatorElement = (Element) validators.item(j);

               String validatorType = validatorElement.getAttribute(FieldValidatorAttributes.type.name());
               if (validatorType != null && !validatorType.equals("")) {
                  parseValidator(validatorElement, fieldName.trim(), validatorType.trim());
               }
            }
         }
      }
   }

   private FieldValidator validatorFactory(String name, String type) {
      FieldValidator toReturn = null;
      ValidationTypes validationType = ValidationTypes.valueOf(type);
      switch (validationType) {
         case requiredstring:
            toReturn = new RequiredStringValidator(name);
            break;
         case required:
            toReturn = new RequiredValidator(name);
            break;
         case expression:
            toReturn = new ExpressionValidator(name);
         default:
            break;
      }
      return toReturn;
   }

   public List<IValidator> getValidator(String fieldName) {
      List<IValidator> list = validatorMap.get(fieldName);
      return (list != null ? list : new ArrayList<IValidator>());
   }
}
