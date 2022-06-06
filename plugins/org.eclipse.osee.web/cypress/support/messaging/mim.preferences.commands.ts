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
Cypress.Commands.add('openMIMUserDialog', () => {
  return cy
    .get('[data-cy="user-overflow-btn"]')
    .click()
    .get('[data-cy="mim-settings"]')
    .click()
    .get('mat-progress-bar', { timeout: 10000 })
    .should('not.exist');
});

Cypress.Commands.add('closeMIMUserDialog', () => {
  cy.intercept('/orcs/txs').as('txs');
  cy.intercept('/mim/user/*').as('user');
  return cy
    .get('[data-cy="submit-btn"]')
    .click({ force: true })
    .get('app-column-preferences-dialog')
    .should('not.exist')
    .wait('@txs')
    .wait('@user')
    .get('mat-progress-bar')
    .should('exist');
});

Cypress.Commands.add('waitForMIMUserDialog', () => {
  cy.intercept('/orcs/txs').as('txs');
  cy.intercept('/ats/config/teamdef/*/leads').as('leads');
  cy.intercept('/ats/ple/action/*/approval').as('approval');
  cy.intercept('/orcs/applicui/branch/*').as('applicui');
  cy.intercept('/ats/teamwf/**/*').as('teamwf');
  return (
    cy
      .wait('@leads')
      // .wait('@teamwf')
      // .wait('@approval')
      .get('app-column-preferences-dialog')
      .should('not.exist')
  );
});

Cypress.Commands.add('resetColumnPrefsToDefault', () => {
  return cy
    .openMIMUserDialog()
    .get('mat-progress-bar', { timeout: 10000 })
    .should('not.exist')
    .get('[data-cy="reset-default-btn"]')
    .click()
    .closeMIMUserDialog()
    .get('app-column-preferences-dialog')
    .should('not.exist');
});
Cypress.Commands.add('setUserMIMEditPreference', (preference: boolean) => {
  if (preference) {
    return cy
      .openMIMUserDialog()
      .get('input[type="checkbox"]')
      .check({ force: true })
      .closeMIMUserDialog()
      .waitForMIMUserDialog();
  } else {
    return cy
      .openMIMUserDialog()
      .get('input[type="checkbox"]')
      .uncheck({ force: true })
      .closeMIMUserDialog()
      .waitForMIMUserDialog();
  }
});
Cypress.Commands.add('enableMIMEditing', () => {
  return cy.setUserMIMEditPreference(true);
});
Cypress.Commands.add('disableMIMEditing', () => {
  return cy.setUserMIMEditPreference(false);
});
Cypress.Commands.add(
  'setUserMIMColumnPreferences',
  (...preferences: string[]) => {
    cy.intercept('/orcs/txs').as('txs');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    cy.intercept('/orcs/applicui/branch/*').as('applicui');
    cy.intercept('/ats/teamwf/**/*').as('teamwf');
    cy.intercept('GET', '/ats/action/**/*').as('action');
    cy.intercept(
      '/mim/branch/*/connections/*/messages/*/submessages/*/structures/**/*'
    ).as('structures');
    const allPreferences = [
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
      'txRate',
    ];
    const selectedPreferences = allPreferences.filter((value) =>
      preferences.includes(value)
    );
    cy.openMIMUserDialog()
    .get('mat-progress-bar', { timeout: 10000 })
    .should('not.exist');
    /**
     * Set all preferences that are checked to unchecked.
     */
    allPreferences.forEach((pref) => {
      cy.get('body').then((body) => {
        if (
          body.find(
            `[data-cy="header-${pref}"][data-cy-checked="selection-true"]`
          ).length > 0
        ) {
          cy.get(
            `[data-cy="header-${pref}"][data-cy-checked="selection-true"]`
          ).click({ multiple: true });
        }
      });
    });
    selectedPreferences.forEach((pref) => {
      cy.get('body').then((body) => {
        if (
          body.find(
            `[data-cy="header-${pref}"][data-cy-checked="selection-false"]`
          ).length > 0
        ) {
          cy.get(
            `[data-cy="header-${pref}"][data-cy-checked="selection-false"]`
          ).click({ multiple: true });
        }
      });
    });
    return cy
      .closeMIMUserDialog()
      .wait('@txs')
      .waitForMIMUserDialog()
      .wait('@structures')
      .wait('@action')
      .get('mat-progress-bar', { timeout: 10000 })
      .should('not.exist');
  }
);
