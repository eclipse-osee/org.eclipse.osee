/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.orcs.rest.internal;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeGeneric;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.rest.internal.writers.GenericReportBuilder;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * Test Case for {@link GenericReportBuilder}
 *
 * @author David W. Miller
 */
public class GenericReportBuilderTest {

   // @formatter:off
   @Mock private OrcsApi orcsApi;
   @Mock private QueryFactory queryFactory;
   @Mock private QueryBuilder queryBuilder;
   @Mock private OrcsTokenService tokenService;
   @Mock private ArtifactReadable artifact1;
   @Mock private ArtifactReadable artifact2;
   @Mock private ArtifactReadable childArtifact1;
   @Mock private ArtifactTypeToken artifactType;
   @Mock private AttributeTypeGeneric<?> nameAttrType;
   @Mock private AttributeTypeGeneric<?> descAttrType;
   @Mock private RelationTypeToken relationType;
   @Mock private RelationTypeToken relationType2;
   // @formatter:on

   private GenericReportBuilder report;
   private final BranchId branch = BranchId.valueOf(570L);
   private final ArtifactId view = ArtifactId.valueOf(-1L);

   @Before
   public void setup() {
      MockitoAnnotations.initMocks(this);
      when(orcsApi.getQueryFactory()).thenReturn(queryFactory);
      when(orcsApi.tokenService()).thenReturn(tokenService);
      when(queryFactory.fromBranch(branch, view)).thenReturn(queryBuilder);
      report = new GenericReportBuilder(branch, view, orcsApi);
   }

   @Test
   public void testConstructor() {
      assertNotNull(report);
      assertEquals(0, report.getColumnCount());
      assertTrue(report.getLevels().isEmpty());
   }

