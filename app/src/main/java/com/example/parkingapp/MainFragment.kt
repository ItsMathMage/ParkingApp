package com.example.parkingapp

import android.annotation.SuppressLint
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

        val bundle = arguments
        val uid: String? = bundle?.getString("UID")

        var user = User()


        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

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
            popupMenu.menu.add("Елемент 1")
            popupMenu.menu.add("Елемент 2")
            popupMenu.menu.add("Вийти")

            // Відображаємо випадаюче меню
            popupMenu.show()

            // Обробка вибору елемента меню
            popupMenu.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Елемент 1" -> {
                        // Обробка вибору елемента 1
                        true
                    }
                    "Елемент 2" -> {
                        // Обробка вибору елемента 2
                        true
                    }
                    "Вийти" -> {
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