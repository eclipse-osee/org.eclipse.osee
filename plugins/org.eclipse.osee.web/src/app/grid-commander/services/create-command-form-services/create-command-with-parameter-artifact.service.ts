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
import { Injectable } from '@angular/core';
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
import { createArtifact } from '@osee/shared/types';
import { CreateCommandService } from './create-command.service';
import { CreateParameterService } from './create-parameter.service';

@Injectable({
	providedIn: 'root',
})
export class CreateCommandWithParameterArtifactService {
	done = new Subject();

	public set doneFx(val: unknown) {
		this.done.next(val);
	}

	constructor(
		private createCommandService: CreateCommandService,
		private createParameterService: CreateParameterService,
		private uiService: UiService
	) {}

	public createCommandWithParameter(
		command: Partial<createArtifact>,
		parameter: Partial<createArtifact>
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
