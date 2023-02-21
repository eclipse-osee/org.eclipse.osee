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
import { createArtifact } from '@osee/shared/types';
import {
	combineLatest,
	of,
	Subject,
	switchMap,
	take,
	takeUntil,
	tap,
} from 'rxjs';
import { UiService } from '../../../ple-services/ui/ui.service';
import { CreateCommandandAndRelationsService } from '../create-command-artifact-and-relations/create-command-and-relations.service';
import { GCBranchIdService } from '../fetch-data-services/branch/gc-branch-id.service';
import { ContextSelectionService } from './context-selection.service';

@Injectable({
	providedIn: 'root',
})
export class CreateCommandService {
	contextData = this.contextSelectionService.contextDetails;

	private _commandArtId: string = '';

	done = new Subject();

	public set doneFx(val: unknown) {
		this.done.next(val);
	}
	constructor(
		private branchIdService: GCBranchIdService,
		private createCommandAndRelationsService: CreateCommandandAndRelationsService,
		private contextSelectionService: ContextSelectionService,
		private uiService: UiService
	) {}

	public createCommandArtifact(commandObject: Partial<createArtifact>) {
		return combineLatest([of(commandObject), this.contextData]).pipe(
			take(1),
			switchMap(([commandObject, contextData]) =>
				this.createCommandAndRelationsService
					.createCommandAndEstablishContextRelation(
						this.branchIdService.branchId,
						commandObject,
						contextData
					)
					.pipe(
						take(1),
						tap(() => (this.uiService.updated = true))
					)
			),
			takeUntil(this.done)
		);
	}

	public get commandArtId(): string {
		return this._commandArtId;
	}
	public set commandArtId(value: string) {
		this._commandArtId = value;
	}
}
