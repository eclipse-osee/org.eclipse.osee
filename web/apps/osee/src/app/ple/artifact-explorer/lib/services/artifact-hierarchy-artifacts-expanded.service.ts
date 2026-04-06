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
import { Injectable, inject, linkedSignal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { UiService } from '@osee/shared/services';

@Injectable({
	providedIn: 'root',
})
export class ArtifactHierarchyArtifactsExpandedService {
	private uiService = inject(UiService);

	private _id = toSignal(this.uiService.id);

	private artifactsExpandedStructArray = linkedSignal<
		string | undefined,
		artifactsExpandedStruct[]
	>({
		source: this._id,
		computation: (updatedId, previous) => {
			if (previous === undefined) {
				return [];
			}
			if (updatedId === undefined) {
				return previous.value;
			}
			return [];
		},
	});

	setArtifactsExpandedStructArray(artifactArray: artifactsExpandedStruct[]) {
		this.artifactsExpandedStructArray.set([...artifactArray]);
	}

	expandArtifact(artifactId: string, childArtifactId: string) {
		const existingArtifact = this.artifactsExpandedStructArray().find(
			(artifact) => artifact.artifactId === artifactId
		);
		if (existingArtifact) {
			existingArtifact.childArtifactIds.push(childArtifactId);
		} else {
			this.artifactsExpandedStructArray.update((arts) => [
				...arts,
				{
					artifactId,
					childArtifactIds: [childArtifactId],
				},
			]);
		}
	}

	collapseArtifact(artifactId: string, childArtifactId: string) {
		const existingArtifact = this.artifactsExpandedStructArray().find(
			(artifact) => artifact.artifactId === artifactId
		);
		if (existingArtifact) {
			existingArtifact.childArtifactIds =
				existingArtifact.childArtifactIds.filter(
					(id) => id !== childArtifactId
				);
		}
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
