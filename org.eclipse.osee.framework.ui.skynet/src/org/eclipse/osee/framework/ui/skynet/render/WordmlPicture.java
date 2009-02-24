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
package org.eclipse.osee.framework.ui.skynet.render;

import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;

/**
 * @author Theron Virgin
 */
public class WordmlPicture {
   private int pictureStartIndex;
   private String binaryData;
   private String pictureDefinition;
   private Attribute attribute;
   private static final boolean DEBUG =
         "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.ui.skynet/debug/Word"));

   public WordmlPicture() {

   }

   public WordmlPicture(int pictureStartIndex, String pictureDefinition, String document, Attribute attribute) throws OseeCoreException {
      this.pictureStartIndex = pictureStartIndex;
      this.pictureDefinition = pictureDefinition;
      this.attribute = attribute;
      findBinaryData(document);
   }

   private void findBinaryData(String document) throws OseeCoreException {
      if (pictureDefinition.contains("<v:textbox ") || pictureDefinition.contains("<v:rect ") || pictureDefinition.contains("<v:line ")) {
         //ignore this case
      } else if (pictureDefinition.contains("<w:binData")) {
         int index = pictureDefinition.indexOf(">", pictureDefinition.indexOf("<w:binData")) + 1;
         binaryData = pictureDefinition.substring(index, pictureDefinition.indexOf("<", index));
      } else if (pictureDefinition.contains("<v:imagedata")) {
         int index = pictureDefinition.indexOf("src=\"", pictureDefinition.indexOf("<v:imagedata")) + 5;
         String pictureId = pictureDefinition.substring(index, pictureDefinition.indexOf("\"", index));
         int dataIndex = document.indexOf("<w:binData w:name=\"" + pictureId + "\"");
         if (dataIndex < 0) {
            if (DEBUG) {
               System.out.println(pictureDefinition);
            }
            throw new OseeCoreException(
                  "This document is missing Image Data.  The Image can not be checked for modifications    Artifact =>" + attribute.getArtifact().getSafeName() + "  " + attribute.getArtifact().getArtId());
         }
         binaryData =
               document.substring(document.indexOf(">", dataIndex) + 1, document.indexOf("<", document.indexOf(">",
                     dataIndex) + 1));
      } else {
         if (!(pictureDefinition.contains("<v:formulas>") || pictureDefinition.contains("<v:path ") || pictureDefinition.contains("<v:textbox ") || pictureDefinition.contains("<v:rect ") || pictureDefinition.contains("<v:line "))) {
            if (DEBUG) {
               System.out.println(pictureDefinition);
            }
            throw new OseeCoreException(
                  "This document contains undefined picture data.  Please report details to OSEE development team.    Artifact=> " + attribute.getArtifact().getSafeName() + "  " + attribute.getArtifact().getArtId());
         }
      }
   }

   public int getStartIndex() {
      return pictureStartIndex;
   }

   public String getBinaryData() {
      return binaryData;
   }

}
