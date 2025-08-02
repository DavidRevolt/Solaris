package com.davidrevolt.core.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class PreferencesDataSourceImpl @Inject constructor(private val dataStore: DataStore<Preferences>) :
    PreferencesDataSource {

    override suspend fun setCurrentLocation(locationId: String, keepPersisting: Boolean) {
        dataStore.edit { prefs ->
            prefs[CURRENT_LOCATION_ID] = locationId
            prefs[KEEP_PERSISTING] = keepPersisting
        }
    }

    override fun currentLocationId(): Flow<String?> =
        dataStore.data.map { prefs -> prefs[CURRENT_LOCATION_ID] }

    override suspend fun keepPersisting(keepPersisting: Boolean) {
        dataStore.edit { prefs ->
            prefs[KEEP_PERSISTING] = keepPersisting
        }
    }

    override suspend fun keepPersisting(): Boolean =
        dataStore.data.first()[KEEP_PERSISTING] == true


    companion object CurrentLocationPrefsKeys {
        val CURRENT_LOCATION_ID = stringPreferencesKey("current_location_id")
        val KEEP_PERSISTING = booleanPreferencesKey("keep_persisting")
    }
}