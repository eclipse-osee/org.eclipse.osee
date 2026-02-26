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
import { CdkDropListGroup } from '@angular/cdk/drag-drop';
import { NgClass } from '@angular/common';
import { Component, Input, inject, DestroyRef } from '@angular/core'; // Author: Eihab Khudhair (ekhudhai) Task 183 - Auto-open artifact from query params
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatDrawer, MatDrawerContainer } from '@angular/material/sidenav';
import { MatTooltip } from '@angular/material/tooltip';
import { UiService } from '@osee/shared/services';
import { ArtifactTabGroupComponent } from './lib/components/artifact-tab-group/artifact-tab-group.component';
import { ArtifactHierarchyPanelComponent } from './lib/components/hierarchy/artifact-hierarchy-panel/artifact-hierarchy-panel.component';
import { ArtifactExplorerTabService } from './lib/services/artifact-explorer-tab.service';
import { ArtifactExplorerHttpService } from './lib/services/artifact-explorer-http.service'; // Author: Eihab Khudhair (ekhudhai) Task 183 - Load artifact details to open tab
import { artifactWithRelations } from '@osee/artifact-with-relations/types'; // Author: Eihab Khudhair (ekhudhai) Task 183 - Strong typing for opened artifact tab
import { ExplorerPanel, tab } from './lib/types/artifact-explorer';
import { ActionsPanelComponent } from './lib/components/actions/actions-panel.component';
import { ActivatedRoute, Router } from '@angular/router';
import { filter, distinctUntilChanged, map, take } from 'rxjs'; // Author: Eihab Khudhair (ekhudhai) Task 183 - Auto-open artifact from query params
import { toSignal, takeUntilDestroyed } from '@angular/core/rxjs-interop'; // Author: Eihab Khudhair (ekhudhai) Task 183 - Auto-open artifact from query params
import { ArtifactExplorerPreferencesService } from './lib/services/artifact-explorer-preferences.service';

@Component({
	selector: 'osee-artifact-explorer',
	imports: [
		NgClass,
		ArtifactHierarchyPanelComponent,
		ActionsPanelComponent,
		ArtifactTabGroupComponent,
		MatDrawerContainer,
		CdkDropListGroup,
		MatDrawer,
		MatIconButton,
		MatMenuTrigger,
		MatTooltip,
		MatIcon,
		MatMenu,
		MatMenuItem,
	],
	templateUrl: './artifact-explorer.component.html',
})
export class ArtifactExplorerComponent {
	private uiService = inject(UiService);
	private tabService = inject(ArtifactExplorerTabService);
	private artExpHttpService = inject(ArtifactExplorerHttpService); // Author: Eihab Khudhair (ekhudhai) Task 183 - Load artifact for tab open
	private userPrefsService = inject(ArtifactExplorerPreferencesService);
	private routeUrl = inject(ActivatedRoute);
	private router = inject(Router);
	private destroyRef = inject(DestroyRef); // Author: Eihab Khudhair (ekhudhai) Task 183 - Auto-open artifact from query params

	@Input() set branchType(branchType: 'working' | 'baseline' | '') {
		if (branchType != undefined) {
			this.uiService.typeValue = branchType;
		} else {
			this.uiService.typeValue = '';
		}
	}
	@Input() set branchId(branchId: string) {
		if (branchId != undefined) {
			this.uiService.idValue = branchId;
		} else {
			this.uiService.idValue = '';
		}
	}

	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 183 - Auto-open artifact tab when navigating with artifactId query param
	 */
	constructor() {
		this.routeUrl.queryParamMap
			.pipe(
				map((params) => ({
					artifactId: (params.get('artifactId') || '').trim(),
					viewId: (params.get('viewId') || '').trim(),
				})),
				filter((x) => x.artifactId.length > 0),
				distinctUntilChanged((a, b) => a.artifactId === b.artifactId && a.viewId === b.viewId),
				takeUntilDestroyed(this.destroyRef)
			)
			.subscribe(({ artifactId, viewId }) => {
				const parsedViewId = viewId !== '' ? viewId : '-1';
				this.openArtifactTabFromParams(artifactId, parsedViewId);
			});
	}
	/**
	 * Author: Eihab Khudhair (ekhudhai)
	 * Task 183 - Load artifact details and open tab on current branch/view
	 */
	private openArtifactTabFromParams(artifactId: string, viewId: string): void {
		if (!artifactId) return;

		// branchId is already driven by the route inputs (branchId setter updates uiService.idValue)
		this.uiService.id.pipe(take(1)).subscribe((branchId) => {
			if (!branchId) return;

			this.artExpHttpService
				.getartifactWithRelations(branchId, artifactId, viewId, true)
				.pipe(take(1))
				.subscribe({
					next: (artifact: artifactWithRelations) => {
						this.tabService.addArtifactTabOnBranch(artifact, branchId, viewId);
					},
					error: (err: unknown) => {
						const msg = err instanceof Error ? err.message : String(err);
						console.error('Task 183: Failed to load artifact for tab open:', msg);
					},
				});
		});
	}

	openTabs = this.tabService.Tabs;
	selectedTabIndex = this.tabService.selectedIndex;
	panelLocation = toSignal(
		this.userPrefsService.artifactExplorerPreferences.pipe(
			map((prefs) =>
				prefs.artifactExplorerPanelLocation === true ? 'start' : 'end'
			)
		)
	);

	currentPanel = toSignal(
		this.routeUrl.queryParamMap.pipe(
			map((value) => {
				const panel = value.get('panel') as ExplorerPanel | null;
				return panel === null ? 'Artifacts' : panel;
			})
		),
		{ initialValue: 'Artifacts' }
	);

	setCurrentPanel(panel: ExplorerPanel) {
		this.router.navigate([], {
			queryParams: { panel: panel },
			relativeTo: this.routeUrl,
		});
	}

	setSelectedTab(index: number) {
		this.tabService.SelectedIndex = index;
	}
	removeTab(event: MouseEvent, index: number) {
		event.stopPropagation();
		this.tabService.removeTab(index);
	}

	fetchIcon(tab: tab) {
		return this.tabService.getTabIcon(tab);
	}

	getTabIconClasses(tab: tab) {
		return (
			this.tabService.getTabIconClass(tab) +
			' ' +
			this.tabService.getTabIconVariantClass(tab)
		);
	}
}

export default ArtifactExplorerComponent;
