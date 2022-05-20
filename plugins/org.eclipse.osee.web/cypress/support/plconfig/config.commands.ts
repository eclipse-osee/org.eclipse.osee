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
Cypress.Commands.add(
  'editConfiguration',
  (
    wasValue: {
      title: string;
      productTypes: {
        name: string;
        enabled: boolean;
      }[];
      groups: {
        name: string;
        enabled: boolean;
      }[];
    },
    isValue: {
      title: string;
      productTypes: {
        name: string;
        enabled: boolean;
      }[];
      groups: {
        name: string;
        enabled: boolean;
      }[];
    }
  ) => {
    cy.intercept('PUT', '/orcs/branch/*/applic/view').as('editConfiguration');
    cy.intercept('/orcs/branches/*').as('branch');
    cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
    cy.intercept('/orcs/applicui/branch/*').as('applicui');
    cy.intercept('/ats/action/*').as('action');
    cy.intercept('/ats/teamwf/*').as('teamwf');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/orcs/types/productApplicability').as('productApplicability');
    return cy
      .get(`[data-cy-value="field-name-${wasValue.title}"]`)
      .should('exist')
      .get(`[data-cy="field-name"]`)
      .focus()
      .clear()
      .type(isValue.title)
      .verifyGroups(wasValue.groups)
      .editGroups(wasValue.groups, isValue.groups)
      .verifyProductTypes(wasValue.productTypes, true)
      .editProductTypes(wasValue.productTypes, isValue.productTypes)
      .get(`[data-cy="submit-btn"]`)
      .click()
      .wait('@editConfiguration')
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
Cypress.Commands.add(
  'editConfigurationClick',
  (
    wasValue: {
      title: string;
      productTypes: {
        name: string;
        enabled: boolean;
      }[];
      groups: {
        name: string;
        enabled: boolean;
      }[];
    },
    isValue: {
      title: string;
      productTypes: {
        name: string;
        enabled: boolean;
      }[];
      groups: {
        name: string;
        enabled: boolean;
      }[];
    }
  ) => {
    return cy
      .get(`[data-cy="config-header-${wasValue.title}"`)
      .click()
      .editConfiguration(wasValue, isValue);
  }
);
Cypress.Commands.add(
  'editConfigurationDropdown',
  (
    wasValue: {
      title: string;
      productTypes: {
        name: string;
        enabled: boolean;
      }[];
      groups: {
        name: string;
        enabled: boolean;
      }[];
    },
    isValue: {
      title: string;
      productTypes: {
        name: string;
        enabled: boolean;
      }[];
      groups: {
        name: string;
        enabled: boolean;
      }[];
    }
  ) => {
    return cy
      .get('[data-cy=change-config-dropdown-btn]')
      .click()
      .get('[data-cy=edit-menu-btn]')
      .click()
      .get(`[data-cy="edit-config-${wasValue.title}-btn"]`)
      .click()
      .editConfiguration(wasValue, isValue);
  }
);
Cypress.Commands.add(
  'createConfiguration',
  (value: {
    title: string;
    copyFrom: string;
    groups: string[];
    productTypes: {
      name: string;
      enabled: boolean;
    }[];
  }) => {
    cy.intercept('/orcs/branch/*/applic/view').as('submitConfig');
    cy.intercept('/orcs/branch/*/applic/cfggroup/sync/*').as('cfgGroupSync');
    cy.intercept('/orcs/branches/*').as('branch');
    cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
    cy.intercept('/orcs/applicui/branch/*').as('applicui');
    cy.intercept('/ats/action/*').as('action');
    cy.intercept('/ats/teamwf/*').as('teamwf');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/orcs/types/productApplicability').as('productApplicability');
    cy.get('[data-cy=change-config-dropdown-btn]')
      .click()
      .get(`[data-cy="add-menu-btn"]`)
      .click()
      .get(`[data-cy="field-name"]`)
      .focus()
      .clear()
      .type(value.title)
      .get('[data-cy="field-copyFrom"]')
      .click()
      .get(`[data-cy="option-${value.copyFrom}"]`)
      .click()
      .get('[data-cy="field-configGroup"]')
      .click();
    value.groups.forEach((group) => {
      cy.get(`[data-cy="option-${group}"]`).click();
    });
    const types = value.productTypes
      .filter((t) => t.enabled)
      .map((type) => ({
        ...type,
        currentState: false,
      }));
    cy.get('.cdk-overlay-transparent-backdrop')
      .last()
      .click()
      .selectProductTypes(types)
      .get(`[data-cy="submit-btn"]`)
      .click()
      .wait('@submitConfig');
    value.groups.forEach((group) => {
      cy.wait('@cfgGroupSync');
    });
    return cy
      .wait('@branch')
      .wait('@cfggroup')
      .wait('@applicui')
      .wait('@action')
      .wait('@teamwf')
      .wait('@approval')
      .wait('@leads')
      .wait('@productApplicability')
      .get('mat-progress-bar')
      .should('not.exist');
  }
);
Cypress.Commands.add('deleteConfiguration', (name: string) => {
  cy.intercept('DELETE', '/orcs/branch/*/applic/view/*').as('deleteConfig');
  cy.intercept('/orcs/branches/*').as('branch');
  cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
  cy.intercept('/orcs/applicui/branch/*').as('applicui');
  cy.intercept('/ats/action/*').as('action');
  cy.intercept('/ats/teamwf/*').as('teamwf');
  cy.intercept('/ats/ple/action/*/approval').as('approval');
  cy.intercept('/ats/config/teamdef/*/leads').as('leads');
  cy.intercept('/orcs/types/productApplicability').as('productApplicability');
  return cy
    .get('[data-cy=change-config-dropdown-btn]')
    .click()
    .get(`[data-cy="delete-menu-btn"]`)
    .click()
    .get(`[data-cy="delete-config-${name}-btn"]`)
    .click()
    .wait('@deleteConfig')
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
  'copyConfiguration',
  (copyTo: string, copyFrom: string, groupCount: number) => {
    cy.intercept('/orcs/applicui/branch/*').as('configDropdown');
    cy.intercept('PUT', '/orcs/branch/*/applic/view').as('configUpdate');
    cy.intercept('POST', '/orcs/branch/*/applic/cfggroup/sync/*').as(
      'cfgGroupSync'
    );
    cy.intercept('/orcs/branches/*').as('branch');
    cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
    cy.intercept('/orcs/applicui/branch/*').as('applicui');
    cy.intercept('/ats/action/*').as('action');
    cy.intercept('/ats/teamwf/*').as('teamwf');
    cy.intercept('/ats/ple/action/*/approval').as('approval');
    cy.intercept('/ats/config/teamdef/*/leads').as('leads');
    cy.intercept('/orcs/types/productApplicability').as('productApplicability');
    cy.get('[data-cy=change-config-dropdown-btn]')
      .click()
      .get(`[data-cy="copy-menu-btn"]`)
      .click()
      .wait('@configDropdown')
      .get('[data-cy=copyTo-configuration]')
      .click()
      .get(`[data-cy="option-${copyTo}"]`)
      .click()
      .get('[data-cy=copyFrom-configuration]')
      .click()
      .get(`[data-cy="option-${copyFrom}"]`)
      .click()
      .get(`[data-cy="submit-btn"]`)
      .click()
      .wait('@configUpdate');
    while (groupCount--) {
      cy.wait('@cfgGroupSync');
    }
    return cy
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
