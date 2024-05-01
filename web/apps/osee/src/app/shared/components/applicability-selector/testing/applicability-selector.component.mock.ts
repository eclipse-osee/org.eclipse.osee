/*********************************************************************
 * Copyright (c) 2023 Boeing
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
import { Component, Input, Output } from '@angular/core';
import { applic } from '@osee/shared/types/applicability';
import { Subject } from 'rxjs';

@Component({
	selector: 'osee-applicability-selector',
	template: '<div>Dummy</div>',
	standalone: true,
})
export class MockApplicabilitySelectorComponent {
	@Input() applicability: applic | undefined = { id: '-1', name: '' };

	@Output() applicabilityChange = new Subject<applic>();

	@Input() count: number = 3;

	@Input() required: boolean = false;
}
