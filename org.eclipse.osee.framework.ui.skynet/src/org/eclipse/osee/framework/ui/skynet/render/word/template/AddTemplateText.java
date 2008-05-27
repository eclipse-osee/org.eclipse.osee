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

import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * @author b1528444
 */
public class AddTemplateText implements ITemplateTask {

   private String template;
   private int begin;
   private int end;

   /**
    * @param last
    * @param start
    * @param template
    */
   public AddTemplateText(int begin, int end, String template) {
      this.template = template;
      this.end = end;
      this.begin = begin;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateTask#process(java.lang.StringBuilder, org.eclipse.osee.framework.skynet.core.artifact.Artifact, java.util.List)
    */
   @Override
   public void process(WordMLProducer wordMl, Artifact artifact, List<ITemplateAttributeHandler> handlers) throws SQLException, Exception {
      wordMl.addWordMl(template.subSequence(begin, end));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.render.word.template.ITemplateTask#isTypeNameWildcard()
    */
   @Override
   public boolean isTypeNameWildcard() {
      return false;
   }

}
