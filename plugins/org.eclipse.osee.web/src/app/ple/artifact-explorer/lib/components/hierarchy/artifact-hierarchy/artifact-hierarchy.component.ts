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
import {
	Component,
	Input,
	OnChanges,
	SimpleChanges,
	signal,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import {
	AnimatedExpandButtonComponent,
	BranchPickerComponent,
} from '@osee/shared/components';
import { MatExpansionModule } from '@angular/material/expansion';
import {
	BehaviorSubject,
	combineLatest,
	filter,
	map,
	of,
	repeat,
	shareReplay,
	switchMap,
} from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { UiService } from '@osee/shared/services';
import { MatIconModule } from '@angular/material/icon';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatButtonModule } from '@angular/material/button';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import {
	artifact,
	artifactTypeIcon,
} from '../../../types/artifact-explorer.data';
import { ArtifactHierarchyRelationsComponent } from '../artifact-hierarchy-relations/artifact-hierarchy-relations.component';

@Component({
	selector: 'osee-artifact-hierarchy',
	standalone: true,
	imports: [
		CommonModule,
		BranchPickerComponent,
		MatExpansionModule,
		MatIconModule,
		DragDropModule,
		MatButtonModule,
		ArtifactHierarchyRelationsComponent,
		AnimatedExpandButtonComponent,
	],
	templateUrl: './artifact-hierarchy.component.html',
})
export class ArtifactHierarchyComponent implements OnChanges {
	@Input() artifactId!: `${number}`;
	private _artifactId = new BehaviorSubject<string>('');

	branchId$ = this.uiService.id;
	branchType$ = this.uiService.type;
	viewId$ = this.uiService.viewId;

	protected artifactsOpen = signal<string[]>([]);
	addArtifactsOpen(value: string) {
		this.artifactsOpen.update((rows) => [...rows, value]);
	}
	removeArtifactsOpen(value: string) {
		this.artifactsOpen.update((rows) => rows.filter((v) => v !== value));
	}

	trackById(index: number, item: artifact) {
		return item.id;
	}

	constructor(
		private artExpHttpService: ArtifactExplorerHttpService,
		private uiService: UiService,
		private tabService: ArtifactExplorerTabService
	) {}

	ngOnChanges(changes: SimpleChanges): void {
		if (
			changes.artifactId !== undefined &&
			changes.artifactId.previousValue !==
				changes.artifactId.currentValue &&
			changes.artifactId.currentValue !== undefined
		) {
			this._artifactId.next(changes.artifactId.currentValue);
		}
	}

	// grabbing artifact with direct relations
	artifactWithDirRelation$ = combineLatest([
		this.branchId$,
		this.viewId$,
		this._artifactId,
	]).pipe(
		filter(
			([branch, view, artifact]) =>
				branch != '-1' &&
				branch != '0' &&
				branch != '' &&
				artifact != '' &&
				view != ''
		),
		switchMap(([branch, view, artifact]) =>
			this.artExpHttpService
				.getDirectRelations(branch, artifact, view)
				.pipe(repeat({ delay: () => this.uiService.update }))
		),
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);

	// grabbing only relations that are populated
	relation$ = this.artifactWithDirRelation$.pipe(
		map((response) =>
			response.relations.filter(
				(relation) =>
					relation.relationSides.some(
						(side) => side.artifacts.length > 0
					) &&
					relation.relationTypeToken.name !== 'Default Hierarchical'
			)
		)
	);

	// grabbing artifacts
	artifact$ = combineLatest([
		this.artifactWithDirRelation$,
		this.uiService.type,
	]).pipe(
		map(([response, branchType]) => {
			// capture only artifacts that belong within the child side of the default hierarchical relation
			const childArtifacts =
				response.relations
					.find(
						(relation) =>
							relation.relationTypeToken.name ===
							'Default Hierarchical'
					)
					?.relationSides.find((side) => side.name === 'child')
					?.artifacts || [];

			// check if branchType is baseline and set editable accordingly (likely not needed anymore with branchType checking)
			const artfacts = childArtifacts.map((artifact) => ({
				...artifact,
				editable: branchType !== 'baseline',
			}));

			return artfacts;
		}),
		shareReplay({ bufferSize: 1, refCount: true }),
		takeUntilDestroyed()
	);

	addTab(artifact: artifact) {
		this.tabService.addArtifactTab(artifact);
	}

	@Input() set paths(value: string[][]) {
		this._paths.next(value);
	}

	protected _paths = new BehaviorSubject<string[][]>([[]]);

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.tabService.getIconClass(icon) +
			' ' +
			this.tabService.getIconVariantClass(icon)
		);
	}

	latestPaths = combineLatest([
		this._paths.asObservable(),
		this.artifact$,
	]).pipe(
		switchMap(([paths, arts]) => {
			// Remove path from path array if we are no longer on that path
			let childPaths = paths
				.filter((path) => path[path.length - 1] === this.artifactId)
				.map((path) => path.slice(0, -1));

			// If a child artifact is next on path, open it
			childPaths.forEach((path) => {
				arts.find((art) => {
					if (art.id == path[path.length - 1] && path.length > 1) {
						this.addArtifactsOpen(art.id);
					}
				});
			});
			// Update the paths array that we are passing down the hierarchy
			return of(childPaths);
		})
	);
}
