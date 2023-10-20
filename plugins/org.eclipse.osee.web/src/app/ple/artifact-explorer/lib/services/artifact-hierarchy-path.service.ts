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
	BehaviorSubject,
	combineLatest,
	debounceTime,
	filter,
	merge,
	of,
	repeat,
	shareReplay,
	switchMap,
} from 'rxjs';
import { UiService } from '@osee/shared/services';
import { ArtifactExplorerHttpService } from './artifact-explorer-http.service';

@Injectable({
	providedIn: 'root',
})
export class ArtifactHierarchyPathService {
	selectedArtifactId = new BehaviorSubject<string>('');
	branchId = this.uiService.id;
	viewId = this.uiService.viewId;

	private paths = combineLatest([
		this.branchId,
		this.viewId,
		this.selectedArtifactId,
	]).pipe(
		filter(
			([branchId, viewId, artId]) =>
				branchId != '-1' &&
				branchId != '0' &&
				branchId != '' &&
				viewId != '' &&
				artId != ''
		),
		debounceTime(500),
		switchMap(([branchId, viewId, artId]) =>
			this.artExpHttpService
				.getPathToArtifact(branchId, artId, viewId)
				.pipe(repeat({ delay: () => this.uiService.update }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	constructor(
		private artExpHttpService: ArtifactExplorerHttpService,
		private uiService: UiService
	) {}

	getPaths() {
		return this.paths;
	}

	initializePaths(artifactId: string) {
		this.selectedArtifactId.next(artifactId);
	}
}
