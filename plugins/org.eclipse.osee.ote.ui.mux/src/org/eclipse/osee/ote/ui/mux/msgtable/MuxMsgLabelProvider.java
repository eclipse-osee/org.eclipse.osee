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
package org.eclipse.osee.ote.ui.mux.msgtable;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Ky Komadino
 * 
 */
public class MuxMsgLabelProvider extends LabelProvider implements ITableLabelProvider {
   public String getColumnText(Object obj, int index) {
      if (obj != null && obj instanceof MessageNode) {
         switch (index) {
            case 0:
               return ((MessageNode)obj).getName();
            case 1:
               return ((MessageNode)obj).getRtRt();
            case 2:
               return String.valueOf(((MessageNode)obj).getWordCount());
            case 3:
               return ((MessageNode)obj).getStatusWord();
            case 4:
               return ((MessageNode)obj).getEmulation();
            case 5:
               return ((MessageNode)obj).getBus();
            case 6:
               return String.valueOf(((MessageNode)obj).getActivity());
            case 7:
               return String.valueOf(((MessageNode)obj).getErrCount());
            case 8:
               return ((MessageNode)obj).getErrType();
            default:
               return "";
         }
      }
      else
         return "";
   }

   public Image getColumnImage(Object obj, int index) {
      return getImage(obj);
   }

   public String getText(Object obj) {
      return ((MessageNode)obj).getName();
   }
}
