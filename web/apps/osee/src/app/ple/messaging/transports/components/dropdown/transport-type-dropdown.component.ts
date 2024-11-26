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
import { Component, computed, inject, model, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatOption, MatSelect } from '@angular/material/select';
import { transportType } from '@osee/messaging/shared/types';
import { MatOptionLoadingComponent } from '@osee/shared/components';
import { provideOptionalControlContainerNgForm } from '@osee/shared/utils';
import { HasValidIdDirective } from '@osee/shared/validators';
import { CurrentTransportTypeService } from './current-transport-type.service';

let nextUniqueId = 0;

@Component({
	selector: 'osee-transport-type-dropdown',
	imports: [
		MatFormField,
		MatLabel,
		MatSelect,
		FormsModule,
		MatOptionLoadingComponent,
		MatOption,
		HasValidIdDirective,
	],
	template: ` <mat-form-field
		id="transport-type-selector"
		class="tw-w-full">
		<mat-label>Select a Transport Type</mat-label>
		<mat-select
			[(ngModel)]="transportType"
			required
			oseeHasValidId
			[id]="formId()"
			[name]="formId()"
			data-cy="field-transport-type"
			[compareWith]="compareTransportTypes">
			<osee-mat-option-loading
				[data]="transportTypes"
				paginationMode="AUTO"
				[paginationSize]="paginationSize"
				objectName="Transport Types">
				<ng-template let-option>
					<mat-option
						[value]="option"
						[attr.data-cy]="'option-' + option.name"
						[id]="option.id">
						{{ option.name.value }}
					</mat-option>
				</ng-template>
			</osee-mat-option-loading>
		</mat-select>
	</mat-form-field>`,
	viewProviders: [provideOptionalControlContainerNgForm()],
})
export class TransportTypeDropdownComponent {
	protected _componentId = signal(`${nextUniqueId++}`);
	public formId = computed(() => {
		return 'transport-type-' + this._componentId();
	});
	paginationSize = 50;
	transportType = model.required<transportType>();

	private transportTypeService = inject(CurrentTransportTypeService);
	transportTypes = (pageNum: string | number) =>
		this.transportTypeService.getPaginatedTypes(
			pageNum,
			this.paginationSize
		);
	compareTransportTypes(o1: transportType, o2: transportType) {
		return o1?.id === o2?.id && o1.name.value === o2.name.value;
	}
}
