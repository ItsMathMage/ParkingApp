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
        val pass = userPref.getString("UPASS", "")

        var user = User()
        var car = Car()
        var parkingPlace = ParkingPlace()

        val databaseUsers = FirebaseDatabase.getInstance().getReference("users")
        var databasePlaces = FirebaseDatabase.getInstance().getReference("places")
        var databaseCars = FirebaseDatabase.getInstance().getReference("cars")

        if (uid != null) {
            databaseUsers.child(uid).child("password").setValue(pass)
        }

        val editor = userPref.edit()

        editor.putString("UID", uid)
        editor.apply()

        val parkingLayout = view.findViewById<GridLayout>(R.id.parking_body)

        var selectedSpot = -1

        if (uid != null) {
            databaseCars.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    car = dataSnapshot.getValue(Car::class.java)!!

                    editor.putString("CNUM", car.number)
                    editor.putString("CNAME", car.firm)
                    editor.putString("CCOLOR", car.color)

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

                val layoutParams = GridLayout.LayoutParams(
                    GridLayout.spec(GridLayout.UNDEFINED, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, 1f)
                )
                layoutParams.width = 0
                layoutParams.height = 0

                parkingSpot[selectedSpot]!!.layoutParams = layoutParams

                // Додати обробник події OnClickListener до кожної кнопки
                parkingSpot[selectedSpot]!!.setOnClickListener {
                    selectedSpot = (i - 1) * 6 + j
                    databasePlaces.child(selectedSpot.toString())
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                parkingPlace = snapshot.getValue(ParkingPlace::class.java)!!
                                var carNumber = userPref.getString("CNUM", "")
                                var carName = userPref.getString("CNAME", "")
                                var tempPlace = ParkingPlace()
                                var isCar = false
                                var isOne = userPref.getBoolean("ISONE", true)
                                isCar = carNumber != ""

                                if (isCar) {
                                    if (parkingPlace.isTaken) {
                                        if (parkingPlace.number == carNumber) {
                                            tempPlace.name = ""
                                            tempPlace.number = ""
                                            tempPlace.isTaken = false
                                            databasePlaces.child(selectedSpot.toString()).setValue(tempPlace)
                                            editor.putBoolean("ISONE", true)
                                            editor.apply()
                                        } else {
                                            Toast.makeText(requireContext(), "Зайнято", Toast.LENGTH_SHORT).show()
                                        }
                                    } else {
                                        if (isOne) {
                                            tempPlace.name = carName!!
                                            tempPlace.number = carNumber!!
                                            tempPlace.isTaken = true
                                            databasePlaces.child(selectedSpot.toString()).setValue(tempPlace)
                                            editor.putBoolean("ISONE", false)
                                            editor.apply()
                                        } else {
                                            Toast.makeText(requireContext(), "Ви вже зайняли місце", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                } else {
                                    Toast.makeText(requireContext(), "Ви не добавили машину", Toast.LENGTH_SHORT).show()
                                }

                                updateButtons(parkingSpot)
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })
                }

                parkingLayout.addView(parkingSpot[selectedSpot])
            }
        }

        var carNumber = userPref.getString("CNUM", "")
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
                        LogApp.out(findNavController(), requireContext(), R.id.action_mainFragment_to_loginFragment)
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
            val userPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
            var carNumber = userPref.getString("CNUM", "")
            updateDatabasePlace.child(i.toString()).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    updateParkingPlace = snapshot.getValue(ParkingPlace::class.java)!!
                    if (updateParkingPlace.isTaken) {
                        if (updateParkingPlace.number == carNumber) {
                            updateParkingSpot[i]!!.setBackgroundResource(R.drawable.your_button)
                        } else {
                            updateParkingSpot[i]!!.setBackgroundResource(R.drawable.other_button)
                        }
                    } else {
                        updateParkingSpot[i]!!.setBackgroundResource(R.drawable.round_button)
                    }
                }
                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }
    }
}