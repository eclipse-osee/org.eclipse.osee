import { links } from '../../../../../../support/messaging/links';

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
        describe(`Structure Page - ${message.name} > ${submessage.name} Enable Editing`, () => {
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
            cy.intercept('POST','orcs/txs').as('txs');
            cy.intercept('GET', '/ats/action/**/*').as('action');
            cy.intercept('/ats/teamwf/**/*').as('teamwf');
            cy.intercept('/ats/config/teamdef/*/leads').as('leads');
            cy.intercept('/ats/ple/action/*/approval').as('approval');
            cy.enableMIMEditing()
              .wait('@txs')
              .wait('@txs')
              .get('mat-progress-bar')
              .should('exist')
              .wait('@txs').its('response.statusCode')
              .should('eq',200)
              .get('mat-progress-bar')
              .should('not.exist')
              .wait('@structures')
              .wait('@action')
              .wait('@teamwf')
              .wait('@approval')
              .get('mat-progress-bar')
              .should('not.exist');
          });
          it('should turn all columns on', () => {
            cy.intercept('GET',
              '/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
            ).as('structures');
            cy.setUserMIMColumnPreferences(
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
              'interfaceMinSimultaneity',
              'interfaceMaxSimultaneity',
              'interfaceTaskFileType',
              'interfaceStructureCategory',
              'numElements',
              'sizeInBytes',
              'bytesPerSecondMinimum',
              'bytesPerSecondMaximum',
              'txRate'
            )
              .wait('@structures')
              .wait(5000)
              .get('mat-progress-bar')
              .should('not.exist');
          });
        });
        submessage.structures?.forEach((structure) => {
          describe(`Structure Page - ${message.name} > ${submessage.name} Functionality`, () => {
            it(`should create structure - ${structure.name}`, () => {
              cy.createStructure(
                structure.name,
                structure.description,
                structure.maxSimultaneity,
                structure.minSimultaneity,
                structure.taskFileType,
                structure.category
              );
            });
            it('should edit description', () => {
              cy.editStructureDescription(
                structure.name,
                'Edited ' + structure.description
              );
            });
            it('should undo the edit', () => {
              cy.undo();
            });
            it('should insert a structure at the top', () => {
              cy.insertStructureTop(structure.name);
            });
            it('should remove created debug structure(@top)', () => {
              cy.removeStructure('Top Structure');
            });
            it('should insert a structure at the bottom', () => {
              cy.insertStructureBottom(structure.name);
            });
            it('should delete created debug structure(@bottom)', () => {
              cy.deleteStructure('Bottom Structure');
            });
            it('should have default column setup for edit', () => {
              cy.intercept('POST','/orcs/txs').as('txs');
              cy.get('mat-progress-bar',{timeout:20000}).should('not.exist')
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
              cy.wait(5000);
              structureColumns.forEach((h) =>
                cy.validateStructureHeaderExists(h)
              );
              //this has to be hard coded because some columns are using ng-containers instead of divs
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
                   '0'
              )
              .wait('@txs')
                //.validateMIMValue('structure-table', 'sizeInBytes', structure.name, structure.description) don't quite know how to verify these without re-implementing api
                //.validateMIMValue('structure-table', 'bytesPerSecondMinimum', structure.name, structure.description)
                //.validateMIMValue('structure-table', 'bytesPerSecondMaximum', structure.name, structure.description)
                //.validateMIMValue('structure-table', 'applicability', structure.name, structure.applicability)
            });
            it('should have less columns than default edit', () => {
              cy.intercept('POST','/orcs/txs').as('txs');
              cy.get('mat-progress-bar',{timeout:20000}).should('not.exist')
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
                'bytesPerSecondMinimum',
                'bytesPerSecondMaximum',
                'applicability',
              ];
              cy.setUserMIMColumnPreferences(
                ...elementColumns,
                ...structureColumns
              );
              cy.wait(5000);
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
              .wait('@txs')
            });
            it('should have all columns', () => {
              cy.intercept('POST','/orcs/txs').as('txs');
              cy.get('mat-progress-bar',{timeout:20000}).should('not.exist')
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
              cy.wait(5000);
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
                  '0'
              )
              .wait('@txs')
            });

            //these two tests need a bit more work, they are not stable.
            // it('should have default view column prefs after resetting', () => {
            //   cy.intercept('POST','/orcs/txs').as('txs');
            //   cy.intercept('GET',
            //     '/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
            //   ).as('structures');
            //   cy.intercept('/mim/user/*').as('user')
            //   cy.get('mat-progress-bar',{timeout:20000}).should('not.exist')
            //   cy.disableMIMEditing()
            //     .wait('@txs')
            //     .wait('@txs')
            //     .wait('@user')
            //     .wait('@txs')
            //     .wait('@txs')
            //     .wait('@structures')
            //     .get('mat-progress-bar')
            //     .should('not.exist')
            //     .resetColumnPrefsToDefault()
            //     .get('mat-progress-bar')
            //     .should('not.exist');
            //   const structureColumns = [
            //     'name',
            //     'description',
            //     'interfaceMinSimultaneity',
            //     'interfaceMaxSimultaneity',
            //     'interfaceTaskFileType',
            //     'interfaceStructureCategory',
            //     'numElements',
            //     'sizeInBytes',
            //   ];
            //   cy.wait(5000);
            //   structureColumns.forEach((h) =>
            //     cy.validateStructureHeaderExists(h)
            //   );
            //   cy.validateMIMValue(
            //     'structure-table',
            //     'name',
            //     structure.name,
            //     structure.name
            //   )
            //     .validateMIMValue(
            //       'structure-table',
            //       'description',
            //       structure.name,
            //       structure.description
            //     )
            //     .validateMIMValue(
            //       'structure-table',
            //       'interfaceMinSimultaneity',
            //       structure.name,
            //       structure.minSimultaneity
            //     )
            //     .validateMIMValue(
            //       'structure-table',
            //       'interfaceMaxSimultaneity',
            //       structure.name,
            //       structure.maxSimultaneity
            //     )
            //     .validateMIMValue(
            //       'structure-table',
            //       'interfaceTaskFileType',
            //       structure.name,
            //       structure.taskFileType
            //     )
            //     .validateMIMValue(
            //       'structure-table',
            //       'interfaceStructureCategory',
            //       structure.name,
            //       structure.category
            //     )
            //     .validateMIMValue(
            //       'structure-table',
            //       'numElements',
            //       structure.name,
            //       '0'
            //     ).wait('@txs')
            // });
            // it('should have default edit column prefs after resetting', () => {
            //   cy.intercept('POST','/orcs/txs').as('txs');
            //   cy.intercept('/mim/user/*').as('user')
            //   cy.get('mat-progress-bar',{timeout:20000}).should('not.exist')
            //   cy.intercept('GET',
            //     '/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
            //   ).as('structures');
            //   cy.enableMIMEditing()
            //     .wait('@txs')
            //     .wait('@txs')
            //     .wait('@user')
            //     .wait('@txs')
            //     .wait('@txs')
            //     .wait('@structures')
            //     .get('mat-progress-bar',{timeout:20000})
            //     .should('not.exist')
            //     .resetColumnPrefsToDefault()
            //     // .get('mat-progress-bar')
            //     // .should('not.exist')
            //     ;
            //   const structureColumns = [
            //     'name',
            //     'description',
            //     'interfaceMinSimultaneity',
            //     'interfaceMaxSimultaneity',
            //     'interfaceTaskFileType',
            //     'interfaceStructureCategory',
            //     'numElements',
            //     'sizeInBytes',
            //     'bytesPerSecondMinimum',
            //     'bytesPerSecondMaximum',
            //     'applicability',
            //     'txRate',
            //   ];
            //   cy.wait(5000);
            //   structureColumns.forEach((h) =>
            //     cy.validateStructureHeaderExists(h)
            //   );
            //   cy.validateMIMValue(
            //     'structure-table',
            //     'name',
            //     structure.name,
            //     structure.name
            //   )
            //     .validateMIMValue(
            //       'structure-table',
            //       'description',
            //       structure.name,
            //       structure.description
            //     )
            //     .validateMIMValue(
            //       'structure-table',
            //       'interfaceMinSimultaneity',
            //       structure.name,
            //       structure.minSimultaneity
            //     )
            //     .validateMIMValue(
            //       'structure-table',
            //       'interfaceMaxSimultaneity',
            //       structure.name,
            //       structure.maxSimultaneity
            //     )
            //     .validateMIMValue(
            //       'structure-table',
            //       'interfaceTaskFileType',
            //       structure.name,
            //       structure.taskFileType
            //     )
            //     .validateMIMValue(
            //       'structure-table',
            //       'interfaceStructureCategory',
            //       structure.name,
            //       structure.category
            //     )
            //     .validateMIMValue(
            //       'structure-table',
            //       'numElements',
            //       structure.name,
            //       '0'
            //     ).wait('@txs')
            // });
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
