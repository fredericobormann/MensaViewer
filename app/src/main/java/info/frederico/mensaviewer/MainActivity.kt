package info.frederico.mensaviewer

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
import info.frederico.mensaviewer.helper.Essen
import info.frederico.mensaviewer.helper.EssenViewModel
import info.frederico.mensaviewer.helper.Essensplan
import info.frederico.mensaviewer.helper.Mensa
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
        if(newMensa != evModel.mensa){
            if(!evModel.isCachedDataAvailable(newMensa)){
                swipe_container.isRefreshing = true
            }
            tv_error_message_internet.visibility = View.INVISIBLE
            evModel.mensa = newMensa
        }
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
            changeSelectedMensa(viewIdMensaMap.get(item.itemId) ?: Mensa.STUDIERENDENHAUS)
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
        }
    }

    /**
     * Shows a message that no Mensa is selected in the preferences.
     */
    private fun showNavigationInvalidMessage() {
        tv_error_message_internet.text = getString(R.string.error_message_navigation)
        my_recycler_view.visibility = View.INVISIBLE
        tv_error_message_internet.visibility = View.VISIBLE
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
     */
    private fun setListener(){
        swipe_container.setOnRefreshListener {
            evModel.forceReload()
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.setOnNavigationItemReselectedListener(mOnNavigationItemReselectedListener)

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        prefListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
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
            }
        }

        evModel.essen.observe(this, essenObserver)
    }

    /**
     * Shows a message that some kind of network error occured.
     */
    private fun showInternetConnectionMessage() {
        tv_error_message_internet.text = getString(R.string.error_message_internet)
        my_recycler_view.visibility = View.INVISIBLE
        swipe_container.isRefreshing = false
        tv_error_message_internet.visibility = View.VISIBLE
    }

    /**
     * Shows a message that fetched data was empty
     */
    private fun showNoDataMessage() {
        tv_error_message_internet.text = getString(R.string.error_message_data)
        my_recycler_view.visibility = View.INVISIBLE
        swipe_container.isRefreshing = false
        tv_error_message_internet.visibility = View.VISIBLE
    }

    /**
     * Initializes Navigation according to Preferences.
     */
    private fun initializeNavigation() {
        viewIdMensaMap = HashMap()
        navigation.menu.clear()
        var selectedMensas = PreferenceManager.getDefaultSharedPreferences(this).getStringSet(getString(R.string.pref_mensa), resources.getStringArray(R.array.pref_mensa_default).toSet())
        if(selectedMensas.isEmpty()){
            throw NavigationInvalidException()
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
                    val filterMenu = PopupMenu(this, findViewById(R.id.filter))
                    filterMenu.inflate(R.menu.filter_menu)
                    filterMenu.show()
                }
                R.id.licenses -> {
                    val intent = Intent(this, LicenseActivity::class.java)
                    startActivity(intent)
                    return true
                }
                R.id.prefs -> {
                    val intent = Intent(this, SettingsActivity::class.java)
                    startActivity(intent)
                    return true
                }
            }
        }
        return super.onOptionsItemSelected(item)
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
