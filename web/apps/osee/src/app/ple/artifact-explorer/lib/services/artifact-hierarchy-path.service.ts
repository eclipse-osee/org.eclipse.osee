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
import { Injectable, computed, inject, linkedSignal } from '@angular/core';
import { combineLatest, filter, repeat, shareReplay, switchMap } from 'rxjs';
import { UiService } from '@osee/shared/services';
import { ArtifactExplorerHttpService } from './artifact-explorer-http.service';
import { ArtifactHierarchyArtifactsExpandedService } from './artifact-hierarchy-artifacts-expanded.service';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';

@Injectable({
	providedIn: 'root',
})
export class ArtifactHierarchyPathService {
	private artExpHttpService = inject(ArtifactExplorerHttpService);
	private uiService = inject(UiService);
	private artifactsExpandedService = inject(
		ArtifactHierarchyArtifactsExpandedService
	);

	private _id = toSignal(this.uiService.id);
	private _viewId = toSignal(this.uiService.viewId);
	private _branchAndView = computed(() => {
		const id = this._id();
		const _view = this._viewId();
		if (!id && !_view) {
			return undefined;
		}
		if (!_view && id) {
			return { branch: id };
		}
		if (!id && _view) {
			return { view: _view };
		}
		return { branch: id, view: _view };
	});
	private _selectedArtifactId = linkedSignal<
		| {
				branch: string;
				view?: undefined;
		  }
		| {
				view: string;
				branch?: undefined;
		  }
		| {
				branch: string | undefined;
				view: string | undefined;
		  }
		| undefined,
		string
	>({
		source: this._branchAndView,
		computation: (_branchAndView, previous) => {
			if (previous === undefined) {
				return '';
			}
			return '';
		},
	});
	selectedArtifactId = toObservable(this._selectedArtifactId);

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
		this._selectedArtifactId.set(artifactId);
	}
}
