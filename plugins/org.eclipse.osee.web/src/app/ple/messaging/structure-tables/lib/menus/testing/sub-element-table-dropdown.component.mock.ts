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
import { Component, Input } from '@angular/core';
import { applic } from '../../../../../../types/applicability/applic';
import { element } from '../../../../shared/types/element';
import { structure } from '../../../../shared/types/structure';

@Component({
	selector:
		'osee-sub-element-table-dropdown[element][structure][header][branchId][branchType][editMode]',
	template: '<p>Dummy</p>',
	standalone: true,
})
// eslint-disable-next-line @angular-eslint/component-class-suffix
export class MockSubElementTableComponent {
	@Input() element!: element;

	@Input() structure!: structure;

	@Input() header!: string;
	@Input() field?: string | number | boolean | applic;

	@Input('branchId') _branchId: string = '';
	@Input('branchType') _branchType: string = '';

	@Input() editMode: boolean = false;
}
