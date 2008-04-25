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
package org.eclipse.osee.framework.database.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.osee.framework.jdk.core.persistence.Xmlizable;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class TableElement implements Xmlizable {
   
   public enum TableTags{
      Table, ColumnInfo, Row;
   }
   
   public enum TableSections {
      Column, Constraint, Index;
   };
   
   public enum TableDescriptionFields {
      name, schema, backupData, importData, importFrom;
   };

   public enum ColumnFields {
      id, type, limits, identity, defaultValue;;
   };
   
   private Map<TableDescriptionFields, String> tableDescription;
   private Map<String, ColumnMetadata> columns;
   private List<ConstraintElement> constraints;
   private List<ForeignKey> foreignKeys;
   private List<IndexElement> indeces;

   public TableElement() {
      this.tableDescription = new HashMap<TableDescriptionFields, String>();
      this.columns = new HashMap<String, ColumnMetadata>();
      this.constraints = new ArrayList<ConstraintElement>();
      this.foreignKeys = new ArrayList<ForeignKey>();
      this.indeces = new ArrayList<IndexElement>();
   }

   public void addColumn(ColumnMetadata column) {
      this.columns.put(column.getId(), column);
   }
   
   public void addConstraint(ConstraintElement constraint) {
      if(constraint instanceof ForeignKey){
         foreignKeys.add((ForeignKey)constraint);
      } else {
         constraints.add(constraint);
      }
   }
   
   public void addIndexData(IndexElement indexData){
      indeces.add(indexData);
   }

   public String getSchema() {
      return this.tableDescription.get(TableDescriptionFields.schema);
   }

   public String getName() {
      return this.tableDescription.get(TableDescriptionFields.name);
   }
   
   public boolean isBackupDataSet(){
      if(this.tableDescription.containsKey(TableDescriptionFields.backupData)){
         return Boolean.parseBoolean(this.tableDescription.get(TableDescriptionFields.backupData));
      }
      return false;
   }
   
   public boolean isImportDataSet(){
      if(this.tableDescription.containsKey(TableDescriptionFields.importData)){
         return Boolean.parseBoolean(this.tableDescription.get(TableDescriptionFields.importData));
      }
      return false;
   }
   
   public String getImportFrom(){
      if(this.tableDescription.containsKey(TableDescriptionFields.importFrom)){
         return this.tableDescription.get(TableDescriptionFields.importFrom);
      }     
      return OseeProperties.OSEE_IMPORT_FROM_DB_SERVICE;
   }
   
   public void addTableDescription(TableDescriptionFields field, String value){
      if(!field.equals(TableDescriptionFields.importFrom)){
         value = value.toUpperCase();
      }
      value = value.trim();
      this.tableDescription.put(field, value);
   }
   
   public Map<String, ColumnMetadata> getColumns(){
      return this.columns;
   }
   
   public Map<TableDescriptionFields, String> getDescription(){
      return this.tableDescription;
   }
   
   public boolean hasForeignKey(){
      return foreignKeys.size() > 0;
   }
   
   public String getFullyQualifiedTableName(){
	   if(getSchema()==null)
		   return getName();
	   else
		   return getSchema() + "." + getName();
   }
      
   public List<ConstraintElement> getConstraints(){
      return constraints;
   }
   
   public List<ForeignKey> getForeignKeyConstraints(){
      return foreignKeys;
   }
         
   public List<IndexElement> getIndexData(){
      return indeces;
   }
   
   public String toString(){
      StringBuilder toReturn = new StringBuilder();
      toReturn.append(" Table : \n");
      Set<TableDescriptionFields> keys = tableDescription.keySet();
      for(TableDescriptionFields key : keys){
         toReturn.append(" \t" + key.toString() + ": " + tableDescription.get(key));
      }
      toReturn.append("\n");
      int count = 0;
      Set<String> columnKeys = columns.keySet();
      for(String key : columnKeys){
         toReturn.append("\t[" + ++count + "] " + columns.get(key).toString() + "\n");            
      }
      count = 0;
      for(ConstraintElement constraint : constraints){
         toReturn.append("\t[" + ++count + "] " + constraint.toString() + "\n");            
      }
      for(ForeignKey fkeys : foreignKeys){
         toReturn.append("\t[" + ++count + "] " + fkeys.toString() + "\n");            
      }
      count = 0;
      for(IndexElement iData : indeces){
         toReturn.append("\t[" + ++count + "] " + iData.toString() + "\n");
      }
      return toReturn.toString();
   }
      
   /**
    * @param doc The XML document we're creating the XML for
    * @return The XML Element corresponding to this
    */
   public Element toXml(Document doc) {
      Element tableElement = doc.createElement(TableTags.Table.name());
      for(TableDescriptionFields key : tableDescription.keySet()){
         tableElement.setAttribute(key.toString(), tableDescription.get(key));
      }
      for(String key : columns.keySet()) {
         tableElement.appendChild(columns.get(key).toXml(doc));
      }
      for(ConstraintElement constraint : constraints){
         tableElement.appendChild(constraint.toXml(doc));
      }
      for(ForeignKey constraint : foreignKeys){
         tableElement.appendChild(constraint.toXml(doc));
      }
      for(IndexElement iData : indeces){
         tableElement.appendChild(iData.toXml(doc));
      }
      return tableElement;
   }
   
   public Set<String> getTableDependency(){
      Set<String> dependency = new TreeSet<String>();
      for(ForeignKey fk : foreignKeys){
         Set<String> refTables = fk.getReferencedTables();
         for(String tableName : refTables){
            dependency.add(tableName);
         }
      }
      return dependency;
   }
   
      
   @Override
   public boolean equals(Object otherObject) {
      if (otherObject instanceof TableElement == false) {
         return false;
      }
      if (this == otherObject) {
         return true;
      }
      TableElement that = (TableElement) otherObject;
      return new EqualsBuilder().appendSuper(super.equals(otherObject))
            .append(this.tableDescription, that.getDescription())
            .append(this.columns, that.getColumns())
            .append(this.constraints, that.getConstraints())
            .append(this.foreignKeys, that.getForeignKeyConstraints())
            .append(this.indeces, that.getIndexData())
      .isEquals();
   }

   @Override
   public int hashCode() {
      return new HashCodeBuilder(79, 17).append(tableDescription).append(columns)
      .append(constraints).append(foreignKeys).append(indeces).toHashCode();
   }
}
