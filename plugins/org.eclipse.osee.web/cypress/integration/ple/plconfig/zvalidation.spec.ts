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
describe('PLConfig - Test Wrap-up Validation', () => {
    before(() => {
        cy.visit('/ple').get('[data-cy="plconfig-nav-button"]').click();
        cy.task<Cypress.NameResult>('getLatestBranchName').then((branchname) => {
          const branch: string = branchname.name;
          return cy.selectBranch(branch, 'working');
        });
      });
  it('should have configs setup', () => {
    cy.validatePLConfigValues([
      {
        title: 'Product C',
        values: [
          { title: 'CUSTOM_FEATURE', value: 'Excluded' },
          { title: 'CUSTOM_FEATURE2', value: 'VALUE_3' },
          { title: 'DEFAULT_FEATURE', value: 'Included' },
          { title: 'ENGINE_5', value: 'A2543' },
          { title: 'JHU_CONTROLLER', value: 'Included' },
          { title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
          { title: 'ROBOT_SPEAKER', value: 'SPKR_B' },
        ],
      },
      {
        title: 'Product E',
        values: [
          { title: 'CUSTOM_FEATURE', value: 'Excluded' },
          { title: 'CUSTOM_FEATURE2', value: 'VALUE_2,VALUE_3' },
          { title: 'DEFAULT_FEATURE', value: 'Included' },
          { title: 'ENGINE_5', value: 'A2543' },
          { title: 'JHU_CONTROLLER', value: 'Included' },
          { title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
          { title: 'ROBOT_SPEAKER', value: 'SPKR_B' },
        ],
      },
      {
        title: 'Product K',
        values: [
          { title: 'CUSTOM_FEATURE', value: 'Included' },
          { title: 'CUSTOM_FEATURE2', value: 'VALUE_3' },
          { title: 'DEFAULT_FEATURE', value: 'Included' },
          { title: 'ENGINE_5', value: 'B5543' },
          { title: 'JHU_CONTROLLER', value: 'Excluded' },
          { title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
          { title: 'ROBOT_SPEAKER', value: 'SPKR_B' },
        ],
      },
      {
        title: 'Product A',
        values: [
          { title: 'CUSTOM_FEATURE', value: 'Excluded' },
          { title: 'CUSTOM_FEATURE2', value: 'VALUE_2' },
          { title: 'DEFAULT_FEATURE', value: 'Included' },
          { title: 'ENGINE_5', value: 'A2543' },
          { title: 'JHU_CONTROLLER', value: 'Excluded' },
          { title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
          { title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
        ],
      },
      {
        title: 'Product F',
        values: [
          { title: 'CUSTOM_FEATURE', value: 'Included' },
          { title: 'CUSTOM_FEATURE2', value: 'VALUE_2,VALUE_3' },
          { title: 'DEFAULT_FEATURE', value: 'Included' },
          { title: 'ENGINE_5', value: 'A2543' },
          { title: 'JHU_CONTROLLER', value: 'Excluded' },
          { title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
          { title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
        ],
      },
      {
        title: 'Product L',
        values: [
          { title: 'CUSTOM_FEATURE', value: 'Excluded' },
          { title: 'CUSTOM_FEATURE2', value: 'VALUE_2' },
          { title: 'DEFAULT_FEATURE', value: 'Included' },
          { title: 'ENGINE_5', value: 'A2543' },
          { title: 'JHU_CONTROLLER', value: 'Included' },
          { title: 'ROBOT_ARM_LIGHT', value: 'Included' },
          { title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
        ],
      },
    ]);
  });
  it('should have groups setup', () => {
    cy.validatePLConfigValues([
      {
        title: 'abGroup',
        values: [
          { title: 'CUSTOM_FEATURE', value: 'Included' },
          { title: 'CUSTOM_FEATURE2', value: 'VALUE_2,VALUE_3' },
          { title: 'DEFAULT_FEATURE', value: 'Included' },
          { title: 'ENGINE_5', value: 'A2543' },
          { title: 'JHU_CONTROLLER', value: 'Excluded' },
          { title: 'ROBOT_ARM_LIGHT', value: 'Excluded' },
          { title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
        ],
      },
      {
        title: 'GroupK',
        values: [
          { title: 'CUSTOM_FEATURE', value: 'Excluded' },
          { title: 'CUSTOM_FEATURE2', value: 'VALUE_3' },
          { title: 'DEFAULT_FEATURE', value: 'Included' },
          { title: 'ENGINE_5', value: 'C7543' },
          { title: 'JHU_CONTROLLER', value: 'Included' },
          { title: 'ROBOT_ARM_LIGHT', value: 'Included' },
          { title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
        ],
      },
      {
        title: 'NewGroup',
        values: [
          { title: 'CUSTOM_FEATURE', value: 'Excluded' },
          { title: 'CUSTOM_FEATURE2', value: 'VALUE_3' },
          { title: 'DEFAULT_FEATURE', value: 'Included' },
          { title: 'ENGINE_5', value: 'C7543' },
          { title: 'JHU_CONTROLLER', value: 'Included' },
          { title: 'ROBOT_ARM_LIGHT', value: 'Included' },
          { title: 'ROBOT_SPEAKER', value: 'SPKR_A' },
        ],
      },
    ]);
  });
});
