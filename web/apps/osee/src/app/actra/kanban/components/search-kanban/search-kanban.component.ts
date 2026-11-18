import {
	ChangeDetectionStrategy,
	Component,
	computed,
	viewChild,
} from '@angular/core';
import { AssigneeSearchComponent } from './components/assignee-search/assignee-search.component';
import { SearchWorkflowComponent } from './components/search-workflow/search-workflow.component';
import { TeamDefinitionFilterComponent } from './components/team-definition-filter/team-definition-filter.component';
import { TeamFilterComponent } from './components/team-filter/team-filter.component';

@Component({
	selector: 'osee-search-kanban',
	imports: [
		TeamDefinitionFilterComponent,
		AssigneeSearchComponent,
		TeamFilterComponent,
		SearchWorkflowComponent,
	],
	templateUrl: './search-kanban.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SearchKanbanComponent {
	private readonly workflowFilter = viewChild(SearchWorkflowComponent);
	readonly selectedWorkflowIds = computed(
		() => this.workflowFilter()?.selectedWorkflowIds() ?? []
	);
}
