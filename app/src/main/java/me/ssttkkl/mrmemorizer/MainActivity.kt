package me.ssttkkl.mrmemorizer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import me.ssttkkl.mrmemorizer.data.AppDatabase
import me.ssttkkl.mrmemorizer.res.ReviewStage
import me.ssttkkl.mrmemorizer.service.SetupAlarmService

val TOP_DEST = setOf(
    R.id.navigation_dashboard,
    R.id.navigation_note_list,
    R.id.navigation_statistics
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 返回的LiveData只在数据库有变动时更新
        // 但是只有在数据库变动的时候才需要重新设置，所以没问题
        AppDatabase.getInstance().noteDao
            .getNoteNextReview(ReviewStage.nextReviewDuration.size)
            .observe(this, Observer {
                SetupAlarmService.startSetupAlarm(this)
            })
    }
}