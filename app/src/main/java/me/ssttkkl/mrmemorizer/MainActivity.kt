package me.ssttkkl.mrmemorizer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

val TOP_DEST = setOf(
    R.id.navigation_dashboard,
    R.id.navigation_note_list,
    R.id.navigation_statistics
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navController = findNavController(R.id.nav_host_fragment)
        findViewById<BottomNavigationView>(R.id.nav_view).setupWithNavController(navController)
    }
}