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
import { Injectable, inject } from '@angular/core';
import { of, take, switchMap, tap } from 'rxjs';
import { UiService } from '@osee/shared/services';
import { RowObj } from '../../../types/grid-commander-types/table-data-types';
import { ExecutedCommandsArtifactService } from '../../data-services/execution-services/executed-commands-artifact.service';
import { GCBranchIdService } from '../../fetch-data-services/branch/gc-branch-id.service';

@Injectable({
	providedIn: 'root',
})
export class DeleteRowService {
	private executedCommandsArtifactService = inject(
		ExecutedCommandsArtifactService
	);
	private uiService = inject(UiService);
	private branchIdService = inject(GCBranchIdService);

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
