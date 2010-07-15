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
package org.eclipse.osee.framework.core.datastore.schema.operations;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.IDatabaseInfo;
import org.eclipse.osee.framework.core.datastore.internal.Activator;
import org.eclipse.osee.framework.core.datastore.schema.data.SchemaData;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.DatabaseInfoManager;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class ImportDataFromDbServiceOperation extends AbstractOperation {

	private static final File backupDirectory = new File("BackupDirectory");

	private final Map<String, SchemaData> userSpecifiedConfig;
	private final String tableImportSource;
	private final IOseeDatabaseService databaseService;

	public ImportDataFromDbServiceOperation(IOseeDatabaseService databaseService, Map<String, SchemaData> userSpecifiedConfig, String tableImportSource) {
		super("Import Data from Db Service", Activator.PLUGIN_ID);
		this.databaseService = databaseService;
		this.userSpecifiedConfig = userSpecifiedConfig;
		this.tableImportSource = tableImportSource;
	}

	@Override
	protected void doWork(IProgressMonitor monitor) throws Exception {
		Set<String> importConnections = getImportConnections();
		for (String importFromDbService : importConnections) {
			System.out.println("Import Table Data from Db: " + importFromDbService);

			IDatabaseInfo dbInfo = DatabaseInfoManager.getDataStoreById(importFromDbService);
			System.out.println("Gathering information from ..." + importFromDbService);

			String userName = dbInfo.getDatabaseLoginName();
			if (userName != null && !userName.equals("")) {

				Set<String> schemasToGet = new TreeSet<String>();
				schemasToGet.add(userName.toUpperCase());

				Map<String, Set<String>> dataToImport = getTablesToImport(monitor, userName.toUpperCase(), schemasToGet);
				if (dataToImport.size() > 0) {
					System.out.println(dataToImport.toString().replaceAll(", ", "\n"));
					makeBackupDirectoryIfItDoesntExist();

					System.out.println("Backing up Files to: " + backupDirectory.getAbsolutePath());
					DatabaseDataExtractor dbDataExtractor =
								new DatabaseDataExtractor(databaseService, schemasToGet, backupDirectory);

					Set<String> tablesToImport;
					if (importFromDbService.equals(determineDefaultConnection())) {
						tablesToImport = dataToImport.get(tableImportSource);
					} else {
						tablesToImport = dataToImport.get(importFromDbService);
					}

					for (String importTable : tablesToImport) {
						dbDataExtractor.addTableNameToExtract(importTable);
					}
					doSubWork(dbDataExtractor, monitor, 0.10);
					dbDataExtractor.waitForWorkerThreads();

					prepareFilesForImport();
				}
			}
		}
	}

	private void prepareFilesForImport() {
		Set<String> keys = userSpecifiedConfig.keySet();
		if (keys.size() == 1) {
			String userName = "";
			for (String temp : keys) {
				userName = temp;
			}
			List<File> files = FileUtility.getDBDataFileList(backupDirectory);
			for (File fileName : files) {
				String filename = fileName.getAbsolutePath().toString();
				filename = filename.substring(filename.lastIndexOf(File.separator) + 1, filename.length());
				filename = filename.substring(filename.indexOf(".") + 1, filename.length());
				fileName.renameTo(new File(backupDirectory + File.separator + userName + "." + filename));
			}
		}
	}

	private String determineDefaultConnection() {
		String importFromDbService = System.getProperty(tableImportSource);
		if (!Strings.isValid(importFromDbService)) {
			importFromDbService = "oracle";
		}
		return importFromDbService;
	}

	private Set<String> getImportConnections() {
		String defaultConnection = determineDefaultConnection();
		Set<String> userSchemas = userSpecifiedConfig.keySet();
		Set<String> connectionsNeeded = new TreeSet<String>();
		for (String key : userSchemas) {
			SchemaData schemaDataInUserConfig = userSpecifiedConfig.get(key);
			Map<String, Set<String>> tableNamesToImport = schemaDataInUserConfig.getTablesToImport(tableImportSource);
			Set<String> keys = tableNamesToImport.keySet();
			for (String connectionString : keys) {
				if (connectionString.equals(tableImportSource)) {
					connectionsNeeded.add(defaultConnection);
				} else {
					connectionsNeeded.add(connectionString);
				}
			}
		}
		return connectionsNeeded;
	}

	public boolean canRun() {
		return true;
	}

	private Map<String, SchemaData> getAvailableSchemasFromImportDb(IProgressMonitor monitor, Set<String> schemas) throws Exception {
		Map<String, SchemaData> schemaMap = new HashMap<String, SchemaData>();
		ExtractDatabaseSchemaOperation schemaExtractor =
					new ExtractDatabaseSchemaOperation(databaseService, schemas, schemaMap);
		doSubWork(schemaExtractor, monitor, 0.20);
		return schemaMap;
	}

	private Map<String, Set<String>> getTablesToImport(IProgressMonitor monitor, String userName, Set<String> schemasToGet) throws Exception {
		Map<String, SchemaData> currentDbSchemas = getAvailableSchemasFromImportDb(monitor, schemasToGet);
		Set<String> userSchemas = userSpecifiedConfig.keySet();

		SchemaData schemaData = currentDbSchemas.get(userName);
		Map<String, TableElement> tableMap = schemaData.getTableMap();

		Map<String, Set<String>> importTables = new HashMap<String, Set<String>>();
		for (String key : userSchemas) {
			SchemaData schemaDataInUserConfig = userSpecifiedConfig.get(key);
			Map<String, Set<String>> tableNamesToImport = schemaDataInUserConfig.getTablesToImport(tableImportSource);

			Set<String> keys = tableNamesToImport.keySet();
			for (String importKey : keys) {
				Set<String> namesToImport = tableNamesToImport.get(importKey);

				for (String tableName : namesToImport) {
					tableName = tableName.replaceAll(key + "\\.", userName + ".");

					if (tableMap.containsKey(tableName)) {
						Set<String> tableSet;
						if (importTables.containsKey(importKey)) {
							tableSet = importTables.get(importKey);
						} else {
							tableSet = new TreeSet<String>();
						}
						tableSet.add(tableName);
						importTables.put(importKey, tableSet);
					}
				}
			}
		}
		return importTables;
	}

	private void makeBackupDirectoryIfItDoesntExist() {
		if (backupDirectory != null && backupDirectory.exists() && backupDirectory.canWrite()) {
			return;
		} else {
			backupDirectory.mkdirs();
		}
	}

}