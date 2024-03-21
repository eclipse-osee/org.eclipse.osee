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
import { Component, Input, ViewChild, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BranchPickerComponent } from '@osee/shared/components';
import { MatExpansionModule } from '@angular/material/expansion';
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
import { MatIconModule } from '@angular/material/icon';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { MatButtonModule } from '@angular/material/button';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import {
	DEFAULT_HIERARCHY_ROOT_ARTIFACT_ID,
	artifact,
	artifactTypeIcon,
} from '../../../types/artifact-explorer.data';
import { ArtifactHierarchyRelationsComponent } from '../artifact-hierarchy-relations/artifact-hierarchy-relations.component';
import { MatMenuModule, MatMenuTrigger } from '@angular/material/menu';
import { ArtifactOptionsContextMenuComponent } from '../artifact-options-context-menu/artifact-options-context-menu.component';
import { ArtifactIconService } from '../../../services/artifact-icon.service';
import { ArtifactHierarchyArtifactsExpandedService } from '../../../services/artifact-hierarchy-artifacts-expanded.service';

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
		MatMenuModule,
		ArtifactOptionsContextMenuComponent,
	],
	templateUrl: './artifact-hierarchy.component.html',
})
export class ArtifactHierarchyComponent {
	artifactId = input.required<string>();
	@Input() set paths(paths: string[][]) {
		this._paths.next(paths);
	}

	protected _paths = new BehaviorSubject<string[][]>([[]]);

	branchId$ = this.uiService.id;
	branchType$ = this.uiService.type;
	viewId$ = this.uiService.viewId;

	trackById(index: number, item: artifact) {
		return item.id;
	}

	constructor(
		private artExpHttpService: ArtifactExplorerHttpService,
		private uiService: UiService,
		private tabService: ArtifactExplorerTabService,
		private artifactIconService: ArtifactIconService,
		private artifactsExpandedService: ArtifactHierarchyArtifactsExpandedService
	) {}

	// UI expand/collapse artifacts in the hierarchy

	expandArtifact(value: string) {
		this.artifactsExpandedService.expandArtifact(this.artifactId(), value);
	}

	collapseArtifact(value: string) {
		this.artifactsExpandedService.collapseArtifact(
			this.artifactId(),
			value
		);
	}

	artifactIsExpanded(value: string) {
		return this.artifactsExpandedService.isExpanded(
			this.artifactId(),
			value
		);
	}

	toggleExpandButton(artifactId: string) {
		return this.artifactIsExpanded(artifactId)
			? this.collapseArtifact(artifactId)
			: this.expandArtifact(artifactId);
	}

	// Artifact with its direct relations

	artifactWithDirRelations = combineLatest([
		this._paths,
		this.branchId$,
		this.viewId$,
	]).pipe(
		filter(
			([_, branch, view]) =>
				branch !== '-1' &&
				branch !== '0' &&
				branch !== '' &&
				this.artifactId() !== '0' &&
				view !== ''
		),
		switchMap(([_, branch, view]) =>
			this.artExpHttpService
				.getDirectRelations(branch, this.artifactId(), view)
				.pipe(repeat({ delay: () => this.uiService.update }))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	// Relations (only ones that are populated)

	relation$ = this.artifactWithDirRelations.pipe(
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

	// Child artifacts (children of the component's main artifact)

	artifacts = combineLatest([
		this.artifactWithDirRelations,
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
			const artifacts = childArtifacts.map((artifact) => ({
				...artifact,
				editable: branchType !== 'baseline',
			}));

			return artifacts;
		}),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	// Paths (initially from the paths service) that are updated and passed down to children artifact hierarchy components

	latestPaths = combineLatest([this._paths, this.artifacts]).pipe(
		map(([paths, arts]) => {
			// Remove path from path array if we are no longer on that path
			let childPaths = paths
				.filter((path) => path[path.length - 1] === this.artifactId())
				.map((path) => path.slice(0, -1));

			// If a child artifact is next on path, open it
			childPaths.forEach((path) => {
				arts.find((art) => {
					if (art.id === path[path.length - 1]) {
						this.expandArtifact(art.id);
					}
				});
			});
			// Update the paths array that we are passing down the hierarchy
			return childPaths;
		}),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	addTab(artifact: artifact) {
		this.tabService.addArtifactTab(artifact);
	}

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}

	// Right-click context menu

	@ViewChild(MatMenuTrigger, { static: true })
	matMenuTrigger!: MatMenuTrigger;

	menuPosition = {
		x: '0',
		y: '0',
	};

	openContextMenu(event: MouseEvent, artifactId: `${number}`) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger.menuData = {
			artifactId: artifactId,
			parentArtifactId: this.artifactId,
		};
		this.matMenuTrigger.openMenu();
	}

	getSiblingArtifactId(artifactId: `${number}`) {
		return this.artifacts.pipe(
			map((artifacts) =>
				artifacts.filter((artifact) => artifact.id !== artifactId)
			),
			map((filteredArtifacts) =>
				filteredArtifacts.length > 0
					? filteredArtifacts[0].id
					: undefined
			)
		);
	}

	DEFAULT_HIERARCHY_ROOT_ARTIFACT_ID = DEFAULT_HIERARCHY_ROOT_ARTIFACT_ID;
}
