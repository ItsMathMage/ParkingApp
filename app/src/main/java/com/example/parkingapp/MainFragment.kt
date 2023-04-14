package com.example.parkingapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
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
        val textField = view.findViewById<TextView>(R.id.text)


        val databaseReference = FirebaseDatabase.getInstance().getReference("users")

        if (uid != null) {
            databaseReference.child(uid).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Отримуємо дані класу User з Realtime Database
                    user = dataSnapshot.getValue(User::class.java)!!
                    textField.text = user.email
                    // використовуйте дані класу User відповідним чином
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        }



        return view
    }
}