package com.example.parkingapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        val userPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val uid = userPref.getString("UID", "")

        var user = User()

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        val uidPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = uidPref.edit()
        editor.putString("UID", uid)
        editor.apply()

        if (uid != null) {
            databaseReference.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Отримуємо дані класу User з Realtime Database
                    user = dataSnapshot.getValue(User::class.java)!!
                    // використовуйте дані класу User відповідним чином
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        val buttonMenu = view.findViewById<Button>(R.id.button_menu)
        buttonMenu.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), buttonMenu)

            // Додаємо елементи меню
            popupMenu.menu.add("Транспорт")
            popupMenu.menu.add("Налаштування")
            popupMenu.menu.add("Вийти з акаунту")

            // Відображаємо випадаюче меню
            popupMenu.show()

            // Обробка вибору елемента меню
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Транспорт" -> {
                        findNavController().navigate(R.id.action_mainFragment_to_carFragment)
                        true
                    }
                    "Налаштування" -> {
                        findNavController().navigate(R.id.action_mainFragment_to_settingsFragment)
                        true
                    }
                    "Вийти з акаунту" -> {
                        findNavController().navigate(R.id.action_mainFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
        }

        return view
    }
}