   @Test
   public void testLevelWithTypeName() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);

      report.level("Folders", "Folder");

      assertEquals(1, report.getLevels().size());
      assertEquals("Folders", report.getLevels().get(0).getLevelName());
      assertEquals(0, report.getLevels().get(0).getDepth());
   }

   @Test
   public void testLevelWithQueryBuilder() {
      report.level("Custom Level", queryBuilder);

      assertEquals(1, report.getLevels().size());
      assertEquals("Custom Level", report.getLevels().get(0).getLevelName());
      assertEquals(0, report.getLevels().get(0).getDepth());
   }

   @Test
   public void testMultipleLevelsIncrementDepth() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(tokenService.getArtifactType("File")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);

      report.level("Level 0", "Folder");
      report.level("Level 1", "File");

      assertEquals(2, report.getLevels().size());
      assertEquals(0, report.getLevels().get(0).getDepth());
      assertEquals(1, report.getLevels().get(1).getDepth());
   }

   @Test
   public void testRelationLevel() {
      when(tokenService.getRelationType("Default Hierarchy")).thenReturn(relationType);
      when(relationType.getId()).thenReturn(123L);
      when(relationType.getName()).thenReturn("Default Hierarchy");
      when(queryBuilder.follow(any(RelationTypeSide.class))).thenReturn(queryBuilder);

      report.relationLevel("Children", "Default Hierarchy", "SIDE_B");

      assertEquals(1, report.getLevels().size());
      assertEquals("Children", report.getLevels().get(0).getLevelName());
   }

   @Test
   public void testColumnByName() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);

      report.level("Folders", "Folder").column("Artifact Id");

      assertEquals(1, report.getColumnCount());
      assertEquals("Artifact Id", report.getLevels().get(0).getColumns().get(0).getName());
   }

   @Test
   public void testColumnByNameAndTypeName() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      doReturn(nameAttrType).when(tokenService).getAttributeType("Name");
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);

      report.level("Folders", "Folder").column("Folder Name", "Name");

      assertEquals(1, report.getColumnCount());
      assertEquals("Folder Name", report.getLevels().get(0).getColumns().get(0).getName());
   }

   @Test
   public void testColumnByAttributeTypeToken() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);
      when(nameAttrType.getName()).thenReturn("Name");

      report.level("Folders", "Folder").column(nameAttrType);

      assertEquals(1, report.getColumnCount());
      assertEquals("Name", report.getLevels().get(0).getColumns().get(0).getName());
   }

   @Test
   public void testTypeColumn() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);

      report.level("Folders", "Folder").type("Art Type");

      assertEquals(1, report.getColumnCount());
      assertEquals("Art Type", report.getLevels().get(0).getColumns().get(0).getName());
   }

   @Test
   public void testColumnCountAcrossMultipleLevels() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(tokenService.getArtifactType("File")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);

      report.level("Level 0", "Folder").column("Id").column("Name", nameAttrType);
      report.level("Level 1", "File").column("Id").column("Name", nameAttrType).column("Desc", descAttrType);

      assertEquals(5, report.getColumnCount());
   }

   @Test
   public void testGetTopRow() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(tokenService.getArtifactType("File")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);

      report.level("Folders", "Folder").column("Id").column("Name", nameAttrType);
      report.level("Files", "File").column("File Id");

      String[] topRow = report.getTopRow();
      assertEquals(3, topRow.length);
      assertEquals("Folders", topRow[0]);
      assertEquals(null, topRow[1]); // second column of first level is null
      assertEquals("Files", topRow[2]);
   }

   @Test
   public void testGetHeaderRow() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);

      report.level("Folders", "Folder").column("Artifact Id").column("Folder Name", nameAttrType);

      String[] headerRow = report.getHeaderRow();
      assertEquals(2, headerRow.length);
      assertEquals("Artifact Id", headerRow[0]);
      assertEquals("Folder Name", headerRow[1]);
   }

   @Test
   public void testGetDataRowsFromQuerySingleLevel() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);
      when(queryBuilder.asArtifacts()).thenReturn(Arrays.asList(artifact1, artifact2));

      when(artifact1.getIdString()).thenReturn("100");
      when(artifact1.getAttributeValuesAsString(nameAttrType)).thenReturn("Folder A");
      when(artifact2.getIdString()).thenReturn("200");
      when(artifact2.getAttributeValuesAsString(nameAttrType)).thenReturn("Folder B");

      report.level("Folders", "Folder").column("Id").column("Name", nameAttrType);

      List<Object[]> rows = new ArrayList<>();
      report.getDataRowsFromQuery(rows);

      // rows[0] = top row, rows[1] = header row, rows[2..] = data
      assertEquals(4, rows.size());
      assertArrayEquals(new String[] {"Folders", null}, rows.get(0));
      assertArrayEquals(new String[] {"Id", "Name"}, rows.get(1));
      assertArrayEquals(new String[] {"100", "Folder A"}, rows.get(2));
      assertArrayEquals(new String[] {"200", "Folder B"}, rows.get(3));
   }

   @Test(expected = OseeCoreException.class)
   public void testGetDataRowsFromQueryThrowsOnEmptyResults() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);
      when(queryBuilder.asArtifacts()).thenReturn(Collections.emptyList());

      report.level("Folders", "Folder").column("Id");

      List<Object[]> rows = new ArrayList<>();
      report.getDataRowsFromQuery(rows);
   }

   @Test
   public void testGetDataRowsWithMultipleLevelsAndRelation() {
      // Setup first level
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);
      when(queryBuilder.asArtifacts()).thenReturn(Arrays.asList(artifact1));

      when(artifact1.getIdString()).thenReturn("100");
      when(artifact1.getAttributeValuesAsString(nameAttrType)).thenReturn("Parent");

      // Setup second level - relation traversal
      when(tokenService.getRelationType("Default Hierarchy")).thenReturn(relationType);
      when(relationType.getId()).thenReturn(123L);
      when(relationType.getName()).thenReturn("Default Hierarchy");
      when(queryBuilder.follow(any(RelationTypeSide.class))).thenReturn(queryBuilder);
      when(artifact1.getRelated(any(RelationTypeSide.class), any(DeletionFlag.class))).thenReturn(
         Arrays.asList(childArtifact1));

      when(childArtifact1.getIdString()).thenReturn("200");
      when(childArtifact1.getAttributeValuesAsString(nameAttrType)).thenReturn("Child");

      // Build report with two levels
      report.level("Parents", "Folder").column("Id").column("Name", nameAttrType);
      report.relationLevel("Children", "Default Hierarchy", "SIDE_B");
      // Need to add columns to the second level after relationLevel
      report.column("Child Id").column("Child Name", nameAttrType);

      List<Object[]> rows = new ArrayList<>();
      report.getDataRowsFromQuery(rows);

      // rows[0] = top row, rows[1] = header row, rows[2] = data row
      assertEquals(3, rows.size());
      String[] dataRow = (String[]) rows.get(2);
      assertEquals(4, dataRow.length);
      assertEquals("100", dataRow[0]);
      assertEquals("Parent", dataRow[1]);
      assertEquals("200", dataRow[2]);
      assertEquals("Child", dataRow[3]);
   }

   @Test
   public void testFilterExcludesMatchingRows() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);
      when(queryBuilder.asArtifacts()).thenReturn(Arrays.asList(artifact1, artifact2));

      when(artifact1.getIdString()).thenReturn("100");
      when(artifact1.getAttributeValuesAsString(nameAttrType)).thenReturn("EXCLUDE_ME");
      when(artifact2.getIdString()).thenReturn("200");
      when(artifact2.getAttributeValuesAsString(nameAttrType)).thenReturn("Keep This");

      report.level("Folders", "Folder").column("Id").column("Name", nameAttrType).filter(nameAttrType, "EXCLUDE.*");

      List<Object[]> rows = new ArrayList<>();
      report.getDataRowsFromQuery(rows);

      // top row + header row + 1 data row (artifact1 filtered out)
      assertEquals(3, rows.size());
      assertArrayEquals(new String[] {"200", "Keep This"}, rows.get(2));
   }

   @Test
   public void testFluentApiReturnsThis() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);
      when(nameAttrType.getName()).thenReturn("Name");

      // Verify fluent chaining works
      GenericReportBuilder result =
         (GenericReportBuilder) report.level("Folders", "Folder").column("Id").column("Name", nameAttrType).column(
            nameAttrType).type("Type");

      assertNotNull(result);
      assertEquals(4, report.getColumnCount());
   }

   @Test
   public void testQueryAccessor() {
      assertEquals(queryBuilder, report.query());
   }

   @Test
   public void testOrcsApiAccessor() {
      assertEquals(orcsApi, report.getOrcsApi());
   }

   @Test
   public void testFollowForkAfterRelationLevel() {
      when(tokenService.getRelationType("Default Hierarchy")).thenReturn(relationType);
      when(tokenService.getRelationType("Dependency")).thenReturn(relationType2);
      when(relationType.getId()).thenReturn(123L);
      when(relationType.getName()).thenReturn("Default Hierarchy");
      when(relationType2.getId()).thenReturn(456L);
      when(relationType2.getName()).thenReturn("Dependency");
      when(queryBuilder.follow(any(RelationTypeSide.class))).thenReturn(queryBuilder);
      when(queryBuilder.followFork(any(RelationTypeSide.class))).thenReturn(queryBuilder);

      report.relationLevel("Children", "Default Hierarchy", "SIDE_B").followFork("Dependency", "SIDE_A");

      assertEquals(1, report.getLevels().size());
      assertEquals("Children", report.getLevels().get(0).getLevelName());
   }

   @Test
   public void testFollowForkChainedMultipleTimes() {
      RelationTypeToken relationType3 = org.mockito.Mockito.mock(RelationTypeToken.class);
      when(tokenService.getRelationType("Default Hierarchy")).thenReturn(relationType);
      when(tokenService.getRelationType("Dependency")).thenReturn(relationType2);
      when(tokenService.getRelationType("Allocation")).thenReturn(relationType3);
      when(relationType.getId()).thenReturn(123L);
      when(relationType.getName()).thenReturn("Default Hierarchy");
      when(relationType2.getId()).thenReturn(456L);
      when(relationType2.getName()).thenReturn("Dependency");
      when(relationType3.getId()).thenReturn(789L);
      when(relationType3.getName()).thenReturn("Allocation");
      when(queryBuilder.follow(any(RelationTypeSide.class))).thenReturn(queryBuilder);
      when(queryBuilder.followFork(any(RelationTypeSide.class))).thenReturn(queryBuilder);

      report.relationLevel("Children", "Default Hierarchy", "SIDE_B") //
         .followFork("Dependency", "SIDE_A") //
         .followFork("Allocation", "SIDE_B");

      assertEquals(1, report.getLevels().size());
   }

   @Test
   public void testFollowForkAfterColumnThrowsException() {
      when(tokenService.getRelationType("Default Hierarchy")).thenReturn(relationType);
      when(tokenService.getRelationType("Dependency")).thenReturn(relationType2);
      when(relationType.getId()).thenReturn(123L);
      when(relationType.getName()).thenReturn("Default Hierarchy");
      when(relationType2.getId()).thenReturn(456L);
      when(relationType2.getName()).thenReturn("Dependency");
      when(queryBuilder.follow(any(RelationTypeSide.class))).thenReturn(queryBuilder);
      when(queryBuilder.followFork(any(RelationTypeSide.class))).thenReturn(queryBuilder);

      report.relationLevel("Children", "Default Hierarchy", "SIDE_B").column("Id");

      try {
         report.followFork("Dependency", "SIDE_A");
         fail("Expected OseeArgumentException");
      } catch (OseeArgumentException ex) {
         assertTrue("Error message should indicate followFork must come before columns",
            ex.getMessage().contains("followFork must be called before adding columns"));
      }
   }

   @Test
   public void testFollowForkWithDuplicateRelationThrowsException() {
      when(tokenService.getRelationType("Default Hierarchy")).thenReturn(relationType);
      when(relationType.getId()).thenReturn(123L);
      when(relationType.getName()).thenReturn("Default Hierarchy");
      when(queryBuilder.follow(any(RelationTypeSide.class))).thenReturn(queryBuilder);
      when(queryBuilder.followFork(any(RelationTypeSide.class))).thenReturn(queryBuilder);

      report.relationLevel("Children", "Default Hierarchy", "SIDE_B");

      try {
         report.followFork("Default Hierarchy", "SIDE_A");
         fail("Expected OseeArgumentException for duplicate relation");
      } catch (OseeArgumentException ex) {
         assertTrue("Error message should indicate duplicate relation",
            ex.getMessage().contains("already used in this level"));
      }
   }

   @Test
   public void testFollowForkAfterLevelThrowsException() {
      when(tokenService.getArtifactType("Folder")).thenReturn(artifactType);
      when(tokenService.getRelationType("Dependency")).thenReturn(relationType2);
      when(relationType2.getId()).thenReturn(456L);
      when(relationType2.getName()).thenReturn("Dependency");
      when(queryBuilder.andIsOfType(artifactType)).thenReturn(queryBuilder);

      report.level("Folders", "Folder");

      try {
         report.followFork("Dependency", "SIDE_A");
         fail("Expected OseeArgumentException");
      } catch (OseeArgumentException ex) {
         assertTrue("Error message should indicate followFork requires a relationLevel",
            ex.getMessage().contains("followFork can only be used on a level created by relationLevel"));
      }
   }

   @Test
   public void testFollowForkBeforeAnyLevelThrowsException() {
      when(tokenService.getRelationType("Dependency")).thenReturn(relationType2);
      when(relationType2.getId()).thenReturn(456L);
      when(relationType2.getName()).thenReturn("Dependency");

      try {
         report.followFork("Dependency", "SIDE_A");
         fail("Expected OseeArgumentException");
      } catch (OseeArgumentException ex) {
         assertTrue("Error message should indicate no level created yet",
            ex.getMessage().contains("followFork cannot be called before creating a level"));
      }
   }
}
