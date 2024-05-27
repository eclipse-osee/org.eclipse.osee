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
import { Injectable, WritableSignal, signal, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UiService } from '@osee/shared/services';
import { map } from 'rxjs';

@Injectable({
	providedIn: 'root',
})
export class ArtifactHierarchyArtifactsExpandedService {
	private uiService = inject(UiService);

	private artifactsExpandedStructArray: WritableSignal<
		artifactsExpandedStruct[]
	> = signal([]);

	/** Inserted by Angular inject() migration for backwards compatibility */
	constructor(...args: unknown[]);

	constructor() {
		// Clearing the artifactsExpandedStructArray when the branch id changes
		this.uiService.id
			.pipe(
				map(() => this.clear()),
				takeUntilDestroyed()
			)
			.subscribe();
	}

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
