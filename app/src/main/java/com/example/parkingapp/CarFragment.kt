package com.example.parkingapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import androidx.navigation.fragment.findNavController

class CarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_car, container, false)

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
                        findNavController().navigate(R.id.action_carFragment_to_loginFragment)
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

        return view
    }
}