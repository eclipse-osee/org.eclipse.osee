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
	'verifyProductTypes',
	(
		values: {
			name: string;
			enabled: boolean;
		}[],
		includeFlags: boolean
	) => {
		values.forEach(({ name, enabled }) => {
			if (includeFlags) {
				cy.get(
					`[data-cy="field-product-type-${name}-${enabled}"]`
				).should('exist');
			} else if (enabled) {
				cy.get(`[data-cy="field-product-type-${name}"]`).should(
					'exist'
				);
			}
		});
		return cy.get('[data-cy="product-types-selection"]');
	}
);
Cypress.Commands.add(
	'editProductTypes',
	(
		wasValues: {
			name: string;
			enabled: boolean;
		}[],
		isValues: {
			name: string;
			enabled: boolean;
		}[]
	) => {
		if (isValues.length !== wasValues.length) {
			throw Error("Product Types Array Length Doesn't Match");
		}
		isValues.forEach(({ name, enabled }, index) => {
			if (name !== wasValues[index].name) {
				throw Error("Product Type Names Don't Match");
			}
			if (enabled !== wasValues[index].enabled) {
				// cy.get(
				//   `[data-cy="field-product-type-${name}-${wasValues[index].enabled}"]`
				// ).click();
				cy.selectProductTypes([
					{
						name: name,
						enabled: enabled,
						currentState: wasValues[index].enabled,
					},
				]);
			}
		});
		return cy.get('[data-cy="product-types-selection"]');
	}
);
Cypress.Commands.add(
	'selectProductTypes',
	(values: { name: string; enabled: boolean; currentState: boolean }[]) => {
		values.forEach((type) => {
			cy.get(
				`[data-cy="field-product-type-${type.name}-${type.currentState}"]`
			).click();
		});
	}
);
