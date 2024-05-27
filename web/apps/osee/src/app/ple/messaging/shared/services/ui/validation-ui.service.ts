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
import { Injectable, inject } from '@angular/core';
import { UiService } from '@osee/shared/services';
import { filter, switchMap, take } from 'rxjs';
import { ValidationService } from '../http/validation.service';

@Injectable({
	providedIn: 'root',
})
export class ValidationUiService {
	private ui = inject(UiService);
	private validationService = inject(ValidationService);

	validateConnection(connectionId: string, viewId: string) {
		return this.ui.id.pipe(
			take(1),
			filter((branchId) => branchId !== '' && branchId !== '-1'),
			switchMap((branchId) =>
				this.validationService.getConnectionValidation(
					branchId,
					connectionId,
					viewId
				)
			)
		);
	}
}
