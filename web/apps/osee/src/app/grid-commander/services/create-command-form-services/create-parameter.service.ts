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
	debounceTime,
	of,
	Subject,
	switchMap,
	take,
	takeUntil,
	tap,
} from 'rxjs';
import { UiService } from '@osee/shared/services';
import { legacyArtifact, legacyCreateArtifact } from '@osee/transactions/types';
import { CreateParameterAndRelationsService } from '../create-command-artifact-and-relations/create-parameter-and-relations.service';
import { CommandGroupOptionsService } from '../data-services/commands/command-group-options.service';
import { GCBranchIdService } from '../fetch-data-services/branch/gc-branch-id.service';

@Injectable({
	providedIn: 'root',
})
export class CreateParameterService {
	private commandGroupOptionsService = inject(CommandGroupOptionsService);
	private createParameterAndRelationsService = inject(
		CreateParameterAndRelationsService
	);
	private branchIdService = inject(GCBranchIdService);
	private uiService = inject(UiService);

	private _commandArtId = '';
	done = new Subject();

	public set doneFx(val: unknown) {
		this.done.next(val);
	}

	public createParameterArtifact(
		parameter: Partial<legacyCreateArtifact & legacyArtifact>,
		commandId: string
	) {
		return combineLatest([of(parameter), of(commandId)]).pipe(
			debounceTime(750),
			take(1),
			switchMap(([parameter, _commandId]) =>
				this.createParameterAndRelationsService
					.createParameterAndEstablishCommandRelation(
						this.branchIdService.branchId,
						parameter,
						this._commandArtId
					)
					.pipe(
						take(1),
						tap(() => (this.uiService.updated = true)),
						tap(() => this.clearFilterOnSubmit())
					)
			),
			takeUntil(this.done)
		);
	}

	clearFilterOnSubmit() {
		this.commandGroupOptionsService.stringToFilterCommandsBy = '';
	}

	public get commandArtId(): string {
		return this._commandArtId;
	}
	public set commandArtId(value: string) {
		this._commandArtId = value;
	}
}
