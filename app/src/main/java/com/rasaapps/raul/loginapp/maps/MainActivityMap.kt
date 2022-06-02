package com.rasaapps.raul.loginapp.maps

import android.accessibilityservice.GestureDescription
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.rasaapps.raul.loginapp.R


class MainActivityMap : AppCompatActivity() {

    var passedInName : String? = null
    var passedInDescription : String? = null
    var passedInPrice : String? = null


    lateinit var buyButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_map)

        val y2 : Intent = intent
        passedInName = y2.getStringExtra("NAME_TO_MAP")
        passedInDescription = y2.getStringExtra("DESCRIP_TO_MAP")
        passedInPrice = y2.getStringExtra("PRICE_TO_MAP")

        var tvTitle : TextView = findViewById(R.id.product_title_in_detail)
        var tvDescription : TextView = findViewById(R.id.product_description_in_detail)
        var tvPrice : TextView = findViewById(R.id.product_price_in_detail)

        tvTitle.text = passedInName
        tvDescription.text = passedInDescription
        tvPrice.text = passedInPrice

        buyButton = findViewById(R.id.buy_button)
        buyButton.setOnClickListener {
            // Confirmation dialog window
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Buy")
            builder.setMessage("Confirm your purchase?")
            builder.setIcon(R.drawable.green_check_circle_24)
            builder.setPositiveButton("OK"){dialog, id ->
                dialog.dismiss()
                finish()
                 }
            builder.setNegativeButton("Cancel"){dialog, id -> dialog.dismiss()}
            builder.show()
        }

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