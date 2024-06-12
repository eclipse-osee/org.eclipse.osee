/*********************************************************************
 * Copyright (c) 2024 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.orcs.rest.internal.applicability;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.ws.rs.Path;
import org.eclipse.osee.framework.core.applicability.ConfigurationValue;
import org.eclipse.osee.framework.core.applicability.FeatureAttribute;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.applicability.FeatureSelectionWithConstraints;
import org.eclipse.osee.framework.core.applicability.FeatureValue;
import org.eclipse.osee.framework.core.applicability.NamedIdWithGamma;
import org.eclipse.osee.framework.core.applicability.ProductLineConfig;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ConfigurationGroupDefinition;
import org.eclipse.osee.framework.core.data.CreateViewDefinition;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.jdbc.JdbcDbType;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.model.ApplicabilityWebEndpoint;

@Path("applicability")
public class ApplicabilityWebEndpointImpl implements ApplicabilityWebEndpoint {

   private final OrcsApi orcsApi;
   private final BranchId branch;

   public ApplicabilityWebEndpointImpl(OrcsApi orcsApi, BranchId branch) {
      this.orcsApi = orcsApi;
      this.branch = branch;
   }

   @Override
   public ProductLineConfig getTable(String filter, ArtifactId viewId, long pageNum, long pageSize) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      String pleAccess = orcsApi.getSystemProperties().getValue("ple.access");
      if (!pleAccess.isEmpty() && pleAccess.equals("SINGLE") && viewId.isInvalid()) {
         return new ProductLineConfig(new ArrayList<FeatureValue>());
      }
      JdbcDbType dbUtils = orcsApi.getJdbcService().getClient().getDbType();
      String paginationSql = pageNum != 0L && pageSize != 0L ? "WHERE rn between ? and ?" : "";
      String filterSql = !filter.isBlank() & filter.contains("=") ? "kv.value LIKE ? AND\n" : "";
      String filterAttrSql = !filter.isBlank() & !filter.contains("=") ? "AND attrFeature.value LIKE ? \n" : "";
      String viewSql = viewId.isValid() ? "attrConfiguration.art_id = ? AND\n" : "";
      String configurationArray = dbUtils.json_agg(dbUtils.jsonb_object("id", "attrConfiguration.art_id", "name",
         "attrConfiguration.value", "gamma_id", "cfg.gamma_id", "type_id", "cfg.art_type_id", "applicability",
         dbUtils.json_object("id", "kv.key", "name", "kv.value", "gamma_id", "t2_1.gamma_id"), "related",
         "cfg.related"));
      String featureArray = dbUtils.json_agg(dbUtils.jsonb_object("id", "attrFeature.attr_id", "type_id",
         dbUtils.cast("attrFeature.attr_type_id", dbUtils.longToString()), "gamma_id", "attrFeature.gamma_id", "value",
         "attrFeature.value"));
      String paginationOrdering = dbUtils.isPaginationOrderingSupported() ? "ORDER BY applic.name,applic.id DESC" : "";
      String keyValueSplit = dbUtils.split_part("kv2.value", "=", 1);
      String emptyArray = dbUtils.jsonb_array();
      //@formatter:off
      String sql = "WITH\n" +
"artConfiguration AS (\n"+
      "SELECT art.art_id, art.gamma_id, art.art_type_id,"+emptyArray+" as related FROM osee_artifact art, osee_txs txs\r\n" +
      "   WHERE\r\n" +
      "   txs.tx_current =? AND \r\n" +
      "   txs.gamma_id = art.gamma_id AND \r\n" +
      "   txs.branch_id = ? AND\r\n" +
      "   art.art_type_id = ?\r\n" +
      "),\r\n" +
      "artGroup AS (\r\n" +
      "   SELECT art.art_id, art.gamma_id, art.art_type_id,"+dbUtils.json_agg("rel.b_art_id")+" as related FROM osee_artifact art, osee_txs txs, osee_relation_link rel\r\n" +
      "   WHERE\r\n" +
      "   txs.tx_current =? AND \r\n" +
      "   txs.gamma_id = art.gamma_id AND \r\n" +
      "   txs.branch_id = ? AND\r\n" +
      "   art.art_type_id = ? AND\r\n" +
      "   rel.a_art_id = art.art_id AND\r\n" +
      "   rel.rel_link_type_id = ? \r\n"+
      "   GROUP BY art.art_id, art.gamma_id, art.art_type_id"+
      "),\n"+
      "artConfigs AS (\r\n" +
         "SELECT * from artConfiguration UNION ALL SELECT * from artGroup),\n"+
      "attrConfigs AS (\r\n" +
      "SELECT attr.* FROM artConfigs cfg, osee_attribute attr, osee_txs txs WHERE \r\n" +
      "   attr.art_id = cfg.art_id and \r\n" +
      "   txs.gamma_id = attr.gamma_id and \r\n" +
      "   txs.tx_current =? and \r\n" +
      "   txs.branch_id = ? AND"+
      "   attr.attr_type_id = ? ),\r\n" +
      "artFeatures AS (\r\n" +
      "   SELECT * from osee_artifact art, osee_txs txs\r\n" +
      "   WHERE\r\n" +
      "   txs.tx_current =? AND \r\n" +
      "   txs.gamma_id = art.gamma_id AND \r\n" +
      "   txs.branch_id = ? AND\r\n" +
      "   art.art_type_id = ?\r\n" +
      "   ),\r\n" +
      "attrFeatures AS (\r\n" +
      "   SELECT attr.* FROM artFeatures feat, osee_attribute attr, osee_txs txs WHERE \r\n" +
      "   attr.art_id = feat.art_id and \r\n" +
      "   txs.gamma_id = attr.gamma_id and \r\n" +
      "   txs.tx_current =? and \r\n" +
      "   txs.branch_id = ?\r\n" +
      "),\n"+
         "nonCompoundApplicabilities as (\n" +
         "SELECT t2_2.e1 as id,"+keyValueSplit+" as name,\n" +
         configurationArray +" as configuration_values\n" +
         "from \n" +
         "osee_tuple2 t2_1 , osee_tuple2 t2_2 , osee_txs txs1, osee_txs txs2, osee_key_value kv, osee_key_value kv2,\n" +
         "   artConfigs cfg,\n" +
         "   attrConfigs attrConfiguration\n" +
         "WHERE \n" +
         "t2_1.e2 = t2_2.e2 AND \n" +
         "t2_1.tuple_type = ? AND \n" +
         "t2_2.tuple_type = ? AND \n" +
         "t2_2.e1 > ? AND\n" +
         "txs1.gamma_id = t2_1.gamma_id AND \n" +
         "txs1.tx_current = ? AND \n" +
         "txs1.branch_id = ? AND \n" +
         "txs2.gamma_id = t2_2.gamma_id AND \n" +
         "txs2.tx_current = ? AND \n" +
         "txs2.branch_id = ? AND \n" +
         "kv.key = t2_1.e2 AND \n" +
         "kv.value NOT LIKE ? AND\n" +
         "kv.value NOT LIKE ? AND\n" +
         filterSql+
         "kv2.key = t2_2.e2 AND\n" +
         "cfg.art_id = t2_1.e1 AND\n" +
         "attrConfiguration.art_id = t2_1.e1 AND\n" +
         viewSql+
         "attrConfiguration.attr_type_id = ?\n" +
         "   group by "+keyValueSplit+", t2_2.e1\n" +
         "),\n" +
         "   compoundApplicabilities AS (\n" +
         "      SELECT t2_2.e1 as id, kv2.value as name,\n" +
         configurationArray +
         " as configuration_values,\n" +
         emptyArray+" as attributes\n" +
         "      FROM \n" +
         "      osee_tuple2 t2_1, osee_txs txs1, osee_key_value kv,\n" +
         "      osee_tuple2 t2_2, osee_txs txs2, osee_key_value kv2,\n" +
         "      attrConfigs attrConfiguration,\n" +
         "      artConfigs cfg\n" +
         "      WHERE \n" +
         "      t2_1.e2 = t2_2.e2 AND \n" +
         "      t2_1.tuple_type = ? AND\n" +
         "      t2_2.tuple_type = ? AND\n" +
         "      txs1.gamma_id =t2_1.gamma_id AND \n" +
         "      txs1.tx_current = ? AND \n" +
         "      txs1.branch_id = ? AND \n" +
         "      kv.key = t2_1.e2 AND \n" +
         "      kv.value NOT LIKE ? AND\n" +
         "      kv.value NOT LIKE ? AND\n" +
         filterSql+
         "      t2_2.e1 = ? AND\n" +
         "      txs2.gamma_id = t2_2.gamma_id AND\n" +
         "      txs2.tx_current = ? AND\n" +
         "      txs2.branch_id = ? AND\n" +
         "      kv2.key = t2_2.e2 AND\n" +
         "      attrConfiguration.art_id = t2_1.e1 AND\n" +
         viewSql+
         "      attrConfiguration.attr_type_id = ? AND\n" +
         "      cfg.art_id = t2_1.e1 \n" +
         "      group by kv2.value, t2_2.e1\n" +
         "   ),\n" +
         "   withAttributes AS (\n" +
         "   SELECT nca.*,\n" +
         featureArray+
         "  as attributes \n" +
         "      FROM nonCompoundApplicabilities nca,attrFeatures attrFeature WHERE \n" +
         "      attrFeature.art_id = nca.id \n" +
         filterAttrSql+
         "      GROUP BY nca.id, nca.name, nca.configuration_values\n" +
         "   ),\n" + //TODO maybe look into splitting out attribute search here to be name only?
         "   allApplicabilities AS (\n" +
         "      SELECT * from compoundApplicabilities UNION ALL SELECT * from withAttributes\n" +
         "   ),\n" +
         "   applicabilities AS (\n" +
         "   SELECT applic.*,row_number() over ("+paginationOrdering+") rn from allApplicabilities applic\n" +
         "   )\n" +
         "   SELECT * from applicabilities "
         + paginationSql;
    //@formatter:on
      List<FeatureValue> features = new ArrayList<FeatureValue>();
      Consumer<JdbcStatement> consumer = stmt -> {
         Long id = stmt.getLong("id");
         String name = stmt.getString("name");
         List<ConfigurationValue> configurationValues =
            getConfigurationValuesFromString(stmt.getString("configuration_values"));
         List<FeatureAttribute> attributes = getFeatureAttributesFromString(stmt.getString("attributes"));
         features.add(new FeatureValue(id, name, configurationValues, attributes));
      };
      List<Object> firstChunk = new ArrayList<Object>(25);
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(CoreArtifactTypes.BranchView.getId());
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(CoreArtifactTypes.GroupArtifact.getId());
      firstChunk.add(CoreRelationTypes.PlConfigurationGroup.getId());
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(CoreAttributeTypes.Name.getId());
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(CoreArtifactTypes.Feature.getId());
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(CoreTupleTypes.ViewApplicability.getId());
      firstChunk.add(CoreTupleTypes.ApplicabilityDefinition.getId());
      firstChunk.add(0);
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add("Config =%");
      firstChunk.add("ConfigurationGroup =%");
      List<Object> filterList = new ArrayList<Object>();
      if (!filter.isBlank() & filter.contains("=")) {
         filterList.add(filter.replaceFirst("\\s?=\\s?", " = ")); //make sure all tuple filters fit the form: FEATURE = VALUE instead of FEATURE=VALUE
      }

      List<Object> viewList = new ArrayList<Object>();
      if (viewId.isValid()) {
         viewList.add(viewId);
      }
      List<Object> secondChunk = new ArrayList<Object>();
      secondChunk.add(CoreAttributeTypes.Name.getId());
      secondChunk.add(CoreTupleTypes.ViewApplicability.getId());
      secondChunk.add(CoreTupleTypes.ApplicabilityDefinition.getId());
      secondChunk.add(TxCurrent.CURRENT);
      secondChunk.add(branch);
      secondChunk.add("Config =%");
      secondChunk.add("ConfigurationGroup =%");
      List<Object> thirdChunk = new ArrayList<Object>();
      thirdChunk.add(Id.SENTINEL.intValue());
      thirdChunk.add(TxCurrent.CURRENT);
      thirdChunk.add(branch);

      List<Object> fourthChunk = new ArrayList<Object>();
      fourthChunk.add(CoreAttributeTypes.Name.getId());
      List<Object> filterAttrList = new ArrayList<Object>();
      if (!filter.isBlank() & !filter.contains("=")) {
         filterAttrList.add("%" + filter + "%");
      }
      List<Object> pagination = new ArrayList<Object>();
      if (pageNum != 0L && pageSize != 0L) {
         Long tempLowerBound = (pageNum - 1) * pageSize;
         Long lowerBound = tempLowerBound == 0 ? tempLowerBound : tempLowerBound + 1L;
         Long upperBound = tempLowerBound == 0 ? lowerBound + pageSize : lowerBound + pageSize - 1L;
         pagination.add(lowerBound);
         pagination.add(upperBound);
         // add row num upper and lower to the object[]
      }
      Object[] paramsArray = Stream.concat(firstChunk.stream(),
         Stream.concat(filterList.stream(),
            Stream.concat(viewList.stream(),
               Stream.concat(secondChunk.stream(),
                  Stream.concat(filterList.stream(),
                     Stream.concat(thirdChunk.stream(),
                        Stream.concat(viewList.stream(),
                           Stream.concat(fourthChunk.stream(),
                              Stream.concat(filterAttrList.stream(), pagination.stream()))))))))).collect(
                                 Collectors.toList()).toArray();
      orcsApi.getJdbcService().getClient().runQuery(consumer, sql, paramsArray);
      return new ProductLineConfig(features);
   }

   private List<FeatureAttribute> getFeatureAttributesFromString(String json) {
      JsonNode tree = this.orcsApi.jaxRsApi().readTree(json);
      Iterator<JsonNode> it = tree.elements();
      List<FeatureAttribute> attributes = new ArrayList<FeatureAttribute>();
      while (it.hasNext()) {
         attributes.add(parseFeatureAttribute(it.next()));
      }
      return attributes;
   }

   private FeatureAttribute parseFeatureAttribute(JsonNode node) {
      if (Objects.isNull(node)) {
         throw new OseeArgumentException("Invalid String in parseFeatureAttribute");
      }
      Long id = node.get("id").asLong();
      String value = node.get("value").asText();
      GammaId gamma = GammaId.valueOf(node.get("gamma_id").asLong());
      AttributeTypeId typeId = this.orcsApi.tokenService().getAttributeType(node.get("type_id").asLong());
      return new FeatureAttribute(id, value, typeId, gamma);
   }

   private List<ConfigurationValue> getConfigurationValuesFromString(String json) {
      JsonNode tree = this.orcsApi.jaxRsApi().readTree(json);
      Iterator<JsonNode> it = tree.elements();
      List<ConfigurationValue> configurationValues = new ArrayList<ConfigurationValue>();
      while (it.hasNext()) {
         configurationValues.add(parseConfigurationValue(it.next()));
      }
      ;
      return configurationValues;

   }

   private ConfigurationValue parseConfigurationValue(JsonNode node) {
      if (Objects.isNull(node)) {
         throw new OseeArgumentException("Invalid String in parseConfigurationValue");
      }
      Long id = node.get("id").asLong();
      String name = node.get("name").asText();
      ArtifactTypeId artifactType = this.orcsApi.tokenService().getArtifactType(node.get("type_id").asLong());
      GammaId gamma = GammaId.valueOf(node.get("gamma_id").asLong());
      NamedIdWithGamma applicability = parseApplicability(node.get("applicability"));
      List<ArtifactId> related = parseStringArrToArtifactIds(node.get("related"));
      return new ConfigurationValue(id, name, gamma, applicability, artifactType, related);

   }

   private List<ArtifactId> parseStringArrToArtifactIds(JsonNode node) {
      List<ArtifactId> list = new ArrayList<ArtifactId>();
      Iterator<JsonNode> it = node.elements();
      while (it.hasNext()) {
         list.add(ArtifactId.valueOf(it.next().asText("")));
      }
      return list;
   }

   private NamedIdWithGamma parseApplicability(JsonNode node) {
      if (Objects.isNull(node)) {
         throw new OseeArgumentException("Invalid String in parseApplicability");
      }
      Long id = node.get("id").asLong();
      String name = node.get("name").asText();
      GammaId gamma = GammaId.valueOf(node.get("gamma_id").asLong());
      return new NamedIdWithGamma(id, name, gamma);
   }

   @Override
   public List<FeatureSelectionWithConstraints> getFeatures(ArtifactId featureId, ArtifactId configId, String filter,
      long pageNum, long pageSize) {
      JdbcDbType dbUtils = orcsApi.getJdbcService().getClient().getDbType();
      String paginationOrdering =
         dbUtils.isPaginationOrderingSupported() ? "ORDER BY \n" + "t2.value, \n" + "t2.constrained_by, \n" + "t2.constrained" : "";
      String paginationSql = pageNum != 0L && pageSize != 0L ? "WHERE rn between ? and ?" : "";
      String filterSql = !filter.isBlank() & filter.contains("=") ? "AND kv2.value LIKE ?\n" : "";
      //@formatter:off
      String sql = "WITH \n" +
         "   branch_constraints AS (\n" +
         "   SELECT t2_constraint.e1,t2_constraint.e2, kv.value FROM osee_tuple2 t2_constraint, \n" +
         "   osee_txs txs, osee_key_value kv WHERE\n" +
         "   txs.tx_current =? and \n" +
         "   txs.branch_id = ? AND\n" +
         "   txs.gamma_id = t2_constraint.gamma_id AND\n" +
         "   t2_constraint.tuple_type = ? AND \n" +
         "   kv.key = t2_constraint.e2\n" +
         "   ),\n" +
         "configuration_values AS (\r\n" +
         "      select e1, e2 from \r\n" +
         "    osee_tuple2 t2_config, \r\n" +
         "    osee_txs txs where \r\n" +
         "    txs.branch_id = ? AND \r\n" +
         "    txs.tx_current = ? AND \r\n" +
         "    txs.gamma_id = t2_config.gamma_id AND\r\n" +
         "    t2_config.e1 = ? AND  -- this is our configuration we are looking for\r\n" +
         "    t2_config.tuple_type =?\r\n" +
         "   ),\r\n" +
         "   constraint_invalidation as (\r\n" +
         "      select \r\n" +
         "   bc.e1,\r\n" +
         "   bc.e2, \r\n" +
         "   configuration_values.e1 as constraint_present \r\n" +
         "   from \r\n" +
         "   branch_constraints bc FULL OUTER JOIN configuration_values on configuration_values.e2 = bc.e2\r\n" +
         "      where configuration_values.e1 is null \r\n" +
         "      ),\r\n" +
         "   feature AS (\r\n" +
         "         SELECT t2_feature.e1,t2_feature.e2,t2_feature.gamma_id,\r\n" +
         "   kv2.value\r\n" +
         "   from \r\n" +
         "   osee_tuple2 t2_feature, \r\n" +
         "   osee_txs txs, \r\n" +
         "   osee_key_value kv2\r\n" +
         "         WHERE \r\n" +
         "   kv2.key = t2_feature.e2 AND\r\n" +
         "   txs.branch_id = ? AND \r\n" +
         "   txs.tx_current = ? AND \r\n" +
         "   txs.gamma_id = t2_feature.gamma_id AND \r\n" +
         "   t2_feature.tuple_type = ? AND \r\n" +
         "   t2_feature.e1 = ? -- this is the feature we are looking for the potential values for\r\n" +
         filterSql +
         "      ),\r\n" +
         "   unconstrained_features AS (\r\n" +
         "      SELECT ft.*, "+dbUtils.booleanFalse()+" as constrained, '' as constrained_by FROM\r\n" +
         "      feature ft FULL OUTER JOIN constraint_invalidation ON constraint_invalidation.e1 = ft.e2 WHERE constraint_invalidation.e1 is null),\r\n" +
         "constrained_features AS (\r\n" +
         "SELECT ft.*,\r\n" +
         "   CASE WHEN ft.e2 = ci.e1 and ci.constraint_present is null THEN "+dbUtils.booleanTrue()+" ELSE "+dbUtils.booleanFalse()+" END as constrained,\r\n" +
         "   CASE WHEN ft.e2 = ci.e1 and ci.constraint_present is null THEN kv.value ELSE '' END as constrained_by \r\n" +
         "   from \r\n" +
         "   feature ft ,\r\n" +
         "   constraint_invalidation ci,\r\n" +
         "   osee_key_value kv WHERE"+
         "   kv.key = ci.e2"+
         "),\r\n" +
         "total_features as (\r\n" +
         "SELECT * from constrained_features UNION SELECT * FROM unconstrained_features\r\n" +
         "),\r\n" +
         "feature_with_pages AS (\r\n" +
         "SELECT t2.* , row_number() over ("+paginationOrdering+") rn FROM\r\n" +
         "total_features t2)\r\n" +
         "\r\n" +
         "   SELECT ft.e1,\r\n" +
         "   ft.e2,\r\n" +
         "   ft.gamma_id,\r\n" +
         "   ft.value,\r\n" +
         "   ft.constrained, \r\n" +
         "   ft.constrained_by \r\n" +
         "   from feature_with_pages ft "+
        paginationSql;
      /**
       * params:
       * TxCurrent.Current,
       * branch,
       * TupleType 17,
       * branch,
       * TxCurrent.Current,
       * configId,
       * TupleType 2,
       * branch,
       * TxCurrent.Current,
       * TupleType 11,
       * featureId
       * applic filter (optional),
       * pagination (optional)
       */
      //@formatter:on
      List<Object> params = new ArrayList<Object>();
      params.add(TxCurrent.CURRENT);
      params.add(branch);
      params.add(CoreTupleTypes.ApplicabilityConstraint.getId());
      params.add(branch);
      params.add(TxCurrent.CURRENT);
      params.add(configId);
      params.add(CoreTupleTypes.ViewApplicability.getId());
      params.add(branch);
      params.add(TxCurrent.CURRENT);
      params.add(CoreTupleTypes.ApplicabilityDefinition.getId());
      params.add(featureId);
      if (!filter.isBlank() & filter.contains("=")) {
         params.add(filter.replaceFirst("\\s?=\\s?", " = ")); //make sure all tuple filters fit the form: FEATURE = VALUE instead of FEATURE=VALUE
      }
      if (pageNum != 0L && pageSize != 0L) {
         Long tempLowerBound = (pageNum - 1) * pageSize;
         Long lowerBound = tempLowerBound == 0 ? tempLowerBound : tempLowerBound + 1L;
         Long upperBound = tempLowerBound == 0 ? lowerBound + pageSize : lowerBound + pageSize - 1L;
         params.add(lowerBound);
         params.add(upperBound);
         // add row num upper and lower to the object[]
      }

      List<FeatureSelectionWithConstraints> features = new ArrayList<FeatureSelectionWithConstraints>();
      Consumer<JdbcStatement> consumer = stmt -> {
         Long e1 = stmt.getLong("e1");
         Long e2 = stmt.getLong("e2");
         GammaId gammaId = GammaId.valueOf(stmt.getLong("gamma_id"));
         String value = stmt.getString("value");
         boolean constrained = stmt.getBoolean("constrained");
         String constrainedBy = stmt.getString("constrained_by");
         features.add(new FeatureSelectionWithConstraints(e1, e2, gammaId, value, constrained, constrainedBy));
      };
      orcsApi.getJdbcService().getClient().runQuery(consumer, sql, params.toArray());
      return features;
   }

   @Override
   public long getTableCount(String filter, ArtifactId viewId) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      String pleAccess = orcsApi.getSystemProperties().getValue("ple.access");
      if (!pleAccess.isEmpty() && pleAccess.equals("SINGLE") && viewId.isInvalid()) {
         return 0L;
      }
      JdbcDbType dbUtils = orcsApi.getJdbcService().getClient().getDbType();
      String filterSql = !filter.isBlank() & filter.contains("=") ? "kv.value LIKE ? AND\n" : "";
      String filterAttrSql = !filter.isBlank() & !filter.contains("=") ? "AND attrFeature.value LIKE ? \n" : "";
      String viewSql = viewId.isValid() ? "attrConfiguration.art_id = ? AND \n" : "";
      String configurationArray = dbUtils.json_agg(dbUtils.jsonb_object("id", "attrConfiguration.art_id", "name",
         "attrConfiguration.value", "gamma_id", "cfg.gamma_id", "type_id", "cfg.art_type_id", "applicability",
         dbUtils.json_object("id", "kv.key", "name", "kv.value", "gamma_id", "t2_1.gamma_id")));
      String featureArray = dbUtils.json_agg(dbUtils.jsonb_object("id", "attrFeature.attr_id", "type_id",
         dbUtils.cast("attrFeature.attr_type_id", dbUtils.longToString()), "gamma_id", "attrFeature.gamma_id", "value",
         "attrFeature.value"));
      String keyValueSplit = dbUtils.split_part("kv2.value", "=", 1);
      String emptyArray = dbUtils.jsonb_array();
      //@formatter:off
      String sql = "WITH\n" +
         "artConfiguration AS (\r\n" +
         "SELECT art.*,"+emptyArray+"as related FROM osee_artifact art, osee_txs txs\r\n" +
         "   WHERE\r\n" +
         "   txs.tx_current =? AND \r\n" +
         "   txs.gamma_id = art.gamma_id AND \r\n" +
         "   txs.branch_id = ? AND\r\n" +
         "   art.art_type_id = ?\r\n" +
         "),\r\n" +
         "artGroup AS (\r\n" +
         "   SELECT art.*,"+emptyArray+" as related FROM osee_artifact art, osee_txs txs\r\n" +
         "   WHERE\r\n" +
         "   txs.tx_current =? AND \r\n" +
         "   txs.gamma_id = art.gamma_id AND \r\n" +
         "   txs.branch_id = ? AND\r\n" +
         "   art.art_type_id = ?\r\n" +
         "),\r\n" +
         "artConfigs AS (\r\n" +
         "SELECT * from artConfiguration UNION ALL SELECT * from artGroup),\r\n" +
         "attrConfigs AS (\r\n" +
         "SELECT attr.* FROM artConfigs cfg, osee_attribute attr, osee_txs txs WHERE \r\n" +
         "   attr.art_id = cfg.art_id and \r\n" +
         "   txs.gamma_id = attr.gamma_id and \r\n" +
         "   txs.tx_current = ? and \r\n" +
         "   txs.branch_id = ? AND"+
         "   attr.attr_type_id = ?"+
         "   ),\r\n" +
         "artFeatures AS (\r\n" +
         "   SELECT * from osee_artifact art, osee_txs txs\r\n" +
         "   WHERE\r\n" +
         "   txs.tx_current = ? AND \r\n" +
         "   txs.gamma_id = art.gamma_id AND \r\n" +
         "   txs.branch_id = ? AND\r\n" +
         "   art.art_type_id = ?\r\n" +
         "   ),\r\n" +
         "attrFeatures AS (\r\n" +
         "   SELECT attr.* FROM artFeatures feat, osee_attribute attr, osee_txs txs WHERE \r\n" +
         "   attr.art_id = feat.art_id and \r\n" +
         "   txs.gamma_id = attr.gamma_id and \r\n" +
         "   txs.tx_current = ? and \r\n" +
         "   txs.branch_id = ?\r\n" +
         "),\n"+
         "nonCompoundApplicabilities as (\n" +
         "SELECT t2_2.e1 as id,"+keyValueSplit+" as name,\n" +
         configurationArray +" as configuration_values\n" +
         "from \n" +
         "osee_tuple2 t2_1 , osee_tuple2 t2_2 , osee_txs txs1, osee_txs txs2, osee_key_value kv, osee_key_value kv2,\n" +
         "   artConfigs cfg,\n" +
         "   attrConfigs attrConfiguration\n" +
         "WHERE \n" +
         "t2_1.e2 = t2_2.e2 AND \n" +
         "t2_1.tuple_type = ? AND \n" +
         "t2_2.tuple_type = ? AND \n" +
         "t2_2.e1 > ? AND\n" +
         "txs1.gamma_id = t2_1.gamma_id AND \n" +
         "txs1.tx_current = ? AND \n" +
         "txs1.branch_id = ? AND \n" +
         "txs2.gamma_id = t2_2.gamma_id AND \n" +
         "txs2.tx_current = ? AND \n" +
         "txs2.branch_id = ? AND \n" +
         "kv.key = t2_1.e2 AND \n" +
         "kv.value NOT LIKE ? AND\n" +
         "kv.value NOT LIKE ? AND\n" +
         filterSql+
         "kv2.key = t2_2.e2 AND\n" +
         "cfg.art_id = t2_1.e1 AND\n" +
         "attrConfiguration.art_id = t2_1.e1 AND\n" +
         viewSql+
         "attrConfiguration.attr_type_id = ?\n" +
         "   group by "+keyValueSplit+", t2_2.e1\n" +
         "),\n" +
         "   compoundApplicabilities AS (\n" +
         "      SELECT t2_2.e1 as id, kv2.value as name,\n" +
         configurationArray +
         " as configuration_values,\n" +
         emptyArray+" as attributes\n" +
         "      FROM \n" +
         "      osee_tuple2 t2_1, osee_txs txs1, osee_key_value kv,\n" +
         "      osee_tuple2 t2_2, osee_txs txs2, osee_key_value kv2,\n" +
         "      attrConfigs attrConfiguration,\r\n" +
         "      artConfigs cfg\n"+
         "      WHERE \n" +
         "      t2_1.e2 = t2_2.e2 AND \n" +
         "      t2_1.tuple_type = ? AND\n" +
         "      t2_2.tuple_type = ? AND\n" +
         "      txs1.gamma_id =t2_1.gamma_id AND \n" +
         "      txs1.tx_current = ? AND \n" +
         "      txs1.branch_id = ? AND \n" +
         "      kv.key = t2_1.e2 AND \n" +
         "      kv.value NOT LIKE ? AND\n" +
         "      kv.value NOT LIKE ? AND\n" +
         filterSql+
         "      t2_2.e1 = ? AND\n" +
         "      txs2.gamma_id = t2_2.gamma_id AND\n" +
         "      txs2.tx_current = ? AND\n" +
         "      txs2.branch_id = ? AND\n" +
         "      kv2.key = t2_2.e2 AND\n" +
         "      attrConfiguration.art_id = t2_1.e1 AND\n" +
         viewSql+
         "      attrConfiguration.attr_type_id = ? AND\n" +
         "      cfg.art_id = t2_1.e1 \n" +
         "      group by kv2.value, t2_2.e1\n" +
         "   ),\n" +
         "   withAttributes AS (\n" +
         "   SELECT nca.*,\n" +
         featureArray+
         "  as attributes \n" +
         "      FROM nonCompoundApplicabilities nca,attrFeatures attrFeature WHERE \n" +
         "      attrFeature.art_id = nca.id \n" +
         filterAttrSql+
         "      GROUP BY nca.id, nca.name, nca.configuration_values\n" +
         "   ),\n" + //TODO maybe look into splitting out attribute search here to be name only?
         "   allApplicabilities AS (\n" +
         "      SELECT * from compoundApplicabilities UNION ALL SELECT * from withAttributes\n" +
         "   ),\n" +
         "   applicabilities AS (\n" +
         "   SELECT applic.* from allApplicabilities applic\n" +
         "   )\n" +
         "   SELECT COUNT(*) from applicabilities ";
    //@formatter:on
      List<Object> firstChunk = new ArrayList<Object>(25);
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(CoreArtifactTypes.BranchView.getId());
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(CoreArtifactTypes.GroupArtifact.getId());
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(CoreAttributeTypes.Name.getId());
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(CoreArtifactTypes.Feature.getId());
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(CoreTupleTypes.ViewApplicability.getId());
      firstChunk.add(CoreTupleTypes.ApplicabilityDefinition.getId());
      firstChunk.add(0);
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add(TxCurrent.CURRENT);
      firstChunk.add(branch);
      firstChunk.add("Config =%");
      firstChunk.add("ConfigurationGroup =%");
      List<Object> filterList = new ArrayList<Object>();
      if (!filter.isBlank() & filter.contains("=")) {
         filterList.add(filter.replaceFirst("\\s?=\\s?", " = ")); //make sure all tuple filters fit the form: FEATURE = VALUE instead of FEATURE=VALUE
      }

      List<Object> viewList = new ArrayList<Object>();
      if (viewId.isValid()) {
         viewList.add(viewId);
      }
      List<Object> secondChunk = new ArrayList<Object>();
      secondChunk.add(CoreAttributeTypes.Name.getId());
      secondChunk.add(CoreTupleTypes.ViewApplicability.getId());
      secondChunk.add(CoreTupleTypes.ApplicabilityDefinition.getId());
      secondChunk.add(TxCurrent.CURRENT);
      secondChunk.add(branch);
      secondChunk.add("Config =%");
      secondChunk.add("ConfigurationGroup =%");
      List<Object> thirdChunk = new ArrayList<Object>();
      thirdChunk.add(Id.SENTINEL.intValue());
      thirdChunk.add(TxCurrent.CURRENT);
      thirdChunk.add(branch);

      List<Object> fourthChunk = new ArrayList<Object>();
      fourthChunk.add(CoreAttributeTypes.Name.getId());
      List<Object> filterAttrList = new ArrayList<Object>();
      if (!filter.isBlank() & !filter.contains("=")) {
         filterAttrList.add("%" + filter + "%");
      }
      Object[] paramsArray = Stream.concat(firstChunk.stream(),
         Stream.concat(filterList.stream(),
            Stream.concat(viewList.stream(),
               Stream.concat(secondChunk.stream(),
                  Stream.concat(filterList.stream(),
                     Stream.concat(thirdChunk.stream(),
                        Stream.concat(viewList.stream(),
                           Stream.concat(fourthChunk.stream(), filterAttrList.stream())))))))).collect(
                              Collectors.toList()).toArray();
      return orcsApi.getJdbcService().getClient().fetch(-1, sql, paramsArray);
   }

   @Override
   public long getFeaturesCount(ArtifactId featureId, ArtifactId configId, String filter) {
      String filterSql = !filter.isBlank() & filter.contains("=") ? "AND kv2.value LIKE ?\n" : "";
      JdbcDbType dbUtils = orcsApi.getJdbcService().getClient().getDbType();
      //@formatter:off
      String sql = "WITH \n" +
         "   branch_constraints AS (\n" +
         "   SELECT t2_constraint.e1,t2_constraint.e2, kv.value FROM osee_tuple2 t2_constraint, \n" +
         "   osee_txs txs, osee_key_value kv WHERE\n" +
         "   txs.tx_current =? and \n" +
         "   txs.branch_id = ? AND\n" +
         "   txs.gamma_id = t2_constraint.gamma_id AND\n" +
         "   t2_constraint.tuple_type = ? AND \n" +
         "   kv.key = t2_constraint.e2\n" +
         "   ),\n" +
         "configuration_values AS (\r\n" +
         "      select e1, e2 from \r\n" +
         "    osee_tuple2 t2_config, \r\n" +
         "    osee_txs txs where \r\n" +
         "    txs.branch_id = ? AND \r\n" +
         "    txs.tx_current = ? AND \r\n" +
         "    txs.gamma_id = t2_config.gamma_id AND\r\n" +
         "    t2_config.e1 = ? AND  -- this is our configuration we are looking for\r\n" +
         "    t2_config.tuple_type =?\r\n" +
         "   ),\r\n" +
         "   constraint_invalidation as (\r\n" +
         "      select \r\n" +
         "   bc.e1,\r\n" +
         "   bc.e2, \r\n" +
         "   configuration_values.e1 as constraint_present \r\n" +
         "   from \r\n" +
         "   branch_constraints bc FULL OUTER JOIN configuration_values on configuration_values.e2 = bc.e2\r\n" +
         "      where configuration_values.e1 is null \r\n" +
         "      ),\r\n" +
         "   feature AS (\r\n" +
         "         SELECT t2_feature.e1,t2_feature.e2,t2_feature.gamma_id,\r\n" +
         "   kv2.value\r\n" +
         "   from \r\n" +
         "   osee_tuple2 t2_feature, \r\n" +
         "   osee_txs txs, \r\n" +
         "   osee_key_value kv2\r\n" +
         "         WHERE \r\n" +
         "   kv2.key = t2_feature.e2 AND\r\n" +
         "   txs.branch_id = ? AND \r\n" +
         "   txs.tx_current = ? AND \r\n" +
         "   txs.gamma_id = t2_feature.gamma_id AND \r\n" +
         "   t2_feature.tuple_type = ? AND \r\n" +
         "   t2_feature.e1 = ? -- this is the feature we are looking for the potential values for\r\n" +
         filterSql +
         "      ),\r\n" +
         "   unconstrained_features AS (\r\n" +
         "      SELECT ft.*, "+dbUtils.booleanFalse()+" as constrained, '' as constrained_by FROM\r\n" +
         "      feature ft FULL OUTER JOIN constraint_invalidation ON constraint_invalidation.e1 = ft.e2 WHERE constraint_invalidation.e1 is null),\r\n" +
         "constrained_features AS (\r\n" +
         "SELECT ft.*,\r\n" +
         "   CASE WHEN ft.e2 = ci.e1 and ci.constraint_present is null THEN "+dbUtils.booleanTrue()+" ELSE "+dbUtils.booleanFalse()+" END as constrained,\r\n" +
         "   CASE WHEN ft.e2 = ci.e1 and ci.constraint_present is null THEN kv.value ELSE '' END as constrained_by \r\n" +
         "   from \r\n" +
         "   feature ft ,\r\n" +
         "   constraint_invalidation ci,\r\n" +
         "   osee_key_value kv WHERE"+
         "   kv.key = ci.e2"+
         "),\r\n" +
         "total_features as (\r\n" +
         "SELECT * from constrained_features UNION SELECT * FROM unconstrained_features\r\n" +
         "),\r\n" +
         "feature_with_pages AS (\n" +
         "SELECT t2.* FROM\n" +
         "total_features t2)\n" +
         "\n" +
         "   SELECT COUNT(*) \n" +
         "   from feature_with_pages \n";
      /**
       * params:
       * TxCurrent.Current,
       * branch,
       * TupleType 17,
       * branch,
       * TxCurrent.Current,
       * configId,
       * TupleType 2,
       * branch,
       * TxCurrent.Current,
       * TupleType 11,
       * featureId
       * applic filter (optional),
       * pagination (optional)
       */
      //@formatter:on
      List<Object> params = new ArrayList<Object>();
      params.add(TxCurrent.CURRENT);
      params.add(branch);
      params.add(CoreTupleTypes.ApplicabilityConstraint.getId());
      params.add(branch);
      params.add(TxCurrent.CURRENT);
      params.add(configId);
      params.add(CoreTupleTypes.ViewApplicability.getId());
      params.add(branch);
      params.add(TxCurrent.CURRENT);
      params.add(CoreTupleTypes.ApplicabilityDefinition.getId());
      params.add(featureId);
      if (!filter.isBlank() & filter.contains("=")) {
         params.add(filter.replaceFirst("\\s?=\\s?", " = ")); //make sure all tuple filters fit the form: FEATURE = VALUE instead of FEATURE=VALUE
      }

      return orcsApi.getJdbcService().getClient().fetch(-1, sql, params.toArray());
   }

   @Override
   public XResultData setApplicability(ArtifactId viewId, ArtifactId featureId, String[] applicability) {
      return orcsApi.getApplicabilityOps().setFeatureForView(viewId, featureId, applicability, branch);
   }

   @Override
   public List<ConfigurationGroupDefinition> getConfigurations(ArtifactId viewId) {
      return orcsApi.getApplicabilityOps().getConfigurationGroupsForView(viewId, branch);
   }

   @Override
   public Collection<CreateViewDefinition> getViews(Collection<ArtifactId> ids,
      AttributeTypeToken orderByAttributeType) {
      if (ids.size() > 0) {
         return orcsApi.getApplicabilityOps().getViewDefinitionsByIds(branch, ids);
      }
      return orcsApi.getApplicabilityOps().getViewDefinitions(branch);
   }

   @Override
   public Collection<FeatureDefinition> getFeatureDefinitions(AttributeTypeToken orderByAttributeType) {
      return orcsApi.getApplicabilityOps().getFeatures(branch, orderByAttributeType);
   }
}
