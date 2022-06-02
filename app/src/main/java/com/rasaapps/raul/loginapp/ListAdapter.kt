package com.rasaapps.raul.loginapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import java.lang.ref.WeakReference


class ListAdapter() : Adapter<ListAdapter.ViewHolder>() {

    private var listener = WeakReference<JumpListener>(null)

    // creating a variable for array list and context.
    private var productDataArrayList: ArrayList<ListElement>? = null
    private var context: Context? = null

    // creating a constructor for our variables.
    constructor(productDataArrayList: ArrayList<ListElement>?, context: Context?) : this() {
        this.productDataArrayList = productDataArrayList
        this.context = context
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // below line is to inflate our layout.
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_element, parent, false)
        return ViewHolder(view)

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int){
        val element : ListElement? = productDataArrayList?.get(position)
        val name: String? = element?.getTitle()
        val description: String? = element?.getDescription()
        val price: String? = element?.getPrice()

        holder.titleTV.text = name
        holder.descriptionTV.text = description
        holder.priceTV.text = price

        holder.cv.animation =
            AnimationUtils.loadAnimation( holder.itemView.context, R.anim.fade_transition)

        holder.cv.setOnClickListener {
            Log.i("ETIQUETA", "Item click on " + position )
            if (name != null && description!=null && price!=null) {
                release(name, description, price )
            }
        }
    }

    fun release(name: String, description: String, price: String) {
        if (name!=null && description!=null && price!=null  )
                listener.get()?.onReadRequest(name, description, price)
    }



    override fun getItemCount(): Int{
        return productDataArrayList?.size!!
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        // creating variables for our views.
        val titleTV: TextView
        val descriptionTV: TextView
        val priceTV: TextView
        val cv : CardView

        init {
            // initializing our views with their ids.
            titleTV = itemView.findViewById(R.id.product_title)
            descriptionTV = itemView.findViewById(R.id.product_description)
            priceTV = itemView.findViewById(R.id.product_price)
            cv = itemView.findViewById(R.id.cv)
        }
    }

    fun addListener(listener: JumpListener){
        this.listener = WeakReference(listener)
    }



}