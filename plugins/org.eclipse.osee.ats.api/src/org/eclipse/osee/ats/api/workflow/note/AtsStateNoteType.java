package org.eclipse.osee.ats.api.workflow.note;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Arrays;
import java.util.Collection;
import org.eclipse.osee.framework.core.enums.OseeEnum;

/**
 * @author Donald G. Dunne
 */
public class AtsStateNoteType extends OseeEnum {

   private static final Long ENUM_ID = 23565232345L;

   public static AtsStateNoteType Other = new AtsStateNoteType(111L, "None");
   public static AtsStateNoteType Info = new AtsStateNoteType(222L, "Info");
   public static AtsStateNoteType Warning = new AtsStateNoteType(333L, "Warning");
   public static AtsStateNoteType Problem = new AtsStateNoteType(444L, "Problem");

   public AtsStateNoteType(long id, String name) {
      super(ENUM_ID, id, name);
   }

   @Override
   public Long getTypeId() {
      return ENUM_ID;
   }

   @JsonIgnore
   @Override
   public OseeEnum getDefault() {
      return Other;
   }

   public static Collection<String> getNames() {
      return Arrays.asList(Info.name(), Warning.name(), Problem.name());
   }

   public static AtsStateNoteType valueOf(String name) {
      AtsStateNoteType type = AtsStateNoteType.Other;
      try {
         type = (AtsStateNoteType) AtsStateNoteType.Other.get(name);
      } catch (Exception ex) {
         // do nothing
      }
      return type;
   }
}
