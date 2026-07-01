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
import {
	Injectable,
	effect,
	inject,
	linkedSignal,
	signal,
} from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class ArtifactHierarchyArtifactsExpandedService {
	private uiService = inject(UiService);

	private _id = toSignal(this.uiService.id);
	private _viewId = toSignal(this.uiService.viewId);

	/** Resets expanded state on branch OR view change. */
	private _branchAndView = linkedSignal<string | undefined, string>({
		source: this._id,
		computation: () => '',
	});

	private _viewReset = linkedSignal<string | undefined, string>({
		source: this._viewId,
		computation: () => '',
	});

	private artifactsExpandedStructArray = signal<artifactsExpandedStruct[]>(
		[]
	);

	/** Effect to clear on branch change. */
	private _branchClearEffect = effect(() => {
		this._branchAndView();
		this.artifactsExpandedStructArray.set([]);
	});

	/** Effect to clear on view change. */
	private _viewClearEffect = effect(() => {
		this._viewReset();
		this.artifactsExpandedStructArray.set([]);
	});

	setArtifactsExpandedStructArray(artifactArray: artifactsExpandedStruct[]) {
		this.artifactsExpandedStructArray.set([...artifactArray]);
	}

	expandArtifact(artifactId: string, childArtifactId: string) {
		this.artifactsExpandedStructArray.update((arts) => {
			const existing = arts.find((a) => a.artifactId === artifactId);
			if (existing) {
				if (!existing.childArtifactIds.includes(childArtifactId)) {
					return arts.map((a) =>
						a.artifactId === artifactId
							? {
									...a,
									childArtifactIds: [
										...a.childArtifactIds,
										childArtifactId,
									],
								}
							: a
					);
				}
				return arts;
			}
			return [
				...arts,
				{ artifactId, childArtifactIds: [childArtifactId] },
			];
		});
	}

	collapseArtifact(artifactId: string, childArtifactId: string) {
		this.artifactsExpandedStructArray.update((arts) =>
			arts.map((a) =>
				a.artifactId === artifactId
					? {
							...a,
							childArtifactIds: a.childArtifactIds.filter(
								(id) => id !== childArtifactId
							),
						}
					: a
			)
		);
	}

	isExpanded(artifactId: string, childArtifactId: string): boolean {
		const existingArtifact = this.artifactsExpandedStructArray().find(
			(artifact) => artifact.artifactId === artifactId
		);
		if (existingArtifact) {
			return existingArtifact.childArtifactIds.includes(childArtifactId);
		}
		return false;
	}

	clear() {
		this.artifactsExpandedStructArray.set([]);
	}
}

type artifactsExpandedStruct = {
	artifactId: string;
	childArtifactIds: string[];
};
