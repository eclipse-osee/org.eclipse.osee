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
describe('PLConfig - Configurations(Edit Mode)', () => {
	before(() => {
		cy.visit('/ple').get('[data-cy="plconfig-nav-button"]').click();
		cy.task<Cypress.NameResult>('getLatestBranchName').then(
			(branchname) => {
				const branch: string = branchname.name;
				return cy.selectBranch(branch, 'working');
			}
		);
	});
	const productTypes = [
		{
			name: 'Code',
			description: 'Description of Code',
		},
		{
			name: 'Continuous_Integration',
			description: 'Description of CI',
		},
		{
			name: 'Documentation',
			description: 'Description of Documentation',
		},
		{
			name: 'Requirements',
			description: 'Requirements',
		},
		{
			name: 'Test',
			description: 'Description of Test',
		},
		{
			name: 'Unspecified',
			description: 'Unspecified',
		},
		{
			name: 'Misc',
			description: 'Misc',
			editedDescription: 'Edited Descripion of Misc',
			editedName: 'Miscellaneous',
			shouldBeDeleted: true,
		},
	];
	productTypes.forEach((type) => {
		it(`should make a product type named ${type.name}`, () => {
			cy.intercept('POST', '/orcs/branch/*/applic/product-types').as(
				'createProductType'
			);
			cy.get('[data-cy=change-product-type-dropdown-btn]')
				.click()
				.get('[data-cy=add-product-type-btn]')
				.click()
				.get('[data-cy=input-name]')
				.focus()
				.type(type.name)
				.get('[data-cy=input-description]')
				.focus()
				.type(type.description)
				.get('[data-cy=submit-btn]')
				.click()
				.get('[data-cy=add-product-type-dialog]')
				.should('not.exist')
				.wait('@createProductType');
		});
		describe(`Should edit a product type named ${type.name}`, () => {
			before(() => {
				cy.intercept('GET', '/orcs/branch/*/applic/product-types').as(
					'getProductTypes'
				);
				cy.get('[data-cy=change-product-type-dropdown-btn]')
					.click()
					.get('[data-cy="edit-product-type-btn"]')
					.click()
					.wait('@getProductTypes')
					.wait(100)
					.get('[data-cy=edit-product-type-' + type.name + '-btn]')
					.click()
					.get('[data-cy=edit-product-type-dialog]')
					.should('exist');
			});
			if (type.editedDescription || type.editedName) {
				if (type.editedName) {
					it('should edit the name', () => {
						cy.get('[data-cy=input-name]')
							.focus()
							.clear()
							.type(type.editedName);
					});
				}

				if (type.editedDescription) {
					it('should edit the description', () => {
						cy.get('[data-cy=input-description]')
							.focus()
							.clear()
							.type(type.editedDescription);
					});
				}
				it('should submit the change', () => {
					cy.intercept(
						'PUT',
						'/orcs/branch/*/applic/product-types'
					).as('modifyProductTypes');
					cy.get('[data-cy=submit-btn]')
						.click()
						.get('[data-cy=edit-product-type-dialog]')
						.should('not.exist')
						.wait('@modifyProductTypes');
				});
			} else {
				it('should close the dialog', () => {
					cy.get('[data-cy=cancel-btn]')
						.click()
						.get('[data-cy=edit-product-type-dialog]')
						.should('not.exist');
				});
			}
		});

		if (type.shouldBeDeleted) {
			describe('Deleting a Product Type', () => {
				it('should delete a product type', () => {
					cy.intercept(
						'GET',
						'/orcs/branch/*/applic/product-types'
					).as('getProductTypes');
					if (type.editedName) {
						cy.get('[data-cy=change-product-type-dropdown-btn]')
							.click()
							.get('[data-cy="delete-product-type-btn"]')
							.click()
							.wait('@getProductTypes')
							.wait(100)
							.get(
								'[data-cy=delete-product-type-' +
									type.editedName +
									'-btn]'
							)
							.click();
					} else {
						cy.get('[data-cy=change-product-type-dropdown-btn]')
							.click()
							.get('[data-cy="delete-product-type-btn"]')
							.click()
							.wait('@getProductTypes')
							.wait(100)
							.get(
								'[data-cy=delete-product-type-' +
									type.name +
									'-btn]'
							)
							.click();
					}
				});
			});
		}
	});
});
