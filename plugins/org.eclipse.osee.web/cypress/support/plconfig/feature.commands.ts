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
	'validateFeatureInsideDialog',
	(
		values: {
			title: string;
			description: string;
			valueType: string;
			multiValued: boolean;
			values: string[];
			defaultValue: string;
			productTypes: {
				name: string;
				enabled: boolean;
			}[];
		},
		includeFlags: boolean
	) => {
		cy.get(`[data-cy-value="field-name-${values.title}"]`)
			.should('exist')
			.get(`[data-cy-value="field-description-${values.description}"]`)
			.should('exist')
			.get(`[data-cy-value="field-valueType-${values.valueType}"]`)
			.should('exist')
			.get(`[data-cy-value="field-multiValued-${values.multiValued}"]`)
			.should('exist')
			.get(`[data-cy-value="field-defaultValue-${values.defaultValue}"]`)
			.should('exist');
		values.values.forEach((value, index) => {
			cy.get(`[data-cy="field-value-${index}"]`)
				.should('exist')
				.get(`[data-cy-value="field-value-${value}"]`)
				.should('exist');
		});
		return cy
			.verifyProductTypes(values.productTypes, includeFlags)
			.get(`[data-cy-value="field-name-${values.title}"]`);
	}
);
Cypress.Commands.add(
	'editFeature',
	(
		featureToEdit: string,
		previousValues: {
			title: string;
			description: string;
			valueType: string;
			multiValued: boolean;
			values: string[];
			defaultValue: string;
			productTypes: {
				name: string;
				enabled: boolean;
			}[];
		},
		newValues: {
			title: string;
			description: string;
			valueType: string;
			multiValued: boolean;
			values: string[];
			defaultValue: string;
			productTypes: {
				name: string;
				enabled: boolean;
			}[];
		}
	) => {
		cy.intercept('PUT', '/orcs/branch/*/applic/feature').as('feature');
		cy.intercept('/orcs/branches/*').as('branch');
		cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
		cy.intercept('/orcs/applicui/branch/*').as('applicui');
		cy.intercept('/ats/action/*').as('action');
		cy.intercept('/ats/teamwf/*').as('teamwf');
		cy.intercept('/ats/ple/action/*/approval').as('approval');
		cy.intercept('/ats/config/teamdef/*/leads').as('leads');
		cy.intercept('/orcs/types/productApplicability').as(
			'productApplicability'
		);
		cy.get(`[data-cy="feature-${featureToEdit}"]`).click();
		/**
		 * Validation steps
		 */
		cy.validateFeatureInsideDialog(previousValues, true);
		if (newValues.values.length < previousValues.values.length) {
			throw Error('Value Array Length is Shrinking');
		}
		cy.get('[data-cy="field-name"]')
			.focus()
			.clear()
			.type(newValues.title)
			.get('[data-cy="field-description"]')
			.focus()
			.clear()
			.type(newValues.description)
			.get('[data-cy="field-valueType"]')
			.click()
			.get(`[data-cy="option-${newValues.valueType}"]`)
			.click();
		if (newValues.values.length !== previousValues.values.length) {
			//click the add button until the disparity is = 0
			let i = newValues.values.length - previousValues.values.length;
			while (i > 0) {
				cy.get('[data-cy=add-value-btn]').click();
				i--;
			}
		}
		newValues.values.forEach((value, index) => {
			cy.get(`[data-cy="field-value-${index}"]`)
				.focus()
				.clear()
				.type(value);
		});
		cy.get('[data-cy="field-defaultValue"]')
			.click()
			.get(`[data-cy="option-${newValues.defaultValue}"]`)
			.click();
		return cy
			.editProductTypes(
				previousValues.productTypes,
				newValues.productTypes
			)
			.validateFeatureInsideDialog(newValues, true)
			.get('[data-cy="submit-btn"]')
			.click()
			.wait('@feature')
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
Cypress.Commands.add('openFeatureDropdown', () => {
	return cy.get('[data-cy="change-feature-dropdown-btn"]').click();
});
Cypress.Commands.add(
	'addFeature',
	(feature: {
		title: string;
		description: string;
		valueType?: string;
		multiValued?: boolean;
		values?: string[];
		defaultValue?: string;
		productTypes?: string[];
	}) => {
		cy.intercept('POST', '/orcs/branch/*/applic/feature').as('feature');
		cy.intercept('/orcs/branches/*').as('branch');
		cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
		cy.intercept('/orcs/applicui/branch/*').as('applicui');
		cy.intercept('/ats/action/*').as('action');
		cy.intercept('/ats/teamwf/*').as('teamwf');
		cy.intercept('/ats/ple/action/*/approval').as('approval');
		cy.intercept('/ats/config/teamdef/*/leads').as('leads');
		cy.intercept('/orcs/types/productApplicability').as(
			'productApplicability'
		);
		cy.openFeatureDropdown()
			.get('[data-cy="add-feature-btn"]')
			.click()
			.get('[data-cy="field-name"]')
			.focus()
			.clear()
			.type(feature.title)
			.get('[data-cy="field-description"]')
			.focus()
			.clear()
			.type(feature.description);
		if (feature.valueType) {
			cy.get('[data-cy="field-valueType"]')
				.click()
				.get(`[data-cy="option-${feature.valueType}"]`)
				.click();
		}
		if (feature.multiValued) {
			cy.get('[data-cy="field-multiValued"]').click();
		}
		if (feature.values) {
			if (feature.values.length > 2) {
				let i = feature.values.length - 2;
				while (i > 0) {
					cy.get('[data-cy=add-value-btn]').click();
					i--;
				}
			}
			feature.values.forEach((value, index) => {
				cy.get(`[data-cy="field-value-${index}"]`)
					.focus()
					.clear()
					.type(value);
			});
		}
		if (feature.defaultValue) {
			cy.get('[data-cy="field-defaultValue"]')
				.click()
				.get(`[data-cy="option-${feature.defaultValue}"]`)
				.click();
		}
		if (feature.productTypes) {
			feature.productTypes.forEach((productType) => {
				cy.get(
					`[data-cy="field-product-type-${productType}-false"]`
				).click();
			});
		}
		return cy
			.get('[data-cy="submit-btn"]')
			.click()
			.wait('@feature')
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
Cypress.Commands.add('deleteFeature', (feature: string) => {
	cy.intercept('DELETE', '/orcs/branch/*/applic/feature/*').as('feature');
	cy.intercept('/orcs/branches/*').as('branch');
	cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
	cy.intercept('/orcs/applicui/branch/*').as('applicui');
	cy.intercept('/ats/action/*').as('action');
	cy.intercept('/ats/teamwf/*').as('teamwf');
	cy.intercept('/ats/ple/action/*/approval').as('approval');
	cy.intercept('/ats/config/teamdef/*/leads').as('leads');
	cy.intercept('/orcs/types/productApplicability').as('productApplicability');
	cy.openFeatureDropdown()
		.get('[data-cy="delete-feature-btn"]')
		.click()
		.get(`[data-cy="delete-feature-${feature}-btn"]`)
		.click()
		.wait('@feature')
		.wait('@branch')
		.wait('@cfggroup')
		.wait('@applicui')
		.wait('@action')
		.wait('@teamwf')
		.wait('@approval')
		.wait('@leads')
		.get('mat-progress-bar')
		.should('not.exist');
});
Cypress.Commands.add('validateFeatureExists', (feature: string) => {
	cy.get(`[data-cy="feature-${feature}"]`).scrollIntoView().should('exist');
});
Cypress.Commands.add('validateFeatureDoesNotExist', (feature: string) => {
	cy.get(`[data-cy="feature-${feature}"]`).should('not.exist');
});
