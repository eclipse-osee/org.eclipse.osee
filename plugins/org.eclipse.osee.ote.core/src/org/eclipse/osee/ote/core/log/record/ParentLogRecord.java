package org.eclipse.osee.ote.core.log.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;

public class ParentLogRecord extends LogRecord {
    private static final long serialVersionUID = 684361479587503820L;
    private Collection<LogRecord> records = new ArrayList<>();

    public ParentLogRecord() {
        super(Level.OFF, "");
    }

    public void addChild(final LogRecord testPoint) {
        records.add(testPoint);
    }

    @JsonProperty
    public Collection<LogRecord> getChildRecords() {
        return records;
    }
    
    @Override
   @JsonIgnore
    public Level getLevel() {
        return super.getLevel();
    };
    
    @Override
   @JsonIgnore
    public String getMessage() {
        return super.getMessage();
    };
}