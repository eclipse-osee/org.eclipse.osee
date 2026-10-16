/*********************************************************************
 * Copyright (c) 2026 Boeing
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
import { inject, Injectable } from '@angular/core';
import { UiService } from '@osee/shared/services';
import { toSignal } from '@angular/core/rxjs-interop';
import { catchError, forkJoin, map, Observable, of, take } from 'rxjs';
import { ArtifactExplorerHttpService } from './artifact-explorer-http.service';
import { ArtifactExplorerTabService } from './artifact-explorer-tab.service';

/**
 * Resolves `<artifact-link>` references for the artifact explorer: looks up display
 * names for the markdown preview and opens the linked artifact in a tab when a link
 * is clicked. Keeps the shared markdown editor decoupled from the explorer's tab
 * model (the editor emits an id; this service turns it into a tab).
 */
@Injectable({
	providedIn: 'root',
})
export class ArtifactLinkService {
	private artExpHttpService = inject(ArtifactExplorerHttpService);
	private tabService = inject(ArtifactExplorerTabService);
	private uiService = inject(UiService);

	private readonly branchId = toSignal(this.uiService.id, {
		initialValue: '',
	});
	private readonly viewId = toSignal(this.uiService.viewId, {
		initialValue: '',
	});
	private readonly branchType = toSignal(this.uiService.type, {
		initialValue: '',
	});

	/**
	 * Fetches display names for the given artifact ids on a branch. Missing or
	 * errored lookups are simply omitted from the map so the caller can fall back
	 * to the id. Returns an empty map when there is nothing to resolve.
	 */
	getArtifactNames(
		artifactIds: string[],
		branchId?: string,
		viewId?: string
	): Observable<Map<string, string>> {
		const branch = branchId || this.branchId();
		const view = viewId || this.viewId();
		if (artifactIds.length === 0 || !branch) {
			return of(new Map<string, string>());
		}
		return forkJoin(
			artifactIds.map((id) =>
				this.artExpHttpService
					.getartifactWithRelations(branch, id, view, false)
					.pipe(
						map((art) => ({ id, name: art.name })),
						catchError(() => of(null))
					)
			)
		).pipe(
			map((results) => {
				const names = new Map<string, string>();
				for (const result of results) {
					if (result) {
						names.set(result.id, result.name);
					}
				}
				return names;
			})
		);
	}

	/**
	 * Opens the linked artifact in a new tab. Resolves on the branch of the artifact
	 * that holds the link (passed by the caller), matching desktop behavior where a
	 * link always targets the same branch as the containing artifact. Falls back to
	 * the current UI branch when no branch is supplied. Fetches the full artifact
	 * (with relations) first so the tab has everything the editor needs.
	 */
	openArtifact(artifactId: string, branchId?: string, viewId?: string): void {
		const branch = branchId || this.branchId();
		const view = viewId || this.viewId();
		if (!branch) {
			return;
		}
		this.artExpHttpService
			.getartifactWithRelations(branch, artifactId, view, true)
			.pipe(take(1))
			.subscribe({
				next: (artifact) =>
					// Derive editability from branch type, matching the hierarchy/search
					// open paths. The raw endpoint payload's `editable` is not branch-aware,
					// so passing it through leaves every attribute editor disabled on a
					// working branch. Open on the link's own branch, not necessarily the
					// current UI branch.
					this.tabService.addArtifactTabOnBranch(
						{
							...artifact,
							editable: this.branchType() !== 'baseline',
						},
						branch,
						view
					),
				error: (err) => {
					this.uiService.ErrorText = `Failed to open linked artifact ${artifactId}: ${err?.message ?? 'Unknown error'}`;
				},
			});
	}
}
