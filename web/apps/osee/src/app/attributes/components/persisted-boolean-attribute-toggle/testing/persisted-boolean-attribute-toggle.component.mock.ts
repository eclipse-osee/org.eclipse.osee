/*********************************************************************
 * Copyright (c) 2024 Boeing
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
import { Component, input, model } from '@angular/core';
import { applic } from '@osee/applicability/types';
import { ATTRIBUTETYPEID } from '@osee/attributes/constants';
import { attribute } from '@osee/attributes/types';

@Component({
	selector: 'osee-persisted-boolean-attribute-toggle',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class MockPersistedBooleanAttributeToggleComponent<
	U extends ATTRIBUTETYPEID,
> {
	artifactId = input.required<`${number}`>();
	artifactApplicability = input.required<applic>();

	comment = input('Modifying attribute');
	value = model.required<attribute<boolean, U>>();
	disabled = input(false);
}
