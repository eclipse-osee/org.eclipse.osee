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
import {
	combineLatest,
	of,
	Subject,
	switchMap,
	take,
	takeUntil,
	tap,
} from 'rxjs';
import { UiService } from '@osee/shared/services';
import { legacyArtifact, legacyCreateArtifact } from '@osee/transactions/types';
import { CreateCommandService } from './create-command.service';
import { CreateParameterService } from './create-parameter.service';

@Injectable({
	providedIn: 'root',
})
export class CreateCommandWithParameterArtifactService {
	private createCommandService = inject(CreateCommandService);
	private createParameterService = inject(CreateParameterService);
	private uiService = inject(UiService);

	done = new Subject();

	public set doneFx(val: unknown) {
		this.done.next(val);
	}

	public createCommandWithParameter(
		command: Partial<legacyCreateArtifact & legacyArtifact>,
		parameter: Partial<legacyCreateArtifact & legacyArtifact>
	) {
		return combineLatest([of(command), of(parameter)]).pipe(
			switchMap(([command, parameter]) =>
				this.createCommandService.createCommandArtifact(command).pipe(
					take(1),
					switchMap(() =>
						this.createParameterService.createParameterArtifact(
							parameter,
							this.createCommandService.commandArtId
						)
					),
					take(1),
					tap(() => (this.uiService.updated = true))
				)
			),
			takeUntil(this.done)
		);
	}
}
