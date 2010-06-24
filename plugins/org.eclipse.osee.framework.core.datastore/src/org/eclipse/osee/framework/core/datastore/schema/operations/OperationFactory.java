/*
 * Created on Jun 21, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.datastore.schema.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.osee.framework.core.datastore.IOseeSchemaProvider;
import org.eclipse.osee.framework.core.datastore.SchemaCreationOptions;
import org.eclipse.osee.framework.core.datastore.internal.Activator;
import org.eclipse.osee.framework.core.datastore.schema.data.SchemaData;
import org.eclipse.osee.framework.core.operation.CompositeOperation;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.database.IOseeDatabaseServiceProvider;

/**
 * @author Roberto E. Escobar
 */
public final class OperationFactory {

   private OperationFactory() {
   }

   public static IOperation createDbSchema(IOseeDatabaseServiceProvider provider, IOseeSchemaProvider schemaProvider, SchemaCreationOptions options) {
      Map<String, SchemaData> userSpecifiedConfig = new HashMap<String, SchemaData>();
      Map<String, SchemaData> currentDatabaseConfig = new HashMap<String, SchemaData>();

      Collection<IOperation> ops = new ArrayList<IOperation>();
      ops.add(new LoadUserSchemasOperation(userSpecifiedConfig, schemaProvider, options));
      ops.add(new ExtractDatabaseSchemaOperation(provider, userSpecifiedConfig.keySet(), currentDatabaseConfig));
      ops.add(new CreateSchemaOperation(provider, userSpecifiedConfig, currentDatabaseConfig));

      return new CompositeOperation("Create OSEE Schema", Activator.PLUGIN_ID, ops);
   }
}
