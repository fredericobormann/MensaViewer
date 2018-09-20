package info.frederico.mensaviewer


import android.content.SharedPreferences
import android.os.Bundle
import android.preference.ListPreference
import android.preference.MultiSelectListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.widget.Toast
import info.frederico.mensaviewer.helper.Mensa

class SettingsFragment : PreferenceFragment(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        updateSummary(findPreference(key))
    }

    private fun updateSummary(pref : Preference) {
        if(pref is ListPreference){
            val listPref = pref as ListPreference
            pref.summary = listPref.entry
        }
        else if (pref is MultiSelectListPreference && !pref.values.isEmpty()) {
            pref.summary = createMultiSelectedListPreferenceSummary(pref)
        }
    }

    private fun createMultiSelectedListPreferenceSummary(pref: MultiSelectListPreference): String {
        val sb: StringBuilder = StringBuilder()
        for(entryValue in pref.values) {
            sb.append(Mensa.valueOf(entryValue).description + ", ")
        }
        return sb.substring(0, sb.length - 2)
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
        if(preference is MultiSelectListPreference && newValue is Set<*>){
            if(newValue.size in 1..4){
                return true
            }
        }
        Toast.makeText(context, "Bitte wähle zwischen 1 und 4 Mensas aus", Toast.LENGTH_LONG).show()
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
