/*********************************************************************
 * Copyright (c) 2021 Boeing
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
import { HighlightFilteredTextDirective } from '@osee/shared/utils';
import { applic } from '@osee/applicability/types';

@Component({
	selector: 'osee-messaging-sub-element-table-no-edit-field-filtered',
	templateUrl: './sub-element-table-no-edit-field-filtered.component.html',
	styles: [],
	imports: [HighlightFilteredTextDirective],
})
export class SubElementTableNoEditFieldFilteredComponent {
	@Input() field: string | number | boolean | applic | undefined = '';
	@Input() filter = '';
}
