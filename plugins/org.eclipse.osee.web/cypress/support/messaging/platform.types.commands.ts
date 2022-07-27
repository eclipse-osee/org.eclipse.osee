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
Cypress.Commands.add('createNewPlatformType', (type: string) => {
  cy.intercept('/orcs/branches/working').as('working');
  cy.intercept('/orcs/branch/10/applic').as('applic');
  cy.intercept('orcs/txs').as('txs');
  cy.intercept('**/*/mim/branch/*/types/filter').as('types');
  cy.intercept('/mim/logicalType/*').as('typeInfo');
  cy.intercept('/ats/config/teamdef/*/leads').as('leads');
  cy.intercept('/ats/ple/action/*/approval').as('approval');
  cy.intercept('/mim/branch/*/query/exact').as('exact')
  return cy
    .get('[data-cy="add-type-bottom-button"]')
    .click()
    .get('mat-progress-bar')
    .should('not.exist')
    .get('[data-cy="logical-type-selector"]')
    .click()
    .get(`[data-cy="logical-type-${type}"]`)
    .click()
    .get('[data-cy="stepper-next-1"]')
    .click()
    .wait('@typeInfo')
    .then((interception) => {
      //interception has id, request , response
      //based on response
      interception.response?.body.fields.forEach((el: any) => {
        if (el.required && el.editable && el.name !== 'Name') {
          cy.get(`[data-cy="field-${el.attributeType}"]`)
            .focus()
            .type(el.defaultValue !== '' ? el.defaultValue : '0',{force:true}).wait('@exact');
        }
        if (el.name === 'Name') {
          cy.get(`[data-cy="field-${el.attributeType}"]`)
            .focus()
            .type(el.defaultValue !== '' ? type + ' ' + el.defaultValue : '0',{force:true}).wait('@exact');
        }
        if (el.name === 'Units') {
          cy.get(`[data-cy="field-${el.attributeType}"]`, {
            timeout: 10000,
          })
            .focus()
            .click({force:true})
            .get('mat-option')
            .first()
            .click().wait('@exact');
        }
      });
    })
    .get('[data-cy="stepper-next-2"]')
    .click({force:true})
    .get('[data-cy=close-new-platform-menu]')
    .click()
    .wait('@txs')
    .get('mat-progress-bar')
    .should('not.exist')
    .wait('@types')
    .wait('@leads')
    .wait('@approval')
    .get('mat-progress-bar')
    .should('not.exist');
});
