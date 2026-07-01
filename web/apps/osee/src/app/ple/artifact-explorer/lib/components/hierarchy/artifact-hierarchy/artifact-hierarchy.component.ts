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
import { CdkDrag } from '@angular/cdk/drag-drop';
import { AsyncPipe } from '@angular/common';
import { Component, Input, input, viewChild, inject } from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import {
	MatMenu,
	MatMenuContent,
	MatMenuTrigger,
} from '@angular/material/menu';
import { ExpandIconComponent } from '@osee/shared/components';
import { UiService } from '@osee/shared/services';
import {
	BehaviorSubject,
	combineLatest,
	debounceTime,
	filter,
	map,
	shareReplay,
	startWith,
	switchMap,
} from 'rxjs';
import { ArtifactExplorerHttpService } from '../../../services/artifact-explorer-http.service';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { ArtifactHierarchyArtifactsExpandedService } from '../../../services/artifact-hierarchy-artifacts-expanded.service';
import { ArtifactIconService } from '../../../services/artifact-icon.service';
import {
	artifactWithRelations,
	artifactTypeIcon,
} from '@osee/artifact-with-relations/types';
import { DEFAULT_HIERARCHY_ROOT_ARTIFACT } from '../../../types/artifact-explorer-constants';
import { ArtifactOperationsContextMenuComponent } from '../artifact-operations-context-menu/artifact-operations-context-menu.component';

@Component({
	selector: 'osee-artifact-hierarchy',
	imports: [
		AsyncPipe,
		ArtifactOperationsContextMenuComponent,
		MatIcon,
		CdkDrag,
		MatMenuTrigger,
		MatMenu,
		MatMenuContent,
		ExpandIconComponent,
	],
	templateUrl: './artifact-hierarchy.component.html',
})
export class ArtifactHierarchyComponent {
	private artExpHttpService = inject(ArtifactExplorerHttpService);
	private uiService = inject(UiService);
	private tabService = inject(ArtifactExplorerTabService);
	private artifactIconService = inject(ArtifactIconService);
	private artifactsExpandedService = inject(
		ArtifactHierarchyArtifactsExpandedService
	);

	artifactId = input.required<string>();
	@Input() set paths(paths: string[][]) {
		this._paths.next(paths);
	}

	protected _paths = new BehaviorSubject<string[][]>([[]]);

	branchId$ = this.uiService.id;
	branchType$ = this.uiService.type;
	viewId$ = this.uiService.viewId;

	trackById(_index: number, item: artifactWithRelations) {
		return item.id;
	}

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

	/** Whether this artifact has an open tab in the editor. */
	isOpenInTab(artifactId: string): boolean {
		return this.tabService
			.Tabs()
			.some(
				(t) => t.tabType === 'Artifact' && t.artifact.id === artifactId
			);
	}

	toggleExpandButton(artifactId: string) {
		return this.artifactIsExpanded(artifactId)
			? this.collapseArtifact(artifactId)
			: this.expandArtifact(artifactId);
	}

	// Hierarchical children (lightweight - only name, id, icon)

	children$ = combineLatest([
		this._paths,
		this.branchId$,
		this.viewId$,
		this.uiService.update.pipe(startWith(true)),
	]).pipe(
		debounceTime(100),
		filter(
			([_, branch, view]) =>
				branch !== '-1' &&
				branch !== '0' &&
				branch !== '' &&
				this.artifactId() !== '0' &&
				view !== ''
		),
		switchMap(([_, branch, view]) =>
			this.artExpHttpService.getHierarchicalChildren(
				branch,
				this.artifactId(),
				view
			)
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	// Child artifacts with branchType-aware editable flag

	artifacts = combineLatest([this.children$, this.uiService.type]).pipe(
		map(([children, branchType]) =>
			children.map((artifact) => ({
				...artifact,
				editable: branchType !== 'baseline',
			}))
		),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	// Paths filtered for this level and passed down to children hierarchy components

	latestPaths = combineLatest([this._paths, this.artifacts]).pipe(
		map(([paths, _arts]) => {
			// Filter to paths that pass through this artifact and trim this level
			return paths
				.filter((path) => path[path.length - 1] === this.artifactId())
				.map((path) => path.slice(0, -1));
		}),
		shareReplay({ bufferSize: 1, refCount: true })
	);

	addTab(artifact: artifactWithRelations) {
		this.tabService.addArtifactTab(artifact);
	}

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}

	matMenuTrigger = viewChild.required(MatMenuTrigger);

	menuPosition = {
		x: '0',
		y: '0',
	};

	openContextMenu(event: MouseEvent, artifact: artifactWithRelations) {
		event.preventDefault();
		this.menuPosition.x = event.clientX + 'px';
		this.menuPosition.y = event.clientY + 'px';
		this.matMenuTrigger().menuData = {
			artifactId: artifact.id,
			parentArtifactId: this.artifactId(),
			operationTypes: artifact.operationTypes,
		};
		this.matMenuTrigger().openMenu();
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

	DEFAULT_HIERARCHY_ROOT_ARTIFACT = DEFAULT_HIERARCHY_ROOT_ARTIFACT;
}
