package com.example.parkingapp

import android.app.AlertDialog
import android.content.Context
import androidx.navigation.NavController

class LogApp {
    companion object {
        fun out(navController: NavController, context: Context, actionId: Int) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Вихід з акаунту")
            builder.setMessage("Ви точно бажаєте вийти з акаунту?")

            builder.setPositiveButton("Так") { dialog, _ ->
                val uidPref = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = uidPref.edit()
                editor.putBoolean("ISLOG", false)
                editor.apply()
                navController.navigate(actionId)
            }
            builder.setNegativeButton("Ні") { dialog, _ ->
                // дії, які виконуються при натисканні кнопки "Відміна"
            }
            builder.show()
        }
    }
}
