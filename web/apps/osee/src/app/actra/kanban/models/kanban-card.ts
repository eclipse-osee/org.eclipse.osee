import { KanbanColumnId } from './kanban-column-id';
import { KanbanSyncStatus } from './kanban-sync-status';

export interface KanbanCard {
	id: string;
	title: string;
	columnId: KanbanColumnId;
	assignees: string[];
	syncStatus: KanbanSyncStatus;
}
