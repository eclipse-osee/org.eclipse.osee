import { editPlatformTypeDialogDataMode } from "./EditPlatformTypeDialogDataMode.enum";
import { PlatformType } from "./platformType";

/**
 * Container containing info on whether or not the Edit Dialog should open in create/edit mode and what data to pre populate the fields with.
 */
export interface editPlatformTypeDialogData {
    mode: editPlatformTypeDialogDataMode,
    type: PlatformType
}