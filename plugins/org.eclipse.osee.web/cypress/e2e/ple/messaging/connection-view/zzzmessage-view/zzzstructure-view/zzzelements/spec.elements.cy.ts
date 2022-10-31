import { links } from '../../../../../../../support/messaging/links';

/*********************************************************************
 * Copyright (c) 2022 Boeing
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
links.forEach((link) => {
  describe(`Connection - ${link.name}`, () => {
    before('Initial Navigate', () => {
      cy.navigateToConnectionPage().navigateToMessagePage(link.name);
    });
    link.messages?.forEach((message) => {
      message.subMessages?.forEach((submessage) => {
        describe(`Structure Page - ${message.name} > ${submessage.name} Enable Editing for elements`, () => {
          before(() => {
            cy.navigateToStructurePage(
              message.name,
              submessage.name,
              message.nodeIsFirst
            );
          });
          it('should enable MIM Editing', () => {
            cy.intercept('GET',
              '/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
            ).as('structures');
            cy.intercept('/ats/teamwf/**/*').as('teamwf');
            cy.intercept('/ats/config/teamdef/*/leads').as('leads');
            cy.intercept('/ats/ple/action/*/approval').as('approval');
            cy.enableMIMEditing()
              .wait('@structures')
              .wait('@teamwf')
              .wait('@approval')
              .get('mat-progress-bar')
              .should('not.exist');
          });
        });
        submessage.structures?.forEach((structure) => {
          //top level describe for open
          describe(`Opening Structure - ${structure.name}`, () => {
            it('should open', () => {
              cy.openStructure(structure.name);
            });
          });
          //elements for each -> top level describe
          structure.elements?.forEach((element, index) => {
            describe(`Element Functionality - ${element.name}`, () => {
              it('should create', () => {
                cy.createElement(
                  element.name,
                  element.description,
                  element.notes,
                  element.indexStart,
                  element.indexEnd,
                  element.alterable,
                  element.logicalType
                );
              });
              it('should edit the description', () => {
                cy.editElementDescription(
                  element.name,
                  'Edited ' + element.description
                );
              });
              it('should undo the edit', () => {
                cy.undo();
              });
              it('should edit the notes', () => {
                cy.editElementNotes(element.name, 'Edited ' + element.notes);
              });
              it('should undo the edit', () => {
                cy.undo();
              });
              it('should insert an element @top', () => {
                cy.insertElementTop(element.name);
              });
              it('should insert element @bottom', () => {
                cy.insertElementBottom(element.name);
              });
              it('should remove element @top', () => {
                cy.removeElement('Debug element Top');
              });
              it('delete element @bottom', () => {
                cy.deleteElement('Debug element Bottom');
              });
              it('should have default column setup for edit', () => {
                const elementColumns = [
                  'name',
                  'description',
                  'platformTypeName2',
                  'interfaceElementIndexStart',
                  'interfaceElementIndexEnd',
                  'logicalType',
                  'interfacePlatformTypeDefaultValue',
                  'interfacePlatformTypeMinval',
                  'interfacePlatformTypeMaxval',
                  'beginWord',
                  'endWord',
                  'beginByte',
                  'endByte',
                  'interfaceElementAlterable',
                  'interfacePlatformTypeDescription',
                  'notes',
                  'applicability',
                  'units',
                ];
                const structureColumns = [
                  'name',
                  'description',
                  'interfaceMinSimultaneity',
                  'interfaceMaxSimultaneity',
                  'interfaceTaskFileType',
                  'interfaceStructureCategory',
                  'numElements',
                  'sizeInBytes',
                  'bytesPerSecondMinimum',
                  'bytesPerSecondMaximum',
                  'applicability',
                  'txRate',
                ];
                cy.setUserMIMColumnPreferences(
                  ...elementColumns,
                  ...structureColumns
                );
                elementColumns.forEach((h) =>
                  cy.validateElementHeaderExists(h)
                );
                structureColumns.forEach((h) =>
                  cy.validateStructureHeaderExists(h)
                );
                cy.validateMIMValue(
                  'structure-table',
                  'name',
                  structure.name,
                  structure.name
                )
                  .validateMIMValue(
                    'structure-table',
                    'description',
                    structure.name,
                    structure.description
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceMinSimultaneity',
                    structure.name,
                    structure.minSimultaneity
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceMaxSimultaneity',
                    structure.name,
                    structure.maxSimultaneity
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceTaskFileType',
                    structure.name,
                    structure.taskFileType
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceStructureCategory',
                    structure.name,
                    structure.category
                  )
                  .validateMIMValue(
                    'structure-table',
                    'numElements',
                    structure.name,
                    (index + 1).toString()
                  )
                  .validateMIMValue(
                    'element-table',
                    'name',
                    element.name,
                    element.name
                  )
                  .validateMIMValue(
                    'element-table',
                    'description',
                    element.name,
                    element.description
                  )
                  .validateMIMValue(
                    'element-table',
                    'platformTypeName2',
                    element.name,
                    element.logicalType + ' Name'
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementIndexStart',
                    element.name,
                    element.indexStart
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementIndexEnd',
                    element.name,
                    element.indexEnd
                  )
                  .validateMIMValue(
                    'element-table',
                    'logicalType',
                    element.name,
                    element.logicalType
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementAlterable',
                    element.name,
                    'true'
                  )
                  .validateMIMValue(
                    'element-table',
                    'description',
                    element.name,
                    element.description
                  )
                  .validateMIMValue(
                    'element-table',
                    'notes',
                    element.name,
                    element.notes
                  );
              });
              it('should have less columns than default edit', () => {
                const elementColumns = [
                  'name',
                  'description',
                  'platformTypeName2',
                  'interfaceElementIndexStart',
                  'interfaceElementIndexEnd',
                  'logicalType',
                  'interfaceElementAlterable',
                  'notes',
                  'applicability',
                ];
                const structureColumns = [
                  'name',
                  'description',
                  'interfaceMinSimultaneity',
                  'interfaceMaxSimultaneity',
                  'interfaceTaskFileType',
                  'interfaceStructureCategory',
                  'numElements',
                  'sizeInBytes',
                  'bytesPerSecondMinimum',
                  'bytesPerSecondMaximum',
                  'applicability',
                  'txRate',
                ];
                cy.setUserMIMColumnPreferences(
                  ...elementColumns,
                  ...structureColumns
                );
                elementColumns.forEach((h) =>
                  cy.validateElementHeaderExists(h)
                );
                structureColumns.forEach((h) =>
                  cy.validateStructureHeaderExists(h)
                );
                cy.validateMIMValue(
                  'structure-table',
                  'name',
                  structure.name,
                  structure.name
                )
                  .validateMIMValue(
                    'structure-table',
                    'description',
                    structure.name,
                    structure.description
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceMinSimultaneity',
                    structure.name,
                    structure.minSimultaneity
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceMaxSimultaneity',
                    structure.name,
                    structure.maxSimultaneity
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceTaskFileType',
                    structure.name,
                    structure.taskFileType
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceStructureCategory',
                    structure.name,
                    structure.category
                  )
                  .validateMIMValue(
                    'element-table',
                    'name',
                    element.name,
                    element.name
                  )
                  .validateMIMValue(
                    'element-table',
                    'description',
                    element.name,
                    element.description
                  )
                  .validateMIMValue(
                    'element-table',
                    'platformTypeName2',
                    element.name,
                    element.logicalType + ' Name'
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementIndexStart',
                    element.name,
                    element.indexStart
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementIndexEnd',
                    element.name,
                    element.indexEnd
                  )
                  .validateMIMValue(
                    'element-table',
                    'logicalType',
                    element.name,
                    element.logicalType
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementAlterable',
                    element.name,
                    'true'
                  )
                  .validateMIMValue(
                    'element-table',
                    'description',
                    element.name,
                    element.description
                  )
                  .validateMIMValue(
                    'element-table',
                    'notes',
                    element.name,
                    element.notes
                  );
              });
              it('should have all columns', () => {
                const elementColumns = [
                  'name',
                  'platformTypeName2',
                  'interfaceElementIndexStart',
                  'interfaceElementIndexEnd',
                  'logicalType',
                  'interfacePlatformTypeDefaultValue',
                  'interfacePlatformTypeMinval',
                  'interfacePlatformTypeMaxval',
                  'interfacePlatformTypeDescription',
                  'beginWord',
                  'endWord',
                  'beginByte',
                  'endByte',
                  'interfaceElementAlterable',
                  'description',
                  'notes',
                  'applicability',
                  'units',
                ];
                const structureColumns = [
                  'interfaceMinSimultaneity',
                  'interfaceMaxSimultaneity',
                  'interfaceTaskFileType',
                  'interfaceStructureCategory',
                  'numElements',
                  'sizeInBytes',
                  'bytesPerSecondMinimum',
                  'bytesPerSecondMaximum',
                  'txRate',
                ];
                cy.setUserMIMColumnPreferences(
                  ...elementColumns,
                  ...structureColumns
                );
                elementColumns.forEach((h) =>
                  cy.validateElementHeaderExists(h)
                );
                structureColumns.forEach((h) =>
                  cy.validateStructureHeaderExists(h)
                );
                cy.validateMIMValue(
                  'structure-table',
                  'name',
                  structure.name,
                  structure.name
                )
                  .validateMIMValue(
                    'structure-table',
                    'description',
                    structure.name,
                    structure.description
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceMinSimultaneity',
                    structure.name,
                    structure.minSimultaneity
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceMaxSimultaneity',
                    structure.name,
                    structure.maxSimultaneity
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceTaskFileType',
                    structure.name,
                    structure.taskFileType
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceStructureCategory',
                    structure.name,
                    structure.category
                  )
                  .validateMIMValue(
                    'structure-table',
                    'numElements',
                    structure.name,
                    (index + 1).toString()
                  )
                  .validateMIMValue(
                    'element-table',
                    'name',
                    element.name,
                    element.name
                  )
                  .validateMIMValue(
                    'element-table',
                    'platformTypeName2',
                    element.name,
                    element.logicalType + ' Name'
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementIndexStart',
                    element.name,
                    element.indexStart
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementIndexEnd',
                    element.name,
                    element.indexEnd
                  )
                  .validateMIMValue(
                    'element-table',
                    'logicalType',
                    element.name,
                    element.logicalType
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementAlterable',
                    element.name,
                    'true'
                  )
                  .validateMIMValue(
                    'element-table',
                    'description',
                    element.name,
                    element.description
                  )
                  .validateMIMValue(
                    'element-table',
                    'notes',
                    element.name,
                    element.notes
                  );
              });

              it('should have default view column prefs after resetting', () => {
                cy.intercept('GET',
                  '/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
                ).as('structures');
                cy.disableMIMEditing()
                  .wait('@structures')
                  .get('mat-progress-bar')
                  .should('not.exist')
                  .resetColumnPrefsToDefault();
                const elementColumns = [
                  'name',
                  'description',
                  'logicalType',
                  'interfacePlatformTypeDescription',
                  'notes',
                ];
                elementColumns.forEach((h) =>
                  cy.validateElementHeaderExists(h)
                );
                cy.validateMIMValue(
                  'structure-table',
                  'name',
                  structure.name,
                  structure.name
                )
                  .validateMIMValue(
                    'structure-table',
                    'description',
                    structure.name,
                    structure.description
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceMinSimultaneity',
                    structure.name,
                    structure.minSimultaneity
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceMaxSimultaneity',
                    structure.name,
                    structure.maxSimultaneity
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceTaskFileType',
                    structure.name,
                    structure.taskFileType
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceStructureCategory',
                    structure.name,
                    structure.category
                  )
                  .validateMIMValue(
                    'structure-table',
                    'numElements',
                    structure.name,
                    (index + 1).toString()
                  )
                  .validateMIMValue(
                    'element-table',
                    'name',
                    element.name,
                    element.name
                  )
                  .validateMIMValue(
                    'element-table',
                    'description',
                    element.name,
                    element.description
                  )
                  .validateMIMValue(
                    'element-table',
                    'logicalType',
                    element.name,
                    element.logicalType
                  )
                  .validateMIMValue(
                    'element-table',
                    'notes',
                    element.name,
                    element.notes
                  );
              });
              it('should have default edit column prefs after resetting', () => {
                cy.intercept('GET',
                  '/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
                ).as('structures');
                cy.enableMIMEditing()
                  .wait('@structures')
                  .get('mat-progress-bar')
                  .should('not.exist')
                  .resetColumnPrefsToDefault();
                const elementColumns = [
                  'name',
                  'description',
                  'platformTypeName2',
                  'interfaceElementIndexStart',
                  'interfaceElementIndexEnd',
                  'logicalType',
                  'interfacePlatformTypeDefaultValue',
                  'interfacePlatformTypeMinval',
                  'interfacePlatformTypeMaxval',
                  'beginWord',
                  'endWord',
                  'beginByte',
                  'endByte',
                  'interfaceElementAlterable',
                  'interfacePlatformTypeDescription',
                  'notes',
                  'applicability',
                  'units',
                ];
                elementColumns.forEach((h) =>
                  cy.validateElementHeaderExists(h)
                );
                cy.validateMIMValue(
                  'structure-table',
                  'name',
                  structure.name,
                  structure.name
                )
                  .validateMIMValue(
                    'structure-table',
                    'description',
                    structure.name,
                    structure.description
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceMinSimultaneity',
                    structure.name,
                    structure.minSimultaneity
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceMaxSimultaneity',
                    structure.name,
                    structure.maxSimultaneity
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceTaskFileType',
                    structure.name,
                    structure.taskFileType
                  )
                  .validateMIMValue(
                    'structure-table',
                    'interfaceStructureCategory',
                    structure.name,
                    structure.category
                  )
                  .validateMIMValue(
                    'structure-table',
                    'numElements',
                    structure.name,
                    (index + 1).toString()
                  )
                  .validateMIMValue(
                    'element-table',
                    'name',
                    element.name,
                    element.name
                  )
                  .validateMIMValue(
                    'element-table',
                    'platformTypeName2',
                    element.name,
                    element.logicalType + ' Name'
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementIndexStart',
                    element.name,
                    element.indexStart
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementIndexEnd',
                    element.name,
                    element.indexEnd
                  )
                  .validateMIMValue(
                    'element-table',
                    'logicalType',
                    element.name,
                    element.logicalType
                  )
                  .validateMIMValue(
                    'element-table',
                    'interfaceElementAlterable',
                    element.name,
                    'true'
                  )
                  .validateMIMValue(
                    'element-table',
                    'description',
                    element.name,
                    element.description
                  )
                  .validateMIMValue(
                    'element-table',
                    'notes',
                    element.name,
                    element.notes
                  );
              });
            });
          });
          //top level describe for close
          describe(`Closing Structure - ${structure.name}`, () => {
            it('should close', () => {
              cy.closeStructure(structure.name);
            });
          });
        });
        describe(`Structure Page - ${message.name} > ${submessage.name} Disable Editing`, () => {
          it('should disable MIM Editing', () => {
            cy.disableMIMEditing();
            cy.navigateToConnectionPage().navigateToMessagePage(link.name);
          });
        });
      });
    });
  });
});
