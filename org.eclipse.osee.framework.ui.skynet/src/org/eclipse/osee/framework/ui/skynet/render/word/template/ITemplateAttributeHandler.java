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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.exception.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.exception.MultipleAttributesExist;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * @author b1528444
 */
public interface ITemplateAttributeHandler {

   void process(WordMLProducer wordMl, Artifact artifact, TemplateAttribute attribute) throws SQLException, IllegalStateException, IOException, MultipleAttributesExist, AttributeDoesNotExist;

   boolean canHandle(Artifact artifact, TemplateAttribute attribute) throws SQLException;
}
