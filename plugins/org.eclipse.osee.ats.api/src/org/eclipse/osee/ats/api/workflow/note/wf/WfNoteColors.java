package org.eclipse.osee.ats.api.workflow.note.wf;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class WfNoteColors extends OseeEnum {

   private static final Long ENUM_ID = 23988293L;
   private static Map<Long, WfNoteColors> idToColor = new HashMap<>();
   private final List<WfNoteColors> orderedColors = new ArrayList<>();

   public static WfNoteColors SENTINEL = new WfNoteColors(-1L, "SENTINEL", 2L);
   public static WfNoteColors COLOR_BLACK = new WfNoteColors(111L, "BLACK", 2L);
   public static WfNoteColors COLOR_BLUE = new WfNoteColors(222L, "BLUE", 9L);
   public static WfNoteColors COLOR_CYAN = new WfNoteColors(333L, "CYAN", 13L);
   public static WfNoteColors COLOR_GRAY = new WfNoteColors(444L, "GRAY", 15L);
   public static WfNoteColors COLOR_GREEN = new WfNoteColors(555L, "GREEN", 5L);
   public static WfNoteColors COLOR_MAGENTA = new WfNoteColors(666L, "MAGENTA", 11L);
   public static WfNoteColors COLOR_RED = new WfNoteColors(777L, "RED", 3L);
   public static WfNoteColors COLOR_YELLOW = new WfNoteColors(888L, "YELLOW", 7L);
   public static WfNoteColors COLOR_DARK_BLUE = new WfNoteColors(1111L, "DARK_BLUE", 10L);
   public static WfNoteColors COLOR_DARK_CYAN = new WfNoteColors(2222L, "DARK_CYAN", 14L);
   public static WfNoteColors COLOR_DARK_GRAY = new WfNoteColors(3333L, "DARK_GRAY", 16L);
   public static WfNoteColors COLOR_DARK_GREEN = new WfNoteColors(4444L, "DARK_GREEN", 6L);
   public static WfNoteColors COLOR_DARK_MAGENTA = new WfNoteColors(5555L, "DARK_MAGENTA", 11L);
   public static WfNoteColors COLOR_DARK_RED = new WfNoteColors(6666L, "DARK_RED", 3L);
   public static WfNoteColors COLOR_DARK_YELLOW = new WfNoteColors(7777L, "DARK_YELLOW", 7L);
   private final Long swtId;

   /**
    * @param id - id to store in db. These ids should NOT be changed, but new colors can be added and placed in
    * appropriate order, but use if swtId needs to be specifically handled for those
    * @param swtId - matches Eclipse's SWT.COLOR_* ids
    */
   public WfNoteColors(long id, String name, long swtId) {
      super(ENUM_ID, id, name);
      this.swtId = swtId;
      idToColor.put(id, this);
      orderedColors.add(this);
   }

   public static WfNoteColors getById(Long id) {
      return idToColor.get(id);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return SENTINEL;
   }

   public static Collection<WfNoteColors> get() {
      return idToColor.values();
   }

   public Long getSwtId() {
      return swtId;
   }

}
