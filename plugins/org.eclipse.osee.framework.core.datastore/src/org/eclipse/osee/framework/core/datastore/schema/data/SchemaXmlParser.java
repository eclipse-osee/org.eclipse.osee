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
package org.eclipse.osee.framework.core.datastore.schema.data;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.datastore.IOseeSchemaProvider;
import org.eclipse.osee.framework.core.datastore.IOseeSchemaResource;
import org.eclipse.osee.framework.core.datastore.schema.data.AppliesToClause.AppliesToEntries;
import org.eclipse.osee.framework.core.datastore.schema.data.AppliesToClause.OrderType;
import org.eclipse.osee.framework.core.datastore.schema.data.ConstraintElement.ConstraintFields;
import org.eclipse.osee.framework.core.datastore.schema.data.IndexElement.IndexFields;
import org.eclipse.osee.framework.core.datastore.schema.data.ReferenceClause.OnDeleteEnum;
import org.eclipse.osee.framework.core.datastore.schema.data.ReferenceClause.OnUpdateEnum;
import org.eclipse.osee.framework.core.datastore.schema.data.ReferenceClause.ReferencesFields;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement.ColumnFields;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement.TableDescriptionFields;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement.TableSections;
import org.eclipse.osee.framework.core.datastore.schema.data.TableElement.TableTags;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

/**
 * @author Roberto E. Escobar
 */
public class SchemaXmlParser {

   private void parseTableDescription(Element element, TableElement tableEntry) {
      NamedNodeMap attributes = element.getAttributes();
      for (int i = 0; i < attributes.getLength(); i++) {
         Attr attribute = (Attr) attributes.item(i);
         TableDescriptionFields tableField = TableDescriptionFields.valueOf(attribute.getName());
         tableEntry.addTableDescription(tableField, attribute.getValue());
      }
   }

   private void parseColumnEntries(Element element, TableElement tableEntry) {
      List<Element> columns = Jaxp.getChildDirects(element, TableSections.Column.name());
      for (Element column : columns) {
         NamedNodeMap attributes = column.getAttributes();
         ColumnMetadata columnMetadata = new ColumnMetadata("");
         for (int i = 0; i < attributes.getLength(); i++) {
            Attr attribute = (Attr) attributes.item(i);
            ColumnFields columnField = ColumnFields.valueOf(attribute.getName());
            columnMetadata.addColumnField(columnField, attribute.getValue().toUpperCase());
         }
         tableEntry.addColumn(columnMetadata);
      }
   }

   private void parseConstraintEntries(Element element, TableElement tableEntry) {
      List<Element> constraints = Jaxp.getChildDirects(element, TableSections.Constraint.name());
      for (Element constraint : constraints) {
         Attr type = constraint.getAttributeNode(ConstraintFields.type.name());
         Attr id = constraint.getAttributeNode(ConstraintFields.id.name());
         String schema = constraint.getAttribute(ConstraintFields.schema.name());
         String deferrable = constraint.getAttribute(ConstraintFields.deferrable.name());
         if (type != null && id != null) {

            ConstraintElement constraintElement =
               ConstraintFactory.getConstraint(ConstraintTypes.textToType(type.getValue().toUpperCase()),
                  schema.toUpperCase(), id.getValue().toUpperCase(),
                  deferrable.equalsIgnoreCase(Boolean.toString(true)));

            Attr appliesTo = constraint.getAttributeNode(ConstraintFields.appliesTo.name());
            if (appliesTo != null) {
               String[] columns = appliesTo.getValue().split(",");
               for (String column : columns) {
                  constraintElement.addColumn(column.toUpperCase());
               }
            }

            if (constraintElement instanceof ForeignKey) {
               Element reference = Jaxp.getChildDirect(constraint, ReferenceClause.REFERENCES_TAG);
               if (reference != null) {
                  Attr table = reference.getAttributeNode(ReferencesFields.table.name());
                  Attr refColumn = reference.getAttributeNode(ReferencesFields.column.name());
                  Attr onUpdate = reference.getAttributeNode(ReferencesFields.onUpdate.name());
                  Attr onDelete = reference.getAttributeNode(ReferencesFields.onDelete.name());
                  String refSchema = reference.getAttribute(ReferencesFields.schema.name());

                  if (table != null) {

                     ReferenceClause references =
                        new ReferenceClause(refSchema.toUpperCase(), table.getValue().toUpperCase());
                     if (refColumn != null) {
                        String[] columns = refColumn.getValue().split(",");
                        for (String column : columns) {
                           references.addColumn(column.toUpperCase());
                        }
                     }
                     if (onUpdate != null) {
                        OnUpdateEnum[] values = OnUpdateEnum.values();
                        for (OnUpdateEnum value : values) {
                           if (value.toString().equals(onUpdate.getValue().toUpperCase())) {
                              references.setOnUpdateAction(value);
                           }
                        }
                     }
                     if (onDelete != null) {
                        OnDeleteEnum[] values = OnDeleteEnum.values();
                        for (OnDeleteEnum value : values) {
                           if (value.toString().equals(onDelete.getValue().toUpperCase())) {
                              references.setOnDeleteAction(value);
                           }
                        }
                     }
                     ((ForeignKey) constraintElement).addReference(references);
                  }
               }
            }
            tableEntry.addConstraint(constraintElement);
         }
      }
   }

