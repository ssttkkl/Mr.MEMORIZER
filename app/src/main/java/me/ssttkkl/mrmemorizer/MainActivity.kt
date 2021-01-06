package me.ssttkkl.mrmemorizer

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import me.ssttkkl.mrmemorizer.data.AppDatabase
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

        // 返回的LiveData只在数据库有变动时更新
        // 但是只有在数据库变动的时候才需要重新设置，所以没问题
        AppDatabase.getInstance().noteDao
            .getNoteNextReview(AppPreferences.reviewInterval.size)
            .observe(this, Observer {
                SetupAlarmService.startSetupAlarm(this)
            })
    }
}