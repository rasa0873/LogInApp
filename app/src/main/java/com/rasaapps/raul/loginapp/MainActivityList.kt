package com.rasaapps.raul.loginapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.rasaapps.raul.loginapp.maps.MainActivityMap

class MainActivityList : AppCompatActivity(), JumpListener {

    private var productRV: RecyclerView? = null

    var productDataArrayList : ArrayList<ListElement>? = null

    var prodTitles: List<String>? = null
    var prodDescriptions: List<String>? = null
    var prodPrices: List<String>? = null

    private var progressBar: ProgressBar? = null

    var mDrawerLayout: View? = null

    var passedInName: String? = null

    val TAG = "ETIQUETA"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_list)



        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Drawer *** *** ***
        mDrawerLayout = findViewById(R.id.drawer_layout) // Drawer view
        val drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.open_nav_drawer,
            R.string.close_nav_drawer
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()
        val drawerNavView: NavigationView = findViewById(R.id.nav_view)
        var navView: View = drawerNavView.getHeaderView(0)
        var drawerTVHeaderName: TextView = navView.findViewById(R.id.drawer_header_name)

        drawerNavView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.log_out_from_drawer -> { drawer.close()
                    leaveActivity("logout")}
            }

            true
        }

        progressBar = findViewById(R.id.idPB)

        productRV = findViewById(R.id.idRVProducts)  // RecycleView

        // below line we are creating a new array list
        productDataArrayList = ArrayList()

        // Populate the ArrayList
        getData()

        buildRecycleView()

        progressBar!!.visibility = View.GONE
        productRV!!.visibility = View.VISIBLE

        val i2: Intent = intent
        passedInName = i2.getStringExtra("NAME FROM PREF")
        if (passedInName!=null)
            drawerTVHeaderName.text = passedInName


    }

    fun getData(){

        prodTitles = ArrayList()
        prodDescriptions = ArrayList()
        prodPrices = ArrayList()

        (prodTitles as ArrayList<String>).add("Nylon Backpack")
        (prodTitles as ArrayList<String>).add("Dumb Bells")
        (prodTitles as ArrayList<String>).add("Jump Rope")
        (prodTitles as ArrayList<String>).add("Leather Gloves")
        (prodTitles as ArrayList<String>).add("Lifting belt")
        (prodTitles as ArrayList<String>).add("Crossfit shorts, black")
        (prodTitles as ArrayList<String>).add("Workout T-shirt")
        (prodTitles as ArrayList<String>).add("Lightweight sports cap")
        (prodTitles as ArrayList<String>).add("Compression Sleeve")

        (prodDescriptions as ArrayList<String>).add("Designer backpack nylon. For Gym or travelling")
        (prodDescriptions as ArrayList<String>).add("45 lbs Dumb Bell covered in rubber")
        (prodDescriptions as ArrayList<String>).add("Fast and light jumping rope with rubber handles")
        (prodDescriptions as ArrayList<String>).add("Heavy duty leather gloves for workout")
        (prodDescriptions as ArrayList<String>).add("Nylon 6-inch Firm & Comfortable Back Support, Best for Workouts")
        (prodDescriptions as ArrayList<String>).add("7 inch black crossfit shorts")
        (prodDescriptions as ArrayList<String>).add("Polyester grey and black T-shit")
        (prodDescriptions as ArrayList<String>).add("Light white cap. Made from comfortable cotton")
        (prodDescriptions as ArrayList<String>).add("Improve circulation, relax muscles, and reduce swelling")



        productDataArrayList!!.add(
            ListElement((prodTitles as ArrayList<String>).get(0),
                (prodDescriptions as ArrayList<String>).get(0),
        "25.45$")
        )
        productDataArrayList!!.add(ListElement(
            (prodTitles as ArrayList<String>).get(1), (prodDescriptions as ArrayList<String>).get(1),
            "12.45$"))
        productDataArrayList!!.add(ListElement((prodTitles as ArrayList<String>).get(2),
            (prodDescriptions as ArrayList<String>).get(2),
            "9.50$"))
        productDataArrayList!!.add(ListElement((prodTitles as ArrayList<String>).get(3),
            (prodDescriptions as ArrayList<String>).get(3),
            "10.15$"))
        productDataArrayList!!.add(ListElement((prodTitles as ArrayList<String>).get(4),
            (prodDescriptions as ArrayList<String>).get(4),
            "12.95$"))
        productDataArrayList!!.add(ListElement((prodTitles as ArrayList<String>).get(5),
            "7 inch black crossfit shorts",
            "15.50$"))
        productDataArrayList!!.add(ListElement((prodTitles as ArrayList<String>).get(6),
            "Polyester grey and black T-shit",
            "9.40$"))
        productDataArrayList!!.add(ListElement((prodTitles as ArrayList<String>).get(7),
            "Light white cap. Made from comfortable cotton",
            "11.45$"))
        productDataArrayList!!.add(ListElement((prodTitles as ArrayList<String>).get(8),
            "Improve circulation, relax muscles, and reduce swelling",
            "25.45$"))
    }

    fun buildRecycleView(){
        // initializing our adapter class.
        val adapter = ListAdapter(productDataArrayList, this)
        adapter.addListener(this)

        val manager = LinearLayoutManager(this)
        productRV!!.setHasFixedSize(true)

        // setting layout manager
        // to our recycler view.
        productRV!!.layoutManager = manager

        // setting adapter to
        // our recycler view.
        productRV!!.adapter = adapter
    }

    override fun onBackPressed() {
        var drawer: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.close()
        } else {
            leaveActivity("no_logout")
            super.onBackPressed()
        }
    }

    fun leaveActivity(logoutFlag: String){
        val responseIntent = Intent()
        responseIntent.putExtra("RESPONSE_FROM_LIST", logoutFlag)
        setResult(RESULT_OK, responseIntent)
        finish()
    }


    override fun onReadRequest(name: String, description: String, price: String) {
        // Jump to Map with name, description, price
        val y = Intent(this, MainActivityMap::class.java)
        y.putExtra("NAME_TO_MAP", name)
        y.putExtra("DESCRIP_TO_MAP", description)
        y.putExtra("PRICE_TO_MAP", price)
        startActivity(y)
    }


}