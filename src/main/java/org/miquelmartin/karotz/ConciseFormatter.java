package org.miquelmartin.karotz;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class ConciseFormatter extends Formatter {

	@Override
	public String format(LogRecord record) {
		return record.getLevel() + "\t" + record.getSourceMethodName() + ":\t"
				+ record.getMessage() + "\n";
	}

}
