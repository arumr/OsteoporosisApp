package com.example.osteoporosis

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MenuAdapter(
    private val menuList: ArrayList<Menu>,
    private val clickListener: OnItemClickListener
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(menu: Menu)
    }

    inner class MenuViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val imageView: ImageView = itemView.findViewById(R.id.ivMenu)
        val textView: TextView = itemView.findViewById(R.id.tvMenu)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(menu: Menu) {
            imageView.setImageResource(menu.image)
            textView.text = menu.name
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                clickListener.onItemClick(menuList[position])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.menu_item, parent, false)
        return MenuViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(menuList[position])
    }

    override fun getItemCount(): Int {
        return menuList.size
    }
}
