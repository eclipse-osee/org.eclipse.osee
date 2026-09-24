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
import {
	UiService,
	ArtifactChangeNotificationService,
} from '@osee/shared/services';
import {
	BehaviorSubject,
	combineLatest,
	debounceTime,
	filter,
	map,
	merge,
	shareReplay,
	startWith,
	switchMap,
	tap,
} from 'rxjs';
import { ATTRIBUTETYPEIDENUM } from '@osee/attributes/constants';
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
	private changeNotification = inject(ArtifactChangeNotificationService);
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

	/**
	 * Structural change trigger: fires on remote creates/deletes via SSE (plus local updates), and
	 * on SSE resync (reconnect) since structural changes may have been missed during the gap.
	 */
	private structuralChange$ = this.branchId$.pipe(
		filter((branch) => branch !== '' && branch !== '-1' && branch !== '0'),
		switchMap((branch) =>
			merge(
				this.changeNotification.structuralChangesForBranch(branch),
				this.changeNotification.resync$
			)
		),
		map(() => true)
	);

	/**
	 * Name-change trigger: refetches this level when a child artifact's Name attribute changes, so
	 * its tree label stays current. A Name edit is a pure `attribute_modified` and so is excluded
	 * from {@link structuralChange$}; here we react specifically to the Name attribute type (via
	 * `changedAttributeTypeIds`), and only when the changed artifact is one currently shown at this
	 * level, so an unrelated Name edit elsewhere on the branch does not refetch.
	 */
	/**
	 * Ids of the artifacts currently shown at this level. Maintained as a plain field (updated as a
	 * side effect of {@link children$}) so {@link nameChange$} can scope its refetch to visible
	 * nodes WITHOUT subscribing to `children$` — which would create a circular subscription, since
	 * `children$`'s own trigger merges `nameChange$`.
	 */
	private currentChildIds = new Set<string>();

	private nameChange$ = this.branchId$.pipe(
		filter((branch) => branch !== '' && branch !== '-1' && branch !== '0'),
		switchMap((branch) =>
			this.changeNotification.forChangedAttributeType(
				branch,
				ATTRIBUTETYPEIDENUM.NAME
			)
		),
		// Only refetch when the renamed artifact is one currently shown at this level, so an
		// unrelated Name edit elsewhere on the branch does not refetch this level.
		filter((inv) => this.currentChildIds.has(inv.artifactId)),
		map(() => true)
	);

	children$ = combineLatest([
		this._paths,
		this.branchId$,
		this.viewId$,
		merge(this.structuralChange$, this.nameChange$).pipe(startWith(true)),
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
		// Track the visible node ids so nameChange$ can scope its refetch without subscribing here.
		tap((children) => {
			this.currentChildIds = new Set(children.map((c) => c.id));
		}),
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
