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
import { AsyncPipe, NgClass } from '@angular/common';
import {
	Component,
	input,
	computed,
	forwardRef,
	inject,
	signal,
	effect,
} from '@angular/core';
import { MatIcon } from '@angular/material/icon';
import { ExpandIconComponent } from '@osee/shared/components';
import { BehaviorSubject } from 'rxjs';
import { ArtifactExplorerTabService } from '../../../services/artifact-explorer-tab.service';
import { ArtifactIconService } from '../../../services/artifact-icon.service';
import { ArtifactHierarchyComponent } from '../artifact-hierarchy/artifact-hierarchy.component';
import {
	artifactWithRelations,
	artifactTypeIcon,
	artifactRelationSide,
	relationTypeToken,
} from '@osee/artifact-with-relations/types';

@Component({
	selector: 'osee-artifact-hierarchy-relation-side',
	imports: [
		NgClass,
		AsyncPipe,
		MatIcon,
		CdkDrag,
		ExpandIconComponent,
		forwardRef(() => ArtifactHierarchyComponent),
	],
	templateUrl: './artifact-hierarchy-relation-side.component.html',
})
export class ArtifactHierarchyRelationSideComponent {
	private tabService = inject(ArtifactExplorerTabService);
	private artifactIconService = inject(ArtifactIconService);

	relationSide = input.required<artifactRelationSide>();
	private _effectRelationSide = effect(() => {
		//Init relationSide
		const value = this.relationSide();
		if (value !== undefined) {
			this._relationSide.set(value);
		}
	});
	typeToken = input.required<relationTypeToken>();
	private _effectTypeToken = effect(() => {
		//init typeToken
		const ttk_value = this.typeToken();
		if (ttk_value !== undefined) {
			this._typeToken.set(ttk_value);
		}
	});
	paths = input.required<string[][]>();
	private _effectPaths = effect(() => {
		//init paths
		const pt_value = this.paths();
		if (pt_value) {
			if (pt_value.length > 0) {
				pt_value.forEach((path) => {
					this.artifacts().find((art) => {
						if (art.id == path[path.length - 1]) {
							this.open();
							if (path.length > 1) {
								this.addArtifactsOpen(art.id);
							}
						}
					});
				});
				// Update the paths array that we are passing down the hierarchy
				this._paths.next([...pt_value]);
			}
		}
	});

	protected _paths = new BehaviorSubject<string[][]>([[]]);

	protected _relationSide = signal<artifactRelationSide>({
		name: '',
		artifacts: [],
		isSideA: false,
		isSideB: false,
	});
	protected _typeToken = signal<relationTypeToken>({
		id: '-1',
		idIntValue: -1,
		idString: '-1',
		multiplicity: '',
		name: '',
		newRelationTable: false,
		order: '',
		ordered: false,
		relationArtifactType: '',
	});
	protected typeName = computed(() => this._typeToken().name);
	protected relationSideName = computed(() => this._relationSide().name);
	protected artifacts = computed(() => this._relationSide().artifacts);
	protected isOpen = signal<boolean>(false);

	protected artifactsOpen = signal<string[]>([]);
	addArtifactsOpen(value: string) {
		this.artifactsOpen.update((rows) => [...rows, value]);
	}
	removeArtifactsOpen(value: string) {
		this.artifactsOpen.update((rows) => rows.filter((v) => v !== value));
	}
	open() {
		this.isOpen.set(true);
	}
	close() {
		this.isOpen.set(false);
	}

	addTab(artifact: artifactWithRelations) {
		this.tabService.addArtifactTab(artifact);
	}

	trackArtifacts(_index: number, item: artifactWithRelations) {
		return item.id;
	}

	getIconClasses(icon: artifactTypeIcon) {
		return (
			this.artifactIconService.getIconClass(icon) +
			' ' +
			this.artifactIconService.getIconVariantClass(icon)
		);
	}
}
