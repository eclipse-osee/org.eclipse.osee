/*
 * Created on Mar 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.word.template;

import java.io.IOException;
import java.sql.SQLException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.util.AttributeDoesNotExist;
import org.eclipse.osee.framework.skynet.core.util.MultipleAttributesExist;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;

/**
 * @author b1528444
 */
public interface ITemplateAttributeHandler {

   void process(WordMLProducer wordMl, Artifact artifact, TemplateAttribute attribute) throws SQLException, IllegalStateException, IOException, MultipleAttributesExist, AttributeDoesNotExist;

   boolean canHandle(Artifact artifact, TemplateAttribute attribute) throws SQLException;
}
