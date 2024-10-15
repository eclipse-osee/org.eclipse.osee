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
import { Injectable, inject } from '@angular/core';
import { ArtifactUiService } from '@osee/shared/services';
import { ARTIFACTTYPEIDENUM } from '@osee/shared/types/constants';
import { shareReplay } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class TeamWorkflowService {
	private artifactUiService = inject(ArtifactUiService);

	private _allTeamWorkflowAttributes = this.artifactUiService
		.getArtifactTypeAttributes(ARTIFACTTYPEIDENUM.TEAMWORKFLOW)
		.pipe(shareReplay({ bufferSize: 1, refCount: true }));

	get allTeamWorkflowAttributes() {
		return this._allTeamWorkflowAttributes;
	}
}
