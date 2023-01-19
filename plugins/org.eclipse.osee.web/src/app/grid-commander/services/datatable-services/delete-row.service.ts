/*********************************************************************
 * Copyright (c) 2022 Boeing
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
import { of, take, switchMap, tap } from 'rxjs';
import { UiService } from 'src/app/ple-services/ui/ui.service';
import { RowObj } from '../../types/grid-commander-types/table-data-types';
import { ExecutedCommandsArtifactService } from '../data-services/executed-commands-artifact.service';
import { GCBranchIdService } from '../fetch-data-services/gc-branch-id.service';

@Injectable({
	providedIn: 'root',
})
export class DeleteRowService {
	constructor(
		private executedCommandsArtifactService: ExecutedCommandsArtifactService,
		private uiService: UiService,
		private branchIdService: GCBranchIdService
	) {}

	createModifiedObjectToDelete(rowObj: RowObj) {
		return of(rowObj['Artifact Id'].toString());
	}

	deleteArtifactFromDataTable(artifactIdToDelete: string) {
		return of(artifactIdToDelete).pipe(
			take(1),
			switchMap((artIdToDelete) =>
				this.executedCommandsArtifactService
					.deleteExistingCommandArtifact(
						this.branchIdService.branchId,
						artIdToDelete
					)
					.pipe(
						take(1),
						tap(() => (this.uiService.updated = true))
					)
			)
		);
	}
}
