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
	'validatePLConfigValues',
	(
		columns: { title: string; values: { title: string; value: string }[] }[]
	) => {
		columns.forEach((column) => {
			column.values.forEach((value) => {
				cy.get(
					`[data-cy="value-${value.value}-${column.title}-${value.title}"]`
				)
					.scrollIntoView()
					.should('exist');
			});
		});
	}
);
Cypress.Commands.add(
	'changeApplicability',
	(
		feature: string,
		featureIsMultiSelect: boolean,
		configOrGroup: string,
		wasValue: string | string[],
		isValue: string | string[]
	) => {
		cy.intercept('PUT', '/orcs/branch/*/applic/view/*/applic').as(
			'applicChange'
		);
		cy.intercept('/orcs/branches/*').as('branch');
		cy.intercept('/orcs/branch/*/applic/cfggroup').as('cfggroup');
		cy.intercept('/orcs/applicui/branch/*').as('applicui');
		cy.intercept('/ats/action/*').as('action');
		cy.intercept('/ats/teamwf/*').as('teamwf');
		cy.intercept('/ats/ple/action/*/approval').as('approval');
		cy.intercept('/ats/config/teamdef/*/leads').as('leads');
		cy.intercept('/orcs/branch/*/applic/product-types').as(
			'productApplicability'
		);
		cy.get(
			`[data-cy="value-${wasValue.toString()}-${configOrGroup}-${feature}"]`
		)
			.scrollIntoView()
			.should('be.visible')
			.click({ force: true });
		if (
			Array.isArray(isValue) &&
			Array.isArray(wasValue) &&
			featureIsMultiSelect
		) {
			//disable previous values that are not included in new list
			wasValue.forEach((previousValue) => {
				if (isValue.includes(previousValue)) {
					//do nothing
				} else {
					cy.get(`[data-cy="option-${previousValue}-true"]`).click();
				}
			});
			isValue.forEach((value) => {
				if (wasValue.includes(value)) {
					//do nothing
				} else {
					cy.get(`[data-cy="option-${value}-false"]`).click();
				}
			});
		} else if (
			!Array.isArray(isValue) &&
			!Array.isArray(wasValue) &&
			!featureIsMultiSelect
		) {
			cy.get(`[data-cy="option-${isValue}"]`).click();
		} else {
			throw Error('types do not match feature');
		}
		return cy
			.wait('@applicChange')
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
