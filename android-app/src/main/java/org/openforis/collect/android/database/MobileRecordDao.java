package org.openforis.collect.android.database;

import static org.openforis.collect.persistence.jooq.Tables.OFC_RECORD;
import static org.openforis.collect.persistence.jooq.tables.OfcUser.OFC_USER;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.openforis.collect.model.CollectRecord;
import org.openforis.collect.model.CollectRecord.Step;
import org.openforis.collect.model.CollectSurvey;
import org.openforis.collect.model.RecordSummarySortField;
import org.openforis.collect.model.User;
import org.openforis.idm.metamodel.EntityDefinition;
import org.openforis.idm.metamodel.Schema;
import org.springframework.transaction.annotation.Transactional;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class MobileRecordDao extends org.openforis.collect.persistence.RecordDao {

	public MobileRecordDao() {
		super();
	}
	
	
}