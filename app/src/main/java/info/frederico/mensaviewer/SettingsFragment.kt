package info.frederico.mensaviewer


import android.content.SharedPreferences
import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.MultiSelectListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import android.widget.Toast
import info.frederico.mensaviewer.helper.Mensa

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        updateSummary(findPreference(key)!!)
    }

    private fun updateSummary(pref : Preference) {
        if (pref is ListPreference) {
            pref.summary = pref.entry
        }
        else if (pref is MultiSelectListPreference && pref.values.isNotEmpty()) {
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

    private fun checkMensaPreference(preference: Preference?, newValue: Any?) : Boolean {
        if(preference is MultiSelectListPreference && newValue is Set<*>){
            if(newValue.size in 1..4){
                return true
            }
        }
        Toast.makeText(context, getString(R.string.error_message_preferred_canteen_selection), Toast.LENGTH_LONG).show()
        return false
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)
        for(i in 0 until preferenceScreen.preferenceCount){
            updateSummary(preferenceScreen.getPreference(i))
        }

        val mensaPreference = findPreference<Preference>(getString(R.string.pref_mensa))!!
        mensaPreference.setOnPreferenceChangeListener { preference, newValue ->
            return@setOnPreferenceChangeListener checkMensaPreference(preference, newValue)
        }
    }
}
