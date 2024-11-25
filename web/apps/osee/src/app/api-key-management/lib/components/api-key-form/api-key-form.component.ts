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
import {
	ChangeDetectionStrategy,
	Component,
	inject,
	computed,
	signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MatCheckbox } from '@angular/material/checkbox';
import { MatButton } from '@angular/material/button';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { MatDialogRef, MatDialogClose } from '@angular/material/dialog';
import { ApiKey, keyScope } from '../../types/apiKey';
import { HasValidDateRangeDirective } from '../../validators/date-range.directive';
import { writableSlice } from '@osee/shared/utils';
import { ApiKeyService } from '../../services/api-key.service';
import { take, tap } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';

@Component({
	selector: 'osee-api-key-form',
	imports: [
		FormsModule,
		MatCheckbox,
		MatButton,
		MatError,
		MatFormField,
		MatLabel,
		MatInput,
		HasValidDateRangeDirective,
		MatDialogClose,
	],
	changeDetection: ChangeDetectionStrategy.OnPush,
	templateUrl: './api-key-form.component.html',
})
export class ApiKeyFormComponent {
	apiKeyService = inject(ApiKeyService);
	dialogRef = inject(MatDialogRef<ApiKeyFormComponent>);

	minDate: string;
	maxDate: string;
	currentDate: Date;
	nextYearDate: Date;

	apiKey = signal(new ApiKey('', [], '', ''));
	keyName = writableSlice(this.apiKey, 'name');
	keyExpirationDate = writableSlice(this.apiKey, 'expirationDate');
	keyScopes = writableSlice(this.apiKey, 'scopes');

	valueToReturn = computed(() => {
		return {
			...this.apiKey(),
			creationDate: new Date().toISOString().split('T')[0],
		};
	});

	constructor() {
		toSignal(
			this.apiKeyService.getApiScopes().pipe(
				take(1),
				tap((returnedKeyScopes: keyScope[]) => {
					this.keyScopes.set(returnedKeyScopes);
				})
			)
		);

		this.currentDate = new Date();
		this.nextYearDate = new Date();
		this.nextYearDate.setFullYear(this.nextYearDate.getFullYear() + 1);

		this.minDate = this.currentDate.toISOString().split('T')[0];
		this.maxDate = this.nextYearDate.toISOString().split('T')[0];
	}

	closeForm() {
		this.dialogRef.close();
	}
}
