package me.ssttkkl.mrmemorizer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.ssttkkl.mrmemorizer.service.SetupAlarmService

val TOP_DEST = setOf(
    R.id.navigation_dashboard,
    R.id.navigation_note_list,
    R.id.navigation_statistics
)

class MainActivity : AppCompatActivity() {

    private lateinit var navView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = findNavController(R.id.nav_host_fragment)
        navView = findViewById(R.id.nav_view)
        navView.setupWithNavController(navController)

        // 让navView只在顶层导航时显示
        navController.addOnDestinationChangedListener { _, destination, _ ->
            navView.visibility = if (destination.id in TOP_DEST)
                View.VISIBLE
            else
                View.GONE
        }

        SetupAlarmService.startSetupAlarm(this)
    }
}