import { httpResource } from '@angular/common/http';
import {
	ChangeDetectionStrategy,
	Component,
	computed,
	signal,
} from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { apiURL } from '@osee/environments';

type AtsTeamConfiguration = {
	topTeamDefinition: string | null;
	idToTeamDef: Record<string, { name: string; parentId: string | null }>;
};

@Component({
	selector: 'osee-team-definition-filter',
	imports: [MatFormField, MatLabel, MatSelect, MatOption],
	templateUrl: './team-definition-filter.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeamDefinitionFilterComponent {
	private readonly selection = signal('');
	readonly selectedTeamDefinitionId = this.selection.asReadonly();

	protected readonly configuration = httpResource<AtsTeamConfiguration>(
		() => apiURL + '/ats/config'
	);
	protected readonly teamDefinitions = computed(() => {
		if (!this.configuration.hasValue()) {
			return [];
		}
		const { topTeamDefinition, idToTeamDef } = this.configuration.value();
		if (!topTeamDefinition) {
			return [];
		}
		return Object.entries(idToTeamDef)
			.filter(([, team]) => team.parentId === topTeamDefinition)
			.map(([id, { name }]) => ({ id, name }))
			.sort((a, b) => a.name.localeCompare(b.name));
	});
	readonly selectedTeamScope = computed(() => {
		const selectedId = this.selectedTeamDefinitionId();
		if (!selectedId || !this.configuration.hasValue()) {
			return undefined;
		}

		const childrenByParent = new Map<string, string[]>();
		for (const [id, { parentId }] of Object.entries(
			this.configuration.value().idToTeamDef
		)) {
			if (!parentId) {
				continue;
			}
			const children = childrenByParent.get(parentId);
			if (children) {
				children.push(id);
			} else {
				childrenByParent.set(parentId, [id]);
			}
		}

		const ids = new Set([selectedId]);
		for (const id of ids) {
			for (const child of childrenByParent.get(id) ?? []) {
				ids.add(child);
			}
		}
		return [...ids];
	});

	protected selectTeamDefinition(id: string): void {
		this.selection.set(id);
	}
}
