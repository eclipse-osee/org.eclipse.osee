import { httpResource } from '@angular/common/http';
import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { MatOption } from '@angular/material/core';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatSelect } from '@angular/material/select';
import { apiURL } from '@osee/environments';

@Component({
	selector: 'osee-team-definition-filter',
	imports: [MatFormField, MatLabel, MatSelect, MatOption],
	templateUrl: './team-definition-filter.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TeamDefinitionFilterComponent {
	private readonly selection = signal('');
	readonly selectedTeamDefinitionId = this.selection.asReadonly();

	protected readonly teamDefinitions = httpResource<
		{ id: string; name: string }[]
	>(() => apiURL + '/ats/config/teamdef');

	protected selectTeamDefinition(id: string): void {
		this.selection.set(id);
	}
}
