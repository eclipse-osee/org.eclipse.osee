/*
 * Created on Jan 19, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render;

import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Theron Virgin
 */
public class WordmlPicture {
   private int pictureStartIndex;
   private String binaryData;
   private String pictureDefinition;

   public WordmlPicture() {

   }

   public WordmlPicture(int pictureStartIndex, String pictureDefinition, String document) throws OseeCoreException {
      this.pictureStartIndex = pictureStartIndex;
      this.pictureDefinition = pictureDefinition;
      findBinaryData(document);
   }

   private void findBinaryData(String document) throws OseeCoreException {
      if (pictureDefinition.contains("<w:binData")) {
         int index = pictureDefinition.indexOf(">", pictureDefinition.indexOf("<w:binData")) + 1;
         binaryData = pictureDefinition.substring(index, pictureDefinition.indexOf("<", index));
      } else if (pictureDefinition.contains("<v:imagedata")) {
         int index = pictureDefinition.indexOf("src=\"", pictureDefinition.indexOf("<v:imagedata")) + 5;
         String pictureId = pictureDefinition.substring(index, pictureDefinition.indexOf("\"", index));
         int dataIndex = document.indexOf("<w:binData w:name=\"" + pictureId + "\"");
         if (dataIndex < 0) {
            throw new OseeCoreException(
                  "This document contains undefined picture data.  Please report details to OSEE development team.");
         }
         binaryData =
               document.substring(document.indexOf(">", dataIndex) + 1, document.indexOf("<", document.indexOf(">",
                     dataIndex) + 1));
      } else {
         throw new OseeCoreException(
               "This document contains undefined picture data.  Please report details to OSEE development team.");
      }
   }

   public int getStartIndex() {
      return pictureStartIndex;
   }

   public String getBinaryData() {
      return binaryData;
   }

}
