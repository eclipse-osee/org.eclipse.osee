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
Cypress.Commands.add('openConfigGroupDropdown', () => {
  return cy.get('[data-cy=change-config-group-dropdown-btn]').click();
});
Cypress.Commands.add('addConfigurationGroup', (name: string) => {
  cy.intercept('POST', '/orcs/branch/*/applic/cfggroup/').as('createCfgGroup');
  cy.intercept('/orcs/branches/*').as('branch');
  cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
  cy.intercept('/orcs/applicui/branch/*').as('applicui');
  cy.intercept('/ats/action/*').as('action');
  cy.intercept('/ats/teamwf/*').as('teamwf');
  cy.intercept('/ats/ple/action/*/approval').as('approval');
  cy.intercept('/ats/config/teamdef/*/leads').as('leads');
  cy.intercept('/orcs/types/productApplicability').as('productApplicability');
  return cy
    .openConfigGroupDropdown()
    .get(`[data-cy="add-config-group-btn"]`)
    .click()
    .get(`[data-cy="field-title"]`)
    .focus()
    .clear()
    .type(name)
    .get('[data-cy=submit-btn]')
    .click()
    .wait('@createCfgGroup')
    .wait('@branch')
    .wait('@cfggroup')
    .wait('@applicui')
    .wait('@action')
    .wait('@teamwf')
    .wait('@approval')
    .wait('@leads')
    .get('mat-progress-bar', { timeout: 10000 })
    .should('not.exist');
});
Cypress.Commands.add('syncConfigGroups', () => {
  cy.intercept('POST', '/orcs/branch/*/applic/cfggroup/sync/*').as('sync');
  return cy
    .openConfigGroupDropdown()
    .get('[data-cy="sync-config-group-btn"]')
    .click()
    .wait('@sync')
    .wait('@sync')
    .wait('@sync')
    .get('mat-progress-bar', { timeout: 10000 })
    .should('not.exist');
});
Cypress.Commands.add('deleteConfigGroup', (name: string) => {
  cy.intercept('/orcs/branches/*').as('branch');
  cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
  cy.intercept('/orcs/applicui/branch/*').as('applicui');
  cy.intercept('/ats/action/*').as('action');
  cy.intercept('/ats/teamwf/*').as('teamwf');
  cy.intercept('/ats/ple/action/*/approval').as('approval');
  cy.intercept('/ats/config/teamdef/*/leads').as('leads');
  cy.intercept('/orcs/types/productApplicability').as('productApplicability');
  cy.intercept('DELETE', '/orcs/branch/*/applic/cfggroup/*').as('deleteGroup');
  return cy
    .openConfigGroupDropdown()
    .get('[data-cy="delete-config-group-btn"]')
    .click()
    .get(`[data-cy="delete-config-group-${name}-btn"]`)
    .click()
    .wait('@deleteGroup')
    .wait('@branch')
    .wait('@cfggroup')
    .wait('@applicui')
    .wait('@action')
    .wait('@teamwf')
    .wait('@approval')
    .wait('@leads')
    .get('mat-progress-bar', { timeout: 10000 })
    .should('not.exist');
});
Cypress.Commands.add(
  'verifyGroups',
  (groups: { name: string; enabled: boolean }[]) => {
    cy.get('[data-cy=field-group]').click();
    groups.forEach((group) => {
      cy.get(`[data-cy="option-${group.name}-${group.enabled}"]`).should(
        'exist'
      );
    });
    return cy
      .get('.cdk-overlay-transparent-backdrop')
      .last()
      .click({ force: true });
  }
);
Cypress.Commands.add(
  'editGroups',
  (
    wasValues: { name: string; enabled: boolean }[],
    isValues: { name: string; enabled: boolean }[]
  ) => {
    cy.get('[data-cy=field-group]').click();
    if (wasValues.length !== isValues.length) {
      throw Error("Configuration Groups Array Length Doesn't Match");
    }
    isValues.forEach(({ name, enabled }, index) => {
      if (name !== wasValues[index].name) {
        throw Error("Group Names Don't Match");
      }
      if (enabled !== wasValues[index].enabled) {
        cy.get(
          `[data-cy="option-${name}-${wasValues[index].enabled}"]`
        ).click();
      }
    });
    return cy
      .get('.cdk-overlay-transparent-backdrop')
      .last()
      .click({ force: true });
  }
);
Cypress.Commands.add(
  'removeConfigFromGroup',
  (group: string, config: string) => {
    cy.intercept('PUT', '/orcs/branch/*/applic/cfggroup').as('updateCfgGroup');
    cy.intercept('/orcs/branches/*').as('branch');
    cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
    cy.intercept('/orcs/applicui/branch/*').as('applicui');
    cy.intercept('/ats/action/*').as('action');
    cy.intercept('/ats/teamwf/*').as('teamwf');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/orcs/types/productApplicability').as('productApplicability');
    cy.get(`[data-cy="config-group-header-${group}"]`)
      .click()
      .get(`[data-cy="view-${config}"]`)
      .click()
      .get('[data-cy=submit-btn]')
      .click()
      .wait('@updateCfgGroup')
      .wait('@branch')
      .wait('@cfggroup')
      .wait('@applicui')
      .wait('@action')
      .wait('@teamwf')
      .wait('@approval')
      .wait('@leads')
      .get('mat-progress-bar', { timeout: 10000 })
      .should('not.exist');
  }
);
