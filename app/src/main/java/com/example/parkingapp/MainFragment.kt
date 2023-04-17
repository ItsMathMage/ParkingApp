package com.example.parkingapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
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
        var car = Car()
        var parkingPlace = ParkingPlace()

        val databaseUsers = FirebaseDatabase.getInstance().getReference("users")
        var databasePlaces = FirebaseDatabase.getInstance().getReference("places")
        var databaseCars = FirebaseDatabase.getInstance().getReference("cars")

        val editor = userPref.edit()
        editor.putString("UID", uid)
        editor.apply()

        val parkingLayout = view.findViewById<GridLayout>(R.id.parking_body)

        var selectedSpot = -1

        if (uid != null) {
            databaseCars.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    car = dataSnapshot.getValue(Car::class.java)!!
                    editor.putString("CN", car.number)
                    editor.apply()
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }

        var parkingSpot = arrayOfNulls<AppCompatButton>(25)

        for (i in 1..4) {
            for (j in 1..6) {
                selectedSpot = (i - 1) * 6 + j
                parkingSpot[selectedSpot] = AppCompatButton(requireContext())
                parkingSpot[selectedSpot]!!.text = "Місце \n ${(i - 1) * 6 + j}"
                parkingSpot[selectedSpot]!!.setBackgroundResource(R.drawable.round_button)

                // Додати обробник події OnClickListener до кожної кнопки
                parkingSpot[selectedSpot]!!.setOnClickListener {

                    databasePlaces.child(selectedSpot.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                parkingPlace = snapshot.getValue(ParkingPlace::class.java)!!
                                val carNumber = userPref.getString("CN", "")
                                if (parkingPlace.isTaken) {

                                } else {

                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                }

                parkingLayout.addView(parkingSpot[selectedSpot])
            }
        }

        updateButtons(parkingSpot)

        val updateButton = view.findViewById<Button>(R.id.button_update)
        updateButton.setOnClickListener {
            updateButtons(parkingSpot)
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

    fun updateButtons(updateParkingSpot: Array<AppCompatButton?>) {
        for (i in 1..24) {
            var updateDatabasePlace = FirebaseDatabase.getInstance().getReference("places")
            var updateParkingPlace = ParkingPlace()
            updateDatabasePlace.child(i.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    updateParkingPlace = snapshot.getValue(ParkingPlace::class.java)!!
                    if (updateParkingPlace.isTaken) {
                        updateParkingSpot[i]!!.setBackgroundColor(1)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}