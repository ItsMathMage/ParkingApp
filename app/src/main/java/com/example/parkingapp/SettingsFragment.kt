package com.example.parkingapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        var user = User()

        val userPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val uid = userPref.getString("UID", "")

        val firstnameField = view.findViewById<EditText>(R.id.your_firstname_field)
        val lastnameField = view.findViewById<EditText>(R.id.your_lastname_field)
        val emailField = view.findViewById<EditText>(R.id.your_email_field)
        val phoneField = view.findViewById<EditText>(R.id.your_phone_field)
        val passwordField = view.findViewById<EditText>(R.id.your_password_field)
        val uidField = view.findViewById<EditText>(R.id.your_uid_field)

        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        if (uid != null) {
            databaseReference.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        user = dataSnapshot.getValue(User::class.java)!!
                        firstnameField.setText(user.firstname)
                        lastnameField.setText(user.lastname)
                        emailField.setText(user.email)
                        phoneField.setText(user.phone)
                        passwordField.setText(user.password)
                        uidField.setText(user.uid)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireContext(), "Помилка в записі даних.", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        val buttonMenu = view.findViewById<Button>(R.id.button_menu2)
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
                        findNavController().navigate(R.id.action_settingsFragment_to_carFragment)
                        true
                    }
                    "Налаштування" -> {

                        true
                    }
                    "Вийти з акаунту" -> {
                        LogApp.out(findNavController(), requireContext(), R.id.action_settingsFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
        }

        val buttonBack = view.findViewById<Button>(R.id.to_back_button)
        buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_settingsFragment_to_mainFragment)
        }

        val buttonSave = view.findViewById<Button>(R.id.save_button)
        buttonSave.setOnClickListener {
            user.firstname = firstnameField.text.toString()
            user.lastname = lastnameField.text.toString()
            user.email = emailField.text.toString()
            user.phone = phoneField.text.toString()
            user.password = passwordField.text.toString()

            if (uid != uidField.text.toString()) {
                Toast.makeText(
                    requireContext(),
                    "Ваш персональний id не можливо змінити.",
                    Toast.LENGTH_SHORT
                ).show()
            } else if (uid != null) {
                databaseReference.child(uid).setValue(user)
                Toast.makeText(requireContext(), "Дані успішно змінено.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Помилка!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}