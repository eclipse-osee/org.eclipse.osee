/*
 * Created on Mar 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.render.word.template;

import java.sql.SQLException;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.render.word.WordMLProducer;


/**
 * @author b1528444
 *
 */
public interface ITemplateTask {

	void process(WordMLProducer wordMl, Artifact artifact, List<ITemplateAttributeHandler> handlers) throws SQLException, Exception;

   /**
    * @return
    */
   boolean isTypeNameWildcard();

}
