package com.example.parkingapp

import android.annotation.SuppressLint
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_car, container, false)

        val databaseReference = FirebaseDatabase.getInstance().getReference("cars")

        val firmField = view.findViewById<EditText>(R.id.firm_car_field)
        val modelField = view.findViewById<EditText>(R.id.model_car_field)
        val colorField = view.findViewById<EditText>(R.id.color_car_field)
        val numberField = view.findViewById<EditText>(R.id.number_car_field)

        var car = Car()

        val userPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val uid = userPref.getString("UID", "")

        if (uid != null) {
            databaseReference.child(uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        car = dataSnapshot.getValue(Car::class.java)!!
                        firmField.setText(car.firm)
                        modelField.setText(car.model)
                        colorField.setText(car.color)
                        numberField.setText(car.number)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        TODO("Not yet implemented")
                    }
                })
        }

        val buttonMenu = view.findViewById<Button>(R.id.button_menu3)
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

                        true
                    }
                    "Налаштування" -> {
                        findNavController().navigate(R.id.action_carFragment_to_settingsFragment)
                        true
                    }
                    "Вийти з акаунту" -> {
                        LogApp.out(findNavController(), requireContext(), R.id.action_carFragment_to_loginFragment)
                        true
                    }
                    else -> false
                }
            }
        }

        val buttonBack = view.findViewById<Button>(R.id.to_back_button)
        buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_carFragment_to_mainFragment)
        }

        val buttonSave = view.findViewById<Button>(R.id.save_car_button)
        buttonSave.setOnClickListener {

            car.firm = firmField.text.toString()
            car.model = modelField.text.toString()
            car.color = colorField.text.toString()
            car.number = numberField.text.toString()

            if (uid != null) {
                databaseReference.child(uid).setValue(car)
                Toast.makeText(requireContext(), "Дані успішно записані.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Помилка!", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}