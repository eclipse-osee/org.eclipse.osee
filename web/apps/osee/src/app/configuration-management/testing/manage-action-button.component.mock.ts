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
import { Component, input } from '@angular/core';
import { branch } from '@osee/shared/types';

@Component({
	selector: 'osee-manage-action-button',
	template: '<div>Dummy</div>',
	standalone: true,
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class ManageActionButtonComponentMock {
	label = input<string>('Transition');
	branch = input.required<branch>();
	isDisabled = input(false);
}
