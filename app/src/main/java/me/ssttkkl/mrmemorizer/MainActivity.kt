package me.ssttkkl.mrmemorizer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

val TOP_DEST = setOf(
    R.id.navigation_dashboard,
    R.id.navigation_note_list,
    R.id.navigation_statistics
)

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}