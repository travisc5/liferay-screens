/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.mobile.screens.ddl.form.interactor.update;

import com.liferay.mobile.android.service.JSONObjectWrapper;
import com.liferay.mobile.android.service.Session;
import com.liferay.mobile.android.v62.ddlrecord.DDLRecordService;
import com.liferay.mobile.screens.base.interactor.BaseCachedWriteRemoteInteractor;
import com.liferay.mobile.screens.cache.OfflinePolicy;
import com.liferay.mobile.screens.cache.ddl.form.DDLRecordCache;
import com.liferay.mobile.screens.cache.sql.CacheSQL;
import com.liferay.mobile.screens.context.SessionContext;
import com.liferay.mobile.screens.ddl.form.DDLFormListener;
import com.liferay.mobile.screens.ddl.model.Record;

import org.json.JSONObject;


/**
 * @author Jose Manuel Navarro
 */
public class DDLFormUpdateRecordInteractorImpl
	extends BaseCachedWriteRemoteInteractor<DDLFormListener, DDLFormUpdateRecordEvent>
	implements DDLFormUpdateRecordInteractor {

	public DDLFormUpdateRecordInteractorImpl(int targetScreenletId, OfflinePolicy offlinePolicy) {
		super(targetScreenletId, offlinePolicy);
	}

	@Override
	public void updateRecord(long groupId, final Record record) throws Exception {
		validate(groupId, record);

		storeWithCache(groupId, record);
	}

	public void onEvent(DDLFormUpdateRecordEvent event) {
		if (!isValidEvent(event)) {
			return;
		}

		onEventWithCache(event, event.getGroupId(), event.getRecord(), event.getJSONObject());
	}

	@Override
	protected void online(Object[] args) throws Exception {

		long groupId = (long) args[0];
		Record record = (Record) args[1];
		JSONObject fieldsValues = new JSONObject(record.getData());

		final JSONObject serviceContextAttributes = new JSONObject();
		serviceContextAttributes.put("userId", record.getCreatorUserId());
		serviceContextAttributes.put("scopeGroupId", groupId);

		JSONObjectWrapper serviceContextWrapper = new JSONObjectWrapper(serviceContextAttributes);

		getDDLRecordService(record, groupId).updateRecord(record.getRecordId(),
			0, fieldsValues, true, serviceContextWrapper);
	}

	@Override
	protected void notifySuccess(DDLFormUpdateRecordEvent event) {
		getListener().onDDLFormRecordUpdated(event.getRecord());
	}

	@Override
	protected void notifyError(DDLFormUpdateRecordEvent event) {
		getListener().onDDLFormUpdateRecordFailed(event.getException());
	}

	@Override
	protected void storeToCache(boolean synced, Object[] args) {
		long groupId = (long) args[0];
		Record record = (Record) args[1];
		JSONObject fieldsValues = new JSONObject(record.getData());

		DDLRecordCache recordCache = new DDLRecordCache(groupId, record, fieldsValues);
		recordCache.setDirty(!synced);
		CacheSQL.getInstance().set(recordCache);

		onEvent(new DDLFormUpdateRecordEvent(getTargetScreenletId(), record, groupId, fieldsValues));
	}

	protected DDLRecordService getDDLRecordService(Record record, long groupId) {
		Session session = SessionContext.createSessionFromCurrentSession();
		session.setCallback(new DDLFormUpdateRecordCallback(getTargetScreenletId(), record, groupId));
		return new DDLRecordService(session);
	}

	protected void validate(long groupId, Record record) {
		if (groupId <= 0) {
			throw new IllegalArgumentException("groupId cannot be 0 or negative");
		}

		if (record == null) {
			throw new IllegalArgumentException("record cannot be empty");
		}

		if (record.getFieldCount() == 0) {
			throw new IllegalArgumentException("Record's fields cannot be empty");
		}

		if (record.getRecordId() <= 0) {
			throw new IllegalArgumentException("Record's recordId cannot be 0 or negative");
		}
	}

}