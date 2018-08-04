package info.frederico.mensaviewer


import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.MultiSelectListPreference
import android.preference.Preference
import android.preference.PreferenceFragment

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        updateSummary(findPreference(key))
    }

    private fun updateSummary(pref : Preference) {
        if(pref is ListPreference){
            val listPref = pref as ListPreference
            pref.summary = listPref.entry
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        addPreferencesFromResource(R.xml.preferences)
        for(i in 0 until preferenceScreen.preferenceCount){
            updateSummary(preferenceScreen.getPreference(i))
        }

        val mensaPreference = findPreference(getString(R.string.pref_mensa))
        mensaPreference.setOnPreferenceChangeListener { preference, newValue ->
            return@setOnPreferenceChangeListener checkMensaPreference(preference, newValue)
        }
    }

    private fun checkMensaPreference(preference: Preference?, newValue: Any?) : Boolean {
        if(preference is MultiSelectListPreference && newValue is Array<*>){
            if(newValue.size in 1..4){
                return true
            }
        }
        return false
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
