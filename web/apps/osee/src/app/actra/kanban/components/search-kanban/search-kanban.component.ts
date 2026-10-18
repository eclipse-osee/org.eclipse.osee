import { ChangeDetectionStrategy, Component } from '@angular/core';
import { AssigneeSearchComponent } from './components/assignee-search/assignee-search.component';

@Component({
	selector: 'osee-search-kanban',
	imports: [AssigneeSearchComponent],
	templateUrl: './search-kanban.component.html',
	changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SearchKanbanComponent {}