   private void parseIndexDataEntries(Element element, TableElement tableEntry) {
      List<Element> indexDataList = Jaxp.getChildDirects(element, TableSections.Index.name());
      for (Element indexDataEntry : indexDataList) {
         String id = indexDataEntry.getAttribute(IndexFields.id.name());
         String ignore = indexDataEntry.getAttribute(IndexFields.mySqlIgnore.name());
         Attr indexType = indexDataEntry.getAttributeNode(IndexFields.type.name());
         String tablespace = indexDataEntry.getAttribute(IndexFields.tablespace.name());
         if (id.length() > 0) {
            IndexElement indexData = new IndexElement(id);
            if (Boolean.parseBoolean(ignore)) {
               indexData.setMySqlIgnore(true);
            }
            if (indexType != null) {
               indexData.setIndexType(indexType.getValue());
            }
            if (tablespace != null) {
               indexData.setTablespace(tablespace);
            }
            parseAppliesToClause(indexDataEntry, indexData);
            tableEntry.addIndexData(indexData);
         }
      }
   }

   public void parseAppliesToClause(Element element, IndexElement indexData) {
      List<Element> appliesToList = Jaxp.getChildDirects(element, AppliesToClause.APPLIES_TO_TAG);
      for (Element appliesToElement : appliesToList) {
         String idString = appliesToElement.getAttribute(AppliesToEntries.id.name());
         String sortString = appliesToElement.getAttribute(AppliesToEntries.sort.name());
         if (idString.length() > 0) {
            OrderType orderType = OrderType.Undefined;
            if (sortString.equalsIgnoreCase("Ascending")) {
               orderType = OrderType.Ascending;
            } else if (sortString.equalsIgnoreCase("Descending")) {
               orderType = OrderType.Descending;
            }
            indexData.addAppliesTo(idString, orderType);
         }
      }
   }

   public SchemaData parseFromInputStream(InputStream configFile) throws Exception {
      Document document = Jaxp.readXmlDocument(configFile);
      SchemaData schemaData = new SchemaData();
      List<Element> elements = Jaxp.getChildDirects(document.getDocumentElement(), TableTags.Table.name());
      for (Element element : elements) {

         TableElement tableEntry = new TableElement();

         parseTableDescription(element, tableEntry);
         parseColumnEntries(element, tableEntry);
         parseConstraintEntries(element, tableEntry);
         parseIndexDataEntries(element, tableEntry);

         schemaData.addTableDefinition(tableEntry);
      }
      return schemaData;
   }

   public void parseFromSchemaProvider(IOseeSchemaProvider schemaProvider, Map<String, SchemaData> schemas) {
      for (IOseeSchemaResource schemaResource : schemaProvider.getSchemaResources()) {
         SchemaData schemaData;
         InputStream inputStream = null;
         try {
            inputStream = new BufferedInputStream(schemaResource.getContent());
            schemaData = parseFromInputStream(inputStream);
            List<TableElement> tables = schemaData.getTablesOrderedByDependency();
            for (TableElement table : tables) {
               String schemaAddress = table.getSchema();

               SchemaData schema = null;
               if (!schemas.containsKey(schemaAddress)) {
                  schema = new SchemaData();
                  schemas.put(schemaAddress, schema);
               } else {
                  schema = schemas.get(schemaAddress);
               }
               schema.addTableDefinition(table);
            }
         } catch (Exception e) {
            e.printStackTrace();
         } finally {
            Lib.close(inputStream);
         }
      }
   }
}
