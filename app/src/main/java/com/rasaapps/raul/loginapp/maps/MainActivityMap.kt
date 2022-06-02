package com.rasaapps.raul.loginapp.maps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.rasaapps.raul.loginapp.R

class MainActivityMap : AppCompatActivity() {

    var passedInName : String? = null
    var passedInDescription : String? = null
    var passedInPrice : String? = null


    private var btn2: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_map)

        val y2 : Intent = intent
        passedInName = y2.getStringExtra("NAME_TO_MAP")
        passedInDescription = y2.getStringExtra("DESCRIP_TO_MAP")
        passedInPrice = y2.getStringExtra("PRICE_TO_MAP")

        var tvTitle : TextView = findViewById(R.id.product_title_in_detail)
        var tvDescription : TextView = findViewById(R.id.product_description_in_detail)

        tvTitle.text = passedInName
        tvDescription.text = passedInDescription

        btn2 = findViewById<Button>(R.id.btn2)

        addFragment(MapFragment(), false, "Map")
    }

    fun addFragment(fragment: Fragment, addToBackStack: Boolean, tag: String){
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()

        if (addToBackStack){
            ft.addToBackStack(tag)
        }
        ft.replace(R.id.container_frame_back, fragment, tag)
        ft.commitAllowingStateLoss()
    }
}