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
import { Component, Input, inject } from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatMenu, MatMenuItem, MatMenuTrigger } from '@angular/material/menu';
import { MatDrawer, MatDrawerContainer } from '@angular/material/sidenav';
import { MatTooltip } from '@angular/material/tooltip';
import { UiService } from '@osee/shared/services';
import { ArtifactTabGroupComponent } from './lib/components/artifact-tab-group/artifact-tab-group.component';
import { ArtifactHierarchyPanelComponent } from './lib/components/hierarchy/artifact-hierarchy-panel/artifact-hierarchy-panel.component';
import { ArtifactExplorerTabService } from './lib/services/artifact-explorer-tab.service';
import { ExplorerPanel, tab } from './lib/types/artifact-explorer';
import { ActionsPanelComponent } from './lib/components/actions/actions-panel.component';
import { ActivatedRoute, Router } from '@angular/router';
import { map } from 'rxjs';
import { toSignal } from '@angular/core/rxjs-interop';
import { ArtifactExplorerPreferencesService } from './lib/services/artifact-explorer-preferences.service';

@Component({
	selector: 'osee-artifact-explorer',
	standalone: true,
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
	private userPrefsService = inject(ArtifactExplorerPreferencesService);
	private routeUrl = inject(ActivatedRoute);
	private router = inject(Router);

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
