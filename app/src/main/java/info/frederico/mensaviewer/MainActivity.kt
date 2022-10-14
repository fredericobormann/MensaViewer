package info.frederico.mensaviewer

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import info.frederico.mensaviewer.helper.EssenViewModel
import info.frederico.mensaviewer.helper.Essensplan
import info.frederico.mensaviewer.helper.Mensa
import info.frederico.mensaviewer.helper.VeggieFilterOption
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: EssenAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager
    private var essensliste: Essensplan = Essensplan()
    private lateinit var prefListener: SharedPreferences.OnSharedPreferenceChangeListener

    private lateinit var evModel: EssenViewModel

    private lateinit var viewIdMensaMap: HashMap<Int, Mensa>

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        return@OnNavigationItemSelectedListener reactToNavSelection(item)
    }

    /**
     * Changes selected Mensa in the associated EssenViewModel, which changes the plan accordingly.
     *
     * @param newMensa new Mensa to use.
     */
    private fun changeSelectedMensa(newMensa: Mensa){
        if(!evModel.isCachedDataAvailable(newMensa)){
                swipe_container.isRefreshing = true
        }
        tv_error_message_internet.visibility = View.INVISIBLE
        button_launch_settings_activity.visibility = View.INVISIBLE
        evModel.mensa = newMensa
    }

    /**
     * Update the canteen plan according to the new Navigation-Item selected
     *
     * @param item new Navigation-Item selected
     *
     * @return true if completed successfully
     */
    private fun reactToNavSelection(item: MenuItem): Boolean{
        if(viewIdMensaMap.containsKey(item.itemId)) {
            recyclerView.visibility = View.INVISIBLE
            changeSelectedMensa(viewIdMensaMap[item.itemId] ?: Mensa.STUDIERENDENHAUS)
            return true
        }

        return false
    }

    private val mOnNavigationItemReselectedListener = BottomNavigationView.OnNavigationItemReselectedListener {}

    /**
     * Create the MainActivity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        evModel = ViewModelProviders.of(this).get(EssenViewModel::class.java)

        try {
            setLayout()
        }
        catch(nie: NavigationInvalidException){
            showNavigationInvalidMessage()
        }
        finally {
            setListener()
            swipe_container.isRefreshing = true
            evModel.forceReload()
        }
    }

    /**
     * Shows a message that no Mensa is selected in the preferences.
     */
    private fun showNavigationInvalidMessage() {
        tv_error_message_internet.text = getString(R.string.error_message_navigation)
        my_recycler_view.visibility = View.INVISIBLE
        tv_error_message_internet.visibility = View.VISIBLE
        button_launch_settings_activity.visibility = View.VISIBLE
        swipe_container.visibility = View.INVISIBLE
    }

    /**
     * Initializes UI components:
     *  RecyclerView
     *  SwipeContainer
     *  Navigation
     */
    private fun setLayout() {
        setContentView(R.layout.activity_main)


        viewManager = LinearLayoutManager(this)
        viewAdapter = EssenAdapter(essensliste, this)

        recyclerView = my_recycler_view.apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        swipe_container.setColorSchemeColors(getColor(R.color.colorAccent), getColor(R.color.colorPrimary), getColor(R.color.secondaryLightColor))

        initializeNavigation()
    }

    /**
     * Registers Listeners for:
     *  SwipeContainer
     *  LiveData
     *  Preferences
     *  Navigation
     *  Settings-Button
     */
    private fun setListener(){
        swipe_container.setOnRefreshListener {
            evModel.forceReload()
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            sharedPreferencesChanged(key)
        }
        preferences.registerOnSharedPreferenceChangeListener(prefListener)

        val essenObserver = Observer<Essensplan>{ result ->
            if(result == null){
                showInternetConnectionMessage()
            }
            else if (result.isEmpty()) {
                showNoDataMessage()
            } else {
                viewAdapter.setEssensplan(result)
                swipe_container.isRefreshing = false
                my_recycler_view.visibility = View.VISIBLE
                tv_error_message_internet.visibility = View.INVISIBLE
                button_launch_settings_activity.visibility = View.INVISIBLE
            }
        }
        evModel.essen.observe(this, essenObserver)

        button_launch_settings_activity.setOnClickListener {
            launchSettingsActivity()
        }
    }

    /**
     * Shows a message that some kind of network error occured.
     */
    private fun showInternetConnectionMessage() {
        tv_error_message_internet.text = getString(R.string.error_message_internet)
        my_recycler_view.visibility = View.INVISIBLE
        swipe_container.isRefreshing = false
        tv_error_message_internet.visibility = View.VISIBLE
        button_launch_settings_activity.visibility = View.INVISIBLE
    }

    /**
     * Shows a message that fetched data was empty
     */
    private fun showNoDataMessage() {
        tv_error_message_internet.text = getString(R.string.error_message_data)
        my_recycler_view.visibility = View.INVISIBLE
        swipe_container.isRefreshing = false
        tv_error_message_internet.visibility = View.VISIBLE
        button_launch_settings_activity.visibility = View.INVISIBLE
    }

    /**
     * Initializes Navigation according to Preferences.
     */
    private fun initializeNavigation() {
        viewIdMensaMap = HashMap()
        navigation.menu.clear()
        val selectedMensas = PreferenceManager.getDefaultSharedPreferences(this).getStringSet(getString(R.string.pref_mensa), resources.getStringArray(R.array.pref_mensa_default).toSet())!!
        if(selectedMensas.isEmpty()){
            throw NavigationInvalidException()
        } else {
            // BottomNavigationBar is hidden by default / if it's empty
            navigation.visibility = View.VISIBLE
        }
        for (m in selectedMensas) {
            val mensa = Mensa.valueOf(m)
            val item = navigation.menu.add(0, mensa.navigationViewId, mensa.ordinal, mensa.description)
            item.icon = getDrawable(mensa.icon)
            viewIdMensaMap[item.itemId] = mensa
        }
        swipe_container.visibility = View.VISIBLE
    }

    /**
     * Reacts to Preference changes.
     */
    @SuppressLint("NotifyDataSetChanged")
    private fun sharedPreferencesChanged(key: String?) {
        when (key ?: "") {
            getString(R.string.pref_usergroup) -> {
                recyclerView.invalidate()
                viewAdapter.notifyDataSetChanged()
            }
            getString(R.string.pref_mensa) -> {
                try {
                    initializeNavigation()
                }
                catch (nie: NavigationInvalidException){
                    showNavigationInvalidMessage()
                }
            }
        }
    }

    /**
     * Create OptionsMenu
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuinflater = menuInflater
        menuinflater.inflate(R.menu.options_menu, menu)
        return true
    }

    /**
     * Reacts to OptionsMenu selection.
     */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            when (item.itemId) {
                R.id.filter -> {
                    PopupMenu(this, findViewById(R.id.filter)).apply {
                        inflate(R.menu.filter_menu)
                        setOnMenuItemClickListener { item -> onFilterMenuItemSelected(item) }
                        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@MainActivity)
                        val selectedItem = VeggieFilterOption.valueOf(sharedPreferences.getString(getString(R.string.pref_filter), VeggieFilterOption.SHOW_ALL_DISHES.toString())!!)
                        menu.getItem(selectedItem.ordinal).isChecked = true
                        show()
                    }
                }
                R.id.licenses -> {
                    val intent = Intent(this, LicenseActivity::class.java)
                    startActivity(intent)
                    return true
                }
                R.id.prefs -> {
                    launchSettingsActivity()
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Launches the SettingsActivity.
     */
    private fun launchSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    private fun onFilterMenuItemSelected(item: MenuItem): Boolean {
        item.isChecked = true
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        with(sharedPreferences.edit()){
            putString(getString(R.string.pref_filter), VeggieFilterOption.values()[item.order].toString())
            apply()
        }
        evModel.getData()
        return true
    }

    /**
     * Mark navigation item that was selected last (if it exists) and update data accordingly.
     */
    override fun onResume() {
        super.onResume()
        if (navigation.menu.hasVisibleItems()){
            if(navigation.menu.findItem(evModel.mensa?.navigationViewId ?: -1) != null){
                navigation.selectedItemId = evModel.mensa!!.navigationViewId
                if(evModel.essen.value != null){ // Prevents showing no internet message on start
                    evModel.getData()
                }
            }
            else{
                navigation.selectedItemId = navigation.menu.getItem(0).itemId
                changeSelectedMensa(viewIdMensaMap[navigation.menu.getItem(0).itemId] ?: Mensa.STUDIERENDENHAUS)
            }
        }
    }

    /**
     * Unregisters SharedPreferenceChangeListener, when activity is destroyed
     */
    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(prefListener)
    }
}
