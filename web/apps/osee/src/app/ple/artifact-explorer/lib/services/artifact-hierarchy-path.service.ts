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
	BehaviorSubject,
	combineLatest,
	filter,
	map,
	repeat,
	shareReplay,
	switchMap,
} from 'rxjs';
import { UiService } from '@osee/shared/services';
import { ArtifactExplorerHttpService } from './artifact-explorer-http.service';
import { ArtifactHierarchyArtifactsExpandedService } from './artifact-hierarchy-artifacts-expanded.service';

@Injectable({
	providedIn: 'root',
})
export class ArtifactHierarchyPathService {
	private artExpHttpService = inject(ArtifactExplorerHttpService);
	private uiService = inject(UiService);
	private artifactsExpandedService = inject(
		ArtifactHierarchyArtifactsExpandedService
	);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
		// Clearing the selectedArtifactId when the branch id / view id changes
		combineLatest([this.uiService.id, this.uiService.viewId])
			.pipe(
				map(([_branchId, _viewId]) => {
					this.selectedArtifactId.next('');
				})
			)
			.subscribe();
	}

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
		switchMap(([branchId, viewId, artId]) =>
			this.artExpHttpService
				.getPathToArtifact(branchId, artId, viewId)
				.pipe(repeat({ delay: () => this.uiService.update }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	getPaths() {
		return this.paths;
	}

	updatePaths(artifactId: string) {
		this.artifactsExpandedService.clear();
		this.selectedArtifactId.next(artifactId);
	}
}
