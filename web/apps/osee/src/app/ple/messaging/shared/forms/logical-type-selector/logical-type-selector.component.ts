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
import { TitleCasePipe } from '@angular/common';
import { Component, model, signal, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { TypesService } from '@osee/messaging/shared/services';
import type { logicalType } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { HasValidIdDirective } from '@osee/shared/validators';
let nextUniqueId = 0;
@Component({
	selector: 'osee-logical-type-dropdown',
	standalone: true,
	imports: [
		TitleCasePipe,
		FormsModule,
		MatFormField,
		MatLabel,
		MatSelect,
		MatOption,
		MatOptionLoadingComponent,
		HasValidIdDirective,
	],
	template: `
		<mat-form-field
			class="tw-w-full"
			required>
			<mat-label>Logical Type</mat-label>
			<mat-select
				[(ngModel)]="type"
				data-testid="logical-type-selector"
				[name]="'logicalType_' + _componentId()"
				required
				oseeHasValidId
				[compareWith]="compareIds">
				<osee-mat-option-loading
					[data]="logicalTypes"
					objectName="Logical Types">
					<ng-template let-option>
						<mat-option
							[value]="option"
							[attr.data-cy]="'logical-type-' + option.name">
							{{ option.name | titlecase }}
						</mat-option>
					</ng-template>
				</osee-mat-option-loading>
			</mat-select>
		</mat-form-field>
	`,
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class LogicalTypeSelectorComponent {
	private typesService = inject(TypesService);

	protected _componentId = signal(`${nextUniqueId++}`);

	type = model.required<logicalType>();
	logicalTypes = this.typesService.logicalTypes;

	setType(value: logicalType) {
		this.type.set(value);
	}

	compareIds<T extends { id: string }>(a: T, b: T) {
		if (a == null || b == null) {
			return false;
		}
		return a.id === b.id;
	}
}
