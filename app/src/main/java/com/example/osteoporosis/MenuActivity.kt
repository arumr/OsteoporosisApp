package com.example.osteoporosis

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MenuActivity : AppCompatActivity(), MenuAdapter.OnItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var menuList: ArrayList<Menu>
    private lateinit var menuAdapter: MenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_menu)
        init()
    }

    private fun init() {
        recyclerView = findViewById(R.id.rv)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        menuList = ArrayList()
        addDataToList()

        menuAdapter = MenuAdapter(menuList, this)
        recyclerView.adapter = menuAdapter
    }

    private fun addDataToList() {
        menuList.add(Menu(R.drawable.dataset, "Dataset"))
        menuList.add(Menu(R.drawable.fitur, "Fitur"))
        menuList.add(Menu(R.drawable.model, "Model"))
        menuList.add(Menu(R.drawable.simulasi, "Simulasi Model"))
        menuList.add(Menu(R.drawable.tentang, "Tentang Aplikasi"))
        menuList.add(Menu(R.drawable.exit, "Kembali"))
    }

    override fun onItemClick(menu: Menu) {
        when (menu.name) {
            "Dataset" -> startActivity(Intent(this, DatasetActivity::class.java))
            "Fitur" -> startActivity(Intent(this, FiturActivity::class.java))
            "Model" -> startActivity(Intent(this, ModelActivity::class.java))
            "Simulasi Model" -> startActivity(Intent(this, SimulasiModelActivity::class.java))
            "Tentang Aplikasi" -> startActivity(Intent(this, TentangAplikasiActivity::class.java))
            "Kembali" -> finish()  // Assuming "Kembali" means back to previous activity
        }
    }
}
